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
 * Request DTO for creating or updating questions.
 * Contains validation rules for question data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Question type is required")
    @Pattern(regexp = "^(SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE)$", 
             message = "Type must be one of: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE")
    private String type;

    @NotBlank(message = "Question content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Size(max = 10000, message = "Explanation must not exceed 10000 characters")
    private String explanation;

    @Pattern(regexp = "^(EASY|MEDIUM|HARD)$", 
             message = "Difficulty must be one of: EASY, MEDIUM, HARD")
    private String difficulty;

    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100, message = "Points must not exceed 100")
    private Integer points;

    @Min(value = 0, message = "Time limit must be non-negative")
    @Max(value = 3600, message = "Time limit must not exceed 3600 seconds")
    private Integer timeLimitSeconds;

    @Size(max = 500, message = "Reference URL must not exceed 500 characters")
    private String referenceUrl;
}
