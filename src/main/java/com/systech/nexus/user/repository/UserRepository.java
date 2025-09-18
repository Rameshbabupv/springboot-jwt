package com.systech.nexus.user.repository;

import com.systech.nexus.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 *
 * Features:
 * - Case-sensitive username operations for security
 * - Case-insensitive email operations for user experience
 * - Flexible search across username, firstName, and lastName
 * - Ordering capabilities for user listings
 *
 * @author Claude Code Assistant
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (case-sensitive)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if username exists (case-sensitive)
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find users by first name or last name containing the search term (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    java.util.List<User> findByNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Custom query to find all users ordered by creation date (newest first)
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    java.util.List<User> findAllOrderByCreatedAtDesc();
}