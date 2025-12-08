package com.certimaster.exam_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a tag.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {

    /**
     * The name of the tag.
     * Must not be blank and cannot exceed 100 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    /**
     * Status of the tag.
     * Valid values: ACTIVE, INACTIVE.
     */
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be one of: ACTIVE, INACTIVE")
    private String status;
}
