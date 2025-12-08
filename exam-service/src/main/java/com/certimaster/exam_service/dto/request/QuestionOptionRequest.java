package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating question options.
 * Represents a single answer choice for a question.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionRequest {

    @NotBlank(message = "Option content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect;

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}
