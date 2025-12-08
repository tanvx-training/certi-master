# Hướng Dẫn Sử Dụng Hệ Thống Phân Quyền

## Mục Lục

1. [Thêm Module và Feature Mới](#1-thêm-module-và-feature-mới)
2. [Quản Lý Permissions](#2-quản-lý-permissions)
3. [Context-Based Permissions](#3-context-based-permissions)
4. [Kiểm Tra Permissions Trong Code](#4-kiểm-tra-permissions-trong-code)

---

## 1. Thêm Module và Feature Mới

### 1.1 Tổng Quan

Hệ thống phân quyền sử dụng cấu trúc phân cấp: **Module → Feature → Resource**

- **Module**: Nhóm các tính năng liên quan (ví dụ: User Management, Course Management)
- **Feature**: Tính năng cụ thể trong module (ví dụ: Create User, View User List)
- **Resource**: Tài nguyên có thể phân quyền (API endpoint, UI button, menu item)

### 1.2 Bước 1: Tạo Module Mới

**SQL Script:**

```sql
-- Thêm module mới
INSERT INTO modules (code, name, description, icon, route, order_index, is_active, created_at, updated_at)
VALUES (
    'EXAM_MGMT',                    -- Mã module (unique, uppercase, snake_case)
    'Quản Lý Kỳ Thi',              -- Tên hiển thị
    'Module quản lý kỳ thi, đề thi và kết quả thi',  -- Mô tả
    'exam-icon',                    -- Icon class
    '/exam-management',             -- Base route
    3,                              -- Thứ tự hiển thị (số càng nhỏ càng ưu tiên)
    true,                           -- Active
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

**Lưu Ý:**
- `code`: Phải unique, sử dụng UPPERCASE và snake_case
- `route`: Base route cho frontend routing
- `order_index`: Quyết định thứ tự hiển thị trong menu (0, 1, 2, ...)


### 1.3 Bước 2: Tạo Features Cho Module

**SQL Script:**

```sql
-- Lấy ID của module vừa tạo
-- Giả sử module_id = 3

-- Feature 1: Danh sách kỳ thi
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,                              -- ID của module EXAM_MGMT
    'EXAM_LIST',                    -- Mã feature (unique trong module)
    'Danh Sách Kỳ Thi',            -- Tên hiển thị
    'Xem danh sách các kỳ thi',    -- Mô tả
    '/exam-management/list',        -- Route cụ thể
    'list-icon',                    -- Icon
    1,                              -- Thứ tự
    NULL,                           -- Không có parent (top-level feature)
    true,                           -- Active
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Feature 2: Tạo kỳ thi mới
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,
    'EXAM_CREATE',
    'Tạo Kỳ Thi',
    'Tạo kỳ thi mới',
    '/exam-management/create',
    'create-icon',
    2,
    NULL,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Feature 3: Chỉnh sửa kỳ thi
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,
    'EXAM_EDIT',
    'Chỉnh Sửa Kỳ Thi',
    'Chỉnh sửa thông tin kỳ thi',
    '/exam-management/edit',
    'edit-icon',
    3,
    NULL,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

**Tạo Sub-Feature (Menu Con):**

```sql
-- Feature cha: Quản lý đề thi
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,
    'QUESTION_MGMT',
    'Quản Lý Đề Thi',
    'Quản lý ngân hàng câu hỏi và đề thi',
    '/exam-management/questions',
    'question-icon',
    4,
    NULL,                           -- Top-level feature
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Feature con 1: Danh sách câu hỏi
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,
    'QUESTION_LIST',
    'Danh Sách Câu Hỏi',
    'Xem danh sách câu hỏi',
    '/exam-management/questions/list',
    'list-icon',
    1,
    (SELECT id FROM features WHERE code = 'QUESTION_MGMT' AND module_id = 3),  -- ID của feature cha
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Feature con 2: Tạo câu hỏi
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES (
    3,
    'QUESTION_CREATE',
    'Tạo Câu Hỏi',
    'Tạo câu hỏi mới',
    '/exam-management/questions/create',
    'create-icon',
    2,
    (SELECT id FROM features WHERE code = 'QUESTION_MGMT' AND module_id = 3),
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```


### 1.4 Bước 3: Tạo Resources Cho Features

Resources là các điểm phân quyền cụ thể. Mỗi feature thường có nhiều resources tương ứng với các actions khác nhau.

**SQL Script:**

```sql
-- Resource 1: API xem danh sách kỳ thi
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM features WHERE code = 'EXAM_LIST' AND module_id = 3),
    (SELECT id FROM actions WHERE code = 'READ'),
    'EXAM_LIST_API',
    'API Danh Sách Kỳ Thi',
    'API endpoint để lấy danh sách kỳ thi',
    '/api/v1/exams',                -- API path pattern
    'GET',                          -- HTTP method
    'API',                          -- Component type: API, BUTTON, MENU, PAGE
    NULL,                           -- Component key (cho UI components)
    'DEPARTMENT',                   -- Default data scope
    false,                          -- Không cần approval
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Resource 2: Menu item xem danh sách
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM features WHERE code = 'EXAM_LIST' AND module_id = 3),
    (SELECT id FROM actions WHERE code = 'READ'),
    'EXAM_LIST_MENU',
    'Menu Danh Sách Kỳ Thi',
    'Menu item để truy cập danh sách kỳ thi',
    NULL,
    NULL,
    'MENU',
    'exam-list-menu',               -- Key để frontend check
    'DEPARTMENT',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Resource 3: API tạo kỳ thi mới
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM features WHERE code = 'EXAM_CREATE' AND module_id = 3),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'EXAM_CREATE_API',
    'API Tạo Kỳ Thi',
    'API endpoint để tạo kỳ thi mới',
    '/api/v1/exams',
    'POST',
    'API',
    NULL,
    'OWN',                          -- Người tạo sở hữu
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Resource 4: Button tạo kỳ thi
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM features WHERE code = 'EXAM_CREATE' AND module_id = 3),
    (SELECT id FROM actions WHERE code = 'CREATE'),
    'EXAM_CREATE_BUTTON',
    'Button Tạo Kỳ Thi',
    'Button để mở form tạo kỳ thi',
    NULL,
    NULL,
    'BUTTON',
    'exam-create-btn',
    'OWN',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Resource 5: API xóa kỳ thi (cần approval)
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM features WHERE code = 'EXAM_EDIT' AND module_id = 3),
    (SELECT id FROM actions WHERE code = 'DELETE'),
    'EXAM_DELETE_API',
    'API Xóa Kỳ Thi',
    'API endpoint để xóa kỳ thi',
    '/api/v1/exams/{id}',           -- Path với parameter
    'DELETE',
    'API',
    NULL,
    'OWN',
    true,                           -- CẦN APPROVAL
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

**Lưu Ý:**
- `component_type`: API (backend), BUTTON/MENU/PAGE (frontend)
- `api_path_pattern`: Hỗ trợ path parameters như `{id}`, `{userId}`
- `default_scope`: Scope mặc định khi gán permission
- `requires_approval`: true cho các action nhạy cảm (DELETE, APPROVE)


### 1.5 Script Hoàn Chỉnh: Thêm Module Mới

```sql
-- ============================================
-- SCRIPT: Thêm Module Exam Management
-- ============================================

BEGIN;

-- 1. Tạo Module
INSERT INTO modules (code, name, description, icon, route, order_index, is_active, created_at, updated_at)
VALUES ('EXAM_MGMT', 'Quản Lý Kỳ Thi', 'Module quản lý kỳ thi, đề thi và kết quả thi', 
        'exam-icon', '/exam-management', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
RETURNING id;  -- Giả sử trả về id = 3

-- 2. Tạo Features
INSERT INTO features (module_id, code, name, description, route, icon, order_index, parent_feature_id, is_active, created_at, updated_at)
VALUES 
    (3, 'EXAM_LIST', 'Danh Sách Kỳ Thi', 'Xem danh sách các kỳ thi', '/exam-management/list', 'list-icon', 1, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'EXAM_CREATE', 'Tạo Kỳ Thi', 'Tạo kỳ thi mới', '/exam-management/create', 'create-icon', 2, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'EXAM_EDIT', 'Chỉnh Sửa Kỳ Thi', 'Chỉnh sửa thông tin kỳ thi', '/exam-management/edit', 'edit-icon', 3, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Tạo Resources cho EXAM_LIST
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES 
    ((SELECT id FROM features WHERE code = 'EXAM_LIST'), (SELECT id FROM actions WHERE code = 'READ'), 
     'EXAM_LIST_API', 'API Danh Sách Kỳ Thi', 'API endpoint để lấy danh sách kỳ thi', 
     '/api/v1/exams', 'GET', 'API', NULL, 'DEPARTMENT', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ((SELECT id FROM features WHERE code = 'EXAM_LIST'), (SELECT id FROM actions WHERE code = 'READ'), 
     'EXAM_LIST_MENU', 'Menu Danh Sách Kỳ Thi', 'Menu item để truy cập danh sách kỳ thi', 
     NULL, NULL, 'MENU', 'exam-list-menu', 'DEPARTMENT', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. Tạo Resources cho EXAM_CREATE
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES 
    ((SELECT id FROM features WHERE code = 'EXAM_CREATE'), (SELECT id FROM actions WHERE code = 'CREATE'), 
     'EXAM_CREATE_API', 'API Tạo Kỳ Thi', 'API endpoint để tạo kỳ thi mới', 
     '/api/v1/exams', 'POST', 'API', NULL, 'OWN', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ((SELECT id FROM features WHERE code = 'EXAM_CREATE'), (SELECT id FROM actions WHERE code = 'CREATE'), 
     'EXAM_CREATE_BUTTON', 'Button Tạo Kỳ Thi', 'Button để mở form tạo kỳ thi', 
     NULL, NULL, 'BUTTON', 'exam-create-btn', 'OWN', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. Tạo Resources cho EXAM_EDIT
INSERT INTO resources (feature_id, action_id, code, name, description, api_path_pattern, http_method, component_type, component_key, default_scope, requires_approval, is_active, created_at, updated_at)
VALUES 
    ((SELECT id FROM features WHERE code = 'EXAM_EDIT'), (SELECT id FROM actions WHERE code = 'UPDATE'), 
     'EXAM_UPDATE_API', 'API Cập Nhật Kỳ Thi', 'API endpoint để cập nhật kỳ thi', 
     '/api/v1/exams/{id}', 'PUT', 'API', NULL, 'OWN', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ((SELECT id FROM features WHERE code = 'EXAM_EDIT'), (SELECT id FROM actions WHERE code = 'DELETE'), 
     'EXAM_DELETE_API', 'API Xóa Kỳ Thi', 'API endpoint để xóa kỳ thi', 
     '/api/v1/exams/{id}', 'DELETE', 'API', NULL, 'OWN', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMIT;
```

---


## 2. Quản Lý Permissions

### 2.1 Tổng Quan Permission Model

Hệ thống sử dụng **Dual Permission Model**:

1. **Role Permissions** (RolePermission): Quyền được gán cho role, user inherit từ role
2. **User Permissions** (UserPermission): Quyền được gán trực tiếp cho user, có thể GRANT hoặc DENY

**Thứ tự ưu tiên:**
```
1. UserPermission DENY (cao nhất - từ chối ngay)
2. UserPermission GRANT (cấp quyền đặc biệt)
3. RolePermission (quyền từ role)
4. No Permission (từ chối mặc định)
```

### 2.2 Tạo và Gán Role Permissions

#### 2.2.1 Gán Quyền Cơ Bản Cho Role

```sql
-- Gán quyền xem danh sách kỳ thi cho role USER
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, expires_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'USER'),
    (SELECT id FROM resources WHERE code = 'EXAM_LIST_API'),
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),  -- Chỉ xem trong phòng ban
    NULL,                           -- Không có điều kiện bổ sung
    1,                              -- ID của admin cấp quyền
    CURRENT_TIMESTAMP,
    NULL,                           -- Không hết hạn
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Gán quyền tạo kỳ thi cho role TEACHER
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, expires_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'TEACHER'),
    (SELECT id FROM resources WHERE code = 'EXAM_CREATE_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),  -- Chỉ tạo cho chính mình
    NULL,
    1,
    CURRENT_TIMESTAMP,
    NULL,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### 2.2.2 Gán Quyền Với Data Scope Khác Nhau

```sql
-- ADMIN: Xem tất cả kỳ thi trong tổ chức
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    (SELECT id FROM resources WHERE code = 'EXAM_LIST_API'),
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),  -- Toàn tổ chức
    NULL, 1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- MANAGER: Xem kỳ thi trong phòng ban
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    (SELECT id FROM resources WHERE code = 'EXAM_LIST_API'),
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),  -- Phòng ban
    NULL, 1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- USER: Chỉ xem kỳ thi của chính mình
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'USER'),
    (SELECT id FROM resources WHERE code = 'EXAM_LIST_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),  -- Chỉ của mình
    NULL, 1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
```


#### 2.2.3 Gán Quyền Với Điều Kiện (JSONB Conditions)

```sql
-- Quyền chỉ có hiệu lực trong giờ hành chính (9h-17h)
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'USER'),
    (SELECT id FROM resources WHERE code = 'EXAM_CREATE_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    '{"timeRange": {"start": "09:00", "end": "17:00"}}'::jsonb,  -- Điều kiện thời gian
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Quyền với giới hạn IP
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    (SELECT id FROM resources WHERE code = 'EXAM_DELETE_API'),
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    '{"ipWhitelist": ["192.168.1.0/24", "10.0.0.0/8"]}'::jsonb,  -- Chỉ từ IP nội bộ
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Quyền với giới hạn số lượng
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'TEACHER'),
    (SELECT id FROM resources WHERE code = 'EXAM_CREATE_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    '{"maxExamsPerDay": 5, "maxStudentsPerExam": 100}'::jsonb,  -- Giới hạn số lượng
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
```

#### 2.2.4 Gán Quyền Có Thời Hạn

```sql
-- Quyền tạm thời (hết hạn sau 30 ngày)
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, conditions, granted_by, granted_at, expires_at, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM roles WHERE code = 'TEMP_ADMIN'),
    (SELECT id FROM resources WHERE code = 'EXAM_DELETE_API'),
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',  -- Hết hạn sau 30 ngày
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

### 2.3 Gán User-Specific Permissions

#### 2.3.1 GRANT: Cấp Quyền Đặc Biệt Cho User

Sử dụng khi muốn cấp quyền cho user mà role của họ không có.

```sql
-- User A là USER nhưng cần quyền xóa kỳ thi trong 7 ngày
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, conditions, granted_by, granted_at, expires_at, reason, is_active, created_at, updated_at)
VALUES (
    123,                            -- ID của user A
    (SELECT id FROM resources WHERE code = 'EXAM_DELETE_API'),
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    'GRANT',                        -- Cấp quyền
    NULL,
    1,                              -- Admin cấp quyền
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '7 days',  -- Hết hạn sau 7 ngày
    'Cấp quyền tạm thời để xử lý dữ liệu lỗi',  -- Lý do
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- User B cần quyền xem toàn bộ kỳ thi (thay vì chỉ phòng ban)
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, conditions, granted_by, granted_at, expires_at, reason, is_active, created_at, updated_at)
VALUES (
    456,                            -- ID của user B
    (SELECT id FROM resources WHERE code = 'EXAM_LIST_API'),
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),  -- Nâng scope lên ORGANIZATION
    'GRANT',
    NULL,
    1,
    CURRENT_TIMESTAMP,
    NULL,                           -- Không hết hạn
    'User B là auditor cần xem toàn bộ dữ liệu',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```


#### 2.3.2 DENY: Thu Hồi Quyền Của User

Sử dụng khi muốn thu hồi quyền mà user có từ role.

```sql
-- User C là ADMIN nhưng bị cấm xóa kỳ thi
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, conditions, granted_by, granted_at, expires_at, reason, is_active, created_at, updated_at)
VALUES (
    789,                            -- ID của user C
    (SELECT id FROM resources WHERE code = 'EXAM_DELETE_API'),
    NULL,                           -- DENY không cần data_scope
    'DENY',                         -- Thu hồi quyền
    NULL,
    1,
    CURRENT_TIMESTAMP,
    NULL,
    'User C đã xóa nhầm dữ liệu quan trọng, tạm thời thu hồi quyền xóa',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- User D bị cấm tạo kỳ thi trong 30 ngày (kỷ luật)
INSERT INTO user_permissions (user_id, resource_id, data_scope_id, permission_type, conditions, granted_by, granted_at, expires_at, reason, is_active, created_at, updated_at)
VALUES (
    101,                            -- ID của user D
    (SELECT id FROM resources WHERE code = 'EXAM_CREATE_API'),
    NULL,
    'DENY',
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',  -- Hết hạn sau 30 ngày
    'Vi phạm quy định tạo kỳ thi, tạm thời thu hồi quyền',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

### 2.4 So Sánh GRANT vs DENY

| Tiêu Chí | GRANT | DENY |
|----------|-------|------|
| **Mục đích** | Cấp quyền đặc biệt cho user | Thu hồi quyền của user |
| **Use case** | User cần quyền mà role không có | User có quyền từ role nhưng cần thu hồi |
| **Ưu tiên** | Thứ 2 (sau DENY) | Thứ 1 (cao nhất) |
| **Data Scope** | Bắt buộc | Không cần (deny toàn bộ) |
| **Ví dụ** | USER cần quyền DELETE tạm thời | ADMIN bị cấm DELETE |

### 2.5 Quản Lý Data Scopes

#### 2.5.1 Các Data Scope Chuẩn

```sql
-- OWN (Level 1): Chỉ dữ liệu của chính mình
SELECT * FROM data_scopes WHERE code = 'OWN';
-- Filter: WHERE created_by = current_user_id

-- TEAM (Level 2): Dữ liệu của team
SELECT * FROM data_scopes WHERE code = 'TEAM';
-- Filter: WHERE team_id IN (user_team_ids)

-- DEPARTMENT (Level 3): Dữ liệu của phòng ban
SELECT * FROM data_scopes WHERE code = 'DEPARTMENT';
-- Filter: WHERE department_id = user_department_id

-- ORGANIZATION (Level 4): Dữ liệu của tổ chức
SELECT * FROM data_scopes WHERE code = 'ORGANIZATION';
-- Filter: WHERE organization_id = user_organization_id

-- ALL (Level 5): Tất cả dữ liệu
SELECT * FROM data_scopes WHERE code = 'ALL';
-- Filter: (no filter)
```

#### 2.5.2 Tạo Custom Data Scope

```sql
-- Scope cho project-based access
INSERT INTO data_scopes (code, name, description, level, filter_type, filter_expression, created_at, updated_at)
VALUES (
    'PROJECT',
    'Phạm Vi Dự Án',
    'Chỉ truy cập dữ liệu trong các dự án được gán',
    2,                              -- Level giữa OWN và DEPARTMENT
    'PROJECT_ID',
    'project_id IN (SELECT project_id FROM user_projects WHERE user_id = :userId)',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Scope cho region-based access
INSERT INTO data_scopes (code, name, description, level, filter_type, filter_expression, created_at, updated_at)
VALUES (
    'REGION',
    'Phạm Vi Khu Vực',
    'Chỉ truy cập dữ liệu trong khu vực địa lý',
    3,
    'REGION_ID',
    'region_id = (SELECT region_id FROM users WHERE id = :userId)',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```


### 2.6 Best Practices

#### 2.6.1 Nguyên Tắc Phân Quyền

1. **Principle of Least Privilege**: Chỉ cấp quyền tối thiểu cần thiết
2. **Default Deny**: Mặc định từ chối, chỉ grant khi cần
3. **Role-First**: Ưu tiên gán quyền qua role, chỉ dùng user permission khi thực sự cần
4. **Audit Trail**: Luôn ghi lại `granted_by` và `reason` cho user permissions
5. **Temporal Permissions**: Đặt `expires_at` cho quyền tạm thời

#### 2.6.2 Ví Dụ Phân Quyền Theo Role

```sql
-- ============================================
-- SCRIPT: Phân quyền cho Module Exam Management
-- ============================================

BEGIN;

-- 1. SUPER_ADMIN: Toàn quyền
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, granted_by, granted_at, is_active, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ALL'),
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM resources r
WHERE r.feature_id IN (SELECT id FROM features WHERE module_id = (SELECT id FROM modules WHERE code = 'EXAM_MGMT'));

-- 2. ADMIN: Toàn quyền trong organization
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, granted_by, granted_at, is_active, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'ORGANIZATION'),
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM resources r
WHERE r.feature_id IN (SELECT id FROM features WHERE module_id = (SELECT id FROM modules WHERE code = 'EXAM_MGMT'));

-- 3. MANAGER: Quyền quản lý trong department
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, granted_by, granted_at, is_active, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'DEPARTMENT'),
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM resources r
WHERE r.feature_id IN (SELECT id FROM features WHERE module_id = (SELECT id FROM modules WHERE code = 'EXAM_MGMT'))
  AND r.action_id IN (SELECT id FROM actions WHERE code IN ('READ', 'CREATE', 'UPDATE'));

-- 4. TEACHER: Quyền tạo và quản lý kỳ thi của mình
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, granted_by, granted_at, is_active, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'TEACHER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM resources r
WHERE r.feature_id IN (SELECT id FROM features WHERE module_id = (SELECT id FROM modules WHERE code = 'EXAM_MGMT'))
  AND r.action_id IN (SELECT id FROM actions WHERE code IN ('READ', 'CREATE', 'UPDATE'));

-- 5. USER: Chỉ xem kỳ thi
INSERT INTO role_permissions (role_id, resource_id, data_scope_id, granted_by, granted_at, is_active, created_at, updated_at)
SELECT 
    (SELECT id FROM roles WHERE code = 'USER'),
    r.id,
    (SELECT id FROM data_scopes WHERE code = 'OWN'),
    1, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM resources r
WHERE r.feature_id IN (SELECT id FROM features WHERE module_id = (SELECT id FROM modules WHERE code = 'EXAM_MGMT'))
  AND r.action_id = (SELECT id FROM actions WHERE code = 'READ');

COMMIT;
```

---


## 3. Context-Based Permissions

### 3.1 Tổng Quan

**Context-Based Permissions** cho phép user có các role khác nhau tùy theo ngữ cảnh (context). Ví dụ:
- User A là **ADMIN** trong Organization X
- User A là **USER** trong Organization Y
- User B là **MANAGER** trong Project Alpha
- User B là **MEMBER** trong Project Beta

### 3.2 Context Types

Hệ thống hỗ trợ các loại context phổ biến:

| Context Type | Mô Tả | Ví Dụ |
|--------------|-------|-------|
| **ORGANIZATION** | Tổ chức/công ty | User là ADMIN trong Org A, USER trong Org B |
| **PROJECT** | Dự án | User là MANAGER trong Project X, MEMBER trong Project Y |
| **TEAM** | Nhóm/đội | User là LEADER trong Team Alpha, MEMBER trong Team Beta |
| **DEPARTMENT** | Phòng ban | User là HEAD trong Dept A, STAFF trong Dept B |
| **COURSE** | Khóa học | User là INSTRUCTOR trong Course 101, STUDENT trong Course 102 |

### 3.3 Gán Context-Based Roles

#### 3.3.1 Gán Role Theo Organization

```sql
-- User 123 là ADMIN trong Organization 1
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    123,
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    'ORGANIZATION',
    1,                              -- ID của Organization 1
    CURRENT_TIMESTAMP,
    NULL,                           -- Không hết hạn
    true,                           -- Role chính
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- User 123 là USER trong Organization 2
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    123,
    (SELECT id FROM roles WHERE code = 'USER'),
    'ORGANIZATION',
    2,                              -- ID của Organization 2
    CURRENT_TIMESTAMP,
    NULL,
    false,                          -- Không phải role chính
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### 3.3.2 Gán Role Theo Project

```sql
-- User 456 là MANAGER trong Project Alpha (ID = 10)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    456,
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    'PROJECT',
    10,                             -- ID của Project Alpha
    CURRENT_TIMESTAMP,
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- User 456 là MEMBER trong Project Beta (ID = 20)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    456,
    (SELECT id FROM roles WHERE code = 'MEMBER'),
    'PROJECT',
    20,                             -- ID của Project Beta
    CURRENT_TIMESTAMP,
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### 3.3.3 Gán Role Theo Team

```sql
-- User 789 là TEAM_LEADER trong Team 5
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    789,
    (SELECT id FROM roles WHERE code = 'TEAM_LEADER'),
    'TEAM',
    5,
    CURRENT_TIMESTAMP,
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```


#### 3.3.4 Gán Role Tạm Thời (Temporal Context Role)

```sql
-- User 101 là TEMP_ADMIN trong Organization 1 trong 30 ngày
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, valid_until, is_primary, created_at, updated_at)
VALUES (
    101,
    (SELECT id FROM roles WHERE code = 'TEMP_ADMIN'),
    'ORGANIZATION',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',  -- Hết hạn sau 30 ngày
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### 3.3.5 Primary Role vs Context Roles

**Primary Role** (`is_primary = true`):
- Role mặc định của user khi không có context cụ thể
- Mỗi user chỉ có 1 primary role
- Được sử dụng khi không truyền context vào permission check

**Context Roles** (`is_primary = false`):
- Roles theo context cụ thể
- User có thể có nhiều context roles
- Được sử dụng khi có context trong request

```sql
-- User 123 có primary role là USER
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, is_primary, created_at, updated_at)
VALUES (
    123,
    (SELECT id FROM roles WHERE code = 'USER'),
    NULL,                           -- Không có context
    NULL,
    CURRENT_TIMESTAMP,
    true,                           -- Primary role
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- User 123 là ADMIN trong Organization 1 (context role)
INSERT INTO user_roles (user_id, role_id, context_type, context_id, valid_from, is_primary, created_at, updated_at)
VALUES (
    123,
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    'ORGANIZATION',
    1,
    CURRENT_TIMESTAMP,
    false,                          -- Context role
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

### 3.4 Kiểm Tra Permissions Với Context

#### 3.4.1 Logic Kiểm Tra Permission

```
function hasPermission(userId, resourceCode, contextType, contextId):
    // Step 1: Check user-specific DENY (highest priority)
    userDeny = UserPermission.find(userId, resourceCode, type=DENY, active=true)
    if userDeny exists and not expired:
        return false
    
    // Step 2: Check user-specific GRANT
    userGrant = UserPermission.find(userId, resourceCode, type=GRANT, active=true)
    if userGrant exists and not expired:
        return true with userGrant.dataScope
    
    // Step 3: Get user roles based on context
    if contextType and contextId:
        // Get context-specific roles
        userRoles = UserRole.find(userId, contextType, contextId, active=true, not expired)
    else:
        // Get primary role
        userRoles = UserRole.find(userId, isPrimary=true, active=true)
    
    // Step 4: Check role permissions
    for each role in userRoles:
        rolePermission = RolePermission.find(role, resourceCode, active=true)
        if rolePermission exists and not expired:
            return true with rolePermission.dataScope
    
    // Step 5: No permission found
    return false
```

#### 3.4.2 Query Roles Theo Context

```sql
-- Lấy roles của user trong Organization 1
SELECT ur.*, r.code, r.name
FROM user_roles ur
JOIN roles r ON ur.role_id = r.id
WHERE ur.user_id = 123
  AND ur.context_type = 'ORGANIZATION'
  AND ur.context_id = 1
  AND ur.is_active = true
  AND (ur.valid_until IS NULL OR ur.valid_until > CURRENT_TIMESTAMP);

-- Lấy primary role của user
SELECT ur.*, r.code, r.name
FROM user_roles ur
JOIN roles r ON ur.role_id = r.id
WHERE ur.user_id = 123
  AND ur.is_primary = true
  AND ur.is_active = true;

-- Lấy tất cả roles của user (bao gồm cả context và primary)
SELECT ur.*, r.code, r.name
FROM user_roles ur
JOIN roles r ON ur.role_id = r.id
WHERE ur.user_id = 123
  AND ur.is_active = true
  AND (ur.valid_until IS NULL OR ur.valid_until > CURRENT_TIMESTAMP)
ORDER BY ur.is_primary DESC, ur.context_type, ur.context_id;
```


### 3.5 Implementation Examples

#### 3.5.1 Java Service Layer

```java
@Service
public class PermissionService {
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private UserPermissionRepository userPermissionRepository;
    
    /**
     * Kiểm tra user có quyền truy cập resource không
     * 
     * @param userId ID của user
     * @param resourceCode Mã resource (ví dụ: EXAM_CREATE_API)
     * @param contextType Loại context (ORGANIZATION, PROJECT, TEAM)
     * @param contextId ID của context
     * @return PermissionResult chứa hasPermission và dataScope
     */
    public PermissionResult hasPermission(Long userId, String resourceCode, 
                                         String contextType, Long contextId) {
        // Step 1: Check user-specific DENY
        Optional<UserPermission> userDeny = userPermissionRepository
            .findByUserIdAndResourceCodeAndPermissionType(userId, resourceCode, "DENY");
        
        if (userDeny.isPresent() && userDeny.get().isActive() && !userDeny.get().isExpired()) {
            return PermissionResult.denied("Access explicitly denied");
        }
        
        // Step 2: Check user-specific GRANT
        Optional<UserPermission> userGrant = userPermissionRepository
            .findByUserIdAndResourceCodeAndPermissionType(userId, resourceCode, "GRANT");
        
        if (userGrant.isPresent() && userGrant.get().isActive() && !userGrant.get().isExpired()) {
            return PermissionResult.granted(userGrant.get().getDataScope());
        }
        
        // Step 3: Get user roles based on context
        List<UserRole> userRoles;
        if (contextType != null && contextId != null) {
            // Get context-specific roles
            userRoles = userRoleRepository
                .findByUserIdAndContextTypeAndContextIdAndIsActiveTrue(userId, contextType, contextId);
        } else {
            // Get primary role
            userRoles = userRoleRepository
                .findByUserIdAndIsPrimaryTrueAndIsActiveTrue(userId);
        }
        
        // Filter expired roles
        userRoles = userRoles.stream()
            .filter(ur -> ur.getValidUntil() == null || ur.getValidUntil().isAfter(LocalDateTime.now()))
            .collect(Collectors.toList());
        
        // Step 4: Check role permissions
        for (UserRole userRole : userRoles) {
            Optional<RolePermission> rolePermission = rolePermissionRepository
                .findByRoleIdAndResourceCodeAndIsActiveTrue(userRole.getRole().getId(), resourceCode);
            
            if (rolePermission.isPresent() && !rolePermission.get().isExpired()) {
                return PermissionResult.granted(rolePermission.get().getDataScope());
            }
        }
        
        // Step 5: No permission found
        return PermissionResult.denied("No permission found");
    }
    
    /**
     * Lấy tất cả contexts mà user có role
     */
    public Map<String, List<Long>> getUserContexts(Long userId) {
        List<UserRole> userRoles = userRoleRepository
            .findByUserIdAndIsActiveTrue(userId);
        
        return userRoles.stream()
            .filter(ur -> ur.getContextType() != null)
            .filter(ur -> ur.getValidUntil() == null || ur.getValidUntil().isAfter(LocalDateTime.now()))
            .collect(Collectors.groupingBy(
                UserRole::getContextType,
                Collectors.mapping(UserRole::getContextId, Collectors.toList())
            ));
    }
}
```

#### 3.5.2 Controller Layer với Context

```java
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private ExamService examService;
    
    /**
     * Lấy danh sách kỳ thi trong organization
     */
    @GetMapping
    public ResponseEntity<List<ExamDto>> getExams(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        
        // Check permission với context ORGANIZATION
        PermissionResult permission = permissionService.hasPermission(
            userId, 
            "EXAM_LIST_API", 
            "ORGANIZATION", 
            organizationId
        );
        
        if (!permission.isGranted()) {
            throw new ForbiddenException("You don't have permission to view exams in this organization");
        }
        
        // Lấy data với data scope filtering
        List<ExamDto> exams = examService.getExams(
            organizationId, 
            userId, 
            permission.getDataScope()
        );
        
        return ResponseEntity.ok(exams);
    }
    
    /**
     * Tạo kỳ thi mới trong project
     */
    @PostMapping
    public ResponseEntity<ExamDto> createExam(
            @RequestHeader("X-Project-Id") Long projectId,
            @RequestBody CreateExamRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        
        // Check permission với context PROJECT
        PermissionResult permission = permissionService.hasPermission(
            userId, 
            "EXAM_CREATE_API", 
            "PROJECT", 
            projectId
        );
        
        if (!permission.isGranted()) {
            throw new ForbiddenException("You don't have permission to create exams in this project");
        }
        
        ExamDto exam = examService.createExam(projectId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(exam);
    }
}
```


#### 3.5.3 Frontend Context Switching

```javascript
// React example: Context selector component
import React, { useState, useEffect } from 'react';
import { getUserContexts, switchContext } from './api/authService';

function ContextSelector() {
    const [contexts, setContexts] = useState({});
    const [currentContext, setCurrentContext] = useState(null);
    
    useEffect(() => {
        // Load user's contexts
        getUserContexts().then(data => {
            setContexts(data);
            // Set default context
            if (data.ORGANIZATION && data.ORGANIZATION.length > 0) {
                setCurrentContext({
                    type: 'ORGANIZATION',
                    id: data.ORGANIZATION[0]
                });
            }
        });
    }, []);
    
    const handleContextChange = (type, id) => {
        setCurrentContext({ type, id });
        // Store in localStorage or context
        localStorage.setItem('currentContext', JSON.stringify({ type, id }));
        // Reload permissions
        switchContext(type, id);
    };
    
    return (
        <div className="context-selector">
            <h3>Switch Context</h3>
            
            {/* Organization selector */}
            {contexts.ORGANIZATION && (
                <div>
                    <label>Organization:</label>
                    <select onChange={(e) => handleContextChange('ORGANIZATION', e.target.value)}>
                        {contexts.ORGANIZATION.map(orgId => (
                            <option key={orgId} value={orgId}>
                                Organization {orgId}
                            </option>
                        ))}
                    </select>
                </div>
            )}
            
            {/* Project selector */}
            {contexts.PROJECT && (
                <div>
                    <label>Project:</label>
                    <select onChange={(e) => handleContextChange('PROJECT', e.target.value)}>
                        {contexts.PROJECT.map(projectId => (
                            <option key={projectId} value={projectId}>
                                Project {projectId}
                            </option>
                        ))}
                    </select>
                </div>
            )}
        </div>
    );
}

// API service
export const authService = {
    getUserContexts: async () => {
        const response = await fetch('/api/v1/auth/contexts', {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });
        return response.json();
    },
    
    switchContext: async (contextType, contextId) => {
        // Store context in session
        sessionStorage.setItem('contextType', contextType);
        sessionStorage.setItem('contextId', contextId);
        
        // Reload user permissions for this context
        const response = await fetch('/api/v1/auth/permissions', {
            headers: {
                'Authorization': `Bearer ${getToken()}`,
                'X-Context-Type': contextType,
                'X-Context-Id': contextId
            }
        });
        
        const permissions = await response.json();
        sessionStorage.setItem('permissions', JSON.stringify(permissions));
        
        // Trigger app reload or permission refresh
        window.dispatchEvent(new Event('contextChanged'));
    }
};
```

### 3.6 Use Cases

#### Use Case 1: Multi-Tenant SaaS Application

```sql
-- User A là ADMIN trong Company X
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (1, (SELECT id FROM roles WHERE code = 'ADMIN'), 'ORGANIZATION', 100, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User A là USER trong Company Y
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (1, (SELECT id FROM roles WHERE code = 'USER'), 'ORGANIZATION', 200, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Khi user A truy cập Company X → có quyền ADMIN
-- Khi user A truy cập Company Y → chỉ có quyền USER
```

#### Use Case 2: Project-Based Collaboration

```sql
-- User B là PROJECT_MANAGER trong Project Alpha
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (2, (SELECT id FROM roles WHERE code = 'PROJECT_MANAGER'), 'PROJECT', 10, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User B là CONTRIBUTOR trong Project Beta
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (2, (SELECT id FROM roles WHERE code = 'CONTRIBUTOR'), 'PROJECT', 20, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User B có quyền quản lý Project Alpha nhưng chỉ contribute vào Project Beta
```

#### Use Case 3: Course Management System

```sql
-- User C là INSTRUCTOR trong Course 101
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (3, (SELECT id FROM roles WHERE code = 'INSTRUCTOR'), 'COURSE', 101, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User C là STUDENT trong Course 102
INSERT INTO user_roles (user_id, role_id, context_type, context_id, is_primary, created_at, updated_at)
VALUES (3, (SELECT id FROM roles WHERE code = 'STUDENT'), 'COURSE', 102, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User C có thể tạo bài thi trong Course 101 nhưng chỉ làm bài trong Course 102
```

---


## 4. Kiểm Tra Permissions Trong Code

### 4.1 Spring Security Integration

#### 4.1.1 Custom Permission Evaluator

```java
package com.certimaster.auth_service.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String resourceCode = permission.toString();

        // Get context from request (if available)
        String contextType = RequestContextHolder.getContextType();
        Long contextId = RequestContextHolder.getContextId();

        PermissionResult result = permissionService.hasPermission(
                userId, resourceCode, contextType, contextId
        );

        return result.isGranted();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        // Implementation for checking permission on specific entity
        return hasPermission(authentication, null, permission);
    }
}
```

#### 4.1.2 Security Configuration

```java
package com.certimaster.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
```

### 4.2 Controller Layer Permission Checks

#### 4.2.1 Using @PreAuthorize Annotation

```java
package com.certimaster.auth_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * Lấy danh sách kỳ thi - Cần quyền READ
     */
    @GetMapping
    @PreAuthorize("hasPermission(null, 'EXAM_LIST_API')")
    public ResponseEntity<List<ExamDto>> getExams(
            @RequestParam(required = false) Long organizationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Permission đã được check bởi @PreAuthorize
        List<ExamDto> exams = examService.getExams(organizationId, userDetails.getUserId());
        return ResponseEntity.ok(exams);
    }

    /**
     * Tạo kỳ thi mới - Cần quyền CREATE
     */
    @PostMapping
    @PreAuthorize("hasPermission(null, 'EXAM_CREATE_API')")
    public ResponseEntity<ExamDto> createExam(
            @RequestBody CreateExamRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ExamDto exam = examService.createExam(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(exam);
    }

    /**
     * Cập nhật kỳ thi - Cần quyền UPDATE
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EXAM_UPDATE_API')")
    public ResponseEntity<ExamDto> updateExam(
            @PathVariable Long id,
            @RequestBody UpdateExamRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ExamDto exam = examService.updateExam(id, request, userDetails.getUserId());
        return ResponseEntity.ok(exam);
    }

    /**
     * Xóa kỳ thi - Cần quyền DELETE (requires approval)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EXAM_DELETE_API')")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        examService.deleteExam(id, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
```

#### 4.2.2 Manual Permission Check

```java
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private ExamService examService;
    
    /**
     * Kiểm tra permission thủ công với context
     */
    @GetMapping
    public ResponseEntity<List<ExamDto>> getExams(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails.getUserId();
        
        // Manual permission check với context
        PermissionResult permission = permissionService.hasPermission(
            userId, 
            "EXAM_LIST_API", 
            "ORGANIZATION", 
            organizationId
        );
        
        if (!permission.isGranted()) {
            throw new ForbiddenException("You don't have permission to view exams");
        }
        
        // Lấy data với data scope
        List<ExamDto> exams = examService.getExamsWithScope(
            organizationId, 
            userId, 
            permission.getDataScope()
        );
        
        return ResponseEntity.ok(exams);
    }
    
    /**
     * Kiểm tra nhiều permissions
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<ExamDto> publishExam(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails.getUserId();
        
        // Check quyền UPDATE
        PermissionResult updatePermission = permissionService.hasPermission(
            userId, "EXAM_UPDATE_API", null, null
        );
        
        // Check quyền APPROVE (publish cần approval)
        PermissionResult approvePermission = permissionService.hasPermission(
            userId, "EXAM_APPROVE_API", null, null
        );
        
        if (!updatePermission.isGranted() || !approvePermission.isGranted()) {
            throw new ForbiddenException("You don't have permission to publish exams");
        }
        
        ExamDto exam = examService.publishExam(id, userId);
        return ResponseEntity.ok(exam);
    }
}
```


### 4.3 Service Layer với Data Scope Filtering

#### 4.3.1 Apply Data Scope Filter

```java
package com.certimaster.auth_service.service;

import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.List;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy danh sách kỳ thi với data scope filtering
     */
    public List<ExamDto> getExamsWithScope(Long organizationId, Long userId, DataScope dataScope) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Build query với data scope filter
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Exam> query = cb.createQuery(Exam.class);
        Root<Exam> exam = query.from(Exam.class);

        // Base condition: organization
        Predicate predicate = cb.equal(exam.get("organizationId"), organizationId);

        // Apply data scope filter
        switch (dataScope.getCode()) {
            case "OWN":
                // Chỉ kỳ thi do user tạo
                predicate = cb.and(predicate, cb.equal(exam.get("createdBy"), userId));
                break;

            case "TEAM":
                // Kỳ thi của team
                List<Long> teamIds = user.getTeamIds();
                predicate = cb.and(predicate, exam.get("teamId").in(teamIds));
                break;

            case "DEPARTMENT":
                // Kỳ thi của phòng ban
                predicate = cb.and(predicate, cb.equal(exam.get("departmentId"), user.getDepartmentId()));
                break;

            case "ORGANIZATION":
                // Tất cả kỳ thi trong organization (không thêm filter)
                break;

            case "ALL":
                // Tất cả kỳ thi (bỏ cả organization filter)
                predicate = cb.conjunction();
                break;

            default:
                throw new IllegalArgumentException("Unknown data scope: " + dataScope.getCode());
        }

        query.where(predicate);
        List<Exam> exams = entityManager.createQuery(query).getResultList();

        return exams.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra user có quyền truy cập exam cụ thể không
     */
    public boolean canAccessExam(Long examId, Long userId, DataScope dataScope) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        switch (dataScope.getCode()) {
            case "OWN":
                return exam.getCreatedBy().equals(userId);

            case "TEAM":
                return user.getTeamIds().contains(exam.getTeamId());

            case "DEPARTMENT":
                return exam.getDepartmentId().equals(user.getDepartmentId());

            case "ORGANIZATION":
                return exam.getOrganizationId().equals(user.getOrganizationId());

            case "ALL":
                return true;

            default:
                return false;
        }
    }
}
```

#### 4.3.2 Repository với Data Scope

```java
package com.certimaster.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    // OWN scope: Chỉ của user
    List<Exam> findByCreatedBy(Long userId);

    // TEAM scope: Của team
    @Query("SELECT e FROM Exam e WHERE e.teamId IN :teamIds")
    List<Exam> findByTeamIds(@Param("teamIds") List<Long> teamIds);

    // DEPARTMENT scope: Của phòng ban
    List<Exam> findByDepartmentId(Long departmentId);

    // ORGANIZATION scope: Của tổ chức
    List<Exam> findByOrganizationId(Long organizationId);

    // Dynamic scope query
    @Query("SELECT e FROM Exam e WHERE " +
            "(:scope = 'OWN' AND e.createdBy = :userId) OR " +
            "(:scope = 'TEAM' AND e.teamId IN :teamIds) OR " +
            "(:scope = 'DEPARTMENT' AND e.departmentId = :departmentId) OR " +
            "(:scope = 'ORGANIZATION' AND e.organizationId = :organizationId) OR " +
            "(:scope = 'ALL')")
    List<Exam> findByScope(
            @Param("scope") String scope,
            @Param("userId") Long userId,
            @Param("teamIds") List<Long> teamIds,
            @Param("departmentId") Long departmentId,
            @Param("organizationId") Long organizationId
    );
}
```


### 4.4 Aspect-Oriented Permission Checking

#### 4.4.1 Custom Annotation

```java
package com.certimaster.auth_service.security.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * Resource code cần kiểm tra
     */
    String value();

    /**
     * Context type (optional)
     */
    String contextType() default "";

    /**
     * Tên parameter chứa context ID (optional)
     */
    String contextIdParam() default "";

    /**
     * Message khi không có quyền
     */
    String message() default "You don't have permission to access this resource";
}
```

#### 4.4.2 Permission Aspect

```java
package com.certimaster.auth_service.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission)
            throws Throwable {

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnauthorizedException("User not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // Get resource code
        String resourceCode = requirePermission.value();

        // Get context (if specified)
        String contextType = requirePermission.contextType();
        Long contextId = null;

        if (!contextType.isEmpty() && !requirePermission.contextIdParam().isEmpty()) {
            // Extract context ID from method parameters
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            for (int i = 0; i < paramNames.length; i++) {
                if (paramNames[i].equals(requirePermission.contextIdParam())) {
                    contextId = (Long) paramValues[i];
                    break;
                }
            }
        }

        // Check permission
        PermissionResult permission = permissionService.hasPermission(
                userId, resourceCode, contextType.isEmpty() ? null : contextType, contextId
        );

        if (!permission.isGranted()) {
            throw new ForbiddenException(requirePermission.message());
        }

        // Store data scope in thread local for later use
        DataScopeContext.setDataScope(permission.getDataScope());

        try {
            return joinPoint.proceed();
        } finally {
            DataScopeContext.clear();
        }
    }
}
```

#### 4.4.3 Using Custom Annotation

```java
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    /**
     * Sử dụng @RequirePermission annotation
     */
    @GetMapping
    @RequirePermission(value = "EXAM_LIST_API", message = "You cannot view exams")
    public ResponseEntity<List<ExamDto>> getExams() {
        // Permission đã được check bởi aspect
        // Data scope có sẵn trong DataScopeContext
        List<ExamDto> exams = examService.getExams();
        return ResponseEntity.ok(exams);
    }
    
    /**
     * Với context
     */
    @GetMapping("/organization/{organizationId}")
    @RequirePermission(
        value = "EXAM_LIST_API",
        contextType = "ORGANIZATION",
        contextIdParam = "organizationId",
        message = "You cannot view exams in this organization"
    )
    public ResponseEntity<List<ExamDto>> getExamsByOrganization(
            @PathVariable Long organizationId) {
        
        List<ExamDto> exams = examService.getExamsByOrganization(organizationId);
        return ResponseEntity.ok(exams);
    }
    
    @PostMapping
    @RequirePermission(value = "EXAM_CREATE_API", message = "You cannot create exams")
    public ResponseEntity<ExamDto> createExam(@RequestBody CreateExamRequest request) {
        ExamDto exam = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(exam);
    }
    
    @DeleteMapping("/{id}")
    @RequirePermission(value = "EXAM_DELETE_API", message = "You cannot delete exams")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
```


### 4.5 Exception Handling

#### 4.5.1 Permission Exception Classes

```java
package com.certimaster.auth_service.exception;

public class ForbiddenException extends RuntimeException {
    private String resourceCode;
    private String reason;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, String resourceCode, String reason) {
        super(message);
        this.resourceCode = resourceCode;
        this.reason = reason;
    }

    // Getters
}

public class InsufficientDataScopeException extends ForbiddenException {
    private String requiredScope;
    private String userScope;

    public InsufficientDataScopeException(String message, String requiredScope, String userScope) {
        super(message);
        this.requiredScope = requiredScope;
        this.userScope = userScope;
    }

    // Getters
}
```

#### 4.5.2 Global Exception Handler

```java
package com.certimaster.auth_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PermissionExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(ex.getMessage())
                .resourceCode(ex.getResourceCode())
                .reason(ex.getReason())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InsufficientDataScopeException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientDataScopeException(
            InsufficientDataScopeException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Insufficient Data Scope")
                .message(ex.getMessage())
                .details(Map.of(
                        "requiredScope", ex.getRequiredScope(),
                        "userScope", ex.getUserScope()
                ))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```


### 4.6 Utility Classes

#### 4.6.1 DataScopeContext (ThreadLocal)

```java
package com.certimaster.auth_service.util;

public class DataScopeContext {
    private static final ThreadLocal<DataScope> dataScopeHolder = new ThreadLocal<>();

    public static void setDataScope(DataScope dataScope) {
        dataScopeHolder.set(dataScope);
    }

    public static DataScope getDataScope() {
        return dataScopeHolder.get();
    }

    public static void clear() {
        dataScopeHolder.remove();
    }
}
```

#### 4.6.2 RequestContextHolder

```java
package com.certimaster.auth_service.util;

public class RequestContextHolder {
    private static final ThreadLocal<String> contextTypeHolder = new ThreadLocal<>();
    private static final ThreadLocal<Long> contextIdHolder = new ThreadLocal<>();

    public static void setContext(String contextType, Long contextId) {
        contextTypeHolder.set(contextType);
        contextIdHolder.set(contextId);
    }

    public static String getContextType() {
        return contextTypeHolder.get();
    }

    public static Long getContextId() {
        return contextIdHolder.get();
    }

    public static void clear() {
        contextTypeHolder.remove();
        contextIdHolder.remove();
    }
}
```

#### 4.6.3 Context Interceptor

```java
package com.certimaster.auth_service.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract context from headers
        String contextType = request.getHeader("X-Context-Type");
        String contextIdStr = request.getHeader("X-Context-Id");

        if (contextType != null && contextIdStr != null) {
            Long contextId = Long.parseLong(contextIdStr);
            RequestContextHolder.setContext(contextType, contextId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // Clean up thread local
        RequestContextHolder.clear();
        DataScopeContext.clear();
    }
}
```


### 4.7 Complete Example: End-to-End Flow

#### 4.7.1 Scenario

User A muốn xem danh sách kỳ thi trong Organization 1, nơi user có role MANAGER.

#### 4.7.2 Request Flow

```
1. Client gửi request:
   GET /api/v1/exams
   Headers:
     Authorization: Bearer <jwt_token>
     X-Context-Type: ORGANIZATION
     X-Context-Id: 1

2. ContextInterceptor extract context:
   RequestContextHolder.setContext("ORGANIZATION", 1)

3. Spring Security authenticate user:
   JWT token → CustomUserDetails (userId = 123)

4. @PreAuthorize check permission:
   hasPermission(null, 'EXAM_LIST_API')
   
5. CustomPermissionEvaluator.hasPermission():
   a. Check UserPermission DENY → Không có
   b. Check UserPermission GRANT → Không có
   c. Get UserRoles với context (ORGANIZATION, 1) → MANAGER role
   d. Check RolePermission (MANAGER, EXAM_LIST_API) → Có, với DEPARTMENT scope
   e. Return: granted = true, dataScope = DEPARTMENT

6. Controller method execute:
   ExamService.getExamsWithScope(1, 123, DEPARTMENT)

7. Service apply data scope filter:
   WHERE organization_id = 1 AND department_id = user.departmentId

8. Return filtered exams to client

9. ContextInterceptor.afterCompletion():
   RequestContextHolder.clear()
   DataScopeContext.clear()
```

#### 4.7.3 Complete Code Example

```java
// 1. Controller
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @GetMapping
    @PreAuthorize("hasPermission(null, 'EXAM_LIST_API')")
    public ResponseEntity<List<ExamDto>> getExams(
            @RequestHeader("X-Context-Id") Long organizationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Get data scope from thread local
        DataScope dataScope = DataScopeContext.getDataScope();
        
        List<ExamDto> exams = examService.getExamsWithScope(
            organizationId, 
            userDetails.getUserId(), 
            dataScope
        );
        
        return ResponseEntity.ok(exams);
    }
}

// 2. Service
@Service
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<ExamDto> getExamsWithScope(Long organizationId, Long userId, DataScope dataScope) {
        User user = userRepository.findById(userId).orElseThrow();
        
        List<Exam> exams;
        
        switch (dataScope.getCode()) {
            case "OWN":
                exams = examRepository.findByOrganizationIdAndCreatedBy(organizationId, userId);
                break;
            case "DEPARTMENT":
                exams = examRepository.findByOrganizationIdAndDepartmentId(
                    organizationId, user.getDepartmentId()
                );
                break;
            case "ORGANIZATION":
                exams = examRepository.findByOrganizationId(organizationId);
                break;
            case "ALL":
                exams = examRepository.findAll();
                break;
            default:
                throw new IllegalArgumentException("Unknown scope: " + dataScope.getCode());
        }
        
        return exams.stream().map(this::toDto).collect(Collectors.toList());
    }
}

// 3. Repository
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByOrganizationIdAndCreatedBy(Long organizationId, Long createdBy);
    List<Exam> findByOrganizationIdAndDepartmentId(Long organizationId, Long departmentId);
    List<Exam> findByOrganizationId(Long organizationId);
}
```

### 4.8 Best Practices

1. **Luôn check permission ở controller layer**: Sử dụng `@PreAuthorize` hoặc manual check
2. **Apply data scope filter ở service layer**: Đảm bảo user chỉ thấy data được phép
3. **Sử dụng ThreadLocal cho context**: Tránh pass context qua nhiều layers
4. **Clean up ThreadLocal**: Luôn clear trong `finally` hoặc interceptor
5. **Cache permissions**: Cache user permissions trong Redis (TTL 5-15 phút)
6. **Log permission checks**: Log tất cả permission denials để audit
7. **Fail secure**: Mặc định deny nếu không chắc chắn
8. **Test thoroughly**: Viết integration tests cho permission flows

---

## Tổng Kết

Tài liệu này cung cấp hướng dẫn chi tiết về:

1. **Thêm Module và Feature**: Cách tạo module, feature, và resource mới
2. **Quản Lý Permissions**: Cách gán role permissions và user permissions (GRANT/DENY)
3. **Context-Based Permissions**: Cách triển khai permissions theo context (organization, project, team)
4. **Kiểm Tra Permissions**: Cách implement permission checking trong code với Spring Security

Hệ thống phân quyền này cung cấp:
- ✅ Granular permissions (resource-level)
- ✅ Data scope control (OWN, TEAM, DEPARTMENT, ORGANIZATION, ALL)
- ✅ Context-based roles (multi-tenant support)
- ✅ Temporal permissions (expiry support)
- ✅ User-specific overrides (GRANT/DENY)
- ✅ Flexible conditions (JSONB)
- ✅ Audit trail (granted_by, granted_at, reason)

