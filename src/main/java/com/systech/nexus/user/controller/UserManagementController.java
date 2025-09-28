package com.systech.nexus.user.controller;

import com.systech.nexus.user.service.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for User Management Operations.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for user management
 *
 * For complete change history: git log --follow UserManagementController.java
 *
 * Features:
 * - Create test users via REST API
 * - Check if users exist
 * - Development-only endpoints
 *
 * Usage:
 * - POST /api/admin/users/create-test-users - Create all test users
 * - GET /api/admin/users/{username}/exists - Check if user exists
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/users")
@Profile("dev")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private KeycloakUserService keycloakUserService;

    /**
     * Create all test users in Keycloak.
     *
     * @return success response
     * @since 1.0
     */
    @PostMapping("/create-test-users")
    @PreAuthorize("hasRole('nexus-admin')")
    public ResponseEntity<Map<String, Object>> createTestUsers() {
        try {
            keycloakUserService.createTestUsers();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test users created successfully",
                "users", new String[]{"nexus-user", "nexus-manager", "nexus-admin"}
            ));
        } catch (Exception e) {
            logger.error("Error creating test users: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to create test users: " + e.getMessage()
            ));
        }
    }

    /**
     * Check if a user exists in Keycloak.
     *
     * @param username the username to check
     * @return existence status
     * @since 1.0
     */
    @GetMapping("/{username}/exists")
    @PreAuthorize("hasAnyRole('nexus-admin', 'nexus-manager')")
    public ResponseEntity<Map<String, Object>> userExists(@PathVariable String username) {
        try {
            boolean exists = keycloakUserService.userExists(username);
            return ResponseEntity.ok(Map.of(
                "username", username,
                "exists", exists
            ));
        } catch (Exception e) {
            logger.error("Error checking user existence: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to check user existence: " + e.getMessage()
            ));
        }
    }

    /**
     * Create a single user with specified details.
     *
     * @param userRequest the user creation request
     * @return creation result
     * @since 1.0
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('nexus-admin')")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody CreateUserRequest userRequest) {
        try {
            boolean success = keycloakUserService.createUser(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPassword(),
                userRequest.getRoles().toArray(new String[0])
            );

            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User created successfully",
                    "username", userRequest.getUsername()
                ));
            } else {
                return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "Failed to create user"
                ));
            }
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to create user: " + e.getMessage()
            ));
        }
    }

    /**
     * Request DTO for user creation.
     */
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private java.util.List<String> roles;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public java.util.List<String> getRoles() { return roles; }
        public void setRoles(java.util.List<String> roles) { this.roles = roles; }
    }
}