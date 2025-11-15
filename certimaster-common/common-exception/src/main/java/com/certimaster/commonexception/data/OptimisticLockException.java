package com.certimaster.commonexception.data;

import com.certimaster.commonexception.business.BaseException;

/**
 * Exception for optimistic locking failures
 */
public class OptimisticLockException extends BaseException {

    public OptimisticLockException(String entity, Long id) {
        super("OPTIMISTIC_LOCK_FAILURE",
                String.format("The %s with id %d has been modified by another user", entity, id));
        addDetail("entity", entity);
        addDetail("id", id);
    }

    public OptimisticLockException(String entity, Long id, Long currentVersion, Long expectedVersion) {
        super("OPTIMISTIC_LOCK_FAILURE",
                String.format("The %s with id %d has been modified. Expected version: %d, Current version: %d",
                        entity, id, expectedVersion, currentVersion));
        addDetail("entity", entity);
        addDetail("id", id);
        addDetail("expectedVersion", expectedVersion);
        addDetail("currentVersion", currentVersion);
    }
}
