package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.dto.request.ReactionRequest;
import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.entity.Comment;
import com.certimaster.blog_service.entity.CommentReaction;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostReaction;
import com.certimaster.blog_service.entity.ReactionType;
import com.certimaster.blog_service.repository.CommentReactionRepository;
import com.certimaster.blog_service.repository.CommentRepository;
import com.certimaster.blog_service.repository.PostReactionRepository;
import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.service.ReactionService;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of ReactionService.
 * 
 * Requirements:
 * - 6.1: Add reaction with counter increment
 * - 6.2: Change reaction type updates existing record
 * - 6.3: Remove reaction with counter decrement
 * - 6.4: React to comments
 * - 6.5: Include current user's reaction status
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final PostReactionRepository postReactionRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ReactionResponse addPostReaction(Long postId, ReactionRequest request, Long userId) {
        log.debug("Adding reaction to post {} by user {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        ReactionType reactionType = ReactionType.valueOf(request.getReactionType());
        Optional<PostReaction> existingReaction = postReactionRepository.findByPostIdAndUserId(postId, userId);

        PostReaction reaction;
        if (existingReaction.isPresent()) {
            // Update existing reaction type (Requirement 6.2)
            reaction = existingReaction.get();
            reaction.setReactionType(reactionType);
            log.debug("Updated existing reaction type to {}", reactionType);
        } else {
            // Create new reaction and increment counter (Requirement 6.1)
            reaction = PostReaction.builder()
                    .post(post)
                    .userId(userId)
                    .reactionType(reactionType)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            // Increment likes_count
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            log.debug("Created new reaction and incremented likes_count to {}", post.getLikesCount());
        }

        reaction = postReactionRepository.save(reaction);
        return mapToReactionResponse(reaction);
    }


    @Override
    @Transactional
    public void removePostReaction(Long postId, Long userId) {
        log.debug("Removing reaction from post {} by user {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Optional<PostReaction> existingReaction = postReactionRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingReaction.isPresent()) {
            // Delete reaction and decrement counter (Requirement 6.3)
            postReactionRepository.delete(existingReaction.get());
            
            // Decrement likes_count
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postRepository.save(post);
            log.debug("Removed reaction and decremented likes_count to {}", post.getLikesCount());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getPostReaction(Long postId, Long userId) {
        if (userId == null) {
            return null;
        }
        
        return postReactionRepository.findByPostIdAndUserId(postId, userId)
                .map(this::mapToReactionResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public ReactionResponse addCommentReaction(Long commentId, Long userId) {
        log.debug("Adding reaction to comment {} by user {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Optional<CommentReaction> existingReaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);

        CommentReaction reaction;
        if (existingReaction.isPresent()) {
            // Reaction already exists, return it
            reaction = existingReaction.get();
            log.debug("Reaction already exists for comment {} by user {}", commentId, userId);
        } else {
            // Create new reaction and increment counter (Requirement 6.4)
            reaction = CommentReaction.builder()
                    .comment(comment)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            // Increment likes_count
            comment.setLikesCount(comment.getLikesCount() + 1);
            commentRepository.save(comment);
            reaction = commentReactionRepository.save(reaction);
            log.debug("Created new reaction and incremented likes_count to {}", comment.getLikesCount());
        }

        return mapToCommentReactionResponse(reaction);
    }

    @Override
    @Transactional
    public void removeCommentReaction(Long commentId, Long userId) {
        log.debug("Removing reaction from comment {} by user {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Optional<CommentReaction> existingReaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);
        
        if (existingReaction.isPresent()) {
            // Delete reaction and decrement counter
            commentReactionRepository.delete(existingReaction.get());
            
            // Decrement likes_count
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
            commentRepository.save(comment);
            log.debug("Removed reaction and decremented likes_count to {}", comment.getLikesCount());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getCommentReaction(Long commentId, Long userId) {
        if (userId == null) {
            return null;
        }
        
        return commentReactionRepository.findByCommentIdAndUserId(commentId, userId)
                .map(this::mapToCommentReactionResponse)
                .orElse(null);
    }

    private ReactionResponse mapToReactionResponse(PostReaction reaction) {
        return ReactionResponse.builder()
                .id(reaction.getId())
                .userId(reaction.getUserId())
                .reactionType(reaction.getReactionType().name())
                .createdAt(reaction.getCreatedAt())
                .build();
    }

    private ReactionResponse mapToCommentReactionResponse(CommentReaction reaction) {
        return ReactionResponse.builder()
                .id(reaction.getId())
                .userId(reaction.getUserId())
                .reactionType("LIKE") // Comment reactions are always LIKE type
                .createdAt(reaction.getCreatedAt())
                .build();
    }
}
