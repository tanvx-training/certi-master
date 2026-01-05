package com.certimaster.blog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for reaction data.
 * 
 * @see Requirements 6.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {

    /**
     * ID of the reaction.
     */
    private Long id;

    /**
     * ID of the user who reacted.
     */
    private Long userId;

    /**
     * Type of reaction (LIKE, LOVE, HELPFUL).
     */
    private String reactionType;

    /**
     * Timestamp when the reaction was created.
     */
    private LocalDateTime createdAt;
}
