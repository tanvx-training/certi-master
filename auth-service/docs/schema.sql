-- ============================================================================
-- Auth Service Database Schema - PostgreSQL DDL
-- ============================================================================
-- Description: Complete database schema for RBAC authorization system
-- Version: 1.0
-- Database: PostgreSQL 12+
-- ============================================================================

-- ============================================================================
-- SECTION 1: CREATE TABLES
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
    created_by BIGINT,
    updated_by BIGINT
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
-- SECTION 2: FOREIGN KEY CONSTRAINTS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Foreign Keys for: refresh_tokens
-- ----------------------------------------------------------------------------
ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

COMMENT ON CONSTRAINT fk_refresh_tokens_user ON refresh_tokens IS 
    'Cascade delete tokens when user is deleted';

-- ----------------------------------------------------------------------------
-- Foreign Keys for: user_roles
-- ----------------------------------------------------------------------------
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

COMMENT ON CONSTRAINT fk_user_roles_user ON user_roles IS 
    'Cascade delete role assignments when user is deleted';
COMMENT ON CONSTRAINT fk_user_roles_role ON user_roles IS 
    'Cascade delete role assignments when role is deleted';

-- ----------------------------------------------------------------------------
-- Foreign Keys for: features
-- ----------------------------------------------------------------------------
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

COMMENT ON CONSTRAINT fk_features_module ON features IS 
    'Cascade delete features when module is deleted';
COMMENT ON CONSTRAINT fk_features_parent ON features IS 
    'Set NULL when parent feature is deleted to preserve child features';

-- ----------------------------------------------------------------------------
-- Foreign Keys for: resources
-- ----------------------------------------------------------------------------
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

COMMENT ON CONSTRAINT fk_resources_feature ON resources IS 
    'Cascade delete resources when feature is deleted';
COMMENT ON CONSTRAINT fk_resources_action ON resources IS 
    'Restrict deletion of actions that are referenced by resources';

-- ----------------------------------------------------------------------------
-- Foreign Keys for: role_permissions
-- ----------------------------------------------------------------------------
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

COMMENT ON CONSTRAINT fk_role_permissions_role ON role_permissions IS 
    'Cascade delete permissions when role is deleted';
COMMENT ON CONSTRAINT fk_role_permissions_resource ON role_permissions IS 
    'Cascade delete permissions when resource is deleted';
COMMENT ON CONSTRAINT fk_role_permissions_data_scope ON role_permissions IS 
    'Set NULL when data scope is deleted, permission remains but without scope restriction';

-- ----------------------------------------------------------------------------
-- Foreign Keys for: user_permissions
-- ----------------------------------------------------------------------------
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

COMMENT ON CONSTRAINT fk_user_permissions_user ON user_permissions IS 
    'Cascade delete permissions when user is deleted';
COMMENT ON CONSTRAINT fk_user_permissions_resource ON user_permissions IS 
    'Cascade delete permissions when resource is deleted';
COMMENT ON CONSTRAINT fk_user_permissions_data_scope ON user_permissions IS 
    'Set NULL when data scope is deleted, permission remains but without scope restriction';

-- ----------------------------------------------------------------------------
-- Indexes for Foreign Keys (Performance Optimization)
-- ----------------------------------------------------------------------------
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

-- ============================================================================
-- SECTION 3: UNIQUE CONSTRAINTS AND INDEXES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Unique Constraints for: users
-- ----------------------------------------------------------------------------
ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username);

COMMENT ON CONSTRAINT uk_users_email ON users IS 
    'Email must be unique across all users';
COMMENT ON CONSTRAINT uk_users_username ON users IS 
    'Username must be unique across all users';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: refresh_tokens
-- ----------------------------------------------------------------------------
ALTER TABLE refresh_tokens
    ADD CONSTRAINT uk_refresh_tokens_token UNIQUE (token);

COMMENT ON CONSTRAINT uk_refresh_tokens_token ON refresh_tokens IS 
    'Refresh token must be unique to prevent token reuse';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: roles
-- ----------------------------------------------------------------------------
ALTER TABLE roles
    ADD CONSTRAINT uk_roles_code UNIQUE (code);

COMMENT ON CONSTRAINT uk_roles_code ON roles IS 
    'Role code must be unique (e.g., ADMIN, USER, MANAGER)';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: user_roles
-- ----------------------------------------------------------------------------
ALTER TABLE user_roles
    ADD CONSTRAINT uk_user_roles_user_role_context 
    UNIQUE (user_id, role_id, context_type, context_id);

COMMENT ON CONSTRAINT uk_user_roles_user_role_context ON user_roles IS 
    'Prevent duplicate role assignments for same user, role, and context combination';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: modules
-- ----------------------------------------------------------------------------
ALTER TABLE modules
    ADD CONSTRAINT uk_modules_code UNIQUE (code);

