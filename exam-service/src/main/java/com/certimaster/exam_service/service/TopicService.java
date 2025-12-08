package com.certimaster.exam_service.service;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.exam_service.dto.request.TopicRequest;
import com.certimaster.exam_service.dto.request.TopicSearchRequest;
import com.certimaster.exam_service.dto.response.TopicResponse;

import java.util.List;

/**
 * Service interface for Topic operations.
 */
public interface TopicService {

    /**
     * Search topics with filters and pagination.
     *
     * @param request search criteria
     * @return paginated list of topics
     */
    PageDto<TopicResponse> search(TopicSearchRequest request);

    /**
     * Get topic by ID.
     *
     * @param id topic ID
     * @return topic details
     */
    TopicResponse getById(Long id);

    /**
     * Get topics by certification ID.
     *
     * @param certificationId certification ID
     * @return list of topics ordered by orderIndex
     */
    List<TopicResponse> getByCertificationId(Long certificationId);

    /**
     * Create a new topic.
     *
     * @param request topic data
     * @return created topic
     */
    TopicResponse create(TopicRequest request);

    /**
     * Update an existing topic.
     *
     * @param id topic ID
     * @param request updated topic data
     * @return updated topic
     */
    TopicResponse update(Long id, TopicRequest request);

    /**
     * Delete a topic.
     *
     * @param id topic ID
     */
    void delete(Long id);
}
