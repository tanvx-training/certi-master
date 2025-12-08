package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Response DTO for certification data.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CertificationResponse extends BaseDto {

    /**
     * The name of the certification.
     */
    private String name;

    /**
     * The unique code identifying the certification.
     */
    private String code;

    /**
     * The provider or organization offering the certification.
     */
    private String provider;

    /**
     * The difficulty level of the certification.
     */
    private String level;

    /**
     * A detailed description of the certification.
     */
    private String description;

    /**
     * The duration of the certification exam in minutes.
     */
    private Integer durationMinutes;

    /**
     * The total number of questions in the certification exam.
     */
    private Integer totalQuestions;

    /**
     * The minimum score required to pass the certification exam.
     */
    private BigDecimal passingScore;

    /**
     * The price of the certification exam.
     */
    private BigDecimal price;

    /**
     * The current status of the certification.
     */
    private String status;
}
