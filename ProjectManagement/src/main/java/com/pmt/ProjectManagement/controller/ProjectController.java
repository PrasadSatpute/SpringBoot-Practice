package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.ProjectDTO;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.Task;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.ProjectPriority;
import com.pmt.ProjectManagement.enums.ProjectStatus;
import com.pmt.ProjectManagement.enums.Role;
import com.pmt.ProjectManagement.enums.TaskStatus;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.ProjectService;
import com.pmt.ProjectManagement.service.TaskService;
import com.pmt.ProjectManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, UserService userService, TaskService taskService) {
        this.projectService = projectService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping
    public String listProjects(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Project> projects;
        User currentUser = userDetails.getUser();

        // Show all projects to ADMIN, CEO, MANAGER
        if (currentUser.getRole() == Role.ADMIN ||
                currentUser.getRole() == Role.CEO ||
                currentUser.getRole() == Role.MANAGER) {
            projects = projectService.getAllProjects();
        } else {
            // Show only assigned projects to other users
            System.out.println("List of Project By Assign User-------------------");
            projects = projectService.getProjectsByAssignedUser(currentUser);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("currentUser", currentUser);

        System.out.println("List of All Project -------------------");
        return "projects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User currentUser = userDetails.getUser();

        // Check if user has permission
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        model.addAttribute("projectDTO", new ProjectDTO());
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("priorities", ProjectPriority.values());
        model.addAttribute("users", userService.getActiveUsers());
        model.addAttribute("managers", userService.getActiveUsers().stream()
                .filter(u -> u.getRole() == Role.MANAGER ||
                        u.getRole() == Role.PROJECT_LEAD ||
                        u.getRole() == Role.CEO ||
                        u.getRole() == Role.ADMIN)
                .collect(Collectors.toList()));
        model.addAttribute("currentUser", currentUser);
        return "projects/create";
    }

    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute ProjectDTO projectDTO,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        // Check if user has permission
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        // Validate dates
        if (projectDTO.getStartDate() != null && projectDTO.getEndDate() != null &&
                projectDTO.getEndDate().isBefore(projectDTO.getStartDate())) {
            result.rejectValue("endDate", "error.projectDTO", "End date must be after start date");
        }

        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("priorities", ProjectPriority.values());
            model.addAttribute("users", userService.getActiveUsers());
            model.addAttribute("managers", userService.getActiveUsers().stream()
                    .filter(u -> u.getRole() == Role.MANAGER ||
                            u.getRole() == Role.PROJECT_LEAD ||
                            u.getRole() == Role.CEO ||
                            u.getRole() == Role.ADMIN)
                    .collect(Collectors.toList()));
            model.addAttribute("currentUser", currentUser);
            return "projects/create";
        }

        try {
            projectService.createProject(projectDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Project created successfully!");
            return "redirect:/projects";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("priorities", ProjectPriority.values());
            model.addAttribute("users", userService.getActiveUsers());
            model.addAttribute("managers", userService.getActiveUsers().stream()
                    .filter(u -> u.getRole() == Role.MANAGER ||
                            u.getRole() == Role.PROJECT_LEAD ||
                            u.getRole() == Role.CEO ||
                            u.getRole() == Role.ADMIN)
                    .collect(Collectors.toList()));
            model.addAttribute("currentUser", currentUser);
            return "projects/create";
        }
    }

    @GetMapping("/view/{id}")
    public String viewProject(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectByIdWithUsers(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Task> tasks = taskService.getTasksByProject(project);
        long todoTasks = taskService.countTasksByProjectAndStatus(project, TaskStatus.TODO);
        long inProgressTasks = taskService.countTasksByProjectAndStatus(project, TaskStatus.IN_PROGRESS);
        long completedTasks = taskService.countTasksByProjectAndStatus(project, TaskStatus.COMPLETED);


        model.addAttribute("project", project);
        model.addAttribute("currentUser", userDetails.getUser());
        return "projects/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        // Check if user has permission
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        Project project = projectService.getProjectByIdWithUsers(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setProjectName(project.getProjectName());
        projectDTO.setProjectCode(project.getProjectCode());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setProjectManagerId(project.getProjectManager() != null ? project.getProjectManager().getId() : null);
        projectDTO.setStatus(project.getStatus());
        projectDTO.setPriority(project.getPriority());
        projectDTO.setStartDate(project.getStartDate());
        projectDTO.setEndDate(project.getEndDate());
        projectDTO.setBudget(project.getBudget());
        projectDTO.setClient(project.getClient());
        projectDTO.setDepartment(project.getDepartment());
        projectDTO.setCompletionPercentage(project.getCompletionPercentage());
        projectDTO.setAssignedUserIds(project.getAssignedUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        model.addAttribute("projectDTO", projectDTO);
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("priorities", ProjectPriority.values());
        model.addAttribute("users", userService.getActiveUsers());
        model.addAttribute("managers", userService.getActiveUsers().stream()
                .filter(u -> u.getRole() == Role.MANAGER ||
                        u.getRole() == Role.PROJECT_LEAD ||
                        u.getRole() == Role.CEO ||
                        u.getRole() == Role.ADMIN)
                .collect(Collectors.toList()));
        model.addAttribute("currentUser", currentUser);
        return "projects/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute ProjectDTO projectDTO,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        // Check if user has permission
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        // Validate dates
        if (projectDTO.getStartDate() != null && projectDTO.getEndDate() != null &&
                projectDTO.getEndDate().isBefore(projectDTO.getStartDate())) {
            result.rejectValue("endDate", "error.projectDTO", "End date must be after start date");
        }

        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("priorities", ProjectPriority.values());
            model.addAttribute("users", userService.getActiveUsers());
            model.addAttribute("managers", userService.getActiveUsers().stream()
                    .filter(u -> u.getRole() == Role.MANAGER ||
                            u.getRole() == Role.PROJECT_LEAD ||
                            u.getRole() == Role.CEO ||
                            u.getRole() == Role.ADMIN)
                    .collect(Collectors.toList()));
            model.addAttribute("currentUser", currentUser);
            return "projects/edit";
        }

        try {
            projectService.updateProject(id, projectDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Project updated successfully!");
            return "redirect:/projects";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("priorities", ProjectPriority.values());
            model.addAttribute("users", userService.getActiveUsers());
            model.addAttribute("managers", userService.getActiveUsers().stream()
                    .filter(u -> u.getRole() == Role.MANAGER ||
                            u.getRole() == Role.PROJECT_LEAD ||
                            u.getRole() == Role.CEO ||
                            u.getRole() == Role.ADMIN)
                    .collect(Collectors.toList()));
            model.addAttribute("currentUser", currentUser);
            return "projects/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        // Check if user has permission
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO) {
            return "redirect:/access-denied";
        }

        try {
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projects";
    }
}