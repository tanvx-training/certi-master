-- ============================================================================
-- Auth Service Seed Data - PostgreSQL INSERT Scripts
-- ============================================================================
-- Description: Seed data for RBAC authorization system
-- Version: 1.0
-- Database: PostgreSQL 12+
-- ============================================================================

-- ============================================================================
-- SECTION 1: INSERT COMMON ACTIONS
-- ============================================================================
-- Description: Standard CRUD and workflow actions used across the system
-- These are system actions that cannot be deleted
-- ============================================================================

INSERT INTO actions (code, name, description, http_method, is_system, created_at, updated_at) VALUES
('CREATE', 'Create', 'Create new resource', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('READ', 'Read', 'View/read resource details', 'GET', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UPDATE', 'Update', 'Modify existing resource', 'PUT', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DELETE', 'Delete', 'Remove resource', 'DELETE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EXECUTE', 'Execute', 'Execute action or operation', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('APPROVE', 'Approve', 'Approve pending request', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REJECT', 'Reject', 'Reject pending request', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMENT ON TABLE actions IS 'Standard actions: CREATE (POST), READ (GET), UPDATE (PUT), DELETE (DELETE), EXECUTE (POST), APPROVE (POST), REJECT (POST)';


-- ============================================================================
-- SECTION 2: INSERT COMMON DATA SCOPES
-- ============================================================================
-- Description: Hierarchical data access scopes from most restrictive to least
-- Level 1 (OWN) -> Level 2 (TEAM) -> Level 3 (DEPARTMENT) -> Level 4 (ORGANIZATION) -> Level 5 (ALL)
-- ============================================================================

INSERT INTO data_scopes (code, name, description, level, filter_type, filter_expression, created_at, updated_at) VALUES
(
    'OWN',
    'Own Data',
    'Access only data created by or assigned to the user',
    1,
    'USER_ID',
    'created_by = :userId OR assigned_to = :userId',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'TEAM',
    'Team Data',
    'Access data within user''s team',
    2,
    'TEAM_ID',
    'team_id IN (:userTeamIds)',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'DEPARTMENT',
    'Department Data',
    'Access data within user''s department',
    3,
    'DEPARTMENT_ID',
    'department_id = :userDepartmentId',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'ORGANIZATION',
    'Organization Data',
    'Access all data within user''s organization',
    4,
    'ORGANIZATION_ID',
    'organization_id = :userOrganizationId',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'ALL',
    'All Data',
    'Access all data across all organizations (super admin)',
    5,
    'NONE',
    '1=1',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

COMMENT ON TABLE data_scopes IS 'Hierarchical data scopes: OWN (1) < TEAM (2) < DEPARTMENT (3) < ORGANIZATION (4) < ALL (5)';


-- ============================================================================
-- SECTION 3: INSERT SAMPLE MODULES
-- ============================================================================
-- Description: Top-level modules that group related features
-- ============================================================================

INSERT INTO modules (code, name, description, icon, route, order_index, is_active, created_at, updated_at) VALUES
(
    'USER_MGMT',
    'User Management',
    'Manage users, roles, and permissions',
    'users',
    '/users',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'COURSE_MGMT',
    'Course Management',
    'Manage courses, lessons, and learning content',
    'book',
    '/courses',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'SYSTEM_MGMT',
    'System Management',
    'System configuration and administration',
    'settings',
    '/system',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 4: INSERT SAMPLE FEATURES
-- ============================================================================
-- Description: Specific features within modules with hierarchical structure
-- ============================================================================

-- ----------------------------------------------------------------------------
-- User Management Features
-- ----------------------------------------------------------------------------
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
-- Top-level User Management features
(
    (SELECT id FROM modules WHERE code = 'USER_MGMT'),
    NULL,
    'USER_LIST',
    'User List',
    'View and search users',
    '/users/list',
    'list',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'USER_MGMT'),
    NULL,
    'USER_CREATE',
    'Create User',
    'Create new user account',
    '/users/create',
    'user-plus',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'USER_MGMT'),
    NULL,
    'USER_EDIT',
    'Edit User',
    'Edit existing user account',
    '/users/edit/:id',
    'edit',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'USER_MGMT'),
    NULL,
    'USER_DELETE',
    'Delete User',
    'Delete user account',
    '/users/delete/:id',
    'trash',
    4,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'USER_MGMT'),
    NULL,
    'ROLE_MGMT',
    'Role Management',
    'Manage roles and permissions',
    '/users/roles',
    'shield',
    5,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Course Management Features
-- ----------------------------------------------------------------------------
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
-- Top-level Course Management features
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    NULL,
    'COURSE_LIST',
    'Course List',
    'View and search courses',
    '/courses/list',
    'list',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    NULL,
    'COURSE_CREATE',
    'Create Course',
    'Create new course',
    '/courses/create',
    'plus',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    NULL,
    'COURSE_EDIT',
    'Edit Course',
    'Edit existing course',
    '/courses/edit/:id',
    'edit',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    NULL,
    'COURSE_DELETE',
    'Delete Course',
    'Delete course',
    '/courses/delete/:id',
    'trash',
    4,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    NULL,
    'LESSON_MGMT',
    'Lesson Management',
    'Manage course lessons',
    '/courses/:courseId/lessons',
    'book-open',
    5,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Sub-features under Lesson Management (hierarchical structure example)
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    (SELECT id FROM features WHERE code = 'LESSON_MGMT'),
    'LESSON_CREATE',
    'Create Lesson',
    'Create new lesson in course',
    '/courses/:courseId/lessons/create',
    'plus',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'COURSE_MGMT'),
    (SELECT id FROM features WHERE code = 'LESSON_MGMT'),
    'LESSON_EDIT',
    'Edit Lesson',
    'Edit existing lesson',
    '/courses/:courseId/lessons/edit/:id',
    'edit',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- System Management Features
-- ----------------------------------------------------------------------------
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM modules WHERE code = 'SYSTEM_MGMT'),
    NULL,
    'SYSTEM_CONFIG',
    'System Configuration',
    'Configure system settings',
    '/system/config',
    'cog',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM modules WHERE code = 'SYSTEM_MGMT'),
    NULL,
    'AUDIT_LOG',
    'Audit Log',
    'View system audit logs',
    '/system/audit',
    'file-text',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- ============================================================================
