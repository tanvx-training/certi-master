package com.certimaster.commonkafka.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Default error handler for Kafka consumers
 */
@Slf4j
@Component
public class DefaultErrorHandler implements CommonErrorHandler {

    private static final int MAX_RETRIES = 3;

    @Override
    public boolean handleOne(
            Exception thrownException,
            ConsumerRecord<?, ?> record,
            Consumer<?, ?> consumer,
            MessageListenerContainer container) {

        log.error("Error processing record from topic {} partition {} offset {}: {}",
                record.topic(),
                record.partition(),
                record.offset(),
                thrownException.getMessage(),
                thrownException);

        // Check retry count
        String retryHeader = getRetryCount(record);
        int retries = retryHeader != null ? Integer.parseInt(retryHeader) : 0;

        if (retries < MAX_RETRIES) {
            log.info("Retrying message (attempt {}/{})", retries + 1, MAX_RETRIES);
            // Return false to retry
            return false;
        } else {
            log.error("Max retries exceeded for message. Sending to DLT.");
            // Send to Dead Letter Topic
            sendToDeadLetterTopic(record, thrownException);
            // Return true to skip
            return true;
        }
    }

    private String getRetryCount(ConsumerRecord<?, ?> record) {
        // Extract retry count from headers
        org.apache.kafka.common.header.Header header =
                record.headers().lastHeader("retry-count");
        return header != null ? new String(header.value()) : null;
    }

    private void sendToDeadLetterTopic(ConsumerRecord<?, ?> record, Exception exception) {
        // Implement DLT logic
        log.error("Record sent to DLT: topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());
    }
}
