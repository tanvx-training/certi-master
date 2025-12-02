-- =====================================================
-- Index Creation Script for Exam Service
-- =====================================================
-- This script creates indexes to optimize query performance
-- =====================================================

-- =====================================================
-- Indexes for certifications table
-- =====================================================
-- Unique index on code (already created by UNIQUE constraint)
-- Index for status filtering
CREATE INDEX idx_certifications_status ON certifications(status);

-- Index for level filtering
CREATE INDEX idx_certifications_level ON certifications(level);

-- Index for provider filtering
CREATE INDEX idx_certifications_provider ON certifications(provider);

-- Composite index for active certifications by level
CREATE INDEX idx_certifications_status_level ON certifications(status, level);

-- =====================================================
-- Indexes for topics table
-- =====================================================
-- Foreign key index
CREATE INDEX idx_topics_certification_id ON topics(certification_id);

-- Index for status filtering
CREATE INDEX idx_topics_status ON topics(status);

-- Composite index for active topics by certification
CREATE INDEX idx_topics_cert_status ON topics(certification_id, status);

-- Index for ordering
CREATE INDEX idx_topics_order ON topics(certification_id, order_index);

-- =====================================================
-- Indexes for exams table
-- =====================================================
-- Foreign key index
CREATE INDEX idx_exams_certification_id ON exams(certification_id);

-- Index for type filtering (frequently queried)
CREATE INDEX idx_exams_type ON exams(type);

-- Index for status filtering
CREATE INDEX idx_exams_status ON exams(status);

-- Composite index for active exams by certification and type
CREATE INDEX idx_exams_cert_type_status ON exams(certification_id, type, status);

-- =====================================================
-- Indexes for questions table
-- =====================================================
-- Foreign key index
CREATE INDEX idx_questions_topic_id ON questions(topic_id);

-- Index for type filtering
CREATE INDEX idx_questions_type ON questions(type);

-- Index for difficulty filtering
CREATE INDEX idx_questions_difficulty ON questions(difficulty);

-- Index for status filtering
CREATE INDEX idx_questions_status ON questions(status);

-- Composite index for active questions by topic and difficulty
CREATE INDEX idx_questions_topic_difficulty ON questions(topic_id, difficulty, status);

-- Composite index for question selection queries
CREATE INDEX idx_questions_topic_type_status ON questions(topic_id, type, status);

-- =====================================================
-- Indexes for question_options table
-- =====================================================
-- Foreign key index
CREATE INDEX idx_question_options_question_id ON question_options(question_id);

-- Index for status filtering
CREATE INDEX idx_question_options_status ON question_options(status);

-- Composite index for retrieving options with question
CREATE INDEX idx_question_options_question_order ON question_options(question_id, order_index);

-- Index for correct answers
CREATE INDEX idx_question_options_correct ON question_options(question_id, is_correct);

-- =====================================================
-- Indexes for exam_questions table
-- =====================================================
-- Foreign key indexes
CREATE INDEX idx_exam_questions_exam_id ON exam_questions(exam_id);
CREATE INDEX idx_exam_questions_question_id ON exam_questions(question_id);

-- Index for status filtering
CREATE INDEX idx_exam_questions_status ON exam_questions(status);

-- Composite index for retrieving questions in order for an exam
CREATE INDEX idx_exam_questions_exam_order ON exam_questions(exam_id, order_index);

-- Composite index for finding exams containing a specific question
CREATE INDEX idx_exam_questions_question_exam ON exam_questions(question_id, exam_id);

-- =====================================================
-- Indexes for tags table
-- =====================================================
-- Unique index on name (already created by UNIQUE constraint)
-- Index for status filtering
CREATE INDEX idx_tags_status ON tags(status);

-- Index for name search (case-insensitive)
CREATE INDEX idx_tags_name_lower ON tags(LOWER(name));

-- =====================================================
-- Indexes for question_tags table
-- =====================================================
-- Foreign key indexes
CREATE INDEX idx_question_tags_question_id ON question_tags(question_id);
CREATE INDEX idx_question_tags_tag_id ON question_tags(tag_id);

-- Index for status filtering
CREATE INDEX idx_question_tags_status ON question_tags(status);

-- Composite index for finding all tags for a question
CREATE INDEX idx_question_tags_question_tag ON question_tags(question_id, tag_id);

-- Composite index for finding all questions with a specific tag
CREATE INDEX idx_question_tags_tag_question ON question_tags(tag_id, question_id);

-- =====================================================
-- Performance Notes
-- =====================================================
-- 1. All foreign key columns are indexed to improve JOIN performance
-- 2. Status columns are indexed to support filtering active/inactive records
-- 3. Composite indexes are created for common query patterns
-- 4. Order indexes help with sorting and pagination
-- 5. Consider monitoring query performance and adding additional indexes as needed
