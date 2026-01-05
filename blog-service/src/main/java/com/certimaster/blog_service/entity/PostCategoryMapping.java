package com.certimaster.blog_service.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the many-to-many relationship between Post and PostCategory.
 * 
 * @see Requirements 3.2
 */
@Entity
@Table(name = "post_category_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCategoryMapping {

    @EmbeddedId
    @Builder.Default
    private PostCategoryMappingId id = new PostCategoryMappingId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private PostCategory category;

    public PostCategoryMapping(Post post, PostCategory category) {
        this.post = post;
        this.category = category;
        this.id = new PostCategoryMappingId(post.getId(), category.getId());
    }
}
