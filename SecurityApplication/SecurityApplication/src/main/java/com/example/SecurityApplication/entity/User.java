package com.example.SecurityApplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * USER ENTITY - DATABASE MODEL
 * =============================
 * This represents the User table in MySQL database.
 *
 * SECURITY FLOW CONTEXT:
 * - This entity stores user credentials and roles
 * - Spring Security uses this data for authentication and authorization
 * - The password field stores BCrypt encrypted passwords
 * - The role field determines what actions user can perform
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * PRIMARY KEY
     * Auto-generated unique identifier for each user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * USERNAME - Used for login
     * Must be unique across all users
     * This is the principal in Spring Security
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * PASSWORD - Encrypted using BCrypt
     * SECURITY NOTE: Never store plain text passwords!
     * Spring Security's PasswordEncoder handles encryption/verification
     *
     * BCrypt format: $2a$10$[22 character salt][31 character hash]
     * Example: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * EMAIL - User's email address
     * Optional field for user communication
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * ROLE - Defines user permissions
     *
     * SPRING SECURITY AUTHORIZATION:
     * - ADMIN: Full access (CREATE, READ, UPDATE, DELETE)
     * - USER: Read-only access
     *
     * IMPORTANT: Spring Security expects roles to start with "ROLE_"
     * But we store without prefix and add it in UserDetailsService
     */
    @Column(nullable = false, length = 20)
    private String role; // Values: "ADMIN" or "USER"

    /**
     * ENABLED STATUS
     * If false, user cannot login even with correct credentials
     * Useful for account suspension/activation
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Constructor without ID (for creating new users)
     */
    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = true;
    }
}