package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.PostRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.dto.response.PostDetailResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostCategoryMapping;
import com.certimaster.blog_service.entity.PostTagMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Post entities and DTOs.
 * Handles transformation of post data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CategoryMapper.class, TagMapper.class}
)
public interface PostMapper {

    /**
     * Converts a Post entity to PostResponse DTO (list view).
     *
     * @param post the post entity
     * @return the post response DTO
     */
    @Mapping(target = "status", source = "status")
    @Mapping(target = "categories", source = "categoryMappings", qualifiedByName = "mapCategories")
    @Mapping(target = "tags", source = "tagMappings", qualifiedByName = "mapTags")
    PostResponse toResponse(Post post);

    /**
     * Converts a list of Post entities to PostResponse DTOs.
     *
     * @param posts the list of post entities
     * @return the list of post response DTOs
     */
    List<PostResponse> toResponseList(List<Post> posts);

    /**
     * Converts a Post entity to PostDetailResponse DTO (single post view).
     *
     * @param post the post entity
     * @return the detailed post response DTO
     */
    @Mapping(target = "status", source = "status")
    @Mapping(target = "categories", source = "categoryMappings", qualifiedByName = "mapCategories")
    @Mapping(target = "tags", source = "tagMappings", qualifiedByName = "mapTags")
    @Mapping(target = "currentUserReaction", ignore = true)
    PostDetailResponse toDetailResponse(Post post);

    /**
     * Converts a PostRequest DTO to Post entity.
     * Note: slug, authorId, contentHtml, and relationships must be set separately.
     *
     * @param request the post request DTO
     * @return the post entity
     */
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "contentHtml", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "viewsCount", constant = "0")
    @Mapping(target = "likesCount", constant = "0")
    @Mapping(target = "commentsCount", constant = "0")
    @Mapping(target = "readingTimeMinutes", ignore = true)
    @Mapping(target = "categoryMappings", ignore = true)
    @Mapping(target = "tagMappings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Post toEntity(PostRequest request);

    /**
     * Updates an existing Post entity with data from PostRequest DTO.
     * Note: slug is preserved, contentHtml must be regenerated separately.
     *
     * @param post the post entity to update
     * @param request the post request DTO with new data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "contentHtml", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "commentsCount", ignore = true)
    @Mapping(target = "readingTimeMinutes", ignore = true)
    @Mapping(target = "categoryMappings", ignore = true)
    @Mapping(target = "tagMappings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget Post post, PostRequest request);

    /**
     * Maps category mappings to category responses.
     *
     * @param mappings the set of category mappings
     * @return the list of category responses
     */
    @Named("mapCategories")
    default List<CategoryResponse> mapCategories(Set<PostCategoryMapping> mappings) {
        if (mappings == null) {
            return null;
        }
        return mappings.stream()
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
     *
     * @param mappings the set of tag mappings
     * @return the list of tag responses
     */
    @Named("mapTags")
    default List<TagResponse> mapTags(Set<PostTagMapping> mappings) {
        if (mappings == null) {
            return null;
        }
        return mappings.stream()
            .map(mapping -> TagResponse.builder()
                .id(mapping.getTag().getId())
                .name(mapping.getTag().getName())
                .slug(mapping.getTag().getSlug())
                .build())
            .collect(Collectors.toList());
    }
}
