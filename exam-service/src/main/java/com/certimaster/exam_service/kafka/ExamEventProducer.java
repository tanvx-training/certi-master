package com.certimaster.exam_service.kafka;

import com.certimaster.common_library.event.ExamCompletedEvent;
import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.common_library.event.KafkaTopics;
import com.certimaster.common_library.exception.business.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.time.Duration;
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

    private final ReplyingKafkaTemplate<String, ExamCompletedEvent, ExamResultResponse> examCompletedReplyingKafkaTemplate;

    @Value("${exam.session.reply-timeout-seconds:30}")
    private long replyTimeoutSeconds;

    /**
     * Publish exam completed event and wait for result calculation reply.
     *
     * @param event the exam completed event
     * @return the exam result response with calculated scores
     */
    public ExamResultResponse publishExamCompletedAndWaitReply(ExamCompletedEvent event) {
        String key = event.getSessionId() + "-" + event.getUserId();

        ProducerRecord<String, ExamCompletedEvent> record =
                new ProducerRecord<>(KafkaTopics.EXAM_COMPLETED, key, event);

        log.info("Sending ExamCompletedEvent for session {} user {} and waiting for result reply",
                event.getSessionId(), event.getUserId());

        try {
            RequestReplyFuture<String, ExamCompletedEvent, ExamResultResponse> future =
                    examCompletedReplyingKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(replyTimeoutSeconds));

            ConsumerRecord<String, ExamResultResponse> reply = future.get(replyTimeoutSeconds, TimeUnit.SECONDS);
            ExamResultResponse resultResponse = reply.value();

            if (resultResponse.isSuccess()) {
                log.info("Received result reply: session {} score {}% status {}",
                        event.getSessionId(), resultResponse.getPercentage(), resultResponse.getPassStatus());
            } else {
                log.error("Result calculation failed: {}", resultResponse.getErrorMessage());
                throw BusinessException.invalidInput(resultResponse.getErrorMessage());
            }

            return resultResponse;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for result calculation reply", e);
            throw BusinessException.invalidInput("Result calculation was interrupted");
        } catch (ExecutionException e) {
            log.error("Error during result calculation", e);
            throw BusinessException.invalidInput("Failed to calculate results: " + e.getMessage());
        } catch (TimeoutException e) {
            log.error("Timeout waiting for result calculation reply", e);
            throw BusinessException.invalidInput("Result calculation timed out. Please try again.");
        }
    }
}
