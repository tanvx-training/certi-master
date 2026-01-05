package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.entity.CommentReaction;
import com.certimaster.blog_service.entity.PostReaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Reaction entities and DTOs.
 * Handles transformation of reaction data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReactionMapper {

    /**
     * Converts a PostReaction entity to ReactionResponse DTO.
     *
     * @param reaction the post reaction entity
     * @return the reaction response DTO
     */
    @Mapping(target = "reactionType", source = "reactionType")
    ReactionResponse toResponse(PostReaction reaction);

    /**
     * Converts a CommentReaction entity to ReactionResponse DTO.
     * Note: CommentReaction doesn't have reactionType, defaults to LIKE.
     *
     * @param reaction the comment reaction entity
     * @return the reaction response DTO
     */
    @Mapping(target = "reactionType", constant = "LIKE")
    ReactionResponse toResponse(CommentReaction reaction);
}
