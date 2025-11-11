package com.pmt.ProjectManagement.service;

import com.pmt.ProjectManagement.dto.ProjectTaskDTO;
import com.pmt.ProjectManagement.entity.ProjectTask;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.repository.ProjectTaskRepository;
import com.pmt.ProjectManagement.repository.ProjectRepository;
import com.pmt.ProjectManagement.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ProjectTask> getAllTasks() {
        return projectTaskRepository.findAll();
    }

    public List<ProjectTask> getTasksByProjectId(Long projectID) {
        return projectTaskRepository.findByProjectId(projectID);
    }

    public Optional<ProjectTask> getTaskById(Long id) {
        return projectTaskRepository.findById(id);
    }

    public ProjectTask createTask(ProjectTaskDTO dto) {
        ProjectTask task = new ProjectTask();

        task.setTaskName(dto.getTaskName());
        task.setDescription(dto.getDescription());

        // Fetch Project entity
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        task.setProject(project);

        // Fetch assigned user if present
        if (dto.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));
            task.setAssignedUser(assignedUser);
        } else {
            task.setAssignedUser(null);
        }

        task.setStartDate(dto.getStartDate());
        task.setDueDate(dto.getDueDate());

        if (dto.getCompletionPercentage() != null) {
            task.setCompletionPercentage(dto.getCompletionPercentage());
        } else {
            task.setCompletionPercentage(0);
        }

        // Fetch createdBy user if present
        if (dto.getCreatedById() != null) {
            User createdBy = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new IllegalArgumentException("CreatedBy user not found"));
            task.setCreatedBy(createdBy);
        }

        return projectTaskRepository.save(task);
    }

    public ProjectTask updateTask(Long id, ProjectTaskDTO dto) {
        ProjectTask task = projectTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTaskName(dto.getTaskName());
        task.setDescription(dto.getDescription());

        // Update project if different
        if (!task.getProject().getId().equals(dto.getProjectId())) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found"));
            task.setProject(project);
        }

        // Update assigned user
        if (dto.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));
            task.setAssignedUser(assignedUser);
        } else {
            task.setAssignedUser(null);
        }

        task.setStartDate(dto.getStartDate());
        task.setDueDate(dto.getDueDate());

        if (dto.getCompletionPercentage() != null) {
            task.setCompletionPercentage(dto.getCompletionPercentage());
        }

        // Usually createdBy is not updated after creation

        return projectTaskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!projectTaskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task not found");
        }
        projectTaskRepository.deleteById(id);
    }

    // Additional methods as needed, e.g. find by project, find overdue, etc.

    // ProjectTaskService method to update completion percentage
    public void updateCompletionPercentage(Long taskId, Integer completionPercentage) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        task.setCompletionPercentage(completionPercentage);
        projectTaskRepository.save(task);
    }

    public Long getProjectIdByTaskId(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        return task.getProject().getId();
    }



}
