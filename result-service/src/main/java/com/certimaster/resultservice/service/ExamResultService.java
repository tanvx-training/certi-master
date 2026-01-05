package com.certimaster.resultservice.service;

import com.certimaster.common_library.event.ExamCompletedEvent;
import com.certimaster.common_library.event.ExamResultResponse;

/**
 * Service interface for exam result operations.
 */
public interface ExamResultService {

    /**
     * Process a completed exam event and calculate results.
     * Creates ExamResult, TopicPerformance, and QuestionResult records.
     *
     * @param event the exam completed event containing session data and answers
     * @return the exam result response with calculated scores and performance data
     */
    ExamResultResponse processCompletedExam(ExamCompletedEvent event);
}
