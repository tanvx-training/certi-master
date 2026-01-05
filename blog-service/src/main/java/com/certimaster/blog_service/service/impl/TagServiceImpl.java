package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.dto.mapper.PostMapper;
import com.certimaster.blog_service.dto.mapper.TagMapper;
import com.certimaster.blog_service.dto.request.TagRequest;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostStatus;
import com.certimaster.blog_service.entity.PostTag;
import com.certimaster.blog_service.entity.PostTagMapping;
import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.repository.PostTagMappingRepository;
import com.certimaster.blog_service.repository.PostTagRepository;
import com.certimaster.blog_service.service.TagService;
import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of TagService for managing blog tags.
 * 
 * Requirements:
 * - 4.1: Create tags with auto-creation of new tags when adding to posts
 * - 4.2: Retrieve posts by tag with pagination
 * - 4.3: Delete tag with cascade to mappings
 * - 4.4: List tags with post counts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagServiceImpl implements TagService {

    private final PostTagRepository tagRepository;
    private final PostTagMappingRepository tagMappingRepository;
    private final PostRepository postRepository;
    private final TagMapper tagMapper;
    private final PostMapper postMapper;

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");


    @Override
    public TagResponse createTag(TagRequest request) {
        log.debug("Creating tag with name: {}", request.getName());

        // Check if name already exists
        if (tagRepository.existsByName(request.getName())) {
            throw BusinessException.duplicateResource("Tag", request.getName());
        }

        // Create tag entity
        PostTag tag = tagMapper.toEntity(request);

        // Generate unique slug from name (Requirement 4.1)
        String slug = generateUniqueSlug(request.getName());
        tag.setSlug(slug);

        // Save tag
        PostTag savedTag = tagRepository.save(tag);
        log.info("Created tag with id: {} and slug: {}", savedTag.getId(), savedTag.getSlug());

        return buildTagResponse(savedTag);
    }

    @Override
    public TagResponse updateTag(Long id, TagRequest request) {
        log.debug("Updating tag: {}", id);

        PostTag tag = findTagById(id);

        // Check if new name conflicts with existing tag
        if (tagRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw BusinessException.duplicateResource("Tag", request.getName());
        }

        // Update fields
        tagMapper.updateEntity(tag, request);

        // Save updated tag
        PostTag savedTag = tagRepository.save(tag);
        log.info("Updated tag: {}", savedTag.getId());

        return buildTagResponse(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        log.debug("Getting tag by id: {}", id);
        PostTag tag = findTagById(id);
        return buildTagResponse(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagBySlug(String slug) {
        log.debug("Getting tag by slug: {}", slug);
        PostTag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> ResourceNotFoundException.byField("Tag", "slug", slug));
        return buildTagResponse(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        log.debug("Getting all tags with post counts");
        return getTagsWithCounts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getTagsWithCounts() {
        log.debug("Getting all tags with post counts");

        List<Object[]> results = tagRepository.findAllWithPostCount();

        return results.stream()
                .map(row -> {
                    PostTag tag = (PostTag) row[0];
                    Long postCount = (Long) row[1];
                    TagResponse response = tagMapper.toResponse(tag);
                    response.setPostCount(postCount);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTag(Long id) {
        log.debug("Deleting tag: {}", id);

        PostTag tag = findTagById(id);

        // Delete all mappings first (Requirement 4.3)
        tagMappingRepository.deleteByTagId(id);

        // Delete tag
        tagRepository.delete(tag);
        log.info("Deleted tag: {}", id);
    }


    @Override
    @Transactional(readOnly = true)
    public PageDto<PostResponse> getPostsByTag(String tagSlug, int page, int size) {
        log.debug("Getting posts by tag slug: {}", tagSlug);

        // Verify tag exists
        if (!tagRepository.existsBySlug(tagSlug)) {
            throw ResourceNotFoundException.byField("Tag", "slug", tagSlug);
        }

        // Get published posts with tag (Requirement 4.2)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> postPage = postRepository.findByTagSlug(tagSlug, PostStatus.PUBLISHED, pageable);

        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        return PageDto.of(responsePage);
    }

    @Override
    public void assignTagsToPost(Long postId, List<String> tagNames) {
        log.debug("Assigning tags {} to post: {}", tagNames, postId);

        if (tagNames == null || tagNames.isEmpty()) {
            log.debug("No tags to assign to post: {}", postId);
            return;
        }

        // Verify post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", postId));

        // Remove existing mappings
        tagMappingRepository.deleteByPostId(postId);

        // Find existing tags by names
        List<PostTag> existingTags = tagRepository.findByNameIn(tagNames);
        Map<String, PostTag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(PostTag::getName, tag -> tag));

        // Create mappings for each tag (Requirement 4.1 - auto-create new tags)
        for (String tagName : tagNames) {
            PostTag tag = existingTagMap.get(tagName);
            
            // Create new tag if it doesn't exist
            if (tag == null) {
                tag = PostTag.builder()
                        .name(tagName)
                        .slug(generateUniqueSlug(tagName))
                        .build();
                tag = tagRepository.save(tag);
                log.info("Auto-created new tag: {} with slug: {}", tag.getName(), tag.getSlug());
            }

            // Create mapping
            PostTagMapping mapping = new PostTagMapping(post, tag);
            tagMappingRepository.save(mapping);
        }

        log.info("Assigned {} tags to post: {}", tagNames.size(), postId);
    }

    @Override
    public void removeAllTagsFromPost(Long postId) {
        log.debug("Removing all tags from post: {}", postId);
        tagMappingRepository.deleteByPostId(postId);
        log.info("Removed all tags from post: {}", postId);
    }

    @Override
    public TagResponse getOrCreateTag(String name) {
        log.debug("Getting or creating tag with name: {}", name);

        // Try to find existing tag
        return tagRepository.findByName(name)
                .map(this::buildTagResponse)
                .orElseGet(() -> {
                    // Create new tag if it doesn't exist (Requirement 4.1)
                    TagRequest request = TagRequest.builder()
                            .name(name)
                            .build();
                    return createTag(request);
                });
    }


    // ==================== Private Helper Methods ====================

    /**
     * Find tag by ID or throw ResourceNotFoundException.
     */
    private PostTag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Tag", id));
    }

    /**
     * Build TagResponse with post count.
     */
    private TagResponse buildTagResponse(PostTag tag) {
        TagResponse response = tagMapper.toResponse(tag);
        Long postCount = tagRepository.countPublishedPostsByTagId(tag.getId());
        response.setPostCount(postCount);
        return response;
    }

    /**
     * Generate a slug from the given name.
     * Converts to lowercase, replaces spaces with hyphens, removes special characters.
     */
    private String generateSlug(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        // Normalize unicode characters
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Convert to lowercase
        String slug = normalized.toLowerCase();

        // Remove special characters
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("");

        // Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Replace multiple consecutive hyphens with single hyphen
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }

    /**
     * Generate a unique slug from the given name.
     * If the generated slug already exists, appends a numeric suffix.
     */
    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);

        if (baseSlug.isEmpty()) {
            baseSlug = "tag";
        }

        String slug = baseSlug;
        int suffix = 1;

        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }
}
