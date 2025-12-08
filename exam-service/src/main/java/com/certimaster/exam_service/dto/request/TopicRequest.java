package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating or updating a topic.
 * Contains all necessary fields with validation constraints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest {

    /**
     * The ID of the certification this topic belongs to.
     * Must not be null.
     */
    @NotNull(message = "Certification ID is required")
    private Long certificationId;

    /**
     * The name of the topic.
     * Must not be blank and cannot exceed 255 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    /**
     * The unique code identifying the topic.
     * Cannot exceed 50 characters and must contain only uppercase letters, numbers, and hyphens.
     */
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]*$", message = "Code must contain only uppercase letters, numbers, and hyphens")
    private String code;

    /**
     * A detailed description of the topic.
     * Cannot exceed 5000 characters.
     */
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    /**
     * The weight percentage of this topic in the certification.
     * Must be between 0.0 and 100.0.
     */
    @DecimalMin(value = "0.0", message = "Weight percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Weight percentage must not exceed 100.0")
    private BigDecimal weightPercentage;

    /**
     * The order index for displaying topics.
     * Must be non-negative.
     */
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}
