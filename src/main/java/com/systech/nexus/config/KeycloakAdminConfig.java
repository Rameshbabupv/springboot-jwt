package com.systech.nexus.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Keycloak Admin Client Configuration.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for user management
 *
 * For complete change history: git log --follow KeycloakAdminConfig.java
 *
 * Features:
 * - Admin client for programmatic user creation
 * - Configured for systech realm
 * - Uses admin credentials for management operations
 *
 * Usage:
 * - Only active in dev profile for development setup
 * - Allows Spring Boot to create test users automatically
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Configuration
@Profile({"dev", "test"})
public class KeycloakAdminConfig {

    @Value("${keycloak.admin.server-url:http://localhost:8090}")
    private String serverUrl;

    @Value("${keycloak.admin.realm:systech}")
    private String realm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin.username:admin}")
    private String username;

    @Value("${keycloak.admin.password:admin}")
    private String password;

    /**
     * Create Keycloak admin client for programmatic user management.
     *
     * @return configured Keycloak admin client
     * @since 1.0
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // Admin operations use master realm
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Get the configured realm name.
     *
     * @return the realm name
     * @since 1.0
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Get a new Keycloak admin client instance.
     *
     * @return new Keycloak admin client
     * @since 1.0
     */
    public Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // Admin operations use master realm
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }
}