-- ============================================================================
-- Auth Service Seed Data - Roles and Permissions
-- ============================================================================
-- Description: Seed data for simplified RBAC system
--              - 3 predefined roles: ADMIN, INSTRUCTOR, STUDENT
--              - String-based permissions in resource:action format
-- Version: 2.0
-- Database: PostgreSQL 12+
-- ============================================================================

-- Start transaction
BEGIN;

-- ============================================================================
-- SECTION 1: INSERT PREDEFINED ROLES
-- ============================================================================

INSERT INTO roles (code, name, description, created_at, updated_at) VALUES
(
    'ADMIN',
    'Administrator',
    'Full system access with all permissions. Can manage users, roles, exams, questions, and view all results.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'INSTRUCTOR',
    'Instructor',
    'Can create and manage exams, questions, and view student results. Cannot manage users or system settings.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'STUDENT',
    'Student',
    'Can take exams, view questions during exams, and view own results. Default role for new registrations.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 2: INSERT PERMISSIONS FOR ADMIN ROLE
-- ============================================================================
-- ADMIN has all permissions

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    -- User management permissions
    ('user:create'),
    ('user:read'),
    ('user:update'),
    ('user:delete'),
    
    -- Role management permissions
    ('role:create'),
    ('role:read'),
    ('role:update'),
    ('role:delete'),
    ('role:assign'),
    
    -- Exam management permissions
    ('exam:create'),
    ('exam:read'),
    ('exam:update'),
    ('exam:delete'),
    
    -- Question management permissions
    ('question:create'),
    ('question:read'),
    ('question:update'),
    ('question:delete'),
    
    -- Certification management permissions
    ('certification:create'),
    ('certification:read'),
    ('certification:update'),
    ('certification:delete'),
    
    -- Topic management permissions
    ('topic:create'),
    ('topic:read'),
    ('topic:update'),
    ('topic:delete'),
    
    -- Tag management permissions
    ('tag:create'),
    ('tag:read'),
    ('tag:update'),
    ('tag:delete'),
    
    -- Result management permissions
    ('result:read'),
    ('result:read_all'),
    ('result:delete'),
    
    -- System management permissions
    ('system:config'),
    ('system:audit')
) AS p(permission)
WHERE r.code = 'ADMIN';

-- ============================================================================
-- SECTION 3: INSERT PERMISSIONS FOR INSTRUCTOR ROLE
-- ============================================================================
-- INSTRUCTOR can manage exams, questions, and view all results

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    -- Exam management permissions
    ('exam:create'),
    ('exam:read'),
    ('exam:update'),
    ('exam:delete'),
    
    -- Question management permissions
    ('question:create'),
    ('question:read'),
    ('question:update'),
    ('question:delete'),
    
    -- Certification read permission
    ('certification:read'),
    
    -- Topic management permissions
    ('topic:create'),
    ('topic:read'),
    ('topic:update'),
    
    -- Tag management permissions
    ('tag:create'),
    ('tag:read'),
    ('tag:update'),
    
    -- Result permissions (can view all student results)
    ('result:read'),
    ('result:read_all')
) AS p(permission)
WHERE r.code = 'INSTRUCTOR';

-- ============================================================================
-- SECTION 4: INSERT PERMISSIONS FOR STUDENT ROLE
-- ============================================================================
-- STUDENT has limited read permissions and can view own results

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (VALUES
    -- Exam read permission (to take exams)
    ('exam:read'),
    
    -- Question read permission (during exams)
    ('question:read'),
    
    -- Certification read permission
    ('certification:read'),
    
    -- Topic read permission
    ('topic:read'),
    
    -- Tag read permission
    ('tag:read'),
    
    -- Result read permission (own results only)
    ('result:read')
) AS p(permission)
WHERE r.code = 'STUDENT';

-- ============================================================================
-- SECTION 5: VERIFICATION QUERIES
-- ============================================================================

DO $$
DECLARE
    admin_perms INTEGER;
    instructor_perms INTEGER;
    student_perms INTEGER;
BEGIN
    SELECT COUNT(*) INTO admin_perms 
    FROM role_permissions rp 
    JOIN roles r ON rp.role_id = r.id 
    WHERE r.code = 'ADMIN';
    
    SELECT COUNT(*) INTO instructor_perms 
    FROM role_permissions rp 
    JOIN roles r ON rp.role_id = r.id 
    WHERE r.code = 'INSTRUCTOR';
    
    SELECT COUNT(*) INTO student_perms 
    FROM role_permissions rp 
    JOIN roles r ON rp.role_id = r.id 
    WHERE r.code = 'STUDENT';
    
    RAISE NOTICE '============================================';
    RAISE NOTICE 'SEED DATA SUMMARY';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Roles created: 3 (ADMIN, INSTRUCTOR, STUDENT)';
    RAISE NOTICE 'ADMIN permissions: %', admin_perms;
    RAISE NOTICE 'INSTRUCTOR permissions: %', instructor_perms;
    RAISE NOTICE 'STUDENT permissions: %', student_perms;
    RAISE NOTICE '============================================';
END $$;

COMMIT;

-- ============================================================================
-- PERMISSION REFERENCE
-- ============================================================================
-- Permission format: resource:action
--
-- Resources:
--   user        - User management
--   role        - Role management
--   exam        - Exam management
--   question    - Question management
--   certification - Certification management
--   topic       - Topic management
--   tag         - Tag management
--   result      - Exam results
--   system      - System configuration
--
-- Actions:
--   create      - Create new resource
--   read        - View/read resource
--   update      - Modify existing resource
--   delete      - Remove resource
--   read_all    - View all resources (not just own)
--   assign      - Assign resource to another entity
--   config      - Configure system settings
--   audit       - View audit logs
--
-- Role-Permission Matrix:
-- +----------------+-------+------------+---------+
-- | Permission     | ADMIN | INSTRUCTOR | STUDENT |
-- +----------------+-------+------------+---------+
-- | user:*         |   ✓   |     ✗      |    ✗    |
-- | role:*         |   ✓   |     ✗      |    ✗    |
-- | exam:create    |   ✓   |     ✓      |    ✗    |
-- | exam:read      |   ✓   |     ✓      |    ✓    |
-- | exam:update    |   ✓   |     ✓      |    ✗    |
-- | exam:delete    |   ✓   |     ✓      |    ✗    |
-- | question:*     |   ✓   |     ✓      |  read   |
-- | certification:*|   ✓   |   read     |  read   |
-- | topic:*        |   ✓   |  CRU       |  read   |
-- | tag:*          |   ✓   |  CRU       |  read   |
-- | result:read    |   ✓   |     ✓      |    ✓    |
-- | result:read_all|   ✓   |     ✓      |    ✗    |
-- | result:delete  |   ✓   |     ✗      |    ✗    |
-- | system:*       |   ✓   |     ✗      |    ✗    |
-- +----------------+-------+------------+---------+
-- ============================================================================
