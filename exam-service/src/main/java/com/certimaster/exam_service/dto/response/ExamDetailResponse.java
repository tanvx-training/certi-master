package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Detailed response DTO for exam data with nested exam questions.
 * Extends ExamResponse to inherit all basic exam fields.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamDetailResponse extends ExamResponse {

    /**
     * List of exam questions associated with this exam.
     */
    private List<ExamQuestionResponse> examQuestions;
    
    // Inherits all fields from ExamResponse and BaseDto
}
