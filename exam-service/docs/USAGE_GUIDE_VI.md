# Hướng Dẫn Sử Dụng Entity - Exam Service

## Mục Lục

1. [Giới Thiệu](#giới-thiệu)
2. [Ví Dụ Code Java Cho CRUD Operations](#ví-dụ-code-java-cho-crud-operations)
3. [Hướng Dẫn Về JPA Queries](#hướng-dẫn-về-jpa-queries)
4. [Design Patterns và Best Practices](#design-patterns-và-best-practices)
5. [Troubleshooting](#troubleshooting)
6. [FAQ](#faq)

---

## Giới Thiệu

Tài liệu này cung cấp hướng dẫn thực tế về cách sử dụng các entity trong exam-service. Bao gồm:
- Ví dụ code Java cho các operations phổ biến
- Best practices khi làm việc với JPA/Hibernate
- Design patterns được áp dụng
- Giải pháp cho các vấn đề thường gặp

### Yêu Cầu

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL 14+
- Lombok

---

## Ví Dụ Code Java Cho CRUD Operations

### 1. Tạo Certification Với Topics

#### Ví Dụ Cơ Bản

```java
@Service
@RequiredArgsConstructor
public class CertificationService {
    
    private final CertificationRepository certificationRepository;
    
    @Transactional
    public Certification createCertificationWithTopics(CreateCertificationRequest request) {
        // Tạo certification
        Certification certification = Certification.builder()
            .name(request.getName())
            .code(request.getCode())
            .provider(request.getProvider())
            .description(request.getDescription())
            .level(request.getLevel())
            .durationMinutes(request.getDurationMinutes())
            .passingScore(request.getPassingScore())
            .totalQuestions(request.getTotalQuestions())
            .price(request.getPrice())
            .status("ACTIVE")
            .build();
        
        // Tạo topics và add vào certification
        for (CreateTopicRequest topicRequest : request.getTopics()) {
            Topic topic = Topic.builder()
                .name(topicRequest.getName())
                .code(topicRequest.getCode())
                .description(topicRequest.getDescription())
                .weightPercentage(topicRequest.getWeightPercentage())
                .orderIndex(topicRequest.getOrderIndex())
                .certification(certification)  // Set parent reference
                .build();
            
            certification.getTopics().add(topic);  // Add to collection
        }
        
        // Save certification - topics tự động được save (cascade PERSIST)
        return certificationRepository.save(certification);
    }
}
```

#### DTO Classes

```java
@Data
public class CreateCertificationRequest {
    private String name;
    private String code;
    private String provider;
    private String description;
    private String level;
    private Integer durationMinutes;
    private BigDecimal passingScore;
    private Integer totalQuestions;
    private BigDecimal price;
    private List<CreateTopicRequest> topics;
}

@Data
public class CreateTopicRequest {
    private String name;
    private String code;
    private String description;
    private BigDecimal weightPercentage;
    private Integer orderIndex;
}
```

#### Ví Dụ Sử Dụng

```java
@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {
    
    private final CertificationService certificationService;
    
    @PostMapping
    public ResponseEntity<Certification> createCertification(
            @RequestBody CreateCertificationRequest request) {
        
        Certification certification = certificationService
            .createCertificationWithTopics(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(certification);
    }
}
```

#### Request Example (JSON)

```json
{
  "name": "AWS Solutions Architect Associate",
  "code": "SAA-C03",
  "provider": "Amazon Web Services",
  "description": "This certification validates expertise in designing distributed systems on AWS",
  "level": "INTERMEDIATE",
  "durationMinutes": 130,
  "passingScore": 72.0,
  "totalQuestions": 65,
  "price": 150.00,
  "topics": [
    {
      "name": "Design Resilient Architectures",
      "code": "DOMAIN-1",
      "description": "Design scalable and loosely coupled architectures",
      "weightPercentage": 30.0,
      "orderIndex": 1
    },
    {
      "name": "Design High-Performing Architectures",
      "code": "DOMAIN-2",
      "description": "Identify elastic and scalable compute solutions",
      "weightPercentage": 28.0,
      "orderIndex": 2
    },
    {
      "name": "Design Secure Applications",
      "code": "DOMAIN-3",
      "description": "Design secure access to AWS resources",
      "weightPercentage": 24.0,
      "orderIndex": 3
    },
    {
      "name": "Design Cost-Optimized Architectures",
      "code": "DOMAIN-4",
      "description": "Design cost-optimized storage and compute solutions",
      "weightPercentage": 18.0,
      "orderIndex": 4
    }
  ]
}
```

---

### 2. Tạo Question Với QuestionOptions

#### Service Implementation

```java
@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    
    @Transactional
    public Question createQuestionWithOptions(CreateQuestionRequest request) {
        // Validate topic exists
        Topic topic = topicRepository.findById(request.getTopicId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Topic not found with id: " + request.getTopicId()));
        
        // Create question
        Question question = Question.builder()
            .topic(topic)
            .type(request.getType())
            .content(request.getContent())
            .explanation(request.getExplanation())
            .difficulty(request.getDifficulty())
            .points(request.getPoints())
            .timeLimitSeconds(request.getTimeLimitSeconds())
            .referenceUrl(request.getReferenceUrl())
            .build();
        
        // Create options and add to question
        for (CreateQuestionOptionRequest optionRequest : request.getOptions()) {
            QuestionOption option = QuestionOption.builder()
                .content(optionRequest.getContent())
                .isCorrect(optionRequest.getIsCorrect())
                .orderIndex(optionRequest.getOrderIndex())
                .question(question)  // Set parent reference
                .build();
            
            question.getQuestionOptions().add(option);
        }
        
        // Validate question before saving
        validateQuestion(question);
        
        // Save question - options tự động được save (cascade PERSIST)
        return questionRepository.save(question);
    }
    
    private void validateQuestion(Question question) {
        // Validate minimum options
        if (question.getQuestionOptions().size() < 2) {
            throw new ValidationException("Question must have at least 2 options");
        }
        
        // Count correct answers
        long correctCount = question.getQuestionOptions().stream()
            .filter(QuestionOption::getIsCorrect)
            .count();
        
        // Validate based on question type
        switch (question.getType()) {
            case "SINGLE_CHOICE":
            case "TRUE_FALSE":
                if (correctCount != 1) {
                    throw new ValidationException(
                        question.getType() + " must have exactly 1 correct answer");
                }
                break;
            case "MULTIPLE_CHOICE":
                if (correctCount < 2) {
                    throw new ValidationException(
                        "MULTIPLE_CHOICE must have at least 2 correct answers");
                }
                break;
            default:
                throw new ValidationException("Invalid question type: " + question.getType());
        }
    }
}
```

#### DTO Classes

```java
@Data
public class CreateQuestionRequest {
    private Long topicId;
    private String type;  // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE
    private String content;
    private String explanation;
    private String difficulty;  // EASY, MEDIUM, HARD
    private Integer points;
    private Integer timeLimitSeconds;
    private String referenceUrl;
    private List<CreateQuestionOptionRequest> options;
}

@Data
public class CreateQuestionOptionRequest {
    private String content;
    private Boolean isCorrect;
    private Integer orderIndex;
}
```

#### Request Example (JSON)

```json
{
  "topicId": 1,
  "type": "SINGLE_CHOICE",
  "content": "Which AWS service provides a managed NoSQL database with single-digit millisecond performance?",
  "explanation": "Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.",
  "difficulty": "MEDIUM",
  "points": 1,
  "timeLimitSeconds": 120,
  "referenceUrl": "https://docs.aws.amazon.com/dynamodb/",
  "options": [
    {
      "content": "Amazon RDS",
      "isCorrect": false,
      "orderIndex": 1
    },
    {
      "content": "Amazon DynamoDB",
      "isCorrect": true,
      "orderIndex": 2
    },
    {
      "content": "Amazon Redshift",
      "isCorrect": false,
      "orderIndex": 3
    },
    {
      "content": "Amazon Aurora",
      "isCorrect": false,
      "orderIndex": 4
    }
  ]
}
```

---

### 3. Tạo Exam Với Questions Thông Qua ExamQuestion

#### Service Implementation

```java
@Service
@RequiredArgsConstructor
public class ExamService {
    
    private final ExamRepository examRepository;
    private final CertificationRepository certificationRepository;
    private final QuestionRepository questionRepository;
    
    @Transactional
    public Exam createExamWithQuestions(CreateExamRequest request) {
        // Validate certification exists
        Certification certification = certificationRepository
            .findById(request.getCertificationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Certification not found with id: " + request.getCertificationId()));
        
        // Create exam
        Exam exam = Exam.builder()
            .certification(certification)
            .title(request.getTitle())
            .type(request.getType())
            .description(request.getDescription())
            .durationMinutes(request.getDurationMinutes())
            .totalQuestions(request.getTotalQuestions())
            .passingScore(request.getPassingScore())
            .status("ACTIVE")
            .build();
        
        // Get questions based on strategy
        List<Question> selectedQuestions;
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            // Use specific questions
            selectedQuestions = questionRepository.findAllById(request.getQuestionIds());
        } else {
            // Auto-select questions based on topic weights
            selectedQuestions = selectQuestionsByTopicWeight(
                certification, request.getTotalQuestions());
        }
        
        // Validate question count
        if (selectedQuestions.size() < request.getTotalQuestions()) {
            throw new ValidationException(
                "Not enough questions available. Required: " + request.getTotalQuestions() +
                ", Available: " + selectedQuestions.size());
        }
        
        // Shuffle questions if requested
        if (request.getShuffleQuestions()) {
            Collections.shuffle(selectedQuestions);
        }
        
        // Create ExamQuestions with orderIndex
        for (int i = 0; i < request.getTotalQuestions(); i++) {
            ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(selectedQuestions.get(i))
                .orderIndex(i + 1)
                .build();
            
            exam.getExamQuestions().add(examQuestion);
        }
        
        // Save exam - examQuestions tự động được save (cascade PERSIST)
        return examRepository.save(exam);
    }
    
    private List<Question> selectQuestionsByTopicWeight(
            Certification certification, Integer totalQuestions) {
        
        List<Question> selectedQuestions = new ArrayList<>();
        
        // Get all topics with their weights
        List<Topic> topics = new ArrayList<>(certification.getTopics());
        topics.sort(Comparator.comparing(Topic::getOrderIndex));
        
        // Calculate questions per topic based on weight
        for (Topic topic : topics) {
            int questionsForTopic = (int) Math.round(
                totalQuestions * topic.getWeightPercentage().doubleValue() / 100.0);
            
            // Get random questions from this topic
            List<Question> topicQuestions = questionRepository
                .findRandomQuestionsByTopic(topic.getId(), questionsForTopic);
            
            selectedQuestions.addAll(topicQuestions);
        }
        
        return selectedQuestions;
    }
}
```

#### DTO Classes

```java
@Data
public class CreateExamRequest {
    private Long certificationId;
    private String title;
    private String type;  // PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer passingScore;
    private Boolean shuffleQuestions = true;
    private List<Long> questionIds;  // Optional: specific questions
}
```

#### Request Example (JSON)

```json
{
  "certificationId": 1,
  "title": "AWS SAA Practice Test 1",
  "type": "PRACTICE",
  "description": "Practice test covering all domains",
  "durationMinutes": 130,
  "totalQuestions": 65,
  "passingScore": 72,
  "shuffleQuestions": true
}
```

---

### 4. Thêm Tags Cho Questions

#### Service Implementation

```java
@Service
@RequiredArgsConstructor
public class TagService {
    
    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    
    @Transactional
    public Question addTagsToQuestion(Long questionId, List<String> tagNames) {
        // Validate question exists
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Question not found with id: " + questionId));
        
        for (String tagName : tagNames) {
            // Normalize tag name (lowercase, trim)
            String normalizedName = tagName.toLowerCase().trim();
            
            // Find or create tag
            Tag tag = tagRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                        .name(normalizedName)
                        .build();
                    return tagRepository.save(newTag);
                });
            
            // Check if already tagged
            boolean alreadyTagged = questionTagRepository
                .existsByQuestionIdAndTagId(questionId, tag.getId());
            
            if (!alreadyTagged) {
                QuestionTag questionTag = QuestionTag.builder()
                    .question(question)
                    .tag(tag)
                    .build();
                questionTagRepository.save(questionTag);
            }
        }
        
        // Reload question with tags
        return questionRepository.findByIdWithTags(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }
    
    @Transactional
    public Question removeTagsFromQuestion(Long questionId, List<String> tagNames) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Question not found with id: " + questionId));
        
        for (String tagName : tagNames) {
            String normalizedName = tagName.toLowerCase().trim();
            
            Tag tag = tagRepository.findByName(normalizedName)
                .orElse(null);
            
            if (tag != null) {
                questionTagRepository.deleteByQuestionIdAndTagId(questionId, tag.getId());
            }
        }
        
        return questionRepository.findByIdWithTags(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }
    
    public List<Question> findQuestionsByTags(List<String> tagNames, boolean matchAll) {
        if (matchAll) {
            // Must have ALL tags (AND logic)
            return questionRepository.findByAllTags(tagNames, (long) tagNames.size());
        } else {
            // Must have ANY tag (OR logic)
            return questionRepository.findByAnyTag(tagNames);
        }
    }
}
```

#### Repository Methods

```java
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.questionTags qt " +
           "LEFT JOIN FETCH qt.tag " +
           "WHERE q.id = :questionId")
    Optional<Question> findByIdWithTags(@Param("questionId") Long questionId);
    
    @Query("SELECT DISTINCT q FROM Question q " +
           "JOIN q.questionTags qt " +
           "JOIN qt.tag t " +
           "WHERE t.name IN :tagNames " +
           "GROUP BY q.id " +
           "HAVING COUNT(DISTINCT t.id) = :tagCount")
    List<Question> findByAllTags(@Param("tagNames") List<String> tagNames,
                                  @Param("tagCount") Long tagCount);
    
    @Query("SELECT DISTINCT q FROM Question q " +
           "JOIN q.questionTags qt " +
           "JOIN qt.tag t " +
           "WHERE t.name IN :tagNames")
    List<Question> findByAnyTag(@Param("tagNames") List<String> tagNames);
}

public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
    
    boolean existsByQuestionIdAndTagId(Long questionId, Long tagId);
    
    void deleteByQuestionIdAndTagId(Long questionId, Long tagId);
}
```

#### Usage Example

```java
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    
    private final TagService tagService;
    
    @PostMapping("/{questionId}/tags")
    public ResponseEntity<Question> addTags(
            @PathVariable Long questionId,
            @RequestBody AddTagsRequest request) {
        
        Question question = tagService.addTagsToQuestion(
            questionId, request.getTagNames());
        
        return ResponseEntity.ok(question);
    }
    
    @DeleteMapping("/{questionId}/tags")
    public ResponseEntity<Question> removeTags(
            @PathVariable Long questionId,
            @RequestBody RemoveTagsRequest request) {
        
        Question question = tagService.removeTagsFromQuestion(
            questionId, request.getTagNames());
        
        return ResponseEntity.ok(question);
    }
    
    @GetMapping("/search/by-tags")
    public ResponseEntity<List<Question>> searchByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "false") boolean matchAll) {
        
        List<Question> questions = tagService.findQuestionsByTags(tags, matchAll);
        
        return ResponseEntity.ok(questions);
    }
}

@Data
class AddTagsRequest {
    private List<String> tagNames;
}

@Data
class RemoveTagsRequest {
    private List<String> tagNames;
}
```

#### Request Examples

**Add Tags:**
```json
POST /api/questions/123/tags
{
  "tagNames": ["security", "encryption", "s3", "best-practices"]
}
```

**Search by Tags (ANY):**
```
GET /api/questions/search/by-tags?tags=security,networking&matchAll=false
```

**Search by Tags (ALL):**
```
GET /api/questions/search/by-tags?tags=security,networking&matchAll=true
```

---

## Hướng Dẫn Về JPA Queries

### 1. Basic Queries Với JpaRepository

#### Built-in Methods

```java
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    
    // Find by single field
    Optional<Certification> findByCode(String code);
    
    // Find by multiple fields
    List<Certification> findByProviderAndLevel(String provider, String level);
    
    // Find with LIKE
    List<Certification> findByNameContainingIgnoreCase(String keyword);
    
    // Find with comparison
    List<Certification> findByPriceGreaterThan(BigDecimal price);
    List<Certification> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find with IN clause
    List<Certification> findByLevelIn(List<String> levels);
    
    // Find with sorting
    List<Certification> findByStatusOrderByCreatedAtDesc(String status);
    
    // Count queries
    long countByProvider(String provider);
    boolean existsByCode(String code);
    
    // Delete queries
    void deleteByCode(String code);
}
```

#### Usage Examples

```java
@Service
@RequiredArgsConstructor
public class CertificationQueryService {
    
    private final CertificationRepository certificationRepository;
    
    public Certification findByCode(String code) {
        return certificationRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Certification not found with code: " + code));
    }
    
    public List<Certification> searchByName(String keyword) {
        return certificationRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    public List<Certification> findByPriceRange(BigDecimal min, BigDecimal max) {
        return certificationRepository.findByPriceBetween(min, max);
    }
    
    public List<Certification> findActiveCertifications() {
        return certificationRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
    }
    
    public boolean isCodeAvailable(String code) {
        return !certificationRepository.existsByCode(code);
    }
}
```

---

### 2. JOIN FETCH Để Tránh Lazy Loading Issues

#### Problem: N+1 Query

```java
// BAD: N+1 query problem
public List<CertificationDTO> getAllCertifications() {
    List<Certification> certifications = certificationRepository.findAll();
    // SQL 1: SELECT * FROM certifications
    
    return certifications.stream()
        .map(cert -> {
            CertificationDTO dto = new CertificationDTO();
            dto.setName(cert.getName());
            dto.setTopicCount(cert.getTopics().size());  
            // SQL 2, 3, 4, ..., N+1: SELECT * FROM topics WHERE certification_id = ?
            return dto;
        })
        .collect(Collectors.toList());
}
```

#### Solution: JOIN FETCH

```java
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    
    // Single collection JOIN FETCH
    @Query("SELECT DISTINCT c FROM Certification c LEFT JOIN FETCH c.topics")
    List<Certification> findAllWithTopics();
    
    // Multiple level JOIN FETCH
    @Query("SELECT DISTINCT c FROM Certification c " +
           "LEFT JOIN FETCH c.topics t " +
           "LEFT JOIN FETCH t.questions")
    List<Certification> findAllWithTopicsAndQuestions();
    
    // JOIN FETCH with WHERE clause
    @Query("SELECT c FROM Certification c " +
           "LEFT JOIN FETCH c.topics " +
           "WHERE c.status = :status")
    List<Certification> findByStatusWithTopics(@Param("status") String status);
    
    // JOIN FETCH for single entity
    @Query("SELECT c FROM Certification c " +
           "LEFT JOIN FETCH c.topics " +
           "LEFT JOIN FETCH c.exams " +
           "WHERE c.id = :id")
    Optional<Certification> findByIdWithTopicsAndExams(@Param("id") Long id);
}
```

#### Best Practice: Separate Queries for Multiple Collections

```java
// AVOID: Cartesian product with multiple collections
@Query("SELECT c FROM Certification c " +
       "LEFT JOIN FETCH c.topics " +
       "LEFT JOIN FETCH c.exams")  // BAD: Cartesian product!
List<Certification> findAllWithTopicsAndExams();

// GOOD: Separate queries
public List<Certification> findAllWithTopicsAndExams() {
    // Query 1: Load certifications with topics
    List<Certification> certifications = certificationRepository
        .findAllWithTopics();
    
    // Query 2: Load exams separately
    List<Long> certIds = certifications.stream()
        .map(Certification::getId)
        .collect(Collectors.toList());
    
    List<Exam> exams = examRepository.findByCertificationIdIn(certIds);
    
    // Map exams to certifications
    Map<Long, List<Exam>> examsByCertId = exams.stream()
        .collect(Collectors.groupingBy(e -> e.getCertification().getId()));
    
    certifications.forEach(cert -> {
        List<Exam> certExams = examsByCertId.getOrDefault(
            cert.getId(), Collections.emptyList());
        cert.getExams().addAll(certExams);
    });
    
    return certifications;
}
```

---

### 3. Custom Queries Với @Query Annotation

#### JPQL Queries

```java
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Simple JPQL
    @Query("SELECT q FROM Question q WHERE q.difficulty = :difficulty")
    List<Question> findByDifficulty(@Param("difficulty") String difficulty);
    
    // JPQL with JOIN
    @Query("SELECT q FROM Question q " +
           "JOIN q.topic t " +
           "WHERE t.certification.id = :certificationId " +
           "AND q.difficulty = :difficulty")
    List<Question> findByCertificationAndDifficulty(
        @Param("certificationId") Long certificationId,
        @Param("difficulty") String difficulty);
    
    // JPQL with aggregation
    @Query("SELECT t.name, COUNT(q) FROM Question q " +
           "JOIN q.topic t " +
           "WHERE t.certification.id = :certificationId " +
           "GROUP BY t.id, t.name")
    List<Object[]> countQuestionsByTopic(@Param("certificationId") Long certificationId);
    
    // JPQL with subquery
    @Query("SELECT q FROM Question q " +
           "WHERE q.topic.id = :topicId " +
           "AND q.id NOT IN (" +
           "  SELECT eq.question.id FROM ExamQuestion eq " +
           "  WHERE eq.exam.id = :examId" +
           ")")
    List<Question> findAvailableQuestionsForExam(
        @Param("topicId") Long topicId,
        @Param("examId") Long examId);
    
    // Random selection
    @Query(value = "SELECT * FROM questions q " +
           "WHERE q.topic_id = :topicId " +
           "ORDER BY RANDOM() " +
           "LIMIT :limit",
           nativeQuery = true)
    List<Question> findRandomQuestionsByTopic(
        @Param("topicId") Long topicId,
        @Param("limit") int limit);
}
```

#### Native SQL Queries

```java
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    // Native query with complex logic
    @Query(value = "SELECT e.* FROM exams e " +
           "WHERE e.certification_id = :certificationId " +
           "AND e.status = 'ACTIVE' " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM user_exam_attempts uea " +
           "  WHERE uea.exam_id = e.id " +
           "  AND uea.user_id = :userId" +
           ")",
           nativeQuery = true)
    List<Exam> findAvailableExamsForUser(
        @Param("certificationId") Long certificationId,
        @Param("userId") Long userId);
    
    // Native query with window functions
    @Query(value = "SELECT e.*, " +
           "  ROW_NUMBER() OVER (PARTITION BY e.type ORDER BY e.created_at DESC) as rn " +
           "FROM exams e " +
           "WHERE e.certification_id = :certificationId",
           nativeQuery = true)
    List<Object[]> findExamsWithRowNumber(@Param("certificationId") Long certificationId);
}
```

#### DTO Projections

```java
// Interface-based projection
public interface CertificationSummary {
    Long getId();
    String getName();
    String getCode();
    Integer getTopicCount();
    Integer getExamCount();
}

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    
    @Query("SELECT c.id as id, c.name as name, c.code as code, " +
           "  SIZE(c.topics) as topicCount, " +
           "  SIZE(c.exams) as examCount " +
           "FROM Certification c " +
           "WHERE c.status = :status")
    List<CertificationSummary> findSummariesByStatus(@Param("status") String status);
}

// Class-based projection
@Data
@AllArgsConstructor
public class QuestionStatistics {
    private String difficulty;
    private Long count;
    private Double averagePoints;
}

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    @Query("SELECT new com.certimaster.exam_service.dto.QuestionStatistics(" +
           "  q.difficulty, COUNT(q), AVG(q.points)) " +
           "FROM Question q " +
           "WHERE q.topic.certification.id = :certificationId " +
           "GROUP BY q.difficulty")
    List<QuestionStatistics> getStatisticsByCertification(
        @Param("certificationId") Long certificationId);
}
```

---

### 4. Pagination và Sorting

#### Basic Pagination

```java
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    
    // Method name query with Pageable
    Page<Certification> findByStatus(String status, Pageable pageable);
    
    // Custom query with Pageable
    @Query("SELECT c FROM Certification c WHERE c.provider = :provider")
    Page<Certification> findByProvider(@Param("provider") String provider, 
                                       Pageable pageable);
}
```

#### Service Implementation

```java
@Service
@RequiredArgsConstructor
public class CertificationService {
    
    private final CertificationRepository certificationRepository;
    
    public Page<Certification> getCertifications(
            String status,
            int page,
            int size,
            String sortBy,
            String sortDirection) {
        
        // Create sort
        Sort sort = Sort.by(
            sortDirection.equalsIgnoreCase("DESC") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            sortBy
        );
        
        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Query with pagination
        return certificationRepository.findByStatus(status, pageable);
    }
    
    public Page<Certification> searchCertifications(
            CertificationSearchCriteria criteria,
            Pageable pageable) {
        
        // Use Specification for dynamic queries
        Specification<Certification> spec = CertificationSpecification
            .buildSpecification(criteria);
        
        return certificationRepository.findAll(spec, pageable);
    }
}
```

#### Specification for Dynamic Queries

```java
public class CertificationSpecification {
    
    public static Specification<Certification> buildSpecification(
            CertificationSearchCriteria criteria) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (criteria.getName() != null) {
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + criteria.getName().toLowerCase() + "%"
                ));
            }
            
            if (criteria.getProvider() != null) {
                predicates.add(cb.equal(
                    root.get("provider"),
                    criteria.getProvider()
                ));
            }
            
            if (criteria.getLevel() != null) {
                predicates.add(cb.equal(
                    root.get("level"),
                    criteria.getLevel()
                ));
            }
            
            if (criteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("price"),
                    criteria.getMinPrice()
                ));
            }
            
            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("price"),
                    criteria.getMaxPrice()
                ));
            }
            
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(
                    root.get("status"),
                    criteria.getStatus()
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

@Data
public class CertificationSearchCriteria {
    private String name;
    private String provider;
    private String level;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String status;
}
```

#### Controller Example

```java
@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {
    
    private final CertificationService certificationService;
    
    @GetMapping
    public ResponseEntity<Page<Certification>> getCertifications(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Page<Certification> certifications = certificationService
            .getCertifications(status, page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(certifications);
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<Certification>> searchCertifications(
            @RequestBody CertificationSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        Page<Certification> certifications = certificationService
            .searchCertifications(criteria, pageable);
        
        return ResponseEntity.ok(certifications);
    }
}
```

---

## Design Patterns và Best Practices

### 1. Builder Pattern

#### Tại Sao Sử Dụng Builder Pattern?

**Problems với Constructor:**
```java
// BAD: Too many parameters, hard to read
Certification cert = new Certification(
    null,  // id
    "AWS Solutions Architect",  // name
    "SAA-C03",  // code
    "Amazon Web Services",  // provider
    "This certification...",  // description
    "INTERMEDIATE",  // level
    130,  // durationMinutes
    new BigDecimal("72.0"),  // passingScore
    65,  // totalQuestions
    new BigDecimal("150.00"),  // price
    "ACTIVE",  // status
    null,  // createdAt
    null,  // updatedAt
    null,  // createdBy
    null,  // updatedBy
    new HashSet<>(),  // topics
    new HashSet<>()   // exams
);
```

**Solution: Builder Pattern:**
```java
// GOOD: Fluent, readable, flexible
Certification cert = Certification.builder()
    .name("AWS Solutions Architect")
    .code("SAA-C03")
    .provider("Amazon Web Services")
    .description("This certification...")
    .level("INTERMEDIATE")
    .durationMinutes(130)
    .passingScore(new BigDecimal("72.0"))
    .totalQuestions(65)
    .price(new BigDecimal("150.00"))
    .status("ACTIVE")
    .build();
```

#### Benefits

1. **Readability**: Self-documenting code
2. **Flexibility**: Optional parameters
3. **Immutability**: Can create immutable objects
4. **Type Safety**: Compile-time checking
5. **Default Values**: Can set defaults with @Builder.Default

#### Implementation với Lombok

```java
@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {
    
    private String name;
    private String code;
    
    @Builder.Default
    private String status = "DRAFT";
    
    @Builder.Default
    private Set<Topic> topics = new HashSet<>();
    
    @Builder.Default
    private Set<Exam> exams = new HashSet<>();
}
```

---

### 2. Transaction Management

#### @Transactional Best Practices

**Service Layer Transactions:**
```java
@Service
@Transactional(readOnly = true)  // Default for all methods
@RequiredArgsConstructor
public class CertificationService {
    
    private final CertificationRepository certificationRepository;
    
    // Read-only method (uses class-level @Transactional)
    public Certification findById(Long id) {
        return certificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
    }
    
    // Write method (override with readOnly = false)
    @Transactional  // readOnly = false by default
    public Certification createCertification(CreateCertificationRequest request) {
        // ... create logic
        return certificationRepository.save(certification);
    }
    
    // Complex transaction with multiple operations
    @Transactional(rollbackFor = Exception.class)
    public void updateCertificationWithTopics(Long certId, UpdateRequest request) {
        Certification cert = findById(certId);
        
        // Update certification
        cert.setName(request.getName());
        cert.setDescription(request.getDescription());
        
        // Update topics
        cert.getTopics().clear();
        for (TopicRequest topicReq : request.getTopics()) {
            Topic topic = Topic.builder()
                .name(topicReq.getName())
                .certification(cert)
                .build();
            cert.getTopics().add(topic);
        }
        
        certificationRepository.save(cert);
        // If any exception occurs, entire transaction rolls back
    }
}
```

#### Transaction Propagation

```java
@Service
@RequiredArgsConstructor
public class ExamService {
    
    private final ExamRepository examRepository;
    private final QuestionService questionService;
    
    // REQUIRED (default): Join existing transaction or create new
    @Transactional(propagation = Propagation.REQUIRED)
    public Exam createExam(CreateExamRequest request) {
        Exam exam = buildExam(request);
        
        // This joins the same transaction
        questionService.validateQuestions(request.getQuestionIds());
        
        return examRepository.save(exam);
    }
    
    // REQUIRES_NEW: Always create new transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logExamCreation(Long examId) {
        // This runs in separate transaction
        // Even if parent transaction rolls back, this commits
        auditRepository.save(new AuditLog("Exam created: " + examId));
    }
    
    // SUPPORTS: Join if exists, non-transactional if not
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Exam> findExams() {
        return examRepository.findAll();
    }
}
```

#### Handling Exceptions

```java
@Service
@RequiredArgsConstructor
public class QuestionService {
    
    // Rollback for all exceptions (including checked)
    @Transactional(rollbackFor = Exception.class)
    public Question createQuestion(CreateQuestionRequest request) {
        try {
            Question question = buildQuestion(request);
            validateQuestion(question);
            return questionRepository.save(question);
        } catch (ValidationException e) {
            // Transaction will rollback
            log.error("Validation failed", e);
            throw e;
        }
    }
    
    // No rollback for specific exceptions
    @Transactional(noRollbackFor = NotFoundException.class)
    public void updateQuestion(Long id, UpdateQuestionRequest request) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Question not found"));
            // Transaction will NOT rollback for NotFoundException
        
        // Update logic...
        questionRepository.save(question);
    }
}
```

---

### 3. Lazy Loading Best Practices

#### Problem: LazyInitializationException

```java
// BAD: LazyInitializationException
@GetMapping("/{id}")
public Certification getCertification(@PathVariable Long id) {
    Certification cert = certificationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found"));
    
    // Transaction closed here (outside @Transactional method)
    
    cert.getTopics().size();  // LazyInitializationException!
    return cert;
}
```

#### Solution 1: @Transactional on Controller Method

```java
// WORKS but not recommended (transaction in controller)
@GetMapping("/{id}")
@Transactional(readOnly = true)
public Certification getCertification(@PathVariable Long id) {
    Certification cert = certificationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found"));
    
    cert.getTopics().size();  // OK: Still in transaction
    return cert;
}
```

#### Solution 2: JOIN FETCH in Repository

```java
// RECOMMENDED: Fetch in repository
@Query("SELECT c FROM Certification c " +
       "LEFT JOIN FETCH c.topics " +
       "WHERE c.id = :id")
Optional<Certification> findByIdWithTopics(@Param("id") Long id);

@GetMapping("/{id}")
public Certification getCertification(@PathVariable Long id) {
    return certificationRepository.findByIdWithTopics(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found"));
}
```

#### Solution 3: DTO Projection

```java
// BEST: Use DTO to avoid lazy loading
@Data
public class CertificationDetailDTO {
    private Long id;
    private String name;
    private String code;
    private List<TopicDTO> topics;
    
    public static CertificationDetailDTO from(Certification cert) {
        CertificationDetailDTO dto = new CertificationDetailDTO();
        dto.setId(cert.getId());
        dto.setName(cert.getName());
        dto.setCode(cert.getCode());
        dto.setTopics(cert.getTopics().stream()
            .map(TopicDTO::from)
            .collect(Collectors.toList()));
        return dto;
    }
}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    
    public CertificationDetailDTO getCertificationDetail(Long id) {
        Certification cert = certificationRepository.findByIdWithTopics(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        
        return CertificationDetailDTO.from(cert);
        // DTO created inside transaction, safe to return
    }
}
```

---

### 4. Cascade Operations Best Practices

#### When to Use CASCADE.ALL

```java
// GOOD: Parent-child relationship
@Entity
public class Question extends BaseEntity {
    
    @OneToMany(mappedBy = "question", 
               cascade = CascadeType.ALL,  // Options belong to question
               orphanRemoval = true)
    private Set<QuestionOption> questionOptions;
}

// Usage
Question question = questionRepository.findById(id).orElseThrow();
question.getQuestionOptions().clear();  // All options deleted
questionRepository.save(question);
```

#### When NOT to Use CASCADE

```java
// BAD: Don't cascade to shared entities
@Entity
public class QuestionTag extends BaseEntity {
    
    @ManyToOne  // NO cascade!
    private Question question;
    
    @ManyToOne  // NO cascade!
    private Tag tag;  // Tag is shared, don't delete it
}
```

#### Orphan Removal

```java
@Entity
public class Certification extends BaseEntity {
    
    @OneToMany(mappedBy = "certification",
               cascade = CascadeType.ALL,
               orphanRemoval = true)  // Remove orphaned topics
    private Set<Topic> topics;
}

// Usage
Certification cert = certificationRepository.findById(id).orElseThrow();
Topic topicToRemove = cert.getTopics().iterator().next();

cert.getTopics().remove(topicToRemove);  // Remove from collection
certificationRepository.save(cert);  // Topic deleted from DB (orphan removal)
```

---

### 5. Naming Conventions

#### Table Names

```java
@Entity
@Table(name = "certifications")  // Plural, snake_case
public class Certification extends BaseEntity {
    // ...
}

@Entity
@Table(name = "question_options")  // Compound names with underscore
public class QuestionOption extends BaseEntity {
    // ...
}
```

#### Column Names

```java
@Entity
public class Certification extends BaseEntity {
    
    @Column(name = "name")  // Simple lowercase
    private String name;
    
    @Column(name = "duration_minutes")  // snake_case for compound
    private Integer durationMinutes;
    
    @Column(name = "passing_score")  // snake_case
    private BigDecimal passingScore;
}
```

#### Foreign Key Names

```java
@Entity
public class Topic extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "certification_id")  // {entity}_id pattern
    private Certification certification;
}
```

#### Index Names

```sql
CREATE INDEX idx_certifications_code ON certifications(code);
CREATE INDEX idx_certifications_provider_level ON certifications(provider, level);
CREATE INDEX idx_questions_topic_id ON questions(topic_id);
CREATE INDEX idx_questions_difficulty ON questions(difficulty);
```

---

## Troubleshooting

### 1. LazyInitializationException

**Problem:**
```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

**Causes:**
- Accessing lazy-loaded collection outside transaction
- Serializing entity with lazy collections to JSON

**Solutions:**
1. Use JOIN FETCH in query
2. Add @Transactional to method
3. Use DTO instead of entity
4. Configure Jackson to ignore lazy properties

```java
// Solution 1: JOIN FETCH
@Query("SELECT c FROM Certification c LEFT JOIN FETCH c.topics WHERE c.id = :id")
Optional<Certification> findByIdWithTopics(@Param("id") Long id);

// Solution 2: @Transactional
@Transactional(readOnly = true)
public CertificationDTO getCertification(Long id) {
    Certification cert = certificationRepository.findById(id).orElseThrow();
    return CertificationDTO.from(cert);  // Convert inside transaction
}

// Solution 3: Jackson configuration
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Certification extends BaseEntity {
    
    @OneToMany(mappedBy = "certification", fetch = FetchType.LAZY)
    @JsonIgnore  // Ignore lazy collection in JSON
    private Set<Topic> topics;
}
```

---

### 2. N+1 Query Problem

**Problem:**
```java
List<Certification> certs = certificationRepository.findAll();  // 1 query
for (Certification cert : certs) {
    System.out.println(cert.getTopics().size());  // N queries
}
```

**Detection:**
```properties
# application.yml - Enable query logging
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

**Solutions:**
```java
// Solution 1: JOIN FETCH
@Query("SELECT DISTINCT c FROM Certification c LEFT JOIN FETCH c.topics")
List<Certification> findAllWithTopics();

// Solution 2: @EntityGraph
@EntityGraph(attributePaths = {"topics"})
List<Certification> findAll();

// Solution 3: Batch fetching
@Entity
@BatchSize(size = 10)
public class Topic extends BaseEntity {
    // Hibernate will batch load topics in groups of 10
}
```

---

### 3. Detached Entity Exception

**Problem:**
```
org.hibernate.PersistentObjectException: detached entity passed to persist
```

**Cause:**
Trying to persist an entity that was loaded in a different session

**Solution:**
```java
// BAD
@Transactional
public void updateCertification(Certification cert) {
    certificationRepository.save(cert);  // May fail if cert is detached
}

// GOOD
@Transactional
public void updateCertification(Long certId, UpdateRequest request) {
    Certification cert = certificationRepository.findById(certId).orElseThrow();
    // cert is managed in this transaction
    cert.setName(request.getName());
    // No need to call save(), changes auto-persisted
}

// ALTERNATIVE: Use merge
@Transactional
public Certification updateCertification(Certification cert) {
    return certificationRepository.save(cert);  // save() calls merge() for detached
}
```

---

### 4. Unique Constraint Violation

**Problem:**
```
org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint
```

**Solution:**
```java
@Service
@RequiredArgsConstructor
public class CertificationService {
    
    @Transactional
    public Certification createCertification(CreateCertificationRequest request) {
        // Check if code already exists
        if (certificationRepository.existsByCode(request.getCode())) {
            throw new ValidationException(
                "Certification with code " + request.getCode() + " already exists");
        }
        
        Certification cert = Certification.builder()
            .code(request.getCode())
            // ... other fields
            .build();
        
        return certificationRepository.save(cert);
    }
}
```

---

## FAQ

### Q1: Khi nào nên sử dụng EAGER loading?

**A:** Hầu như không bao giờ. Luôn dùng LAZY và fetch explicitly khi cần:
```java
// Default: LAZY
@OneToMany(fetch = FetchType.LAZY)
private Set<Topic> topics;

// Fetch when needed
@Query("SELECT c FROM Certification c LEFT JOIN FETCH c.topics WHERE c.id = :id")
Optional<Certification> findByIdWithTopics(@Param("id") Long id);
```

### Q2: Có nên return Entity trực tiếp từ Controller không?

**A:** Không nên. Sử dụng DTO:
```java
// BAD
@GetMapping("/{id}")
public Certification getCertification(@PathVariable Long id) {
    return certificationRepository.findById(id).orElseThrow();
}

// GOOD
@GetMapping("/{id}")
public CertificationDTO getCertification(@PathVariable Long id) {
    Certification cert = certificationRepository.findById(id).orElseThrow();
    return CertificationDTO.from(cert);
}
```

### Q3: Khi nào cần gọi save() explicitly?

**A:** 
- Khi tạo entity mới: Luôn cần save()
- Khi update entity trong transaction: Không cần save() (dirty checking)
- Khi entity detached: Cần save() để merge

```java
// Create: Need save()
@Transactional
public Certification create(CreateRequest request) {
    Certification cert = Certification.builder().build();
    return certificationRepository.save(cert);  // REQUIRED
}

// Update: No save() needed
@Transactional
public void update(Long id, UpdateRequest request) {
    Certification cert = certificationRepository.findById(id).orElseThrow();
    cert.setName(request.getName());  // Auto-persisted (dirty checking)
}
```

### Q4: Làm sao để debug JPA queries?

**A:** Enable logging:
```properties
# application.yml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Q5: Cascade DELETE có an toàn không?

**A:** Phụ thuộc vào relationship:
- Parent-child (Certification → Topic): An toàn
- Shared entities (Question ← ExamQuestion → Exam): KHÔNG an toàn

```java
// SAFE: Parent-child
@OneToMany(cascade = CascadeType.ALL)
private Set<Topic> topics;

// UNSAFE: Shared entity
@ManyToOne  // NO cascade!
private Question question;
```

---

**Tài liệu này cung cấp hướng dẫn thực tế để làm việc với exam-service entities. Để biết thêm chi tiết về thiết kế, vui lòng tham khảo ENTITY_DOCUMENTATION_VI.md.**
