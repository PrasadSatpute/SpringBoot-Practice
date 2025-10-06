package com.pmt.ProjectManagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class ProjectTaskDTO {

    private Long id;

    @NotBlank(message = "Task name is required")
    @Size(min = 3, max = 100, message = "Task name must be between 3 and 100 characters")
    private String taskName;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assignedUserId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Min(value = 0, message = "Completion percentage must be at least 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private Integer completionPercentage = 0;

    private Long createdById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Task name is required") @Size(min = 3, max = 100, message = "Task name must be between 3 and 100 characters") String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NotBlank(message = "Task name is required") @Size(min = 3, max = 100, message = "Task name must be between 3 and 100 characters") String taskName) {
        this.taskName = taskName;
    }

    public @Size(max = 2000, message = "Description cannot exceed 2000 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 2000, message = "Description cannot exceed 2000 characters") String description) {
        this.description = description;
    }

    public @NotNull(message = "Project ID is required") Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull(message = "Project ID is required") Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public ProjectTaskDTO() {
    }

    public ProjectTaskDTO(Long id, String taskName, String description, Long projectId, Long assignedUserId, LocalDate startDate, LocalDate dueDate, Integer completionPercentage, Long createdById) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.completionPercentage = completionPercentage;
        this.createdById = createdById;
    }
}
