package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.ProjectTaskDTO;
import com.pmt.ProjectManagement.entity.ProjectTask;
import com.pmt.ProjectManagement.service.ProjectTaskService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    // List all tasks
    @GetMapping
    public String listTasks(Model model) {
        List<ProjectTask> tasks = projectTaskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks/list";  // Thymeleaf template: src/main/resources/templates/tasks/list.html
    }

    // Show form to create a new task
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("projectTaskDTO", new ProjectTaskDTO());
        return "tasks/create"; // Thymeleaf template: tasks/create.html
    }

    // Handle form submission to create new task
    @PostMapping
    public String createTask(@Valid @ModelAttribute("projectTaskDTO") ProjectTaskDTO dto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "tasks/create";
        }
        try {
            projectTaskService.createTask(dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "tasks/create";
        }
        return "redirect:/tasks";
    }

    // Show form to edit existing task
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProjectTask task = projectTaskService.getTaskById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        // Convert entity to DTO for the form
        ProjectTaskDTO dto = mapToDTO(task);
        model.addAttribute("projectTaskDTO", dto);
        return "tasks/edit"; // Thymeleaf template: tasks/edit.html
    }

    // Handle form submission to update existing task
    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("projectTaskDTO") ProjectTaskDTO dto,
                             BindingResult result, Model model) {
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
        projectTaskService.deleteTask(id);
        return "redirect:/tasks";
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
