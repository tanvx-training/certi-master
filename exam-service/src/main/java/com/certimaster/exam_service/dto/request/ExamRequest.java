package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating exam data.
 * Contains validation constraints for all fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequest {

    /**
     * ID of the certification this exam belongs to.
     */
    @NotNull(message = "Certification ID is required")
    private Long certificationId;

    /**
     * Title of the exam.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    /**
     * Type of the exam.
     * Valid values: PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE
     */
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(PRACTICE|MOCK|FINAL|DIAGNOSTIC|TOPIC_WISE)$", 
             message = "Type must be one of: PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE")
    private String type;

    /**
     * Description of the exam.
     */
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    /**
     * Duration of the exam in minutes.
     */
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;

    /**
     * Total number of questions in the exam.
     */
    @Min(value = 1, message = "Total questions must be at least 1")
    @Max(value = 500, message = "Total questions must not exceed 500")
    private Integer totalQuestions;

    /**
     * Passing score required to pass the exam.
     */
    @Min(value = 0, message = "Passing score must be non-negative")
    @Max(value = 1000, message = "Passing score must not exceed 1000")
    private Integer passingScore;

    /**
     * Status of the exam.
     * Valid values: ACTIVE, INACTIVE, DRAFT
     */
    @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$", 
             message = "Status must be one of: ACTIVE, INACTIVE, DRAFT")
    private String status;
}
