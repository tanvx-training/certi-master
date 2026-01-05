package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.PostReaction;
import com.certimaster.blog_service.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostReaction entity.
 * 
 * @see Requirements 6.2, 6.5
 */
@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    /**
     * Find user's reaction on a post.
     * 
     * @see Requirements 6.2, 6.5
     */
    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * Check if user has reacted to a post.
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * Find all reactions for a post.
     */
    List<PostReaction> findByPostId(Long postId);

    /**
     * Find all reactions by a user.
     */
    List<PostReaction> findByUserId(Long userId);

    /**
     * Count reactions by type for a post.
     */
    @Query("SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.id = :postId AND pr.reactionType = :reactionType")
    long countByPostIdAndReactionType(@Param("postId") Long postId, @Param("reactionType") ReactionType reactionType);

    /**
     * Count total reactions for a post.
     */
    long countByPostId(Long postId);

    /**
     * Delete user's reaction on a post.
     * 
     * @see Requirements 6.3
     */
    @Modifying
    @Query("DELETE FROM PostReaction pr WHERE pr.post.id = :postId AND pr.userId = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * Delete all reactions for a post.
     */
    @Modifying
    @Query("DELETE FROM PostReaction pr WHERE pr.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * Get reaction type counts for a post.
     * Returns array of [ReactionType, count] pairs.
     */
    @Query("""
            SELECT pr.reactionType, COUNT(pr)
            FROM PostReaction pr
            WHERE pr.post.id = :postId
            GROUP BY pr.reactionType
            """)
    List<Object[]> countReactionsByTypeForPost(@Param("postId") Long postId);
}
