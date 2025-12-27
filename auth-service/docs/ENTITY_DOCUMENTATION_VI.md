# Tài Liệu Chi Tiết Về Các Entity Trong Hệ Thống Phân Quyền (v2.0)

## Mục Lục

1. [Tổng Quan Hệ Thống](#1-tổng-quan-hệ-thống)
2. [Kiến Trúc RBAC Đơn Giản](#2-kiến-trúc-rbac-đơn-giản)
3. [Sơ Đồ Quan Hệ Entity](#3-sơ-đồ-quan-hệ-entity)
4. [Chi Tiết Các Entity](#4-chi-tiết-các-entity)
5. [JWT Token Structure](#5-jwt-token-structure)
6. [Permission Format](#6-permission-format)
7. [Hướng Dẫn Sử Dụng](#7-hướng-dẫn-sử-dụng)

---

## 1. Tổng Quan Hệ Thống

### 1.1. Giới Thiệu

Hệ thống auth-service v2.0 triển khai mô hình RBAC (Role-Based Access Control) đơn giản với **5 entity** chính, được tối ưu hóa cho ứng dụng CertiMaster.

### 1.2. Cải Tiến So Với v1.0

| Tiêu chí | v1.0 | v2.0 |
|----------|------|------|
| Số lượng entity | 11 | 5 |
| Số JOIN tối đa | 5-6 | 2 |
| Permission format | Complex (Resource → Feature → Module) | Simple string (resource:action) |
| Authorization | Database query mỗi request | JWT embedded permissions |
| DataScope | OWN, TEAM, DEPARTMENT, ORGANIZATION, ALL | Không cần (ứng dụng đơn giản) |

### 1.3. Đặc Điểm Nổi Bật

- **Đơn giản**: Chỉ 5 bảng database
- **Hiệu suất cao**: Tối đa 2 JOIN operations
- **Stateless**: Permissions embedded trong JWT token
- **Bảo mật**: BCrypt password hashing (strength 12), RS256 JWT signing
- **Dễ bảo trì**: Code dễ đọc, dễ test

### 1.4. Công Nghệ Sử Dụng

- **Framework**: Spring Boot 3.x với JPA/Hibernate
- **Database**: PostgreSQL 12+
- **Security**: Spring Security 6.x
- **JWT**: jjwt với RS256 algorithm
- **Password Hashing**: BCrypt (strength 12)

---

## 2. Kiến Trúc RBAC Đơn Giản

### 2.1. Mô Hình RBAC

```
User ──> user_roles ──> Role ──> role_permissions (strings)
```

### 2.2. Luồng Authentication

```
1. User login với username/password
2. Server validate credentials
3. Server generate JWT access token (15 phút) + refresh token (7 ngày)
4. JWT chứa: userId, username, roles, permissions
5. Client gửi JWT trong Authorization header
6. JwtAuthenticationFilter validate và populate SecurityContext
```

### 2.3. Luồng Authorization

```
1. Request đến với JWT token
2. JwtAuthenticationFilter extract permissions từ token
3. Spring Security check @PreAuthorize annotation
4. Nếu có permission → cho phép
5. Nếu không có → 403 Forbidden
```

---

## 3. Sơ Đồ Quan Hệ Entity

### 3.1. Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    User ||--o{ user_roles : "has"
    User ||--o{ RefreshToken : "has"
    Role ||--o{ user_roles : "assigned to"
    Role ||--o{ role_permissions : "has"
    
    User {
        Long id PK
        String email UK
        String username UK
        String passwordHash
        String fullName
        String avatarUrl
        String phone
        String status
        Boolean emailVerified
    }
    
    Role {
        Long id PK
        String code UK
        String name
        String description
    }
    
    user_roles {
        Long user_id FK
        Long role_id FK
    }
    
    role_permissions {
        Long role_id FK
        String permission
    }
    
    RefreshToken {
        Long id PK
        Long userId FK
        String token UK
        LocalDateTime expiresAt
    }
```

### 3.2. Danh Sách Entity

| # | Entity | Bảng Database | Mô Tả |
|---|--------|---------------|-------|
| 1 | User | users | Thông tin người dùng |
| 2 | Role | roles | Vai trò (ADMIN, INSTRUCTOR, STUDENT) |
| 3 | - | user_roles | Junction table User-Role |
| 4 | - | role_permissions | Permissions của Role (strings) |
| 5 | RefreshToken | refresh_tokens | Quản lý refresh token |

---

## 4. Chi Tiết Các Entity

### 4.1. User Entity

**Mục đích**: Lưu trữ thông tin người dùng và authentication.

**Bảng database**: `users`

#### Các Trường (Fields)

| Tên Trường | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả |
|------------|--------------|-----------|-------|
| `id` | Long | PK, AUTO | Primary key |
| `email` | String(300) | NOT NULL, UNIQUE | Email đăng nhập |
| `username` | String(100) | NOT NULL, UNIQUE | Tên đăng nhập |
| `passwordHash` | String(255) | NOT NULL | Mật khẩu BCrypt hash |
| `fullName` | String(300) | NULLABLE | Tên đầy đủ |
| `avatarUrl` | String(500) | NULLABLE | URL ảnh đại diện |
| `phone` | String(20) | NULLABLE | Số điện thoại |
| `status` | String(20) | DEFAULT 'ACTIVE' | ACTIVE, INACTIVE, LOCKED, SUSPENDED |
| `emailVerified` | Boolean | DEFAULT false | Email đã xác thực |

#### Quan Hệ (Relationships)

```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles;
```

#### Helper Methods

```java
public void addRole(Role role)           // Thêm role
public void removeRole(Role role)        // Xóa role
public boolean hasRole(String roleCode)  // Kiểm tra có role
public Set<String> getAllPermissions()   // Lấy tất cả permissions
```

#### Ví Dụ Sử Dụng

```java
// Tạo user mới
User user = User.builder()
    .email("student@example.com")
    .username("student01")
    .passwordHash(passwordEncoder.encode("password123"))
    .fullName("Nguyễn Văn A")
    .status("ACTIVE")
    .emailVerified(false)
    .build();

// Thêm role STUDENT
Role studentRole = roleRepository.findByCode("STUDENT").orElseThrow();
user.addRole(studentRole);
userRepository.save(user);
```

---

### 4.2. Role Entity

**Mục đích**: Định nghĩa vai trò trong hệ thống.

**Bảng database**: `roles`

#### Các Trường (Fields)

| Tên Trường | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả |
|------------|--------------|-----------|-------|
| `id` | Long | PK, AUTO | Primary key |
| `code` | String(50) | NOT NULL, UNIQUE | Mã vai trò (ADMIN, INSTRUCTOR, STUDENT) |
| `name` | String(100) | NOT NULL | Tên hiển thị |
| `description` | Text | NULLABLE | Mô tả vai trò |

#### Permissions (ElementCollection)

```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(
    name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id")
)
@Column(name = "permission")
private Set<String> permissions;
```

#### Predefined Roles

| Role | Code | Permissions |
|------|------|-------------|
| Admin | ADMIN | Tất cả permissions |
| Instructor | INSTRUCTOR | exam:*, question:*, result:read_all |
| Student | STUDENT | exam:read, question:read, result:read |

---

### 4.3. RefreshToken Entity

**Mục đích**: Quản lý refresh tokens cho JWT authentication.

**Bảng database**: `refresh_tokens`

#### Các Trường (Fields)

| Tên Trường | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả |
|------------|--------------|-----------|-------|
| `id` | Long | PK, AUTO | Primary key |
| `userId` | Long | NOT NULL, FK | User sở hữu token |
| `token` | String(500) | NOT NULL, UNIQUE | Refresh token string |
| `expiresAt` | LocalDateTime | NOT NULL | Thời điểm hết hạn (7 ngày) |

---

## 5. JWT Token Structure

### 5.1. Access Token Claims

```json
{
  "sub": "123",
  "username": "student01",
  "email": "student@example.com",
  "roles": ["STUDENT"],
  "permissions": ["exam:read", "question:read", "result:read"],
  "type": "access",
  "iss": "certimaster-auth",
  "iat": 1703577600,
  "exp": 1703578500
}
```

### 5.2. Token Configuration

| Token Type | Expiration | Algorithm |
|------------|------------|-----------|
| Access Token | 15 phút (900000 ms) | RS256 |
| Refresh Token | 7 ngày (604800000 ms) | RS256 |

---

## 6. Permission Format

### 6.1. Format

```
resource:action
```

- **resource**: Tên tài nguyên (lowercase)
- **action**: Hành động (lowercase)

### 6.2. Danh Sách Permissions

| Permission | Mô Tả |
|------------|-------|
| `user:create` | Tạo user mới |
| `user:read` | Xem thông tin user |
| `user:update` | Cập nhật user |
| `user:delete` | Xóa user |
| `user:read_all` | Xem tất cả users |
| `role:create` | Tạo role mới |
| `role:read` | Xem thông tin role |
| `role:update` | Cập nhật role |
| `role:delete` | Xóa role |
| `role:assign` | Gán role cho user |
| `exam:create` | Tạo exam mới |
| `exam:read` | Xem exam |
| `exam:update` | Cập nhật exam |
| `exam:delete` | Xóa exam |
| `question:create` | Tạo question mới |
| `question:read` | Xem question |
| `question:update` | Cập nhật question |
| `question:delete` | Xóa question |
| `result:read` | Xem kết quả của mình |
| `result:read_all` | Xem tất cả kết quả |
| `system:config` | Cấu hình hệ thống |
| `system:audit` | Xem audit logs |

### 6.3. Sử Dụng Trong Code

```java
// Import constants
import static com.certimaster.auth_service.constant.Permissions.*;

// Sử dụng với @PreAuthorize
@PreAuthorize("hasAuthority('" + EXAM_CREATE + "')")
public ResponseDto<ExamResponse> createExam(ExamRequest request) {
    // ...
}

// Hoặc hardcode string
@PreAuthorize("hasAuthority('exam:create')")
public ResponseDto<ExamResponse> createExam(ExamRequest request) {
    // ...
}
```

---

## 7. Hướng Dẫn Sử Dụng

### 7.1. Registration Flow

```java
// 1. User đăng ký
POST /api/v1/auth/register
{
    "email": "student@example.com",
    "username": "student01",
    "password": "password123",
    "fullName": "Nguyễn Văn A"
}

// 2. Server tự động:
//    - Hash password với BCrypt (strength 12)
//    - Gán role STUDENT
//    - Tạo email verification token
//    - Gửi email xác thực
```

### 7.2. Login Flow

```java
// 1. User đăng nhập
POST /api/v1/auth/login
{
    "username": "student01",
    "password": "password123"
}

// 2. Server trả về
{
    "success": true,
    "data": {
        "user": { ... },
        "tokens": {
            "accessToken": "eyJhbGciOiJSUzI1NiIs...",
            "refreshToken": "eyJhbGciOiJSUzI1NiIs...",
            "tokenType": "Bearer",
            "expiresIn": 900
        },
        "permissions": ["exam:read", "question:read", "result:read"]
    }
}
```

### 7.3. Protected API Call

```java
// Client gửi request với JWT
GET /api/v1/exams
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...

// Server validate JWT và check permission
@PreAuthorize("hasAuthority('exam:read')")
public ResponseDto<List<ExamResponse>> getExams() {
    // ...
}
```

### 7.4. Token Refresh

```java
// Khi access token hết hạn
POST /api/v1/auth/refresh
{
    "refreshToken": "eyJhbGciOiJSUzI1NiIs..."
}

// Server trả về access token mới
{
    "success": true,
    "data": {
        "accessToken": "eyJhbGciOiJSUzI1NiIs...",
        "expiresIn": 900
    }
}
```

---

## Changelog

### v2.0 (2024-12-27)
- Simplified từ 11 entity xuống 5 entity
- Chuyển sang string-based permissions
- JWT embedded permissions cho stateless authorization
- Tối ưu performance (max 2 JOINs)
- Loại bỏ: Module, Feature, Resource, Action, DataScope, UserPermission, UserRole, RolePermission entities

### v1.0
- Initial complex RBAC implementation với 11 entities
