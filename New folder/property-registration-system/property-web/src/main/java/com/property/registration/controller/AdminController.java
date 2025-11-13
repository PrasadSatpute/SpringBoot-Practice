package com.property.registration.controller;

import com.property.registration.entity.ULB;
import com.property.registration.entity.User;
import com.property.registration.entity.Role;
import com.property.registration.service.ULBService;
import com.property.registration.service.UserService;
import com.property.registration.service.PropertyApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ULBService ulbService;
    private final UserService userService;
    private final PropertyApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalULBs", ulbService.getAllULBs().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        return "admin/dashboard";
    }

    @GetMapping("/ulbs")
    public String viewAllULBs(Model model) {
        model.addAttribute("ulbs", ulbService.getAllULBs());
        return "admin/ulbs";
    }

    @GetMapping("/ulb/new")
    public String newULBForm(Model model) {
        model.addAttribute("ulb", new ULB());
        return "admin/new-ulb";
    }

    @PostMapping("/ulb/create")
    public String createULB(@ModelAttribute ULB ulb, Model model) {
        try {
            ulbService.createULB(ulb);
            return "redirect:/admin/ulbs?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/new-ulb";
        }
    }

    @GetMapping("/ulb/edit/{id}")
    public String editULBForm(@PathVariable Long id, Model model) {
        try {
            ULB ulb = ulbService.getULBById(id);
            model.addAttribute("ulb", ulb);
            return "admin/edit-ulb";
        } catch (Exception e) {
            return "redirect:/admin/ulbs?error=notfound";
        }
    }

    @PostMapping("/ulb/update/{id}")
    public String updateULB(@PathVariable Long id, @ModelAttribute ULB ulb, Model model) {
        try {
            ulbService.updateULB(id, ulb);
            return "redirect:/admin/ulbs?success=updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/edit-ulb";
        }
    }

    @GetMapping("/ulb/delete/{id}")
    public String deleteULB(@PathVariable Long id) {
        try {
            ulbService.deleteULB(id);
            return "redirect:/admin/ulbs?success=deleted";
        } catch (Exception e) {
            return "redirect:/admin/ulbs?error=deletefailed";
        }
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/user/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("ulbs", ulbService.getAllULBs());
        return "admin/new-user";
    }

    @PostMapping("/user/create")
    public String createUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user);
            return "redirect:/admin/users?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ulbs", ulbService.getAllULBs());
            return "admin/new-user";
        }
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("ulbs", ulbService.getAllULBs());
            return "admin/edit-user";
        } catch (Exception e) {
            return "redirect:/admin/users?error=notfound";
        }
    }

    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, Model model) {
        try {
            userService.updateUser(id, user);
            return "redirect:/admin/users?success=updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ulbs", ulbService.getAllULBs());
            return "admin/edit-user";
        }
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin/users?success=deleted";
        } catch (Exception e) {
            return "redirect:/admin/users?error=deletefailed";
        }
    }

    @GetMapping("/applications")
    public String viewAllApplications(Model model) {
        return "admin/applications";
    }
}