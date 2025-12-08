package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for topic performance data.
 * Provides detailed performance metrics for a specific topic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicPerformanceResponse {

    /**
     * The unique identifier of the topic.
     */
    private Long topicId;

    /**
     * The name of the topic.
     */
    private String topicName;

    /**
     * The total number of questions in this topic.
     */
    private Integer totalQuestions;

    /**
     * The number of questions answered correctly.
     */
    private Integer correctAnswers;

    /**
     * The number of questions answered incorrectly.
     */
    private Integer wrongAnswers;

    /**
     * The percentage of correct answers for this topic.
     */
    private BigDecimal percentage;

    /**
     * The performance status for this topic (e.g., EXCELLENT, GOOD, NEEDS_IMPROVEMENT).
     */
    private String status;
}
