package com.certimaster.blog_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for tag data.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 * 
 * @see Requirements 4.4
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagResponse extends BaseDto {

    /**
     * Name of the tag.
     */
    private String name;

    /**
     * URL-friendly slug of the tag.
     */
    private String slug;

    /**
     * Number of posts with this tag.
     */
    private Long postCount;
}
