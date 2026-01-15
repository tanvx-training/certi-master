package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.ExamRequest;
import com.certimaster.exam_service.dto.response.ExamDetailResponse;
import com.certimaster.exam_service.dto.response.ExamQuestionResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.entity.ExamQuestion;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Exam entities and DTOs.
 * Handles transformation of exam data between persistence and API layers.
 * Provides null-safe mapping operations with nested null handling.
 */
@Component
public class ExamMapper {

    /**
     * Converts an Exam entity to ExamResponse DTO.
     *
     * @param exam the exam entity
     * @return the exam response DTO, or null if input is null
     */
    public ExamResponse toResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        return ExamResponse.builder()
                .id(exam.getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .createdBy(exam.getCreatedBy())
                .updatedBy(exam.getUpdatedBy())
                .title(exam.getTitle())
                .type(exam.getType())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .passingScore(exam.getPassingScore())
                .status(exam.getStatus())
                .certificationId(exam.getCertification() != null ? exam.getCertification().getId() : null)
                .certificationName(exam.getCertification() != null ? exam.getCertification().getName() : null)
                .build();
    }

    /**
     * Converts an Exam entity to ExamDetailResponse DTO with nested exam questions.
     *
     * @param exam the exam entity
     * @return the detailed exam response DTO, or null if input is null
     */
    public ExamDetailResponse toDetailResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        
        List<ExamQuestionResponse> examQuestions = mapExamQuestions(exam);
        
        return ExamDetailResponse.builder()
                .id(exam.getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .createdBy(exam.getCreatedBy())
                .updatedBy(exam.getUpdatedBy())
                .title(exam.getTitle())
                .type(exam.getType())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .passingScore(exam.getPassingScore())
                .status(exam.getStatus())
                .certificationId(exam.getCertification() != null ? exam.getCertification().getId() : null)
                .certificationName(exam.getCertification() != null ? exam.getCertification().getName() : null)
                .examQuestions(examQuestions)
                .build();
    }

    /**
     * Converts an ExamRequest DTO to Exam entity.
     * Note: Certification relationship must be set separately.
     *
     * @param request the exam request DTO
     * @return the exam entity, or null if input is null
     */
    public Exam toEntity(ExamRequest request) {
        if (request == null) {
            return null;
        }
        return Exam.builder()
                .title(request.getTitle())
                .type(request.getType())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .totalQuestions(request.getTotalQuestions())
                .passingScore(request.getPassingScore())
                .status(request.getStatus())
                .build();
    }

    /**
     * Updates an existing Exam entity with data from ExamRequest DTO.
     * Only updates non-null fields from the request (partial update).
     * Note: Certification relationship must be updated separately if needed.
     *
     * @param exam the exam entity to update
     * @param request the exam request DTO with new data
     */
    public void updateEntity(Exam exam, ExamRequest request) {
        if (exam == null || request == null) {
            return;
        }
        if (request.getTitle() != null) {
            exam.setTitle(request.getTitle());
        }
        if (request.getType() != null) {
            exam.setType(request.getType());
        }
        if (request.getDescription() != null) {
            exam.setDescription(request.getDescription());
        }
        if (request.getDurationMinutes() != null) {
            exam.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getTotalQuestions() != null) {
            exam.setTotalQuestions(request.getTotalQuestions());
        }
        if (request.getPassingScore() != null) {
            exam.setPassingScore(request.getPassingScore());
        }
        if (request.getStatus() != null) {
            exam.setStatus(request.getStatus());
        }
    }

    /**
     * Converts an ExamQuestion entity to ExamQuestionResponse DTO.
     *
     * @param examQuestion the exam question entity
     * @return the exam question response DTO, or null if input is null
     */
    public ExamQuestionResponse toExamQuestionResponse(ExamQuestion examQuestion) {
        if (examQuestion == null) {
            return null;
        }
        return ExamQuestionResponse.builder()
                .id(examQuestion.getId())
                .createdAt(examQuestion.getCreatedAt())
                .updatedAt(examQuestion.getUpdatedAt())
                .createdBy(examQuestion.getCreatedBy())
                .updatedBy(examQuestion.getUpdatedBy())
                .examId(examQuestion.getExam() != null ? examQuestion.getExam().getId() : null)
                .questionId(examQuestion.getQuestion() != null ? examQuestion.getQuestion().getId() : null)
                .orderIndex(examQuestion.getOrderIndex())
                .build();
    }

    /**
     * Maps exam questions collection from exam entity.
     * Returns empty list if examQuestions is null or empty.
     */
    private List<ExamQuestionResponse> mapExamQuestions(Exam exam) {
        if (exam.getExamQuestions() == null || exam.getExamQuestions().isEmpty()) {
            return Collections.emptyList();
        }
        return exam.getExamQuestions().stream()
                .filter(eq -> eq != null)
                .map(this::toExamQuestionResponse)
                .collect(Collectors.toList());
    }
}
