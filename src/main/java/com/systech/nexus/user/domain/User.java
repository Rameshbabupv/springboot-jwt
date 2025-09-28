package com.systech.nexus.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing a user in the system.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-27) - Initial minimal implementation matching actual PostgreSQL database structure
 *
 * Database Details:
 * - Table: user_master (PostgreSQL nx_core schema)
 * - Primary Key: user_id (sequence-generated)
 * - Foreign Key: company_id references company_master
 * - Unique Constraints: (company_id, username), (company_id, email_address)
 *
 * Business Rules:
 * - Users belong to a specific company (company_id required)
 * - Username and email must be unique within company scope
 * - First name and last name are required
 * - User status defaults to ACTIVE
 * - Preferred language defaults to 'en'
 *
 * Phase 1 Implementation:
 * - Contains only minimum critical fields to get tests passing
 * - Advanced features (MFA, preferences, security) to be added in Phase 2
 *
 * @author Backend Developer
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "user_master", schema = "nx_core")
@EntityListeners(AuditingEntityListener.class)
public class User {

    /**
     * Primary key for the user entity.
     * Maps to user_id column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "nx_core.user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;

    /**
     * Company that this user belongs to (required).
     * Foreign key reference to company_master table.
     */
    @Column(name = "company_id", nullable = false)
    @NotNull(message = "Company ID is required")
    private Long companyId;

    /**
     * Unique username within company scope (required).
     * Used for user login and identification.
     */
    @Column(name = "username", nullable = false, length = 50)
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    /**
     * User's email address (required).
     * Must be unique within company scope.
     */
    @Column(name = "email_address", nullable = false, length = 150)
    @NotBlank(message = "Email address is required")
    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String emailAddress;

    /**
     * User's first name (required).
     */
    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    /**
     * User's last name (required).
     */
    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    /**
     * User's preferred language (required).
     * Defaults to 'en' (English).
     */
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage = "en";

    /**
     * Current status of the user (required).
     * Uses enum values that match database constraints.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 20)
    private UserStatus userStatus = UserStatus.ACTIVE;

    /**
     * Audit field: Timestamp when user was created (required).
     * Automatically managed by database.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Audit field: Timestamp when user was last modified (required).
     * Automatically managed by database.
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Enum for user status values matching database constraints.
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, LOCKED, SUSPENDED, PENDING_ACTIVATION
    }

    /**
     * Default no-args constructor required by JPA.
     */
    public User() {}

    /**
     * Constructor for creating a new user with required fields.
     *
     * @param companyId     the company this user belongs to
     * @param username      the unique username
     * @param emailAddress  the user's email address
     * @param firstName     the user's first name
     * @param lastName      the user's last name
     */
    public User(Long companyId, String username, String emailAddress, String firstName, String lastName) {
        this.companyId = companyId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredLanguage = "en";
        this.userStatus = UserStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    // Legacy compatibility methods (for backward compatibility with existing code)
    public String getEmail() { return emailAddress; }
    public void setEmail(String email) { this.emailAddress = email; }

    public LocalDateTime getUpdatedAt() { return modifiedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.modifiedAt = updatedAt; }

    // Business Methods
    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE;
    }

    public void activate() {
        this.userStatus = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
    }

    public void lock() {
        this.userStatus = UserStatus.LOCKED;
    }

    public void suspend() {
        this.userStatus = UserStatus.SUSPENDED;
    }

    /**
     * Business method to get the user's full display name.
     *
     * @return the full name in "firstName lastName" format
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Performs a partial update of user data.
     * Only updates fields that are non-null in the updateData parameter.
     *
     * @param updateData User object containing the fields to update
     */
    public void updateFrom(User updateData) {
        if (updateData.getUsername() != null) {
            this.username = updateData.getUsername();
        }
        if (updateData.getEmailAddress() != null) {
            this.emailAddress = updateData.getEmailAddress();
        }
        if (updateData.getFirstName() != null) {
            this.firstName = updateData.getFirstName();
        }
        if (updateData.getLastName() != null) {
            this.lastName = updateData.getLastName();
        }
        if (updateData.getPreferredLanguage() != null) {
            this.preferredLanguage = updateData.getPreferredLanguage();
        }
        if (updateData.getUserStatus() != null) {
            this.userStatus = updateData.getUserStatus();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(companyId, user.companyId) &&
               Objects.equals(username, user.username) &&
               Objects.equals(emailAddress, user.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, username, emailAddress);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userStatus=" + userStatus +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}