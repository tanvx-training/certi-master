package com.certimaster.blog_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for searching posts with filtering and pagination.
 * 
 * @see Requirements 7.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchRequest {

    /**
     * Search keyword for full-text search on title and content.
     */
    private String keyword;

    /**
     * Filter by post status (DRAFT, PUBLISHED, ARCHIVED).
     */
    private String status;

    /**
     * Filter by author ID.
     */
    private Long authorId;

    /**
     * Filter by category ID.
     */
    private Long categoryId;

    /**
     * Filter by tag ID.
     */
    private Long tagId;

    /**
     * Filter posts published after this date.
     */
    private LocalDateTime publishedFrom;

    /**
     * Filter posts published before this date.
     */
    private LocalDateTime publishedTo;

    /**
     * Page number (0-based).
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Page size.
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * Sort field (e.g., createdAt, publishedAt, viewsCount, likesCount).
     */
    @Builder.Default
    private String sortBy = "createdAt";

    /**
     * Sort direction (ASC, DESC).
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
