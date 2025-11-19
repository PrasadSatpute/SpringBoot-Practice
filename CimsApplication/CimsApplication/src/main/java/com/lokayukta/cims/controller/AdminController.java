package com.lokayukta.cims.controller;

import com.lokayukta.cims.dto.UserRegistrationDTO;
import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user-list";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("userDTO", new UserRegistrationDTO());
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("sections", User.Section.values());
        return "admin/create-user";
    }

    @PostMapping("/users/create")
    public String createUser(
            @Valid @ModelAttribute("userDTO") UserRegistrationDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("sections", User.Section.values());
            return "admin/create-user";
        }

        try {
            userService.createUser(dto);
            redirectAttributes.addFlashAttribute("success",
                    "User created successfully");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("sections", User.Section.values());
            return "admin/create-user";
        }
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "User deactivated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "User activated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}