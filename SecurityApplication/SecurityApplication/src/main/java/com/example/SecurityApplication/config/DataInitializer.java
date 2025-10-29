package com.example.SecurityApplication.config;

import com.example.SecurityApplication.entity.User;
import com.example.SecurityApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DATA INITIALIZER - SAMPLE DATA LOADER
 * ======================================
 * This class runs ONCE when the application starts.
 * It creates default users for testing purposes.
 *
 * EXECUTION FLOW:
 * ===============
 * STEP 1: Spring Boot application starts
 * STEP 2: All beans are created and configured
 * STEP 3: CommandLineRunner beans are executed
 * STEP 4: This run() method is called
 * STEP 5: Sample users are created in database
 * STEP 6: Application is ready to accept requests
 *
 * DEFAULT USERS CREATED:
 * ======================
 * 1. ADMIN USER:
 *    Username: admin
 *    Password: admin123
 *    Role: ADMIN
 *    Permissions: CREATE, READ, UPDATE, DELETE
 *
 * 2. NORMAL USER:
 *    Username: user
 *    Password: user123
 *    Role: USER
 *    Permissions: READ ONLY
 */
@Component
public class DataInitializer implements CommandLineRunner {

    /**
     * INJECT DEPENDENCIES
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * RUN METHOD - EXECUTED AT STARTUP
     * =================================
     * This method runs automatically when application starts.
     *
     * @param args - Command line arguments (not used)
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("==============================================");
        System.out.println("DATA INITIALIZATION: Creating sample users");
        System.out.println("==============================================");

        // Check if users already exist (prevent duplicates on restart)
        if (userRepository.count() == 0) {

            // CREATE ADMIN USER
            // =================
            System.out.println("Creating ADMIN user...");

            User admin = new User();
            admin.setUsername("admin");
            // CRITICAL: Encrypt password using BCrypt
            // Plain: "admin123" → Encrypted: "$2a$10$..."
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("✓ ADMIN user created");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin123");
            System.out.println("  Role: ADMIN");
            System.out.println("  Permissions: CREATE, READ, UPDATE, DELETE");

            // CREATE NORMAL USER
            // ==================
            System.out.println("\nCreating USER...");

            User user = new User();
            user.setUsername("user");
            // CRITICAL: Encrypt password using BCrypt
            // Plain: "user123" → Encrypted: "$2a$10$..."
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setRole("USER");
            user.setEnabled(true);

            userRepository.save(user);

            System.out.println("✓ USER created");
            System.out.println("  Username: user");
            System.out.println("  Password: user123");
            System.out.println("  Role: USER");
            System.out.println("  Permissions: READ ONLY");

            System.out.println("\n==============================================");
            System.out.println("Sample data created successfully!");
            System.out.println("==============================================");
            System.out.println("\nYou can now login with:");
            System.out.println("1. Admin: admin/admin123 (Full Access)");
            System.out.println("2. User: user/user123 (Read Only)");
            System.out.println("==============================================\n");

        } else {
            System.out.println("Users already exist in database");
            System.out.println("Total users: " + userRepository.count());
            System.out.println("Skipping data initialization");
            System.out.println("==============================================\n");
        }
    }
}