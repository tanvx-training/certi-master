-- ============================================================================
-- Auth Service Complete Migration Script
-- ============================================================================
-- Description: Complete idempotent migration from complex RBAC (11 tables)
--              to simplified RBAC (5 tables)
-- Version: 2.0
-- Database: PostgreSQL 12+
-- Usage: psql -U postgres -d your_database -f complete_migration.sql
-- ============================================================================

-- Start transaction
BEGIN;

-- ============================================================================
-- STEP 1: DROP OLD TABLES
-- ============================================================================
-- Remove all existing tables in dependency order

DROP TABLE IF EXISTS user_permissions CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS features CASCADE;
DROP TABLE IF EXISTS modules CASCADE;
DROP TABLE IF EXISTS data_scopes CASCADE;
DROP TABLE IF EXISTS actions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS email_verification_tokens CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop backup tables if they exist
DROP TABLE IF EXISTS users_backup CASCADE;
DROP TABLE IF EXISTS roles_backup CASCADE;
DROP TABLE IF EXISTS user_roles_backup CASCADE;
DROP TABLE IF EXISTS refresh_tokens_backup CASCADE;
DROP TABLE IF EXISTS migration_verification CASCADE;

-- ============================================================================
-- STEP 2: CREATE SIMPLIFIED SCHEMA (5 TABLES)
-- ============================================================================

-- Table 1: users
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

-- Table 2: roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_roles_code UNIQUE (code)
);

-- Table 3: user_roles (junction table)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE
);

-- Table 4: role_permissions (string-based)
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (role_id, permission),
    
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT chk_permission_format CHECK (permission ~ '^[a-z_]+:[a-z_]+$')
);

-- Table 5: refresh_tokens
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

-- ============================================================================
-- STEP 3: CREATE INDEXES
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
-- STEP 4: SEED ROLES
-- ============================================================================

INSERT INTO roles (code, name, description) VALUES
('ADMIN', 'Administrator', 'Full system access with all permissions'),
('INSTRUCTOR', 'Instructor', 'Can manage exams, questions, and view student results'),
('STUDENT', 'Student', 'Can take exams and view own results. Default role for new users');

-- ============================================================================
-- STEP 5: SEED ADMIN PERMISSIONS
-- ============================================================================

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    ('user:create'), ('user:read'), ('user:update'), ('user:delete'),
    ('role:create'), ('role:read'), ('role:update'), ('role:delete'), ('role:assign'),
    ('exam:create'), ('exam:read'), ('exam:update'), ('exam:delete'),
    ('question:create'), ('question:read'), ('question:update'), ('question:delete'),
    ('certification:create'), ('certification:read'), ('certification:update'), ('certification:delete'),
    ('topic:create'), ('topic:read'), ('topic:update'), ('topic:delete'),
    ('tag:create'), ('tag:read'), ('tag:update'), ('tag:delete'),
    ('result:read'), ('result:read_all'), ('result:delete'),
    ('system:config'), ('system:audit')
) AS p(permission)
WHERE r.code = 'ADMIN';

-- ============================================================================
-- STEP 6: SEED INSTRUCTOR PERMISSIONS
-- ============================================================================

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    ('exam:create'), ('exam:read'), ('exam:update'), ('exam:delete'),
    ('question:create'), ('question:read'), ('question:update'), ('question:delete'),
    ('certification:read'),
    ('topic:create'), ('topic:read'), ('topic:update'),
    ('tag:create'), ('tag:read'), ('tag:update'),
    ('result:read'), ('result:read_all')
) AS p(permission)
WHERE r.code = 'INSTRUCTOR';

-- ============================================================================
-- STEP 7: SEED STUDENT PERMISSIONS
-- ============================================================================

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
-- STEP 8: VERIFICATION
-- ============================================================================

DO $$
DECLARE
    table_count INTEGER;
    role_count INTEGER;
    admin_perms INTEGER;
    instructor_perms INTEGER;
    student_perms INTEGER;
BEGIN
    SELECT COUNT(*) INTO table_count 
    FROM information_schema.tables 
    WHERE table_schema = 'public' 
    AND table_name IN ('users', 'roles', 'user_roles', 'role_permissions', 'refresh_tokens');
    
    SELECT COUNT(*) INTO role_count FROM roles;
    
    SELECT COUNT(*) INTO admin_perms 
    FROM role_permissions rp JOIN roles r ON rp.role_id = r.id WHERE r.code = 'ADMIN';
    
    SELECT COUNT(*) INTO instructor_perms 
    FROM role_permissions rp JOIN roles r ON rp.role_id = r.id WHERE r.code = 'INSTRUCTOR';
    
    SELECT COUNT(*) INTO student_perms 
    FROM role_permissions rp JOIN roles r ON rp.role_id = r.id WHERE r.code = 'STUDENT';
    
    RAISE NOTICE '============================================';
    RAISE NOTICE 'MIGRATION COMPLETE';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Tables created: % (expected: 5)', table_count;
    RAISE NOTICE 'Roles created: % (expected: 3)', role_count;
    RAISE NOTICE 'ADMIN permissions: %', admin_perms;
    RAISE NOTICE 'INSTRUCTOR permissions: %', instructor_perms;
    RAISE NOTICE 'STUDENT permissions: %', student_perms;
    RAISE NOTICE '============================================';
    
    IF table_count != 5 THEN
        RAISE EXCEPTION 'Migration failed: Expected 5 tables, got %', table_count;
    END IF;
    
    IF role_count != 3 THEN
        RAISE EXCEPTION 'Migration failed: Expected 3 roles, got %', role_count;
    END IF;
END $$;

COMMIT;
