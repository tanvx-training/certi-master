package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.CertificationMapper;
import com.certimaster.exam_service.dto.request.CertificationRequest;
import com.certimaster.exam_service.dto.request.CertificationSearchRequest;
import com.certimaster.exam_service.dto.response.CertificationDetailResponse;
import com.certimaster.exam_service.dto.response.CertificationResponse;
import com.certimaster.exam_service.entity.Certification;
import com.certimaster.exam_service.repository.CertificationRepository;
import com.certimaster.exam_service.service.CertificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of CertificationService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationServiceImpl implements CertificationService {

    private static final String CERTIFICATION = "Certification";
    private static final String DELETED_STATUS = "DELETED";

    private final CertificationRepository certificationRepository;
    private final CertificationMapper certificationMapper;

    @Override
    public PageDto<CertificationResponse> search(CertificationSearchRequest request) {
        log.debug("Searching certifications with criteria: {}", request);

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Certification> page = certificationRepository.search(
                request.getKeyword(),
                request.getProvider(),
                request.getLevel(),
                request.getStatus(),
                pageable
        );

        Page<CertificationResponse> responsePage = page.map(certificationMapper::toResponse);
        return PageDto.of(responsePage);
    }


    @Override
    public CertificationDetailResponse getById(Long id) {
        log.debug("Getting certification by id: {}", id);

        Certification certification = findActiveOrThrow(id);
        return certificationMapper.toDetailResponse(certification);
    }

    @Override
    @Transactional
    public CertificationResponse create(CertificationRequest request) {
        log.debug("Creating certification with code: {}", request.getCode());

        validateCodeUnique(request.getCode(), null);

        Certification certification = certificationMapper.toEntity(request);
        if (certification.getStatus() == null) {
            certification.setStatus("DRAFT");
        }

        Certification saved = certificationRepository.save(certification);
        log.info("Created certification with id: {}", saved.getId());

        return certificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CertificationResponse update(Long id, CertificationRequest request) {
        log.debug("Updating certification with id: {}", id);

        Certification certification = findActiveOrThrow(id);
        validateCodeUnique(request.getCode(), id);

        certificationMapper.updateEntity(certification, request);
        Certification saved = certificationRepository.save(certification);
        log.info("Updated certification with id: {}", saved.getId());

        return certificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Soft deleting certification with id: {}", id);

        Certification certification = findActiveOrThrow(id);
        certification.setStatus(DELETED_STATUS);
        certificationRepository.save(certification);

        log.info("Soft deleted certification with id: {}", id);
    }

    private Certification findActiveOrThrow(Long id) {
        return certificationRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, id));
    }

    private void validateCodeUnique(String code, Long excludeId) {
        boolean exists = excludeId == null
                ? certificationRepository.existsByCodeIgnoreCase(code)
                : certificationRepository.existsByCodeIgnoreCaseAndIdNot(code, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(CERTIFICATION, code);
        }
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
