package com.certimaster.blog_service.controller;

import com.certimaster.blog_service.dto.request.ReactionRequest;
import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.security.SecurityUtils;
import com.certimaster.blog_service.service.ReactionService;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.exception.business.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Reaction API.
 * 
 * Requirements:
 * - 6.1: Add reaction with counter increment
 * - 6.2: Change reaction type updates existing record
 * - 6.3: Remove reaction with counter decrement
 * - 6.4: React to comments
 * - 10.4: Allow any authenticated user to react
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    /**
     * Add or update a reaction on a post.
     * Requires authentication.
     * 
     * POST /api/v1/reactions/posts/{postId}
     * @see Requirements 6.1, 6.2, 10.4
     */
    @PostMapping("/posts/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<ReactionResponse>> addPostReaction(
            @PathVariable Long postId,
            @Valid @RequestBody ReactionRequest request
    ) {
        log.debug("Add reaction to post: {}", postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        ReactionResponse result = reactionService.addPostReaction(postId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Reaction added successfully", result));
    }

    /**
     * Remove a reaction from a post.
     * Requires authentication.
     * 
     * DELETE /api/v1/reactions/posts/{postId}
     * @see Requirements 6.3
     */
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<Void>> removePostReaction(@PathVariable Long postId) {
        log.debug("Remove reaction from post: {}", postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        reactionService.removePostReaction(postId, userId);
        return ResponseEntity.ok(ResponseDto.success("Reaction removed successfully", null));
    }

    /**
     * Get current user's reaction on a post.
     * Requires authentication.
     * 
     * GET /api/v1/reactions/posts/{postId}
     */
    @GetMapping("/posts/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<ReactionResponse>> getPostReaction(@PathVariable Long postId) {
        log.debug("Get reaction for post: {}", postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        ReactionResponse result = reactionService.getPostReaction(postId, userId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Add a reaction (like) on a comment.
     * Requires authentication.
     * 
     * POST /api/v1/reactions/comments/{commentId}
     * @see Requirements 6.4, 10.4
     */
    @PostMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<ReactionResponse>> addCommentReaction(
            @PathVariable Long commentId
    ) {
        log.debug("Add reaction to comment: {}", commentId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        ReactionResponse result = reactionService.addCommentReaction(commentId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Reaction added successfully", result));
    }

    /**
     * Remove a reaction from a comment.
     * Requires authentication.
     * 
     * DELETE /api/v1/reactions/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<Void>> removeCommentReaction(@PathVariable Long commentId) {
        log.debug("Remove reaction from comment: {}", commentId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        reactionService.removeCommentReaction(commentId, userId);
        return ResponseEntity.ok(ResponseDto.success("Reaction removed successfully", null));
    }

    /**
     * Get current user's reaction on a comment.
     * Requires authentication.
     * 
     * GET /api/v1/reactions/comments/{commentId}
     */
    @GetMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<ReactionResponse>> getCommentReaction(@PathVariable Long commentId) {
        log.debug("Get reaction for comment: {}", commentId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        ReactionResponse result = reactionService.getCommentReaction(commentId, userId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}
