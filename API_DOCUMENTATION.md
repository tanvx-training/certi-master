# API Documentation - CertiMaster Backend

Tài liệu API chi tiết cho dự án front-end tích hợp với back-end CertiMaster.

## Thông tin chung

### Base URLs
| Service | Port | Base URL |
|---------|------|----------|
| Auth Service | 8081 | `http://localhost:8081/api/v1` |
| Blog Service | 8083 | `http://localhost:8083/api/v1` |

### Response Format
Tất cả API đều trả về cấu trúc `ResponseDto<T>`:

```json
{
  "success": true,
  "message": "Optional message",
  "data": { /* Response data */ },
  "errorCode": "ERROR_CODE (only on error)",
  "metadata": { /* Optional metadata */ },
  "timestamp": "28-12-2024T10:30:00"
}
```

### Pagination Format
Các API có phân trang trả về `PageDto<T>`:

```json
{
  "content": [ /* Array of items */ ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "empty": false
}
```

### Authentication
- Sử dụng JWT Bearer Token
- Header: `Authorization: Bearer <access_token>`
- Access token hết hạn sau 15 phút
- Refresh token hết hạn sau 7 ngày

---

## AUTH SERVICE API

### 1. Register - Đăng ký tài khoản
```
POST /api/v1/auth/register
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "username": "username123",
  "password": "Password@123",
  "fullName": "Nguyễn Văn A",
  "phone": "+84901234567",
  "acceptTerms": true
}
```

**Validation Rules:**
- `email`: Required, valid email format, max 255 chars
- `username`: Required, 3-50 chars, chỉ chứa letters, numbers, underscore, hyphen
- `password`: Required, min 8 chars, phải có uppercase, lowercase, number, special char
- `fullName`: Required, 2-255 chars
- `phone`: Optional, format E.164 (+84...)
- `acceptTerms`: Required, must be true

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "username": "username123",
    "fullName": "Nguyễn Văn A",
    "avatarUrl": null,
    "phone": "+84901234567",
    "status": "ACTIVE",
    "emailVerified": false,
    "roles": [
      {
        "id": 1,
        "code": "USER",
        "name": "User",
        "isPrimary": true
      }
    ],
    "createdAt": "28-12-2024T10:30:00"
  }
}
```

---

### 2. Login - Đăng nhập
```
POST /api/v1/auth/login
```

**Request Body:**
```json
{
  "username": "username123",
  "password": "Password@123",
  "rememberMe": false
}
```

**Validation Rules:**
- `username`: Required (có thể là username hoặc email)
- `password`: Required
- `rememberMe`: Optional, default false

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com",
      "username": "username123",
      "fullName": "Nguyễn Văn A",
      "avatar": "https://...",
      "roles": [
        {
          "id": 1,
          "code": "USER",
          "name": "User",
          "isPrimary": true
        }
      ]
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIs...",
      "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
      "tokenType": "Bearer",
      "expiresIn": 900000
    },
    "permissions": ["BLOG_CREATE", "BLOG_UPDATE", "..."]
  }
}
```

---

### 3. Get Current User - Lấy thông tin user hiện tại
```
GET /api/v1/auth/current
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com",
      "username": "username123",
      "fullName": "Nguyễn Văn A",
      "avatarUrl": "https://...",
      "phone": "+84901234567",
      "status": "ACTIVE",
      "emailVerified": true,
      "roles": [
        {
          "id": 1,
          "code": "USER",
          "name": "User",
          "isPrimary": true
        }
      ]
    },
    "permissions": ["BLOG_CREATE", "BLOG_UPDATE", "..."]
  }
}
```

---

### 4. Refresh Token - Làm mới access token
```
POST /api/v1/auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": { /* UserInfo */ },
    "tokens": {
      "accessToken": "new_access_token...",
      "refreshToken": "new_refresh_token...",
      "tokenType": "Bearer",
      "expiresIn": 900000
    },
    "permissions": ["..."]
  }
}
```

---

