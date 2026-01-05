-- =====================================================
-- Blog Service Database Schema
-- PostgreSQL Database Schema for CertiMaster Blog Service
-- =====================================================

-- Drop existing tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS comment_reactions CASCADE;
DROP TABLE IF EXISTS post_reactions CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS post_tag_mappings CASCADE;
DROP TABLE IF EXISTS post_category_mappings CASCADE;
DROP TABLE IF EXISTS post_tags CASCADE;
DROP TABLE IF EXISTS post_categories CASCADE;
DROP TABLE IF EXISTS posts CASCADE;

-- =====================================================
-- POSTS TABLE
-- Main table for blog posts with Markdown content
-- =====================================================
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    content TEXT NOT NULL,
    content_html TEXT,
    excerpt TEXT,
    featured_image VARCHAR(500),
    author_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    views_count INT DEFAULT 0,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    reading_time_minutes INT,
    seo_title VARCHAR(255),
    seo_description TEXT,
    seo_keywords TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    CONSTRAINT chk_posts_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT chk_posts_views_count CHECK (views_count >= 0),
    CONSTRAINT chk_posts_likes_count CHECK (likes_count >= 0),
    CONSTRAINT chk_posts_comments_count CHECK (comments_count >= 0),
    CONSTRAINT chk_posts_reading_time CHECK (reading_time_minutes IS NULL OR reading_time_minutes >= 0)
);

COMMENT ON TABLE posts IS 'Blog posts with Markdown content for CertiMaster platform';
COMMENT ON COLUMN posts.content IS 'Raw Markdown content';
COMMENT ON COLUMN posts.content_html IS 'Rendered HTML content (cached)';
COMMENT ON COLUMN posts.status IS 'Post status: DRAFT, PUBLISHED, or ARCHIVED';
COMMENT ON COLUMN posts.reading_time_minutes IS 'Estimated reading time based on word count (200 words/min)';

-- =====================================================
-- POST CATEGORIES TABLE
-- Categories for organizing blog posts
-- =====================================================
CREATE TABLE post_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

COMMENT ON TABLE post_categories IS 'Categories for organizing blog posts (e.g., AWS, Azure, DevOps)';

-- =====================================================
-- POST TAGS TABLE
-- Tags for labeling blog posts
-- =====================================================
CREATE TABLE post_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

COMMENT ON TABLE post_tags IS 'Tags for labeling blog posts with keywords';

-- =====================================================
-- POST CATEGORY MAPPINGS TABLE
-- Many-to-many relationship between posts and categories
-- =====================================================
CREATE TABLE post_category_mappings (
    post_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, category_id),
    CONSTRAINT fk_pcm_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_pcm_category FOREIGN KEY (category_id) REFERENCES post_categories(id) ON DELETE CASCADE
);

COMMENT ON TABLE post_category_mappings IS 'Many-to-many mapping between posts and categories';

-- =====================================================
-- POST TAG MAPPINGS TABLE
-- Many-to-many relationship between posts and tags
-- =====================================================
CREATE TABLE post_tag_mappings (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_ptm_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_ptm_tag FOREIGN KEY (tag_id) REFERENCES post_tags(id) ON DELETE CASCADE
);

COMMENT ON TABLE post_tag_mappings IS 'Many-to-many mapping between posts and tags';

-- =====================================================
-- COMMENTS TABLE
-- Comments on blog posts with hierarchical support
-- =====================================================
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    content TEXT NOT NULL,
    likes_count INT DEFAULT 0,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT chk_comments_likes_count CHECK (likes_count >= 0)
);

COMMENT ON TABLE comments IS 'Comments on blog posts with support for nested replies';
COMMENT ON COLUMN comments.parent_comment_id IS 'Reference to parent comment for nested replies';
COMMENT ON COLUMN comments.is_approved IS 'Moderation flag for comment visibility';

-- =====================================================
-- POST REACTIONS TABLE
-- User reactions to blog posts
-- =====================================================
CREATE TABLE post_reactions (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction_type VARCHAR(50) DEFAULT 'LIKE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pr_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT uq_post_reactions_user_post UNIQUE (post_id, user_id),
    CONSTRAINT chk_pr_reaction_type CHECK (reaction_type IN ('LIKE', 'LOVE', 'HELPFUL'))
);

COMMENT ON TABLE post_reactions IS 'User reactions to blog posts (LIKE, LOVE, HELPFUL)';
COMMENT ON COLUMN post_reactions.reaction_type IS 'Type of reaction: LIKE, LOVE, or HELPFUL';

-- =====================================================
-- COMMENT REACTIONS TABLE
-- User reactions to comments
-- =====================================================
CREATE TABLE comment_reactions (
    id BIGSERIAL PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cr_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT uq_comment_reactions_user_comment UNIQUE (comment_id, user_id)
);

COMMENT ON TABLE comment_reactions IS 'User reactions to comments';

-- =====================================================
-- INDEXES
-- Performance optimization indexes
-- =====================================================

-- Posts indexes
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_published_at ON posts(published_at);
CREATE INDEX idx_posts_slug ON posts(slug);
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_posts_status_published ON posts(status, published_at) WHERE status = 'PUBLISHED';

-- Full-text search index for posts
CREATE INDEX idx_posts_search ON posts USING GIN(to_tsvector('english', title || ' ' || COALESCE(content, '')));

-- Categories indexes
CREATE INDEX idx_post_categories_slug ON post_categories(slug);
CREATE INDEX idx_post_categories_name ON post_categories(name);

-- Tags indexes
CREATE INDEX idx_post_tags_slug ON post_tags(slug);
CREATE INDEX idx_post_tags_name ON post_tags(name);

-- Category mappings indexes
CREATE INDEX idx_pcm_post_id ON post_category_mappings(post_id);
CREATE INDEX idx_pcm_category_id ON post_category_mappings(category_id);

-- Tag mappings indexes
CREATE INDEX idx_ptm_post_id ON post_tag_mappings(post_id);
CREATE INDEX idx_ptm_tag_id ON post_tag_mappings(tag_id);

-- Comments indexes
CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_comments_parent ON comments(parent_comment_id);
CREATE INDEX idx_comments_post_approved ON comments(post_id, is_approved) WHERE is_approved = TRUE;
CREATE INDEX idx_comments_created_at ON comments(created_at);

-- Post reactions indexes
CREATE INDEX idx_post_reactions_post ON post_reactions(post_id);
CREATE INDEX idx_post_reactions_user ON post_reactions(user_id);
CREATE INDEX idx_post_reactions_type ON post_reactions(reaction_type);

-- Comment reactions indexes
CREATE INDEX idx_comment_reactions_comment ON comment_reactions(comment_id);
CREATE INDEX idx_comment_reactions_user ON comment_reactions(user_id);
