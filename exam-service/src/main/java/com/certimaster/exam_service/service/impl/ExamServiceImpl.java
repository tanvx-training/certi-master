package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.ExamMapper;
import com.certimaster.exam_service.dto.request.ExamRequest;
import com.certimaster.exam_service.dto.request.ExamSearchRequest;
import com.certimaster.exam_service.dto.response.ExamDetailResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;
import com.certimaster.exam_service.entity.Certification;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.repository.CertificationRepository;
import com.certimaster.exam_service.repository.ExamRepository;
import com.certimaster.exam_service.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ExamService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamServiceImpl implements ExamService {

    private static final String EXAM = "Exam";
    private static final String CERTIFICATION = "Certification";
    private static final String DELETED_STATUS = "DELETED";

    private final ExamRepository examRepository;
    private final CertificationRepository certificationRepository;
    private final ExamMapper examMapper;

    @Override
    public PageDto<ExamResponse> search(ExamSearchRequest request) {
        log.debug("Searching exams with criteria: {}", request);

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Exam> page = examRepository.search(
                request.getKeyword(),
                request.getCertificationId(),
                request.getType(),
                request.getStatus(),
                pageable
        );

        Page<ExamResponse> responsePage = page.map(examMapper::toResponse);
        return PageDto.of(responsePage);
    }


    @Override
    public ExamDetailResponse getById(Long id) {
        log.debug("Getting exam by id: {}", id);

        Exam exam = findActiveOrThrow(id);
        return examMapper.toDetailResponse(exam);
    }

    @Override
    public List<ExamResponse> getByCertificationId(Long certificationId) {
        log.debug("Getting exams by certification id: {}", certificationId);
        boolean exists = certificationRepository.existsById(certificationId);
        if (!exists) {
            throw ResourceNotFoundException.byId(CERTIFICATION, certificationId);
        }

        List<Exam> exams = examRepository.findByCertificationId(certificationId);
        return exams.stream()
                .map(examMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ExamResponse create(ExamRequest request) {
        log.debug("Creating exam with title: {}", request.getTitle());

        Certification certification = findCertificationOrThrow(request.getCertificationId());
        validateTitleUnique(request.getTitle(), request.getCertificationId(), null);

        Exam exam = examMapper.toEntity(request);
        exam.setCertification(certification);
        if (exam.getStatus() == null) {
            exam.setStatus("DRAFT");
        }

        Exam saved = examRepository.save(exam);
        log.info("Created exam with id: {}", saved.getId());

        return examMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ExamResponse update(Long id, ExamRequest request) {
        log.debug("Updating exam with id: {}", id);

        Exam exam = findActiveOrThrow(id);
        
        // Update certification if changed
        if (!exam.getCertification().getId().equals(request.getCertificationId())) {
            Certification certification = findCertificationOrThrow(request.getCertificationId());
            exam.setCertification(certification);
        }

        validateTitleUnique(request.getTitle(), request.getCertificationId(), id);
        examMapper.updateEntity(exam, request);

        Exam saved = examRepository.save(exam);
        log.info("Updated exam with id: {}", saved.getId());

        return examMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Soft deleting exam with id: {}", id);

        Exam exam = findActiveOrThrow(id);
        exam.setStatus(DELETED_STATUS);
        examRepository.save(exam);

        log.info("Soft deleted exam with id: {}", id);
    }

    private Exam findActiveOrThrow(Long id) {
        return examRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, id));
    }

    private Certification findCertificationOrThrow(Long certificationId) {
        return certificationRepository.findActiveById(certificationId)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, certificationId));
    }

    private void validateTitleUnique(String title, Long certificationId, Long excludeId) {
        boolean exists = excludeId == null
                ? examRepository.existsByTitleIgnoreCaseAndCertificationId(title, certificationId)
                : examRepository.existsByTitleIgnoreCaseAndCertificationIdAndIdNot(title, certificationId, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(EXAM, title);
        }
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
