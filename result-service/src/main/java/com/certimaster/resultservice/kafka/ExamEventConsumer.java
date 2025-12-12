package com.certimaster.resultservice.kafka;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.event.KafkaTopics;
import com.certimaster.resultservice.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for exam-related events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExamEventConsumer {

    private final ExamResultService examResultService;

    /**
     * Handle exam session started event.
     */
    @KafkaListener(
            topics = KafkaTopics.EXAM_SESSION_STARTED,
            containerFactory = "sessionKafkaListenerContainerFactory"
    )
    public void handleSessionStarted(ExamSessionStartedEvent event) {
        log.info("Received ExamSessionStartedEvent for session {} user {}",
                event.getSessionId(), event.getUserId());

        try {
            examResultService.createSession(event);
            log.info("Successfully created session {} in result-service", event.getSessionId());
        } catch (Exception e) {
            log.error("Failed to process ExamSessionStartedEvent for session {}",
                    event.getSessionId(), e);
            // Could implement retry logic or dead letter queue here
        }
    }

    /**
     * Handle answer submitted event.
     */
    @KafkaListener(
            topics = KafkaTopics.ANSWER_SUBMITTED,
            containerFactory = "answerKafkaListenerContainerFactory"
    )
    public void handleAnswerSubmitted(AnswerSubmittedEvent event) {
        log.info("Received AnswerSubmittedEvent for session {} question {}",
                event.getSessionId(), event.getQuestionId());

        try {
            examResultService.saveAnswer(event);
            log.info("Successfully saved answer for session {} question {}",
                    event.getSessionId(), event.getQuestionId());
        } catch (Exception e) {
            log.error("Failed to process AnswerSubmittedEvent for session {} question {}",
                    event.getSessionId(), event.getQuestionId(), e);
        }
    }
}
