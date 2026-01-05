package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Comment entity with hierarchical comment retrieval.
 * 
 * @see Requirements 5.3, 5.4
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find top-level comments for a post (comments without parent).
     * Used for hierarchical comment retrieval.
     * 
     * @see Requirements 5.3
     */
    @Query("""
            SELECT c FROM Comment c
            WHERE c.post.id = :postId
            AND c.parentComment IS NULL
            AND c.isApproved = true
            ORDER BY c.createdAt DESC
            """)
    Page<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    /**
     * Find all top-level comments for a post (without pagination).
     * 
     * @see Requirements 5.3
     */
    @Query("""
            SELECT c FROM Comment c
            WHERE c.post.id = :postId
            AND c.parentComment IS NULL
            AND c.isApproved = true
            ORDER BY c.createdAt DESC
            """)
    List<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId);

    /**
     * Find replies to a comment.
     * 
     * @see Requirements 5.3
     */
    @Query("""
            SELECT c FROM Comment c
            WHERE c.parentComment.id = :parentCommentId
            AND c.isApproved = true
            ORDER BY c.createdAt ASC
            """)
    List<Comment> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Find all comments for a post (flat list).
     */
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    /**
     * Find all comments by a user.
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    /**
     * Count comments by post.
     * 
     * @see Requirements 5.4
     */
    long countByPostId(Long postId);

    /**
     * Count approved comments by post.
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isApproved = true")
    long countApprovedByPostId(@Param("postId") Long postId);

    /**
     * Count replies to a comment (including nested replies recursively).
     * This counts direct replies only; for full cascade count, use service layer.
     * 
     * @see Requirements 5.4
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentCommentId")
    long countRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Find all descendant comments (replies and their replies) for cascade delete.
     * Uses recursive approach in service layer.
     * 
     * @see Requirements 5.4
     */
    List<Comment> findByParentCommentId(Long parentCommentId);

    /**
     * Delete all comments for a post.
     */
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * Increment likes count for a comment.
     * 
     * @see Requirements 6.4
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likesCount = c.likesCount + 1 WHERE c.id = :id")
    void incrementLikesCount(@Param("id") Long id);

    /**
     * Decrement likes count for a comment.
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likesCount = c.likesCount - 1 WHERE c.id = :id AND c.likesCount > 0")
    void decrementLikesCount(@Param("id") Long id);

    /**
     * Check if comment belongs to a specific post.
     */
    boolean existsByIdAndPostId(Long id, Long postId);
}
