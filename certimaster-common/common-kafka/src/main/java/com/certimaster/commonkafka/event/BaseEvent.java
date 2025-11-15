package com.certimaster.commonkafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base event class for all Kafka events
 * Provides common fields and metadata
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "USER_REGISTERED"),
        @JsonSubTypes.Type(value = UserUpdatedEvent.class, name = "USER_UPDATED"),
        @JsonSubTypes.Type(value = ExamCompletedEvent.class, name = "EXAM_COMPLETED"),
        @JsonSubTypes.Type(value = ExamGeneratedEvent.class, name = "EXAM_GENERATED")
})
public abstract class BaseEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique event identifier
     */
    private String eventId;

    /**
     * Event type (e.g., USER_REGISTERED, EXAM_COMPLETED)
     */
    private String eventType;

    /**
     * Source service that generated the event
     */
    private String source;

    /**
     * Timestamp when event occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    /**
     * Event version for schema evolution
     */
    private String version;

    /**
     * Correlation ID for tracing related events
     */
    private String correlationId;

    /**
     * Additional metadata
     */
    private Map<String, Object> metadata;

    /**
     * Initialize event with defaults
     */
    protected void init() {
        if (this.eventId == null) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.occurredAt == null) {
            this.occurredAt = LocalDateTime.now();
        }
        if (this.version == null) {
            this.version = "1.0";
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    /**
     * Add metadata
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
