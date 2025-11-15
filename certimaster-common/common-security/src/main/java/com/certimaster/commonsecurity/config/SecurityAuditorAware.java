package com.certimaster.commonsecurity.config;

import com.certimaster.commonsecurity.util.SecurityContextUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor aware implementation for JPA auditing
 * Automatically sets createdBy and updatedBy fields
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return SecurityContextUtil.getCurrentUsername()
                .or(() -> Optional.of("SYSTEM"));
    }
}
