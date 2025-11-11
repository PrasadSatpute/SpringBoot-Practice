package com.example.SecurityApplication.config;

import com.example.SecurityApplication.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SECURITY CONFIGURATION - THE SECURITY BRAIN
 * ============================================
 * This is where ALL Spring Security configuration happens.
 *
 * COMPLETE SECURITY FLOW - FROM START TO END:
 * ===========================================
 *
 * INITIALIZATION PHASE (Application Startup):
 * ------------------------------------------
 * STEP 1: Spring Boot starts, scans @Configuration classes
 * STEP 2: This SecurityConfig class is loaded
 * STEP 3: All @Bean methods are executed:
 *         - passwordEncoder() → Creates BCrypt encoder
 *         - authenticationProvider() → Sets up authentication logic
 *         - securityFilterChain() → Configures security filters
 * STEP 4: Security filter chain is registered with servlet container
 * STEP 5: Application is ready to handle requests
 *
 * REQUEST PHASE (When user makes a request):
 * ------------------------------------------
 * STEP 1: User makes HTTP request (e.g., GET /dashboard)
 * STEP 2: Request enters Spring Security Filter Chain
 * STEP 3: SecurityContextPersistenceFilter checks if user is authenticated
 *         - Looks for existing session/authentication
 * STEP 4: AuthorizationFilter checks if user has permission
 *         - Uses rules defined in securityFilterChain()
 * STEP 5: If not authenticated → Redirect to /login
 *         If authenticated but no permission → 403 Forbidden
 *         If authenticated with permission → Allow request
 *
 * LOGIN PHASE (When user submits login form):
 * -------------------------------------------
 * STEP 1: User submits POST /login with username & password
 * STEP 2: UsernamePasswordAuthenticationFilter intercepts
 * STEP 3: Creates UsernamePasswordAuthenticationToken
 * STEP 4: Passes to AuthenticationManager
 * STEP 5: AuthenticationManager uses DaoAuthenticationProvider
 * STEP 6: DaoAuthenticationProvider calls CustomUserDetailsService
 * STEP 7: CustomUserDetailsService loads user from database
 * STEP 8: Returns UserDetails with username, password hash, authorities
 * STEP 9: DaoAuthenticationProvider verifies password using BCrypt
 * STEP 10: If valid → Authentication object created
 * STEP 11: SecurityContext is populated with authentication
 * STEP 12: Session is created, user is redirected to /dashboard
 * STEP 13: All subsequent requests are authenticated via session
 *
 * AUTHORIZATION PHASE (When accessing protected resources):
 * ---------------------------------------------------------
 * STEP 1: Authenticated user requests protected resource
 * STEP 2: AuthorizationFilter checks user's authorities
 * STEP 3: Compares with required authorities (hasRole)
 * STEP 4: If match → Allow access
 *         If no match → Deny access (403 Forbidden)
 *
 * LOGOUT PHASE (When user logs out):
 * ----------------------------------
 * STEP 1: User clicks logout or accesses /logout
 * STEP 2: LogoutFilter intercepts request
 * STEP 3: SecurityContext is cleared
 * STEP 4: Session is invalidated
 * STEP 5: User is redirected to /login?logout
 */
