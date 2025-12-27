package com.certimaster.resultservice.service;

import com.certimaster.common_library.event.AnswerSubmittedEvent;
import com.certimaster.common_library.event.ExamSessionCreatedEvent;
import com.certimaster.common_library.event.ExamSessionStartedEvent;

/**
 * Service interface for exam result operations.
 */
public interface ExamResultService {

    /**
     * Create a new exam session from event.
     *
     * @param event the session started event
     * @return the created event with session ID
     */
    ExamSessionCreatedEvent createSession(ExamSessionStartedEvent event);

    /**
     * Save or update an answer from event.
     */
    void saveAnswer(AnswerSubmittedEvent event);
}
