package com.certimaster.commonsecurity.annotation;

import com.certimaster.commonexception.business.ForbiddenException;
import com.certimaster.commonsecurity.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for handling @RequireRole annotation
 */
@Slf4j
@Aspect
@Component
public class RoleAspect {

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole)
            throws Throwable {

        String[] roles = requireRole.value();
        boolean requireAll = requireRole.requireAll();

        boolean hasAccess;
        if (requireAll) {
            hasAccess = SecurityContextUtil.hasAllRoles(roles);
        } else {
            hasAccess = SecurityContextUtil.hasAnyRole(roles);
        }

        if (!hasAccess) {
            log.warn("Access denied. Required roles: {}, User has: {}",
                    roles, SecurityContextUtil.getCurrentAuthorities());
            throw ForbiddenException.insufficientPermissions();
        }

        return joinPoint.proceed();
    }
}
