package com.systech.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/**
 * JPA Auditing Configuration for automatic audit field population.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial implementation with JWT-based auditor extraction
 *
 * Features:
 * - Enables JPA auditing for @CreatedBy and @LastModifiedBy annotations
 * - Extracts current user from JWT token in security context
 * - Handles unauthenticated scenarios gracefully with system defaults
 * - Supports Keycloak JWT token structure (preferred_username claim)
 * - Integration with Spring Security OAuth2 resource server
 *
 * Audit Strategy:
 * - Uses preferred_username from JWT token as the auditor
 * - Falls back to "system" for unauthenticated operations
 * - Automatically populates createdBy and modifiedBy fields
 * - Works seamlessly with @EntityListeners(AuditingEntityListener.class)
 *
 * Security Integration:
 * - Integrates with existing Spring Security configuration
 * - Reads from SecurityContextHolder for current authentication
 * - Supports both authenticated and system operations
 * - Compatible with Keycloak JWT token structure
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provides the current auditor for JPA auditing operations.
     * Extracts the username from the JWT token in the security context.
     *
     * @return AuditorAware implementation that returns the current user
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Implementation of AuditorAware that extracts the current user from Spring Security context.
     * Specifically designed to work with JWT tokens from Keycloak authentication.
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {

        /**
         * Returns the current auditor (username) from the security context.
         *
         * Extraction Strategy:
         * 1. Get authentication from SecurityContextHolder
         * 2. Extract JWT token from OAuth2 authentication
         * 3. Read preferred_username claim from JWT
         * 4. Fall back to "system" if no authentication or username available
         *
         * @return Optional containing the current username or "system" as fallback
         */
        @Override
        public Optional<String> getCurrentAuditor() {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                    // No authentication - use system user
                    return Optional.of("system");
                }

                // Handle JWT token from OAuth2 resource server
                if (authentication.getPrincipal() instanceof Jwt) {
                    Jwt jwt = (Jwt) authentication.getPrincipal();

                    // Extract preferred_username claim (Keycloak standard)
                    String username = jwt.getClaimAsString("preferred_username");
                    if (username != null && !username.trim().isEmpty()) {
                        return Optional.of(username.trim());
                    }

                    // Fall back to subject if preferred_username not available
                    String subject = jwt.getSubject();
                    if (subject != null && !subject.trim().isEmpty()) {
                        return Optional.of(subject.trim());
                    }
                }

                // Fall back to authentication name for other authentication types
                String name = authentication.getName();
                if (name != null && !name.trim().isEmpty() && !"anonymousUser".equals(name)) {
                    return Optional.of(name.trim());
                }

                // Default fallback
                return Optional.of("system");

            } catch (Exception e) {
                // Log error and fall back to system user
                // In production, consider using a logger here
                System.err.println("Error extracting current auditor: " + e.getMessage());
                return Optional.of("system");
            }
        }
    }
}