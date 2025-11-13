package com.property.registration.controller;

import com.property.registration.entity.*;
import com.property.registration.service.PropertyApplicationService;
import com.property.registration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ulb")
@RequiredArgsConstructor
public class ULBController {

    private final UserService userService;
    private final PropertyApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUlb() == null) {
            return "redirect:/login?error=noULB";
        }

        model.addAttribute("user", user);
        model.addAttribute("ulb", user.getUlb());

        if (user.getRole() == Role.ULB_LEVEL_1) {
            model.addAttribute("pendingApplications",
                    applicationService.getApplicationsByULBAndStatus(user.getUlb(), ApplicationStatus.PAYMENT_COMPLETED));
        } else if (user.getRole() == Role.ULB_LEVEL_2) {
            model.addAttribute("pendingApplications",
                    applicationService.getApplicationsByULBAndStatus(user.getUlb(), ApplicationStatus.LEVEL1_APPROVED));
        }

        return "ulb/dashboard";
    }

    @GetMapping("/applications")
    public String viewAllApplications(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("applications", applicationService.getApplicationsByULB(user.getUlb()));
        return "ulb/applications";
    }

    @GetMapping("/application/view/{id}")
    public String viewApplication(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PropertyApplication application = applicationService.getApplicationById(id);

            if (!application.getAssignedUlb().getId().equals(user.getUlb().getId())) {
                return "redirect:/ulb/dashboard?error=unauthorized";
            }

            model.addAttribute("application", application);
            model.addAttribute("documents", applicationService.getApplicationDocuments(id));
            model.addAttribute("userRole", user.getRole());
            return "ulb/view-application";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/ulb/dashboard";
        }
    }

    @PostMapping("/application/{id}/level1-approval")
    public String level1Approval(@PathVariable Long id,
                                 @RequestParam boolean approved,
                                 @RequestParam String remarks,
                                 Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != Role.ULB_LEVEL_1) {
                return "redirect:/ulb/dashboard?error=unauthorized";
            }

            applicationService.level1Approval(id, user, approved, remarks);
            return "redirect:/ulb/application/view/" + id + "?approval=success";
        } catch (Exception e) {
            return "redirect:/ulb/application/view/" + id + "?approval=failed";
        }
    }

    @PostMapping("/application/{id}/level2-approval")
    public String level2Approval(@PathVariable Long id,
                                 @RequestParam boolean approved,
                                 @RequestParam String remarks,
                                 Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != Role.ULB_LEVEL_2) {
                return "redirect:/ulb/dashboard?error=unauthorized";
            }

            applicationService.level2Approval(id, user, approved, remarks);
            return "redirect:/ulb/application/view/" + id + "?approval=success";
        } catch (Exception e) {
            return "redirect:/ulb/application/view/" + id + "?approval=failed";
        }
    }

    @GetMapping("/citizen/new")
    public String newCitizenForm(Model model) {
        model.addAttribute("user", new User());
        return "ulb/new-citizen";
    }

    @PostMapping("/citizen/create")
    public String createCitizen(@ModelAttribute User user,
                                Authentication authentication, Model model) {
        try {
            User ulbUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setRole(Role.CITIZEN);
            user.setUlb(ulbUser.getUlb());
            userService.createUser(user);
            return "redirect:/ulb/dashboard?citizen=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "ulb/new-citizen";
        }
    }

    @GetMapping("/application/new-for-citizen")
    public String newApplicationForCitizenForm(Model model) {
        model.addAttribute("application", new PropertyApplication());
        return "ulb/new-application-for-citizen";
    }

    @PostMapping("/application/create-for-citizen")
    public String createApplicationForCitizen(@ModelAttribute PropertyApplication application,
                                              @RequestParam Long citizenId,
                                              Authentication authentication, Model model) {
        try {
            User citizen = userService.getUserById(citizenId);
            PropertyApplication created = applicationService.createApplication(application, citizen);
            return "redirect:/ulb/application/view/" + created.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "ulb/new-application-for-citizen";
        }
    }
}