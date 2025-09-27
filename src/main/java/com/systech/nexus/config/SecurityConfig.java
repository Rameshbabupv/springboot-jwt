package com.systech.nexus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Security Configuration for Nexus Application with JWT Authentication.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation with Keycloak JWT integration
 * v1.1 (2025-09-18) - Made CORS configuration externalized and configurable
 *
 * For complete change history: git log --follow SecurityConfig.java
 *
 * Features:
 * - JWT token validation using Keycloak public keys
 * - Role-based access control with nexus realm roles
 * - Externalized CORS configuration for React frontend integration
 * - Stateless session management
 * - Method-level security annotations support
 *
 * Single Client Architecture:
 * - React frontend uses systech-hrms-client for authentication
 * - Spring Boot validates JWT tokens from systech-hrms-client
 * - No client secret needed for token validation (uses public keys)
 *
 * @author Claude
 * @version 1.1
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile("!dev-no-auth")
public class SecurityConfig {

    private final CorsProperties corsProperties;

    @Autowired
    public SecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

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

                // Group-based access control (Keycloak groups)
                .requestMatchers("/api/admin/**").hasRole("platform-admins")
                .requestMatchers("/api/manager/**").hasAnyRole("platform-admins", "app-admins")
                .requestMatchers("/api/user/**").hasAnyRole("platform-admins", "app-admins", "users")

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
     * Extracts roles from Keycloak JWT token with custom realm_access handling.
     *
     * @return the configured JwtAuthenticationConverter
     * @since 1.0
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Custom authorities converter for Keycloak groups
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Extract groups claim
            @SuppressWarnings("unchecked")
            List<String> groups = jwt.getClaimAsStringList("groups");
            if (groups != null) {
                for (String group : groups) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + group));
                }
            }

            // Also extract realm roles for backward compatibility
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            return authorities;
        });

        return converter;
    }

    /**
     * CORS configuration for React frontend using externalized properties.
     *
     * @return the configured CorsConfigurationSource
     * @since 1.0
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configure allowed origins from properties
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setAllowCredentials(true);

        // Use max-age from properties or default to 3600 seconds
        Long maxAge = corsProperties.getMaxAge() != null ? corsProperties.getMaxAge() : 3600L;
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}