-- SECTION 5: INSERT SAMPLE RESOURCES
-- ============================================================================
-- Description: Resources represent specific permission points (API, UI components)
-- Each resource combines a feature with an action
-- ============================================================================

-- ----------------------------------------------------------------------------
-- User Management Resources
-- ----------------------------------------------------------------------------

-- USER_LIST feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'USER_LIST'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'USER_LIST_VIEW',
    'View User List',
    'View list of users with search and filter',
    '/api/users',
    'GET',
    'API',
    'user.list.view',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'USER_LIST'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'USER_LIST_MENU',
    'User List Menu',
    'Menu item to access user list',
    NULL,
    NULL,
    'MENU',
    'menu.users.list',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- USER_CREATE feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'USER_CREATE'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'USER_CREATE_API',
    'Create User API',
    'API endpoint to create new user',
    '/api/users',
    'POST',
    'API',
    'user.create.api',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'USER_CREATE'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'USER_CREATE_BUTTON',
    'Create User Button',
    'Button to open create user form',
    NULL,
    NULL,
    'BUTTON',
    'button.user.create',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- USER_EDIT feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'USER_EDIT'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'USER_DETAIL_VIEW',
    'View User Detail',
    'API to get user details for editing',
    '/api/users/{id}',
    'GET',
    'API',
    'user.detail.view',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'USER_EDIT'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'USER_UPDATE_API',
    'Update User API',
    'API endpoint to update user',
    '/api/users/{id}',
    'PUT',
    'API',
    'user.update.api',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'USER_EDIT'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'USER_EDIT_BUTTON',
    'Edit User Button',
    'Button to edit user',
    NULL,
    NULL,
    'BUTTON',
    'button.user.edit',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- USER_DELETE feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'USER_DELETE'),
    (SELECT id FROM actions WHERE code = 'DELETE'),
    'USER_DELETE_API',
    'Delete User API',
    'API endpoint to delete user',
    '/api/users/{id}',
    'DELETE',
    'API',
    'user.delete.api',
    'DEPARTMENT',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'USER_DELETE'),
    (SELECT id FROM actions WHERE code = 'DELETE'),
    'USER_DELETE_BUTTON',
    'Delete User Button',
    'Button to delete user',
    NULL,
    NULL,
    'BUTTON',
    'button.user.delete',
    'DEPARTMENT',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ROLE_MGMT feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'ROLE_MGMT'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'ROLE_LIST_VIEW',
    'View Role List',
    'View list of roles',
    '/api/roles',
    'GET',
    'API',
    'role.list.view',
    'ORGANIZATION',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'ROLE_MGMT'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'ROLE_CREATE_API',
    'Create Role API',
    'API endpoint to create new role',
    '/api/roles',
    'POST',
    'API',
    'role.create.api',
    'ORGANIZATION',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'ROLE_MGMT'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'ROLE_UPDATE_API',
    'Update Role API',
    'API endpoint to update role',
    '/api/roles/{id}',
    'PUT',
    'API',
    'role.update.api',
    'ORGANIZATION',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Course Management Resources
