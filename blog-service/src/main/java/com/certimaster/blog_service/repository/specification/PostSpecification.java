package com.certimaster.blog_service.repository.specification;

import com.certimaster.blog_service.dto.request.PostSearchRequest;
import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Post queries.
 * Solves PostgreSQL parameter type inference issues with nullable parameters.
 */
public class PostSpecification {

    private PostSpecification() {
        // Utility class
    }

    /**
     * Build specification from search request.
     */
    public static Specification<Post> fromSearchRequest(PostSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search on title and content
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), keyword);
                Predicate contentMatch = cb.like(cb.lower(root.get("content")), keyword);
                predicates.add(cb.or(titleMatch, contentMatch));
            }

            // Status filter
            if (StringUtils.hasText(request.getStatus())) {
                try {
                    PostStatus status = PostStatus.valueOf(request.getStatus().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                    // Invalid status, skip filter
                }
            }

            // Author filter
            if (request.getAuthorId() != null) {
                predicates.add(cb.equal(root.get("authorId"), request.getAuthorId()));
            }

            // Category filter
            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(
                    root.join("categoryMappings").get("category").get("id"),
                    request.getCategoryId()
                ));
            }

            // Tag filter
            if (request.getTagId() != null) {
                predicates.add(cb.equal(
                    root.join("tagMappings").get("tag").get("id"),
                    request.getTagId()
                ));
            }

            // Published date range
            if (request.getPublishedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishedAt"), request.getPublishedFrom()));
            }

            if (request.getPublishedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publishedAt"), request.getPublishedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
