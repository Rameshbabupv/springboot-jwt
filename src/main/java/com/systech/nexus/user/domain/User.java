package com.systech.nexus.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing a user in the system.
 * This entity is mapped to the 'users' table in the database.
 *
 * Features:
 * - Unique constraints on username and email
 * - Automatic timestamp management for created/updated times
 * - Validation annotations for data integrity
 * - Support for partial updates via updateFrom() method
 * - Business methods for common operations
 *
 * @author Claude Code Assistant
 * @version 1.0
 */
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
public class User {

    /**
     * Primary key for the user entity.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for the user.
     * Must be between 3-50 characters and is case-sensitive.
     * Used for user identification and login.
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * User's email address.
     * Must be unique and valid email format.
     * Case-insensitive for uniqueness checking.
     */
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    /**
     * User's first name.
     * Optional field, max 50 characters.
     */
    @Column(length = 50)
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Optional field, max 50 characters.
     */
    @Column(length = 50)
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    /**
     * Timestamp when the user record was created.
     * Automatically set by Hibernate on entity creation.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user record was last updated.
     * Automatically updated by Hibernate on entity modification.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default no-args constructor required by JPA.
     */
    public User() {}

    /**
     * Constructor for creating a new user with all required and optional fields.
     *
     * @param username  the unique username for the user
     * @param email     the user's email address
     * @param firstName the user's first name (optional)
     * @param lastName  the user's last name (optional)
     */
    public User(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Business method to get the user's full display name.
     * Falls back to username if name fields are not available.
     *
     * @return the full name in "firstName lastName" format, or username as fallback
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }

    /**
     * Performs a partial update of user data.
     * Only updates fields that are non-null in the updateData parameter.
     * This method is used for PATCH-style updates where only specific fields are modified.
     *
     * @param updateData User object containing the fields to update
     */
    public void updateFrom(User updateData) {
        if (updateData.getUsername() != null) {
            this.username = updateData.getUsername();
        }
        if (updateData.getEmail() != null) {
            this.email = updateData.getEmail();
        }
        if (updateData.getFirstName() != null) {
            this.firstName = updateData.getFirstName();
        }
        if (updateData.getLastName() != null) {
            this.lastName = updateData.getLastName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}