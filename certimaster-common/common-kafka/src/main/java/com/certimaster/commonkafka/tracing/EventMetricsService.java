package com.certimaster.commonkafka.tracing;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service for tracking event metrics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventMetricsService {

    private final MeterRegistry meterRegistry;

    /**
     * Record event published
     */
    public void recordEventPublished(String topic, String eventType) {
        Counter.builder("kafka.events.published")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Record event consumed
     */
    public void recordEventConsumed(String topic, String eventType, boolean success) {
        Counter.builder("kafka.events.consumed")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("success", String.valueOf(success))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Record processing time
     */
    public void recordProcessingTime(String eventType, Duration duration) {
        Timer.builder("kafka.events.processing.time")
                .tag("eventType", eventType)
                .register(meterRegistry)
                .record(duration);
    }

    /**
     * Record event failure
     */
    public void recordEventFailure(String topic, String eventType, String errorType) {
        Counter.builder("kafka.events.failed")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("errorType", errorType)
                .register(meterRegistry)
                .increment();
    }
}
