-- =====================================================
-- Schema Creation Script for Exam Service
-- =====================================================
-- This script creates all tables for the exam-service
-- including constraints, foreign keys, and check constraints
-- =====================================================

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS question_tags CASCADE;
DROP TABLE IF EXISTS exam_questions CASCADE;
DROP TABLE IF EXISTS question_options CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS exams CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS certifications CASCADE;

-- =====================================================
-- Table: certifications
-- =====================================================
-- Represents a certification or course that users can enroll in
CREATE TABLE certifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    provider VARCHAR(100),
    description TEXT,
    level VARCHAR(50) CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    duration_minutes INTEGER CHECK (duration_minutes > 0),
    passing_score NUMERIC(5,2) CHECK (passing_score >= 0 AND passing_score <= 100),
    total_questions INTEGER CHECK (total_questions > 0),
    price NUMERIC(10,2) CHECK (price >= 0),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE certifications IS 'Stores certification/course information';
COMMENT ON COLUMN certifications.code IS 'Unique identifier code for the certification (e.g., SAA-C03)';
COMMENT ON COLUMN certifications.passing_score IS 'Minimum score required to pass (percentage)';

-- =====================================================
-- Table: topics
-- =====================================================
-- Represents knowledge domains within a certification
CREATE TABLE topics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    certification_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    weight_percentage NUMERIC(5,2) CHECK (weight_percentage >= 0 AND weight_percentage <= 100),
    order_index INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_topic_certification FOREIGN KEY (certification_id) 
        REFERENCES certifications(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

COMMENT ON TABLE topics IS 'Stores topics/domains within certifications';
COMMENT ON COLUMN topics.weight_percentage IS 'Weight of this topic in the exam (percentage)';
COMMENT ON COLUMN topics.order_index IS 'Display order of the topic';

-- =====================================================
-- Table: exams
-- =====================================================
-- Represents specific exam instances (practice, mock, final)
CREATE TABLE exams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    certification_id UUID NOT NULL,
    title VARCHAR(255),
    type VARCHAR(50) CHECK (type IN ('PRACTICE', 'MOCK', 'FINAL', 'DIAGNOSTIC', 'TOPIC_WISE')),
    description TEXT,
    duration_minutes INTEGER CHECK (duration_minutes > 0),
    total_questions INTEGER CHECK (total_questions > 0),
    passing_score INTEGER CHECK (passing_score >= 0 AND passing_score <= 100),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exam_certification FOREIGN KEY (certification_id) 
        REFERENCES certifications(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

COMMENT ON TABLE exams IS 'Stores exam instances for certifications';
COMMENT ON COLUMN exams.type IS 'Type of exam: PRACTICE, MOCK, FINAL, DIAGNOSTIC, or TOPIC_WISE';

-- =====================================================
-- Table: questions
-- =====================================================
-- Represents questions in the question bank
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE')),
    content TEXT NOT NULL,
    explanation TEXT,
    difficulty VARCHAR(20) CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    points INTEGER DEFAULT 1 CHECK (points > 0),
    time_limit_seconds INTEGER CHECK (time_limit_seconds > 0),
    reference_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_topic FOREIGN KEY (topic_id) 
        REFERENCES topics(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

COMMENT ON TABLE questions IS 'Stores questions that can be reused across multiple exams';
COMMENT ON COLUMN questions.type IS 'Question type: SINGLE_CHOICE, MULTIPLE_CHOICE, or TRUE_FALSE';
COMMENT ON COLUMN questions.explanation IS 'Explanation of the correct answer';

-- =====================================================
-- Table: question_options
-- =====================================================
-- Represents answer options for questions
CREATE TABLE question_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    order_index INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_option_question FOREIGN KEY (question_id) 
        REFERENCES questions(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

COMMENT ON TABLE question_options IS 'Stores answer options for questions';
COMMENT ON COLUMN question_options.is_correct IS 'Indicates if this option is a correct answer';

-- =====================================================
-- Table: exam_questions
-- =====================================================
-- Junction table linking exams to questions with ordering
CREATE TABLE exam_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id UUID NOT NULL,
    question_id UUID NOT NULL,
    order_index INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_examquestion_exam FOREIGN KEY (exam_id) 
        REFERENCES exams(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_examquestion_question FOREIGN KEY (question_id) 
        REFERENCES questions(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT uk_exam_question UNIQUE (exam_id, question_id)
);

COMMENT ON TABLE exam_questions IS 'Junction table managing many-to-many relationship between exams and questions';
COMMENT ON COLUMN exam_questions.order_index IS 'Order of the question within the exam';

-- =====================================================
-- Table: tags
-- =====================================================
-- Represents classification tags for questions
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tags IS 'Stores tags for categorizing questions';

-- =====================================================
-- Table: question_tags
-- =====================================================
-- Junction table linking questions to tags
CREATE TABLE question_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questiontag_question FOREIGN KEY (question_id) 
        REFERENCES questions(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_questiontag_tag FOREIGN KEY (tag_id) 
        REFERENCES tags(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT uk_question_tag UNIQUE (question_id, tag_id)
);

COMMENT ON TABLE question_tags IS 'Junction table managing many-to-many relationship between questions and tags';
