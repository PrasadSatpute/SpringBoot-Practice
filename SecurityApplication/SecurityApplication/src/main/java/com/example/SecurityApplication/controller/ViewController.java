package com.example.SecurityApplication.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * VIEW CONTROLLER - WEB PAGE CONTROLLER
 * ======================================
 * This controller handles web page requests and returns Thymeleaf templates.
 *
 * DIFFERENCE FROM REST CONTROLLER:
 * - @Controller (not @RestController) - Returns view names, not data
 * - Returns String (view name) instead of ResponseEntity
 * - Thymeleaf resolves view name to HTML template
 *
 * SECURITY FLOW FOR WEB PAGES:
 * ============================
 *
 * STEP 1: User requests a page (e.g., GET /dashboard)
 *
 * STEP 2: Request enters Security Filter Chain
 *         - SecurityContextPersistenceFilter checks for authentication
 *
 * STEP 3: AuthorizationFilter checks URL permissions
 *         - Compares request URL with rules in SecurityConfig
 *
 * STEP 4: Authorization Decision:
 *         - /login → Allowed for everyone
 *         - /dashboard → Requires authentication
 *         - If not authenticated → Redirect to /login
 *         - If authenticated → Continue to controller
 *
 * STEP 5: Controller method executes
 *         - Gets authentication from SecurityContext
 *         - Adds data to Model for Thymeleaf
 *
 * STEP 6: Returns view name (e.g., "dashboard")
 *         - Thymeleaf resolves to: /templates/dashboard.html
 *
 * STEP 7: Thymeleaf processes template
 *         - Uses sec:authorize to show/hide content based on roles
 *         - Renders final HTML
 *
 * STEP 8: HTML sent to browser
 */
@Controller
public class ViewController {

    /**
     * LOGIN PAGE
     * ==========
     * Endpoint: GET /login
     *
     * SECURITY:
     * - Accessible to everyone (permitAll in SecurityConfig)
     * - If user is already authenticated, can redirect to dashboard
     *
     * URL PARAMETERS:
     * - error=true → Login failed (wrong username/password)
     * - logout=true → User just logged out
     *
     * @param error - Optional parameter indicating login error
     * @param logout - Optional parameter indicating logout success
     * @param model - Spring Model to pass data to view
     * @return "login" - Resolves to /templates/login.html
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        System.out.println("==============================================");
        System.out.println("VIEW REQUEST: GET /login");

        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                !auth.getPrincipal().equals("anonymousUser")) {
            System.out.println("User already authenticated: " + auth.getName());
            System.out.println("Redirecting to dashboard...");
            System.out.println("==============================================");

            // User is already logged in, redirect to dashboard
            return "redirect:/dashboard";
        }

        // Handle error parameter (login failed)
        if (error != null) {
            System.out.println("Login failed - Invalid credentials");
            model.addAttribute("error", "Invalid username or password!");
        }

        // Handle logout parameter (successful logout)
        if (logout != null) {
            System.out.println("User logged out successfully");
            model.addAttribute("message", "You have been logged out successfully.");
        }

        System.out.println("Rendering login page");
        System.out.println("==============================================");

        // Return view name - Thymeleaf will resolve to /templates/login.html
        return "login";
    }

    /**
     * DASHBOARD PAGE
     * ==============
     * Endpoint: GET /dashboard
     *
     * SECURITY:
     * - Requires authentication (configured in SecurityConfig)
     * - Accessible to both ADMIN and USER roles
     *
     * WHAT HAPPENS HERE:
     * 1. User requests /dashboard
     * 2. Security filter checks if authenticated
     * 3. If not authenticated → Redirect to /login
     * 4. If authenticated → This method executes
     * 5. We get current user's authentication from SecurityContext
     * 6. Extract username and role
     * 7. Pass data to Thymeleaf template
     * 8. Template uses sec:authorize to show/hide content based on role
     *
     * @param model - Spring Model to pass data to view
     * @return "dashboard" - Resolves to /templates/dashboard.html
     */
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        System.out.println("==============================================");
        System.out.println("VIEW REQUEST: GET /dashboard");

        // STEP 1: Get authentication from SecurityContext
        // SecurityContext is thread-local storage maintained by Spring Security
        // It contains the current user's authentication information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Authentication found: " + authentication.getName());

        // STEP 2: Extract username (principal)
        // This is the username used during login
        String username = authentication.getName();
        System.out.println("Current user: " + username);

        // STEP 3: Extract authorities (roles)
        // Authorities are the roles/permissions assigned to the user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // STEP 4: Determine user's role
        String role = "USER"; // Default
        for (GrantedAuthority authority : authorities) {
            String authName = authority.getAuthority();
            System.out.println("User authority: " + authName);

            // Check if user is ADMIN
            if (authName.equals("ROLE_ADMIN")) {
                role = "ADMIN";
                break;
            }
        }

        System.out.println("User role: " + role);

        // STEP 5: Add data to model
        // Model is used by Thymeleaf to access data in the template
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", role.equals("ADMIN"));

        System.out.println("Dashboard data prepared");
        System.out.println("Is Admin: " + role.equals("ADMIN"));
        System.out.println("Rendering dashboard page");
        System.out.println("==============================================");

        // STEP 6: Return view name
        // Thymeleaf will resolve "dashboard" to /templates/dashboard.html
        // Template will use sec:authorize to show/hide admin features
        return "dashboard";
    }

    /**
     * HOME/ROOT PAGE
     * ==============
     * Endpoint: GET /
     *
     * SECURITY:
     * - Redirects to dashboard if authenticated
     * - Redirects to login if not authenticated
     *
     * @return Redirect to appropriate page
     */
    @GetMapping("/")
    public String homePage() {
        System.out.println("==============================================");
        System.out.println("VIEW REQUEST: GET /");

        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated
        if (auth != null && auth.isAuthenticated() &&
                !auth.getPrincipal().equals("anonymousUser")) {
            System.out.println("User authenticated, redirecting to dashboard");
            System.out.println("==============================================");
            return "redirect:/dashboard";
        } else {
            System.out.println("User not authenticated, redirecting to login");
            System.out.println("==============================================");
            return "redirect:/login";
        }
    }

    /**
     * ERROR PAGE - 403 FORBIDDEN
     * ===========================
     * Endpoint: GET /error/403
     *
     * WHEN THIS IS SHOWN:
     * - User is authenticated but doesn't have required role
     * - Example: USER role trying to access ADMIN-only endpoint
     *
     * @param model - Spring Model
     * @return "error-403" - Resolves to /templates/error-403.html
     */
    @GetMapping("/error/403")
    public String error403(Model model) {
        System.out.println("==============================================");
        System.out.println("VIEW REQUEST: GET /error/403");
        System.out.println("Access Denied - Insufficient permissions");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            System.out.println("User: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
        }

        System.out.println("Rendering 403 error page");
        System.out.println("==============================================");

        model.addAttribute("message", "You don't have permission to access this resource.");

        return "error-403";
    }
}