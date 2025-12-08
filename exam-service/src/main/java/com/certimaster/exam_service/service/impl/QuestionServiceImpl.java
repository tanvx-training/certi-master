package com.certimaster.exam_service.service.impl;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.exam_service.dto.mapper.QuestionMapper;
import com.certimaster.exam_service.dto.request.QuestionOptionRequest;
import com.certimaster.exam_service.dto.request.QuestionSearchRequest;
import com.certimaster.exam_service.dto.request.QuestionWithOptionsRequest;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import com.certimaster.exam_service.entity.Topic;
import com.certimaster.exam_service.repository.QuestionOptionRepository;
import com.certimaster.exam_service.repository.QuestionRepository;
import com.certimaster.exam_service.repository.TopicRepository;
import com.certimaster.exam_service.service.QuestionService;
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
 * Implementation of QuestionService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private static final String QUESTION = "Question";
    private static final String TOPIC = "Topic";

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final TopicRepository topicRepository;
    private final QuestionMapper questionMapper;

    @Override
    public PageDto<QuestionResponse> search(QuestionSearchRequest request) {
        log.debug("Searching questions with criteria: {}", request);

        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Question> page = questionRepository.search(
                request.getKeyword(),
                request.getTopicId(),
                request.getType(),
                request.getDifficulty(),
                pageable
        );

        Page<QuestionResponse> responsePage = page.map(questionMapper::toResponse);
        return PageDto.of(responsePage);
    }

    @Override
    public QuestionResponse getById(Long id) {
        log.debug("Getting question by id: {}", id);

        Question question = findOrThrow(id);
        return questionMapper.toResponse(question);
    }

    @Override
    public QuestionResponse getByIdForExam(Long id) {
        log.debug("Getting question by id for exam: {}", id);

        Question question = findOrThrow(id);
        return questionMapper.toResponseWithoutCorrect(question);
    }


    @Override
    public List<QuestionResponse> getByTopicId(Long topicId) {
        log.debug("Getting questions by topic id: {}", topicId);

        List<Question> questions = questionRepository.findByTopicId(topicId);
        return questions.stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse create(QuestionWithOptionsRequest request) {
        log.debug("Creating question for topic: {}", request.getTopicId());

        Topic topic = findTopicOrThrow(request.getTopicId());
        validateOptions(request);

        Question question = questionMapper.toEntity(request);
        question.setTopic(topic);

        Question saved = questionRepository.save(question);
        createOptions(saved, request.getOptions());

        log.info("Created question with id: {}", saved.getId());
        return questionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public QuestionResponse update(Long id, QuestionWithOptionsRequest request) {
        log.debug("Updating question with id: {}", id);

        Question question = findOrThrow(id);
        validateOptions(request);

        // Update topic if changed
        if (!question.getTopic().getId().equals(request.getTopicId())) {
            Topic topic = findTopicOrThrow(request.getTopicId());
            question.setTopic(topic);
        }

        questionMapper.updateEntity(question, request);

        // Delete old options and create new ones
        questionOptionRepository.deleteByQuestionId(id);
        question.getQuestionOptions().clear();
        createOptions(question, request.getOptions());

        Question saved = questionRepository.save(question);
        log.info("Updated question with id: {}", saved.getId());

        return questionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting question with id: {}", id);

        Question question = findOrThrow(id);
        questionRepository.delete(question);

        log.info("Deleted question with id: {}", id);
    }

    private Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, id));
    }

    private Topic findTopicOrThrow(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> ResourceNotFoundException.byId(TOPIC, topicId));
    }

    private void validateOptions(QuestionWithOptionsRequest request) {
        List<QuestionOptionRequest> options = request.getOptions();

        // Validate at least one correct answer
        boolean hasCorrect = options.stream().anyMatch(QuestionOptionRequest::getIsCorrect);
        if (!hasCorrect) {
            throw BusinessException.invalidInput("At least one option must be marked as correct");
        }

        // Validate based on question type
        long correctCount = options.stream().filter(QuestionOptionRequest::getIsCorrect).count();

        if ("SINGLE_CHOICE".equals(request.getType()) && correctCount != 1) {
            throw BusinessException.invalidInput("Single choice question must have exactly one correct answer");
        }

        if ("TRUE_FALSE".equals(request.getType())) {
            if (options.size() != 2) {
                throw BusinessException.invalidInput("True/False question must have exactly 2 options");
            }
            if (correctCount != 1) {
                throw BusinessException.invalidInput("True/False question must have exactly one correct answer");
            }
        }
    }

    private void createOptions(Question question, List<QuestionOptionRequest> optionRequests) {
        for (QuestionOptionRequest optionRequest : optionRequests) {
            QuestionOption option = QuestionOption.builder()
                    .question(question)
                    .content(optionRequest.getContent())
                    .isCorrect(optionRequest.getIsCorrect())
                    .orderIndex(optionRequest.getOrderIndex())
                    .build();
            question.getQuestionOptions().add(option);
        }
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
