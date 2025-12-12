package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.QuestionMapper;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.entity.ExamQuestion;
import com.certimaster.exam_service.entity.ExamSession;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import com.certimaster.exam_service.kafka.ExamEventProducer;
import com.certimaster.exam_service.repository.ExamRepository;
import com.certimaster.exam_service.repository.ExamSessionRepository;
import com.certimaster.exam_service.repository.QuestionRepository;
import com.certimaster.exam_service.service.ExamSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamSessionServiceImpl implements ExamSessionService {

    private static final String EXAM = "Exam";
    private static final String SESSION = "ExamSession";
    private static final String QUESTION = "Question";

    private final ExamRepository examRepository;
    private final ExamSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ExamEventProducer eventProducer;

    @Override
    @Transactional
    public ExamSessionResponse startExam(Long examId, Long userId, String username, StartExamRequest request) {
        log.info("Starting exam {} for user {} with mode {}", examId, userId, request.getMode());

        // Check for existing active session
        if (sessionRepository.existsByUserIdAndExamIdAndStatus(userId, examId, "IN_PROGRESS")) {
            throw BusinessException.invalidInput("You already have an active session for this exam");
        }

        // Get exam with questions
        Exam exam = examRepository.findActiveById(examId)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, examId));

        // Get questions for this exam
        List<Question> questions = getExamQuestions(exam);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("This exam has no questions");
        }


        // Create session
        LocalDateTime now = LocalDateTime.now();
        ExamSession session = ExamSession.builder()
                .userId(userId)
                .username(username)
                .exam(exam)
                .mode(request.getMode())
                .totalQuestions(questions.size())
                .startTime(now)
                .status("IN_PROGRESS")
                .currentQuestionIndex(0)
                .build();

        ExamSession savedSession = sessionRepository.save(session);
        log.info("Created exam session {} for user {}", savedSession.getId(), userId);

        // Publish event to Kafka
        ExamSessionStartedEvent event = ExamSessionStartedEvent.builder()
                .sessionId(savedSession.getId())
                .userId(userId)
                .username(username)
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(request.getMode())
                .totalQuestions(questions.size())
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .startTime(now)
                .build();

        eventProducer.publishSessionStarted(event);

        // Build response
        List<QuestionResponse> questionResponses = questions.stream()
                .map(questionMapper::toResponseWithoutCorrect)
                .toList();

        return ExamSessionResponse.builder()
                .id(savedSession.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(request.getMode())
                .status("IN_PROGRESS")
                .startTime(now)
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    @Override
    public ExamSessionResponse getSession(Long sessionId, Long userId) {
        log.debug("Getting session {} for user {}", sessionId, userId);

        ExamSession session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);

        Exam exam = session.getExam();
        List<Question> questions = getExamQuestions(exam);

        List<QuestionResponse> questionResponses = questions.stream()
                .map(questionMapper::toResponseWithoutCorrect)
                .toList();

        return ExamSessionResponse.builder()
                .id(session.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(session.getMode())
                .status(session.getStatus())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalQuestions(session.getTotalQuestions())
                .currentQuestionIndex(session.getCurrentQuestionIndex())
                .questions(questionResponses)
                .build();
    }


    @Override
    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long sessionId, Long userId, AnswerQuestionRequest request) {
        log.debug("Submitting answer for session {} question {}", sessionId, request.getQuestionId());

        ExamSession session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);
        validateSessionActive(session);

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, request.getQuestionId()));

        // Get correct option IDs
        Set<QuestionOption> options = question.getQuestionOptions();
        List<Long> correctOptionIds = options.stream()
                .filter(QuestionOption::getIsCorrect)
                .map(QuestionOption::getId)
                .toList();

        // Check if answer is correct
        boolean isCorrect = correctOptionIds.size() == request.getSelectedOptionIds().size()
                && correctOptionIds.containsAll(request.getSelectedOptionIds());

        // Publish answer event to Kafka
        AnswerSubmittedEvent event = AnswerSubmittedEvent.builder()
                .sessionId(sessionId)
                .userId(userId)
                .questionId(question.getId())
                .selectedOptionIds(request.getSelectedOptionIds())
                .correctOptionIds(correctOptionIds)
                .isCorrect(isCorrect)
                .isFlagged(request.getIsFlagged() != null && request.getIsFlagged())
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .topicId(question.getTopic().getId())
                .topicName(question.getTopic().getName())
                .answeredAt(LocalDateTime.now())
                .build();

        eventProducer.publishAnswerSubmitted(event);

        // Build response based on mode
        AnswerFeedbackResponse.AnswerFeedbackResponseBuilder responseBuilder = AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .answered(true);

        // In PRACTICE mode, show correct answers immediately
        if ("PRACTICE".equals(session.getMode())) {
            responseBuilder
                    .isCorrect(isCorrect)
                    .correctOptionIds(correctOptionIds)
                    .explanation(question.getExplanation());
        }

        return responseBuilder.build();
    }

    @Override
    @Transactional
    public void completeSession(Long sessionId, Long userId) {
        log.info("Completing session {} for user {}", sessionId, userId);

        ExamSession session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);

        session.setStatus("COMPLETED");
        session.setEndTime(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("Session {} completed", sessionId);
    }

    private ExamSession findSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION, sessionId));
    }

    private void validateSessionOwner(ExamSession session, Long userId) {
        if (!session.getUserId().equals(userId)) {
            throw BusinessException.invalidInput("You don't have access to this session");
        }
    }

    private void validateSessionActive(ExamSession session) {
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw BusinessException.invalidInput("This session is no longer active");
        }
    }

    private List<Question> getExamQuestions(Exam exam) {
        return exam.getExamQuestions().stream()
                .sorted(Comparator.comparing(ExamQuestion::getOrderIndex))
                .map(ExamQuestion::getQuestion)
                .toList();
    }
}
