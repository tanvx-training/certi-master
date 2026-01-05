package com.certimaster.blog_service.service;

import com.certimaster.blog_service.dto.request.CommentRequest;
import com.certimaster.blog_service.dto.response.CommentResponse;
import com.certimaster.common_library.dto.PageDto;

import java.util.List;

/**
 * Service interface for managing comments on blog posts.
 * 
 * Requirements:
 * - 5.1: Add comment with counter increment
 * - 5.2: Add reply with parent reference
 * - 5.3: Get comments with hierarchical structure
 * - 5.4: Delete comment with cascade and counter decrement
 * - 5.5: Edit comment with updated_at timestamp
 */
public interface CommentService {

    /**
     * Add a new comment to a post.
     * Increments the post's comments_count.
     *
     * @param postId the ID of the post to comment on
     * @param request the comment request containing content
     * @param userId the ID of the user adding the comment
     * @return the created comment response
     * @see Requirements 5.1
     */
    CommentResponse addComment(Long postId, CommentRequest request, Long userId);

    /**
     * Add a reply to an existing comment.
     * Sets the parent_comment_id reference and increments post's comments_count.
     *
     * @param postId the ID of the post
     * @param parentCommentId the ID of the parent comment
     * @param request the comment request containing content
     * @param userId the ID of the user adding the reply
     * @return the created reply response
     * @see Requirements 5.2
     */
    CommentResponse addReply(Long postId, Long parentCommentId, CommentRequest request, Long userId);

    /**
     * Get comments for a post in hierarchical structure.
     * Returns top-level comments with nested replies.
     *
     * @param postId the ID of the post
     * @param currentUserId the ID of the current user (for reaction status), can be null
     * @return list of comments with nested replies
     * @see Requirements 5.3
     */
    List<CommentResponse> getComments(Long postId, Long currentUserId);

    /**
     * Get comments for a post with pagination.
     * Returns top-level comments with nested replies.
     *
     * @param postId the ID of the post
     * @param currentUserId the ID of the current user (for reaction status), can be null
     * @param page the page number
     * @param size the page size
     * @return paginated list of comments with nested replies
     * @see Requirements 5.3
     */
    PageDto<CommentResponse> getCommentsPaginated(Long postId, Long currentUserId, int page, int size);

    /**
     * Update an existing comment.
     * Sets the updated_at timestamp.
     *
     * @param postId the ID of the post
     * @param commentId the ID of the comment to update
     * @param request the comment request with new content
     * @param userId the ID of the user performing the update
     * @return the updated comment response
     * @see Requirements 5.5
     */
    CommentResponse updateComment(Long postId, Long commentId, CommentRequest request, Long userId);

    /**
     * Delete a comment and all its replies.
     * Cascade deletes all replies and decrements post's comments_count accordingly.
     *
     * @param postId the ID of the post
     * @param commentId the ID of the comment to delete
     * @param userId the ID of the user performing the deletion
     * @see Requirements 5.4
     */
    void deleteComment(Long postId, Long commentId, Long userId);

    /**
     * Get a single comment by ID.
     *
     * @param commentId the ID of the comment
     * @param currentUserId the ID of the current user (for reaction status), can be null
     * @return the comment response
     */
    CommentResponse getCommentById(Long commentId, Long currentUserId);

    /**
     * Count total comments for a post (including replies).
     *
     * @param postId the ID of the post
     * @return the total comment count
     */
    long countCommentsByPost(Long postId);
}
