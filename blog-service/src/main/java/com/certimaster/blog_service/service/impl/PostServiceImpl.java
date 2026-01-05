package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.dto.mapper.PostMapper;
import com.certimaster.blog_service.dto.request.PostRequest;
import com.certimaster.blog_service.dto.request.PostSearchRequest;
import com.certimaster.blog_service.dto.response.PostDetailResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.ReactionResponse;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostReaction;
import com.certimaster.blog_service.entity.PostStatus;
import com.certimaster.blog_service.repository.PostReactionRepository;
import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.repository.specification.PostSpecification;
import com.certimaster.blog_service.service.MarkdownService;
import com.certimaster.blog_service.service.PostService;
import com.certimaster.blog_service.service.SlugService;
import com.certimaster.blog_service.util.ReadingTimeCalculator;
import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.exception.business.ForbiddenException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostMapper postMapper;
    private final SlugService slugService;
    private final MarkdownService markdownService;
    private final ReadingTimeCalculator readingTimeCalculator;

    @Override
    public PostResponse createPost(PostRequest request, Long authorId) {
        log.debug("Creating post with title: {} for author: {}", request.getTitle(), authorId);

        // Create post entity from request
        Post post = postMapper.toEntity(request);
        
        // Set author
        post.setAuthorId(authorId);
        
        // Generate unique slug from title (Requirement 2.1, 2.2)
        String slug = slugService.generateUniqueSlug(request.getTitle());
        post.setSlug(slug);
        
        // Set DRAFT status (Requirement 1.1)
        post.setStatus(PostStatus.DRAFT);
        
        // Calculate reading time (Requirement 1.5)
        int readingTime = readingTimeCalculator.calculateReadingTime(request.getContent());
        post.setReadingTimeMinutes(readingTime);
        
        // Render Markdown to HTML and cache it
        String contentHtml = markdownService.renderToHtml(request.getContent());
        post.setContentHtml(contentHtml);
        
        // Apply SEO defaults if not provided (Requirement 9.2)
        applySeoDefaults(post, request);
        
        // Initialize counters
        post.setViewsCount(0);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        
        // Save post
        Post savedPost = postRepository.save(post);
        log.info("Created post with id: {} and slug: {}", savedPost.getId(), savedPost.getSlug());
        
        return postMapper.toResponse(savedPost);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest request, Long userId) {
        log.debug("Updating post: {} by user: {}", id, userId);

        Post post = findPostById(id);
        
        // Check authorization - only author can update
        checkPostOwnership(post, userId);
        
        // Update fields from request (slug is preserved - Requirement 1.2)
        postMapper.updateEntity(post, request);
        
        // Recalculate reading time (Requirement 1.5)
        int readingTime = readingTimeCalculator.calculateReadingTime(request.getContent());
        post.setReadingTimeMinutes(readingTime);
        
        // Re-render Markdown to HTML
        String contentHtml = markdownService.renderToHtml(request.getContent());
        post.setContentHtml(contentHtml);
        
        // Apply SEO defaults if not provided (Requirement 9.2)
        applySeoDefaults(post, request);
        
        // Save updated post
        Post savedPost = postRepository.save(post);
        log.info("Updated post: {}", savedPost.getId());
        
        return postMapper.toResponse(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostBySlug(String slug, Long currentUserId) {
        log.debug("Getting post by slug: {}", slug);

        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> ResourceNotFoundException.byField("Post", "slug", slug));
        
        return buildDetailResponse(post, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(Long id, Long currentUserId) {
        log.debug("Getting post by id: {}", id);

        Post post = findPostById(id);
        return buildDetailResponse(post, currentUserId);
    }

    @Override
    public void deletePost(Long id, Long userId) {
        log.debug("Deleting post: {} by user: {}", id, userId);

        Post post = findPostById(id);
        
        // Check authorization - only author can delete
        checkPostOwnership(post, userId);
        
        postRepository.delete(post);
        log.info("Deleted post: {}", id);
    }

    @Override
    public PostResponse publishPost(Long id, Long userId) {
        log.debug("Publishing post: {} by user: {}", id, userId);

        Post post = findPostById(id);
        
        // Check authorization - only author can publish
        checkPostOwnership(post, userId);
        
        // Set status to PUBLISHED and timestamp (Requirement 1.3)
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        
        Post savedPost = postRepository.save(post);
        log.info("Published post: {}", savedPost.getId());
        
        return postMapper.toResponse(savedPost);
    }

    @Override
    public PostResponse archivePost(Long id, Long userId) {
        log.debug("Archiving post: {} by user: {}", id, userId);

        Post post = findPostById(id);
        
        // Check authorization - only author can archive
        checkPostOwnership(post, userId);
        
        // Set status to ARCHIVED (Requirement 1.4)
        post.setStatus(PostStatus.ARCHIVED);
        
        Post savedPost = postRepository.save(post);
        log.info("Archived post: {}", savedPost.getId());
        
        return postMapper.toResponse(savedPost);
    }

    @Override
    public void incrementViewCount(Long id) {
        log.debug("Incrementing view count for post: {}", id);
        
        // Verify post exists
        if (!postRepository.existsById(id)) {
            throw ResourceNotFoundException.byId("Post", id);
        }
        
        // Increment view count (Requirement 8.1)
        postRepository.incrementViewCount(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<PostResponse> searchPosts(PostSearchRequest request) {
        log.debug("Searching posts with request: {}", request);

        // Build pageable with sorting
        Pageable pageable = buildPageable(request);
        
        // Build specification for dynamic query
        Specification<Post> spec = PostSpecification.fromSearchRequest(request);

        Page<Post> postPage = postRepository.findAll(spec, pageable);
        
        // Map to response DTOs
        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        
        return PageDto.of(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<PostResponse> getPostsByAuthor(Long authorId, int page, int size) {
        log.debug("Getting posts by author: {}", authorId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findByAuthorId(authorId, pageable);
        
        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        
        return PageDto.of(responsePage);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Find post by ID or throw ResourceNotFoundException.
     */
    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", id));
    }

    /**
     * Check if user is the owner of the post.
     * Throws ForbiddenException if not.
     */
    private void checkPostOwnership(Post post, Long userId) {
        if (!post.getAuthorId().equals(userId)) {
            throw ForbiddenException.resourceOwnerOnly();
        }
    }

    /**
     * Build PostDetailResponse with current user's reaction status.
     */
    private PostDetailResponse buildDetailResponse(Post post, Long currentUserId) {
        PostDetailResponse response = postMapper.toDetailResponse(post);
        
        // Ensure HTML content is rendered (Requirement 1.7)
        if (response.getContentHtml() == null || response.getContentHtml().isEmpty()) {
            String contentHtml = markdownService.renderToHtml(post.getContent());
            response.setContentHtml(contentHtml);
        }
        
        // Get current user's reaction if authenticated (Requirement 6.5)
        if (currentUserId != null) {
            Optional<PostReaction> reaction = postReactionRepository
                    .findByPostIdAndUserId(post.getId(), currentUserId);
            
            reaction.ifPresent(r -> {
                ReactionResponse reactionResponse = ReactionResponse.builder()
                        .id(r.getId())
                        .reactionType(r.getReactionType().name())
                        .userId(r.getUserId())
                        .build();
                response.setCurrentUserReaction(reactionResponse);
            });
        }
        
        return response;
    }

    /**
     * Apply SEO defaults if not provided (Requirement 9.2).
     * - seo_title defaults to title
     * - seo_description defaults to excerpt
     */
    private void applySeoDefaults(Post post, PostRequest request) {
        // SEO title defaults to post title
        if (request.getSeoTitle() == null || request.getSeoTitle().isBlank()) {
            post.setSeoTitle(request.getTitle());
        }
        
        // SEO description defaults to excerpt
        if (request.getSeoDescription() == null || request.getSeoDescription().isBlank()) {
            post.setSeoDescription(request.getExcerpt());
        }
    }

    /**
     * Build Pageable from search request.
     */
    private Pageable buildPageable(PostSearchRequest request) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        
        return PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10,
                Sort.by(direction, sortBy)
        );
    }
}