### 5. Logout - Đăng xuất
```
POST /api/v1/auth/logout
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

---

### 6. Verify Email - Xác thực email
```
GET /api/v1/auth/verify-email?token=xxx
```

**Query Parameters:**
- `token`: Required - Token xác thực email

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

---

## BLOG SERVICE API

### Posts API

#### 1. Search Posts - Tìm kiếm bài viết
```
GET /api/v1/posts
```

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| keyword | string | - | Tìm kiếm trong title và content |
| status | string | - | DRAFT, PUBLISHED, ARCHIVED |
| authorId | number | - | Filter theo author |
| categoryId | number | - | Filter theo category |
| tagId | number | - | Filter theo tag |
| publishedFrom | datetime | - | Từ ngày publish (ISO format) |
| publishedTo | datetime | - | Đến ngày publish (ISO format) |
| page | number | 0 | Số trang (0-based) |
| size | number | 10 | Số items/trang |
| sortBy | string | createdAt | createdAt, publishedAt, viewsCount, likesCount |
| sortDirection | string | DESC | ASC, DESC |

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Tiêu đề bài viết",
        "slug": "tieu-de-bai-viet",
        "excerpt": "Tóm tắt ngắn...",
        "featuredImage": "https://...",
        "authorId": 1,
        "status": "PUBLISHED",
        "publishedAt": "28-12-2024T10:30:00",
        "viewsCount": 100,
        "likesCount": 25,
        "commentsCount": 10,
        "readingTimeMinutes": 5,
        "categories": [
          { "id": 1, "name": "Tech", "slug": "tech", "postCount": 50 }
        ],
        "tags": [
          { "id": 1, "name": "Java", "slug": "java", "postCount": 30 }
        ],
        "createdAt": "28-12-2024T10:00:00",
        "updatedAt": "28-12-2024T10:30:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false,
    "first": true,
    "empty": false
  }
}
```

---

#### 2. Get Post by Slug - Lấy chi tiết bài viết
```
GET /api/v1/posts/{slug}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Tiêu đề bài viết",
    "slug": "tieu-de-bai-viet",
    "content": "# Markdown content...",
    "contentHtml": "<h1>Rendered HTML...</h1>",
    "excerpt": "Tóm tắt ngắn...",
    "featuredImage": "https://...",
    "authorId": 1,
    "status": "PUBLISHED",
    "publishedAt": "28-12-2024T10:30:00",
    "viewsCount": 100,
    "likesCount": 25,
    "commentsCount": 10,
    "readingTimeMinutes": 5,
    "seoTitle": "SEO Title",
    "seoDescription": "SEO Description",
    "seoKeywords": "keyword1, keyword2",
    "categories": [{ "id": 1, "name": "Tech", "slug": "tech" }],
    "tags": [{ "id": 1, "name": "Java", "slug": "java" }],
    "currentUserReaction": {
      "id": 1,
      "userId": 1,
      "reactionType": "LIKE",
      "createdAt": "28-12-2024T10:30:00"
    },
    "createdAt": "28-12-2024T10:00:00",
    "updatedAt": "28-12-2024T10:30:00"
  }
}
```

---

#### 3. Create Post - Tạo bài viết mới
```
POST /api/v1/posts
Authorization: Bearer <access_token>
Permission: BLOG_CREATE
```

**Request Body:**
```json
{
  "title": "Tiêu đề bài viết",
  "content": "# Markdown content...",
  "excerpt": "Tóm tắt ngắn (optional)",
  "featuredImage": "https://... (optional)",
  "categoryIds": [1, 2],
  "tagNames": ["Java", "Spring Boot"],
  "seoTitle": "SEO Title (optional)",
  "seoDescription": "SEO Description (optional)",
  "seoKeywords": "keyword1, keyword2 (optional)"
}
```

**Validation Rules:**
- `title`: Required, max 255 chars
- `content`: Required (Markdown)
- `excerpt`: Optional, max 500 chars
- `featuredImage`: Optional, max 500 chars
- `categoryIds`: Optional, array of category IDs
- `tagNames`: Optional, array of tag names (tự động tạo tag mới nếu chưa tồn tại)
- `seoTitle`: Optional, max 255 chars
- `seoDescription`: Optional, max 500 chars
- `seoKeywords`: Optional, max 500 chars

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Post created successfully",
  "data": { /* PostResponse */ }
}
```

---

#### 4. Update Post - Cập nhật bài viết
```
PUT /api/v1/posts/{id}
Authorization: Bearer <access_token>
Permission: BLOG_UPDATE hoặc BLOG_CREATE (owner)
```

**Request Body:** (giống Create Post)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Post updated successfully",
  "data": { /* PostResponse */ }
}
```

---

#### 5. Delete Post - Xóa bài viết
```
DELETE /api/v1/posts/{id}
Authorization: Bearer <access_token>
Permission: BLOG_DELETE hoặc BLOG_CREATE (owner)
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Post deleted successfully",
  "data": null
}
```

---

#### 6. Publish Post - Xuất bản bài viết
```
POST /api/v1/posts/{id}/publish
Authorization: Bearer <access_token>
Permission: BLOG_PUBLISH
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Post published successfully",
  "data": { /* PostResponse with status: PUBLISHED */ }
}
```

