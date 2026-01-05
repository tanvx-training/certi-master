package com.certimaster.blog_service.entity;

/**
 * Enum representing the type of reaction a user can give to a post or comment.
 * 
 * @see Requirements 6.1
 */
public enum ReactionType {
    /**
     * Standard like reaction
     */
    LIKE,
    
    /**
     * Love reaction for exceptional content
     */
    LOVE,
    
    /**
     * Helpful reaction for useful technical content
     */
    HELPFUL
}
