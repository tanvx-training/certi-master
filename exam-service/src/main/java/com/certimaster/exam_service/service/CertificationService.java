package com.certimaster.exam_service.service;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.exam_service.dto.request.CertificationRequest;
import com.certimaster.exam_service.dto.request.CertificationSearchRequest;
import com.certimaster.exam_service.dto.response.CertificationDetailResponse;
import com.certimaster.exam_service.dto.response.CertificationResponse;

/**
 * Service interface for Certification operations.
 */
public interface CertificationService {

    /**
     * Search certifications with filters and pagination.
     *
     * @param request search criteria
     * @return paginated list of certifications
     */
    PageDto<CertificationResponse> search(CertificationSearchRequest request);

    /**
     * Get certification by ID.
     *
     * @param id certification ID
     * @return certification details
     */
    CertificationDetailResponse getById(Long id);

    /**
     * Create a new certification.
     *
     * @param request certification data
     * @return created certification
     */
    CertificationResponse create(CertificationRequest request);

    /**
     * Update an existing certification.
     *
     * @param id certification ID
     * @param request updated certification data
     * @return updated certification
     */
    CertificationResponse update(Long id, CertificationRequest request);

    /**
     * Soft delete a certification (set status to DELETED).
     *
     * @param id certification ID
     */
    void delete(Long id);
}
