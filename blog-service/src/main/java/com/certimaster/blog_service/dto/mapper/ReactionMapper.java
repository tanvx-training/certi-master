package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.entity.CommentReaction;
import com.certimaster.blog_service.entity.PostReaction;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between Reaction entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class ReactionMapper {

    /**
     * Converts a PostReaction entity to ReactionResponse DTO.
     *
     * @param reaction the post reaction entity
     * @return the reaction response DTO, or null if input is null
     */
    public ReactionResponse toResponse(PostReaction reaction) {
        if (reaction == null) {
            return null;
        }
        return ReactionResponse.builder()
                .id(reaction.getId())
                .userId(reaction.getUserId())
                .reactionType(reaction.getReactionType() != null 
                        ? reaction.getReactionType().name() 
                        : null)
                .createdAt(reaction.getCreatedAt())
                .build();
    }

    /**
     * Converts a CommentReaction entity to ReactionResponse DTO.
     * Note: CommentReaction doesn't have reactionType, defaults to LIKE.
     *
     * @param reaction the comment reaction entity
     * @return the reaction response DTO, or null if input is null
     */
    public ReactionResponse toResponse(CommentReaction reaction) {
        if (reaction == null) {
            return null;
        }
        return ReactionResponse.builder()
                .id(reaction.getId())
                .userId(reaction.getUserId())
                .reactionType("LIKE")
                .createdAt(reaction.getCreatedAt())
                .build();
    }
}
