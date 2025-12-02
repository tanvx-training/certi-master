-- ============================================================================
-- Auth Service Complete Database Setup Script
-- ============================================================================
-- Description: Complete idempotent script for RBAC authorization system
--              Combines DDL (schema) and DML (seed data) in a single transaction
-- Version: 1.0
-- Database: PostgreSQL 12+
-- Usage: psql -U postgres -d your_database -f complete_setup.sql
-- ============================================================================

-- Start transaction
BEGIN;

-- ============================================================================
-- SECTION 1: DROP EXISTING TABLES (Idempotent - can be re-executed safely)
-- ============================================================================
-- Drop tables in reverse dependency order to avoid foreign key constraint errors
-- ============================================================================

DROP TABLE IF EXISTS user_permissions CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS features CASCADE;
DROP TABLE IF EXISTS modules CASCADE;
DROP TABLE IF EXISTS data_scopes CASCADE;
DROP TABLE IF EXISTS actions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================================================
-- SECTION 2: CREATE TABLES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: users
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
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE users IS 'Stores user identity and authentication information';
COMMENT ON COLUMN users.id IS 'Primary key - auto-increment user ID';
COMMENT ON COLUMN users.email IS 'User email address - must be unique';
COMMENT ON COLUMN users.username IS 'User login username - must be unique';
COMMENT ON COLUMN users.password_hash IS 'Hashed password using BCrypt or Argon2';
COMMENT ON COLUMN users.full_name IS 'Full display name of the user';
COMMENT ON COLUMN users.avatar_url IS 'URL to user profile picture';
COMMENT ON COLUMN users.phone IS 'User phone number';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, LOCKED, SUSPENDED';
COMMENT ON COLUMN users.email_verified IS 'Whether email has been verified';

-- ----------------------------------------------------------------------------
-- Table: refresh_tokens
-- Description: Manages JWT refresh tokens for authentication
-- ----------------------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE refresh_tokens IS 'Manages JWT refresh tokens for user authentication';
COMMENT ON COLUMN refresh_tokens.id IS 'Primary key - auto-increment token ID';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN refresh_tokens.token IS 'Refresh token string - must be unique';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration timestamp';

-- ----------------------------------------------------------------------------
-- Table: roles
-- Description: Defines system roles (ADMIN, USER, MANAGER, etc.)
-- ----------------------------------------------------------------------------
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE roles IS 'Defines system roles for RBAC';
COMMENT ON COLUMN roles.id IS 'Primary key - auto-increment role ID';
COMMENT ON COLUMN roles.code IS 'Role code used in application logic (e.g., ADMIN, USER)';
COMMENT ON COLUMN roles.name IS 'Display name of the role';
COMMENT ON COLUMN roles.description IS 'Detailed description of role purpose and permissions';
COMMENT ON COLUMN roles.is_system IS 'System role flag - cannot be deleted or modified';
COMMENT ON COLUMN roles.is_active IS 'Whether role is currently active';

-- ----------------------------------------------------------------------------
-- Table: user_roles
-- Description: Junction table assigning roles to users with context
-- ----------------------------------------------------------------------------
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    context_type VARCHAR(50),
    context_id BIGINT,
    valid_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until TIMESTAMP,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE user_roles IS 'Assigns roles to users with context and temporal constraints';
COMMENT ON COLUMN user_roles.id IS 'Primary key - auto-increment assignment ID';
COMMENT ON COLUMN user_roles.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN user_roles.role_id IS 'Foreign key to roles table';
COMMENT ON COLUMN user_roles.context_type IS 'Context type: ORGANIZATION, PROJECT, TEAM, etc.';
COMMENT ON COLUMN user_roles.context_id IS 'ID of the context entity';
COMMENT ON COLUMN user_roles.valid_from IS 'Timestamp when role assignment becomes valid';
COMMENT ON COLUMN user_roles.valid_until IS 'Timestamp when role assignment expires (NULL = no expiry)';
COMMENT ON COLUMN user_roles.is_primary IS 'Whether this is the primary role for the user';

