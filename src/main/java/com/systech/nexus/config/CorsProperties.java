package com.systech.nexus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for CORS (Cross-Origin Resource Sharing) settings.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-18) - Initial implementation for configurable CORS
 *
 * For complete change history: git log --follow CorsProperties.java
 *
 * Features:
 * - Externalized CORS configuration from application properties
 * - Support for multiple allowed origins, methods, and headers
 * - Configurable max-age for preflight requests
 * - Environment-specific CORS settings
 *
 * Usage:
 * Configure in application.yml under nexus.security.cors:
 * - allowed-origins: List of allowed origin URLs
 * - allowed-methods: List of allowed HTTP methods
 * - allowed-headers: List of allowed request headers
 * - max-age: Cache duration for preflight requests in seconds
 *
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "nexus.security.cors")
public class CorsProperties {

    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private Long maxAge;

    /**
     * Get the list of allowed origins for CORS requests.
     *
     * @return list of allowed origin URLs
     * @since 1.0
     */
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Set the list of allowed origins for CORS requests.
     *
     * @param allowedOrigins list of allowed origin URLs
     * @since 1.0
     */
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Get the list of allowed HTTP methods for CORS requests.
     *
     * @return list of allowed HTTP methods
     * @since 1.0
     */
    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Set the list of allowed HTTP methods for CORS requests.
     *
     * @param allowedMethods list of allowed HTTP methods
     * @since 1.0
     */
    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * Get the list of allowed headers for CORS requests.
     *
     * @return list of allowed request headers
     * @since 1.0
     */
    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * Set the list of allowed headers for CORS requests.
     *
     * @param allowedHeaders list of allowed request headers
     * @since 1.0
     */
    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    /**
     * Get the max age for preflight request caching.
     *
     * @return max age in seconds
     * @since 1.0
     */
    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Set the max age for preflight request caching.
     *
     * @param maxAge max age in seconds
     * @since 1.0
     */
    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }
}