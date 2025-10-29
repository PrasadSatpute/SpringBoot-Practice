package com.example.SecurityApplication.controller;

import com.example.SecurityApplication.entity.User;
import com.example.SecurityApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * USER REST API CONTROLLER
 * =========================
 * This controller provides REST API endpoints for user management.
 *
 * SECURITY FLOW FOR API REQUESTS:
 * ================================
 *
 * STEP 1: User makes API request (e.g., GET /api/users)
 *
 * STEP 2: Request enters Spring Security Filter Chain
 *         - SecurityContextPersistenceFilter loads authentication from session
 *
 * STEP 3: AuthorizationFilter checks if user is authenticated
 *         - If no authentication → 401 Unauthorized
 *         - If authenticated → Continue to next step
 *
 * STEP 4: Request reaches controller method
 *
 * STEP 5: @PreAuthorize annotation is evaluated
 *         - Checks if authenticated user has required role
 *         - Uses SecurityContext to get current user's authorities
 *
 * STEP 6: Authorization Decision:
 *         - If user has required role → Method executes
 *         - If user doesn't have role → 403 Forbidden (Access Denied)
 *
 * STEP 7: Method executes and returns response
 *
 * AUTHORIZATION RULES IN THIS CONTROLLER:
 * - GET /api/users → Requires ROLE_ADMIN
 * - GET /api/users/{id} → Requires ROLE_ADMIN
 * - POST /api/users → Requires ROLE_ADMIN (Create)
 * - PUT /api/users/{id} → Requires ROLE_ADMIN (Update)
 * - DELETE /api/users/{id} → Requires ROLE_ADMIN (Delete)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * INJECT USER SERVICE
     * Handles business logic for user operations
     */
    @Autowired
    private UserService userService;

    /**
     * GET ALL USERS
     * =============
     * Endpoint: GET /api/users
     *
     * AUTHORIZATION:
     * - @PreAuthorize("hasRole('ADMIN')") - Only ADMIN can access
     *
     * HOW @PreAuthorize WORKS:
     * 1. Spring AOP creates proxy around this method
     * 2. Before method executes, security check is performed
     * 3. Gets current authentication from SecurityContext
     * 4. Checks if authentication has "ROLE_ADMIN" authority
     * 5. If yes → Method executes
     *    If no → AccessDeniedException thrown → 403 response
     *
     * @return List of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("==============================================");
        System.out.println("API REQUEST: GET /api/users");
        System.out.println("Authorization: Checking ROLE_ADMIN...");
        System.out.println("==============================================");

        // If we reach here, user has ROLE_ADMIN
        // Security check passed!

        List<User> users = userService.getAllUsers();

        System.out.println("Returning " + users.size() + " users");

        return ResponseEntity.ok(users);
    }

    /**
     * GET USER BY ID
     * ==============
     * Endpoint: GET /api/users/{id}
     *
     * AUTHORIZATION:
     * - Only ADMIN can access
     *
     * @param id - User ID to fetch
     * @return User if found, 404 if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        System.out.println("==============================================");
        System.out.println("API REQUEST: GET /api/users/" + id);
        System.out.println("Authorization: ROLE_ADMIN verified");
        System.out.println("==============================================");

        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }
    }

    /**
     * CREATE NEW USER
     * ===============
     * Endpoint: POST /api/users
     *
     * AUTHORIZATION:
     * - Only ADMIN can create users
     *
     * SECURITY NOTE:
     * - Password will be encrypted by UserService
     * - Never send plain passwords in production (use HTTPS)
     *
     * REQUEST BODY:
     * {
     *   "username": "newuser",
     *   "password": "password123",
     *   "email": "user@example.com",
     *   "role": "USER"
     * }
     *
     * @param user - User object from request body
     * @return Created user (password will be encrypted)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        System.out.println("==============================================");
        System.out.println("API REQUEST: POST /api/users");
        System.out.println("Creating new user: " + user.getUsername());
        System.out.println("Authorization: ROLE_ADMIN verified");
        System.out.println("==============================================");

        // Validation: Check if username already exists
        if (userService.usernameExists(user.getUsername())) {
            System.out.println("ERROR: Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists: " + user.getUsername());
        }

        // Validation: Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            System.out.println("ERROR: Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists: " + user.getEmail());
        }

        // Create user (password will be encrypted in service)
        User createdUser = userService.createUser(user);

        System.out.println("User created successfully with ID: " + createdUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * UPDATE USER
     * ===========
     * Endpoint: PUT /api/users/{id}
     *
     * AUTHORIZATION:
     * - Only ADMIN can update users
     *
     * REQUEST BODY (all fields optional):
     * {
     *   "username": "updateduser",
     *   "password": "newpassword",
     *   "email": "newemail@example.com",
     *   "role": "ADMIN"
     * }
     *
     * @param id - User ID to update
     * @param user - User object with updated fields
     * @return Updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("==============================================");
        System.out.println("API REQUEST: PUT /api/users/" + id);
        System.out.println("Authorization: ROLE_ADMIN verified");
        System.out.println("==============================================");

        try {
            // Update user (password will be encrypted if changed)
            User updatedUser = userService.updateUser(id, user);

            System.out.println("User updated successfully");

            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * DELETE USER
     * ===========
     * Endpoint: DELETE /api/users/{id}
     *
     * AUTHORIZATION:
     * - Only ADMIN can delete users
     *
     * SECURITY WARNING:
     * - Don't allow admin to delete themselves
     * - Implement additional validation in production
     *
     * @param id - User ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        System.out.println("==============================================");
        System.out.println("API REQUEST: DELETE /api/users/" + id);
        System.out.println("Authorization: ROLE_ADMIN verified");
        System.out.println("==============================================");

        try {
            userService.deleteUser(id);

            System.out.println("User deleted successfully");

            return ResponseEntity.ok("User deleted successfully with ID: " + id);
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}