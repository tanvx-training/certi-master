package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Detailed response DTO for certification data.
 * Extends CertificationResponse to include nested topic and exam information.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CertificationDetailResponse extends CertificationResponse {

    /**
     * List of topics associated with this certification.
     */
    private List<TopicResponse> topics;

    /**
     * List of exams associated with this certification.
     */
    private List<ExamResponse> exams;
}
