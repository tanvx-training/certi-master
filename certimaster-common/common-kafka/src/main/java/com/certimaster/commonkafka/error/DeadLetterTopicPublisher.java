package com.certimaster.commonkafka.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Publisher for dead letter topics
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterTopicPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String DLT_SUFFIX = ".DLT";

    /**
     * Send failed message to dead letter topic
     */
    public void sendToDeadLetterTopic(
            String originalTopic,
            Object payload,
            Exception exception) {

        String dltTopic = originalTopic + DLT_SUFFIX;

        Map<String, Object> deadLetter = new HashMap<>();
        deadLetter.put("originalTopic", originalTopic);
        deadLetter.put("payload", payload);
        deadLetter.put("error", exception.getMessage());
        deadLetter.put("timestamp", LocalDateTime.now());
        deadLetter.put("stackTrace", getStackTrace(exception));

        kafkaTemplate.send(dltTopic, deadLetter)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent to DLT: {}", dltTopic);
                    } else {
                        log.error("Failed to send message to DLT", ex);
                    }
                });
    }

    private String getStackTrace(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
