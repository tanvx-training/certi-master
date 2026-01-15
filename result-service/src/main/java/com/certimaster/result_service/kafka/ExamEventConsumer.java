package com.certimaster.result_service.kafka;

import com.certimaster.common_library.event.ExamCompletedEvent;
import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.common_library.event.KafkaTopics;
import com.certimaster.result_service.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for exam-related events.
 * Processes ExamCompletedEvent and returns ExamResultResponse via reply pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExamEventConsumer {

    private final ExamResultService examResultService;

    /**
     * Handle exam completed event and send reply with calculated results.
     * Uses Request-Reply pattern.
     *
     * @param event the exam completed event containing session data and answers
     * @return the exam result response with calculated scores and performance data
     */
    @KafkaListener(
            topics = KafkaTopics.EXAM_COMPLETED,
            containerFactory = "examCompletedKafkaListenerContainerFactory"
    )
    @SendTo
    public ExamResultResponse handleExamCompleted(ExamCompletedEvent event) {
        log.info("Received ExamCompletedEvent for session {} user {} exam {}",
                event.getSessionId(), event.getUserId(), event.getExamId());

        try {
            ExamResultResponse response = examResultService.processCompletedExam(event);

            if (response.isSuccess()) {
                log.info("Successfully processed exam completion for session {} - score: {}%, status: {}",
                        event.getSessionId(), response.getPercentage(), response.getPassStatus());
            } else {
                log.error("Failed to process exam completion for session {}: {}",
                        event.getSessionId(), response.getErrorMessage());
            }

            return response;
        } catch (Exception e) {
            log.error("Error processing ExamCompletedEvent for session {}", event.getSessionId(), e);
            return ExamResultResponse.builder()
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .success(false)
                    .errorMessage("Failed to process exam completion: " + e.getMessage())
                    .build();
        }
    }
}
