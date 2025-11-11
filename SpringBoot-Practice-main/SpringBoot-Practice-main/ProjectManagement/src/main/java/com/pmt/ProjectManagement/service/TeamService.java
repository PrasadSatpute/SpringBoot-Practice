package com.pmt.ProjectManagement.service;

import com.pmt.ProjectManagement.dto.TeamDTO;
import com.pmt.ProjectManagement.entity.Team;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.repository.TeamRepository;
import com.pmt.ProjectManagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamDTO createTeam(TeamDTO teamDTO) {
        if (teamRepository.existsByName(teamDTO.getName())) {
            throw new IllegalArgumentException("A team with this name already exists.");
        }

        User teamLead = userRepository.findById(teamDTO.getTeamLeadId())
                .orElseThrow(() -> new EntityNotFoundException("Team lead not found with ID: " + teamDTO.getTeamLeadId()));

        Set<User> members = new HashSet<>();
        if (teamDTO.getMemberIds() != null) {
            members = userRepository.findAllById(teamDTO.getMemberIds())
                    .stream()
                    .collect(Collectors.toSet());
        }

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setTeamLead(teamLead);
        team.setMembers(members);

        Team savedTeam = teamRepository.save(team);
        return mapToDTO(savedTeam);
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with ID: " + id));
        return mapToDTO(team);
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with ID: " + id));

        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());

        User teamLead = userRepository.findById(teamDTO.getTeamLeadId())
                .orElseThrow(() -> new EntityNotFoundException("Team lead not found with ID: " + teamDTO.getTeamLeadId()));
        team.setTeamLead(teamLead);

        Set<User> members = userRepository.findAllById(teamDTO.getMemberIds())
                .stream()
                .collect(Collectors.toSet());
        team.setMembers(members);

        Team updatedTeam = teamRepository.save(team);
        return mapToDTO(updatedTeam);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found with ID: " + id);
        }
        teamRepository.deleteById(id);
    }

//    private TeamDTO mapToDTO(Team team) {
//        TeamDTO dto = new TeamDTO();
//        dto.setId(team.getId());
//        dto.setName(team.getName());
//        dto.setDescription(team.getDescription());
//        dto.setTeamLeadId(team.getTeamLead().getId());
//        dto.setMemberIds(
//                team.getMembers().stream()
//                        .map(User::getId)
//                        .collect(Collectors.toSet())
//        );
//        dto.setCreatedAt(team.getCreatedAt().toString());
//        dto.setUpdatedAt(team.getUpdatedAt().toString());
//        return dto;
//    }

    public TeamDTO mapToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setTeamLeadId(team.getTeamLead() != null ? team.getTeamLead().getId() : null);
        dto.setTeamLeadName(team.getTeamLead() != null
                ? team.getTeamLead().getFirstName() + " " + team.getTeamLead().getLastName()
                : "N/A");


        List<String> memberNames = team.getMembers().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());
        dto.setMemberNames(memberNames);

        return dto;
    }

}
