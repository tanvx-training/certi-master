package com.certimaster.blog_service.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for calculating reading time based on content length.
 * 
 * Requirements:
 * - 1.5: Calculate and store reading_time_minutes based on content length
 * 
 * Uses average reading speed of 200 words per minute.
 */
@Component
public class ReadingTimeCalculator {

    /**
     * Average reading speed in words per minute.
     */
    private static final int WORDS_PER_MINUTE = 200;

    /**
     * Calculate the estimated reading time in minutes for the given content.
     * 
     * @param content the text content to calculate reading time for
     * @return the estimated reading time in minutes (minimum 1 minute)
     */
    public int calculateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        int wordCount = countWords(content);
        
        if (wordCount == 0) {
            return 0;
        }

        // Calculate reading time: ceil(wordCount / 200)
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

    /**
     * Count the number of words in the given content.
     * Words are separated by whitespace.
     * 
     * @param content the text content to count words in
     * @return the number of words
     */
    public int countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        // Split by whitespace and count non-empty tokens
        String[] words = content.trim().split("\\s+");
        
        int count = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                count++;
            }
        }
        
        return count;
    }
}
