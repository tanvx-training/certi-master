package com.certimaster.exam_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for searching topics with filtering and pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicSearchRequest {

    /**
     * Search keyword for name, code, or description.
     */
    private String keyword;

    /**
     * Filter by certification ID.
     */
    private Long certificationId;

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
    private String sortBy = "orderIndex";

    /**
     * Sort direction (ASC, DESC).
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
