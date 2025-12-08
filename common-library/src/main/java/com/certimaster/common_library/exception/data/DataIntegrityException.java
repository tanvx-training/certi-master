package com.certimaster.common_library.exception.data;

import com.certimaster.common_library.exception.business.BaseException;

/**
 * Exception for data integrity violations
 */
public class DataIntegrityException extends BaseException {

    public DataIntegrityException(String message) {
        super("DATA_INTEGRITY_VIOLATION", message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super("DATA_INTEGRITY_VIOLATION", message, cause);
    }

    // Factory methods

    public static DataIntegrityException uniqueConstraint(String field, Object value) {
        DataIntegrityException ex = new DataIntegrityException(
                String.format("Value '%s' already exists for field '%s'", value, field));
        ex.addDetail("field", field);
        ex.addDetail("value", value);
        ex.addDetail("constraintType", "UNIQUE");
        return ex;
    }

    public static DataIntegrityException foreignKeyConstraint(String entity, String referencedEntity) {
        DataIntegrityException ex = new DataIntegrityException(
                String.format("Cannot delete/update '%s' because it is referenced by '%s'",
                        entity, referencedEntity));
        ex.addDetail("entity", entity);
        ex.addDetail("referencedBy", referencedEntity);
        ex.addDetail("constraintType", "FOREIGN_KEY");
        return ex;
    }

    public static DataIntegrityException notNullConstraint(String field) {
        DataIntegrityException ex = new DataIntegrityException(
                String.format("Field '%s' cannot be null", field));
        ex.addDetail("field", field);
        ex.addDetail("constraintType", "NOT_NULL");
        return ex;
    }
}
