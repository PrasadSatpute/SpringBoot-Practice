package com.example.SecurityApplication.security;

import com.example.SecurityApplication.entity.User;
import com.example.SecurityApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * CUSTOM USER DETAILS SERVICE - AUTHENTICATION BRIDGE
 * ====================================================
 * This is the HEART of Spring Security authentication process.
 *
 * CRITICAL SECURITY FLOW - STEP 6: USER AUTHENTICATION
 * =====================================================
 *
 * WHAT HAPPENS DURING LOGIN:
 *
 * 1. User submits login form (username + password)
 *    └─> POST request to /login
 *
 * 2. UsernamePasswordAuthenticationFilter intercepts the request
 *    └─> Extracts username and password from request
 *
 * 3. AuthenticationManager receives authentication request
 *    └─> Delegates to DaoAuthenticationProvider
 *
 * 4. DaoAuthenticationProvider calls THIS SERVICE (loadUserByUsername)
 *    └─> Loads user from database
 *
 * 5. THIS METHOD executes:
 *    a) Query database for user by username
 *    b) If not found → throw UsernameNotFoundException
 *    c) If found → convert User entity to UserDetails
 *    d) Return UserDetails with username, password, and authorities
 *
 * 6. DaoAuthenticationProvider verifies password
 *    └─> Uses PasswordEncoder.matches(rawPassword, encodedPassword)
 *    └─> Compares submitted password with stored BCrypt hash
 *
 * 7. If password matches:
 *    └─> Authentication object is created
 *    └─> SecurityContext is populated with authentication
 *    └─> User is logged in, session is created
 *
 * 8. If password doesn't match:
 *    └─> BadCredentialsException is thrown
 *    └─> User redirected to /login?error
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * INJECT USER REPOSITORY
     * This is used to fetch user data from MySQL database
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * LOAD USER BY USERNAME - CORE AUTHENTICATION METHOD
     * ===================================================
     * This method is automatically called by Spring Security during login.
     *
     * EXECUTION FLOW:
     * ---------------
     * @param username - The username entered in login form
     * @return UserDetails - Spring Security's representation of the user
     * @throws UsernameNotFoundException - If user not found in database
     *
     * INTERNAL STEPS:
     * 1. Query database using UserRepository.findByUsername()
     * 2. If user not found, throw exception (authentication fails)
     * 3. If user found, build UserDetails object with:
     *    - Username (identifier)
     *    - Password (BCrypt encrypted)
     *    - Authorities (roles/permissions)
     *    - Account status flags
     * 4. Return UserDetails to DaoAuthenticationProvider
     * 5. Provider compares passwords and completes authentication
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("==============================================");
        System.out.println("AUTHENTICATION STEP 6: Loading user from database");
        System.out.println("Username: " + username);
        System.out.println("==============================================");

        // STEP 6A: Query database for user
        // This calls: SELECT * FROM users WHERE username = ?
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("ERROR: User not found - " + username);
                    // This exception stops authentication and redirects to /login?error
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("SUCCESS: User found in database");
        System.out.println("User ID: " + user.getId());
        System.out.println("Role: " + user.getRole());
        System.out.println("Enabled: " + user.isEnabled());

        // STEP 6B: Convert our User entity to Spring Security's UserDetails
        // UserDetails is what Spring Security understands and uses
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                // Password is BCrypt encrypted, Spring Security will verify it
                .password(user.getPassword())
                // Convert role to GrantedAuthority (Spring Security's permission format)
                .authorities(getAuthorities(user.getRole()))
                // Account status flags (all true = account is active and valid)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();

        System.out.println("UserDetails object created successfully");
        System.out.println("Authorities: " + userDetails.getAuthorities());
        System.out.println("Now password will be verified by DaoAuthenticationProvider");
        System.out.println("==============================================");

        // STEP 6C: Return UserDetails to authentication provider
        // Next step: Password verification
        return userDetails;
    }

    /**
     * GET AUTHORITIES (ROLES) FOR USER
     * =================================
     * Converts role string to Spring Security's GrantedAuthority format.
     *
     * SPRING SECURITY AUTHORIZATION:
     * - Authorities determine what user can access
     * - Format: "ROLE_" prefix + role name
     * - Example: "ADMIN" becomes "ROLE_ADMIN"
     *
     * WHY "ROLE_" PREFIX?
     * - Spring Security's hasRole("ADMIN") automatically adds "ROLE_" prefix
     * - So we need to store it as "ROLE_ADMIN" in authorities
     *
     * @param role - The role from database (ADMIN or USER)
     * @return Collection of GrantedAuthority objects
     */
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {

        System.out.println("Converting role to authority: " + role + " -> ROLE_" + role);

        // Create GrantedAuthority with "ROLE_" prefix
        // This is used by @PreAuthorize, hasRole(), and other security checks
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
        );
    }
}