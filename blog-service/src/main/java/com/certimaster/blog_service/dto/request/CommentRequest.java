package com.certimaster.blog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a comment.
 * 
 * @see Requirements 5.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    /**
     * Content of the comment.
     */
    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    private String content;

    /**
     * ID of the parent comment for replies.
     * Null for top-level comments.
     */
    private Long parentCommentId;
}
