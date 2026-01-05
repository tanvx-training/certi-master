package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.CommentRequest;
import com.certimaster.blog_service.dto.response.CommentResponse;
import com.certimaster.blog_service.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between Comment entities and DTOs.
 * Handles transformation of comment data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {

    /**
     * Converts a Comment entity to CommentResponse DTO.
     * Note: replies and currentUserReaction must be set separately.
     *
     * @param comment the comment entity
     * @return the comment response DTO
     */
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "currentUserReaction", ignore = true)
    CommentResponse toResponse(Comment comment);

    /**
     * Converts a list of Comment entities to CommentResponse DTOs.
     *
     * @param comments the list of comment entities
     * @return the list of comment response DTOs
     */
    List<CommentResponse> toResponseList(List<Comment> comments);

    /**
     * Converts a CommentRequest DTO to Comment entity.
     * Note: post, parentComment, and userId must be set separately.
     *
     * @param request the comment request DTO
     * @return the comment entity
     */
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "likesCount", constant = "0")
    @Mapping(target = "isApproved", constant = "true")
    Comment toEntity(CommentRequest request);

    /**
     * Updates an existing Comment entity with data from CommentRequest DTO.
     *
     * @param comment the comment entity to update
     * @param request the comment request DTO with new data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "isApproved", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget Comment comment, CommentRequest request);
}
