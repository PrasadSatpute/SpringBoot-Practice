package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.TaskDTO;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.Task;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.Role;
import com.pmt.ProjectManagement.enums.TaskPriority;
import com.pmt.ProjectManagement.enums.TaskStatus;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.ProjectService;
import com.pmt.ProjectManagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping("/create")
    public String showCreateForm(@PathVariable Long projectId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        // Check authorization
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        Project project = projectService.getProjectByIdWithUsers(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(projectId);
        taskDTO.setStatus(TaskStatus.TODO);
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setCompletionPercentage(0);

        model.addAttribute("taskDTO", taskDTO);
        model.addAttribute("project", project);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("projectMembers", project.getAssignedUsers());
        model.addAttribute("currentUser", currentUser);
        return "tasks/create";
    }

    @PostMapping("/create")
    public String createTask(@PathVariable Long projectId,
                             @Valid @ModelAttribute TaskDTO taskDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        Project project = projectService.getProjectByIdWithUsers(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (taskDTO.getStartDate() != null && taskDTO.getDueDate() != null &&
                taskDTO.getDueDate().isBefore(taskDTO.getStartDate())) {
            result.rejectValue("dueDate", "error.taskDTO", "Due date must be after start date");
        }

        if (result.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projectMembers", project.getAssignedUsers());
            model.addAttribute("currentUser", currentUser);
            return "tasks/create";
        }

        try {
            taskService.createTask(taskDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");
            return "redirect:/projects/view/" + projectId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("project", project);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projectMembers", project.getAssignedUsers());
            model.addAttribute("currentUser", currentUser);
            return "tasks/create";
        }
    }

    @GetMapping("/{taskId}")
    public String viewTask(@PathVariable Long projectId,
                           @PathVariable Long taskId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        model.addAttribute("task", task);
        model.addAttribute("currentUser", userDetails.getUser());
        return "tasks/view";
    }

    @GetMapping("/{taskId}/edit")
    public String showEditForm(@PathVariable Long projectId,
                               @PathVariable Long taskId,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = task.getProject();

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTaskName(task.getTaskName());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setProjectId(project.getId());
        taskDTO.setAssignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null);
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setStartDate(task.getStartDate());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setCompletionPercentage(task.getCompletionPercentage());
        taskDTO.setComments(task.getComments());

        model.addAttribute("taskDTO", taskDTO);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("projectMembers", project.getAssignedUsers());
        model.addAttribute("currentUser", userDetails.getUser());
        return "tasks/edit";
    }

    @PostMapping("/{taskId}/edit")
    public String updateTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @Valid @ModelAttribute TaskDTO taskDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = task.getProject();

        if (taskDTO.getStartDate() != null && taskDTO.getDueDate() != null &&
                taskDTO.getDueDate().isBefore(taskDTO.getStartDate())) {
            result.rejectValue("dueDate", "error.taskDTO", "Due date must be after start date");
        }

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("project", project);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projectMembers", project.getAssignedUsers());
            model.addAttribute("currentUser", userDetails.getUser());
            return "tasks/edit";
        }

        try {
            taskService.updateTask(taskId, taskDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
            return "redirect:/projects/" + projectId + "/tasks/" + taskId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("task", task);
            model.addAttribute("project", project);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projectMembers", project.getAssignedUsers());
            model.addAttribute("currentUser", userDetails.getUser());
            return "tasks/edit";
        }
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userDetails.getUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.CEO &&
                currentUser.getRole() != Role.MANAGER) {
            return "redirect:/access-denied";
        }

        try {
            taskService.deleteTask(taskId);
            redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projects/view/" + projectId;
    }
}