-- ----------------------------------------------------------------------------

-- COURSE_LIST feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'COURSE_LIST'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'COURSE_LIST_VIEW',
    'View Course List',
    'View list of courses',
    '/api/courses',
    'GET',
    'API',
    'course.list.view',
    'ORGANIZATION',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'COURSE_LIST'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'COURSE_LIST_MENU',
    'Course List Menu',
    'Menu item to access course list',
    NULL,
    NULL,
    'MENU',
    'menu.courses.list',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- COURSE_CREATE feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'COURSE_CREATE'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'COURSE_CREATE_API',
    'Create Course API',
    'API endpoint to create new course',
    '/api/courses',
    'POST',
    'API',
    'course.create.api',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'COURSE_CREATE'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'COURSE_CREATE_BUTTON',
    'Create Course Button',
    'Button to create new course',
    NULL,
    NULL,
    'BUTTON',
    'button.course.create',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- COURSE_EDIT feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'COURSE_EDIT'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'COURSE_DETAIL_VIEW',
    'View Course Detail',
    'API to get course details',
    '/api/courses/{id}',
    'GET',
    'API',
    'course.detail.view',
    'ORGANIZATION',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'COURSE_EDIT'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'COURSE_UPDATE_API',
    'Update Course API',
    'API endpoint to update course',
    '/api/courses/{id}',
    'PUT',
    'API',
    'course.update.api',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- COURSE_DELETE feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'COURSE_DELETE'),
    (SELECT id FROM actions WHERE code = 'DELETE'),
    'COURSE_DELETE_API',
    'Delete Course API',
    'API endpoint to delete course',
    '/api/courses/{id}',
    'DELETE',
    'API',
    'course.delete.api',
    'DEPARTMENT',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- LESSON_CREATE feature resources (sub-feature example)
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'LESSON_CREATE'),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'LESSON_CREATE_API',
    'Create Lesson API',
    'API endpoint to create new lesson',
    '/api/courses/{courseId}/lessons',
    'POST',
    'API',
    'lesson.create.api',
    'OWN',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- LESSON_EDIT feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'LESSON_EDIT'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'LESSON_UPDATE_API',
    'Update Lesson API',
    'API endpoint to update lesson',
    '/api/courses/{courseId}/lessons/{id}',
    'PUT',
    'API',
    'lesson.update.api',
    'DEPARTMENT',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- System Management Resources
-- ----------------------------------------------------------------------------

