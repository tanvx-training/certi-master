package com.certimaster.auth_service.constant;

/**
 * Permission constants for the CertiMaster authorization system.
 * 
 * Permission format: "resource:action" (lowercase, colon-separated)
 * 
 * Requirements:
 * - 3.1: Define permissions using string format "resource:action"
 * 
 * Role-Permission Mapping:
 * - ADMIN: All permissions
 * - INSTRUCTOR: exam:*, question:*, result:read_all
 * - STUDENT: exam:read, question:read, result:read
 */
public final class Permissions {

    private Permissions() {
        // Prevent instantiation
    }

    // ========================================================================
    // USER MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new users */
    public static final String USER_CREATE = "user:create";
    
    /** Read user information */
    public static final String USER_READ = "user:read";
    
    /** Update user information */
    public static final String USER_UPDATE = "user:update";
    
    /** Delete users */
    public static final String USER_DELETE = "user:delete";
    
    /** Read all users (admin only) */
    public static final String USER_READ_ALL = "user:read_all";

    // ========================================================================
    // ROLE MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new roles */
    public static final String ROLE_CREATE = "role:create";
    
    /** Read role information */
    public static final String ROLE_READ = "role:read";
    
    /** Update role information */
    public static final String ROLE_UPDATE = "role:update";
    
    /** Delete roles */
    public static final String ROLE_DELETE = "role:delete";
    
    /** Assign roles to users */
    public static final String ROLE_ASSIGN = "role:assign";

    // ========================================================================
    // EXAM MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new exams */
    public static final String EXAM_CREATE = "exam:create";
    
    /** Read exam information */
    public static final String EXAM_READ = "exam:read";
    
    /** Update exam information */
    public static final String EXAM_UPDATE = "exam:update";
    
    /** Delete exams */
    public static final String EXAM_DELETE = "exam:delete";

    // ========================================================================
    // QUESTION MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new questions */
    public static final String QUESTION_CREATE = "question:create";
    
    /** Read question information */
    public static final String QUESTION_READ = "question:read";
    
    /** Update question information */
    public static final String QUESTION_UPDATE = "question:update";
    
    /** Delete questions */
    public static final String QUESTION_DELETE = "question:delete";

    // ========================================================================
    // CERTIFICATION MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new certifications */
    public static final String CERTIFICATION_CREATE = "certification:create";
    
    /** Read certification information */
    public static final String CERTIFICATION_READ = "certification:read";
    
    /** Update certification information */
    public static final String CERTIFICATION_UPDATE = "certification:update";
    
    /** Delete certifications */
    public static final String CERTIFICATION_DELETE = "certification:delete";

    // ========================================================================
    // TOPIC MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new topics */
    public static final String TOPIC_CREATE = "topic:create";
    
    /** Read topic information */
    public static final String TOPIC_READ = "topic:read";
    
    /** Update topic information */
    public static final String TOPIC_UPDATE = "topic:update";
    
    /** Delete topics */
    public static final String TOPIC_DELETE = "topic:delete";

    // ========================================================================
    // TAG MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Create new tags */
    public static final String TAG_CREATE = "tag:create";
    
    /** Read tag information */
    public static final String TAG_READ = "tag:read";
    
    /** Update tag information */
    public static final String TAG_UPDATE = "tag:update";
    
    /** Delete tags */
    public static final String TAG_DELETE = "tag:delete";

    // ========================================================================
    // RESULT MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Read own results */
    public static final String RESULT_READ = "result:read";
    
    /** Read all results (instructor/admin) */
    public static final String RESULT_READ_ALL = "result:read_all";

    // ========================================================================
    // SYSTEM MANAGEMENT PERMISSIONS
    // ========================================================================
    
    /** Configure system settings */
    public static final String SYSTEM_CONFIG = "system:config";
    
    /** View audit logs */
    public static final String SYSTEM_AUDIT = "system:audit";

    // ========================================================================
    // ROLE CODES
    // ========================================================================
    
    /** Admin role code */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /** Instructor role code */
    public static final String ROLE_INSTRUCTOR = "INSTRUCTOR";
    
    /** Student role code */
    public static final String ROLE_STUDENT = "STUDENT";
}
