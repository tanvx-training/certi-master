package com.certimaster.exam_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for searching exams with filtering and pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSearchRequest {

    /**
     * Search keyword for title or description.
     */
    private String keyword;

    /**
     * Filter by certification ID.
     */
    private Long certificationId;

    /**
     * Filter by exam type (PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE).
     */
    private String type;

    /**
     * Filter by status (ACTIVE, INACTIVE, DRAFT).
     */
    private String status;

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
