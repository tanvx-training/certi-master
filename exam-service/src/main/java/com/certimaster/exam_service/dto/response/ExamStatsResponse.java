package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for exam statistics.
 * Provides aggregated data about exam attempts and performance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStatsResponse {

    /**
     * The total number of attempts made on this exam.
     */
    private Long totalAttempts;

    /**
     * The average score achieved across all exam attempts.
     */
    private BigDecimal averageScore;

    /**
     * The percentage of exam attempts that resulted in a passing score.
     */
    private BigDecimal passRate;

    /**
     * The percentage of exam attempts that were completed (not abandoned).
     */
    private BigDecimal completionRate;
}
