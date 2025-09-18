package com.systech.nexus.config;

import com.systech.nexus.common.util.JwtTokenUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Security Configuration for Unit Tests.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for test support
 * v1.1 (2025-09-18) - Added test security filter chain to disable security
 *
 * For complete change history: git log --follow TestSecurityConfig.java
 *
 * Features:
 * - Provides mock JwtTokenUtil for tests
 * - Disables security for @WebMvcTest scenarios
 * - Allows public endpoints to work in test environment
 * - Prevents dependency injection failures in unit tests
 *
 * @author Claude
 * @version 1.1
 * @since 1.0
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Test security filter chain that permits all requests.
     * This overrides the main SecurityConfig for test scenarios.
     *
     * @param http the HttpSecurity configuration
     * @return the configured SecurityFilterChain for tests
     * @throws Exception if configuration fails
     * @since 1.1
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        return http.build();
    }

    /**
     * Mock JwtTokenUtil for testing.
     * Provides default values for test scenarios.
     *
     * @return mocked JwtTokenUtil
     * @since 1.0
     */
    @Bean
    @Primary
    public JwtTokenUtil jwtTokenUtil() {
        JwtTokenUtil mockUtil = mock(JwtTokenUtil.class);

        // Set up default mock behavior
        when(mockUtil.getCurrentUsername()).thenReturn("test-user");
        when(mockUtil.getCurrentUserId()).thenReturn("test-user-id");
        when(mockUtil.getCurrentUserEmail()).thenReturn("test@example.com");
        when(mockUtil.getCurrentUserRoles()).thenReturn(java.util.List.of("nexus-user"));
        when(mockUtil.hasRole("nexus-user")).thenReturn(true);
        when(mockUtil.hasRole("nexus-manager")).thenReturn(false);
        when(mockUtil.hasRole("nexus-admin")).thenReturn(false);
        when(mockUtil.isManagerOrAdmin()).thenReturn(false);
        when(mockUtil.isAdmin()).thenReturn(false);

        return mockUtil;
    }
}