package com.certimaster.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a user submits an answer.
 * Sent from exam-service to result-service via Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmittedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Session ID.
     */
    private Long sessionId;

    /**
     * User ID.
     */
    private Long userId;

    /**
     * Question ID.
     */
    private Long questionId;

    /**
     * Question order index in the exam.
     */
    private Integer questionIndex;

    /**
     * Selected option IDs.
     */
    private List<Long> selectedOptionIds;

    /**
     * Correct option IDs (for immediate feedback in practice mode).
     */
    private List<Long> correctOptionIds;

    /**
     * Whether the answer is correct.
     */
    private Boolean isCorrect;

    /**
     * Whether the question is flagged for review.
     */
    private Boolean isFlagged;

    /**
     * Time spent on this question in seconds.
     */
    private Integer timeSpentSeconds;

    /**
     * Topic ID for performance tracking.
     */
    private Long topicId;

    /**
     * Topic name.
     */
    private String topicName;

    /**
     * Answer submission time.
     */
    private LocalDateTime answeredAt;

    /**
     * Event timestamp.
     */
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
