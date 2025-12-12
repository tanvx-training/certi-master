package com.certimaster.resultservice.service.impl;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.resultservice.entity.UserAnswer;
import com.certimaster.resultservice.entity.UserExamSession;
import com.certimaster.resultservice.repository.UserAnswerRepository;
import com.certimaster.resultservice.repository.UserExamSessionRepository;
import com.certimaster.resultservice.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of ExamResultService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamResultServiceImpl implements ExamResultService {

    private final UserExamSessionRepository sessionRepository;
    private final UserAnswerRepository answerRepository;

    @Override
    @Transactional
    public void createSession(ExamSessionStartedEvent event) {
        log.debug("Creating session from event: {}", event.getSessionId());

        // Check if session already exists (idempotency)
        if (sessionRepository.existsById(event.getSessionId())) {
            log.warn("Session {} already exists, skipping", event.getSessionId());
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        UserExamSession session = UserExamSession.builder()
                .id(event.getSessionId())
                .userId(event.getUserId())
                .examId(event.getExamId())
                .certificationId(event.getCertificationId())
                .startTime(event.getStartTime())
                .status("IN_PROGRESS")
                .mode(event.getMode())
                .examTitle(event.getExamTitle())
                .totalQuestions(event.getTotalQuestions())
                .durationMinutes(event.getDurationMinutes())
                .unansweredCount(event.getTotalQuestions())
                .createdAt(now)
                .updatedAt(now)
                .build();

        sessionRepository.save(session);
        log.info("Created session {} for user {}", event.getSessionId(), event.getUserId());
    }

    @Override
    @Transactional
    public void saveAnswer(AnswerSubmittedEvent event) {
        log.debug("Saving answer for session {} question {}", event.getSessionId(), event.getQuestionId());

        // Find or create answer (upsert)
        Optional<UserAnswer> existingAnswer = answerRepository
                .findBySessionIdAndQuestionId(event.getSessionId(), event.getQuestionId());

        LocalDateTime now = LocalDateTime.now();
        UserAnswer answer;

        if (existingAnswer.isPresent()) {
            // Update existing answer
            answer = existingAnswer.get();
            answer.setSelectedOptionIds(event.getSelectedOptionIds().toArray(new Long[0]));
            answer.setIsCorrect(event.getIsCorrect());
            answer.setIsFlagged(event.getIsFlagged());
            answer.setTimeSpentSeconds(event.getTimeSpentSeconds());
            answer.setAnsweredAt(event.getAnsweredAt());
            answer.setUpdatedAt(now);
            log.debug("Updated existing answer for question {}", event.getQuestionId());
        } else {
            // Create new answer
            answer = UserAnswer.builder()
                    .sessionId(event.getSessionId())
                    .questionId(event.getQuestionId())
                    .selectedOptionIds(event.getSelectedOptionIds().toArray(new Long[0]))
                    .isCorrect(event.getIsCorrect())
                    .isFlagged(event.getIsFlagged())
                    .timeSpentSeconds(event.getTimeSpentSeconds())
                    .answeredAt(event.getAnsweredAt())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            log.debug("Created new answer for question {}", event.getQuestionId());
        }

        answerRepository.save(answer);

        // Update session statistics
        updateSessionStats(event.getSessionId());
    }

    private void updateSessionStats(Long sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            long answeredCount = answerRepository.findBySessionId(sessionId).size();
            long correctCount = answerRepository.countBySessionIdAndIsCorrectTrue(sessionId);
            long wrongCount = answerRepository.countBySessionIdAndIsCorrectFalse(sessionId);
            long flaggedCount = answerRepository.countBySessionIdAndIsFlaggedTrue(sessionId);

            session.setAnsweredCount((int) answeredCount);
            session.setCorrectCount((int) correctCount);
            session.setWrongCount((int) wrongCount);
            session.setFlaggedCount((int) flaggedCount);
            session.setUnansweredCount(session.getTotalQuestions() - (int) answeredCount);
            session.setUpdatedAt(LocalDateTime.now());

            sessionRepository.save(session);
            log.debug("Updated session {} stats: answered={}, correct={}, wrong={}",
                    sessionId, answeredCount, correctCount, wrongCount);
        });
    }
}
