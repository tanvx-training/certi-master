-- ============================================================================
-- Auth Service Simplified Database Schema
-- ============================================================================
-- Description: Simplified RBAC schema with 5 tables (down from 11)
--              - users: User identity and authentication
--              - roles: System roles (ADMIN, INSTRUCTOR, STUDENT)
--              - user_roles: Junction table for user-role assignments
--              - role_permissions: String-based permissions per role
--              - refresh_tokens: JWT refresh token management
-- Version: 2.0
-- Database: PostgreSQL 12+
-- ============================================================================

-- Start transaction
BEGIN;

-- ============================================================================
-- SECTION 1: DROP OLD TABLES (in dependency order)
-- ============================================================================
-- Remove complex RBAC tables that are no longer needed

DROP TABLE IF EXISTS user_permissions CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS features CASCADE;
DROP TABLE IF EXISTS modules CASCADE;
DROP TABLE IF EXISTS data_scopes CASCADE;
DROP TABLE IF EXISTS actions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================================================
-- SECTION 2: CREATE SIMPLIFIED TABLES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table 1: users
-- Description: Stores user identity and authentication information
-- ----------------------------------------------------------------------------
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
COMMENT ON COLUMN users.id IS 'Primary key - auto-increment user ID';
COMMENT ON COLUMN users.email IS 'User email address - must be unique';
COMMENT ON COLUMN users.username IS 'User login username - must be unique';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password (strength 12)';
COMMENT ON COLUMN users.full_name IS 'Full display name of the user';
COMMENT ON COLUMN users.avatar_url IS 'URL to user profile picture';
COMMENT ON COLUMN users.phone IS 'User phone number';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, LOCKED, SUSPENDED';
COMMENT ON COLUMN users.email_verified IS 'Whether email has been verified';

-- ----------------------------------------------------------------------------
-- Table 2: roles
-- Description: Defines system roles (ADMIN, INSTRUCTOR, STUDENT)
-- ----------------------------------------------------------------------------
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_roles_code UNIQUE (code)
);

COMMENT ON TABLE roles IS 'Defines system roles for simplified RBAC';
COMMENT ON COLUMN roles.id IS 'Primary key - auto-increment role ID';
COMMENT ON COLUMN roles.code IS 'Role code: ADMIN, INSTRUCTOR, STUDENT';
COMMENT ON COLUMN roles.name IS 'Display name of the role';
COMMENT ON COLUMN roles.description IS 'Description of role purpose and permissions';

-- ----------------------------------------------------------------------------
-- Table 3: user_roles
-- Description: Junction table for user-role assignments (simplified)
-- ----------------------------------------------------------------------------
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE
);

COMMENT ON TABLE user_roles IS 'Junction table assigning roles to users';
COMMENT ON COLUMN user_roles.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN user_roles.role_id IS 'Foreign key to roles table';

-- ----------------------------------------------------------------------------
-- Table 4: role_permissions
-- Description: String-based permissions per role (resource:action format)
-- ----------------------------------------------------------------------------
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (role_id, permission),
    
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT chk_permission_format CHECK (permission ~ '^[a-z_]+:[a-z_]+$')
);

COMMENT ON TABLE role_permissions IS 'String-based permissions assigned to roles';
COMMENT ON COLUMN role_permissions.role_id IS 'Foreign key to roles table';
COMMENT ON COLUMN role_permissions.permission IS 'Permission string in format resource:action (e.g., exam:create)';

-- ----------------------------------------------------------------------------
-- Table 5: refresh_tokens
-- Description: JWT refresh token management
-- ----------------------------------------------------------------------------
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

COMMENT ON TABLE refresh_tokens IS 'Manages JWT refresh tokens for authentication';
COMMENT ON COLUMN refresh_tokens.id IS 'Primary key - auto-increment token ID';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN refresh_tokens.token IS 'Refresh token string - must be unique';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration timestamp (7 days from creation)';

-- ============================================================================
-- SECTION 3: CREATE PERFORMANCE INDEXES
-- ============================================================================

-- Users table indexes
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

COMMIT;

-- ============================================================================
-- SCHEMA SUMMARY
-- ============================================================================
-- Tables: 5 (reduced from 11)
-- 1. users - User identity and authentication
-- 2. roles - System roles (ADMIN, INSTRUCTOR, STUDENT)
-- 3. user_roles - User-role assignments (simple junction table)
-- 4. role_permissions - String-based permissions (resource:action format)
-- 5. refresh_tokens - JWT refresh token management
--
-- Removed tables:
-- - modules (not needed for simple RBAC)
-- - features (not needed for simple RBAC)
-- - actions (replaced by string-based permissions)
-- - resources (replaced by string-based permissions)
-- - data_scopes (not needed - no organizational hierarchy)
-- - user_permissions (no user-level overrides needed)
--
-- Performance improvements:
-- - Maximum 2 JOINs to check permissions (user -> user_roles -> role_permissions)
-- - Permissions embedded in JWT for stateless authorization
-- - Simplified indexes for common queries
-- ============================================================================
