package com.pmt.ProjectManagement.dto;

import com.pmt.ProjectManagement.enums.TaskPriority;
import com.pmt.ProjectManagement.enums.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Task name is required")
    @Size(min = 3, max = 200, message = "Task name must be between 3 and 200 characters")
    private String taskName;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Project is required")
    private Long projectId;

    private Long assignedToId;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Min(value = 0, message = "Completion percentage must be at least 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private Integer completionPercentage = 0;

    private String comments;

    public TaskDTO(Long id, String taskName, String description, Long projectId, Long assignedToId, TaskStatus status, TaskPriority priority, LocalDate startDate, LocalDate dueDate, Integer completionPercentage, String comments) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.projectId = projectId;
        this.assignedToId = assignedToId;
        this.status = status;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.completionPercentage = completionPercentage;
        this.comments = comments;
    }

    public TaskDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Task name is required") @Size(min = 3, max = 200, message = "Task name must be between 3 and 200 characters") String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NotBlank(message = "Task name is required") @Size(min = 3, max = 200, message = "Task name must be between 3 and 200 characters") String taskName) {
        this.taskName = taskName;
    }

    public @Size(max = 2000, message = "Description cannot exceed 2000 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 2000, message = "Description cannot exceed 2000 characters") String description) {
        this.description = description;
    }

    public @NotNull(message = "Project is required") Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull(message = "Project is required") Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public @NotNull(message = "Status is required") TaskStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") TaskStatus status) {
        this.status = status;
    }

    public @NotNull(message = "Priority is required") TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(@NotNull(message = "Priority is required") TaskPriority priority) {
        this.priority = priority;
    }

    public @NotNull(message = "Start date is required") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "Due date is required") LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(@NotNull(message = "Due date is required") LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public @Min(value = 0, message = "Completion percentage must be at least 0") @Max(value = 100, message = "Completion percentage cannot exceed 100") Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(@Min(value = 0, message = "Completion percentage must be at least 0") @Max(value = 100, message = "Completion percentage cannot exceed 100") Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}