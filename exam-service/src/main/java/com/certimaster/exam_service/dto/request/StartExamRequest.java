package com.certimaster.exam_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for starting an exam session.
 * Contains mode and optional settings for the exam session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamRequest {

    /**
     * Mode of the exam session.
     * Valid values: PRACTICE, TIMED
     */
    @NotBlank(message = "Mode is required")
    @Pattern(regexp = "^(PRACTICE|TIMED)$", 
             message = "Mode must be one of: PRACTICE, TIMED")
    private String mode;

    /**
     * Optional settings for the exam session.
     */
    @Valid
    private ExamSettingsRequest settings;
}
