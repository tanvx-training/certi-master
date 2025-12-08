package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.TagMapper;
import com.certimaster.exam_service.dto.request.TagRequest;
import com.certimaster.exam_service.dto.request.TagSearchRequest;
import com.certimaster.exam_service.dto.response.TagResponse;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionTag;
import com.certimaster.exam_service.entity.Tag;
import com.certimaster.exam_service.repository.QuestionRepository;
import com.certimaster.exam_service.repository.QuestionTagRepository;
import com.certimaster.exam_service.repository.TagRepository;
import com.certimaster.exam_service.service.TagService;
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
 * Implementation of TagService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private static final String TAG = "Tag";
    private static final String QUESTION = "Question";

    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final QuestionRepository questionRepository;
    private final TagMapper tagMapper;

    private static final String DELETED_STATUS = "DELETED";

    @Override
    public PageDto<TagResponse> search(TagSearchRequest request) {
        log.debug("Searching tags with criteria: {}", request);

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Tag> page = tagRepository.search(request.getKeyword(), request.getStatus(), pageable);
        Page<TagResponse> responsePage = page.map(tagMapper::toResponse);
        return PageDto.of(responsePage);
    }

    @Override
    public List<TagResponse> getAll() {
        log.debug("Getting all tags");

        return tagRepository.findAllActive().stream()
                .map(tagMapper::toResponse)
                .toList();
    }


    @Override
    public TagResponse getById(Long id) {
        log.debug("Getting tag by id: {}", id);

        Tag tag = findOrThrow(id);
        return tagMapper.toResponse(tag);
    }

    @Override
    public List<TagResponse> getByQuestionId(Long questionId) {
        log.debug("Getting tags by question id: {}", questionId);

        List<Tag> tags = questionTagRepository.findTagsByQuestionId(questionId);
        return tags.stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TagResponse create(TagRequest request) {
        log.debug("Creating tag with name: {}", request.getName());

        validateNameUnique(request.getName(), null);

        Tag tag = tagMapper.toEntity(request);
        if (tag.getStatus() == null) {
            tag.setStatus("ACTIVE");
        }
        Tag saved = tagRepository.save(tag);

        log.info("Created tag with id: {}", saved.getId());
        return tagMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TagResponse update(Long id, TagRequest request) {
        log.debug("Updating tag with id: {}", id);

        Tag tag = findOrThrow(id);
        validateNameUnique(request.getName(), id);

        tagMapper.updateEntity(tag, request);
        Tag saved = tagRepository.save(tag);

        log.info("Updated tag with id: {}", saved.getId());
        return tagMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Soft deleting tag with id: {}", id);

        Tag tag = findActiveOrThrow(id);
        tag.setStatus(DELETED_STATUS);
        tagRepository.save(tag);

        log.info("Soft deleted tag with id: {}", id);
    }

    @Override
    @Transactional
    public void addTagsToQuestion(Long questionId, List<Long> tagIds) {
        log.debug("Adding tags {} to question {}", tagIds, questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, questionId));

        List<Tag> tags = tagRepository.findByIdIn(tagIds);
        if (tags.size() != tagIds.size()) {
            throw BusinessException.invalidInput("Some tags were not found");
        }

        for (Tag tag : tags) {
            if (!questionTagRepository.existsByQuestionIdAndTagId(questionId, tag.getId())) {
                QuestionTag questionTag = QuestionTag.builder()
                        .question(question)
                        .tag(tag)
                        .build();
                questionTagRepository.save(questionTag);
            }
        }

        log.info("Added {} tags to question {}", tags.size(), questionId);
    }

    @Override
    @Transactional
    public void removeTagsFromQuestion(Long questionId, List<Long> tagIds) {
        log.debug("Removing tags {} from question {}", tagIds, questionId);

        List<QuestionTag> questionTags = questionTagRepository.findByQuestionId(questionId);
        List<QuestionTag> toRemove = questionTags.stream()
                .filter(qt -> tagIds.contains(qt.getTag().getId()))
                .toList();

        questionTagRepository.deleteAll(toRemove);
        log.info("Removed {} tags from question {}", toRemove.size(), questionId);
    }

    private Tag findOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(TAG, id));
    }

    private Tag findActiveOrThrow(Long id) {
        return tagRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(TAG, id));
    }

    private void validateNameUnique(String name, Long excludeId) {
        boolean exists = excludeId == null
                ? tagRepository.existsByNameIgnoreCase(name)
                : tagRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(TAG, name);
        }
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
