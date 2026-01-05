package com.certimaster.blog_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding or updating a reaction.
 * 
 * @see Requirements 6.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequest {

    /**
     * Type of reaction (LIKE, LOVE, HELPFUL).
     */
    @NotNull(message = "Reaction type is required")
    @Pattern(regexp = "^(LIKE|LOVE|HELPFUL)$", 
             message = "Reaction type must be one of: LIKE, LOVE, HELPFUL")
    private String reactionType;
}
