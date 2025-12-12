package com.certimaster.exam_service.kafka;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.event.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for exam-related events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExamEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish exam session started event.
     */
    public void publishSessionStarted(ExamSessionStartedEvent event) {
        String key = String.valueOf(event.getSessionId());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaTopics.EXAM_SESSION_STARTED, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent ExamSessionStartedEvent for session {} to partition {} with offset {}",
                        event.getSessionId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send ExamSessionStartedEvent for session {}", 
                        event.getSessionId(), ex);
            }
        });
    }

    /**
     * Publish answer submitted event.
     */
    public void publishAnswerSubmitted(AnswerSubmittedEvent event) {
        String key = String.valueOf(event.getSessionId());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaTopics.ANSWER_SUBMITTED, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent AnswerSubmittedEvent for session {} question {} to partition {} with offset {}",
                        event.getSessionId(),
                        event.getQuestionId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send AnswerSubmittedEvent for session {} question {}", 
                        event.getSessionId(), event.getQuestionId(), ex);
            }
        });
    }
}
