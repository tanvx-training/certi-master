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

-- Drop existing tables if they exist (for clean migration)
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop old v1.0 tables if they exist
DROP TABLE IF EXISTS user_permissions CASCADE;
DROP TABLE IF EXISTS role_permission CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS actions CASCADE;
DROP TABLE IF EXISTS features CASCADE;
DROP TABLE IF EXISTS modules CASCADE;
DROP TABLE IF EXISTS data_scopes CASCADE;
DROP TABLE IF EXISTS email_verification_tokens CASCADE;

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
    created_by BIGINT,
    updated_by BIGINT,
    
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED'))
);

COMMENT ON TABLE users IS 'Stores user identity and authentication information';

COMMENT ON COLUMN users.id IS 'Primary key - auto-increment user ID';
COMMENT ON COLUMN users.email IS 'User email address - must be unique';
COMMENT ON COLUMN users.username IS 'User login username - must be unique';
COMMENT ON COLUMN users.password_hash IS 'Hashed password using BCrypt (strength 12)';
COMMENT ON COLUMN users.full_name IS 'Full display name of the user';
COMMENT ON COLUMN users.avatar_url IS 'URL to user profile picture';
COMMENT ON COLUMN users.phone IS 'User phone number';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, LOCKED, SUSPENDED';
COMMENT ON COLUMN users.email_verified IS 'Whether email has been verified';

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
    created_by BIGINT,
    updated_by BIGINT,
    
    CONSTRAINT uk_roles_code UNIQUE (code)
);

COMMENT ON TABLE roles IS 'Defines system roles: ADMIN, INSTRUCTOR, STUDENT';
COMMENT ON COLUMN roles.id IS 'Primary key - auto-increment role ID';
COMMENT ON COLUMN roles.code IS 'Role code (e.g., ADMIN, INSTRUCTOR, STUDENT)';
COMMENT ON COLUMN roles.name IS 'Display name of the role';
COMMENT ON COLUMN roles.description IS 'Detailed description of role purpose';

-- ============================================================================
-- TABLE 3: user_roles (Junction Table)
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

COMMENT ON TABLE user_roles IS 'Junction table for user-role assignments (ManyToMany)';
COMMENT ON COLUMN user_roles.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN user_roles.role_id IS 'Foreign key to roles table';

-- ============================================================================
-- TABLE 4: role_permissions (ElementCollection)
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
COMMENT ON COLUMN role_permissions.role_id IS 'Foreign key to roles table';
COMMENT ON COLUMN role_permissions.permission IS 'Permission string (e.g., exam:create, user:read)';

-- ============================================================================
-- TABLE 5: refresh_tokens
-- ============================================================================
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE refresh_tokens IS 'JWT refresh token management (7-day expiration)';
COMMENT ON COLUMN refresh_tokens.id IS 'Primary key - auto-increment token ID';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN refresh_tokens.token IS 'Refresh token string - must be unique';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration timestamp (7 days from creation)';


-- ============================================================================
-- TABLE 6: email_verification_tokens (Optional - for email verification)
-- ============================================================================
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_email_verification_tokens_token UNIQUE (token),
    CONSTRAINT fk_email_verification_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE email_verification_tokens IS 'Email verification tokens for user registration';

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

-- Email verification tokens indexes
CREATE INDEX idx_email_verification_tokens_token ON email_verification_tokens(token);
CREATE INDEX idx_email_verification_tokens_user_id ON email_verification_tokens(user_id);

-- ============================================================================
-- SEED DATA: Predefined Roles and Permissions
-- ============================================================================

-- Insert predefined roles
INSERT INTO roles (code, name, description) VALUES
('ADMIN', 'Administrator', 'Full system access with all permissions'),
('INSTRUCTOR', 'Instructor', 'Can create and manage exams, questions, and view all results'),
('STUDENT', 'Student', 'Can take exams and view own results');

-- ADMIN permissions (all permissions)
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    ('user:create'), ('user:read'), ('user:update'), ('user:delete'), ('user:read_all'),
    ('role:create'), ('role:read'), ('role:update'), ('role:delete'), ('role:assign'),
    ('exam:create'), ('exam:read'), ('exam:update'), ('exam:delete'),
    ('question:create'), ('question:read'), ('question:update'), ('question:delete'),
    ('certification:create'), ('certification:read'), ('certification:update'), ('certification:delete'),
    ('topic:create'), ('topic:read'), ('topic:update'), ('topic:delete'),
    ('tag:create'), ('tag:read'), ('tag:update'), ('tag:delete'),
    ('result:read'), ('result:read_all'),
    ('system:config'), ('system:audit')
) AS p(permission)
WHERE r.code = 'ADMIN';

-- INSTRUCTOR permissions
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    ('exam:create'), ('exam:read'), ('exam:update'), ('exam:delete'),
    ('question:create'), ('question:read'), ('question:update'), ('question:delete'),
    ('certification:read'),
    ('topic:read'),
    ('tag:read'), ('tag:create'),
    ('result:read'), ('result:read_all')
) AS p(permission)
WHERE r.code = 'INSTRUCTOR';

-- STUDENT permissions
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    ('exam:read'),
    ('question:read'),
    ('certification:read'),
    ('topic:read'),
    ('tag:read'),
    ('result:read')
) AS p(permission)
WHERE r.code = 'STUDENT';

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
