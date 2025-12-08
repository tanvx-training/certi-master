package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.TopicMapper;
import com.certimaster.exam_service.dto.request.TopicRequest;
import com.certimaster.exam_service.dto.request.TopicSearchRequest;
import com.certimaster.exam_service.dto.response.TopicResponse;
import com.certimaster.exam_service.entity.Certification;
import com.certimaster.exam_service.entity.Topic;
import com.certimaster.exam_service.repository.CertificationRepository;
import com.certimaster.exam_service.repository.TopicRepository;
import com.certimaster.exam_service.service.TopicService;
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
 * Implementation of TopicService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {

    private static final String TOPIC = "Topic";
    private static final String CERTIFICATION = "Certification";

    private final TopicRepository topicRepository;
    private final CertificationRepository certificationRepository;
    private final TopicMapper topicMapper;

    @Override
    public PageDto<TopicResponse> search(TopicSearchRequest request) {
        log.debug("Searching topics with criteria: {}", request);

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Topic> page = topicRepository.search(
                request.getKeyword(),
                request.getCertificationId(),
                pageable
        );

        Page<TopicResponse> responsePage = page.map(topicMapper::toResponse);
        return PageDto.of(responsePage);
    }


    @Override
    public TopicResponse getById(Long id) {
        log.debug("Getting topic by id: {}", id);

        Topic topic = findOrThrow(id);
        return topicMapper.toResponse(topic);
    }

    @Override
    public List<TopicResponse> getByCertificationId(Long certificationId) {
        log.debug("Getting topics by certification id: {}", certificationId);

        List<Topic> topics = topicRepository.findByCertificationIdOrderByOrderIndexAsc(certificationId);
        return topics.stream()
                .map(topicMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TopicResponse create(TopicRequest request) {
        log.debug("Creating topic with name: {}", request.getName());

        Certification certification = findCertificationOrThrow(request.getCertificationId());
        validateNameUnique(request.getName(), request.getCertificationId(), null);
        
        if (request.getCode() != null && !request.getCode().isBlank()) {
            validateCodeUnique(request.getCode(), request.getCertificationId(), null);
        }

        Topic topic = topicMapper.toEntity(request);
        topic.setCertification(certification);

        Topic saved = topicRepository.save(topic);
        log.info("Created topic with id: {}", saved.getId());

        return topicMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TopicResponse update(Long id, TopicRequest request) {
        log.debug("Updating topic with id: {}", id);

        Topic topic = findOrThrow(id);

        // Update certification if changed
        if (!topic.getCertification().getId().equals(request.getCertificationId())) {
            Certification certification = findCertificationOrThrow(request.getCertificationId());
            topic.setCertification(certification);
        }

        validateNameUnique(request.getName(), request.getCertificationId(), id);
        
        if (request.getCode() != null && !request.getCode().isBlank()) {
            validateCodeUnique(request.getCode(), request.getCertificationId(), id);
        }

        topicMapper.updateEntity(topic, request);

        Topic saved = topicRepository.save(topic);
        log.info("Updated topic with id: {}", saved.getId());

        return topicMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting topic with id: {}", id);

        Topic topic = findOrThrow(id);
        topicRepository.delete(topic);

        log.info("Deleted topic with id: {}", id);
    }

    private Topic findOrThrow(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(TOPIC, id));
    }

    private Certification findCertificationOrThrow(Long certificationId) {
        return certificationRepository.findActiveById(certificationId)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, certificationId));
    }

    private void validateNameUnique(String name, Long certificationId, Long excludeId) {
        boolean exists = excludeId == null
                ? topicRepository.existsByNameIgnoreCaseAndCertificationId(name, certificationId)
                : topicRepository.existsByNameIgnoreCaseAndCertificationIdAndIdNot(name, certificationId, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(TOPIC, name);
        }
    }

    private void validateCodeUnique(String code, Long certificationId, Long excludeId) {
        boolean exists = excludeId == null
                ? topicRepository.existsByCodeIgnoreCaseAndCertificationId(code, certificationId)
                : topicRepository.existsByCodeIgnoreCaseAndCertificationIdAndIdNot(code, certificationId, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(TOPIC, code);
        }
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
