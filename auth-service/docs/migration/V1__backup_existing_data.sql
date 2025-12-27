-- ============================================================================
-- Auth Service Migration - Step 1: Backup Existing Data
-- ============================================================================
-- Description: Backup script to export users, roles, user_roles data before
--              migrating to simplified schema
-- Version: 1.0
-- Database: PostgreSQL 12+
-- Usage: Run this script BEFORE applying the new simplified schema
-- ============================================================================

-- Start transaction for safety
BEGIN;

-- ============================================================================
-- SECTION 1: CREATE BACKUP TABLES
-- ============================================================================
-- Create backup tables with timestamp suffix for traceability

-- Backup users table
CREATE TABLE IF NOT EXISTS users_backup AS
SELECT 
    id,
    email,
    username,
    password_hash,
    full_name,
    avatar_url,
    phone,
    status,
    email_verified,
    created_at,
    updated_at
FROM users;

-- Backup roles table
CREATE TABLE IF NOT EXISTS roles_backup AS
SELECT 
    id,
    code,
    name,
    description,
    is_system,
    is_active,
    created_at,
    updated_at
FROM roles;

-- Backup user_roles table (simplified - only user_id and role_id needed)
CREATE TABLE IF NOT EXISTS user_roles_backup AS
SELECT 
    id,
    user_id,
    role_id,
    is_primary,
    created_at,
    updated_at
FROM user_roles;

-- Backup refresh_tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens_backup AS
SELECT 
    id,
    user_id,
    token,
    expires_at,
    created_at
FROM refresh_tokens;

-- ============================================================================
-- SECTION 2: EXPORT DATA COUNTS FOR VERIFICATION
-- ============================================================================
-- Create a verification table to track migration

CREATE TABLE IF NOT EXISTS migration_verification (
    table_name VARCHAR(100) PRIMARY KEY,
    record_count BIGINT,
    backup_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Record counts for verification
INSERT INTO migration_verification (table_name, record_count)
SELECT 'users', COUNT(*) FROM users
ON CONFLICT (table_name) DO UPDATE SET 
    record_count = EXCLUDED.record_count,
    backup_timestamp = CURRENT_TIMESTAMP;

INSERT INTO migration_verification (table_name, record_count)
SELECT 'roles', COUNT(*) FROM roles
ON CONFLICT (table_name) DO UPDATE SET 
    record_count = EXCLUDED.record_count,
    backup_timestamp = CURRENT_TIMESTAMP;

INSERT INTO migration_verification (table_name, record_count)
SELECT 'user_roles', COUNT(*) FROM user_roles
ON CONFLICT (table_name) DO UPDATE SET 
    record_count = EXCLUDED.record_count,
    backup_timestamp = CURRENT_TIMESTAMP;

INSERT INTO migration_verification (table_name, record_count)
SELECT 'refresh_tokens', COUNT(*) FROM refresh_tokens
ON CONFLICT (table_name) DO UPDATE SET 
    record_count = EXCLUDED.record_count,
    backup_timestamp = CURRENT_TIMESTAMP;

-- ============================================================================
-- SECTION 3: DISPLAY BACKUP SUMMARY
-- ============================================================================

DO $$
DECLARE
    users_count BIGINT;
    roles_count BIGINT;
    user_roles_count BIGINT;
    refresh_tokens_count BIGINT;
BEGIN
    SELECT COUNT(*) INTO users_count FROM users_backup;
    SELECT COUNT(*) INTO roles_count FROM roles_backup;
    SELECT COUNT(*) INTO user_roles_count FROM user_roles_backup;
    SELECT COUNT(*) INTO refresh_tokens_count FROM refresh_tokens_backup;
    
    RAISE NOTICE '============================================';
    RAISE NOTICE 'BACKUP SUMMARY';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Users backed up: %', users_count;
    RAISE NOTICE 'Roles backed up: %', roles_count;
    RAISE NOTICE 'User-Role assignments backed up: %', user_roles_count;
    RAISE NOTICE 'Refresh tokens backed up: %', refresh_tokens_count;
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Backup completed successfully!';
    RAISE NOTICE 'You can now proceed with schema migration.';
    RAISE NOTICE '============================================';
END $$;

COMMIT;

-- ============================================================================
-- ROLLBACK SCRIPT (Run if you need to restore from backup)
-- ============================================================================
-- To restore data from backup tables, run the following commands:
--
-- BEGIN;
-- 
-- -- Restore users
-- INSERT INTO users (id, email, username, password_hash, full_name, avatar_url, 
--                    phone, status, email_verified, created_at, updated_at)
-- SELECT id, email, username, password_hash, full_name, avatar_url,
--        phone, status, email_verified, created_at, updated_at
-- FROM users_backup
-- ON CONFLICT (id) DO NOTHING;
--
-- -- Restore roles  
-- INSERT INTO roles (id, code, name, description, created_at, updated_at)
-- SELECT id, code, name, description, created_at, updated_at
-- FROM roles_backup
-- ON CONFLICT (id) DO NOTHING;
--
-- -- Restore user_roles
-- INSERT INTO user_roles (user_id, role_id)
-- SELECT user_id, role_id
-- FROM user_roles_backup
-- ON CONFLICT DO NOTHING;
--
-- COMMIT;
-- ============================================================================
