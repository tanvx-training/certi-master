package com.certimaster.exam_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for exam session settings.
 * Contains optional configuration for how the exam should be presented.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSettingsRequest {

    /**
     * Whether to randomize the order of questions.
     */
    private Boolean randomizeQuestions;

    /**
     * Whether to randomize the order of answer options.
     */
    private Boolean randomizeOptions;

    /**
     * Whether to allow pausing the exam.
     */
    private Boolean allowPause;
}
