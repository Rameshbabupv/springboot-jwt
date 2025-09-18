package com.systech.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development Security Configuration - No JWT authentication required.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for development without Keycloak
 *
 * For complete change history: git log --follow DevSecurityConfig.java
 *
 * Features:
 * - Allows application startup without Keycloak running
 * - Disables authentication for development and testing
 * - Permits all requests for easy development
 * - CORS configuration for React frontend
 *
 * Usage:
 * - Activated with 'dev-no-auth' profile: --spring.profiles.active=dev-no-auth
 * - For local development without authentication requirements
 * - Useful for testing business logic without JWT overhead
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
@Profile("dev-no-auth")
public class DevSecurityConfig {

    /**
     * Configure security filter chain for development (no authentication).
     *
     * @param http the HttpSecurity configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     * @since 1.0
     */
    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API development
            .csrf(csrf -> csrf.disable())

            // Allow all requests without authentication
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )

            // Allow H2 console frames
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}