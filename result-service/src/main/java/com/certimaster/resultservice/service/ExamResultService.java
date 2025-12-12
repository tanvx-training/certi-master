package com.certimaster.resultservice.service;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;

/**
 * Service interface for exam result operations.
 */
public interface ExamResultService {

    /**
     * Create a new exam session from event.
     */
    void createSession(ExamSessionStartedEvent event);

    /**
     * Save or update an answer from event.
     */
    void saveAnswer(AnswerSubmittedEvent event);
}
