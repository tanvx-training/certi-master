package com.certimaster.blog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating or updating a blog post.
 * Contains validation constraints for all fields.
 * 
 * @see Requirements 1.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    /**
     * Title of the post.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    /**
     * Raw Markdown content of the post.
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * Short excerpt/summary of the post.
     */
    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    /**
     * URL of the featured image (from external storage like S3, Cloudinary).
     */
    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    private String featuredImage;

    /**
     * List of category IDs to assign to the post.
     */
    private List<Long> categoryIds;

    /**
     * List of tag names to assign to the post.
     * New tags will be created if they don't exist.
     */
    private List<String> tagNames;

    /**
     * SEO title for search engine optimization.
     */
    @Size(max = 255, message = "SEO title must not exceed 255 characters")
    private String seoTitle;

    /**
     * SEO description for search engine optimization.
     */
    @Size(max = 500, message = "SEO description must not exceed 500 characters")
    private String seoDescription;

    /**
     * SEO keywords for search engine optimization.
     */
    @Size(max = 500, message = "SEO keywords must not exceed 500 characters")
    private String seoKeywords;
}
