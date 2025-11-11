package com.pmt.ProjectManagement.repository;

import com.pmt.ProjectManagement.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    // Example: Find all tasks by project ID
    List<ProjectTask> findByProjectId(Long projectId);

    // Example: Find tasks assigned to a particular user
    List<ProjectTask> findByAssignedUserId(Long assignedUserId);

}
