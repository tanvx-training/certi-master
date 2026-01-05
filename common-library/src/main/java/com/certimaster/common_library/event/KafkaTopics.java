package com.certimaster.common_library.event;

/**
 * Kafka topic constants used across services.
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Utility class
    }

    /**
     * Topic for exam completed events.
     * Sent from exam-service to result-service when a user completes an exam.
     */
    public static final String EXAM_COMPLETED = "exam-completed";

    /**
     * Topic for exam result reply events.
     * Sent from result-service back to exam-service with calculated results.
     */
    public static final String EXAM_RESULT_REPLY = "exam-result-reply";
}
