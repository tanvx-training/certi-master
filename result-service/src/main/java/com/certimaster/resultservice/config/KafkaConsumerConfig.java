package com.certimaster.resultservice.config;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
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
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:result-service-group}")
    private String groupId;

    // Producer for reply messages
    @Bean
    public ProducerFactory<String, ExamSessionCreatedEvent> replyProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ExamSessionCreatedEvent> replyKafkaTemplate() {
        KafkaTemplate<String, ExamSessionCreatedEvent> template = new KafkaTemplate<>(replyProducerFactory());
        template.setDefaultTopic(KafkaTopics.EXAM_SESSION_CREATED_REPLY);
        return template;
    }

    @Bean
    public ConsumerFactory<String, ExamSessionStartedEvent> sessionConsumerFactory() {
        Map<String, Object> props = consumerProps();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(ExamSessionStartedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ExamSessionStartedEvent> sessionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExamSessionStartedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sessionConsumerFactory());
        factory.setReplyTemplate(replyKafkaTemplate());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AnswerSubmittedEvent> answerConsumerFactory() {
        Map<String, Object> props = consumerProps();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(AnswerSubmittedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AnswerSubmittedEvent> answerKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AnswerSubmittedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(answerConsumerFactory());
        return factory;
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.certimaster.*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
