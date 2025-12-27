package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Mode is required")
    private String mode;
}
