package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.event.ExamCompletedEvent;
import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.QuestionMapper;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.dto.response.UserExamSessionResponse;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.entity.ExamQuestion;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import com.certimaster.exam_service.entity.UserAnswer;
import com.certimaster.exam_service.entity.UserExamSession;
import com.certimaster.exam_service.kafka.ExamEventProducer;
import com.certimaster.exam_service.repository.ExamRepository;
import com.certimaster.exam_service.repository.UserAnswerRepository;
import com.certimaster.exam_service.repository.UserExamSessionRepository;
import com.certimaster.exam_service.service.ExamSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamSessionServiceImpl implements ExamSessionService {

    private static final String EXAM = "Exam";
    private static final String SESSION = "ExamSession";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String MODE_PRACTICE = "PRACTICE";

    private final ExamRepository examRepository;
    private final QuestionMapper questionMapper;
    private final UserExamSessionRepository userExamSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ExamEventProducer examEventProducer;

    @Override
    @Transactional
    public ExamSessionResponse startExam(Long examId, Long userId, String username, StartExamRequest request) {
        log.info("Starting exam {} for user {} with mode {}", examId, userId, request.getMode());

        // Check for existing active session locally in exam-service database
        userExamSessionRepository.findByUserIdAndExam_IdAndStatus(userId, examId, STATUS_IN_PROGRESS)
                .ifPresent(existingSession -> {
                    log.info("User {} already has active session {} for exam {}", userId, existingSession.getId(), examId);
                    throw BusinessException.invalidInput(
                            String.format("You already have an active session (ID: %d) for this exam. Please complete or abandon it first.", existingSession.getId())
                    );
                });

        // Get exam with questions
        Exam exam = examRepository.findActiveById(examId)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, examId));

        // Get questions for this exam
        List<Question> questions = getExamQuestions(exam);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("This exam has no questions");
        }

        LocalDateTime startTime = LocalDateTime.now();

        // Create UserExamSession locally in exam-service database
        UserExamSession session = UserExamSession.builder()
                .userId(userId)
                .exam(exam)
                .certification(exam.getCertification())
                .startTime(startTime)
                .status(STATUS_IN_PROGRESS)
                .mode(request.getMode())
                .examTitle(exam.getTitle())
                .totalQuestions(questions.size())
                .durationMinutes(exam.getDurationMinutes())
                .answeredCount(0)
                .correctCount(0)
                .wrongCount(0)
                .unansweredCount(questions.size())
                .flaggedCount(0)
                .timeSpentSeconds(0)
                .build();

        userExamSessionRepository.save(session);
        log.info("Session {} created locally for user {} exam {}", session.getId(), userId, examId);

        // Create UserAnswer records for all questions (pre-populate for tracking)
        for (Question question : questions) {
            // Get correct option IDs for this question
            Long[] correctOptionIds = question.getQuestionOptions().stream()
                    .filter(QuestionOption::getIsCorrect)
                    .map(QuestionOption::getId)
                    .toArray(Long[]::new);

            UserAnswer userAnswer = UserAnswer.builder()
                    .userExamSession(session)
                    .question(question)
                    .correctOptionIds(correctOptionIds)
                    .isCorrect(null)
                    .isFlagged(false)
                    .timeSpentSeconds(0)
                    .build();

            userAnswerRepository.save(userAnswer);
        }

        // Build response with local session ID
        List<QuestionResponse> questionResponses = questions.stream()
                .map(questionMapper::toResponseWithoutCorrect)
                .toList();

        return ExamSessionResponse.builder()
                .id(session.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(request.getMode())
                .status(STATUS_IN_PROGRESS)
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

        // Find and validate session locally
        UserExamSession session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);
        validateSessionActive(session);

        // Find the UserAnswer record for this question
        UserAnswer userAnswer = userAnswerRepository.findByUserExamSessionIdAndQuestion_Id(sessionId, request.getQuestionId())
                .orElseThrow(() -> BusinessException.invalidInput(
                        String.format("Question %d is not part of this session", request.getQuestionId())));

        Question question = userAnswer.getQuestion();

        // Get correct option IDs
        List<Long> correctOptionIds = Arrays.asList(userAnswer.getCorrectOptionIds());

        // Check if answer is correct
        boolean isCorrect = correctOptionIds.size() == request.getSelectedOptionIds().size()
                && new HashSet<>(correctOptionIds).containsAll(request.getSelectedOptionIds());

        // Track if this is a new answer or an update
        boolean wasAnsweredBefore = userAnswer.getSelectedOptionIds() != null;
        Boolean wasPreviouslyCorrect = userAnswer.getIsCorrect();

        // Update UserAnswer record locally
        userAnswer.setSelectedOptionIds(request.getSelectedOptionIds().toArray(new Long[0]));
        userAnswer.setIsCorrect(isCorrect);
        userAnswer.setAnsweredAt(LocalDateTime.now());

        // Update time spent (accumulate)
        if (request.getTimeSpentSeconds() != null) {
            userAnswer.setTimeSpentSeconds(
                    userAnswer.getTimeSpentSeconds() + request.getTimeSpentSeconds());
        }

        // Handle flagging if provided
        if (request.getIsFlagged() != null) {
            boolean wasFlagged = Boolean.TRUE.equals(userAnswer.getIsFlagged());
            boolean nowFlagged = request.getIsFlagged();

            if (wasFlagged != nowFlagged) {
                userAnswer.setIsFlagged(nowFlagged);
                // Update session flaggedCount
                if (nowFlagged) {
                    session.setFlaggedCount(session.getFlaggedCount() + 1);
                } else {
                    session.setFlaggedCount(Math.max(0, session.getFlaggedCount() - 1));
                }
            }
        }

        userAnswerRepository.save(userAnswer);

        // Update session statistics
        updateSessionStatistics(session, wasAnsweredBefore, wasPreviouslyCorrect, isCorrect);

        // Update time spent on session
        if (request.getTimeSpentSeconds() != null) {
            session.setTimeSpentSeconds(session.getTimeSpentSeconds() + request.getTimeSpentSeconds());
        }

        userExamSessionRepository.save(session);

        // Build response based on mode
        AnswerFeedbackResponse.AnswerFeedbackResponseBuilder responseBuilder = AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .answered(true);

        // In PRACTICE mode, show correct answers immediately
        if (MODE_PRACTICE.equals(session.getMode())) {
            responseBuilder
                    .isCorrect(isCorrect)
                    .correctOptionIds(correctOptionIds)
                    .explanation(question.getExplanation());
        }
        // In TIMED mode, don't reveal correctness - fields remain null

        return responseBuilder.build();
    }

    @Override
    @Transactional
    public ExamResultResponse completeSession(Long sessionId, Long userId) {
        log.info("Completing session {} for user {}", sessionId, userId);

        UserExamSession session = findSessionOrThrow(sessionId);
        validateSessionOwner(session, userId);
        validateSessionActive(session);

        LocalDateTime endTime = LocalDateTime.now();

        // Update session status to COMPLETED and set endTime
        session.setStatus(STATUS_COMPLETED);
        session.setEndTime(endTime);
        userExamSessionRepository.save(session);

        // Get all user answers for this session
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserExamSessionId(sessionId);

        // Build ExamCompletedEvent with all session data and answers
        ExamCompletedEvent event = buildExamCompletedEvent(session, userAnswers, endTime);

        log.info("Publishing ExamCompletedEvent for session {} with {} answers", sessionId, userAnswers.size());

        // Publish event and wait for ExamResultResponse reply
        ExamResultResponse result = examEventProducer.publishExamCompletedAndWaitReply(event);

        log.info("Session {} completed for user {} with score {}%", sessionId, userId, result.getPercentage());

        return result;
    }

    @Override
    public UserExamSessionResponse getSession(Long sessionId, Long userId) {
        log.debug("Getting session {} for user {}", sessionId, userId);

        UserExamSession session = userExamSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION, sessionId));

        return mapToUserExamSessionResponse(session);
    }

    @Override
    public List<UserExamSessionResponse> getActiveSessions(Long userId) {
        log.debug("Getting active sessions for user {}", userId);

        List<UserExamSession> sessions = userExamSessionRepository.findByUserIdAndStatus(userId, STATUS_IN_PROGRESS);

        return sessions.stream()
                .map(this::mapToUserExamSessionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map UserExamSession entity to UserExamSessionResponse DTO.
     */
    private UserExamSessionResponse mapToUserExamSessionResponse(UserExamSession session) {
        return UserExamSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .examId(session.getExam().getId())
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .certificationName(session.getCertification() != null ? session.getCertification().getName() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .mode(session.getMode())
                .examTitle(session.getExamTitle())
                .totalQuestions(session.getTotalQuestions())
                .durationMinutes(session.getDurationMinutes())
                .answeredCount(session.getAnsweredCount())
                .correctCount(session.getCorrectCount())
                .wrongCount(session.getWrongCount())
                .unansweredCount(session.getUnansweredCount())
                .flaggedCount(session.getFlaggedCount())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    /**
     * Build ExamCompletedEvent from session and answers.
     */
    private ExamCompletedEvent buildExamCompletedEvent(UserExamSession session, List<UserAnswer> userAnswers, LocalDateTime endTime) {
        Exam exam = session.getExam();

        // Convert UserAnswer entities to UserAnswerData
        List<ExamCompletedEvent.UserAnswerData> answerDataList = userAnswers.stream()
                .map(this::convertToUserAnswerData)
                .collect(Collectors.toList());

        return ExamCompletedEvent.builder()
                .sessionId(session.getId())
                .userId(session.getUserId())
                .examId(exam.getId())
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .mode(session.getMode())
                .examTitle(session.getExamTitle())
                .totalQuestions(session.getTotalQuestions())
                .durationMinutes(session.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .startTime(session.getStartTime())
                .endTime(endTime)
                .answeredCount(session.getAnsweredCount())
                .correctCount(session.getCorrectCount())
                .wrongCount(session.getWrongCount())
                .flaggedCount(session.getFlaggedCount())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .answers(answerDataList)
                .eventTime(LocalDateTime.now())
                .build();
    }

    /**
     * Convert UserAnswer entity to UserAnswerData for the event.
     */
    private ExamCompletedEvent.UserAnswerData convertToUserAnswerData(UserAnswer userAnswer) {
        Question question = userAnswer.getQuestion();

        return ExamCompletedEvent.UserAnswerData.builder()
                .questionId(question.getId())
                .topicId(question.getTopic() != null ? question.getTopic().getId() : null)
                .topicName(question.getTopic() != null ? question.getTopic().getName() : null)
                .selectedOptionIds(userAnswer.getSelectedOptionIds())
                .correctOptionIds(userAnswer.getCorrectOptionIds())
                .isCorrect(userAnswer.getIsCorrect())
                .isFlagged(userAnswer.getIsFlagged())
                .timeSpentSeconds(userAnswer.getTimeSpentSeconds())
                .answeredAt(userAnswer.getAnsweredAt())
                .questionText(question.getContent())
                .explanation(question.getExplanation())
                .reference(question.getReferenceUrl())
                .build();
    }

    /**
     * Update session statistics based on answer submission.
     * Handles both new answers and updates to existing answers.
     */
    private void updateSessionStatistics(UserExamSession session, boolean wasAnsweredBefore,
                                         Boolean wasPreviouslyCorrect, boolean isCorrect) {
        if (!wasAnsweredBefore) {
            // New answer
            session.setAnsweredCount(session.getAnsweredCount() + 1);
            session.setUnansweredCount(Math.max(0, session.getUnansweredCount() - 1));

            if (isCorrect) {
                session.setCorrectCount(session.getCorrectCount() + 1);
            } else {
                session.setWrongCount(session.getWrongCount() + 1);
            }
        } else {
            // Update existing answer - adjust counts if correctness changed
            if (wasPreviouslyCorrect != null && wasPreviouslyCorrect != isCorrect) {
                if (isCorrect) {
                    // Was wrong, now correct
                    session.setCorrectCount(session.getCorrectCount() + 1);
                    session.setWrongCount(Math.max(0, session.getWrongCount() - 1));
                } else {
                    // Was correct, now wrong
                    session.setCorrectCount(Math.max(0, session.getCorrectCount() - 1));
                    session.setWrongCount(session.getWrongCount() + 1);
                }
            }
        }
    }

    private UserExamSession findSessionOrThrow(Long sessionId) {
        return userExamSessionRepository.findById(sessionId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION, sessionId));
    }

    private void validateSessionOwner(UserExamSession session, Long userId) {
        if (!session.getUserId().equals(userId)) {
            throw BusinessException.invalidInput("You don't have access to this session");
        }
    }

    private void validateSessionActive(UserExamSession session) {
        if (!STATUS_IN_PROGRESS.equals(session.getStatus())) {
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
