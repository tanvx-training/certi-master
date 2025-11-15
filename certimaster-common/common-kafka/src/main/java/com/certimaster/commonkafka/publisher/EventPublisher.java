package com.certimaster.commonkafka.publisher;

import com.certimaster.commonkafka.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Event publisher for sending events to Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish event to topic
     */
    public <T extends BaseEvent> void publish(String topic, T event) {
        log.info("Publishing event {} to topic {}", event.getEventId(), topic);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, event.getEventId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event {} published successfully to partition {} with offset {}",
                        event.getEventId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event {}: {}",
                        event.getEventId(), ex.getMessage(), ex);
            }
        });
    }

    /**
     * Publish event with custom key
     */
    public <T extends BaseEvent> void publishWithKey(String topic, String key, T event) {
        log.info("Publishing event {} with key {} to topic {}",
                event.getEventId(), key, topic);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published to partition {} with offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event: {}", ex.getMessage(), ex);
            }
        });
    }

    /**
     * Publish event synchronously
     */
    public <T extends BaseEvent> SendResult<String, Object> publishSync(String topic, T event) {
        try {
            log.info("Publishing event {} synchronously to topic {}", event.getEventId(), topic);

            SendResult<String, Object> result = kafkaTemplate
                    .send(topic, event.getEventId(), event)
                    .get(); // Blocking call

            log.info("Event published synchronously to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            return result;
        } catch (Exception e) {
            log.error("Failed to publish event synchronously: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
