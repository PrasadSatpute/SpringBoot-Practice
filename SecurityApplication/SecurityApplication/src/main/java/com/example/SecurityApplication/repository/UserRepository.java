package com.example.SecurityApplication.repository;

import com.example.SecurityApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * USER REPOSITORY - DATABASE ACCESS LAYER
 * ========================================
 * This interface provides CRUD operations for User entity.
 *
 * SECURITY FLOW CONTEXT:
 * STEP 5: When authentication happens, this repository is called
 * - UserDetailsService uses findByUsername() to load user from database
 * - Spring Data JPA automatically implements this interface
 * - No need to write SQL queries, JPA handles it
 *
 * HOW IT WORKS:
 * 1. User submits login form with username and password
 * 2. Spring Security calls UserDetailsService
 * 3. UserDetailsService calls this repository's findByUsername()
 * 4. Repository queries database: SELECT * FROM users WHERE username = ?
 * 5. Returns Optional<User> (empty if user not found)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * FIND USER BY USERNAME
     * =====================
     * This method is CRITICAL for authentication process
     *
     * INTERNAL FLOW:
     * - Spring Data JPA generates SQL: SELECT * FROM users WHERE username = ?
     * - Returns Optional to handle cases where user doesn't exist
     * - Called by CustomUserDetailsService during login
     *
     * @param username - The username entered in login form
     * @return Optional<User> - User if found, empty Optional if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * FIND USER BY EMAIL
     * ==================
     * Optional method for email-based lookup
     * Can be used for password reset, email verification, etc.
     *
     * @param email - User's email address
     * @return Optional<User> - User if found, empty Optional if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * CHECK IF USERNAME EXISTS
     * ========================
     * Used during registration to prevent duplicate usernames
     *
     * INTERNAL QUERY: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     *
     * @param username - Username to check
     * @return boolean - true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * CHECK IF EMAIL EXISTS
     * =====================
     * Used during registration to prevent duplicate emails
     *
     * @param email - Email to check
     * @return boolean - true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}