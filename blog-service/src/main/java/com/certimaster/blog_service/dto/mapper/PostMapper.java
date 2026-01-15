package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.PostRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.dto.response.PostDetailResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostCategoryMapping;
import com.certimaster.blog_service.entity.PostTagMapping;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Post entities and DTOs.
 * Provides null-safe mapping operations with collection handling.
 */
@Component
public class PostMapper {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public PostMapper(CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    /**
     * Converts a Post entity to PostResponse DTO (list view).
     *
     * @param post the post entity
     * @return the post response DTO, or null if input is null
     */
    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }
        return PostResponse.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdBy(post.getCreatedBy())
                .updatedBy(post.getUpdatedBy())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .featuredImage(post.getFeaturedImage())
                .authorId(post.getAuthorId())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .publishedAt(post.getPublishedAt())
                .viewsCount(post.getViewsCount())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .readingTimeMinutes(post.getReadingTimeMinutes())
                .categories(mapCategories(post.getCategoryMappings()))
                .tags(mapTags(post.getTagMappings()))
                .build();
    }

    /**
     * Converts a list of Post entities to PostResponse DTOs.
     * Filters out null elements from the input list.
     *
     * @param posts the list of post entities
     * @return the list of post response DTOs, or empty list if input is null
     */
    public List<PostResponse> toResponseList(List<Post> posts) {
        if (posts == null) {
            return Collections.emptyList();
        }
        return posts.stream()
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Post entity to PostDetailResponse DTO (single post view).
     *
     * @param post the post entity
     * @return the detailed post response DTO, or null if input is null
     */
    public PostDetailResponse toDetailResponse(Post post) {
        if (post == null) {
            return null;
        }
        return PostDetailResponse.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdBy(post.getCreatedBy())
                .updatedBy(post.getUpdatedBy())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .contentHtml(post.getContentHtml())
                .excerpt(post.getExcerpt())
                .featuredImage(post.getFeaturedImage())
                .authorId(post.getAuthorId())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .publishedAt(post.getPublishedAt())
                .viewsCount(post.getViewsCount())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .readingTimeMinutes(post.getReadingTimeMinutes())
                .seoTitle(post.getSeoTitle())
                .seoDescription(post.getSeoDescription())
                .seoKeywords(post.getSeoKeywords())
                .categories(mapCategories(post.getCategoryMappings()))
                .tags(mapTags(post.getTagMappings()))
                .build();
    }

    /**
     * Converts a PostRequest DTO to Post entity.
     * Note: slug, authorId, contentHtml, and relationships must be set separately.
     *
     * @param request the post request DTO
     * @return the post entity, or null if input is null
     */
    public Post toEntity(PostRequest request) {
        if (request == null) {
            return null;
        }
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .featuredImage(request.getFeaturedImage())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .seoKeywords(request.getSeoKeywords())
                .viewsCount(0)
                .likesCount(0)
                .commentsCount(0)
                .build();
    }

    /**
     * Updates an existing Post entity with data from PostRequest DTO.
     * Only updates non-null fields from the request (partial update).
     * Note: slug is preserved, contentHtml must be regenerated separately.
     *
     * @param post the post entity to update
     * @param request the post request DTO with new data
     */
    public void updateEntity(Post post, PostRequest request) {
        if (post == null || request == null) {
            return;
        }
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getExcerpt() != null) {
            post.setExcerpt(request.getExcerpt());
        }
        if (request.getFeaturedImage() != null) {
            post.setFeaturedImage(request.getFeaturedImage());
        }
        if (request.getSeoTitle() != null) {
            post.setSeoTitle(request.getSeoTitle());
        }
        if (request.getSeoDescription() != null) {
            post.setSeoDescription(request.getSeoDescription());
        }
        if (request.getSeoKeywords() != null) {
            post.setSeoKeywords(request.getSeoKeywords());
        }
    }

    /**
     * Maps category mappings to category responses.
     * Returns empty list if mappings is null or empty.
     *
     * @param mappings the set of category mappings
     * @return the list of category responses
     */
    private List<CategoryResponse> mapCategories(Set<PostCategoryMapping> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();
        }
        return mappings.stream()
                .filter(Objects::nonNull)
                .filter(mapping -> mapping.getCategory() != null)
                .map(mapping -> CategoryResponse.builder()
                        .id(mapping.getCategory().getId())
                        .name(mapping.getCategory().getName())
                        .slug(mapping.getCategory().getSlug())
                        .description(mapping.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Maps tag mappings to tag responses.
     * Returns empty list if mappings is null or empty.
     *
     * @param mappings the set of tag mappings
     * @return the list of tag responses
     */
    private List<TagResponse> mapTags(Set<PostTagMapping> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();
        }
        return mappings.stream()
                .filter(Objects::nonNull)
                .filter(mapping -> mapping.getTag() != null)
                .map(mapping -> TagResponse.builder()
                        .id(mapping.getTag().getId())
                        .name(mapping.getTag().getName())
                        .slug(mapping.getTag().getSlug())
                        .build())
                .collect(Collectors.toList());
    }
}
