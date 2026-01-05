package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.dto.mapper.CategoryMapper;
import com.certimaster.blog_service.dto.mapper.PostMapper;
import com.certimaster.blog_service.dto.request.CategoryRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostCategory;
import com.certimaster.blog_service.entity.PostCategoryMapping;
import com.certimaster.blog_service.entity.PostStatus;
import com.certimaster.blog_service.repository.PostCategoryMappingRepository;
import com.certimaster.blog_service.repository.PostCategoryRepository;
import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.service.CategoryService;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService for managing blog categories.
 * 
 * Requirements:
 * - 3.1: Create category with slug generation
 * - 3.2: Assign categories to posts
 * - 3.3: Retrieve posts by category with pagination
 * - 3.4: Delete category with cascade to mappings
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final PostCategoryRepository categoryRepository;
    private final PostCategoryMappingRepository categoryMappingRepository;
    private final PostRepository postRepository;
    private final CategoryMapper categoryMapper;
    private final PostMapper postMapper;

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating category with name: {}", request.getName());

        // Check if name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw BusinessException.duplicateResource("Category", request.getName());
        }

        // Create category entity
        PostCategory category = categoryMapper.toEntity(request);

        // Generate unique slug from name (Requirement 3.1)
        String slug = generateUniqueSlug(request.getName());
        category.setSlug(slug);

        // Save category
        PostCategory savedCategory = categoryRepository.save(category);
        log.info("Created category with id: {} and slug: {}", savedCategory.getId(), savedCategory.getSlug());

        return buildCategoryResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.debug("Updating category: {}", id);

        PostCategory category = findCategoryById(id);

        // Check if new name conflicts with existing category
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw BusinessException.duplicateResource("Category", request.getName());
        }

        // Update fields
        categoryMapper.updateEntity(category, request);

        // Save updated category
        PostCategory savedCategory = categoryRepository.save(category);
        log.info("Updated category: {}", savedCategory.getId());

        return buildCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Getting category by id: {}", id);
        PostCategory category = findCategoryById(id);
        return buildCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.debug("Getting category by slug: {}", slug);
        PostCategory category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> ResourceNotFoundException.byField("Category", "slug", slug));
        return buildCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("Getting all categories with post counts");

        List<Object[]> results = categoryRepository.findAllWithPostCount();

        return results.stream()
                .map(row -> {
                    PostCategory category = (PostCategory) row[0];
                    Long postCount = (Long) row[1];
                    CategoryResponse response = categoryMapper.toResponse(category);
                    response.setPostCount(postCount);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long id) {
        log.debug("Deleting category: {}", id);

        PostCategory category = findCategoryById(id);

        // Delete all mappings first (Requirement 3.4)
        categoryMappingRepository.deleteByCategoryId(id);

        // Delete category
        categoryRepository.delete(category);
        log.info("Deleted category: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<PostResponse> getPostsByCategory(String categorySlug, int page, int size) {
        log.debug("Getting posts by category slug: {}", categorySlug);

        // Verify category exists
        if (!categoryRepository.existsBySlug(categorySlug)) {
            throw ResourceNotFoundException.byField("Category", "slug", categorySlug);
        }

        // Get published posts in category (Requirement 3.3)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> postPage = postRepository.findByCategorySlug(categorySlug, PostStatus.PUBLISHED, pageable);

        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        return PageDto.of(responsePage);
    }

    @Override
    public void assignCategoriesToPost(Long postId, List<Long> categoryIds) {
        log.debug("Assigning categories {} to post: {}", categoryIds, postId);

        // Verify post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", postId));

        // Remove existing mappings
        categoryMappingRepository.deleteByPostId(postId);

        // Create new mappings (Requirement 3.2)
        for (Long categoryId : categoryIds) {
            PostCategory category = findCategoryById(categoryId);
            PostCategoryMapping mapping = new PostCategoryMapping(post, category);
            categoryMappingRepository.save(mapping);
        }

        log.info("Assigned {} categories to post: {}", categoryIds.size(), postId);
    }

    @Override
    public void removeAllCategoriesFromPost(Long postId) {
        log.debug("Removing all categories from post: {}", postId);
        categoryMappingRepository.deleteByPostId(postId);
        log.info("Removed all categories from post: {}", postId);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Find category by ID or throw ResourceNotFoundException.
     */
    private PostCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Category", id));
    }

    /**
     * Build CategoryResponse with post count.
     */
    private CategoryResponse buildCategoryResponse(PostCategory category) {
        CategoryResponse response = categoryMapper.toResponse(category);
        Long postCount = categoryRepository.countPublishedPostsByCategoryId(category.getId());
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
            baseSlug = "category";
        }

        String slug = baseSlug;
        int suffix = 1;

        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }
}
