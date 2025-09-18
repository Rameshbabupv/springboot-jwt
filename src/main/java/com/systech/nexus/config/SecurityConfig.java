package com.systech.nexus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security Configuration for Nexus Application with JWT Authentication.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation with Keycloak JWT integration
 *
 * For complete change history: git log --follow SecurityConfig.java
 *
 * Features:
 * - JWT token validation using Keycloak public keys
 * - Role-based access control with nexus realm roles
 * - CORS configuration for React frontend integration
 * - Stateless session management
 * - Method-level security annotations support
 *
 * Single Client Architecture:
 * - React frontend uses nexus-web-app client for authentication
 * - Spring Boot validates JWT tokens from nexus-web-app client
 * - No client secret needed for token validation (uses public keys)
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile("!dev-no-auth")
public class SecurityConfig {

    @Value("${nexus.security.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private List<String> allowedOrigins;

    @Value("${nexus.security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private List<String> allowedMethods;

    @Value("${nexus.security.cors.allowed-headers:Authorization,Content-Type,X-Requested-With}")
    private List<String> allowedHeaders;

    /**
     * Configure the main security filter chain.
     *
     * @param http the HttpSecurity configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     * @since 1.0
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS for React frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF (using JWT tokens)
            .csrf(csrf -> csrf.disable())

            // Stateless session (JWT-based)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/graphiql/**").permitAll() // GraphQL IDE for development
                .requestMatchers("/h2-console/**").permitAll() // H2 database console

                // Role-based access control
                .requestMatchers("/api/admin/**").hasRole("nexus-admin")
                .requestMatchers("/api/manager/**").hasAnyRole("nexus-admin", "nexus-manager")
                .requestMatchers("/api/user/**").hasAnyRole("nexus-admin", "nexus-manager", "nexus-user")

                // GraphQL endpoint (requires authentication)
                .requestMatchers("/graphql").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Configure JWT token validation
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )

            // Allow H2 console frames
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Configure JWT to Spring Security roles conversion.
     * Extracts roles from Keycloak JWT token.
     *
     * @return the configured JwtAuthenticationConverter
     * @since 1.0
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Configure role extraction from JWT
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }

    /**
     * CORS configuration for React frontend.
     *
     * @return the configured CorsConfigurationSource
     * @since 1.0
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configure allowed origins (React frontend URLs)
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}