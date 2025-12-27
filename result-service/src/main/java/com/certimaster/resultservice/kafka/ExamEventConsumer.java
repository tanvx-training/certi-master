package com.certimaster.resultservice.kafka;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionCreatedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.event.KafkaTopics;
import com.certimaster.resultservice.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
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
     * Handle exam session started event and send reply with created session ID.
     * Uses Request-Reply pattern.
     */
    @KafkaListener(
            topics = KafkaTopics.EXAM_SESSION_STARTED,
            containerFactory = "sessionKafkaListenerContainerFactory"
    )
    @SendTo
    public ExamSessionCreatedEvent handleSessionStarted(ExamSessionStartedEvent event) {
        log.info("Received ExamSessionStartedEvent for user {} exam {}",
                event.getUserId(), event.getExamId());

        ExamSessionCreatedEvent reply = examResultService.createSession(event);

        if (reply.isSuccess()) {
            log.info("Successfully created session {} for user {} exam {}",
                    reply.getSessionId(), event.getUserId(), event.getExamId());
        } else {
            log.error("Failed to create session for user {} exam {}: {}",
                    event.getUserId(), event.getExamId(), reply.getErrorMessage());
        }

        return reply;
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
