# CertiMaster - Guideline Triển Khai Dự Án

## 📋 Mục Lục

1. [Tổng Quan Dự Án](#1-tổng-quan-dự-án)
2. [Kiến Trúc Hệ Thống](#2-kiến-trúc-hệ-thống)
3. [Cấu Trúc Dự Án](#3-cấu-trúc-dự-án)
4. [Technology Stack](#4-technology-stack)
5. [Common Libraries](#5-common-libraries)
6. [Microservices](#6-microservices)
7. [Infrastructure Components](#7-infrastructure-components)
8. [Deployment Strategy](#8-deployment-strategy)
9. [Development Guidelines](#9-development-guidelines)
10. [Best Practices](#10-best-practices)

---

## 1. Tổng Quan Dự Án

**CertiMaster** là một nền tảng quản lý chứng chỉ và thi trực tuyến được xây dựng theo kiến trúc Microservices, sử dụng Spring Boot 3 và Spring Cloud. Hệ thống được thiết kế để có khả năng mở rộng cao, dễ bảo trì và triển khai linh hoạt.

### Mục Tiêu Dự Án
- ✅ Quản lý người dùng và xác thực
- ✅ Tổ chức và quản lý các kỳ thi trực tuyến
- ✅ Theo dõi và phân tích kết quả thi
- ✅ Quản lý blog và nội dung học tập
- ✅ Phân tích và báo cáo chi tiết

---

## 2. Kiến Trúc Hệ Thống

### 2.1. Kiến Trúc Tổng Quan

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│               (Web App, Mobile App, Admin Portal)            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (Port: 8080)                │
│          (Routing, Load Balancing, Rate Limiting)            │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ Config Server│    │Eureka Server │    │   Services   │
│  (Port: 8888)│    │ (Port: 8761) │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
                                                 │
        ┌────────────────────────────────────────┴───────────┐
        │                                                     │
        ▼                                                     ▼
┌──────────────────────────────────────────┐    ┌──────────────────┐
│         Microservices Layer              │    │  Infrastructure  │
│                                          │    │                  │
│  • Auth Service (Port: 8081)            │    │  • PostgreSQL    │
│  • Exam Service (Port: 8082)            │    │  • Redis         │
│  • Result Service (Port: 8083)          │    │  • Kafka         │
│  • Blog Service (Port: 8084)            │    │  • Elasticsearch │
│  • Analytics Service (Port: 8085)       │    │  • Prometheus    │
└──────────────────────────────────────────┘    └──────────────────┘
```

### 2.2. Mô Hình Kiến Trúc

**Kiến trúc Microservices** với các đặc điểm:
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration Management**: Spring Cloud Config
- **Message Broker**: Apache Kafka
- **Caching**: Redis
- **Database**: PostgreSQL (per service)
- **Monitoring**: Prometheus + Grafana

---

## 3. Cấu Trúc Dự Án

### 3.1. Cấu Trúc Thư Mục

```
certi-master/
│
├── pom.xml                                 # Parent POM
│
├── certimaster-common/                     # Common Libraries
│   ├── common-core/                        # Core utilities & DTOs
│   │   ├── src/main/java/com/certimaster/commoncore/
│   │   │   ├── constant/                   # Hằng số ứng dụng
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   │   ├── BaseDto.java
│   │   │   │   ├── ResponseDto.java
│   │   │   │   └── PageDto.java
│   │   │   ├── entity/                     # Base Entities
│   │   │   │   └── BaseEntity.java
│   │   │   ├── enums/                      # Enumerations
│   │   │   │   ├── ErrorCode.java
│   │   │   │   └── Status.java
│   │   │   └── util/                       # Utility classes
│   │   │       ├── DateUtil.java
│   │   │       ├── StringUtil.java
│   │   │       └── ValidationUtil.java
│   │   └── pom.xml
│   │
│   ├── common-exception/                   # Exception Handling
│   │   ├── src/main/java/com/certimaster/commonexception/
│   │   │   ├── handler/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── model/
│   │   │       ├── BusinessException.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── UnauthorizedException.java
│   │   │       └── ValidationException.java
│   │   └── pom.xml
│   │
│   ├── common-security/                    # Security configurations
│   │   └── pom.xml
│   │
│   ├── common-redis/                       # Redis configurations
│   │   └── pom.xml
│   │
│   ├── common-kafka/                       # Kafka configurations
│   │   └── pom.xml
│   │
│   └── common-logging/                     # Logging configurations
│       └── pom.xml
│
├── certimaster-services/                   # Microservices
│   ├── auth-service/                       # Authentication & Authorization
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   ├── exam-service/                       # Exam Management
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   ├── result-service/                     # Result Processing
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   ├── blog-service/                       # Blog & Content Management
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   └── analytics-service/                  # Analytics & Reporting
│       ├── src/main/java/
│       └── pom.xml
│
├── certimaster-infrastructure/             # Infrastructure Services
│   ├── api-gateway/                        # API Gateway
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   ├── eureka-server/                      # Service Discovery
│   │   ├── src/main/java/
│   │   └── pom.xml
│   │
│   └── config-server/                      # Configuration Server
│       ├── src/main/java/
│       └── pom.xml
│
└── certimaster-deployment/                 # Deployment Configurations
    ├── docker/                             # Docker configurations
    ├── kubernetes/                         # K8s manifests
    ├── jenkins/                            # CI/CD pipelines
    └── monitoring/                         # Monitoring configs
```

---

## 4. Technology Stack

### 4.1. Core Technologies

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.5.7 |
| Cloud | Spring Cloud | 2025.0.0 |
| Build Tool | Maven | 3.8+ |
| Database | PostgreSQL | 15+ |
| Cache | Redis | 7+ |
| Message Broker | Apache Kafka | 3.x |
| Service Discovery | Netflix Eureka | Latest |
| API Gateway | Spring Cloud Gateway | Latest |

### 4.2. Libraries & Dependencies

```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2025.0.0</spring-cloud.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok.version>1.18.30</lombok.version>
</properties>
```

### 4.3. Supporting Tools

- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: Jenkins
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Documentation**: Swagger/OpenAPI

---

## 5. Common Libraries

### 5.1. common-core

**Mục đích**: Cung cấp các class cơ bản, utilities và DTOs được sử dụng chung trong toàn bộ dự án.

#### 5.1.1. BaseEntity

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
    
    @Version
    private Long version; // Optimistic locking
}
```

**Tính năng**:
- Auto-generate ID
- Audit fields (created/updated by/at)
- Optimistic locking với version field
- Serializable support

#### 5.1.2. ResponseDto

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    
    public static <T> ResponseDto<T> success(T data) { ... }
    public static <T> ResponseDto<T> error(String errorCode, String message) { ... }
}
```

**Cách sử dụng**:
```java
// Success response
return ResponseEntity.ok(ResponseDto.success(user));

// Error response
return ResponseEntity
    .status(HttpStatus.BAD_REQUEST)
    .body(ResponseDto.error("USER_NOT_FOUND", "User not found"));
```

#### 5.1.3. Utilities

**DateUtil**: Xử lý date/time operations
**StringUtil**: String manipulation helpers
**ValidationUtil**: Custom validation logic

### 5.2. common-exception

**Mục đích**: Xử lý exception tập trung cho toàn bộ hệ thống.

#### 5.2.1. GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleResourceNotFound(...) { ... }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Void>> handleBusinessException(...) { ... }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Map<String, String>>> handleValidationException(...) { ... }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleGenericException(...) { ... }
}
```

#### 5.2.2. Custom Exceptions

- **BusinessException**: Lỗi logic nghiệp vụ
- **ResourceNotFoundException**: Resource không tồn tại
- **UnauthorizedException**: Lỗi xác thực
- **ValidationException**: Lỗi validation

### 5.3. common-security

**Mục đích**: Cấu hình security chung (JWT, OAuth2, CORS).

**Tính năng**:
- JWT Token generation & validation
- OAuth2 integration
- Role-based access control (RBAC)
- CORS configuration

### 5.4. common-redis

**Mục đích**: Cấu hình Redis cache.

**Tính năng**:
- Redis connection factory
- Cache configuration
- Session management
- Distributed locking

### 5.5. common-kafka

**Mục đích**: Cấu hình Kafka producer/consumer.

**Tính năng**:
- Kafka template configuration
- Event publisher
- Event listener
- Error handling

### 5.6. common-logging

**Mục đích**: Cấu hình logging tập trung.

**Tính năng**:
- Log formatting
- Log aggregation
- Trace ID correlation
- Log level management

---

## 6. Microservices

### 6.1. Auth Service (Port: 8081)

**Chức năng chính**:
- User registration & authentication
- JWT token management
- Role & permission management
- Password reset & recovery
- OAuth2 integration (Google, Facebook)

**Database**: `auth_db`

**Endpoints**:
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh-token
POST   /api/auth/logout
POST   /api/auth/forgot-password
GET    /api/users/{id}
PUT    /api/users/{id}
```

**Tech Stack**:
- Spring Security
- JWT (jjwt library)
- BCrypt password encoding
- Redis (token blacklist)

### 6.2. Exam Service (Port: 8082)

**Chức năng chính**:
- Quản lý đề thi và câu hỏi
- Tạo và cấu hình kỳ thi
- Quản lý thời gian thi
- Anti-cheating features
- Question bank management

**Database**: `exam_db`

**Endpoints**:
```
POST   /api/exams
GET    /api/exams/{id}
PUT    /api/exams/{id}
DELETE /api/exams/{id}
GET    /api/exams/{id}/questions
POST   /api/exams/{id}/start
POST   /api/exams/{id}/submit
```

**Tech Stack**:
- Spring Data JPA
- PostgreSQL
- Redis (caching questions)
- Kafka (exam events)

### 6.3. Result Service (Port: 8083)

**Chức năng chính**:
- Chấm điểm tự động
- Lưu trữ kết quả thi
- Xuất certificate
- Thống kê điểm số
- Review & appeal

**Database**: `result_db`

**Endpoints**:
```
POST   /api/results
GET    /api/results/{examId}/{userId}
GET    /api/results/user/{userId}
GET    /api/results/exam/{examId}
POST   /api/results/{id}/certificate
```

**Tech Stack**:
- Spring Data JPA
- PostgreSQL
- Kafka (result processing)
- PDF generation (iText/Jasper)

### 6.4. Blog Service (Port: 8084)

**Chức năng chính**:
- Quản lý bài viết blog
- Categories & tags
- Comments & likes
- Search functionality
- Content moderation

**Database**: `blog_db`

**Endpoints**:
```
POST   /api/posts
GET    /api/posts
GET    /api/posts/{id}
PUT    /api/posts/{id}
DELETE /api/posts/{id}
POST   /api/posts/{id}/comments
```

**Tech Stack**:
- Spring Data JPA
- PostgreSQL
- Elasticsearch (search)
- Redis (caching)

### 6.5. Analytics Service (Port: 8085)

**Chức năng chính**:
- Phân tích dữ liệu thi
- Dashboard & reports
- User behavior tracking
- Performance metrics
- Export reports

**Database**: `analytics_db`

**Endpoints**:
```
GET    /api/analytics/dashboard
GET    /api/analytics/exams/{id}/statistics
GET    /api/analytics/users/{id}/performance
POST   /api/analytics/reports/generate
```

**Tech Stack**:
- Spring Data JPA
- PostgreSQL
- Apache Kafka (event streaming)
- Elasticsearch (aggregations)

---

## 7. Infrastructure Components

### 7.1. API Gateway (Port: 8080)

**Chức năng**:
- Request routing
- Load balancing
- Rate limiting
- Authentication filter
- CORS handling
- Request/Response logging

**Configuration Example**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/auth/**
          filters:
            - RewritePath=/api/auth/(?<segment>.*), /$\{segment}
```

### 7.2. Eureka Server (Port: 8761)

**Chức năng**:
- Service registration
- Service discovery
- Health check
- Load balancing

**Configuration Example**:
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

### 7.3. Config Server (Port: 8888)

**Chức năng**:
- Centralized configuration
- Environment-specific configs
- Dynamic configuration refresh
- Encryption support

**Configuration Structure**:
```
config-repo/
  ├── application.yml           # Common configs
  ├── auth-service.yml         # Auth service specific
  ├── auth-service-dev.yml     # Dev environment
  ├── auth-service-prod.yml    # Production environment
  └── ...
```

---

## 8. Deployment Strategy

### 8.1. Containerization với Docker

#### 8.1.1. Dockerfile Template

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 8.1.2. Docker Compose

```yaml
version: '3.8'
services:
  eureka-server:
    build: ./certimaster-infrastructure/eureka-server
    ports:
      - "8761:8761"
    networks:
      - certimaster-network
  
  config-server:
    build: ./certimaster-infrastructure/config-server
    ports:
      - "8888:8888"
    depends_on:
      - eureka-server
    networks:
      - certimaster-network
  
  api-gateway:
    build: ./certimaster-infrastructure/api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
      - config-server
    networks:
      - certimaster-network
  
  auth-service:
    build: ./certimaster-services/auth-service
    depends_on:
      - postgres-auth
      - eureka-server
      - config-server
    networks:
      - certimaster-network
  
  postgres-auth:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: auth_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres-auth-data:/var/lib/postgresql/data
    networks:
      - certimaster-network
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - certimaster-network
  
  kafka:
    image: confluentinc/cp-kafka:7.4.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    networks:
      - certimaster-network
  
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    networks:
      - certimaster-network

networks:
  certimaster-network:
    driver: bridge

volumes:
  postgres-auth-data:
```

### 8.2. Kubernetes Deployment

#### 8.2.1. Service Deployment Template

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: certimaster/auth-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://eureka-server:8761/eureka/"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth-service
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

### 8.3. CI/CD Pipeline với Jenkins

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/your-repo/certimaster.git'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Docker Build') {
            steps {
                sh 'docker build -t certimaster/auth-service:${BUILD_NUMBER} ./certimaster-services/auth-service'
            }
        }
        
        stage('Docker Push') {
            steps {
                sh 'docker push certimaster/auth-service:${BUILD_NUMBER}'
            }
        }
        
        stage('Deploy to K8s') {
            steps {
                sh 'kubectl apply -f k8s/auth-service-deployment.yaml'
                sh 'kubectl set image deployment/auth-service auth-service=certimaster/auth-service:${BUILD_NUMBER}'
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
```

---

## 9. Development Guidelines

### 9.1. Quy Trình Phát Triển Feature Mới

#### Bước 1: Phân Tích Requirements
- Xác định service cần implement
- Xác định dependencies với services khác
- Thiết kế database schema
- Xác định API contracts

#### Bước 2: Thiết Kế
- Vẽ sequence diagram
- Thiết kế data models
- Định nghĩa DTOs
- Xác định exception handling

#### Bước 3: Implementation

**3.1. Tạo Entity**
```java
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Enumerated(EnumType.STRING)
    private Status status;
}
```

**3.2. Tạo Repository**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(Status status);
    boolean existsByEmail(String email);
}
```

**3.3. Tạo DTO**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends BaseDto {
    
    private Long id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private UserRole role;
    private Status status;
}
```

**3.4. Tạo Mapper**
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
    List<UserDto> toDtoList(List<User> users);
}
```

**3.5. Tạo Service**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists");
        }
        
        User user = userMapper.toEntity(userDto);
        user.setStatus(Status.ACTIVE);
        User savedUser = userRepository.save(user);
        
        log.info("Created user with id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setFullName(userDto.getFullName());
        user.setRole(userDto.getRole());
        
        User updatedUser = userRepository.save(user);
        log.info("Updated user with id: {}", id);
        
        return userMapper.toDto(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }
}
```

**3.6. Tạo Controller**
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ResponseDto.success(user));
    }
    
    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<ResponseDto<UserDto>> createUser(
            @Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDto.success("User created successfully", createdUser));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ResponseDto<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(
            ResponseDto.success("User updated successfully", updatedUser));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ResponseDto<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
            ResponseDto.success("User deleted successfully", null));
    }
}
```

#### Bước 4: Testing

**4.1. Unit Tests**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void getUserById_Success() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserDto userDto = new UserDto();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        
        // When
        UserDto result = userService.getUserById(userId);
        
        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }
    
    @Test
    void getUserById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> userService.getUserById(userId));
    }
}
```

**4.2. Integration Tests**
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createUser_Success() throws Exception {
        UserDto userDto = UserDto.builder()
            .email("test@example.com")
            .fullName("Test User")
            .build();
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
}
```

### 9.2. Naming Conventions

#### 9.2.1. Package Structure
```
com.certimaster.{service-name}
  ├── controller     # REST Controllers
  ├── service        # Business Logic
  ├── repository     # Data Access
  ├── entity         # JPA Entities
  ├── dto            # Data Transfer Objects
  ├── mapper         # Entity-DTO Mappers
  ├── config         # Configuration Classes
  ├── exception      # Custom Exceptions
  └── util           # Utility Classes
```

#### 9.2.2. Naming Rules

**Classes**:
- Controllers: `{Resource}Controller` (e.g., `UserController`)
- Services: `{Resource}Service` (e.g., `UserService`)
- Repositories: `{Entity}Repository` (e.g., `UserRepository`)
- DTOs: `{Resource}Dto` (e.g., `UserDto`)
- Entities: `{Resource}` (e.g., `User`)

**Methods**:
- GET: `get{Resource}`, `find{Resource}`, `list{Resources}`
- POST: `create{Resource}`, `add{Resource}`
- PUT: `update{Resource}`, `modify{Resource}`
- DELETE: `delete{Resource}`, `remove{Resource}`

**Variables**:
- camelCase: `userName`, `userId`
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)

### 9.3. Configuration Management

#### 9.3.1. application.yml Structure

```yaml
spring:
  application:
    name: auth-service
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:auth_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        show_sql: false
    open-in-view: false
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
  
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_SERVER:http://localhost:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30

server:
  port: ${PORT:8081}

logging:
  level:
    com.certimaster: ${LOG_LEVEL:INFO}
    org.springframework.web: ${LOG_LEVEL:INFO}
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

---

## 10. Best Practices

### 10.1. Code Quality

#### 10.1.1. SOLID Principles
- **S**ingle Responsibility: Mỗi class chỉ có một nhiệm vụ duy nhất
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subclass có thể thay thế parent class
- **I**nterface Segregation: Chia nhỏ interface, không force implement unnecessary methods
- **D**ependency Inversion: Depend on abstractions, not concretions

#### 10.1.2. Clean Code
```java
// ❌ Bad
public List<User> getU() {
    return userRepository.findAll();
}

// ✅ Good
public List<User> getAllActiveUsers() {
    return userRepository.findByStatus(Status.ACTIVE);
}
```

### 10.2. Security Best Practices

#### 10.2.1. Input Validation
```java
@PostMapping("/users")
public ResponseEntity<ResponseDto<UserDto>> createUser(
        @Valid @RequestBody UserDto userDto) {
    // Validation is automatically handled by @Valid
    UserDto createdUser = userService.createUser(userDto);
    return ResponseEntity.ok(ResponseDto.success(createdUser));
}
```

#### 10.2.2. Password Handling
```java
@Service
public class AuthService {
    
    private final PasswordEncoder passwordEncoder;
    
    public void registerUser(UserDto userDto) {
        // ✅ Always hash passwords
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(hashedPassword);
        
        // ❌ Never store plain text passwords
        // user.setPassword(userDto.getPassword());
    }
}
```

#### 10.2.3. API Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

### 10.3. Performance Optimization

#### 10.3.1. Database Optimization
```java
// ✅ Use pagination for large datasets
@GetMapping("/users")
public ResponseEntity<ResponseDto<Page<UserDto>>> getUsers(
        @PageableDefault(size = 20) Pageable pageable) {
    Page<UserDto> users = userService.getUsers(pageable);
    return ResponseEntity.ok(ResponseDto.success(users));
}

// ✅ Use @Query for complex queries
@Query("SELECT u FROM User u WHERE u.status = :status AND u.role = :role")
List<User> findByStatusAndRole(
    @Param("status") Status status, 
    @Param("role") UserRole role);

// ✅ Fetch associations efficiently
@EntityGraph(attributePaths = {"roles", "permissions"})
Optional<User> findByEmail(String email);
```

#### 10.3.2. Caching Strategy
```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        // This will be cached
        return userMapper.toDto(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @CachePut(value = "users", key = "#result.id")
    public UserDto updateUser(Long id, UserDto userDto) {
        // Update logic
        return updatedUser;
    }
}
```

#### 10.3.3. Async Processing
```java
@Service
public class NotificationService {
    
    @Async
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body) {
        // Send email asynchronously
        emailClient.send(to, subject, body);
        return CompletableFuture.completedFuture(null);
    }
}
```

### 10.4. Error Handling

#### 10.4.1. Consistent Error Response
```java
{
    "success": false,
    "errorCode": "RESOURCE_NOT_FOUND",
    "message": "User not found with id: 123",
    "timestamp": "2025-11-14T10:30:00"
}
```

#### 10.4.2. Logging
```java
@Service
@Slf4j
public class UserService {
    
    public UserDto createUser(UserDto userDto) {
        try {
            log.info("Creating user with email: {}", userDto.getEmail());
            User user = userMapper.toEntity(userDto);
            User savedUser = userRepository.save(user);
            log.info("Successfully created user with id: {}", savedUser.getId());
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new BusinessException("USER_CREATION_FAILED", 
                "Failed to create user", e);
        }
    }
}
```

### 10.5. Testing Strategy

#### 10.5.1. Test Coverage Goals
- Unit Tests: > 80%
- Integration Tests: Key workflows
- E2E Tests: Critical user journeys

#### 10.5.2. Test Naming Convention
```java
// Pattern: methodName_scenario_expectedResult
@Test
void createUser_WithValidData_ReturnsCreatedUser() { ... }

@Test
void createUser_WithExistingEmail_ThrowsBusinessException() { ... }

@Test
void getUserById_WithInvalidId_ThrowsResourceNotFoundException() { ... }
```

### 10.6. Documentation

#### 10.6.1. API Documentation với Swagger
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user information by user ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    public ResponseEntity<ResponseDto<UserDto>> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        // Implementation
    }
}
```

#### 10.6.2. Code Comments
```java
/**
 * Service class for managing user operations.
 * 
 * This service handles all business logic related to user management,
 * including CRUD operations, authentication, and authorization.
 * 
 * @author CertiMaster Team
 * @version 1.0
 * @since 2025-11-14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    /**
     * Retrieves a user by their unique identifier.
     * 
     * @param id the unique identifier of the user
     * @return UserDto containing user information
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDto getUserById(Long id) {
        // Implementation
    }
}
```

---

## 📚 Additional Resources

### Learning Materials
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Microservices Patterns](https://microservices.io/patterns/)

### Tools & Libraries
- [Lombok](https://projectlombok.org/)
- [MapStruct](https://mapstruct.org/)
- [Swagger/OpenAPI](https://swagger.io/)

### Community
- Slack: `#certimaster-dev`
- Email: `dev@certimaster.com`

---

## 📝 Change Log

### Version 1.0.0 (2025-11-14)
- ✅ Initial project structure
- ✅ Common libraries setup
- ✅ Microservices skeleton
- ✅ Infrastructure components
- ✅ Documentation

---

## 👥 Contributors

- **Tech Lead**: [Your Name]
- **Backend Team**: [Team Members]
- **DevOps Team**: [Team Members]

---

## 📄 License

Copyright © 2025 CertiMaster. All rights reserved.

---

**Happy Coding! 🚀**


