package com.systech.nexus.user.service;

import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.user.domain.User;
import com.systech.nexus.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Loggable(description = "User Service Operations")
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Get all users")
    public List<User> getAllUsers() {
        return userRepository.findAllOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Get user by ID")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Get user by username")
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Get user by email")
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    @Loggable(description = "Create new user")
    public User createUser(String username, String email, String firstName, String lastName) {
        // Validate uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("Username '" + username + "' already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DataIntegrityViolationException("Email '" + email + "' already exists");
        }

        User newUser = new User(username, email, firstName, lastName);
        return userRepository.save(newUser);
    }

    @Transactional
    @Loggable(description = "Update existing user")
    public User updateUser(Long id, String username, String email, String firstName, String lastName) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check for conflicts only if values are being changed
        if (username != null && !username.equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new DataIntegrityViolationException("Username '" + username + "' already exists");
            }
        }

        if (email != null && !email.equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new DataIntegrityViolationException("Email '" + email + "' already exists");
            }
        }

        // Create update object with only non-null values
        User updateData = new User();
        updateData.setUsername(username);
        updateData.setEmail(email);
        updateData.setFirstName(firstName);
        updateData.setLastName(lastName);

        // Apply partial update
        existingUser.updateFrom(updateData);

        return userRepository.save(existingUser);
    }

    @Transactional
    @Loggable(description = "Delete user")
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Search users by name")
    public List<User> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        return userRepository.findByNameContaining(searchTerm.trim());
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Check if username exists")
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Check if email exists")
    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    @Loggable(description = "Get user count")
    public long getUserCount() {
        return userRepository.count();
    }
}