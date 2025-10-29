package com.example.SecurityApplication.service;

import com.example.SecurityApplication.entity.User;
import com.example.SecurityApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * USER SERVICE - BUSINESS LOGIC LAYER
 * ====================================
 * This service handles all user-related business operations.
 *
 * ROLE IN SECURITY:
 * - Manages user CRUD operations
 * - Ensures passwords are encrypted before saving
 * - Validates user data
 * - Called by controllers to perform operations
 */
@Service
@Transactional
public class UserService {

    /**
     * INJECT DEPENDENCIES
     * - UserRepository: For database operations
     * - PasswordEncoder: For encrypting passwords
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * GET ALL USERS
     * =============
     * Retrieves all users from database.
     *
     * AUTHORIZATION:
     * - Only ADMIN role can call this via API
     * - Protected by @PreAuthorize in controller
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        System.out.println("==============================================");
        System.out.println("SERVICE: Fetching all users from database");

        // Query database: SELECT * FROM users
        List<User> users = userRepository.findAll();

        System.out.println("Found " + users.size() + " users");
        System.out.println("==============================================");

        return users;
    }

    /**
     * GET USER BY ID
     * ==============
     * Retrieves a specific user by their ID.
     *
     * @param id - User ID to fetch
     * @return Optional<User> - User if found, empty if not
     */
    public Optional<User> getUserById(Long id) {
        System.out.println("==============================================");
        System.out.println("SERVICE: Fetching user by ID: " + id);

        // Query database: SELECT * FROM users WHERE id = ?
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getUsername());
        } else {
            System.out.println("User not found with ID: " + id);
        }
        System.out.println("==============================================");

        return user;
    }

    /**
     * GET USER BY USERNAME
     * ====================
     * Retrieves a user by their username.
     *
     * @param username - Username to search
     * @return Optional<User> - User if found, empty if not
     */
    public Optional<User> getUserByUsername(String username) {
        System.out.println("SERVICE: Fetching user by username: " + username);
        return userRepository.findByUsername(username);
    }

    /**
     * CREATE NEW USER
     * ===============
     * Creates a new user in the database.
     *
     * CRITICAL SECURITY STEP:
     * - Password MUST be encrypted before saving
     * - Uses BCrypt password encoder
     * - Never store plain text passwords!
     *
     * AUTHORIZATION:
     * - Only ADMIN can create users via API
     *
     * @param user - User object with plain text password
     * @return Saved user with encrypted password
     */
    public User createUser(User user) {
        System.out.println("==============================================");
        System.out.println("SERVICE: Creating new user");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role: " + user.getRole());

        // CRITICAL: Encrypt password before saving
        // Never store plain text passwords in database!
        String plainPassword = user.getPassword();
        System.out.println("Encrypting password using BCrypt...");

        // BCrypt encryption: Converts "password123" to "$2a$10$xYz..."
        String encryptedPassword = passwordEncoder.encode(plainPassword);
        user.setPassword(encryptedPassword);

        System.out.println("Password encrypted successfully");
        System.out.println("Plain password length: " + plainPassword.length());
        System.out.println("Encrypted password length: " + encryptedPassword.length());

        // Save user to database
        // SQL: INSERT INTO users (username, password, email, role, enabled) VALUES (?, ?, ?, ?, ?)
        User savedUser = userRepository.save(user);

        System.out.println("User created successfully with ID: " + savedUser.getId());
        System.out.println("==============================================");

        return savedUser;
    }

    /**
     * UPDATE EXISTING USER
     * ====================
     * Updates an existing user's information.
     *
     * SECURITY CONSIDERATIONS:
     * - If password is changed, it must be encrypted
     * - Only ADMIN can update users via API
     *
     * @param id - ID of user to update
     * @param updatedUser - User object with new data
     * @return Updated user
     */
    public User updateUser(Long id, User updatedUser) {
        System.out.println("==============================================");
        System.out.println("SERVICE: Updating user with ID: " + id);

        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        System.out.println("Existing user found: " + existingUser.getUsername());

        // Update fields
        if (updatedUser.getUsername() != null) {
            System.out.println("Updating username: " + updatedUser.getUsername());
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null) {
            System.out.println("Updating email: " + updatedUser.getEmail());
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getRole() != null) {
            System.out.println("Updating role: " + updatedUser.getRole());
            existingUser.setRole(updatedUser.getRole());
        }

        // If password is being updated, encrypt it
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            System.out.println("Password is being updated, encrypting...");
            String encryptedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(encryptedPassword);
            System.out.println("Password encrypted successfully");
        }

        // Save updated user
        // SQL: UPDATE users SET username=?, email=?, role=?, password=? WHERE id=?
        User saved = userRepository.save(existingUser);

        System.out.println("User updated successfully");
        System.out.println("==============================================");

        return saved;
    }

    /**
     * DELETE USER
     * ===========
     * Deletes a user from the database.
     *
     * AUTHORIZATION:
     * - Only ADMIN can delete users via API
     *
     * SECURITY NOTE:
     * - Cannot delete yourself (logged-in admin)
     * - Should validate before deletion
     *
     * @param id - ID of user to delete
     */
    public void deleteUser(Long id) {
        System.out.println("==============================================");
        System.out.println("SERVICE: Deleting user with ID: " + id);

        // Verify user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        System.out.println("Deleting user: " + user.getUsername());

        // Delete from database
        // SQL: DELETE FROM users WHERE id = ?
        userRepository.deleteById(id);

        System.out.println("User deleted successfully");
        System.out.println("==============================================");
    }

    /**
     * CHECK IF USERNAME EXISTS
     * ========================
     * Checks if a username is already taken.
     * Used for registration validation.
     *
     * @param username - Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        boolean exists = userRepository.existsByUsername(username);
        System.out.println("Username '" + username + "' exists: " + exists);
        return exists;
    }

    /**
     * CHECK IF EMAIL EXISTS
     * =====================
     * Checks if an email is already registered.
     * Used for registration validation.
     *
     * @param email - Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        boolean exists = userRepository.existsByEmail(email);
        System.out.println("Email '" + email + "' exists: " + exists);
        return exists;
    }
}