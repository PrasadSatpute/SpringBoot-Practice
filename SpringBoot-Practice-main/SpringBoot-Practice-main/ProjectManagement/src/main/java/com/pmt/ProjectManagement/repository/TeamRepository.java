package com.pmt.ProjectManagement.repository;

import com.pmt.ProjectManagement.entity.Team;
import com.pmt.ProjectManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    boolean existsByName(String name);

//    Optional<User> findByEmail(String email);

    List<Team> findByMembers_IdOrTeamLead_Id(Long memberId, Long teamLeadId);

}
