package com.certimaster.commonkafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Kafka configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers = "localhost:9092";
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private String acks = "all";
        private int retries = 3;
        private int batchSize = 16384;
        private int lingerMs = 10;
        private String compressionType = "snappy";
    }

    @Data
    public static class Consumer {
        private String groupId = "certimaster-group";
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = false;
        private int concurrency = 3;
        private int maxPollRecords = 500;
    }
}
