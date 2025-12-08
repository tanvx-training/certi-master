package com.certimaster.exam_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for searching questions with filtering and pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchRequest {

    /**
     * Search keyword for content.
     */
    private String keyword;

    /**
     * Filter by topic ID.
     */
    private Long topicId;

    /**
     * Filter by question type (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE).
     */
    private String type;

    /**
     * Filter by difficulty (EASY, MEDIUM, HARD).
     */
    private String difficulty;

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
     * Sort field.
     */
    @Builder.Default
    private String sortBy = "createdAt";

    /**
     * Sort direction (ASC, DESC).
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
