-- ============================================================================
-- Exam Service Migration: User Exam Sessions and Answers
-- ============================================================================
-- Description: Adds tables for managing user exam sessions locally in exam-service
--              - user_exam_sessions: Tracks user's exam session state and progress
--              - user_answers: Stores user's answers for each question in a session
-- Version: 1.0
-- Database: PostgreSQL 12+
-- Requirements: 5.1, 5.2 (Exam Session Migration Spec)
-- ============================================================================

-- Start transaction
BEGIN;

-- ============================================================================
-- SECTION 1: CREATE TABLES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table 1: user_exam_sessions
-- Description: Tracks user's exam session state, progress, and statistics
-- Migrated from result-service to exam-service for local session management
-- ----------------------------------------------------------------------------
CREATE TABLE user_exam_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    certification_id BIGINT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    mode VARCHAR(20) NOT NULL,
    exam_title VARCHAR(500),
    total_questions INTEGER NOT NULL,
    duration_minutes INTEGER,
    answered_count INTEGER DEFAULT 0,
    correct_count INTEGER DEFAULT 0,
    wrong_count INTEGER DEFAULT 0,
    unanswered_count INTEGER,
    flagged_count INTEGER DEFAULT 0,
    time_spent_seconds INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    CONSTRAINT fk_session_exam FOREIGN KEY (exam_id)
        REFERENCES exams(id) ON DELETE CASCADE,
    CONSTRAINT fk_session_certification FOREIGN KEY (certification_id)
        REFERENCES certifications(id) ON DELETE SET NULL,
    CONSTRAINT chk_session_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'TIMED_OUT')),
    CONSTRAINT chk_session_mode CHECK (mode IN ('PRACTICE', 'TIMED')),
    CONSTRAINT chk_session_counts CHECK (
        answered_count >= 0 AND
        correct_count >= 0 AND
        wrong_count >= 0 AND
        flagged_count >= 0 AND
        time_spent_seconds >= 0
    )
);

COMMENT ON TABLE user_exam_sessions IS 'Tracks user exam session state and progress - migrated from result-service';
COMMENT ON COLUMN user_exam_sessions.id IS 'Primary key - auto-increment session ID';
COMMENT ON COLUMN user_exam_sessions.user_id IS 'User ID from auth-service';
COMMENT ON COLUMN user_exam_sessions.exam_id IS 'Foreign key to exams table';
COMMENT ON COLUMN user_exam_sessions.certification_id IS 'Foreign key to certifications table';
COMMENT ON COLUMN user_exam_sessions.start_time IS 'Timestamp when session started';
COMMENT ON COLUMN user_exam_sessions.end_time IS 'Timestamp when session completed (null if in progress)';
COMMENT ON COLUMN user_exam_sessions.status IS 'Session status: IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT';
COMMENT ON COLUMN user_exam_sessions.mode IS 'Exam mode: PRACTICE (immediate feedback) or TIMED (no feedback until complete)';
COMMENT ON COLUMN user_exam_sessions.exam_title IS 'Cached exam title for display';
COMMENT ON COLUMN user_exam_sessions.total_questions IS 'Total number of questions in this session';
COMMENT ON COLUMN user_exam_sessions.duration_minutes IS 'Time limit in minutes (null for unlimited)';
COMMENT ON COLUMN user_exam_sessions.answered_count IS 'Number of questions answered';
COMMENT ON COLUMN user_exam_sessions.correct_count IS 'Number of correct answers';
COMMENT ON COLUMN user_exam_sessions.wrong_count IS 'Number of wrong answers';
COMMENT ON COLUMN user_exam_sessions.unanswered_count IS 'Number of unanswered questions';
COMMENT ON COLUMN user_exam_sessions.flagged_count IS 'Number of flagged questions for review';
COMMENT ON COLUMN user_exam_sessions.time_spent_seconds IS 'Total time spent on exam in seconds';

-- ----------------------------------------------------------------------------
-- Table 2: user_answers
-- Description: Stores user's answers for each question in a session
-- Includes topic information for result calculation
-- ----------------------------------------------------------------------------
CREATE TABLE user_answers (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_ids BIGINT[],
    correct_option_ids BIGINT[],
    is_correct BOOLEAN,
    is_flagged BOOLEAN DEFAULT FALSE,
    time_spent_seconds INTEGER DEFAULT 0,
    answered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    CONSTRAINT fk_answer_session FOREIGN KEY (session_id)
        REFERENCES user_exam_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id)
        REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT chk_answer_time CHECK (time_spent_seconds >= 0)
);

