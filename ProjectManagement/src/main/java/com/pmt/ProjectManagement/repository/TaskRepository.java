package com.pmt.ProjectManagement.repository;

import com.pmt.ProjectManagement.entity.Project;
import com.pmt.ProjectManagement.entity.Task;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {



    List<Task> findByProject(Project project);

    List<Task> findByProjectOrderByCreatedAtDesc(Project project);

    List<Task> findByAssignedTo(User user);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByProjectAndAssignedTo(Project project, User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project = :project AND t.status = :status")
    long countByProjectAndStatus(Project project, TaskStatus status);

    @Query("SELECT AVG(t.completionPercentage) FROM Task t WHERE t.project = :project")
    Double getAverageCompletionByProject(Project project);

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks();

    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksByProject(Project project);
}