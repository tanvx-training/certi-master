package com.certimaster.exam_service.kafka;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionCreatedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.event.KafkaTopics;
import com.certimaster.common_library.exception.business.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Kafka producer for exam-related events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExamEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, ExamSessionStartedEvent, ExamSessionCreatedEvent> replyingKafkaTemplate;

    @Value("${exam.session.reply-timeout-seconds:30}")
    private long replyTimeoutSeconds;

    /**
     * Publish exam session started event and wait for reply with session ID.
     *
     * @param event the session started event
     * @return the created session event with session ID
     */
    public ExamSessionCreatedEvent publishSessionStartedAndWaitReply(ExamSessionStartedEvent event) {
        String key = event.getUserId() + "-" + event.getExamId();

        ProducerRecord<String, ExamSessionStartedEvent> record =
                new ProducerRecord<>(KafkaTopics.EXAM_SESSION_STARTED, key, event);

        log.info("Sending ExamSessionStartedEvent for user {} exam {} and waiting for reply",
                event.getUserId(), event.getExamId());

        try {
            RequestReplyFuture<String, ExamSessionStartedEvent, ExamSessionCreatedEvent> future =
                    replyingKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(replyTimeoutSeconds));

            ConsumerRecord<String, ExamSessionCreatedEvent> reply = future.get(replyTimeoutSeconds, TimeUnit.SECONDS);
            ExamSessionCreatedEvent createdEvent = reply.value();

            if (createdEvent.isSuccess()) {
                log.info("Received reply: session {} created for user {} exam {}",
                        createdEvent.getSessionId(), event.getUserId(), event.getExamId());
            } else {
                log.error("Session creation failed: {}", createdEvent.getErrorMessage());
                throw BusinessException.invalidInput(createdEvent.getErrorMessage());
            }

            return createdEvent;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for session creation reply", e);
            throw BusinessException.invalidInput("Session creation was interrupted");
        } catch (ExecutionException e) {
            log.error("Error during session creation", e);
            throw BusinessException.invalidInput("Failed to create session: " + e.getMessage());
        } catch (TimeoutException e) {
            log.error("Timeout waiting for session creation reply", e);
            throw BusinessException.invalidInput("Session creation timed out. Please try again.");
        }
    }

    /**
     * Publish answer submitted event (fire and forget).
     */
    public void publishAnswerSubmitted(AnswerSubmittedEvent event) {
        String key = String.valueOf(event.getSessionId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaTopics.ANSWER_SUBMITTED, key, event);

        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
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
