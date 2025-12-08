package com.certimaster.exam_service.service;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.exam_service.dto.request.TagRequest;
import com.certimaster.exam_service.dto.request.TagSearchRequest;
import com.certimaster.exam_service.dto.response.TagResponse;

import java.util.List;

/**
 * Service interface for Tag operations.
 */
public interface TagService {

    /**
     * Search tags with filters and pagination.
     *
     * @param request search criteria
     * @return paginated list of tags
     */
    PageDto<TagResponse> search(TagSearchRequest request);

    /**
     * Get all tags.
     *
     * @return list of all tags
     */
    List<TagResponse> getAll();

    /**
     * Get tag by ID.
     *
     * @param id tag ID
     * @return tag details
     */
    TagResponse getById(Long id);

    /**
     * Get tags by question ID.
     *
     * @param questionId question ID
     * @return list of tags
     */
    List<TagResponse> getByQuestionId(Long questionId);

    /**
     * Create a new tag.
     *
     * @param request tag data
     * @return created tag
     */
    TagResponse create(TagRequest request);

    /**
     * Update an existing tag.
     *
     * @param id tag ID
     * @param request updated tag data
     * @return updated tag
     */
    TagResponse update(Long id, TagRequest request);

    /**
     * Delete a tag.
     *
     * @param id tag ID
     */
    void delete(Long id);

    /**
     * Add tags to a question.
     *
     * @param questionId question ID
     * @param tagIds list of tag IDs
     */
    void addTagsToQuestion(Long questionId, List<Long> tagIds);

    /**
     * Remove tags from a question.
     *
     * @param questionId question ID
     * @param tagIds list of tag IDs to remove
     */
    void removeTagsFromQuestion(Long questionId, List<Long> tagIds);
}
