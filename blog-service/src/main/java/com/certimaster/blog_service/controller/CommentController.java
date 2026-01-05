package com.certimaster.blog_service.controller;

import com.certimaster.blog_service.dto.request.CommentRequest;
import com.certimaster.blog_service.dto.response.CommentResponse;
import com.certimaster.blog_service.security.SecurityUtils;
import com.certimaster.blog_service.service.CommentService;
import com.certimaster.common_library.dto.PageDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Comment API.
 * 
 * Requirements:
 * - 5.1: Add comment with counter increment
 * - 5.2: Add reply with parent reference
 * - 5.3: Get comments with hierarchical structure
 * - 5.4: Delete comment with cascade and counter decrement
 * - 5.5: Edit comment with updated_at timestamp
 * - 10.4: Allow any authenticated user to comment
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Get comments for a post in hierarchical structure.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/posts/{postId}/comments
     * @see Requirements 5.3
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<CommentResponse>>> getComments(
            @PathVariable Long postId
    ) {
        log.debug("Get comments for post: {}", postId);

        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);
        List<CommentResponse> result = commentService.getComments(postId, currentUserId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get comments for a post with pagination.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/posts/{postId}/comments/paginated
     * @see Requirements 5.3
     */
    @GetMapping("/paginated")
    public ResponseEntity<ResponseDto<PageDto<CommentResponse>>> getCommentsPaginated(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.debug("Get paginated comments for post: {}", postId);

        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);
        PageDto<CommentResponse> result = commentService.getCommentsPaginated(postId, currentUserId, page, size);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Add a new comment to a post.
     * Requires authentication.
     * 
     * POST /api/v1/posts/{postId}/comments
     * @see Requirements 5.1, 10.4
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<CommentResponse>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.debug("Add comment to post: {}", postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        CommentResponse result;
        if (request.getParentCommentId() != null) {
            // This is a reply to an existing comment
            result = commentService.addReply(postId, request.getParentCommentId(), request, userId);
        } else {
            // This is a top-level comment
            result = commentService.addComment(postId, request, userId);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Comment added successfully", result));
    }

    /**
     * Add a reply to an existing comment.
     * Requires authentication.
     * 
     * POST /api/v1/posts/{postId}/comments/{commentId}/replies
     * @see Requirements 5.2, 10.4
     */
    @PostMapping("/{commentId}/replies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<CommentResponse>> addReply(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.debug("Add reply to comment: {} on post: {}", commentId, postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        CommentResponse result = commentService.addReply(postId, commentId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Reply added successfully", result));
    }

    /**
     * Update an existing comment.
     * Requires authentication and ownership of the comment.
     * 
     * PUT /api/v1/posts/{postId}/comments/{commentId}
     * @see Requirements 5.5
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.debug("Update comment: {} on post: {}", commentId, postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        CommentResponse result = commentService.updateComment(postId, commentId, request, userId);
        return ResponseEntity.ok(ResponseDto.success("Comment updated successfully", result));
    }

    /**
     * Delete a comment and all its replies.
     * Requires authentication and ownership of the comment (or admin permission).
     * 
     * DELETE /api/v1/posts/{postId}/comments/{commentId}
     * @see Requirements 5.4
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        log.debug("Delete comment: {} on post: {}", commentId, postId);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        commentService.deleteComment(postId, commentId, userId);
        return ResponseEntity.ok(ResponseDto.success("Comment deleted successfully", null));
    }
}
