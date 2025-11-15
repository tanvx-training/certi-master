package com.certimaster.commonkafka.publisher;

import com.certimaster.commonkafka.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Base event consumer with common functionality
 */
@Slf4j
public abstract class BaseEventConsumer<T extends BaseEvent> {

    /**
     * Process event (to be implemented by subclasses)
     */
    protected abstract void processEvent(T event);

    /**
     * Handle event consumption
     */
    protected void handleEvent(
            @Payload T event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Received event {} from topic {} partition {} offset {}",
                    event.getEventId(), topic, partition, offset);

            // Process event
            processEvent(event);

            // Acknowledge message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.debug("Event {} acknowledged", event.getEventId());
            }

        } catch (Exception ex) {
            log.error("Error processing event {}: {}",
                    event.getEventId(), ex.getMessage(), ex);

            // Don't acknowledge - will be retried
            // Consider implementing dead letter queue for repeated failures
        }
    }

    /**
     * Validate event
     */
    protected boolean validateEvent(T event) {
        if (event == null) {
            log.warn("Received null event");
            return false;
        }

        if (event.getEventId() == null) {
            log.warn("Event has null eventId");
            return false;
        }

        return true;
    }
}

