package com.certimaster.blog_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for post data (list view).
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 * 
 * @see Requirements 1.7
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostResponse extends BaseDto {

    /**
     * Title of the post.
     */
    private String title;

    /**
     * URL-friendly slug of the post.
     */
    private String slug;

    /**
     * Short excerpt/summary of the post.
     */
    private String excerpt;

    /**
     * URL of the featured image.
     */
    private String featuredImage;

    /**
     * ID of the author.
     */
    private Long authorId;

    /**
     * Status of the post (DRAFT, PUBLISHED, ARCHIVED).
     */
    private String status;

    /**
     * Timestamp when the post was published.
     */
    private LocalDateTime publishedAt;

    /**
     * Number of views.
     */
    private Integer viewsCount;

    /**
     * Number of likes/reactions.
     */
    private Integer likesCount;

    /**
     * Number of comments.
     */
    private Integer commentsCount;

    /**
     * Estimated reading time in minutes.
     */
    private Integer readingTimeMinutes;

    /**
     * Categories assigned to the post.
     */
    private List<CategoryResponse> categories;

    /**
     * Tags assigned to the post.
     */
    private List<TagResponse> tags;
}
