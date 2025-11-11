package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.enums.ProjectStatus;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.ProjectService;
import com.pmt.ProjectManagement.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final UserService userService;
    private final ProjectService projectService;

    public MainController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("/login")
    public String login() {
        System.out.println("Login Controller Call");
        return "login";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        System.out.println("Dashboard Controller Call");
        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("totalUsers", userService.getUserCount());
        model.addAttribute("activeUsers", userService.getActiveUsers().size());
        model.addAttribute("totalProjects", projectService.getTotalProjects());
        model.addAttribute("activeProjects", projectService.countProjectsByStatus(ProjectStatus.IN_PROGRESS));
        model.addAttribute("completedProjects", projectService.countProjectsByStatus(ProjectStatus.COMPLETED));
        model.addAttribute("overdueProjects", projectService.getOverdueProjects().size());
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}