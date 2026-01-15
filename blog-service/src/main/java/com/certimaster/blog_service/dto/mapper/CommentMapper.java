package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.CommentRequest;
import com.certimaster.blog_service.dto.response.CommentResponse;
import com.certimaster.blog_service.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Comment entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class CommentMapper {

    /**
     * Converts a Comment entity to CommentResponse DTO.
     * Note: replies and currentUserReaction must be set separately.
     *
     * @param comment the comment entity
     * @return the comment response DTO, or null if input is null
     */
    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .createdBy(comment.getCreatedBy())
                .updatedBy(comment.getUpdatedBy())
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .userId(comment.getUserId())
                .parentCommentId(comment.getParentComment() != null 
                        ? comment.getParentComment().getId() 
                        : null)
                .content(comment.getContent())
                .likesCount(comment.getLikesCount())
                .isApproved(comment.getIsApproved())
                .build();
    }

    /**
     * Converts a list of Comment entities to CommentResponse DTOs.
     * Filters out null elements from the input list.
     *
     * @param comments the list of comment entities
     * @return the list of comment response DTOs, or empty list if input is null
     */
    public List<CommentResponse> toResponseList(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a CommentRequest DTO to Comment entity.
     * Note: post, parentComment, and userId must be set separately.
     *
     * @param request the comment request DTO
     * @return the comment entity, or null if input is null
     */
    public Comment toEntity(CommentRequest request) {
        if (request == null) {
            return null;
        }
        return Comment.builder()
                .content(request.getContent())
                .likesCount(0)
                .isApproved(true)
                .build();
    }

    /**
     * Updates an existing Comment entity with data from CommentRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param comment the comment entity to update
     * @param request the comment request DTO with new data
     */
    public void updateEntity(Comment comment, CommentRequest request) {
        if (comment == null || request == null) {
            return;
        }
        if (request.getContent() != null) {
            comment.setContent(request.getContent());
        }
    }
}
