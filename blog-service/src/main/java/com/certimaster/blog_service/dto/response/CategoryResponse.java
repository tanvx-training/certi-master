package com.certimaster.blog_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for category data.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 * 
 * @see Requirements 3.1
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryResponse extends BaseDto {

    /**
     * Name of the category.
     */
    private String name;

    /**
     * URL-friendly slug of the category.
     */
    private String slug;

    /**
     * Description of the category.
     */
    private String description;

    /**
     * Number of posts in this category.
     */
    private Long postCount;
}
