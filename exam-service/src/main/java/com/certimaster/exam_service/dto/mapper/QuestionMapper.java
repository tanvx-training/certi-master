package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.QuestionRequest;
import com.certimaster.exam_service.dto.request.QuestionWithOptionsRequest;
import com.certimaster.exam_service.dto.response.QuestionOptionResponse;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Question entities and DTOs.
 * Handles transformation of question data between domain and API layers.
 * Provides null-safe mapping operations with nested null handling.
 */
@Component
public class QuestionMapper {

    /**
     * Converts a Question entity to a QuestionResponse DTO.
     * Maps question options with isCorrect field included.
     *
     * @param question the question entity
     * @return the question response DTO, or null if input is null
     */
    public QuestionResponse toResponse(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionResponse.builder()
                .id(question.getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .type(question.getType())
                .content(question.getContent())
                .explanation(question.getExplanation())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .referenceUrl(question.getReferenceUrl())
                .topicId(question.getTopic() != null ? question.getTopic().getId() : null)
                .options(mapOptionsWithCorrect(question.getQuestionOptions()))
                .build();
    }

    /**
     * Converts a Question entity to a QuestionResponse DTO without isCorrect field.
     * Used for exam mode where correct answers should not be revealed.
     *
     * @param question the question entity
     * @return the question response DTO without correct answer indicators, or null if input is null
     */
    public QuestionResponse toResponseWithoutCorrect(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionResponse.builder()
                .id(question.getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .type(question.getType())
                .content(question.getContent())
                .explanation(question.getExplanation())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .referenceUrl(question.getReferenceUrl())
                .topicId(question.getTopic() != null ? question.getTopic().getId() : null)
                .options(mapOptionsWithoutCorrect(question.getQuestionOptions()))
                .build();
    }

    /**
     * Converts a QuestionRequest DTO to a Question entity.
     *
     * @param request the question request DTO
     * @return the question entity, or null if input is null
     */
    public Question toEntity(QuestionRequest request) {
        if (request == null) {
            return null;
        }
        return Question.builder()
                .type(request.getType())
                .content(request.getContent())
                .explanation(request.getExplanation())
                .difficulty(request.getDifficulty())
                .points(request.getPoints())
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .referenceUrl(request.getReferenceUrl())
                .build();
    }

    /**
     * Converts a QuestionWithOptionsRequest DTO to a Question entity.
     *
     * @param request the question with options request DTO
     * @return the question entity, or null if input is null
     */
    public Question toEntity(QuestionWithOptionsRequest request) {
        if (request == null) {
            return null;
        }
        return Question.builder()
                .type(request.getType())
                .content(request.getContent())
                .explanation(request.getExplanation())
                .difficulty(request.getDifficulty())
                .points(request.getPoints())
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .referenceUrl(request.getReferenceUrl())
                .build();
    }

    /**
     * Updates an existing Question entity with data from a QuestionRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param question the target question entity to update
     * @param request the question request DTO with updated data
     */
    public void updateEntity(Question question, QuestionRequest request) {
        if (question == null || request == null) {
            return;
        }
        if (request.getType() != null) {
            question.setType(request.getType());
        }
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
        if (request.getExplanation() != null) {
            question.setExplanation(request.getExplanation());
        }
        if (request.getDifficulty() != null) {
            question.setDifficulty(request.getDifficulty());
        }
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        }
        if (request.getTimeLimitSeconds() != null) {
            question.setTimeLimitSeconds(request.getTimeLimitSeconds());
        }
        if (request.getReferenceUrl() != null) {
            question.setReferenceUrl(request.getReferenceUrl());
        }
    }

    /**
     * Updates an existing Question entity with data from a QuestionWithOptionsRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param question the target question entity to update
     * @param request the question with options request DTO with updated data
     */
    public void updateEntity(Question question, QuestionWithOptionsRequest request) {
        if (question == null || request == null) {
            return;
        }
        if (request.getType() != null) {
            question.setType(request.getType());
        }
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
        if (request.getExplanation() != null) {
            question.setExplanation(request.getExplanation());
        }
        if (request.getDifficulty() != null) {
            question.setDifficulty(request.getDifficulty());
        }
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        }
        if (request.getTimeLimitSeconds() != null) {
            question.setTimeLimitSeconds(request.getTimeLimitSeconds());
        }
        if (request.getReferenceUrl() != null) {
            question.setReferenceUrl(request.getReferenceUrl());
        }
    }

    /**
     * Maps question options with isCorrect field included.
     * Used for practice mode or when showing answers.
     * Returns empty list if options is null or empty.
     *
     * @param options the set of question option entities
     * @return list of question option response DTOs with isCorrect field
     */
    private List<QuestionOptionResponse> mapOptionsWithCorrect(Set<QuestionOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .filter(option -> option != null)
                .map(option -> QuestionOptionResponse.builder()
                        .id(option.getId())
                        .content(option.getContent())
                        .orderIndex(option.getOrderIndex())
                        .isCorrect(option.getIsCorrect())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Maps question options without isCorrect field.
     * Used for exam mode where correct answers should not be revealed.
     * Returns empty list if options is null or empty.
     *
     * @param options the set of question option entities
     * @return list of question option response DTOs without isCorrect field
     */
    private List<QuestionOptionResponse> mapOptionsWithoutCorrect(Set<QuestionOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .filter(option -> option != null)
                .map(option -> QuestionOptionResponse.builder()
                        .id(option.getId())
                        .content(option.getContent())
                        .orderIndex(option.getOrderIndex())
                        .isCorrect(null)  // Explicitly set to null to exclude from JSON
                        .build())
                .collect(Collectors.toList());
    }
}
