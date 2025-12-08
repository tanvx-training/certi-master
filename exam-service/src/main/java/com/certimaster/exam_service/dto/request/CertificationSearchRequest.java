package com.certimaster.exam_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for searching certifications with filtering and pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationSearchRequest {

    /**
     * Search keyword for name, code, or provider.
     */
    private String keyword;

    /**
     * Filter by provider.
     */
    private String provider;

    /**
     * Filter by level (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT).
     */
    private String level;

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
