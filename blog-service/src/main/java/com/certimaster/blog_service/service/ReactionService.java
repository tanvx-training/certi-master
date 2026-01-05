package com.certimaster.blog_service.service;

import com.certimaster.blog_service.dto.request.ReactionRequest;
import com.certimaster.blog_service.dto.response.ReactionResponse;

/**
 * Service interface for managing reactions on posts and comments.
 * 
 * Requirements:
 * - 6.1: Add reaction with counter increment
 * - 6.2: Change reaction type updates existing record
 * - 6.3: Remove reaction with counter decrement
 * - 6.4: React to comments
 * - 6.5: Include current user's reaction status
 */
public interface ReactionService {

    /**
     * Add or update a reaction on a post.
     * If user already has a reaction, updates the type.
     * Increments likes_count only for new reactions.
     *
     * @param postId the ID of the post
     * @param request the reaction request
     * @param userId the ID of the user
     * @return the reaction response
     * @see Requirements 6.1, 6.2
     */
    ReactionResponse addPostReaction(Long postId, ReactionRequest request, Long userId);

    /**
     * Remove a reaction from a post.
     * Decrements the post's likes_count.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @see Requirements 6.3
     */
    void removePostReaction(Long postId, Long userId);

    /**
     * Get user's reaction on a post.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return the reaction response, or null if no reaction exists
     * @see Requirements 6.5
     */
    ReactionResponse getPostReaction(Long postId, Long userId);

    /**
     * Add or update a reaction on a comment.
     * If user already has a reaction, updates the type.
     * Increments likes_count only for new reactions.
     *
     * @param commentId the ID of the comment
     * @param userId the ID of the user
     * @return the reaction response
     * @see Requirements 6.4
     */
    ReactionResponse addCommentReaction(Long commentId, Long userId);

    /**
     * Remove a reaction from a comment.
     * Decrements the comment's likes_count.
     *
     * @param commentId the ID of the comment
     * @param userId the ID of the user
     */
    void removeCommentReaction(Long commentId, Long userId);

    /**
     * Get user's reaction on a comment.
     *
     * @param commentId the ID of the comment
     * @param userId the ID of the user
     * @return the reaction response, or null if no reaction exists
     */
    ReactionResponse getCommentReaction(Long commentId, Long userId);
}
