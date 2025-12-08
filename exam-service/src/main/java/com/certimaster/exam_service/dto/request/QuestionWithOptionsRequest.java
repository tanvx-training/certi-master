package com.certimaster.exam_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating or updating a question with its options.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionWithOptionsRequest {

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

    /**
     * List of options for this question.
     * Must have at least 2 options.
     */
    @Valid
    @NotEmpty(message = "At least one option is required")
    @Size(min = 2, max = 10, message = "Question must have between 2 and 10 options")
    private List<QuestionOptionRequest> options;
}
