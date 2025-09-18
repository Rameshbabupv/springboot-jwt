package com.systech.nexus.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Utility class for extracting information from JWT tokens.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation with Keycloak integration
 *
 * For complete change history: git log --follow JwtTokenUtil.java
 *
 * Features:
 * - Extract user information from JWT tokens (ID, username, email, name)
 * - Extract and validate roles from Keycloak realm_access claim
 * - Helper methods for role-based authorization checks
 * - Support for custom claims extraction
 * - Null-safe operations with proper fallbacks
 *
 * Single Client Architecture:
 * - All tokens come from nexus-web-app client
 * - Contains user information and roles from Keycloak
 * - Supports realm roles: nexus-admin, nexus-manager, nexus-user, nexus-viewer
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtTokenUtil {

    /**
     * Get the current authenticated user's ID (subject).
     *
     * @return the user ID from JWT subject claim, or null if not authenticated
     * @since 1.0
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("sub");
        }
        return null;
    }

    /**
     * Get the current authenticated user's username.
     *
     * @return the username from JWT preferred_username claim, or null if not authenticated
     * @since 1.0
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("preferred_username");
        }
        return null;
    }

    /**
     * Get the current authenticated user's email.
     *
     * @return the email from JWT email claim, or null if not authenticated
     * @since 1.0
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    /**
     * Get the current authenticated user's full name.
     *
     * @return the full name from JWT name claim, or null if not authenticated
     * @since 1.0
     */
    public String getCurrentUserFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("name");
        }
        return null;
    }

    /**
     * Get all realm roles for the current user.
     *
     * @return list of roles from realm_access.roles claim, or empty list if not authenticated
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                return (List<String>) realmAccess.get("roles");
            }
        }
        return List.of();
    }

    /**
     * Get a custom claim from the JWT token.
     *
     * @param claimName the name of the claim to extract
     * @return the claim value as string, or null if not found or not authenticated
     * @since 1.0
     */
    public String getCustomClaim(String claimName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString(claimName);
        }
        return null;
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param role the role name to check (without ROLE_ prefix)
     * @return true if user has the role, false otherwise
     * @since 1.0
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Check if the current user has any of the specified roles.
     *
     * @param roles the role names to check (without ROLE_ prefix)
     * @return true if user has any of the roles, false otherwise
     * @since 1.0
     */
    public boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current user is an admin.
     *
     * @return true if user has nexus-admin role, false otherwise
     * @since 1.0
     */
    public boolean isAdmin() {
        return hasRole("nexus-admin");
    }

    /**
     * Check if the current user is a manager or admin.
     *
     * @return true if user has nexus-manager or nexus-admin role, false otherwise
     * @since 1.0
     */
    public boolean isManagerOrAdmin() {
        return hasAnyRole("nexus-manager", "nexus-admin");
    }

    /**
     * Get the full JWT token (for debugging purposes).
     *
     * @return the complete JWT token, or null if not authenticated
     * @since 1.0
     */
    public Jwt getCurrentJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        return null;
    }
}