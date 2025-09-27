package com.systech.nexus.user.service;

import com.systech.nexus.config.KeycloakAdminConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service for managing users in Keycloak.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial implementation with user management operations
 *
 * For complete change history: git log --follow KeycloakUserService.java
 *
 * Features:
 * - Create and manage test users for development
 * - Check user existence in Keycloak
 * - Assign roles to users (nexus-admin, nexus-manager, nexus-user)
 * - Password management for test users
 * - Error handling and logging
 *
 * Test Users:
 * - nexus-user: Standard user with nexus-user role
 * - nexus-manager: Manager with nexus-manager and nexus-user roles
 * - nexus-admin: Administrator with all roles
 * - babu.systech: Default test user for systech realm
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    @Autowired
    private KeycloakAdminConfig keycloakConfig;

    /**
     * Create all test users for development.
     *
     * @return true if all users created successfully
     * @since 1.0
     */
    public boolean createTestUsers() {
        logger.info("Creating test users in Keycloak realm: {}", keycloakConfig.getRealm());

        boolean allSuccess = true;

        // Create standard test users
        allSuccess &= createUser("nexus-user", "nexus-user@example.com", "Nexus", "User", "nexus123", "nexus-user");
        allSuccess &= createUser("nexus-manager", "nexus-manager@example.com", "Nexus", "Manager", "nexus123", "nexus-manager", "nexus-user");
        allSuccess &= createUser("nexus-admin", "nexus-admin@example.com", "Nexus", "Admin", "nexus123", "nexus-admin", "nexus-manager", "nexus-user");

        // Create systech test user
        allSuccess &= createUser("babu.systech", "babu@systech.com", "Babu", "Systech", "nexus123", "nexus-admin", "nexus-manager", "nexus-user");

        logger.info("Test users created successfully");
        return allSuccess;
    }

    /**
     * Check if a user exists in Keycloak.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     * @since 1.0
     */
    public boolean userExists(String username) {
        try (Keycloak keycloak = keycloakConfig.getKeycloakAdminClient()) {
            RealmResource realmResource = keycloak.realm(keycloakConfig.getRealm());
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> users = usersResource.search(username, true);
            return !users.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if user exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create a user with specified details and roles.
     *
     * @param username the username
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     * @param password the password
     * @param roles the roles to assign
     * @return true if user created successfully
     * @since 1.0
     */
    public boolean createUser(String username, String email, String firstName, String lastName, String password, String... roles) {
        try (Keycloak keycloak = keycloakConfig.getKeycloakAdminClient()) {
            RealmResource realmResource = keycloak.realm(keycloakConfig.getRealm());
            UsersResource usersResource = realmResource.users();

            // Check if user already exists
            if (userExists(username)) {
                logger.info("User {} already exists, skipping creation", username);
                return true;
            }

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Create password credential
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            // Create user
            Response response = usersResource.create(user);
            if (response.getStatus() == 201) {
                logger.info("User {} created successfully", username);

                // Get the created user's ID
                String userId = extractUserIdFromLocation(response.getLocation().getPath());
                if (userId != null && roles.length > 0) {
                    assignRolesToUser(realmResource, userId, roles);
                }

                response.close();
                return true;
            } else {
                logger.error("Failed to create user {}. Status: {}", username, response.getStatus());
                response.close();
                return false;
            }
        } catch (Exception e) {
            logger.error("Error creating user {}: {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Assign roles to a user.
     *
     * @param realmResource the realm resource
     * @param userId the user ID
     * @param roleNames the role names to assign
     * @since 1.0
     */
    private void assignRolesToUser(RealmResource realmResource, String userId, String... roleNames) {
        try {
            UsersResource usersResource = realmResource.users();

            for (String roleName : roleNames) {
                try {
                    RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
                    usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
                    logger.debug("Assigned role {} to user {}", roleName, userId);
                } catch (Exception e) {
                    logger.warn("Failed to assign role {} to user {}: {}", roleName, userId, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error assigning roles to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Extract user ID from the location header.
     *
     * @param locationPath the location path from response
     * @return the user ID or null if not found
     * @since 1.0
     */
    private String extractUserIdFromLocation(String locationPath) {
        if (locationPath != null) {
            String[] parts = locationPath.split("/");
            return parts[parts.length - 1];
        }
        return null;
    }
}