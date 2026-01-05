package com.certimaster.blog_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Response DTO for comment data with hierarchical structure.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 * 
 * @see Requirements 5.3
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentResponse extends BaseDto {

    /**
     * ID of the post this comment belongs to.
     */
    private Long postId;

    /**
     * ID of the user who wrote the comment.
     */
    private Long userId;

    /**
     * ID of the parent comment (null for top-level comments).
     */
    private Long parentCommentId;

    /**
     * Content of the comment.
     */
    private String content;

    /**
     * Number of likes on this comment.
     */
    private Integer likesCount;

    /**
     * Whether the comment is approved.
     */
    private Boolean isApproved;

    /**
     * Nested replies to this comment.
     */
    private List<CommentResponse> replies;

    /**
     * Current user's reaction on this comment (if authenticated).
     */
    private ReactionResponse currentUserReaction;
}