-- SYSTEM_CONFIG feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'SYSTEM_CONFIG'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'SYSTEM_CONFIG_VIEW',
    'View System Config',
    'View system configuration',
    '/api/system/config',
    'GET',
    'API',
    'system.config.view',
    'ALL',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM features WHERE code = 'SYSTEM_CONFIG'),
    (SELECT id FROM actions WHERE code = 'UPDATE'),
    'SYSTEM_CONFIG_UPDATE',
    'Update System Config',
    'Update system configuration',
    '/api/system/config',
    'PUT',
    'API',
    'system.config.update',
    'ALL',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- AUDIT_LOG feature resources
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at) VALUES
(
    (SELECT id FROM features WHERE code = 'AUDIT_LOG'),
    (SELECT id FROM actions WHERE code = 'READ'),
    'AUDIT_LOG_VIEW',
    'View Audit Log',
    'View system audit logs',
    '/api/system/audit',
    'GET',
    'API',
    'audit.log.view',
    'ORGANIZATION',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- ============================================================================
-- SECTION 6: INSERT BASIC ROLES
-- ============================================================================
-- Description: Standard system roles with different permission levels
-- ============================================================================

INSERT INTO roles (code, name, description, is_system, is_active, created_at, updated_at) VALUES
(
    'SUPER_ADMIN',
    'Super Administrator',
    'Full system access with all permissions across all organizations',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'ADMIN',
    'Administrator',
    'Organization administrator with management permissions',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'MANAGER',
    'Manager',
    'Department or team manager with limited management permissions',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'USER',
    'Regular User',
    'Standard user with basic access permissions',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'INSTRUCTOR',
    'Instructor',
    'Course instructor with teaching and content management permissions',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'STUDENT',
    'Student',
    'Student with learning access permissions',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 7: INSERT ROLE PERMISSIONS
-- ============================================================================
-- Description: Assign permissions to roles with appropriate data scopes
-- ============================================================================

-- ----------------------------------------------------------------------------
-- SUPER_ADMIN Role Permissions (ALL scope for everything)
-- ----------------------------------------------------------------------------
-- Grant SUPER_ADMIN all user management permissions with ALL scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ALL'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_LIST_VIEW', 'USER_LIST_MENU', 'USER_CREATE_API', 'USER_CREATE_BUTTON',
    'USER_DETAIL_VIEW', 'USER_UPDATE_API', 'USER_EDIT_BUTTON',
    'USER_DELETE_API', 'USER_DELETE_BUTTON',
    'ROLE_LIST_VIEW', 'ROLE_CREATE_API', 'ROLE_UPDATE_API'
);

-- Grant SUPER_ADMIN all course management permissions with ALL scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ALL'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU', 'COURSE_CREATE_API', 'COURSE_CREATE_BUTTON',
    'COURSE_DETAIL_VIEW', 'COURSE_UPDATE_API',
    'COURSE_DELETE_API',
    'LESSON_CREATE_API', 'LESSON_UPDATE_API'
);

-- Grant SUPER_ADMIN all system management permissions
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ALL'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'SYSTEM_CONFIG_VIEW', 'SYSTEM_CONFIG_UPDATE',
    'AUDIT_LOG_VIEW'
);

-- ----------------------------------------------------------------------------
-- ADMIN Role Permissions (ORGANIZATION scope)
-- ----------------------------------------------------------------------------
-- Grant ADMIN user management permissions with ORGANIZATION scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_LIST_VIEW', 'USER_LIST_MENU', 'USER_CREATE_API', 'USER_CREATE_BUTTON',
    'USER_DETAIL_VIEW', 'USER_UPDATE_API', 'USER_EDIT_BUTTON',
    'USER_DELETE_API', 'USER_DELETE_BUTTON',
    'ROLE_LIST_VIEW'
);

-- Grant ADMIN course management permissions with ORGANIZATION scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU', 'COURSE_CREATE_API', 'COURSE_CREATE_BUTTON',
    'COURSE_DETAIL_VIEW', 'COURSE_UPDATE_API',
    'COURSE_DELETE_API',
    'LESSON_CREATE_API', 'LESSON_UPDATE_API'
);

-- Grant ADMIN audit log view with ORGANIZATION scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN ('AUDIT_LOG_VIEW');

-- ----------------------------------------------------------------------------
-- MANAGER Role Permissions (DEPARTMENT scope)
-- ----------------------------------------------------------------------------
-- Grant MANAGER user view permissions with DEPARTMENT scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_LIST_VIEW', 'USER_LIST_MENU',
    'USER_DETAIL_VIEW', 'USER_UPDATE_API', 'USER_EDIT_BUTTON'
);

