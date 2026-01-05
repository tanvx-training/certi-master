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
 * Response DTO for detailed post data (single post view).
 * Includes both raw Markdown content and rendered HTML.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 * 
 * @see Requirements 1.7
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostDetailResponse extends BaseDto {

    /**
     * Title of the post.
     */
    private String title;

    /**
     * URL-friendly slug of the post.
     */
    private String slug;

    /**
     * Raw Markdown content of the post.
     */
    private String content;

    /**
     * Rendered HTML content of the post.
     */
    private String contentHtml;

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
     * SEO title for search engine optimization.
     */
    private String seoTitle;

    /**
     * SEO description for search engine optimization.
     */
    private String seoDescription;

    /**
     * SEO keywords for search engine optimization.
     */
    private String seoKeywords;

    /**
     * Categories assigned to the post.
     */
    private List<CategoryResponse> categories;

    /**
     * Tags assigned to the post.
     */
    private List<TagResponse> tags;

    /**
     * Current user's reaction on this post (if authenticated).
     */
    private ReactionResponse currentUserReaction;
}
