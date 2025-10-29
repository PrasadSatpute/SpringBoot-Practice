package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.ProjectTaskDTO;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.ProjectTask;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.ProjectService;
import com.pmt.ProjectManagement.service.ProjectTaskService;

import com.pmt.ProjectManagement.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private ProjectService projectService;

    // List all tasks
    @GetMapping("/create/{id}")
    public String listTasks(@PathVariable Long id,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        System.out.println("------------List of TASKS ------------");

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        List<ProjectTask> tasks = projectTaskService.getTasksByProjectId(id);

        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("tasks", tasks);
        model.addAttribute("project", project); // send project to view
        model.addAttribute("users", project.getAssignedUsers()); // send assigned users to view

        System.out.println("USERS = "+project.getAssignedUsers());

        return "tasks/list";
    }

    // Controller Method to handle completion percentage update
    @PostMapping("/updateCompletion/{id}")
    @ResponseBody
    public Map<String, Object> updateTaskCompletion(@PathVariable Long id,
                                                    @RequestParam("completionPercentage") Integer completionPercentage) {

        Map<String, Object> response = new HashMap<>();

        if (completionPercentage < 0 || completionPercentage > 100) {
            response.put("error", "Completion percentage must be between 0 and 100.");
            return response;
        }

        try {
            projectTaskService.updateCompletionPercentage(id, completionPercentage);
        } catch (IllegalArgumentException ex) {
            response.put("error", ex.getMessage());
            return response;
        }

        Long projectId = projectTaskService.getProjectIdByTaskId(id);
        response.put("redirectUrl", "/tasks/create/" + projectId);
        return response;
    }


//    @PostMapping("/updateCompletion/{taskId}")
//    @ResponseBody
//    public Map<String, Object> updateTaskCompletionAjax(@PathVariable Long taskId,
//                                                        @RequestParam("completionPercentage") Integer completionPercentage) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            projectTaskService.updateCompletionPercentage(taskId, completionPercentage);
//            Long projectId = projectTaskService.getProjectIdByTaskId(taskId);
//            response.put("redirectUrl", "/tasks/create/" + projectId);
//            response.put("status", "success");
//        } catch (Exception e) {
//            response.put("status", "error");
//            response.put("message", e.getMessage());
//        }
//        return response;
//    }





    // Show form to create a new task
    @GetMapping("/new/{id}")
    public String showCreateForm(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {
        System.out.println("------------Create of TASKS ------------");
        model.addAttribute("projectTaskDTO", new ProjectTaskDTO());
        model.addAttribute("projects", projectService.getAllProjects()); // Add this
        model.addAttribute("currentUser", userDetails.getUser());          // And this

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        model.addAttribute("users", project.getAssignedUsers()); // send assigned users to view
        model.addAttribute("assignProject",project);

        return "tasks/create";
    }


    // Handle form submission to create new task
    @PostMapping("createtask/{id}")
    public String createTask(@PathVariable Long id,@Valid @ModelAttribute("projectTaskDTO") ProjectTaskDTO dto,
                             BindingResult result, Model model) {
        System.out.println("-----Create Task 2----------");
        if (result.hasErrors()) {
            return "tasks/create";
        }
        try {
            projectTaskService.createTask(dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "tasks/create";
        }

//        Long projectId = projectTaskService.getProjectIdByTaskId(id);
        return "redirect:/tasks/create/" + id;

//        return "redirect:/tasks/create/{id}";
    }

    // Show form to edit existing task
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        System.out.println("-----Edit Task ----------");

        ProjectTask task = projectTaskService.getTaskById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        // Convert entity to DTO for the form
        ProjectTaskDTO dto = mapToDTO(task);
        model.addAttribute("taskDTO", dto);
        model.addAttribute("currentUser", userDetails.getUser());

        Project project = projectService.getProjectById(task.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        model.addAttribute("users", project.getAssignedUsers());

        return "tasks/edit"; // Thymeleaf template: tasks/edit.html
    }

    // Handle form submission to update existing task
    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("projectTaskDTO") ProjectTaskDTO dto,
                             BindingResult result, Model model) {

        System.out.println("-----Update Task ----------");
        if (result.hasErrors()) {
            return "tasks/edit";
        }
        try {
            projectTaskService.updateTask(id, dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "tasks/edit";
        }
        return "redirect:/tasks";
    }

    // Delete a task
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        Long projectId = projectTaskService.getProjectIdByTaskId(id);
        projectTaskService.deleteTask(id);
        return "redirect:/tasks/create/" + projectId;

//        return "redirect:/tasks";
    }

    // Helper method to map entity to DTO for editing
    private ProjectTaskDTO mapToDTO(ProjectTask task) {
        Long assignedUserId = task.getAssignedUser() != null ? task.getAssignedUser().getId() : null;
        Long createdById = task.getCreatedBy() != null ? task.getCreatedBy().getId() : null;

        return new ProjectTaskDTO(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getProject().getId(),
                assignedUserId,
                task.getStartDate(),
                task.getDueDate(),
                task.getCompletionPercentage(),
                createdById
        );
    }
}
