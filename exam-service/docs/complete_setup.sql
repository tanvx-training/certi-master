-- =====================================================
-- Complete Database Setup Script for Exam Service
-- =====================================================
-- This script combines schema creation, indexes, and seed data
-- Run this script to set up a complete exam-service database
--
-- Usage: psql -U username -d database_name -f complete_setup.sql
-- =====================================================

-- =====================================================
-- PART 1: DROP EXISTING TABLES
-- =====================================================
-- Drop tables in reverse order of dependencies to avoid foreign key conflicts

DROP TABLE IF EXISTS question_tags CASCADE;
DROP TABLE IF EXISTS exam_questions CASCADE;
DROP TABLE IF EXISTS question_options CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS exams CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS certifications CASCADE;

-- =====================================================
-- PART 2: CREATE TABLES
-- =====================================================

-- Table: certifications
-- Represents a certification or course that users can enroll in
CREATE TABLE certifications
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255)                          NOT NULL,
    code             VARCHAR(100)                          NOT NULL UNIQUE,
    provider         VARCHAR(100),
    description      TEXT,
    level            VARCHAR(50),
    duration_minutes INTEGER,
    passing_score    NUMERIC(5, 2),
    total_questions  INTEGER,
    price            NUMERIC(10, 2),
    status           VARCHAR(20),
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy        VARCHAR(50)
);

