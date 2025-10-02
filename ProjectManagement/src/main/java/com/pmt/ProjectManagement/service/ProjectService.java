package com.pmt.ProjectManagement.service;

import com.pmt.ProjectManagement.dto.ProjectDTO;
import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.ProjectStatus;
import com.pmt.ProjectManagement.repository.ProjectRepository;
import com.pmt.ProjectManagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Project createProject(ProjectDTO projectDTO, User createdBy) {
        if (projectRepository.existsByProjectCode(projectDTO.getProjectCode())) {
            throw new RuntimeException("Project code already exists");
        }

        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectCode(projectDTO.getProjectCode());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setPriority(projectDTO.getPriority());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setBudget(projectDTO.getBudget());
        project.setClient(projectDTO.getClient());
        project.setDepartment(projectDTO.getDepartment());
        project.setCompletionPercentage(projectDTO.getCompletionPercentage());
        project.setCreatedBy(createdBy);

        // Set project manager
        if (projectDTO.getProjectManagerId() != null) {
            User manager = userRepository.findById(projectDTO.getProjectManagerId())
                    .orElseThrow(() -> new RuntimeException("Project manager not found"));
            project.setProjectManager(manager);
        }

        // Assign users
        if (projectDTO.getAssignedUserIds() != null && !projectDTO.getAssignedUserIds().isEmpty()) {
            Set<User> users = new HashSet<>();
            for (Long userId : projectDTO.getAssignedUserIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                users.add(user);
            }
            project.setAssignedUsers(users);
        }

        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setProjectName(projectDTO.getProjectName());

        if (!project.getProjectCode().equals(projectDTO.getProjectCode()) &&
                projectRepository.existsByProjectCode(projectDTO.getProjectCode())) {
            throw new RuntimeException("Project code already exists");
        }

        project.setProjectCode(projectDTO.getProjectCode());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setPriority(projectDTO.getPriority());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setBudget(projectDTO.getBudget());
        project.setClient(projectDTO.getClient());
        project.setDepartment(projectDTO.getDepartment());
        project.setCompletionPercentage(projectDTO.getCompletionPercentage());

        // Update project manager
        if (projectDTO.getProjectManagerId() != null) {
            User manager = userRepository.findById(projectDTO.getProjectManagerId())
                    .orElseThrow(() -> new RuntimeException("Project manager not found"));
            project.setProjectManager(manager);
        } else {
            project.setProjectManager(null);
        }

        // Update assigned users
        project.getAssignedUsers().clear();
        if (projectDTO.getAssignedUserIds() != null && !projectDTO.getAssignedUserIds().isEmpty()) {
            for (Long userId : projectDTO.getAssignedUserIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                project.getAssignedUsers().add(user);
            }
        }

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByIdWithUsers(Long id) {
        return projectRepository.findByIdWithUsers(id);
    }

    public List<Project> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getProjectsByManager(User manager) {
        return projectRepository.findByProjectManager(manager);
    }

    public List<Project> getProjectsByAssignedUser(User user) {
        return projectRepository.findByAssignedUser(user);
    }

    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects();
    }

    public long countProjectsByStatus(ProjectStatus status) {
        return projectRepository.countByStatus(status);
    }

    public long getTotalProjects() {
        return projectRepository.count();
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }
}