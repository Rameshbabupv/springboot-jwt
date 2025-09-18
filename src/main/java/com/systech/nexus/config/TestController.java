package com.systech.nexus.config;

import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test Controller for JWT authentication validation and testing.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for JWT testing and validation
 *
 * For complete change history: git log --follow TestController.java
 *
 * Features:
 * - Public endpoints for health checking without authentication
 * - Protected endpoints for testing JWT validation
 * - Role-based access control testing endpoints
 * - JWT token information extraction and display
 * - Comprehensive security testing coverage
 *
 * Test Endpoints:
 * - Public: /api/test/public - No authentication required
 * - User: /api/test/user - Any authenticated user (nexus-user+)
 * - Manager: /api/test/manager - Manager or admin only (nexus-manager+)
 * - Admin: /api/test/admin - Admin only (nexus-admin)
 *
 * Use this controller to verify JWT integration is working correctly
 * before testing actual business endpoints.
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Public endpoint - no authentication required.
     * Use this to verify the application is running.
     *
     * @return status information
     * @since 1.0
     */
    @GetMapping("/public")
    @Loggable(description = "Test endpoint: public access")
    public Map<String, Object> publicEndpoint() {
        return Map.of(
            "status", "OK",
            "message", "Public endpoint - no authentication required",
            "timestamp", System.currentTimeMillis(),
            "authRequired", false
        );
    }

    /**
     * User level endpoint - requires any authenticated user.
     * Tests basic JWT validation.
     *
     * @return user information from JWT
     * @since 1.0
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Test endpoint: user level access")
    public Map<String, Object> userEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "User endpoint - authentication required");
        response.put("userId", jwtTokenUtil.getCurrentUserId() != null ? jwtTokenUtil.getCurrentUserId() : "unknown");
        response.put("username", jwtTokenUtil.getCurrentUsername() != null ? jwtTokenUtil.getCurrentUsername() : "unknown");
        response.put("email", jwtTokenUtil.getCurrentUserEmail() != null ? jwtTokenUtil.getCurrentUserEmail() : "unknown");
        response.put("fullName", jwtTokenUtil.getCurrentUserFullName() != null ? jwtTokenUtil.getCurrentUserFullName() : "unknown");
        response.put("roles", jwtTokenUtil.getCurrentUserRoles());
        response.put("timestamp", System.currentTimeMillis());
        response.put("authRequired", true);
        response.put("minRole", "nexus-user");
        return response;
    }

    /**
     * Manager level endpoint - requires manager or admin role.
     * Tests role-based access control.
     *
     * @return manager-specific information
     * @since 1.0
     */
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")
    @Loggable(description = "Test endpoint: manager level access")
    public Map<String, Object> managerEndpoint() {
        List<String> userRoles = jwtTokenUtil.getCurrentUserRoles();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Manager endpoint - elevated privileges required");
        response.put("username", jwtTokenUtil.getCurrentUsername() != null ? jwtTokenUtil.getCurrentUsername() : "unknown");
        response.put("roles", userRoles);
        response.put("isManager", jwtTokenUtil.hasRole("nexus-manager"));
        response.put("isAdmin", jwtTokenUtil.isAdmin());
        response.put("isManagerOrAdmin", jwtTokenUtil.isManagerOrAdmin());
        response.put("timestamp", System.currentTimeMillis());
        response.put("authRequired", true);
        response.put("minRole", "nexus-manager");
        return response;
    }

    /**
     * Admin level endpoint - requires admin role only.
     * Tests highest level access control.
     *
     * @return admin-specific information
     * @since 1.0
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('nexus-admin')")
    @Loggable(description = "Test endpoint: admin level access")
    public Map<String, Object> adminEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Admin endpoint - full system access");
        response.put("username", jwtTokenUtil.getCurrentUsername() != null ? jwtTokenUtil.getCurrentUsername() : "unknown");
        response.put("userId", jwtTokenUtil.getCurrentUserId() != null ? jwtTokenUtil.getCurrentUserId() : "unknown");
        response.put("roles", jwtTokenUtil.getCurrentUserRoles());
        response.put("isAdmin", jwtTokenUtil.isAdmin());
        response.put("hasUserRole", jwtTokenUtil.hasRole("nexus-user"));
        response.put("hasManagerRole", jwtTokenUtil.hasRole("nexus-manager"));
        response.put("hasAdminRole", jwtTokenUtil.hasRole("nexus-admin"));
        response.put("timestamp", System.currentTimeMillis());
        response.put("authRequired", true);
        response.put("minRole", "nexus-admin");
        return response;
    }

    /**
     * JWT token details endpoint - requires any authenticated user.
     * Provides detailed JWT token information for debugging.
     *
     * @return detailed JWT token information
     * @since 1.0
     */
    @GetMapping("/token-info")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Test endpoint: JWT token information")
    public Map<String, Object> tokenInfo() {
        var jwt = jwtTokenUtil.getCurrentJwtToken();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "JWT Token Information");
        response.put("tokenPresent", jwt != null);
        response.put("issuer", jwt != null ? jwt.getClaimAsString("iss") : "N/A");
        response.put("subject", jwt != null ? jwt.getClaimAsString("sub") : "N/A");
        response.put("audience", jwt != null ? jwt.getClaimAsString("aud") : "N/A");
        response.put("issuedAt", jwt != null ? jwt.getClaimAsString("iat") : "N/A");
        response.put("expiresAt", jwt != null ? jwt.getClaimAsString("exp") : "N/A");
        response.put("username", jwtTokenUtil.getCurrentUsername());
        response.put("email", jwtTokenUtil.getCurrentUserEmail());
        response.put("roles", jwtTokenUtil.getCurrentUserRoles());
        response.put("customClaim_name", jwtTokenUtil.getCustomClaim("name"));
        response.put("customClaim_given_name", jwtTokenUtil.getCustomClaim("given_name"));
        response.put("customClaim_family_name", jwtTokenUtil.getCustomClaim("family_name"));
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * Role checking endpoint - requires any authenticated user.
     * Tests various role checking utility methods.
     *
     * @return role checking results
     * @since 1.0
     */
    @GetMapping("/role-check")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Test endpoint: role checking utilities")
    public Map<String, Object> roleCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Role Checking Results");
        response.put("username", jwtTokenUtil.getCurrentUsername());
        response.put("allRoles", jwtTokenUtil.getCurrentUserRoles());
        response.put("hasUserRole", jwtTokenUtil.hasRole("nexus-user"));
        response.put("hasManagerRole", jwtTokenUtil.hasRole("nexus-manager"));
        response.put("hasAdminRole", jwtTokenUtil.hasRole("nexus-admin"));
        response.put("isAdmin", jwtTokenUtil.isAdmin());
        response.put("isManagerOrAdmin", jwtTokenUtil.isManagerOrAdmin());
        response.put("hasAnyUserRole", jwtTokenUtil.hasAnyRole("nexus-user", "nexus-manager"));
        response.put("hasAnyManagerRole", jwtTokenUtil.hasAnyRole("nexus-manager", "nexus-admin"));
        response.put("hasInvalidRole", jwtTokenUtil.hasRole("invalid-role"));
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}