---

#### 7. Archive Post - Lưu trữ bài viết
```
POST /api/v1/posts/{id}/archive
Authorization: Bearer <access_token>
Permission: BLOG_PUBLISH
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Post archived successfully",
  "data": { /* PostResponse with status: ARCHIVED */ }
}
```

---

#### 8. Increment View Count - Tăng lượt xem
```
POST /api/v1/posts/{id}/view
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "View count incremented",
  "data": null
}
```

---

#### 9. Get Posts by Author - Lấy bài viết theo tác giả
```
GET /api/v1/posts/author/{authorId}?page=0&size=10
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": { /* PageDto<PostResponse> */ }
}
```

---

### Categories API

#### 1. Get All Categories
```
GET /api/v1/categories
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Technology",
      "slug": "technology",
      "description": "Tech articles",
      "postCount": 50,
      "createdAt": "28-12-2024T10:00:00"
    }
  ]
}
```

---

#### 2. Get Category by Slug
```
GET /api/v1/categories/{slug}
```

---

#### 3. Get Posts by Category
```
GET /api/v1/categories/{slug}/posts?page=0&size=10
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": { /* PageDto<PostResponse> */ }
}
```

---

#### 4. Create Category
```
POST /api/v1/categories
Authorization: Bearer <access_token>
Permission: BLOG_ADMIN hoặc ADMIN role
```

**Request Body:**
```json
{
  "name": "Technology",
  "description": "Tech articles (optional)"
}
```

**Validation:**
- `name`: Required, max 100 chars
- `description`: Optional, max 500 chars

---

#### 5. Update Category
```
PUT /api/v1/categories/{id}
Authorization: Bearer <access_token>
Permission: BLOG_ADMIN hoặc ADMIN role
```

---

#### 6. Delete Category
```
DELETE /api/v1/categories/{id}
Authorization: Bearer <access_token>
Permission: BLOG_ADMIN hoặc ADMIN role
```

---

### Tags API

#### 1. Get All Tags (with post counts)
```
GET /api/v1/tags
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Java",
      "slug": "java",
      "postCount": 30,
      "createdAt": "28-12-2024T10:00:00"
    }
  ]
}
```

---

#### 2. Get Tag by Slug
```
GET /api/v1/tags/{slug}
```

---

#### 3. Get Posts by Tag
```
GET /api/v1/tags/{slug}/posts?page=0&size=10
```

---

#### 4. Create Tag
```
POST /api/v1/tags
Authorization: Bearer <access_token>
Permission: isAuthenticated()
```

**Request Body:**
```json
{
  "name": "Java"
}
```

**Validation:**
- `name`: Required, max 100 chars

---

#### 5. Update Tag
```
PUT /api/v1/tags/{id}
Authorization: Bearer <access_token>
Permission: BLOG_ADMIN hoặc ADMIN role
```

---

#### 6. Delete Tag
```
DELETE /api/v1/tags/{id}
Authorization: Bearer <access_token>
Permission: BLOG_ADMIN hoặc ADMIN role
```

---

### Comments API

#### 1. Get Comments (Hierarchical)
```
GET /api/v1/posts/{postId}/comments
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "postId": 1,
      "userId": 1,
      "parentCommentId": null,
      "content": "Great article!",
      "likesCount": 5,
      "isApproved": true,
      "replies": [
        {
          "id": 2,
          "postId": 1,
          "userId": 2,
          "parentCommentId": 1,
          "content": "Thanks!",
          "likesCount": 2,
          "isApproved": true,
          "replies": [],
          "currentUserReaction": null
        }
      ],
      "currentUserReaction": {
        "id": 1,
        "userId": 1,
        "reactionType": "LIKE",
        "createdAt": "28-12-2024T10:30:00"
      },
      "createdAt": "28-12-2024T10:00:00"
    }
  ]
}
```

---

#### 2. Get Comments (Paginated)
```
GET /api/v1/posts/{postId}/comments/paginated?page=0&size=10
```

---

#### 3. Add Comment
```
POST /api/v1/posts/{postId}/comments
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "content": "Great article!",
  "parentCommentId": null
}
```

**Validation:**
- `content`: Required, max 5000 chars
- `parentCommentId`: Optional (null for top-level comment)

---

#### 4. Add Reply
```
POST /api/v1/posts/{postId}/comments/{commentId}/replies
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "content": "Thanks for your feedback!"
}
```

---

#### 5. Update Comment
```
PUT /api/v1/posts/{postId}/comments/{commentId}
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "content": "Updated comment content"
}
```

