package gov.property.web.controller.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String home() {
        log.info("Home page requested");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        log.info("Login page requested");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        log.info("Register page requested");
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        log.info("Dashboard requested by: {}", authentication != null ? authentication.getName() : "anonymous");
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("role", authentication.getAuthorities().toString());
        }
        return "dashboard";
    }
}