COMMENT ON CONSTRAINT uk_modules_code ON modules IS 
    'Module code must be unique (e.g., USER_MGMT, COURSE_MGMT)';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: features
-- ----------------------------------------------------------------------------
ALTER TABLE features
    ADD CONSTRAINT uk_features_module_code UNIQUE (module_id, code);

COMMENT ON CONSTRAINT uk_features_module_code ON features IS 
    'Feature code must be unique within a module';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: actions
-- ----------------------------------------------------------------------------
ALTER TABLE actions
    ADD CONSTRAINT uk_actions_code UNIQUE (code);

COMMENT ON CONSTRAINT uk_actions_code ON actions IS 
    'Action code must be unique (e.g., CREATE, READ, UPDATE, DELETE)';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: resources
-- ----------------------------------------------------------------------------
ALTER TABLE resources
    ADD CONSTRAINT uk_resources_code UNIQUE (code);

-- Note: Removed uk_resources_feature_action constraint because a feature can have
-- multiple resources with the same action (e.g., API endpoint and menu item both use READ)

COMMENT ON CONSTRAINT uk_resources_code ON resources IS 
    'Resource code must be unique (e.g., USER_CREATE, USER_VIEW_LIST)';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: data_scopes
-- ----------------------------------------------------------------------------
ALTER TABLE data_scopes
    ADD CONSTRAINT uk_data_scopes_code UNIQUE (code);

ALTER TABLE data_scopes
    ADD CONSTRAINT uk_data_scopes_level UNIQUE (level);

COMMENT ON CONSTRAINT uk_data_scopes_code ON data_scopes IS 
    'Data scope code must be unique (e.g., OWN, TEAM, DEPARTMENT)';
COMMENT ON CONSTRAINT uk_data_scopes_level ON data_scopes IS 
    'Each hierarchical level must be unique';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: role_permissions
-- ----------------------------------------------------------------------------
ALTER TABLE role_permissions
    ADD CONSTRAINT uk_role_permissions_role_resource_scope 
    UNIQUE (role_id, resource_id, data_scope_id);

COMMENT ON CONSTRAINT uk_role_permissions_role_resource_scope ON role_permissions IS 
    'Prevent duplicate permission assignments for same role, resource, and data scope';

-- ----------------------------------------------------------------------------
-- Unique Constraints for: user_permissions
-- ----------------------------------------------------------------------------
ALTER TABLE user_permissions
    ADD CONSTRAINT uk_user_permissions_user_resource_type 
    UNIQUE (user_id, resource_id, permission_type);

COMMENT ON CONSTRAINT uk_user_permissions_user_resource_type ON user_permissions IS 
    'Prevent duplicate permission overrides for same user, resource, and permission type';

-- ----------------------------------------------------------------------------
-- Performance Indexes for Frequently Queried Columns
-- ----------------------------------------------------------------------------

