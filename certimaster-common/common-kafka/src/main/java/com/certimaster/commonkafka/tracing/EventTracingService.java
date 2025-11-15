package com.certimaster.commonkafka.tracing;

import com.certimaster.commonkafka.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for event tracing and correlation
 */
@Slf4j
@Service
public class EventTracingService {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    /**
     * Generate new correlation ID
     */
    public String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        CORRELATION_ID.set(correlationId);
        return correlationId;
    }

    /**
     * Get current correlation ID
     */
    public String getCorrelationId() {
        String correlationId = CORRELATION_ID.get();
        if (correlationId == null) {
            correlationId = generateCorrelationId();
        }
        return correlationId;
    }

    /**
     * Set correlation ID from incoming event
     */
    public void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    /**
     * Clear correlation ID
     */
    public void clearCorrelationId() {
        CORRELATION_ID.remove();
    }

    /**
     * Add tracing metadata to event
     */
    public void addTracingMetadata(BaseEvent event) {
        String correlationId = getCorrelationId();
        event.setCorrelationId(correlationId);
        event.addMetadata("traceId", correlationId);
        event.addMetadata("spanId", UUID.randomUUID().toString());
    }
}