@Configuration
@EnableWebSecurity
// Enable method-level security (@PreAuthorize, @Secured, etc.)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * INJECT CUSTOM USER DETAILS SERVICE
     * This service loads user data from database during authentication
     */
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * PASSWORD ENCODER BEAN
     * =====================
     * CRITICAL SECURITY COMPONENT
     *
     * WHAT IT DOES:
     * - Encrypts passwords before storing in database
     * - Verifies passwords during login
     *
     * WHY BCRYPT?
     * - Industry standard for password hashing
     * - Slow by design (prevents brute force attacks)
     * - Includes salt automatically (prevents rainbow table attacks)
     * - Generates different hash each time for same password
     *
     * USAGE IN SECURITY FLOW:
     * 1. Registration: passwordEncoder.encode(plainPassword)
     *    → Converts "password123" to "$2a$10$xYz..."
     * 2. Login: passwordEncoder.matches(submittedPassword, storedHash)
     *    → Returns true if passwords match, false otherwise
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("==============================================");
        System.out.println("SECURITY INITIALIZATION: Creating BCrypt Password Encoder");
        System.out.println("Strength: 10 rounds (2^10 = 1024 iterations)");
        System.out.println("==============================================");

        // BCrypt with strength 10 (default)
        // Higher strength = more secure but slower
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION PROVIDER BEAN
     * ============================
     * This is the AUTHENTICATION ENGINE
     *
     * WHAT IT DOES:
     * - Handles the actual authentication logic
     * - Uses UserDetailsService to load user
     * - Uses PasswordEncoder to verify password
     *
     * AUTHENTICATION FLOW:
     * 1. Receives authentication request (username + password)
     * 2. Calls userDetailsService.loadUserByUsername()
     * 3. Gets UserDetails with encrypted password
     * 4. Calls passwordEncoder.matches() to verify password
     * 5. If match → Returns authenticated Authentication object
     *    If no match → Throws BadCredentialsException
     *
     * @return DaoAuthenticationProvider configured with our services
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        System.out.println("==============================================");
        System.out.println("SECURITY INITIALIZATION: Creating Authentication Provider");
        System.out.println("UserDetailsService: CustomUserDetailsService");
        System.out.println("PasswordEncoder: BCryptPasswordEncoder");
        System.out.println("==============================================");

        // Create the authentication provider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Set our custom UserDetailsService
        // This tells Spring Security where to load user data from
        authProvider.setUserDetailsService(userDetailsService);

        // Set password encoder
        // This tells Spring Security how to verify passwords
        authProvider.setPasswordEncoder(passwordEncoder());

        System.out.println("Authentication Provider configured successfully");

        return authProvider;
    }

    /**
     * AUTHENTICATION MANAGER BEAN
     * ===========================
     * The coordinator of authentication process
     *
     * ROLE IN SECURITY FLOW:
     * - Receives authentication requests
     * - Delegates to appropriate AuthenticationProvider
     * - Returns Authentication object if successful
     *
     * @param authConfig Spring's authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        System.out.println("==============================================");
        System.out.println("SECURITY INITIALIZATION: Creating Authentication Manager");
        System.out.println("==============================================");

        return authConfig.getAuthenticationManager();
    }

    /**
     * SECURITY FILTER CHAIN - THE MAIN SECURITY CONFIGURATION
     * ========================================================
     * This is where we define:
     * - Which URLs are protected
     * - Which roles can access which URLs
     * - Login/logout behavior
     * - Security features (CSRF, headers, etc.)
     *
     * FILTER CHAIN EXECUTION ORDER:
     * 1. SecurityContextPersistenceFilter - Loads security context from session
     * 2. LogoutFilter - Handles logout requests
     * 3. UsernamePasswordAuthenticationFilter - Handles login form submissions
     * 4. RequestCacheAwareFilter - Handles saved requests after login
     * 5. SecurityContextHolderAwareRequestFilter - Wraps request
     * 6. AnonymousAuthenticationFilter - Creates anonymous authentication if needed
     * 7. SessionManagementFilter - Manages sessions
     * 8. ExceptionTranslationFilter - Handles security exceptions
     * 9. AuthorizationFilter - Makes authorization decisions (FINAL STEP)
     *
     * @param http HttpSecurity configuration object
     * @return SecurityFilterChain configured with our rules
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("==============================================");
        System.out.println("SECURITY INITIALIZATION: Configuring Security Filter Chain");
        System.out.println("==============================================");

        http
                // AUTHORIZATION CONFIGURATION
                // ===========================
                // Define which URLs require which permissions
                .authorizeHttpRequests(auth -> {
                    System.out.println("Configuring URL Authorization Rules:");

                    // PUBLIC URLS - No authentication required
                    // These URLs are accessible to everyone
                    System.out.println("- /login, /css/**, /js/** → Permit All (Public)");
                    auth.requestMatchers("/login", "/css/**", "/js/**", "/error").permitAll();

                    // ADMIN-ONLY URLS - Requires ADMIN role
                    // Only users with ROLE_ADMIN can access these
                    System.out.println("- /api/users/** → Requires ROLE_ADMIN");
                    auth.requestMatchers("/api/users/**").hasRole("ADMIN");

                    // USER URLS - Requires USER or ADMIN role
                    // Both ADMIN and USER can access dashboard
                    System.out.println("- /dashboard → Requires authentication (ADMIN or USER)");
                    auth.requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER");

                    // ALL OTHER URLS - Require authentication
                    System.out.println("- /** → Requires authentication");
                    auth.anyRequest().authenticated();
                })

                // FORM LOGIN CONFIGURATION
                // =========================
                // Configure custom login page and behavior
                .formLogin(form -> {
                    System.out.println("Configuring Form Login:");

                    // Custom login page URL
                    System.out.println("- Login Page: /login");
                    form.loginPage("/login");

                    // URL where login form is submitted
                    System.out.println("- Login Processing URL: /login");
                    form.loginProcessingUrl("/login");

                    // Where to redirect after successful login
                    System.out.println("- Success Redirect: /dashboard");
                    form.defaultSuccessUrl("/dashboard", true);

                    // Where to redirect after login failure
                    System.out.println("- Failure Redirect: /login?error");
                    form.failureUrl("/login?error=true");

                    // Form field names (must match HTML form)
                    System.out.println("- Username Parameter: username");
                    System.out.println("- Password Parameter: password");
                    form.usernameParameter("username");
                    form.passwordParameter("password");

                    // Allow everyone to access login page
                    form.permitAll();
                })

                // LOGOUT CONFIGURATION
                // ====================
                // Configure logout behavior
                .logout(logout -> {
                    System.out.println("Configuring Logout:");

                    // Logout URL
                    System.out.println("- Logout URL: /logout");
                    logout.logoutUrl("/logout");

                    // Where to redirect after logout
                    System.out.println("- Logout Success URL: /login?logout");
                    logout.logoutSuccessUrl("/login?logout=true");

                    // Invalidate session on logout
                    System.out.println("- Invalidate Session: true");
                    logout.invalidateHttpSession(true);

                    // Delete cookies on logout
                    System.out.println("- Delete Cookies: JSESSIONID");
                    logout.deleteCookies("JSESSIONID");

                    // Allow everyone to access logout
                    logout.permitAll();
                })

                // EXCEPTION HANDLING
                // ==================
                // Configure how to handle security exceptions
                .exceptionHandling(ex -> {
                    System.out.println("Configuring Exception Handling:");

                    // Redirect to login page when not authenticated
                    System.out.println("- Access Denied Handler: Custom handler");
                    ex.accessDeniedPage("/error/403");
                })

                // SESSION MANAGEMENT
                // ==================
                // Configure session behavior
                .sessionManagement(session -> {
                    System.out.println("Configuring Session Management:");

                    // Configure maximum concurrent sessions
                    System.out.println("- Max Sessions Per User: 1");
                    session.maximumSessions(1)
                            // Expire oldest session when max reached
                            .maxSessionsPreventsLogin(false)
                            .expiredUrl("/login?expired=true");
                });

        System.out.println("==============================================");
        System.out.println("Security Filter Chain configured successfully!");
        System.out.println("Application is now protected by Spring Security");
        System.out.println("==============================================");

        // Build and return the security filter chain
        return http.build();
    }
}