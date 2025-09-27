package com.systech.nexus.user.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service for managing Keycloak users programmatically.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for user management
 *
 * For complete change history: git log --follow KeycloakUserService.java
 *
 * Features:
 * - Create users with roles programmatically
 * - Assign realm roles to users
 * - Check if users exist
 * - Set user passwords
 *
 * Usage:
 * - Development setup for creating test users
 * - Can be extended for user registration features
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Service
@Profile("dev")
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    @Autowired
    private Keycloak keycloakAdminClient;

    @Value("${keycloak.admin.realm:nexus-dev}")
    private String realm;

    /**
     * Create a user with specified roles in Keycloak.
     *
     * @param username the username
     * @param email the user's email
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @param roles the roles to assign to the user
     * @return true if user created successfully, false otherwise
     * @since 1.0
     */
    public boolean createUser(String username, String email, String firstName,
                             String lastName, String password, String... roles) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Check if user already exists
            List<UserRepresentation> existingUsers = usersResource.search(username);
            if (!existingUsers.isEmpty()) {
                logger.info("User {} already exists", username);
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

            // Create user
            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = extractUserIdFromLocation(response.getLocation().toString());
                logger.info("User {} created successfully with ID: {}", username, userId);

                // Set password
                setUserPassword(userId, password);

                // Assign roles
                assignRolesToUser(userId, roles);

                return true;
            } else {
                logger.error("Failed to create user {}. Status: {}", username, response.getStatus());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error creating user {}: {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Set password for a user.
     *
     * @param userId the user ID
     * @param password the password to set
     * @since 1.0
     */
    private void setUserPassword(String userId, String password) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            userResource.resetPassword(credential);
            logger.info("Password set for user ID: {}", userId);

        } catch (Exception e) {
            logger.error("Error setting password for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Assign realm roles to a user.
     *
     * @param userId the user ID
     * @param roleNames the role names to assign
     * @since 1.0
     */
    private void assignRolesToUser(String userId, String... roleNames) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            RoleMappingResource roleMappingResource = userResource.roles();

            for (String roleName : roleNames) {
                RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
                roleMappingResource.realmLevel().add(Collections.singletonList(role));
                logger.info("Assigned role {} to user ID: {}", roleName, userId);
            }

        } catch (Exception e) {
            logger.error("Error assigning roles to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Extract user ID from the location header.
     *
     * @param location the location URL
     * @return the user ID
     * @since 1.0
     */
    private String extractUserIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    /**
     * Create all test users for development.
     *
     * @since 1.0
     */
    public void createTestUsers() {
        logger.info("Creating test users for development...");

        // Create nexus-user
        createUser("nexus-user", "user@nexus.systech.com", "Nexus", "User",
                  "nexus123", "nexus-user");

        // Create nexus-manager
        createUser("nexus-manager", "manager@nexus.systech.com", "Nexus", "Manager",
                  "nexus123", "nexus-manager");

        // Create nexus-admin
        createUser("nexus-admin", "admin@nexus.systech.com", "Nexus", "Admin",
                  "nexus123", "nexus-admin");

        logger.info("Test users creation completed");
    }

    /**
     * Check if user exists in Keycloak.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     * @since 1.0
     */
    public boolean userExists(String username) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username);
            return !users.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if user exists {}: {}", username, e.getMessage());
            return false;
        }
    }
}