---

#### 6. Delete Comment
```
DELETE /api/v1/posts/{postId}/comments/{commentId}
Authorization: Bearer <access_token>
```

---

### Reactions API

#### 1. Add/Update Post Reaction
```
POST /api/v1/reactions/posts/{postId}
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "reactionType": "LIKE"
}
```

**Reaction Types:** `LIKE`, `LOVE`, `HELPFUL`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Reaction added successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "reactionType": "LIKE",
    "createdAt": "28-12-2024T10:30:00"
  }
}
```

---

#### 2. Remove Post Reaction
```
DELETE /api/v1/reactions/posts/{postId}
Authorization: Bearer <access_token>
```

---

#### 3. Get Post Reaction (Current User)
```
GET /api/v1/reactions/posts/{postId}
Authorization: Bearer <access_token>
```

---

#### 4. Add Comment Reaction (Like)
```
POST /api/v1/reactions/comments/{commentId}
Authorization: Bearer <access_token>
```

---

#### 5. Remove Comment Reaction
```
DELETE /api/v1/reactions/comments/{commentId}
Authorization: Bearer <access_token>
```

---

#### 6. Get Comment Reaction (Current User)
```
GET /api/v1/reactions/comments/{commentId}
Authorization: Bearer <access_token>
```

---

## Error Responses

### Common Error Format
```json
{
  "success": false,
  "errorCode": "ERROR_CODE",
  "message": "Error description",
  "timestamp": "28-12-2024T10:30:00"
}
```

### HTTP Status Codes
| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - Missing/invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## Permissions Reference

| Permission | Description |
|------------|-------------|
| BLOG_CREATE | Tạo bài viết mới |
| BLOG_UPDATE | Cập nhật bài viết (bất kỳ) |
| BLOG_DELETE | Xóa bài viết (bất kỳ) |
| BLOG_PUBLISH | Xuất bản/Lưu trữ bài viết |
| BLOG_ADMIN | Quản lý categories, tags |

---

## TypeScript Interfaces

```typescript
// Common Types
interface ResponseDto<T> {
  success: boolean;
  message?: string;
  data?: T;
  errorCode?: string;
  metadata?: Record<string, any>;
  timestamp: string;
}

interface PageDto<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

// Auth Types
interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  fullName: string;
  phone?: string;
  acceptTerms: boolean;
}

interface LoginResponse {
  user: UserInfo;
  tokens: TokenInfo;
  permissions: string[];
}

interface UserInfo {
  id: number;
  email: string;
  username: string;
  fullName: string;
  avatar?: string;
  roles: RoleInfo[];
}

interface RoleInfo {
  id: number;
  code: string;
  name: string;
  isPrimary: boolean;
}

interface TokenInfo {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

// Blog Types
interface PostRequest {
  title: string;
  content: string;
  excerpt?: string;
  featuredImage?: string;
  categoryIds?: number[];
  tagNames?: string[];
  seoTitle?: string;
  seoDescription?: string;
  seoKeywords?: string;
}

interface PostResponse {
  id: number;
  title: string;
  slug: string;
  excerpt?: string;
  featuredImage?: string;
  authorId: number;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  publishedAt?: string;
  viewsCount: number;
  likesCount: number;
  commentsCount: number;
  readingTimeMinutes: number;
  categories: CategoryResponse[];
  tags: TagResponse[];
  createdAt: string;
  updatedAt: string;
}

interface PostDetailResponse extends PostResponse {
  content: string;
  contentHtml: string;
  seoTitle?: string;
  seoDescription?: string;
  seoKeywords?: string;
  currentUserReaction?: ReactionResponse;
}

interface CategoryResponse {
  id: number;
  name: string;
  slug: string;
  description?: string;
  postCount?: number;
  createdAt: string;
}

interface TagResponse {
  id: number;
  name: string;
  slug: string;
  postCount?: number;
  createdAt: string;
}

interface CommentRequest {
  content: string;
  parentCommentId?: number;
}

interface CommentResponse {
  id: number;
  postId: number;
  userId: number;
  parentCommentId?: number;
  content: string;
  likesCount: number;
  isApproved: boolean;
  replies: CommentResponse[];
  currentUserReaction?: ReactionResponse;
  createdAt: string;
  updatedAt: string;
}

interface ReactionRequest {
  reactionType: 'LIKE' | 'LOVE' | 'HELPFUL';
}

interface ReactionResponse {
  id: number;
  userId: number;
  reactionType: string;
  createdAt: string;
}
```