-- Table: topics
-- Represents knowledge domains within a certification
CREATE TABLE topics
(
    id                BIGSERIAL PRIMARY KEY,
    certification_id  BIGINT                                NOT NULL,
    name              VARCHAR(255)                          NOT NULL,
    code              VARCHAR(50),
    description       TEXT,
    weight_percentage NUMERIC(5, 2),
    order_index       INTEGER,
    status            VARCHAR(20),
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by        VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy         VARCHAR(50),
    CONSTRAINT fk_topic_certification FOREIGN KEY (certification_id)
        REFERENCES certifications (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Table: exams
-- Represents specific exam instances (practice, mock, final)
CREATE TABLE exams
(
    id               BIGSERIAL PRIMARY KEY,
    certification_id BIGINT                                NOT NULL,
    title            VARCHAR(255),
    type             VARCHAR(50),
    description      TEXT,
    duration_minutes INTEGER,
    total_questions  INTEGER,
    passing_score    INTEGER,
    status           VARCHAR(20),
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy        VARCHAR(50),
    CONSTRAINT fk_exam_certification FOREIGN KEY (certification_id)
        REFERENCES certifications (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Table: questions
-- Represents questions in the question bank
CREATE TABLE questions
(
    id                 BIGSERIAL PRIMARY KEY,
    topic_id           BIGINT                                NOT NULL,
    type               VARCHAR(50)                           NOT NULL,
    content            TEXT                                  NOT NULL,
    explanation        TEXT,
    difficulty         VARCHAR(20),
    points             INTEGER,
    time_limit_seconds INTEGER,
    reference_url      VARCHAR(500),
    status             VARCHAR(20),
    created_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by         VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy          VARCHAR(50),
    CONSTRAINT fk_question_topic FOREIGN KEY (topic_id)
        REFERENCES topics (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Table: question_options
-- Represents answer options for questions
CREATE TABLE question_options
(
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT                                NOT NULL,
    content     TEXT                                  NOT NULL,
    is_correct  BOOLEAN     DEFAULT FALSE,
    order_index INTEGER,
    status      VARCHAR(20),
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy   VARCHAR(50),
    CONSTRAINT fk_option_question FOREIGN KEY (question_id)
        REFERENCES questions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Table: exam_questions
-- Junction table linking exams to questions with ordering
CREATE TABLE exam_questions
(
    id          BIGSERIAL PRIMARY KEY,
    exam_id     BIGINT                                NOT NULL,
    question_id BIGINT                                NOT NULL,
    order_index INTEGER,
    status      VARCHAR(20),
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy   VARCHAR(50),
    CONSTRAINT fk_exam_question_exam FOREIGN KEY (exam_id)
        REFERENCES exams (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_exam_question_question FOREIGN KEY (question_id)
        REFERENCES questions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uk_exam_question UNIQUE (exam_id, question_id)
);

-- Table: tags
-- Represents classification tags for questions
CREATE TABLE tags
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100)                          NOT NULL UNIQUE,
    status     VARCHAR(20),
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy  VARCHAR(50)
);

-- Table: question_tags
-- Junction table linking questions to tags
CREATE TABLE question_tags
(
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT                                NOT NULL,
    tag_id      BIGINT                                NOT NULL,
    status      VARCHAR(20),
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updatedBy   VARCHAR(50),
    CONSTRAINT fk_question_tag_question FOREIGN KEY (question_id)
        REFERENCES questions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_question_tag_tag FOREIGN KEY (tag_id)
        REFERENCES tags (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uk_question_tag UNIQUE (question_id, tag_id)
);

-- =====================================================
-- PART 3: CREATE INDEXES
-- =====================================================

-- Indexes for certifications table
CREATE INDEX idx_certifications_status ON certifications (status);
CREATE INDEX idx_certifications_level ON certifications (level);
CREATE INDEX idx_certifications_provider ON certifications (provider);
CREATE INDEX idx_certifications_status_level ON certifications (status, level);

-- Indexes for topics table
CREATE INDEX idx_topics_certification_id ON topics (certification_id);
CREATE INDEX idx_topics_status ON topics (status);
CREATE INDEX idx_topics_cert_status ON topics (certification_id, status);
CREATE INDEX idx_topics_order ON topics (certification_id, order_index);

-- Indexes for exams table
CREATE INDEX idx_exams_certification_id ON exams (certification_id);
CREATE INDEX idx_exams_type ON exams (type);
CREATE INDEX idx_exams_status ON exams (status);
CREATE INDEX idx_exams_cert_type_status ON exams (certification_id, type, status);

-- Indexes for questions table
CREATE INDEX idx_questions_topic_id ON questions (topic_id);
CREATE INDEX idx_questions_type ON questions (type);
CREATE INDEX idx_questions_difficulty ON questions (difficulty);
CREATE INDEX idx_questions_status ON questions (status);
CREATE INDEX idx_questions_topic_difficulty ON questions (topic_id, difficulty, status);
CREATE INDEX idx_questions_topic_type_status ON questions (topic_id, type, status);

-- Indexes for question_options table
CREATE INDEX idx_question_options_question_id ON question_options (question_id);
CREATE INDEX idx_question_options_status ON question_options (status);
CREATE INDEX idx_question_options_question_order ON question_options (question_id, order_index);
CREATE INDEX idx_question_options_correct ON question_options (question_id, is_correct);

-- Indexes for exam_questions table
CREATE INDEX idx_exam_questions_exam_id ON exam_questions (exam_id);
CREATE INDEX idx_exam_questions_question_id ON exam_questions (question_id);
CREATE INDEX idx_exam_questions_status ON exam_questions (status);
CREATE INDEX idx_exam_questions_exam_order ON exam_questions (exam_id, order_index);
CREATE INDEX idx_exam_questions_question_exam ON exam_questions (question_id, exam_id);

-- Indexes for tags table
CREATE INDEX idx_tags_status ON tags (status);
CREATE INDEX idx_tags_name_lower ON tags (LOWER(name));

-- Indexes for question_tags table
CREATE INDEX idx_question_tags_question_id ON question_tags (question_id);
CREATE INDEX idx_question_tags_tag_id ON question_tags (tag_id);
CREATE INDEX idx_question_tags_status ON question_tags (status);
CREATE INDEX idx_question_tags_question_tag ON question_tags (question_id, tag_id);
CREATE INDEX idx_question_tags_tag_question ON question_tags (tag_id, question_id);

-- =====================================================
-- PART 4: INSERT SEED DATA
-- =====================================================
-- This section includes sample data for testing and development

-- =====================================================
-- Seed Data Script for Exam Service
-- =====================================================
-- This script inserts sample data for testing and development
-- Includes: 3 Certifications, Topics, Questions, Exams, Tags
-- =====================================================

-- =====================================================
-- Insert Tags (10-15 common tags)
-- =====================================================
INSERT INTO tags (id, name)
VALUES (1, 'core-java'),
       (2, 'spring-boot'),
       (3, 'spring-security'),
       (4, 'kubernetes-basics'),
       (5, 'kubernetes-advanced'),
       (6, 'networking'),
       (7, 'security'),
       (8, 'database'),
       (9, 'oop'),
       (10, 'collections'),
       (11, 'concurrency'),
       (12, 'microservices'),
       (13, 'docker'),
       (14, 'cloud-native'),
       (15, 'rest-api');

-- =====================================================
-- Certification 1: Oracle Certified Associate (OCA)
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score,
                            total_questions, price, status)
VALUES (1,
        'Oracle Certified Associate Java SE 11 Developer',
        'OCA-JAVA-SE-11',
        'Oracle',
        'The Oracle Certified Associate Java SE 11 Developer certification validates foundational knowledge of Java programming and object-oriented concepts.',
        'BEGINNER',
        180,
        68.00,
        50,
        245.00,
        'ACTIVE');

-- Topics for OCA
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index)
VALUES (1, 1, 'Java Basics', 'OCA-TOPIC-1',
        'Understanding Java basics including data types, operators, and control flow', 20.00, 1),
       (2, 1, 'Working with Java Data Types', 'OCA-TOPIC-2',
        'Primitive types, wrapper classes, and String manipulation', 15.00, 2),
       (3, 1, 'Using Operators and Decision Constructs', 'OCA-TOPIC-3', 'Operators, if-else, switch statements', 15.00,
        3),
       (4, 1, 'Creating and Using Arrays', 'OCA-TOPIC-4', 'Array declaration, initialization, and manipulation', 10.00,
        4),
       (5, 1, 'Using Loop Constructs', 'OCA-TOPIC-5', 'For, while, do-while loops and enhanced for loop', 10.00, 5),
       (6, 1, 'Working with Methods and Encapsulation', 'OCA-TOPIC-6',
        'Method declaration, access modifiers, encapsulation principles', 30.00, 6);

-- =====================================================
-- Certification 2: Spring Professional Certification
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score,
                            total_questions, price, status)
VALUES (2,
        'Spring Professional Certification',
        'SPRING-PRO-2024',
        'VMware',
        'The Spring Professional certification validates expertise in Spring Framework, Spring Boot, and enterprise application development.',
        'INTERMEDIATE',
        90,
        76.00,
        50,
        200.00,
        'ACTIVE');

-- Topics for Spring
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index)
VALUES ('7', 2, 'Container, Dependency, and IOC', 'SPRING-TOPIC-1',
        'Spring container, dependency injection, and inversion of control', 25.00, 1),
       (8, 2, 'Aspect Oriented Programming', 'SPRING-TOPIC-2', 'AOP concepts, pointcuts, advice, and aspects', 15.00,
        2),
       (9, 2, 'Data Management', 'SPRING-TOPIC-3', 'JDBC, transactions, JPA, and Spring Data', 20.00, 3),
       (10, 2, 'Spring Boot', 'SPRING-TOPIC-4', 'Auto-configuration, starters, and Spring Boot features', 20.00, 4),
       (11, 2, 'Spring MVC and REST', 'SPRING-TOPIC-5', 'Web applications, REST APIs, and Spring MVC', 15.00, 5),
       (12, 2, 'Security', 'SPRING-TOPIC-6', 'Spring Security fundamentals and authentication', 5.00, 6);

-- =====================================================
-- Certification 3: Certified Kubernetes Administrator (CKA)
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score,
                            total_questions, price, status)
VALUES (3,
        'Certified Kubernetes Administrator',
        'CKA-2024',
        'Cloud Native Computing Foundation',
        'The CKA certification validates skills in Kubernetes administration, including installation, configuration, and management of Kubernetes clusters.',
        'ADVANCED',
        120,
        66.00,
        15,
        395.00,
        'ACTIVE');

-- Topics for CKA
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index)
VALUES (13, 3, 'Cluster Architecture', 'CKA-TOPIC-1', 'Understanding Kubernetes architecture and components', 25.00, 1),
       (14, 3, 'Workloads and Scheduling', 'CKA-TOPIC-2', 'Deployments, pods, and scheduling', 15.00, 2),
       (15, 3, 'Services and Networking', 'CKA-TOPIC-3', 'Services, ingress, and network policies', 20.00, 3),
       (16, 3, 'Storage', 'CKA-TOPIC-4', 'Persistent volumes, storage classes', 10.00, 4),
       (17, 3, 'Troubleshooting', 'CKA-TOPIC-5', 'Debugging and troubleshooting cluster issues', 30.00, 5);


-- =====================================================
-- Questions for OCA Certification (20-30 questions)
-- =====================================================

-- Java Basics Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (1, 1, 'SINGLE_CHOICE',
        'What is the correct way to declare a main method in Java?',
        'The main method must be public static void main(String[] args) to be recognized as the entry point of a Java application.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (1, 1, 'public void main(String[] args)', false, 1),
       (2, 1, 'public static void main(String[] args)', true, 2),
       (3, 1, 'static void main(String[] args)', false, 3),
       (4, 1, 'public main(String[] args)', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (2, 1, 'SINGLE_CHOICE',
        'Which of the following is NOT a valid Java identifier?',
        'Java identifiers cannot start with a digit. They must start with a letter, underscore, or dollar sign.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (5, 2, '_variable', false, 1),
       (6, 2, '$amount', false, 2),
       (7, 2, '2ndValue', true, 3);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (3, 1, 'TRUE_FALSE',
        'Java is a platform-independent language.',
        'True. Java code is compiled to bytecode which can run on any platform with a JVM.',
        'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (8, 3, 'True', true, 1),
       (9, 3, 'False', false, 2);

-- Data Types Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (4, 2, 'SINGLE_CHOICE',
        'What is the size of an int in Java?',
        'An int in Java is always 32 bits (4 bytes), regardless of the platform.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (10, 4, '16 bits', false, 1),
       (11, 4, '32 bits', true, 2),
       (12, 4, '64 bits', false, 3),
       (13, 4, 'Platform dependent', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (5, 2, 'MULTIPLE_CHOICE',
        'Which of the following are wrapper classes in Java? (Select all that apply)',
        'Integer, Double, and Boolean are wrapper classes for primitive types int, double, and boolean respectively.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (14, 5, 'Integer', true, 1),
       (15, 5, 'String', false, 2),
       (16, 5, 'Double', true, 3),
       (17, 5, 'Boolean', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (6, 2, 'SINGLE_CHOICE',
        'What is the default value of a boolean variable in Java?',
        'The default value of a boolean instance variable is false.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (18, 6, 'true', false, 1),
       (19, 6, 'false', true, 2),
       (20, 6, 'null', false, 3),
       (21, 6, '0', false, 4);

-- Operators Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (7, 3, 'SINGLE_CHOICE',
        'What is the result of 10 % 3 in Java?',
        'The modulus operator % returns the remainder of division. 10 divided by 3 is 3 with remainder 1.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (22, 7, '3', false, 1),
       (23, 7, '1', true, 2),
       (24, 7, '0', false, 3),
       (25, 7, '10', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (8, 3, 'SINGLE_CHOICE',
        'What is the difference between == and equals() in Java?',
        '== compares references (memory addresses) while equals() compares the actual content of objects.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (26, 8, 'They are the same', false, 1),
       (27, 8, '== compares references, equals() compares content', true, 2),
       (28, 8, 'equals() is faster than ==', false, 3),
       (29, 8, '== can only be used with primitives', false, 4);

-- Arrays Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (9, 4, 'SINGLE_CHOICE',
        'How do you declare an array of integers in Java?',
        'Arrays can be declared using int[] arrayName or int arrayName[], but int[] is preferred.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (30, 9, 'int array[]', false, 1),
       (31, 9, 'int[] array', true, 2),
       (32, 9, 'array int[]', false, 3),
       (33, 9, 'int array()', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (10, 4, 'SINGLE_CHOICE',
        'What is the index of the first element in a Java array?',
        'Java arrays are zero-indexed, meaning the first element is at index 0.',
        'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (34, 10, '1', false, 1),
       (35, 10, '0', true, 2),
       (36, 10, '-1', false, 3),
       (37, 10, 'Depends on array size', false, 4);

-- Loop Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (11, 5, 'SINGLE_CHOICE',
        'Which loop is guaranteed to execute at least once?',
        'The do-while loop checks the condition after executing the loop body, so it always runs at least once.',
        'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (38, 11, 'for loop', false, 1),
       (39, 11, 'while loop', false, 2),
       (40, 11, 'do-while loop', true, 3),
       (41, 11, 'enhanced for loop', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (12, 5, 'MULTIPLE_CHOICE',
        'Which statements can be used to exit a loop? (Select all that apply)',
        'break exits the loop immediately, return exits the method (and thus the loop), and continue skips to the next iteration.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (42, 12, 'break', true, 1),
       (43, 12, 'continue', false, 2),
       (44, 12, 'return', true, 3),
       (45, 12, 'exit', false, 4);

-- Methods and Encapsulation Questions (Topic 6)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (13, 6, 'SINGLE_CHOICE',
        'Which access modifier makes a member accessible only within the same class?',
        'The private access modifier restricts access to only within the declaring class.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (46, 13, 'public', false, 1),
       (47, 13, 'private', true, 2),
       (48, 13, 'protected', false, 3),
       (49, 13, 'default', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (14, 6, 'SINGLE_CHOICE',
        'What is method overloading?',
        'Method overloading allows multiple methods with the same name but different parameters in the same class.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (50, 14, 'Multiple methods with same name and parameters', false, 1),
       (51, 14, 'Multiple methods with same name but different parameters', true, 2),
       (52, 14, 'Overriding a parent class method', false, 3),
       (53, 14, 'Using static methods', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (15, 6, 'MULTIPLE_CHOICE',
        'Which are principles of encapsulation? (Select all that apply)',
        'Encapsulation involves making fields private and providing public getter/setter methods to control access.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (54, 15, 'Make fields private', true, 1),
       (55, 15, 'Provide public getter/setter methods', true, 2),
       (56, 15, 'Make all methods static', false, 3),
       (57, 15, 'Use inheritance', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (16, 6, 'SINGLE_CHOICE',
        'Can a method have the same name as the class?',
        'Yes, but it is not a constructor unless it has no return type. A method can have the same name as the class.',
        'HARD', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (58, 16, 'No, never', false, 1),
       (59, 16, 'Yes, but it is not a constructor if it has a return type', true, 2),
       (60, 16, 'Only if it is static', false, 3),
       (61, 16, 'Only if it is private', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (17, 6, 'TRUE_FALSE',
        'A static method can access instance variables directly.',
        'False. Static methods belong to the class and cannot access instance variables without an object reference.',
        'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (62, 17, 'True', false, 1),
       (63, 17, 'False', true, 2);

-- Additional OCA questions for variety
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (18, 1, 'SINGLE_CHOICE',
        'What is the output of System.out.println(10 + 20 + "Java");?',
        'The expression is evaluated left to right. 10 + 20 = 30, then 30 + "Java" = "30Java".',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (64, 18, '1020Java', false, 1),
       (65, 18, '30Java', true, 2),
       (66, 18, 'Java30', false, 3),
       (67, 18, 'Compilation error', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (19, 2, 'SINGLE_CHOICE',
        'Which statement about String is correct?',
        'Strings are immutable in Java, meaning once created, their value cannot be changed.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (68, 19, 'Strings are mutable', false, 1),
       (69, 19, 'Strings are immutable', true, 2),
       (70, 19, 'Strings are primitive types', false, 3),
       (71, 19, 'Strings cannot be null', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (20, 4, 'SINGLE_CHOICE',
        'What happens when you try to access an array element beyond its length?',
        'Accessing an array element beyond its bounds throws an ArrayIndexOutOfBoundsException at runtime.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (72, 20, 'Returns null', false, 1),
       (73, 20, 'Returns 0', false, 2),
       (74, 20, 'Throws ArrayIndexOutOfBoundsException', true, 3),
       (75, 20, 'Compilation error', false, 4);


-- =====================================================
-- Questions for Spring Professional Certification (20-30 questions)
-- =====================================================

-- Container, Dependency, and IOC Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (21, '7', 'SINGLE_CHOICE',
        'What is Dependency Injection?',
        'Dependency Injection is a design pattern where dependencies are provided to an object rather than the object creating them itself.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (76, 21, 'A way to create objects', false, 1),
       (77, 21, 'A pattern where dependencies are provided to an object', true, 2),
       (78, 21, 'A database connection pattern', false, 3),
       (79, 21, 'A security mechanism', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (22, '7', 'MULTIPLE_CHOICE',
        'Which are valid ways to inject dependencies in Spring? (Select all that apply)',
        'Spring supports constructor injection, setter injection, and field injection.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (80, 22, 'Constructor injection', true, 1),
       (81, 22, 'Setter injection', true, 2),
       (82, 22, 'Field injection', true, 3),
       (83, 22, 'Method injection', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (23, '7', 'SINGLE_CHOICE',
        'What is the default scope of a Spring bean?',
        'The default scope is singleton, meaning one instance per Spring container.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (84, 23, 'prototype', false, 1),
       (85, 23, 'singleton', true, 2),
       (86, 23, 'request', false, 3),
       (87, 23, 'session', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (24, '7', 'SINGLE_CHOICE',
        'Which annotation is used to mark a class as a Spring bean?',
        '@Component is the generic stereotype annotation for any Spring-managed component.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (88, 24, '@Bean', false, 1),
       (89, 24, '@Component', true, 2),
       (90, 24, '@Autowired', false, 3),
       (91, 24, '@Configuration', false, 4);

-- AOP Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (25, 8, 'SINGLE_CHOICE',
        'What is a pointcut in Spring AOP?',
        'A pointcut is an expression that selects one or more join points where advice should be applied.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (92, 25, 'The code to be executed', false, 1),
       (93, 25, 'An expression that selects join points', true, 2),
       (94, 25, 'A Spring bean', false, 3),
       (95, 25, 'A database connection', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (26, 8, 'MULTIPLE_CHOICE',
        'Which are types of advice in Spring AOP? (Select all that apply)',
        'Spring AOP supports @Before, @After, @Around, @AfterReturning, and @AfterThrowing advice.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (96, 26, '@Before', true, 1),
       (97, 26, '@After', true, 2),
       (98, 26, '@Around', true, 3),
       (99, 26, '@During', false, 4);

-- Data Management Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (27, 9, 'SINGLE_CHOICE',
        'What annotation is used to mark a method as transactional?',
        '@Transactional marks a method or class to be executed within a transaction context.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (100, 27, '@Transaction', false, 1),
       (101, 27, '@Transactional', true, 2),
       (102, 27, '@Tx', false, 3),
       (103, 27, '@Database', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (28, 9, 'SINGLE_CHOICE',
        'What is the purpose of Spring Data JPA?',
        'Spring Data JPA simplifies data access by providing repository abstractions and reducing boilerplate code.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (104, 28, 'To replace JPA', false, 1),
       (105, 28, 'To simplify data access with repository abstractions', true, 2),
       (106, 28, 'To create databases', false, 3),
       (107, 28, 'To handle security', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (29, 9, 'TRUE_FALSE',
        'JpaRepository extends CrudRepository in Spring Data JPA.',
        'True. JpaRepository extends PagingAndSortingRepository which extends CrudRepository.',
        'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (108, 29, 'True', true, 1),
       (109, 29, 'False', false, 2);

-- Spring Boot Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (30, 10, 'SINGLE_CHOICE',
        'What is the purpose of @SpringBootApplication annotation?',
        '@SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (110, 30, 'To enable security', false, 1),
       (111, 30, 'To combine @Configuration, @EnableAutoConfiguration, and @ComponentScan', true, 2),
       (112, 30, 'To create a database', false, 3),
       (113, 30, 'To start a web server', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (31, 10, 'MULTIPLE_CHOICE',
        'Which are benefits of Spring Boot? (Select all that apply)',
        'Spring Boot provides auto-configuration, embedded servers, and production-ready features.',
        'EASY', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (114, 31, 'Auto-configuration', true, 1),
       (115, 31, 'Embedded servers', true, 2),
       (116, 31, 'Production-ready features', true, 3),
       (117, 31, 'Replaces Spring Framework', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (32, 10, 'SINGLE_CHOICE',
        'Where should you place application.properties in a Spring Boot project?',
        'application.properties should be placed in src/main/resources directory.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (118, 32, 'src/main/java', false, 1),
       (119, 32, 'src/main/resources', true, 2),
       (120, 32, 'src/test/java', false, 3),
       (121, 32, 'Root directory', false, 4);

-- Spring MVC and REST Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (33, 11, 'SINGLE_CHOICE',
        'Which annotation is used to create a RESTful controller?',
        '@RestController combines @Controller and @ResponseBody for REST APIs.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (122, 33, '@Controller', false, 1),
       (123, 33, '@RestController', true, 2),
       (124, 33, '@Service', false, 3),
       (125, 33, '@Repository', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (34, 11, 'MULTIPLE_CHOICE',
        'Which HTTP methods are commonly used in REST APIs? (Select all that apply)',
        'REST APIs commonly use GET, POST, PUT, DELETE, and PATCH methods.',
        'EASY', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (126, 34, 'GET', true, 1),
       (127, 34, 'POST', true, 2),
       (128, 34, 'PUT', true, 3),
       (129, 34, 'DELETE', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (35, 11, 'SINGLE_CHOICE',
        'What annotation is used to bind a path variable in Spring MVC?',
        '@PathVariable is used to extract values from the URI path.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (130, 35, '@RequestParam', false, 1),
       (131, 35, '@PathVariable', true, 2),
       (132, 35, '@RequestBody', false, 3),
       (133, 35, '@ModelAttribute', false, 4);

-- Security Questions (Topic 6)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (36, 12, 'SINGLE_CHOICE',
        'What is the purpose of Spring Security?',
        'Spring Security provides authentication, authorization, and protection against common security vulnerabilities.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (134, 36, 'To create databases', false, 1),
       (135, 36, 'To provide authentication and authorization', true, 2),
       (136, 36, 'To handle HTTP requests', false, 3),
       (137, 36, 'To manage transactions', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (37, 12, 'TRUE_FALSE',
        'Spring Security uses filters to intercept requests.',
        'True. Spring Security is based on a chain of servlet filters.',
        'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (138, 37, 'True', true, 1),
       (139, 37, 'False', false, 2);


-- =====================================================
-- Questions for CKA Certification (20-30 questions)
-- =====================================================

-- Cluster Architecture Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (38, 13, 'SINGLE_CHOICE',
        'What is the role of the kube-apiserver in Kubernetes?',
        'The kube-apiserver is the front-end for the Kubernetes control plane and exposes the Kubernetes API.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (140, 38, 'Schedules pods', false, 1),
       (141, 38, 'Exposes the Kubernetes API', true, 2),
       (142, 38, 'Stores cluster data', false, 3),
       (143, 38, 'Runs containers', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (39, 13, 'SINGLE_CHOICE',
        'Which component stores all cluster data in Kubernetes?',
        'etcd is a consistent and highly-available key-value store used as Kubernetes backing store for all cluster data.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (144, 39, 'kube-apiserver', false, 1),
       (145, 39, 'etcd', true, 2),
       (146, 39, 'kube-scheduler', false, 3),
       (147, 39, 'kubelet', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (40, 13, 'MULTIPLE_CHOICE',
        'Which are control plane components? (Select all that apply)',
        'Control plane components include kube-apiserver, etcd, kube-scheduler, and kube-controller-manager.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (148, 40, 'kube-apiserver', true, 1),
       (149, 40, 'etcd', true, 2),
       (150, 40, 'kubelet', false, 3),
       (151, 40, 'kube-scheduler', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (41, 13, 'TRUE_FALSE',
        'The kubelet runs on every node in the cluster.',
        'True. The kubelet is the primary node agent that runs on each node.',
        'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (152, 41, 'True', true, 1),
       (153, 41, 'False', false, 2);

-- Workloads and Scheduling Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (42, 14, 'SINGLE_CHOICE',
        'What is a Pod in Kubernetes?',
        'A Pod is the smallest deployable unit in Kubernetes and can contain one or more containers.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (154, 42, 'A single container', false, 1),
       (155, 42, 'The smallest deployable unit containing one or more containers', true, 2),
       (156, 42, 'A virtual machine', false, 3),
       (157, 42, 'A cluster node', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (43, 14, 'SINGLE_CHOICE',
        'What is the purpose of a Deployment in Kubernetes?',
        'A Deployment provides declarative updates for Pods and ReplicaSets.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (158, 43, 'To store data', false, 1),
       (159, 43, 'To provide declarative updates for Pods and ReplicaSets', true, 2),
       (160, 43, 'To expose services', false, 3),
       (161, 43, 'To manage secrets', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (44, 14, 'MULTIPLE_CHOICE',
        'Which workload resources are available in Kubernetes? (Select all that apply)',
        'Kubernetes provides Deployment, StatefulSet, DaemonSet, and Job as workload resources.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (163, 44, 'Deployment', true, 1),
       (164, 44, 'StatefulSet', true, 2),
       (165, 44, 'DaemonSet', true, 3),
       (166, 44, 'Service', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (45, 14, 'SINGLE_CHOICE',
        'What does a DaemonSet ensure?',
        'A DaemonSet ensures that all (or some) nodes run a copy of a Pod.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (167, 45, 'Pods are scheduled randomly', false, 1),
       (168, 45, 'All nodes run a copy of a Pod', true, 2),
       (169, 45, 'Only one Pod runs in the cluster', false, 3),
       (170, 45, 'Pods are never restarted', false, 4);

-- Services and Networking Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (46, 15, 'SINGLE_CHOICE',
        'What is the purpose of a Service in Kubernetes?',
        'A Service exposes a set of Pods as a network service with a stable IP address.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (171, 46, 'To store configuration', false, 1),
       (172, 46, 'To expose Pods as a network service', true, 2),
       (173, 46, 'To schedule Pods', false, 3),
       (174, 46, 'To manage storage', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (47, 15, 'MULTIPLE_CHOICE',
        'Which are valid Service types in Kubernetes? (Select all that apply)',
        'Kubernetes supports ClusterIP, NodePort, LoadBalancer, and ExternalName service types.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (175, 47, 'ClusterIP', true, 1),
       (176, 47, 'NodePort', true, 2),
       (177, 47, 'LoadBalancer', true, 3),
       (178, 47, 'InternalIP', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (48, 15, 'SINGLE_CHOICE',
        'What is an Ingress in Kubernetes?',
        'An Ingress manages external access to services, typically HTTP/HTTPS routing.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (179, 48, 'A type of Pod', false, 1),
       (180, 48, 'Manages external access to services', true, 2),
       (181, 48, 'A storage volume', false, 3),
       (182, 48, 'A network plugin', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (49, 15, 'TRUE_FALSE',
        'Network Policies in Kubernetes control traffic flow at the IP address or port level.',
        'True. Network Policies specify how groups of pods can communicate with each other and other network endpoints.',
        'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (183, 49, 'True', true, 1),
       (184, 49, 'False', false, 2);

-- Storage Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (50, 16, 'SINGLE_CHOICE',
        'What is a PersistentVolume (PV) in Kubernetes?',
        'A PersistentVolume is a piece of storage in the cluster that has been provisioned by an administrator or dynamically.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (185, 50, 'A temporary storage', false, 1),
       (186, 50, 'A piece of storage provisioned in the cluster', true, 2),
       (187, 50, 'A container image', false, 3),
       (188, 50, 'A network interface', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (51, 16, 'SINGLE_CHOICE',
        'What is the difference between a PersistentVolume and a PersistentVolumeClaim?',
        'A PV is the actual storage resource, while a PVC is a request for storage by a user.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (189, 51, 'They are the same', false, 1),
       (190, 51, 'PV is storage, PVC is a request for storage', true, 2),
       (191, 51, 'PVC is larger than PV', false, 3),
       (192, 51, 'PV is temporary, PVC is permanent', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (52, 16, 'MULTIPLE_CHOICE',
        'Which access modes are supported by PersistentVolumes? (Select all that apply)',
        'PersistentVolumes support ReadWriteOnce, ReadOnlyMany, and ReadWriteMany access modes.',
        'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (193, 52, 'ReadWriteOnce', true, 1),
       (194, 52, 'ReadOnlyMany', true, 2),
       (195, 52, 'ReadWriteMany', true, 3),
       (196, 52, 'WriteOnlyOnce', false, 4);

-- Troubleshooting Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (53, 17, 'SINGLE_CHOICE',
        'Which command is used to view logs of a Pod?',
        'kubectl logs <pod-name> is used to view logs from a Pod.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (197, 53, 'kubectl get logs', false, 1),
       (198, 53, 'kubectl logs', true, 2),
       (199, 53, 'kubectl describe logs', false, 3),
       (200, 53, 'kubectl view logs', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (54, 17, 'SINGLE_CHOICE',
        'Which command provides detailed information about a Kubernetes resource?',
        'kubectl describe <resource-type> <resource-name> provides detailed information.',
        'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (201, 54, 'kubectl get', false, 1),
       (202, 54, 'kubectl describe', true, 2),
       (203, 54, 'kubectl logs', false, 3),
       (204, 54, 'kubectl explain', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (55, 17, 'MULTIPLE_CHOICE',
        'Which are common reasons for a Pod to be in CrashLoopBackOff state? (Select all that apply)',
        'CrashLoopBackOff can be caused by application errors, missing dependencies, or incorrect configuration.',
        'HARD', 2, 120);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (205, 55, 'Application error in the container', true, 1),
       (206, 55, 'Missing dependencies', true, 2),
       (207, 55, 'Incorrect configuration', true, 3),
       (208, 55, 'Too many replicas', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (56, 17, 'SINGLE_CHOICE',
        'How can you execute a command inside a running container?',
        'kubectl exec -it <pod-name> -- <command> executes a command inside a container.',
        'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (209, 56, 'kubectl run', false, 1),
       (210, 56, 'kubectl exec', true, 2),
       (211, 56, 'kubectl attach', false, 3),
       (212, 56, 'kubectl connect', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds)
VALUES (57, 17, 'TRUE_FALSE',
        'The kubectl get events command can help troubleshoot cluster issues.',
        'True. Events provide information about what is happening in the cluster.',
        'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index)
VALUES (213, 57, 'True', true, 1),
       (214, 57, 'False', false, 2);


-- =====================================================
-- Insert Question Tags (linking questions to tags)
-- =====================================================

-- OCA Question Tags
INSERT INTO question_tags (id, question_id, tag_id)
VALUES (1, 1, 1),   -- core-java
       (2, 2, 1),   -- core-java
       (3, 3, 1),   -- core-java
       (4, 4, 1),   -- core-java
       (5, 5, 1),   -- core-java
       (6, 6, 1),   -- core-java
       (7, 7, 1),   -- core-java
       (8, 8, 1),   -- core-java
       (9, 8, 10),  -- collections
       (10, 9, 10),  -- collections
       (11, 10, 10), -- collections
       (12, 11, 1),  -- core-java
       (13, 12, 1),  -- core-java
       (14, 13, 9),  -- oop
       (15, 14, 9),  -- oop
       (16, 15, 9),  -- oop
       (17, 16, 1),  -- core-java
       (18, 17, 1),  -- core-java
       (19, 18, 1),  -- core-java
       (20, 19, 1),  -- core-java
       (21, 20, 10);
-- collections

-- Spring Question Tags
INSERT INTO question_tags (id, question_id, tag_id)
VALUES (22, 21, 2),  -- spring-boot
       (23, 22, 2),  -- spring-boot
       (24, 23, 2),  -- spring-boot
       (25, 24, 2),  -- spring-boot
       (26, 25, 2),  -- spring-boot
       (27, 26, 2),  -- spring-boot
       (28, 27, 8),  -- database
       (29, 28, 8),  -- database
       (30, 29, 8),  -- database
       (31, 30, 2),  -- spring-boot
       (32, 31, 2),  -- spring-boot
       (33, 32, 2),  -- spring-boot
       (34, 33, 15), -- rest-api
       (35, 34, 15), -- rest-api
       (36, 35, 15), -- rest-api
       (37, 36, 3),  -- spring-security
       (38, 36, 7),  -- security
       (39, 37, 3),  -- spring-security
       (40, 37, 7);
-- security

-- CKA Question Tags
INSERT INTO question_tags (id, question_id, tag_id)
VALUES (41, 38, 4), -- kubernetes-basics
       (42, 39, 4), -- kubernetes-basics
       (43, 40, 4), -- kubernetes-basics
       (44, 41, 4), -- kubernetes-basics
       (45, 42, 4), -- kubernetes-basics
       (46, 43, 4), -- kubernetes-basics
       (47, 44, 5), -- kubernetes-advanced
       (48, 45, 5), -- kubernetes-advanced
       (49, 46, 6), -- networking
       (50, 47, 6), -- networking
       (51, 48, 6), -- networking
       (52, 49, 6), -- networking
       (53, 49, 7), -- security
       (54, 50, 5), -- kubernetes-advanced
       (55, 51, 5), -- kubernetes-advanced
       (56, 52, 5), -- kubernetes-advanced
       (57, 53, 4), -- kubernetes-basics
       (58, 54, 4), -- kubernetes-basics
       (59, 55, 5), -- kubernetes-advanced
       (60, 56, 4), -- kubernetes-basics
       (61, 57, 4);
-- kubernetes-basics

-- =====================================================
-- Insert Exams (2-3 per certification)
-- =====================================================

-- OCA Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score,
                   status)
VALUES (1, 1,
        'OCA Java SE 11 Practice Test 1', 'PRACTICE',
        'Practice test covering all OCA Java SE 11 topics', 120, 20, 65, 'ACTIVE'),
       (2, 1,
        'OCA Java SE 11 Mock Exam', 'MOCK',
        'Full-length mock exam simulating the actual OCA exam', 180, 50, 68, 'ACTIVE'),
       (3, 1,
        'OCA Java SE 11 Final Exam', 'FINAL',
        'Official OCA Java SE 11 certification exam', 180, 50, 68, 'ACTIVE');

-- Spring Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score,
                   status)
VALUES (4, 2,
        'Spring Professional Practice Test', 'PRACTICE',
        'Practice test for Spring Professional certification', 60, 15, 70, 'ACTIVE'),
       (5, 2,
        'Spring Professional Mock Exam', 'MOCK',
        'Full-length mock exam for Spring Professional', 90, 50, 76, 'ACTIVE'),
       (6, 2,
        'Spring Professional Final Exam', 'FINAL',
        'Official Spring Professional certification exam', 90, 50, 76, 'ACTIVE');

-- CKA Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score,
                   status)
VALUES (7, 3,
        'CKA Practice Exam', 'PRACTICE',
        'Practice exam for Certified Kubernetes Administrator', 60, 10, 60, 'ACTIVE'),
       (8, 3,
        'CKA Mock Exam', 'MOCK',
        'Full-length mock exam for CKA certification', 120, 15, 66, 'ACTIVE'),
       (9, 3,
        'CKA Final Exam', 'FINAL',
        'Official CKA certification exam', 120, 15, 66, 'ACTIVE');

-- =====================================================
-- Insert Exam Questions (linking exams to questions)
-- =====================================================

-- OCA Practice Test 1 (20 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index)
VALUES (1, 1, 1, 1),
       (2, 1, 2, 2),
       (3, 1, 3, 3),
       (4, 1, 4, 4),
       (5, 1, 5, 5),
       (6, 1, 6, 6),
       (7, 1, 7, 7),
       (8, 1, 8, 8),
       (9, 1, 9, 9),
       (10, 1, 10, 10),
       (11, 1, 11, 11),
       (12, 1, 12, 12),
       (13, 1, 13, 13),
       (14, 1, 14, 14),
       (15, 1, 15, 15),
       (16, 1, 16, 16),
       (17, 1, 17, 17),
       (18, 1, 18, 18),
       (19, 1, 19, 19),
       (20, 1, 20, 20);

-- Spring Practice Test (15 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index)
VALUES (21, 4, 21, 1),
       (22, 4, 22, 2),
       (23, 4, 23, 3),
       (24, 4, 24, 4),
       (25, 4, 25, 5),
       (26, 4, 26, 6),
       (27, 4, 27, 7),
       (28, 4, 28, 8),
       (29, 4, 29, 9),
       (30, 4, 30, 10),
       (31, 4, 31, 11),
       (32, 4, 32, 12),
       (33, 4, 33, 13),
       (34, 4, 34, 14),
       (35, 4, 35, 15);

-- CKA Practice Exam (10 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index)
VALUES (36, 7, 38, 1),
       (37, 7, 39, 2),
       (38, 7, 42, 3),
       (39, 7, 43, 4),
       (40, 7, 46, 5),
       (41, 7, 47, 6),
       (42, 7, 50, 7),
       (43, 7, 51, 8),
       (44, 7, 53, 9),
       (45, 7, 54, 10);

-- =====================================================
-- End of Seed Data
-- =====================================================

-- =====================================================
-- SETUP COMPLETE
-- =====================================================
-- The database is now ready with:
-- - 8 tables (certifications, topics, exams, questions, question_options, exam_questions, tags, question_tags)
-- - All necessary indexes for optimal query performance
-- - 3 certifications (OCA Java SE 11, Spring Professional, CKA)
-- - 16 topics across all certifications
-- - 60+ questions with options
-- - 9 exams (3 per certification: PRACTICE, MOCK, FINAL)
-- - 15 tags for question categorization
-- - All relationships properly established
--
-- You can now start using the exam-service database!
-- =====================================================
-- ROLLBACK;