package com.certimaster.blog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a category.
 * 
 * @see Requirements 3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    /**
     * Name of the category.
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    /**
     * Description of the category.
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