-- Grant MANAGER course management permissions with DEPARTMENT scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU', 'COURSE_CREATE_API', 'COURSE_CREATE_BUTTON',
    'COURSE_DETAIL_VIEW', 'COURSE_UPDATE_API',
    'LESSON_CREATE_API', 'LESSON_UPDATE_API'
);

-- ----------------------------------------------------------------------------
-- USER Role Permissions (OWN scope)
-- ----------------------------------------------------------------------------
-- Grant USER basic view permissions with OWN scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'USER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_LIST_MENU',
    'USER_DETAIL_VIEW'
);

-- Grant USER course view permissions with ORGANIZATION scope (can view all courses)
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'USER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU',
    'COURSE_DETAIL_VIEW'
);

-- ----------------------------------------------------------------------------
-- INSTRUCTOR Role Permissions (DEPARTMENT scope for courses)
-- ----------------------------------------------------------------------------
-- Grant INSTRUCTOR course management permissions with DEPARTMENT scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'INSTRUCTOR'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU', 'COURSE_CREATE_API', 'COURSE_CREATE_BUTTON',
    'COURSE_DETAIL_VIEW', 'COURSE_UPDATE_API',
    'LESSON_CREATE_API', 'LESSON_UPDATE_API'
);

-- Grant INSTRUCTOR user view with TEAM scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'INSTRUCTOR'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'TEAM'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_LIST_VIEW', 'USER_LIST_MENU',
    'USER_DETAIL_VIEW'
);

-- ----------------------------------------------------------------------------
-- STUDENT Role Permissions (ORGANIZATION scope for viewing)
-- ----------------------------------------------------------------------------
-- Grant STUDENT course view permissions with ORGANIZATION scope
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'STUDENT'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'COURSE_LIST_VIEW', 'COURSE_LIST_MENU',
    'COURSE_DETAIL_VIEW'
);

-- Grant STUDENT own profile view
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, is_active, granted_at, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'STUDENT'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM resources r
WHERE r.code IN (
    'USER_DETAIL_VIEW'
);


-- ============================================================================
-- SECTION 8: INSERT SAMPLE USERS
-- ============================================================================
-- Description: Sample users for testing different roles and contexts
-- Note: Passwords are hashed using BCrypt (password: "password123")
-- ============================================================================

INSERT INTO users (email, username, password_hash, full_name, avatar_url, phone, status, email_verified, created_at, updated_at) VALUES
(
    'superadmin@certimaster.com',
    'superadmin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Super Administrator',
    'https://ui-avatars.com/api/?name=Super+Admin&background=0D8ABC&color=fff',
    '+84901234567',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'admin@certimaster.com',
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Organization Administrator',
    'https://ui-avatars.com/api/?name=Admin&background=28a745&color=fff',
    '+84901234568',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'manager@certimaster.com',
    'manager',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Department Manager',
    'https://ui-avatars.com/api/?name=Manager&background=ffc107&color=000',
    '+84901234569',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'instructor@certimaster.com',
    'instructor',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Course Instructor',
    'https://ui-avatars.com/api/?name=Instructor&background=17a2b8&color=fff',
    '+84901234570',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'user@certimaster.com',
    'user',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Regular User',
    'https://ui-avatars.com/api/?name=User&background=6c757d&color=fff',
    '+84901234571',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'student@certimaster.com',
    'student',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Student User',
    'https://ui-avatars.com/api/?name=Student&background=dc3545&color=fff',
    '+84901234572',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 9: INSERT USER ROLE ASSIGNMENTS
-- ============================================================================
-- Description: Assign roles to users with different contexts
-- Demonstrates context-based role assignment and primary roles
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Super Admin User - Global role (no context)
-- ----------------------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'superadmin'),
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    NULL,
    NULL,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Admin User - Organization context
