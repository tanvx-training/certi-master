package com.certimaster.result_service.service.impl;

import com.certimaster.common_library.event.ExamCompletedEvent;
import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.result_service.entity.ExamResult;
import com.certimaster.result_service.entity.QuestionResult;
import com.certimaster.result_service.entity.TopicPerformance;
import com.certimaster.result_service.repository.ExamResultRepository;
import com.certimaster.result_service.repository.QuestionResultRepository;
import com.certimaster.result_service.repository.TopicPerformanceRepository;
import com.certimaster.result_service.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ExamResultService.
 * Processes completed exam events and calculates detailed results.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final TopicPerformanceRepository topicPerformanceRepository;
    private final QuestionResultRepository questionResultRepository;

    @Override
    @Transactional
    public ExamResultResponse processCompletedExam(ExamCompletedEvent event) {
        log.debug("Processing completed exam for session {} user {} exam {}",
                event.getSessionId(), event.getUserId(), event.getExamId());

        try {
            // Check if result already exists (idempotency)
            var existingResult = examResultRepository.findBySessionId(event.getSessionId());
            if (existingResult.isPresent()) {
                log.warn("Result already exists for session {}, returning existing result",
                        event.getSessionId());
                return buildResponseFromExistingResult(existingResult.get());
            }

            // Calculate score percentage
            int totalQuestions = event.getTotalQuestions() != null ? event.getTotalQuestions() : 0;
            int correctCount = event.getCorrectCount() != null ? event.getCorrectCount() : 0;
            int wrongCount = event.getWrongCount() != null ? event.getWrongCount() : 0;
            int unanswered = totalQuestions - (correctCount + wrongCount);

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalQuestions > 0) {
                percentage = BigDecimal.valueOf(correctCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);
            }

            // Determine pass/fail status
            int passingScore = event.getPassingScore() != null ? event.getPassingScore() : 70;
            String passStatus = percentage.compareTo(BigDecimal.valueOf(passingScore)) >= 0 ? "PASSED" : "FAILED";

            // Calculate time taken
            Integer timeTakenSeconds = event.getTimeSpentSeconds();
            if (timeTakenSeconds == null && event.getStartTime() != null && event.getEndTime() != null) {
                timeTakenSeconds = (int) java.time.Duration.between(event.getStartTime(), event.getEndTime()).getSeconds();
            }

            // Create ExamResult entity
            ExamResult examResult = ExamResult.builder()
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .certificationId(event.getCertificationId())
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(event.getEndTime() != null ? event.getEndTime() : LocalDateTime.now())
                    .build();

            examResult = examResultRepository.save(examResult);
            log.info("Created ExamResult {} for session {}", examResult.getId(), event.getSessionId());

            // Calculate and create TopicPerformance records
            List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList =
                    calculateAndSaveTopicPerformances(examResult, event.getAnswers());

            // Create QuestionResult records
            List<ExamResultResponse.QuestionResultData> questionResultDataList =
                    createAndSaveQuestionResults(examResult, event.getAnswers());

            // Build and return response
            return ExamResultResponse.builder()
                    .resultId(examResult.getId())
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .examTitle(event.getExamTitle())
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .passingScore(passingScore)
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(examResult.getCompletedAt())
                    .topicPerformances(topicPerformanceDataList)
                    .questionResults(questionResultDataList)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process completed exam for session {}", event.getSessionId(), e);
            return ExamResultResponse.builder()
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .success(false)
                    .errorMessage("Failed to calculate results: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Calculate topic-wise performance and save TopicPerformance records.
     */
    private List<ExamResultResponse.TopicPerformanceData> calculateAndSaveTopicPerformances(
            ExamResult examResult, List<ExamCompletedEvent.UserAnswerData> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        // Group answers by topic
        Map<Long, List<ExamCompletedEvent.UserAnswerData>> answersByTopic = answers.stream()
                .filter(a -> a.getTopicId() != null)
                .collect(Collectors.groupingBy(ExamCompletedEvent.UserAnswerData::getTopicId));

        List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList = new ArrayList<>();

        for (Map.Entry<Long, List<ExamCompletedEvent.UserAnswerData>> entry : answersByTopic.entrySet()) {
            Long topicId = entry.getKey();
            List<ExamCompletedEvent.UserAnswerData> topicAnswers = entry.getValue();

            int totalInTopic = topicAnswers.size();
            int correctInTopic = (int) topicAnswers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .count();

            BigDecimal topicPercentage = BigDecimal.ZERO;
            if (totalInTopic > 0) {
                topicPercentage = BigDecimal.valueOf(correctInTopic)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalInTopic), 2, RoundingMode.HALF_UP);
            }

            // Get topic name from first answer
            String topicName = topicAnswers.get(0).getTopicName();

            // Create and save TopicPerformance entity
            TopicPerformance topicPerformance = TopicPerformance.builder()
                    .examResult(examResult)
                    .topicId(topicId)
                    .topicName(topicName)
                    .totalQuestions(totalInTopic)
                    .correctAnswers(correctInTopic)
                    .percentage(topicPercentage)
                    .build();

            topicPerformanceRepository.save(topicPerformance);

            // Add to response list
            topicPerformanceDataList.add(ExamResultResponse.TopicPerformanceData.builder()
                    .topicId(topicId)
                    .topicName(topicName)
                    .totalQuestions(totalInTopic)
                    .correctAnswers(correctInTopic)
                    .percentage(topicPercentage)
                    .build());
        }

        log.debug("Created {} TopicPerformance records for result {}", topicPerformanceDataList.size(), examResult.getId());
        return topicPerformanceDataList;
    }

    /**
     * Create QuestionResult records for each answer.
     */
    private List<ExamResultResponse.QuestionResultData> createAndSaveQuestionResults(
            ExamResult examResult, List<ExamCompletedEvent.UserAnswerData> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ExamResultResponse.QuestionResultData> questionResultDataList = new ArrayList<>();

        for (ExamCompletedEvent.UserAnswerData answer : answers) {
            // Create and save QuestionResult entity
            QuestionResult questionResult = QuestionResult.builder()
                    .examResult(examResult)
                    .questionId(answer.getQuestionId())
                    .userAnswerIds(answer.getSelectedOptionIds())
                    .correctAnswerIds(answer.getCorrectOptionIds())
                    .isCorrect(answer.getIsCorrect())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .build();

            questionResultRepository.save(questionResult);

            // Add to response list - include explanation and reference for incorrect answers
            ExamResultResponse.QuestionResultData.QuestionResultDataBuilder dataBuilder =
                    ExamResultResponse.QuestionResultData.builder()
                            .questionId(answer.getQuestionId())
                            .questionText(answer.getQuestionText())
                            .userAnswerIds(answer.getSelectedOptionIds())
                            .correctAnswerIds(answer.getCorrectOptionIds())
                            .isCorrect(answer.getIsCorrect())
                            .timeSpentSeconds(answer.getTimeSpentSeconds());

            // Include explanation and reference for all questions (especially useful for incorrect ones)
            dataBuilder.explanation(answer.getExplanation());
            dataBuilder.reference(answer.getReference());

            questionResultDataList.add(dataBuilder.build());
        }

        log.debug("Created {} QuestionResult records for result {}", questionResultDataList.size(), examResult.getId());
        return questionResultDataList;
    }

    /**
     * Build response from existing ExamResult (for idempotency).
     */
    private ExamResultResponse buildResponseFromExistingResult(ExamResult examResult) {
        // Load topic performances
        List<TopicPerformance> topicPerformances = topicPerformanceRepository.findByExamResultId(examResult.getId());
        List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList = topicPerformances.stream()
                .map(tp -> ExamResultResponse.TopicPerformanceData.builder()
                        .topicId(tp.getTopicId())
                        .topicName(tp.getTopicName())
                        .totalQuestions(tp.getTotalQuestions())
                        .correctAnswers(tp.getCorrectAnswers())
                        .percentage(tp.getPercentage())
                        .build())
                .collect(Collectors.toList());

        // Load question results
        List<QuestionResult> questionResults = questionResultRepository.findByExamResultId(examResult.getId());
        List<ExamResultResponse.QuestionResultData> questionResultDataList = questionResults.stream()
                .map(qr -> ExamResultResponse.QuestionResultData.builder()
                        .questionId(qr.getQuestionId())
                        .userAnswerIds(qr.getUserAnswerIds())
                        .correctAnswerIds(qr.getCorrectAnswerIds())
                        .isCorrect(qr.getIsCorrect())
                        .timeSpentSeconds(qr.getTimeSpentSeconds())
                        .build())
                .collect(Collectors.toList());

        return ExamResultResponse.builder()
                .resultId(examResult.getId())
                .sessionId(examResult.getSessionId())
                .userId(examResult.getUserId())
                .examId(examResult.getExamId())
                .totalQuestions(examResult.getTotalQuestions())
                .correctAnswers(examResult.getCorrectAnswers())
                .wrongAnswers(examResult.getWrongAnswers())
                .unanswered(examResult.getUnanswered())
                .score(examResult.getScore())
                .percentage(examResult.getPercentage())
                .passStatus(examResult.getPassStatus())
                .timeTakenSeconds(examResult.getTimeTakenSeconds())
                .completedAt(examResult.getCompletedAt())
                .topicPerformances(topicPerformanceDataList)
                .questionResults(questionResultDataList)
                .success(true)
                .build();
    }
}
