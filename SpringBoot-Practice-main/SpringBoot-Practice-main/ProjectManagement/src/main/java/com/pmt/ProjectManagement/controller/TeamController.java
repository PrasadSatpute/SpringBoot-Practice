package com.pmt.ProjectManagement.controller;
import com.pmt.ProjectManagement.dto.TeamDTO;
import com.pmt.ProjectManagement.entity.Team;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.ProjectStatus;
import com.pmt.ProjectManagement.enums.Role;
import com.pmt.ProjectManagement.repository.TeamRepository;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.TeamService;
import com.pmt.ProjectManagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

//    @GetMapping
//    public String listTeams(Model model) {
//        List<TeamDTO> teams = teamService.getAllTeams();
//        model.addAttribute("teams", teams);
//        return "team/list"; // src/main/resources/templates/team/list.html
//    }

//    @GetMapping
//    public String listTeams(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
//        // Fetch the logged-in user from the database using email or username
//        User currentUser = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        List<TeamDTO> teams;
//
//        if (isAdminOrManager(currentUser)) {
//            // Show all teams
//            teams = teamService.getAllTeams();
//        } else {
//            // Show only teams the user is a member of or leads
//            List<Team> userTeams = teamRepository.findByMembers_IdOrTeamLead_Id(currentUser.getId(), currentUser.getId());
//            teams = userTeams.stream()
//                    .map(teamService::mapToDTO)
//                    .collect(Collectors.toList());
//        }
//
//        model.addAttribute("teams", teams);
//        model.addAttribute("currentUser", userDetails.getUser());
//        return "team/list";
//    }

    @GetMapping
    public String listTeams(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TeamDTO> teams;
        if (isAdminOrManager(currentUser)) {
            teams = teamService.getAllTeams();
        } else {
            List<Team> userTeams = teamRepository.findByMembers_IdOrTeamLead_Id(currentUser.getId(), currentUser.getId());
            teams = userTeams.stream()
                    .map(teamService::mapToDTO)
                    .collect(Collectors.toList());
        }

        List<User> allUsers = userRepository.findAll(); // You may want to filter by active = true

        model.addAttribute("teams", teams);
        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("users", allUsers); // 👈 Add this



        return "team/list";
    }


    private boolean isAdminOrManager(User user) {
        Role role = user.getRole();
        return role == Role.ADMIN || role == Role.CEO || role == Role.MANAGER;
    }


    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("team", new TeamDTO());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUser", userDetails.getUser());
        return "team/create"; // src/main/resources/templates/team/create.html
    }

    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("team") TeamDTO teamDTO,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "team/create";
        }
        teamService.createTeam(teamDTO);
        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        TeamDTO teamDTO = teamService.getTeamById(id);
        model.addAttribute("team", teamDTO);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUser", userDetails.getUser());
        return "team/edit"; // src/main/resources/templates/team/edit.html
    }

    @PostMapping("/edit/{id}")
    public String updateTeam(@PathVariable Long id,
                             @Valid @ModelAttribute("team") TeamDTO teamDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "team/edit";
        }
        teamService.updateTeam(id, teamDTO);
        return "redirect:/teams";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}
