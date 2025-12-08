package com.certimaster.exam_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable Spring scheduling for background tasks
 * such as exam session expiration checks.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
