package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.CertificationRequest;
import com.certimaster.exam_service.dto.response.CertificationDetailResponse;
import com.certimaster.exam_service.dto.response.CertificationResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;
import com.certimaster.exam_service.dto.response.TopicResponse;
import com.certimaster.exam_service.entity.Certification;
import com.certimaster.exam_service.entity.Exam;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Certification entities and DTOs.
 * Handles transformation of certification data between domain and API layers.
 * Provides null-safe mapping operations with collection handling.
 */
@Component
public class CertificationMapper {

    private final TopicMapper topicMapper;

    public CertificationMapper(TopicMapper topicMapper) {
        this.topicMapper = topicMapper;
    }

    /**
     * Converts a Certification entity to a CertificationResponse DTO.
     *
     * @param certification the certification entity
     * @return the certification response DTO, or null if input is null
     */
    public CertificationResponse toResponse(Certification certification) {
        if (certification == null) {
            return null;
        }
        return CertificationResponse.builder()
                .id(certification.getId())
                .createdAt(certification.getCreatedAt())
                .updatedAt(certification.getUpdatedAt())
                .createdBy(certification.getCreatedBy())
                .updatedBy(certification.getUpdatedBy())
                .name(certification.getName())
                .code(certification.getCode())
                .provider(certification.getProvider())
                .description(certification.getDescription())
                .level(certification.getLevel())
                .durationMinutes(certification.getDurationMinutes())
                .passingScore(certification.getPassingScore())
                .totalQuestions(certification.getTotalQuestions())
                .price(certification.getPrice())
                .status(certification.getStatus())
                .build();
    }

    /**
     * Converts a Certification entity to a CertificationDetailResponse DTO.
     * Includes nested topic and exam information.
     *
     * @param certification the certification entity
     * @return the detailed certification response DTO, or null if input is null
     */
    public CertificationDetailResponse toDetailResponse(Certification certification) {
        if (certification == null) {
            return null;
        }
        
        List<TopicResponse> topics = mapTopics(certification);
        List<ExamResponse> exams = mapExams(certification);
        
        return CertificationDetailResponse.builder()
                .id(certification.getId())
                .createdAt(certification.getCreatedAt())
                .updatedAt(certification.getUpdatedAt())
                .createdBy(certification.getCreatedBy())
                .updatedBy(certification.getUpdatedBy())
                .name(certification.getName())
                .code(certification.getCode())
                .provider(certification.getProvider())
                .description(certification.getDescription())
                .level(certification.getLevel())
                .durationMinutes(certification.getDurationMinutes())
                .passingScore(certification.getPassingScore())
                .totalQuestions(certification.getTotalQuestions())
                .price(certification.getPrice())
                .status(certification.getStatus())
                .topics(topics)
                .exams(exams)
                .build();
    }

    /**
     * Converts a CertificationRequest DTO to a Certification entity.
     *
     * @param request the certification request DTO
     * @return the certification entity, or null if input is null
     */
    public Certification toEntity(CertificationRequest request) {
        if (request == null) {
            return null;
        }
        return Certification.builder()
                .name(request.getName())
                .code(request.getCode())
                .provider(request.getProvider())
                .description(request.getDescription())
                .level(request.getLevel())
                .durationMinutes(request.getDurationMinutes())
                .passingScore(request.getPassingScore())
                .totalQuestions(request.getTotalQuestions())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();
    }

    /**
     * Updates an existing Certification entity with data from a CertificationRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param certification the target certification entity to update
     * @param request the certification request DTO with updated data
     */
    public void updateEntity(Certification certification, CertificationRequest request) {
        if (certification == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            certification.setName(request.getName());
        }
        if (request.getCode() != null) {
            certification.setCode(request.getCode());
        }
        if (request.getProvider() != null) {
            certification.setProvider(request.getProvider());
        }
        if (request.getDescription() != null) {
            certification.setDescription(request.getDescription());
        }
        if (request.getLevel() != null) {
            certification.setLevel(request.getLevel());
        }
        if (request.getDurationMinutes() != null) {
            certification.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getPassingScore() != null) {
            certification.setPassingScore(request.getPassingScore());
        }
        if (request.getTotalQuestions() != null) {
            certification.setTotalQuestions(request.getTotalQuestions());
        }
        if (request.getPrice() != null) {
            certification.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            certification.setStatus(request.getStatus());
        }
    }

    /**
     * Maps topics collection from certification entity.
     * Returns empty list if topics is null or empty.
     */
    private List<TopicResponse> mapTopics(Certification certification) {
        if (certification.getTopics() == null || certification.getTopics().isEmpty()) {
            return Collections.emptyList();
        }
        return certification.getTopics().stream()
                .filter(topic -> topic != null)
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps exams collection from certification entity.
     * Returns empty list if exams is null or empty.
     * Note: Uses inline mapping to avoid circular dependency with ExamMapper.
     */
    private List<ExamResponse> mapExams(Certification certification) {
        if (certification.getExams() == null || certification.getExams().isEmpty()) {
            return Collections.emptyList();
        }
        return certification.getExams().stream()
                .filter(exam -> exam != null)
                .map(this::mapExamToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a single Exam entity to ExamResponse.
     * Used internally to avoid circular dependency with ExamMapper.
     */
    private ExamResponse mapExamToResponse(Exam exam) {
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
}
