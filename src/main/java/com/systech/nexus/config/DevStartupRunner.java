package com.systech.nexus.config;

import com.systech.nexus.user.service.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Development Startup Runner for Keycloak User Setup.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for dev user setup
 *
 * For complete change history: git log --follow DevStartupRunner.java
 *
 * Features:
 * - Automatically creates test users on application startup
 * - Only runs in development profile
 * - Creates nexus-user, nexus-manager, nexus-admin users
 *
 * Usage:
 * - Run with dev profile: --spring.profiles.active=dev
 * - Users will be created if they don't exist
 * - Check logs for creation status
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Component
@Profile("dev")
public class DevStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevStartupRunner.class);

    @Autowired
    private KeycloakUserService keycloakUserService;

    /**
     * Run after Spring Boot application startup.
     * Creates test users in Keycloak for development.
     *
     * @param args command line arguments
     * @since 1.0
     */
    @Override
    public void run(String... args) {
        logger.info("Development mode: Setting up Keycloak test users...");

        try {
            // Wait a bit for Keycloak to be ready
            Thread.sleep(2000);

            // Create test users
            keycloakUserService.createTestUsers();

            logger.info("Keycloak user setup completed successfully");

        } catch (Exception e) {
            logger.warn("Could not create Keycloak users (this is normal if Keycloak is not running): {}",
                       e.getMessage());
        }
    }
}