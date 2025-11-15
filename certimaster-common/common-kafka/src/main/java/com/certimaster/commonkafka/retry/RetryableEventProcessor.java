package com.certimaster.commonkafka.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * Retryable event processor with exponential backoff
 */
@Slf4j
@Component
public class RetryableEventProcessor {

    /**
     * Process event with retry
     */
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,      // Initial delay 1s
                    multiplier = 2,    // Double each retry
                    maxDelay = 10000   // Max 10s
            ),
            retryFor = {Exception.class}
    )
    public void processWithRetry(Runnable action) {
        log.info("Processing event with retry capability");
        action.run();
    }

    /**
     * Recover method called after all retries fail
     */
    @Recover
    public void recover(Exception e) {
        log.error("All retry attempts failed. Recovery triggered.", e);
        // Implement recovery logic (e.g., send to DLT, alert, etc.)
    }
}
