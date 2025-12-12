package com.certimaster.common_library.event;

/**
 * Kafka topic constants used across services.
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Utility class
    }

    /**
     * Topic for exam session started events.
     */
    public static final String EXAM_SESSION_STARTED = "exam-session-started";

    /**
     * Topic for answer submitted events.
     */
    public static final String ANSWER_SUBMITTED = "answer-submitted";

    /**
     * Topic for exam session completed events.
     */
    public static final String EXAM_SESSION_COMPLETED = "exam-session-completed";
}
