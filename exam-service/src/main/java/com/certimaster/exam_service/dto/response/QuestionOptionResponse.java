package com.certimaster.exam_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for question options.
 * The isCorrect field is conditionally included based on context (e.g., practice mode).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionResponse {

    private Long id;
    
    private String content;
    
    private Integer orderIndex;
    
    /**
     * Indicates whether this option is correct.
     * Only included in practice mode or when showing answers.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCorrect;
}
