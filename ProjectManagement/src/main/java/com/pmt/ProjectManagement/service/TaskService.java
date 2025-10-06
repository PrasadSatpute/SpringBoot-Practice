package com.pmt.ProjectManagement.service;

import com.pmt.ProjectManagement.dto.TaskDTO;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.Task;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.TaskStatus;
import com.pmt.ProjectManagement.repository.ProjectRepository;
import com.pmt.ProjectManagement.repository.TaskRepository;
import com.pmt.ProjectManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Constructor injection
    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(TaskDTO taskDTO, User createdBy) {
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setDescription(taskDTO.getDescription());
        task.setProject(project);
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setStartDate(taskDTO.getStartDate());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompletionPercentage(taskDTO.getCompletionPercentage());
        task.setComments(taskDTO.getComments());
        task.setCreatedBy(createdBy);

        if (taskDTO.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignedUser);
        }

        Task savedTask = taskRepository.save(task);
        updateProjectProgress(project);
        return savedTask;
    }

    @Transactional
    public Task updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTaskName(taskDTO.getTaskName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setStartDate(taskDTO.getStartDate());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompletionPercentage(taskDTO.getCompletionPercentage());
        task.setComments(taskDTO.getComments());

        if (taskDTO.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignedUser);
        } else {
            task.setAssignedTo(null);
        }

        // Auto-complete task if completion is 100%
        if (task.getCompletionPercentage() == 100 && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.COMPLETED);
        }

        Task updatedTask = taskRepository.save(task);
        updateProjectProgress(task.getProject());
        return updatedTask;
    }

    @Transactional
    public void updateProjectProgress(Project project) {
        Double avgCompletion = taskRepository.getAverageCompletionByProject(project);
        if (avgCompletion != null) {
            project.setCompletionPercentage(avgCompletion.intValue());
            projectRepository.save(project);
        }
    }

    public List<Task> getTasksByProject(Project project) {
        return taskRepository.findByProjectOrderByCreatedAtDesc(project);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public long countTasksByProjectAndStatus(Project project, TaskStatus status) {
        return taskRepository.countByProjectAndStatus(project, status);
    }

    public List<Task> getOverdueTasksByProject(Project project) {
        return taskRepository.findOverdueTasksByProject(project);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Project project = task.getProject();
        taskRepository.delete(task);
        updateProjectProgress(project);
    }
}