package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CommentReaction entity.
 * 
 * @see Requirements 6.2, 6.5
 */
@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    /**
     * Find user's reaction on a comment.
     * 
     * @see Requirements 6.5
     */
    Optional<CommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * Check if user has reacted to a comment.
     */
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * Find all reactions for a comment.
     */
    List<CommentReaction> findByCommentId(Long commentId);

    /**
     * Find all reactions by a user.
     */
    List<CommentReaction> findByUserId(Long userId);

    /**
     * Count total reactions for a comment.
     */
    long countByCommentId(Long commentId);

    /**
     * Delete user's reaction on a comment.
     */
    @Modifying
    @Query("DELETE FROM CommentReaction cr WHERE cr.comment.id = :commentId AND cr.userId = :userId")
    void deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * Delete all reactions for a comment.
     */
    @Modifying
    @Query("DELETE FROM CommentReaction cr WHERE cr.comment.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);
}
