package com.certimaster.exam_service.service;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.exam_service.dto.request.ExamRequest;
import com.certimaster.exam_service.dto.request.ExamSearchRequest;
import com.certimaster.exam_service.dto.response.ExamDetailResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;

import java.util.List;

/**
 * Service interface for Exam operations.
 */
public interface ExamService {

    /**
     * Search exams with filters and pagination.
     *
     * @param request search criteria
     * @return paginated list of exams
     */
    PageDto<ExamResponse> search(ExamSearchRequest request);

    /**
     * Get exam by ID.
     *
     * @param id exam ID
     * @return exam details
     */
    ExamDetailResponse getById(Long id);

    /**
     * Get exams by certification ID.
     *
     * @param certificationId certification ID
     * @return list of exams
     */
    List<ExamResponse> getByCertificationId(Long certificationId);

    /**
     * Create a new exam.
     *
     * @param request exam data
     * @return created exam
     */
    ExamResponse create(ExamRequest request);

    /**
     * Update an existing exam.
     *
     * @param id exam ID
     * @param request updated exam data
     * @return updated exam
     */
    ExamResponse update(Long id, ExamRequest request);

    /**
     * Soft delete an exam (set status to DELETED).
     *
     * @param id exam ID
     */
    void delete(Long id);
}
