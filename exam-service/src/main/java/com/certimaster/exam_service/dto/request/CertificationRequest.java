package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating or updating a certification.
 * Contains all necessary fields with validation constraints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {

    /**
     * The name of the certification.
     * Must not be blank and cannot exceed 255 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    /**
     * The unique code identifying the certification.
     * Must not be blank, cannot exceed 50 characters, and must contain only uppercase letters, numbers, and hyphens.
     */
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Code must contain only uppercase letters, numbers, and hyphens")
    private String code;

    /**
     * The provider or organization offering the certification.
     * Cannot exceed 100 characters.
     */
    @Size(max = 100, message = "Provider must not exceed 100 characters")
    private String provider;

    /**
     * A detailed description of the certification.
     * Cannot exceed 5000 characters.
     */
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    /**
     * The difficulty level of the certification.
     * Must be one of: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT.
     */
    @Pattern(regexp = "^(BEGINNER|INTERMEDIATE|ADVANCED|EXPERT)$", message = "Invalid level")
    private String level;

    /**
     * The duration of the certification exam in minutes.
     * Must be between 1 and 600 minutes.
     */
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;

    /**
     * The minimum score required to pass the certification exam.
     * Must be between 0.0 and 1000.0.
     */
    @DecimalMin(value = "0.0", message = "Passing score must be non-negative")
    @DecimalMax(value = "1000.0", message = "Passing score must not exceed 1000")
    private BigDecimal passingScore;

    /**
     * The total number of questions in the certification exam.
     * Must be between 1 and 500 questions.
     */
    @Min(value = 1, message = "Total questions must be at least 1")
    @Max(value = 500, message = "Total questions must not exceed 500")
    private Integer totalQuestions;

    /**
     * The price of the certification exam.
     * Must be non-negative.
     */
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    /**
     * The current status of the certification.
     * Must be one of: ACTIVE, INACTIVE, DRAFT.
     */
    @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$", message = "Invalid status")
    private String status;
}
