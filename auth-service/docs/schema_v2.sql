-- ============================================================================
-- Auth Service Database Schema v2.0 - Simplified RBAC
-- ============================================================================
-- Description: Simplified database schema for RBAC authorization system
--              Reduced from 11 tables to 5 tables for better performance
--              and maintainability
-- Version: 2.0
-- Database: PostgreSQL 12+
-- ============================================================================

-- ============================================================================
-- SCHEMA OVERVIEW
-- ============================================================================
-- Tables (5 total):
--   1. users           - User identity and authentication
--   2. roles           - System roles (ADMIN, INSTRUCTOR, STUDENT)
--   3. user_roles      - User-role assignments (junction table)
--   4. role_permissions - String-based permissions per role
--   5. refresh_tokens  - JWT refresh token management
--
-- Key improvements over v1.0:
--   - Reduced from 11 to 5 tables
--   - Maximum 2 JOINs for permission checks
--   - String-based permissions (resource:action format)
--   - Permissions embedded in JWT for stateless authorization
--   - Removed unnecessary complexity (modules, features, data_scopes, etc.)
-- ============================================================================

-- ============================================================================
-- TABLE 1: users
-- ============================================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(300) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(300),
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED'))
);

COMMENT ON TABLE users IS 'Stores user identity and authentication information';

-- ============================================================================
-- TABLE 2: roles
-- ============================================================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_roles_code UNIQUE (code)
);

COMMENT ON TABLE roles IS 'Defines system roles: ADMIN, INSTRUCTOR, STUDENT';

-- ============================================================================
-- TABLE 3: user_roles
-- ============================================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE
);

COMMENT ON TABLE user_roles IS 'Junction table for user-role assignments';

-- ============================================================================
-- TABLE 4: role_permissions
-- ============================================================================
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (role_id, permission),
    
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT chk_permission_format CHECK (permission ~ '^[a-z_]+:[a-z_]+$')
);

COMMENT ON TABLE role_permissions IS 'String-based permissions in resource:action format';

-- ============================================================================
-- TABLE 5: refresh_tokens
-- ============================================================================
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE refresh_tokens IS 'JWT refresh token management (7-day expiration)';

-- ============================================================================
-- INDEXES
-- ============================================================================

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_users_email_verified ON users(email_verified);

-- User roles indexes
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Role permissions indexes
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission);

-- Refresh tokens indexes
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_user_expires ON refresh_tokens(user_id, expires_at);

-- ============================================================================
-- PERMISSION FORMAT REFERENCE
-- ============================================================================
-- Format: resource:action
--
-- Resources:
--   user, role, exam, question, certification, topic, tag, result, system
--
-- Actions:
--   create, read, update, delete, read_all, assign, config, audit
--
-- Examples:
--   exam:create    - Create new exams
--   question:read  - View questions
--   result:read_all - View all results (not just own)
--   system:config  - Configure system settings
-- ============================================================================