-- ----------------------------------------------------------------------------
-- Table: modules
-- Description: Top-level grouping of related features
-- ----------------------------------------------------------------------------
CREATE TABLE modules (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    route VARCHAR(200),
    order_index INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE modules IS 'Top-level grouping of related features (e.g., User Management, Course Management)';
COMMENT ON COLUMN modules.id IS 'Primary key - auto-increment module ID';
COMMENT ON COLUMN modules.code IS 'Module code used in application (e.g., USER_MGMT, COURSE_MGMT)';
COMMENT ON COLUMN modules.name IS 'Display name of the module';
COMMENT ON COLUMN modules.description IS 'Detailed description of module purpose';
COMMENT ON COLUMN modules.icon IS 'Icon class or name for UI display';
COMMENT ON COLUMN modules.route IS 'Base route path for frontend navigation';
COMMENT ON COLUMN modules.order_index IS 'Display order in menu (lower = higher priority)';
COMMENT ON COLUMN modules.is_active IS 'Whether module is currently active';

-- ----------------------------------------------------------------------------
-- Table: features
-- Description: Specific features within modules
-- ----------------------------------------------------------------------------
CREATE TABLE features (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL,
    parent_feature_id BIGINT,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    route VARCHAR(200),
    icon VARCHAR(50),
    order_index INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE features IS 'Specific features within modules, supports hierarchical structure';
COMMENT ON COLUMN features.id IS 'Primary key - auto-increment feature ID';
COMMENT ON COLUMN features.module_id IS 'Foreign key to modules table';
COMMENT ON COLUMN features.parent_feature_id IS 'Foreign key to parent feature for nested menu structure';
COMMENT ON COLUMN features.code IS 'Feature code unique within module';
COMMENT ON COLUMN features.name IS 'Display name of the feature';
COMMENT ON COLUMN features.description IS 'Detailed description of feature functionality';
COMMENT ON COLUMN features.route IS 'Specific route path for this feature';
COMMENT ON COLUMN features.icon IS 'Icon class or name for UI display';
COMMENT ON COLUMN features.order_index IS 'Display order within module (lower = higher priority)';
COMMENT ON COLUMN features.is_active IS 'Whether feature is currently active';

-- ----------------------------------------------------------------------------
-- Table: actions
-- Description: Defines available actions (CREATE, READ, UPDATE, DELETE, etc.)
-- ----------------------------------------------------------------------------
CREATE TABLE actions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    http_method VARCHAR(10),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE actions IS 'Defines available actions that can be performed on resources';
COMMENT ON COLUMN actions.id IS 'Primary key - auto-increment action ID';
COMMENT ON COLUMN actions.code IS 'Action code (e.g., CREATE, READ, UPDATE, DELETE, EXECUTE, APPROVE)';
COMMENT ON COLUMN actions.name IS 'Display name of the action';
COMMENT ON COLUMN actions.description IS 'Detailed description of action purpose';
COMMENT ON COLUMN actions.http_method IS 'Corresponding HTTP method (GET, POST, PUT, DELETE, PATCH)';
COMMENT ON COLUMN actions.is_system IS 'System action flag - cannot be deleted';

-- ----------------------------------------------------------------------------
-- Table: resources
-- Description: Specific resources that can be permission-controlled
-- ----------------------------------------------------------------------------
CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    feature_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    api_path_pattern VARCHAR(500),
    http_method VARCHAR(10),
    component_type VARCHAR(50),
    component_key VARCHAR(100),
    default_scope VARCHAR(20) NOT NULL DEFAULT 'OWN',
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE resources IS 'Specific resources that can be permission-controlled (API, UI components, etc.)';
COMMENT ON COLUMN resources.id IS 'Primary key - auto-increment resource ID';
COMMENT ON COLUMN resources.feature_id IS 'Foreign key to features table';
COMMENT ON COLUMN resources.action_id IS 'Foreign key to actions table';
COMMENT ON COLUMN resources.code IS 'Resource code (e.g., USER_CREATE, USER_VIEW_LIST)';
COMMENT ON COLUMN resources.name IS 'Display name of the resource';
COMMENT ON COLUMN resources.description IS 'Detailed description of resource';
COMMENT ON COLUMN resources.api_path_pattern IS 'API path pattern for matching requests (e.g., /api/users/{id})';
COMMENT ON COLUMN resources.http_method IS 'HTTP method for API resources';
COMMENT ON COLUMN resources.component_type IS 'Type of component: API, BUTTON, MENU, PAGE';
COMMENT ON COLUMN resources.component_key IS 'Key identifier for frontend components';
COMMENT ON COLUMN resources.default_scope IS 'Default data scope for this resource';
COMMENT ON COLUMN resources.requires_approval IS 'Whether action requires approval workflow';
COMMENT ON COLUMN resources.is_active IS 'Whether resource is currently active';

-- ----------------------------------------------------------------------------
-- Table: data_scopes
-- Description: Defines data access scopes (OWN, TEAM, DEPARTMENT, etc.)
-- ----------------------------------------------------------------------------
CREATE TABLE data_scopes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    level INTEGER NOT NULL,
    filter_type VARCHAR(50),
    filter_expression TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE data_scopes IS 'Defines hierarchical data access scopes';
COMMENT ON COLUMN data_scopes.id IS 'Primary key - auto-increment scope ID';
COMMENT ON COLUMN data_scopes.code IS 'Scope code (e.g., OWN, TEAM, DEPARTMENT, ORGANIZATION, ALL)';
COMMENT ON COLUMN data_scopes.name IS 'Display name of the scope';
COMMENT ON COLUMN data_scopes.description IS 'Detailed description of scope coverage';
COMMENT ON COLUMN data_scopes.level IS 'Hierarchical level (1=OWN, 2=TEAM, 3=DEPT, 4=ORG, 5=ALL)';
COMMENT ON COLUMN data_scopes.filter_type IS 'Type of filter to apply (USER_ID, TEAM_ID, DEPARTMENT_ID, ORG_ID)';
COMMENT ON COLUMN data_scopes.filter_expression IS 'SQL WHERE clause template for filtering data';

-- ----------------------------------------------------------------------------
-- Table: role_permissions
-- Description: Assigns permissions to roles with data scope
-- ----------------------------------------------------------------------------
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    data_scope_id BIGINT,
    conditions JSONB,
    granted_by BIGINT,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE role_permissions IS 'Assigns resource permissions to roles with data scope constraints';
COMMENT ON COLUMN role_permissions.id IS 'Primary key - auto-increment permission ID';
COMMENT ON COLUMN role_permissions.role_id IS 'Foreign key to roles table';
COMMENT ON COLUMN role_permissions.resource_id IS 'Foreign key to resources table';
COMMENT ON COLUMN role_permissions.data_scope_id IS 'Foreign key to data_scopes table - limits data access';
COMMENT ON COLUMN role_permissions.conditions IS 'Additional conditions in JSON format (time range, IP whitelist, etc.)';
COMMENT ON COLUMN role_permissions.granted_by IS 'User ID who granted this permission';
COMMENT ON COLUMN role_permissions.granted_at IS 'Timestamp when permission was granted';
COMMENT ON COLUMN role_permissions.expires_at IS 'Timestamp when permission expires (NULL = no expiry)';
COMMENT ON COLUMN role_permissions.is_active IS 'Whether permission is currently active';

-- ----------------------------------------------------------------------------
-- Table: user_permissions
-- Description: User-specific permission overrides (GRANT or DENY)
-- ----------------------------------------------------------------------------
CREATE TABLE user_permissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    data_scope_id BIGINT,
    permission_type VARCHAR(20) NOT NULL,
    conditions JSONB,
    granted_by BIGINT,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    reason TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updatedBy VARCHAR(50)
);

COMMENT ON TABLE user_permissions IS 'User-specific permission overrides that take precedence over role permissions';
COMMENT ON COLUMN user_permissions.id IS 'Primary key - auto-increment permission ID';
COMMENT ON COLUMN user_permissions.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN user_permissions.resource_id IS 'Foreign key to resources table';
COMMENT ON COLUMN user_permissions.data_scope_id IS 'Foreign key to data_scopes table - limits data access';
COMMENT ON COLUMN user_permissions.permission_type IS 'Permission type: GRANT (add permission) or DENY (revoke permission)';
COMMENT ON COLUMN user_permissions.conditions IS 'Additional conditions in JSON format (time range, IP whitelist, etc.)';
COMMENT ON COLUMN user_permissions.granted_by IS 'User ID who granted this permission';
COMMENT ON COLUMN user_permissions.granted_at IS 'Timestamp when permission was granted';
COMMENT ON COLUMN user_permissions.expires_at IS 'Timestamp when permission expires (NULL = no expiry)';
COMMENT ON COLUMN user_permissions.reason IS 'Justification for granting special permission';
COMMENT ON COLUMN user_permissions.is_active IS 'Whether permission is currently active';

-- ============================================================================
-- SECTION 3: FOREIGN KEY CONSTRAINTS
-- ============================================================================

-- Foreign Keys for: refresh_tokens
ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Foreign Keys for: user_roles
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id)
    REFERENCES roles(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Foreign Keys for: features
ALTER TABLE features
    ADD CONSTRAINT fk_features_module
    FOREIGN KEY (module_id)
    REFERENCES modules(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE features
    ADD CONSTRAINT fk_features_parent
    FOREIGN KEY (parent_feature_id)
    REFERENCES features(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Foreign Keys for: resources
ALTER TABLE resources
    ADD CONSTRAINT fk_resources_feature
    FOREIGN KEY (feature_id)
    REFERENCES features(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE resources
    ADD CONSTRAINT fk_resources_action
    FOREIGN KEY (action_id)
    REFERENCES actions(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- Foreign Keys for: role_permissions
ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role
    FOREIGN KEY (role_id)
    REFERENCES roles(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_resource
    FOREIGN KEY (resource_id)
    REFERENCES resources(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_data_scope
    FOREIGN KEY (data_scope_id)
    REFERENCES data_scopes(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Foreign Keys for: user_permissions
ALTER TABLE user_permissions
    ADD CONSTRAINT fk_user_permissions_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE user_permissions
    ADD CONSTRAINT fk_user_permissions_resource
    FOREIGN KEY (resource_id)
    REFERENCES resources(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE user_permissions
    ADD CONSTRAINT fk_user_permissions_data_scope
    FOREIGN KEY (data_scope_id)
    REFERENCES data_scopes(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- ============================================================================
-- SECTION 4: UNIQUE CONSTRAINTS
-- ============================================================================

-- Unique Constraints for: users
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);

-- Unique Constraints for: refresh_tokens
ALTER TABLE refresh_tokens ADD CONSTRAINT uk_refresh_tokens_token UNIQUE (token);

-- Unique Constraints for: roles
ALTER TABLE roles ADD CONSTRAINT uk_roles_code UNIQUE (code);

-- Unique Constraints for: user_roles
ALTER TABLE user_roles ADD CONSTRAINT uk_user_roles_user_role_context UNIQUE (user_id, role_id, context_type, context_id);

-- Unique Constraints for: modules
ALTER TABLE modules ADD CONSTRAINT uk_modules_code UNIQUE (code);

-- Unique Constraints for: features
ALTER TABLE features ADD CONSTRAINT uk_features_module_code UNIQUE (module_id, code);

-- Unique Constraints for: actions
ALTER TABLE actions ADD CONSTRAINT uk_actions_code UNIQUE (code);

-- Unique Constraints for: resources
ALTER TABLE resources ADD CONSTRAINT uk_resources_code UNIQUE (code);
-- Note: Removed uk_resources_feature_action constraint because a feature can have
-- multiple resources with the same action (e.g., API endpoint and menu item both use READ)

-- Unique Constraints for: data_scopes
ALTER TABLE data_scopes ADD CONSTRAINT uk_data_scopes_code UNIQUE (code);
ALTER TABLE data_scopes ADD CONSTRAINT uk_data_scopes_level UNIQUE (level);

-- Unique Constraints for: role_permissions
ALTER TABLE role_permissions ADD CONSTRAINT uk_role_permissions_role_resource_scope UNIQUE (role_id, resource_id, data_scope_id);

-- Unique Constraints for: user_permissions
ALTER TABLE user_permissions ADD CONSTRAINT uk_user_permissions_user_resource_type UNIQUE (user_id, resource_id, permission_type);

-- ============================================================================
-- SECTION 5: PERFORMANCE INDEXES
-- ============================================================================

-- Indexes for Foreign Keys
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_features_module_id ON features(module_id);
CREATE INDEX idx_features_parent_feature_id ON features(parent_feature_id);
CREATE INDEX idx_resources_feature_id ON resources(feature_id);
CREATE INDEX idx_resources_action_id ON resources(action_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_resource_id ON role_permissions(resource_id);
CREATE INDEX idx_role_permissions_data_scope_id ON role_permissions(data_scope_id);
CREATE INDEX idx_user_permissions_user_id ON user_permissions(user_id);
CREATE INDEX idx_user_permissions_resource_id ON user_permissions(resource_id);
CREATE INDEX idx_user_permissions_data_scope_id ON user_permissions(data_scope_id);

-- Indexes for Frequently Queried Columns
CREATE INDEX idx_users_status ON users(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_created_at ON users(created_at);

CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_user_expires ON refresh_tokens(user_id, expires_at);

CREATE INDEX idx_roles_is_active ON roles(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_roles_is_system ON roles(is_system);

CREATE INDEX idx_user_roles_context ON user_roles(context_type, context_id);
CREATE INDEX idx_user_roles_valid_from ON user_roles(valid_from);
CREATE INDEX idx_user_roles_valid_until ON user_roles(valid_until);
CREATE INDEX idx_user_roles_is_primary ON user_roles(is_primary) WHERE is_primary = TRUE;
CREATE INDEX idx_user_roles_user_context ON user_roles(user_id, context_type, context_id);

CREATE INDEX idx_modules_is_active ON modules(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_modules_order_index ON modules(order_index);

CREATE INDEX idx_features_is_active ON features(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_features_order_index ON features(order_index);
CREATE INDEX idx_features_module_order ON features(module_id, order_index);

CREATE INDEX idx_actions_http_method ON actions(http_method);
CREATE INDEX idx_actions_is_system ON actions(is_system);

CREATE INDEX idx_resources_is_active ON resources(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_resources_component_type ON resources(component_type);
CREATE INDEX idx_resources_api_path ON resources(api_path_pattern);
CREATE INDEX idx_resources_http_method ON resources(http_method);
CREATE INDEX idx_resources_feature_active ON resources(feature_id, is_active);

CREATE INDEX idx_data_scopes_level ON data_scopes(level);

CREATE INDEX idx_role_permissions_is_active ON role_permissions(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_role_permissions_expires_at ON role_permissions(expires_at);
CREATE INDEX idx_role_permissions_granted_at ON role_permissions(granted_at);
CREATE INDEX idx_role_permissions_role_active ON role_permissions(role_id, is_active);

CREATE INDEX idx_user_permissions_is_active ON user_permissions(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_user_permissions_expires_at ON user_permissions(expires_at);
CREATE INDEX idx_user_permissions_permission_type ON user_permissions(permission_type);
CREATE INDEX idx_user_permissions_user_active ON user_permissions(user_id, is_active);
CREATE INDEX idx_user_permissions_user_type ON user_permissions(user_id, permission_type);

-- ============================================================================
-- SECTION 6: JSONB INDEXES
-- ============================================================================

-- GIN indexes for efficient JSONB queries
CREATE INDEX idx_role_permissions_conditions_gin ON role_permissions USING GIN (conditions);
CREATE INDEX idx_user_permissions_conditions_gin ON user_permissions USING GIN (conditions);

-- JSONB path indexes for common query patterns
CREATE INDEX idx_role_permissions_conditions_time_range 
    ON role_permissions USING BTREE ((conditions->'timeRange'->>'start'), (conditions->'timeRange'->>'end'))
    WHERE conditions ? 'timeRange';

CREATE INDEX idx_role_permissions_conditions_ip_whitelist 
    ON role_permissions USING GIN ((conditions->'ipWhitelist'))
    WHERE conditions ? 'ipWhitelist';

CREATE INDEX idx_user_permissions_conditions_time_range 
    ON user_permissions USING BTREE ((conditions->'timeRange'->>'start'), (conditions->'timeRange'->>'end'))
    WHERE conditions ? 'timeRange';

CREATE INDEX idx_user_permissions_conditions_ip_whitelist 
    ON user_permissions USING GIN ((conditions->'ipWhitelist'))
    WHERE conditions ? 'ipWhitelist';

CREATE INDEX idx_user_permissions_conditions_max_amount 
    ON user_permissions USING BTREE (((conditions->>'maxAmount')::numeric))
    WHERE conditions ? 'maxAmount';

-- ============================================================================
-- SECTION 7: SEED DATA - ACTIONS
-- ============================================================================

INSERT INTO actions (code, name, description, http_method, is_system, created_at, updated_at) VALUES
('CREATE', 'Create', 'Create new resource', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('READ', 'Read', 'View/read resource details', 'GET', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UPDATE', 'Update', 'Modify existing resource', 'PUT', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DELETE', 'Delete', 'Remove resource', 'DELETE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EXECUTE', 'Execute', 'Execute action or operation', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('APPROVE', 'Approve', 'Approve pending request', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REJECT', 'Reject', 'Reject pending request', 'POST', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================================
-- SECTION 8: SEED DATA - DATA SCOPES
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

-- ============================================================================
-- SECTION 9: SEED DATA - MODULES
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
-- SECTION 10: SEED DATA - FEATURES
-- ============================================================================

-- User Management Features
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
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

-- Course Management Features
INSERT INTO features (module_id, parent_feature_id, code, name, description, route, icon, order_index, is_active, created_at, updated_at) VALUES
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

-- Sub-features under Lesson Management
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

-- System Management Features
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
-- SECTION 11: SEED DATA - RESOURCES (User Management)
-- ============================================================================

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

-- ============================================================================
-- SECTION 12: SEED DATA - RESOURCES (Course Management)
-- ============================================================================

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

-- LESSON_CREATE feature resources
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

-- ============================================================================
-- SECTION 13: SEED DATA - RESOURCES (System Management)
-- ============================================================================

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
-- SECTION 14: SEED DATA - ROLES
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
-- SECTION 15: SEED DATA - ROLE PERMISSIONS (SUPER_ADMIN)
-- ============================================================================

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

-- ============================================================================
-- SECTION 16: SEED DATA - ROLE PERMISSIONS (ADMIN)
-- ============================================================================

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

-- ============================================================================
-- SECTION 17: SEED DATA - ROLE PERMISSIONS (MANAGER, USER, INSTRUCTOR, STUDENT)
-- ============================================================================

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

-- Grant USER course view permissions with ORGANIZATION scope
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
-- SECTION 18: SEED DATA - SAMPLE USERS
-- ============================================================================
-- Note: Password for all users is "password123" (BCrypt hash)
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
-- SECTION 19: SEED DATA - USER ROLE ASSIGNMENTS
-- ============================================================================

-- Super Admin User - Global role (no context)
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

-- Admin User - Organization context
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
),
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

-- Manager User - Department context
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
),
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

-- Instructor User - Project context
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
),
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

-- Regular User - Organization context
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

-- Student User - Multiple contexts with temporal role
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
),
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
),
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
-- SECTION 20: SEED DATA - USER PERMISSION OVERRIDES
-- ============================================================================

-- GRANT Example: Give regular user special permission to delete courses
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

-- DENY Example: Revoke admin's ability to delete users
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

-- GRANT Example: Give manager permission to view system config temporarily
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
-- COMMIT TRANSACTION
-- ============================================================================

COMMIT;

-- ============================================================================
-- SETUP COMPLETE
-- ============================================================================
-- Summary of created data:
-- - 11 Tables (users, roles, modules, features, actions, resources, data_scopes, etc.)
-- - 7 Actions (CREATE, READ, UPDATE, DELETE, EXECUTE, APPROVE, REJECT)
-- - 5 Data Scopes (OWN, TEAM, DEPARTMENT, ORGANIZATION, ALL)
-- - 3 Modules (User Management, Course Management, System Management)
-- - 15 Features (with hierarchical structure)
-- - 30+ Resources (API endpoints, buttons, menus)
-- - 6 Roles (SUPER_ADMIN, ADMIN, MANAGER, USER, INSTRUCTOR, STUDENT)
-- - 100+ Role Permissions (covering all role-resource-scope combinations)
-- - 6 Sample Users (one for each role, password: "password123")
-- - 12 User Role Assignments (demonstrating context-based and temporal roles)
-- - 3 User Permission Overrides (demonstrating GRANT and DENY with conditions)
--
-- The script is idempotent and can be re-executed safely.
-- All operations are wrapped in a transaction for atomicity.
-- ============================================================================
