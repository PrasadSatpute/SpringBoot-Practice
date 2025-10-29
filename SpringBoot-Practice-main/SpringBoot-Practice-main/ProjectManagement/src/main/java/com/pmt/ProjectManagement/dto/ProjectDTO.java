package com.pmt.ProjectManagement.dto;

import com.pmt.ProjectManagement.enums.ProjectPriority;
import com.pmt.ProjectManagement.enums.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String projectName;

    @NotBlank(message = "Project code is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Project code must contain only uppercase letters, numbers, and hyphens")
    @Size(min = 3, max = 20, message = "Project code must be between 3 and 20 characters")
    private String projectCode;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    private Long projectManagerId;

    @NotNull(message = "Status is required")
    private ProjectStatus status;

    @NotNull(message = "Priority is required")
    private ProjectPriority priority;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
    private BigDecimal budget;

    @Size(max = 50, message = "Client name cannot exceed 50 characters")
    private String client;

    @Size(max = 100, message = "Department name cannot exceed 100 characters")
    private String department;

    private Set<Long> assignedUserIds = new HashSet<>();

    @Min(value = 0, message = "Completion percentage must be at least 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private Integer completionPercentage = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Project name is required") @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters") String getProjectName() {
        return projectName;
    }

    public void setProjectName(@NotBlank(message = "Project name is required") @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters") String projectName) {
        this.projectName = projectName;
    }

    public @NotBlank(message = "Project code is required") @Pattern(regexp = "^[A-Z0-9-]+$", message = "Project code must contain only uppercase letters, numbers, and hyphens") @Size(min = 3, max = 20, message = "Project code must be between 3 and 20 characters") String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(@NotBlank(message = "Project code is required") @Pattern(regexp = "^[A-Z0-9-]+$", message = "Project code must contain only uppercase letters, numbers, and hyphens") @Size(min = 3, max = 20, message = "Project code must be between 3 and 20 characters") String projectCode) {
        this.projectCode = projectCode;
    }

    public @Size(max = 5000, message = "Description cannot exceed 5000 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 5000, message = "Description cannot exceed 5000 characters") String description) {
        this.description = description;
    }

    public Long getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(Long projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public @NotNull(message = "Status is required") ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") ProjectStatus status) {
        this.status = status;
    }

    public @NotNull(message = "Priority is required") ProjectPriority getPriority() {
        return priority;
    }

    public void setPriority(@NotNull(message = "Priority is required") ProjectPriority priority) {
        this.priority = priority;
    }

    public @NotNull(message = "Start date is required") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "End date is required") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "End date is required") LocalDate endDate) {
        this.endDate = endDate;
    }

    public @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0") BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(@DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0") BigDecimal budget) {
        this.budget = budget;
    }

    public @Size(max = 50, message = "Client name cannot exceed 50 characters") String getClient() {
        return client;
    }

    public void setClient(@Size(max = 50, message = "Client name cannot exceed 50 characters") String client) {
        this.client = client;
    }

    public @Size(max = 100, message = "Department name cannot exceed 100 characters") String getDepartment() {
        return department;
    }

    public void setDepartment(@Size(max = 100, message = "Department name cannot exceed 100 characters") String department) {
        this.department = department;
    }

    public Set<Long> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(Set<Long> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }

    public @Min(value = 0, message = "Completion percentage must be at least 0") @Max(value = 100, message = "Completion percentage cannot exceed 100") Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(@Min(value = 0, message = "Completion percentage must be at least 0") @Max(value = 100, message = "Completion percentage cannot exceed 100") Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}