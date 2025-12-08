package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for answer feedback.
 * Provides information about whether an answer was correct and includes explanations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerFeedbackResponse {

    private Long questionId;
    
    private Boolean answered;
    
    private Boolean isCorrect;
    
    private List<Long> correctOptionIds;
    
    private String explanation;
    
    private List<String> referenceLinks;
}
