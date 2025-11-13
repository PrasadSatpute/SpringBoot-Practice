package com.property.registration.controller;

import com.property.registration.entity.PropertyApplication;
import com.property.registration.entity.User;
import com.property.registration.entity.DocumentType;
import com.property.registration.service.PropertyApplicationService;
import com.property.registration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Controller
@RequestMapping("/citizen")
@RequiredArgsConstructor
public class CitizenController {

    private final UserService userService;
    private final PropertyApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("applications", applicationService.getApplicationsByCitizen(user));
        return "citizen/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "citizen/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, Authentication authentication, Model model) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userService.updateUser(currentUser.getId(), updatedUser);
            return "redirect:/citizen/profile?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "citizen/profile";
        }
    }

    @GetMapping("/profile/delete")
    public String deleteProfile(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(user.getId());
        return "redirect:/logout";
    }

    @GetMapping("/application/new")
    public String newApplicationForm(Model model) {
        model.addAttribute("application", new PropertyApplication());
        return "citizen/new-application";
    }

    @PostMapping("/application/create")
    public String createApplication(@ModelAttribute PropertyApplication application,
                                    Authentication authentication, Model model) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PropertyApplication created = applicationService.createApplication(application, user);
            return "redirect:/citizen/application/view/" + created.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "citizen/new-application";
        }
    }

    @GetMapping("/application/view/{id}")
    public String viewApplication(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PropertyApplication application = applicationService.getApplicationById(id);

            if (!application.getCitizen().getId().equals(user.getId())) {
                return "redirect:/citizen/dashboard?error=unauthorized";
            }

            model.addAttribute("application", application);
            model.addAttribute("documents", applicationService.getApplicationDocuments(id));
            return "citizen/view-application";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/citizen/dashboard";
        }
    }

    @PostMapping("/application/{id}/upload-document")
    public String uploadDocument(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam("documentType") DocumentType documentType,
                                 Authentication authentication) {
        try {
            applicationService.uploadDocument(id, file, documentType);
            return "redirect:/citizen/application/view/" + id + "?upload=success";
        } catch (Exception e) {
            return "redirect:/citizen/application/view/" + id + "?upload=failed";
        }
    }

    @PostMapping("/application/{id}/payment")
    public String processPayment(@PathVariable Long id,
                                 @RequestParam String transactionId,
                                 @RequestParam BigDecimal amount,
                                 Authentication authentication) {
        try {
            applicationService.processPayment(id, transactionId, amount);
            return "redirect:/citizen/application/view/" + id + "?payment=success";
        } catch (Exception e) {
            return "redirect:/citizen/application/view/" + id + "?payment=failed";
        }
    }

    @GetMapping("/application/{id}/delete")
    public String deleteApplication(@PathVariable Long id, Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            applicationService.deleteApplication(id, user.getId());
            return "redirect:/citizen/dashboard?delete=success";
        } catch (Exception e) {
            return "redirect:/citizen/dashboard?delete=failed";
        }
    }
}