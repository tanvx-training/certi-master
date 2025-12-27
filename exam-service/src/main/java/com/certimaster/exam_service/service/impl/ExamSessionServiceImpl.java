package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.client.ResultServiceClient;
import com.certimaster.exam_service.dto.external.UserExamSessionDto;
import com.certimaster.exam_service.dto.mapper.QuestionMapper;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.entity.ExamQuestion;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import com.certimaster.exam_service.kafka.ExamEventProducer;
import com.certimaster.exam_service.repository.ExamRepository;
import com.certimaster.exam_service.repository.QuestionRepository;
import com.certimaster.exam_service.service.ExamSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ExamEventProducer eventProducer;
    private final ResultServiceClient resultServiceClient;

    @Override
    @Transactional
    public ExamSessionResponse startExam(Long examId, Long userId, String username, StartExamRequest request) {
        log.info("Starting exam {} for user {} with mode {}", examId, userId, request.getMode());

        // Check for existing active session via result-service (Feign client)
        ResponseDto<UserExamSessionDto> response = resultServiceClient.checkActiveSession(userId, examId);
        if (Objects.nonNull(response) && response.isSuccess() && Objects.nonNull(response.getData())) {
            UserExamSessionDto session = response.getData();
            log.info("User {} already has active session {} for exam {}", userId, session.getId(), examId);
            throw BusinessException.invalidInput(
                    String.format("You already have an active session (ID: %d) for this exam. Please complete or abandon it first.", session.getId())
            );
        }

        // Get exam with questions
        Exam exam = examRepository.findActiveById(examId)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, examId));

        // Get questions for this exam
        List<Question> questions = getExamQuestions(exam);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("This exam has no questions");
        }

        LocalDateTime startTime = LocalDateTime.now();

        // Extract question IDs
        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        // Publish event to Kafka and wait for reply with session ID
        ExamSessionStartedEvent event = ExamSessionStartedEvent.builder()
                .userId(userId)
                .examId(exam.getId())
                .certificationId(exam.getCertification().getId())
                .mode(request.getMode())
                .examTitle(exam.getTitle())
                .totalQuestions(questions.size())
                .durationMinutes(exam.getDurationMinutes())
                .startTime(startTime)
                .questionIds(questionIds)
                .build();

        // Send event and wait for reply with created session ID
        var createdEvent = eventProducer.publishSessionStartedAndWaitReply(event);
        Long sessionId = createdEvent.getSessionId();
        log.info("Session {} created for user {} exam {}", sessionId, userId, examId);

        // Build response with session ID from result-service
        List<QuestionResponse> questionResponses = questions.stream()
                .map(questionMapper::toResponseWithoutCorrect)
                .toList();

        return ExamSessionResponse.builder()
                .id(sessionId)
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(request.getMode())
                .status("IN_PROGRESS")
                .startTime(startTime)
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    @Override
    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long sessionId, Long userId, AnswerQuestionRequest request) {
        log.debug("Submitting answer for session {} question {}", sessionId, request.getQuestionId());

        // Validate session via result-service
        UserExamSessionDto session = findSessionOrThrow(sessionId);
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
                && new HashSet<>(correctOptionIds).containsAll(request.getSelectedOptionIds());

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

        UserExamSessionDto session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);

        // Publish complete session event to Kafka (result-service will update status)
        log.info("Session {} completion requested, result-service will handle status update", sessionId);
    }

    private UserExamSessionDto findSessionOrThrow(Long sessionId) {
        ResponseDto<UserExamSessionDto> response = resultServiceClient.getSessionById(sessionId);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw ResourceNotFoundException.byId(SESSION, sessionId);
        }
        return response.getData();
    }

    private void validateSessionOwner(UserExamSessionDto session, Long userId) {
        if (!session.getUserId().equals(userId)) {
            throw BusinessException.invalidInput("You don't have access to this session");
        }
    }

    private void validateSessionActive(UserExamSessionDto session) {
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
