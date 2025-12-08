package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for submitting an answer to a question during an exam session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerQuestionRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotEmpty(message = "At least one option must be selected")
    private List<Long> selectedOptionIds;

    @Min(value = 0, message = "Time spent must be non-negative")
    private Integer timeSpent;

    private Boolean isConfident;
}