COMMENT ON TABLE user_answers IS 'Stores user answers for each question in an exam session';
COMMENT ON COLUMN user_answers.id IS 'Primary key - auto-increment answer ID';
COMMENT ON COLUMN user_answers.session_id IS 'Foreign key to user_exam_sessions table';
COMMENT ON COLUMN user_answers.question_id IS 'Foreign key to questions table';
COMMENT ON COLUMN user_answers.selected_option_ids IS 'Array of selected option IDs';
COMMENT ON COLUMN user_answers.correct_option_ids IS 'Array of correct option IDs (cached for result calculation)';
COMMENT ON COLUMN user_answers.is_correct IS 'Whether the answer is correct (null if not yet answered)';
COMMENT ON COLUMN user_answers.is_flagged IS 'Whether the question is flagged for review';
COMMENT ON COLUMN user_answers.time_spent_seconds IS 'Time spent on this question in seconds';
COMMENT ON COLUMN user_answers.answered_at IS 'Timestamp when answer was submitted';

-- ============================================================================
-- SECTION 2: CREATE PERFORMANCE INDEXES
-- ============================================================================

-- User exam sessions indexes
-- Index for finding active sessions by user
CREATE INDEX idx_user_exam_sessions_user_status 
    ON user_exam_sessions(user_id, status);

-- Index for finding sessions by user and exam (duplicate check)
CREATE INDEX idx_user_exam_sessions_user_exam 
    ON user_exam_sessions(user_id, exam_id);

-- Index for finding sessions by user and exam with status (active session check)
CREATE INDEX idx_user_exam_sessions_user_exam_status 
    ON user_exam_sessions(user_id, exam_id, status);

-- Index for session lookup by ID and user (ownership validation)
CREATE INDEX idx_user_exam_sessions_id_user 
    ON user_exam_sessions(id, user_id);

-- Index for filtering by status
CREATE INDEX idx_user_exam_sessions_status 
    ON user_exam_sessions(status);

-- Index for exam statistics queries
CREATE INDEX idx_user_exam_sessions_exam_id 
    ON user_exam_sessions(exam_id);

-- Index for certification statistics queries
CREATE INDEX idx_user_exam_sessions_certification_id 
    ON user_exam_sessions(certification_id) 
    WHERE certification_id IS NOT NULL;

-- User answers indexes
-- Index for finding answers by session
CREATE INDEX idx_user_answers_session 
    ON user_answers(session_id);

-- Unique index for session-question combination (one answer per question per session)
CREATE UNIQUE INDEX idx_user_answers_session_question 
    ON user_answers(session_id, question_id);

-- Index for finding specific answer by session and question
CREATE INDEX idx_user_answers_session_question_lookup 
    ON user_answers(session_id, question_id);

-- Index for counting answered questions
CREATE INDEX idx_user_answers_session_answered 
    ON user_answers(session_id) 
    WHERE selected_option_ids IS NOT NULL;

-- Index for counting correct answers
CREATE INDEX idx_user_answers_session_correct 
    ON user_answers(session_id, is_correct) 
    WHERE is_correct = TRUE;

-- Index for counting flagged questions
CREATE INDEX idx_user_answers_session_flagged 
    ON user_answers(session_id, is_flagged) 
    WHERE is_flagged = TRUE;

-- Index for question statistics
CREATE INDEX idx_user_answers_question_id 
    ON user_answers(question_id);

COMMIT;

-- ============================================================================
-- MIGRATION SUMMARY
-- ============================================================================
-- Tables created: 2
-- 1. user_exam_sessions - Tracks user exam session state and progress
-- 2. user_answers - Stores user answers for each question in a session
--
-- Indexes created: 13
-- - 7 indexes on user_exam_sessions for session lookup and filtering
-- - 6 indexes on user_answers for answer lookup and statistics
--
-- Foreign keys:
-- - user_exam_sessions.exam_id -> exams.id (CASCADE DELETE)
-- - user_exam_sessions.certification_id -> certifications.id (SET NULL)
-- - user_answers.session_id -> user_exam_sessions.id (CASCADE DELETE)
-- - user_answers.question_id -> questions.id (CASCADE DELETE)
--
-- Constraints:
-- - Status must be one of: IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT
-- - Mode must be one of: PRACTICE, TIMED
-- - Count fields must be non-negative
-- - Unique constraint on (session_id, question_id) in user_answers
-- ============================================================================
