package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.dto.mapper.CommentMapper;
import com.certimaster.blog_service.dto.request.CommentRequest;
import com.certimaster.blog_service.dto.response.CommentResponse;
import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.entity.Comment;
import com.certimaster.blog_service.entity.CommentReaction;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.repository.CommentReactionRepository;
import com.certimaster.blog_service.repository.CommentRepository;
import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.service.CommentService;
import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.ForbiddenException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import com.certimaster.common_library.exception.business.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CommentService for managing comments on blog posts.
 * 
 * Requirements:
 * - 5.1: Add comment with counter increment
 * - 5.2: Add reply with parent reference
 * - 5.3: Get comments with hierarchical structure
 * - 5.4: Delete comment with cascade and counter decrement
 * - 5.5: Edit comment with updated_at timestamp
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final CommentMapper commentMapper;


    @Override
    public CommentResponse addComment(Long postId, CommentRequest request, Long userId) {
        log.debug("Adding comment to post: {} by user: {}", postId, userId);

        // Find the post
        Post post = findPostById(postId);

        // Create comment entity
        Comment comment = commentMapper.toEntity(request);
        comment.setPost(post);
        comment.setUserId(userId);
        comment.setLikesCount(0);
        comment.setIsApproved(true);

        // Save comment
        Comment savedComment = commentRepository.save(comment);

        // Increment post's comments_count (Requirement 5.1)
        postRepository.incrementCommentsCount(postId);

        log.info("Added comment {} to post {} by user {}", savedComment.getId(), postId, userId);

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public CommentResponse addReply(Long postId, Long parentCommentId, CommentRequest request, Long userId) {
        log.debug("Adding reply to comment: {} on post: {} by user: {}", parentCommentId, postId, userId);

        // Find the post
        Post post = findPostById(postId);

        // Find the parent comment
        Comment parentComment = findCommentById(parentCommentId);

        // Validate parent comment belongs to the same post
        if (!parentComment.getPost().getId().equals(postId)) {
            throw new ValidationException("Parent comment does not belong to the specified post");
        }

        // Create reply entity
        Comment reply = commentMapper.toEntity(request);
        reply.setPost(post);
        reply.setUserId(userId);
        reply.setParentComment(parentComment);
        reply.setLikesCount(0);
        reply.setIsApproved(true);

        // Save reply
        Comment savedReply = commentRepository.save(reply);

        // Increment post's comments_count (Requirement 5.2)
        postRepository.incrementCommentsCount(postId);

        log.info("Added reply {} to comment {} on post {} by user {}", 
                savedReply.getId(), parentCommentId, postId, userId);

        return commentMapper.toResponse(savedReply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, Long currentUserId) {
        log.debug("Getting comments for post: {}", postId);

        // Verify post exists
        if (!postRepository.existsById(postId)) {
            throw ResourceNotFoundException.byId("Post", postId);
        }

        // Get top-level comments
        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPostId(postId);

        // Build hierarchical response
        return buildHierarchicalComments(topLevelComments, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<CommentResponse> getCommentsPaginated(Long postId, Long currentUserId, int page, int size) {
        log.debug("Getting paginated comments for post: {} (page: {}, size: {})", postId, page, size);

        // Verify post exists
        if (!postRepository.existsById(postId)) {
            throw ResourceNotFoundException.byId("Post", postId);
        }

        // Get top-level comments with pagination
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findTopLevelCommentsByPostId(postId, pageable);

        // Build hierarchical response for each top-level comment
        Page<CommentResponse> responsePage = commentPage.map(comment -> 
                buildCommentWithReplies(comment, currentUserId));

        return PageDto.of(responsePage);
    }

    @Override
    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest request, Long userId) {
        log.debug("Updating comment: {} on post: {} by user: {}", commentId, postId, userId);

        // Find the comment
        Comment comment = findCommentById(commentId);

        // Validate comment belongs to the post
        if (!comment.getPost().getId().equals(postId)) {
            throw new ValidationException("Comment does not belong to the specified post");
        }

        // Check authorization - only comment owner can update
        checkCommentOwnership(comment, userId);

        // Update content (Requirement 5.5 - updated_at is handled by BaseEntity)
        commentMapper.updateEntity(comment, request);

        // Save updated comment
        Comment savedComment = commentRepository.save(comment);

        log.info("Updated comment {} on post {} by user {}", commentId, postId, userId);

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId, Long userId) {
        log.debug("Deleting comment: {} on post: {} by user: {}", commentId, postId, userId);

        // Find the comment
        Comment comment = findCommentById(commentId);

        // Validate comment belongs to the post
        if (!comment.getPost().getId().equals(postId)) {
            throw new ValidationException("Comment does not belong to the specified post");
        }

        // Check authorization - only comment owner can delete
        checkCommentOwnership(comment, userId);

        // Count total comments to delete (comment + all nested replies)
        int totalToDelete = countCommentAndReplies(comment);

        // Delete comment (cascade will delete replies due to orphanRemoval)
        commentRepository.delete(comment);

        // Decrement post's comments_count by total deleted (Requirement 5.4)
        postRepository.decrementCommentsCount(postId, totalToDelete);

        log.info("Deleted comment {} and {} replies from post {} by user {}", 
                commentId, totalToDelete - 1, postId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId, Long currentUserId) {
        log.debug("Getting comment by id: {}", commentId);

        Comment comment = findCommentById(commentId);
        return buildCommentWithReplies(comment, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCommentsByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }


    // ==================== Private Helper Methods ====================

    /**
     * Find post by ID or throw ResourceNotFoundException.
     */
    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", id));
    }

    /**
     * Find comment by ID or throw ResourceNotFoundException.
     */
    private Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Comment", id));
    }

    /**
     * Check if user is the owner of the comment.
     * Throws ForbiddenException if not.
     */
    private void checkCommentOwnership(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw ForbiddenException.resourceOwnerOnly();
        }
    }

    /**
     * Build hierarchical comment responses from top-level comments.
     * Recursively fetches and nests replies.
     *
     * @see Requirements 5.3
     */
    private List<CommentResponse> buildHierarchicalComments(List<Comment> topLevelComments, Long currentUserId) {
        List<CommentResponse> responses = new ArrayList<>();
        
        for (Comment comment : topLevelComments) {
            CommentResponse response = buildCommentWithReplies(comment, currentUserId);
            responses.add(response);
        }
        
        return responses;
    }

    /**
     * Build a single comment response with nested replies.
     */
    private CommentResponse buildCommentWithReplies(Comment comment, Long currentUserId) {
        CommentResponse response = commentMapper.toResponse(comment);
        
        // Get current user's reaction if authenticated
        if (currentUserId != null) {
            Optional<CommentReaction> reaction = commentReactionRepository
                    .findByCommentIdAndUserId(comment.getId(), currentUserId);
            
            reaction.ifPresent(r -> {
                ReactionResponse reactionResponse = ReactionResponse.builder()
                        .id(r.getId())
                        .userId(r.getUserId())
                        .build();
                response.setCurrentUserReaction(reactionResponse);
            });
        }
        
        // Recursively build replies
        List<Comment> replies = commentRepository.findRepliesByParentCommentId(comment.getId());
        if (!replies.isEmpty()) {
            List<CommentResponse> replyResponses = new ArrayList<>();
            for (Comment reply : replies) {
                replyResponses.add(buildCommentWithReplies(reply, currentUserId));
            }
            response.setReplies(replyResponses);
        } else {
            response.setReplies(new ArrayList<>());
        }
        
        return response;
    }

    /**
     * Count a comment and all its nested replies recursively.
     * Used for decrementing comments_count on delete.
     *
     * @see Requirements 5.4
     */
    private int countCommentAndReplies(Comment comment) {
        int count = 1; // Count the comment itself
        
        // Get direct replies
        List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
        
        // Recursively count each reply and its nested replies
        for (Comment reply : replies) {
            count += countCommentAndReplies(reply);
        }
        
        return count;
    }
}
