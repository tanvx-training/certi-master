package com.certimaster.blog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a tag.
 * 
 * @see Requirements 4.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {

    /**
     * Name of the tag.
     */
    @NotBlank(message = "Tag name is required")
    @Size(max = 100, message = "Tag name must not exceed 100 characters")
    private String name;
}
