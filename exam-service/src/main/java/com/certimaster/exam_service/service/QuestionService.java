package com.certimaster.exam_service.service;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.exam_service.dto.request.QuestionSearchRequest;
import com.certimaster.exam_service.dto.request.QuestionWithOptionsRequest;
import com.certimaster.exam_service.dto.response.QuestionResponse;

import java.util.List;

/**
 * Service interface for Question operations.
 */
public interface QuestionService {

    /**
     * Search questions with filters and pagination.
     *
     * @param request search criteria
     * @return paginated list of questions
     */
    PageDto<QuestionResponse> search(QuestionSearchRequest request);

    /**
     * Get question by ID with options (including isCorrect).
     *
     * @param id question ID
     * @return question details with correct answers
     */
    QuestionResponse getById(Long id);

    /**
     * Get question by ID without correct answers (for exam mode).
     *
     * @param id question ID
     * @return question details without correct answers
     */
    QuestionResponse getByIdForExam(Long id);

    /**
     * Get questions by topic ID.
     *
     * @param topicId topic ID
     * @return list of questions
     */
    List<QuestionResponse> getByTopicId(Long topicId);

    /**
     * Create a new question with options.
     *
     * @param request question data with options
     * @return created question
     */
    QuestionResponse create(QuestionWithOptionsRequest request);

    /**
     * Update an existing question with options.
     *
     * @param id question ID
     * @param request updated question data with options
     * @return updated question
     */
    QuestionResponse update(Long id, QuestionWithOptionsRequest request);

    /**
     * Delete a question and its options.
     *
     * @param id question ID
     */
    void delete(Long id);
}
