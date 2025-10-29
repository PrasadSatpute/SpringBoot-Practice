package com.pmt.ProjectManagement.repository;

import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);

    boolean existsByProjectCode(String projectCode);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByProjectManager(User projectManager);

    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.assignedUsers")
    List<Project> findByAssignedUser(User user);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.endDate < CURRENT_DATE AND p.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Project> findOverdueProjects();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.assignedUsers WHERE p.id = :id")
    Optional<Project> findByIdWithUsers(Long id);
}