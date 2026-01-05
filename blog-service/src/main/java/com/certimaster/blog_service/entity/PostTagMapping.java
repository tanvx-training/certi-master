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
 * Entity representing the many-to-many relationship between Post and PostTag.
 * 
 * @see Requirements 4.1
 */
@Entity
@Table(name = "post_tag_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTagMapping {

    @EmbeddedId
    @Builder.Default
    private PostTagMappingId id = new PostTagMappingId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private PostTag tag;

    public PostTagMapping(Post post, PostTag tag) {
        this.post = post;
        this.tag = tag;
        this.id = new PostTagMappingId(post.getId(), tag.getId());
    }
}
