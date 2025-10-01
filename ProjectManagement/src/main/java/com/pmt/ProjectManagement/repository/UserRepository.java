package com.pmt.ProjectManagement.repository;

import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByActiveTrue();

    boolean existsByEmail(String email);
}