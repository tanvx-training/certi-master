package com.certimaster.commonkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration
 */
@Configuration
public class KafkaTopicConfig {

    // Topic names
    public static final String USER_EVENTS = "user-events";
    public static final String EXAM_EVENTS = "exam-events";
    public static final String RESULT_EVENTS = "result-events";
    public static final String NOTIFICATION_EVENTS = "notification-events";
    public static final String ANALYTICS_EVENTS = "analytics-events";
    public static final String AUDIT_EVENTS = "audit-events";

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(USER_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000") // 7 days
                .config("compression.type", "snappy")
                .build();
    }

    @Bean
    public NewTopic examEventsTopic() {
        return TopicBuilder.name(EXAM_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .build();
    }

    @Bean
    public NewTopic resultEventsTopic() {
        return TopicBuilder.name(RESULT_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "86400000") // 1 day
                .build();
    }

    @Bean
    public NewTopic analyticsEventsTopic() {
        return TopicBuilder.name(ANALYTICS_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "2592000000") // 30 days
                .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(AUDIT_EVENTS)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "7776000000") // 90 days
                .build();
    }
}
