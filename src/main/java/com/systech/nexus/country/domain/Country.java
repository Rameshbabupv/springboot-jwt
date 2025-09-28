package com.systech.nexus.country.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Country entity representing a country master record in the system.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-28) - Initial implementation with non-null columns from country_master table
 *
 * Database Details:
 * - Table: country_master (PostgreSQL nx_core schema)
 * - Primary Key: country_id (sequence-generated from global_id_seq)
 * - Unique Constraints: country_code (2-char), country_code_3 (3-char)
 * - Check Constraints: country_code format [A-Z]{2}, country_code_3 format [A-Z]{3}
 *
 * Phase 1 Implementation:
 * - Contains only non-null columns from actual database table
 * - User input fields: country_code, country_code_3, country_name
 * - Database defaults: is_active (true), created_at, modified_at (CURRENT_TIMESTAMP)
 * - Referenced by: company_master (registered_country_id, communication_country_id)
 *
 * Business Rules:
 * - Country code must be 2-character uppercase (ISO 3166-1 alpha-2)
 * - Country code 3 must be 3-character uppercase (ISO 3166-1 alpha-3)
 * - Country name is required and must be unique
 * - Active status defaults to true
 * - Supports foreign key relationships with companies and locations
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "country_master", schema = "nx_core")
@EntityListeners(AuditingEntityListener.class)
public class Country {

    /**
     * Primary key for the country entity.
     * Maps to country_id column in the database.
     * Uses global sequence shared across master tables.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @SequenceGenerator(name = "global_seq", sequenceName = "nx_core.global_id_seq", allocationSize = 1)
    @Column(name = "country_id")
    private Long id;

    // === USER INPUT REQUIRED FIELDS ===

    /**
     * ISO 3166-1 alpha-2 country code (required user input).
     * Two-character uppercase country identifier (e.g., IN, US, GB).
     */
    @Column(name = "country_code", nullable = false, unique = true, length = 2)
    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
    private String countryCode;

    /**
     * ISO 3166-1 alpha-3 country code (required user input).
     * Three-character uppercase country identifier (e.g., IND, USA, GBR).
     */
    @Column(name = "country_code_3", nullable = false, unique = true, length = 3)
    @NotBlank(message = "Country code 3 is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Country code 3 must be 3 uppercase letters")
    private String countryCode3;

    /**
     * Country name (required user input).
     * Full name of the country for display purposes.
     */
    @Column(name = "country_name", nullable = false, length = 100)
    @NotBlank(message = "Country name is required")
    @Size(max = 100, message = "Country name must not exceed 100 characters")
    private String countryName;

    // === FIELDS WITH DATABASE DEFAULTS ===

    /**
     * Active status (has database default: true).
     * Whether the country is active in the system.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // === AUDIT FIELDS (AUTO-POPULATED) ===

    /**
     * Timestamp when the country record was created (auto-populated).
     * Managed by database triggers.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the country record was last modified (auto-populated).
     * Managed by database triggers.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    // === CONSTRUCTORS ===

    /**
     * Default no-args constructor required by JPA.
     */
    public Country() {}

    /**
     * Constructor for creating a new country with required user input fields.
     * Active status will use database default (true).
     *
     * @param countryCode   the 2-character ISO country code
     * @param countryCode3  the 3-character ISO country code
     * @param countryName   the full country name
     */
    public Country(String countryCode, String countryCode3, String countryName) {
        this.countryCode = countryCode;
        this.countryCode3 = countryCode3;
        this.countryName = countryName;
        this.isActive = true; // Default value
    }

    // === GETTERS AND SETTERS ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryCode3() { return countryCode3; }
    public void setCountryCode3(String countryCode3) { this.countryCode3 = countryCode3; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    // === BUSINESS METHODS ===

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Updates country information from another country object.
     * Only updates non-null fields from the source.
     * Does not update audit fields or ID.
     *
     * @param updateData Country object containing fields to update
     */
    public void updateFrom(Country updateData) {
        if (updateData.getCountryCode() != null) {
            this.countryCode = updateData.getCountryCode();
        }
        if (updateData.getCountryCode3() != null) {
            this.countryCode3 = updateData.getCountryCode3();
        }
        if (updateData.getCountryName() != null) {
            this.countryName = updateData.getCountryName();
        }
        if (updateData.getIsActive() != null) {
            this.isActive = updateData.getIsActive();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(id, country.id) &&
               Objects.equals(countryCode, country.countryCode) &&
               Objects.equals(countryCode3, country.countryCode3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryCode, countryCode3);
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", countryCode3='" + countryCode3 + '\'' +
                ", countryName='" + countryName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}