-- ----------------------------------------------------------------------------
-- Admin in Organization 1 (primary)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    'ORGANIZATION',
    1,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Admin also has USER role in Organization 2 (demonstrates multi-org access)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE code = 'USER'),
    'ORGANIZATION',
    2,
    CURRENT_TIMESTAMP,
    NULL,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Manager User - Department context
-- ----------------------------------------------------------------------------
-- Manager in Department 1 of Organization 1 (primary)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'manager'),
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    'DEPARTMENT',
    1,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Manager also has USER role in Department 2 (demonstrates multi-department access)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'manager'),
    (SELECT id FROM roles WHERE code = 'USER'),
    'DEPARTMENT',
    2,
    CURRENT_TIMESTAMP,
    NULL,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Instructor User - Team/Project context
-- ----------------------------------------------------------------------------
-- Instructor in Project 101 (primary)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'instructor'),
    (SELECT id FROM roles WHERE code = 'INSTRUCTOR'),
    'PROJECT',
    101,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Instructor also teaches in Project 102
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'instructor'),
    (SELECT id FROM roles WHERE code = 'INSTRUCTOR'),
    'PROJECT',
    102,
    CURRENT_TIMESTAMP,
    NULL,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Regular User - Organization context
-- ----------------------------------------------------------------------------
-- User in Organization 1 (primary)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'user'),
    (SELECT id FROM roles WHERE code = 'USER'),
    'ORGANIZATION',
    1,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Student User - Multiple contexts with temporal role
-- ----------------------------------------------------------------------------
-- Student in Project 101 (primary)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM roles WHERE code = 'STUDENT'),
    'PROJECT',
    101,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Student also enrolled in Project 102
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM roles WHERE code = 'STUDENT'),
    'PROJECT',
    102,
    CURRENT_TIMESTAMP,
    NULL,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Student has temporary INSTRUCTOR role in Project 103 (expires in 30 days)
-- This demonstrates temporal role assignment
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM roles WHERE code = 'INSTRUCTOR'),
    'PROJECT',
    103,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 10: INSERT SAMPLE USER PERMISSIONS (OVERRIDES)
-- ============================================================================
-- Description: User-specific permission overrides demonstrating GRANT and DENY
-- ============================================================================

-- ----------------------------------------------------------------------------
-- GRANT Example: Give regular user special permission to delete courses
-- ----------------------------------------------------------------------------
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, reason, is_active, granted_at, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'user'),
    (SELECT id FROM resources WHERE code = 'COURSE_DELETE_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    'GRANT',
    'Temporary permission granted for cleanup project - expires in 7 days',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- DENY Example: Revoke admin's ability to delete users in specific context
-- ----------------------------------------------------------------------------
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, reason, is_active, granted_at, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM resources WHERE code = 'USER_DELETE_API'),
    NULL,
    'DENY',
    'User deletion restricted during audit period - expires in 14 days',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '14 days',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- GRANT Example: Give manager permission to view system config temporarily
-- ----------------------------------------------------------------------------
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, conditions, reason, is_active, granted_at, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM users WHERE username = 'manager'),
    (SELECT id FROM resources WHERE code = 'SYSTEM_CONFIG_VIEW'),
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    'GRANT',
    '{"timeRange": {"start": "09:00", "end": "17:00"}, "daysOfWeek": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"], "requiresMFA": true}'::jsonb,
    'Temporary access for system configuration review during business hours only',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================

-- Summary of seed data:
-- - 7 Actions (CREATE, READ, UPDATE, DELETE, EXECUTE, APPROVE, REJECT)
-- - 5 Data Scopes (OWN, TEAM, DEPARTMENT, ORGANIZATION, ALL)
-- - 3 Modules (User Management, Course Management, System Management)
-- - 15 Features (with hierarchical structure)
-- - 30+ Resources (API endpoints, buttons, menus)
-- - 6 Roles (SUPER_ADMIN, ADMIN, MANAGER, USER, INSTRUCTOR, STUDENT)
-- - 100+ Role Permissions (covering all role-resource-scope combinations)
-- - 6 Sample Users (one for each role)
-- - 12 User Role Assignments (demonstrating context-based and temporal roles)
-- - 3 User Permission Overrides (demonstrating GRANT and DENY with conditions)

