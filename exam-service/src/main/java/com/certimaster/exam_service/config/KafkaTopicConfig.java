package com.certimaster.exam_service.config;

import com.certimaster.common_library.event.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration.
 * Auto-creates required topics on startup.
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Topic for exam completed events.
     */
    @Bean
    public NewTopic examCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.EXAM_COMPLETED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Topic for exam result reply events.
     */
    @Bean
    public NewTopic examResultReplyTopic() {
        return TopicBuilder.name(KafkaTopics.EXAM_RESULT_REPLY)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
