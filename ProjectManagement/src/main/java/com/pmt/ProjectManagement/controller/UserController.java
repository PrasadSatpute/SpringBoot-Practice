package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.UserDTO;
import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.Gender;
import com.pmt.ProjectManagement.enums.Role;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping
    public String listUsers(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", userDetails.getUser());
        return "users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("roles", Role.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("currentUser", userDetails.getUser());
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute UserDTO userDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("currentUser", userDetails.getUser());
            return "users/create";
        }

        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("currentUser", userDetails.getUser());
            return "users/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setGender(user.getGender());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setRole(user.getRole());
        userDTO.setActive(user.getActive());

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("roles", Role.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("currentUser", userDetails.getUser());
        return "users/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute UserDTO userDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("currentUser", userDetails.getUser());
            return "users/edit";
        }

        try {
            userService.updateUser(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("currentUser", userDetails.getUser());
            return "users/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}