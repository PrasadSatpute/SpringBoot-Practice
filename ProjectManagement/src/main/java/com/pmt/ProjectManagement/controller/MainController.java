package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    public MainController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("totalUsers", userService.getUserCount());
        model.addAttribute("activeUsers", userService.getActiveUsers().size());
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}