-- Users table indexes
CREATE INDEX idx_users_status ON users(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Refresh tokens indexes
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_user_expires ON refresh_tokens(user_id, expires_at);

-- Roles table indexes
CREATE INDEX idx_roles_is_active ON roles(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_roles_is_system ON roles(is_system);

-- User roles indexes
CREATE INDEX idx_user_roles_context ON user_roles(context_type, context_id);
CREATE INDEX idx_user_roles_valid_from ON user_roles(valid_from);
CREATE INDEX idx_user_roles_valid_until ON user_roles(valid_until);
CREATE INDEX idx_user_roles_is_primary ON user_roles(is_primary) WHERE is_primary = TRUE;
CREATE INDEX idx_user_roles_user_context ON user_roles(user_id, context_type, context_id);

-- Modules table indexes
CREATE INDEX idx_modules_is_active ON modules(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_modules_order_index ON modules(order_index);

-- Features table indexes
CREATE INDEX idx_features_is_active ON features(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_features_order_index ON features(order_index);
CREATE INDEX idx_features_module_order ON features(module_id, order_index);

-- Actions table indexes
CREATE INDEX idx_actions_http_method ON actions(http_method);
CREATE INDEX idx_actions_is_system ON actions(is_system);

-- Resources table indexes
CREATE INDEX idx_resources_is_active ON resources(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_resources_component_type ON resources(component_type);
CREATE INDEX idx_resources_api_path ON resources(api_path_pattern);
CREATE INDEX idx_resources_http_method ON resources(http_method);
CREATE INDEX idx_resources_feature_active ON resources(feature_id, is_active);

-- Data scopes indexes
CREATE INDEX idx_data_scopes_level ON data_scopes(level);

-- Role permissions indexes
CREATE INDEX idx_role_permissions_is_active ON role_permissions(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_role_permissions_expires_at ON role_permissions(expires_at);
CREATE INDEX idx_role_permissions_granted_at ON role_permissions(granted_at);
CREATE INDEX idx_role_permissions_role_active ON role_permissions(role_id, is_active);

-- User permissions indexes
CREATE INDEX idx_user_permissions_is_active ON user_permissions(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_user_permissions_expires_at ON user_permissions(expires_at);
CREATE INDEX idx_user_permissions_permission_type ON user_permissions(permission_type);
CREATE INDEX idx_user_permissions_user_active ON user_permissions(user_id, is_active);
CREATE INDEX idx_user_permissions_user_type ON user_permissions(user_id, permission_type);

-- ============================================================================
-- SECTION 4: JSONB COLUMN DEFINITIONS AND INDEXES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- JSONB Indexes for: role_permissions
-- ----------------------------------------------------------------------------
-- GIN index for efficient JSONB queries on conditions column
CREATE INDEX idx_role_permissions_conditions_gin ON role_permissions USING GIN (conditions);

COMMENT ON INDEX idx_role_permissions_conditions_gin IS 
    'GIN index for efficient querying of JSONB conditions (time ranges, IP whitelist, etc.)';

-- Example JSONB path indexes for common query patterns
CREATE INDEX idx_role_permissions_conditions_time_range 
    ON role_permissions USING BTREE ((conditions->'timeRange'->>'start'), (conditions->'timeRange'->>'end'))
    WHERE conditions ? 'timeRange';

CREATE INDEX idx_role_permissions_conditions_ip_whitelist 
    ON role_permissions USING GIN ((conditions->'ipWhitelist'))
    WHERE conditions ? 'ipWhitelist';

COMMENT ON INDEX idx_role_permissions_conditions_time_range IS 
    'B-tree index for time range queries in conditions';
COMMENT ON INDEX idx_role_permissions_conditions_ip_whitelist IS 
    'GIN index for IP whitelist array queries in conditions';

-- ----------------------------------------------------------------------------
-- JSONB Indexes for: user_permissions
-- ----------------------------------------------------------------------------
-- GIN index for efficient JSONB queries on conditions column
CREATE INDEX idx_user_permissions_conditions_gin ON user_permissions USING GIN (conditions);

COMMENT ON INDEX idx_user_permissions_conditions_gin IS 
    'GIN index for efficient querying of JSONB conditions (time ranges, IP whitelist, etc.)';

-- Example JSONB path indexes for common query patterns
CREATE INDEX idx_user_permissions_conditions_time_range 
    ON user_permissions USING BTREE ((conditions->'timeRange'->>'start'), (conditions->'timeRange'->>'end'))
    WHERE conditions ? 'timeRange';

CREATE INDEX idx_user_permissions_conditions_ip_whitelist 
    ON user_permissions USING GIN ((conditions->'ipWhitelist'))
    WHERE conditions ? 'ipWhitelist';

CREATE INDEX idx_user_permissions_conditions_max_amount 
    ON user_permissions USING BTREE (((conditions->>'maxAmount')::numeric))
    WHERE conditions ? 'maxAmount';

COMMENT ON INDEX idx_user_permissions_conditions_time_range IS 
    'B-tree index for time range queries in conditions';
COMMENT ON INDEX idx_user_permissions_conditions_ip_whitelist IS 
    'GIN index for IP whitelist array queries in conditions';
COMMENT ON INDEX idx_user_permissions_conditions_max_amount IS 
    'B-tree index for max amount queries in conditions';

-- ----------------------------------------------------------------------------
-- JSONB Column Usage Examples
-- ----------------------------------------------------------------------------
-- The conditions JSONB column can store complex permission rules such as:
--
-- Example 1: Time-based restrictions
-- {
--   "timeRange": {
--     "start": "09:00",
--     "end": "17:00"
--   },
--   "daysOfWeek": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
-- }
--
-- Example 2: IP whitelist
-- {
--   "ipWhitelist": ["192.168.1.0/24", "10.0.0.0/8"]
-- }
--
-- Example 3: Amount limits
-- {
--   "maxAmount": 10000,
--   "currency": "USD"
-- }
--
-- Example 4: Combined conditions
-- {
--   "timeRange": {
--     "start": "09:00",
--     "end": "17:00"
--   },
--   "ipWhitelist": ["192.168.1.0/24"],
--   "maxAmount": 5000,
--   "requiresMFA": true
-- }
--
-- Query examples:
-- 
-- 1. Find permissions with time restrictions:
--    SELECT * FROM role_permissions WHERE conditions ? 'timeRange';
--
-- 2. Find permissions for specific IP range:
--    SELECT * FROM role_permissions 
--    WHERE conditions->'ipWhitelist' ? '192.168.1.0/24';
--
-- 3. Find permissions with amount limits:
--    SELECT * FROM user_permissions 
--    WHERE (conditions->>'maxAmount')::numeric < 10000;
--
-- 4. Check if permission has MFA requirement:
--    SELECT * FROM user_permissions 
--    WHERE conditions->>'requiresMFA' = 'true';
