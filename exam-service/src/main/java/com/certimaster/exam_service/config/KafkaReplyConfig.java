package com.certimaster.exam_service.config;

import com.certimaster.common_library.event.ExamSessionCreatedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;
import com.certimaster.common_library.event.KafkaTopics;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka configuration for Request-Reply pattern.
 */
@Configuration
public class KafkaReplyConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092,localhost:9094,localhost:9096}")
    private String bootstrapServers;

    @Value("${exam.session.reply-timeout-seconds:30}")
    private long replyTimeoutSeconds;

    @Bean
    public ProducerFactory<String, ExamSessionStartedEvent> sessionProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, ExamSessionCreatedEvent> replyConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "exam-service-reply-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.certimaster.*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(ExamSessionCreatedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ExamSessionCreatedEvent> replyListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExamSessionCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(replyConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, ExamSessionCreatedEvent> replyContainer() {
        ConcurrentMessageListenerContainer<String, ExamSessionCreatedEvent> container =
                replyListenerContainerFactory().createContainer(KafkaTopics.EXAM_SESSION_CREATED_REPLY);
        container.getContainerProperties().setGroupId("exam-service-reply-" + UUID.randomUUID());
        container.setAutoStartup(true);
        return container;
    }

    @Bean
    public ReplyingKafkaTemplate<String, ExamSessionStartedEvent, ExamSessionCreatedEvent> replyingKafkaTemplate() {
        ReplyingKafkaTemplate<String, ExamSessionStartedEvent, ExamSessionCreatedEvent> template =
                new ReplyingKafkaTemplate<>(sessionProducerFactory(), replyContainer());
        template.setDefaultReplyTimeout(Duration.ofSeconds(replyTimeoutSeconds));
        template.setSharedReplyTopic(true);
        return template;
    }
}
