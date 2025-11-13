package gov.property.web.controller.view;

import gov.property.model.entity.Property;
import gov.property.model.entity.PropertyRegistration;
import gov.property.service.PropertyService;
import gov.property.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationViewController {

    private final RegistrationService registrationService;
    private final PropertyService propertyService;

    @GetMapping
    public String listRegistrations(Model model, Authentication authentication) {
        List<PropertyRegistration> registrations = registrationService.getAllRegistrations();
        model.addAttribute("registrations", registrations);
        model.addAttribute("username", authentication.getName());
        return "registration/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Authentication authentication) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("registration", new PropertyRegistration());
        model.addAttribute("properties", properties);
        model.addAttribute("username", authentication.getName());
        return "registration/add";
    }

    @PostMapping("/add")
    public String addRegistration(@ModelAttribute PropertyRegistration registration,
                                  @RequestParam Long propertyId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyService.getPropertyById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));
            registration.setProperty(property);
            registration.setCreatedBy(1L);
            registrationService.createRegistration(registration);
            redirectAttributes.addFlashAttribute("success", "Registration created successfully!");
            return "redirect:/registrations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create registration: " + e.getMessage());
            return "redirect:/registrations/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        PropertyRegistration registration = registrationService.getRegistrationById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("registration", registration);
        model.addAttribute("properties", properties);
        model.addAttribute("username", authentication.getName());
        return "registration/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRegistration(@PathVariable Long id,
                                     @ModelAttribute PropertyRegistration registration,
                                     RedirectAttributes redirectAttributes) {
        try {
            registrationService.updateRegistration(id, registration);
            redirectAttributes.addFlashAttribute("success", "Registration updated successfully!");
            return "redirect:/registrations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update registration: " + e.getMessage());
            return "redirect:/registrations/edit/" + id;
        }
    }

    @GetMapping("/view/{id}")
    public String viewRegistration(@PathVariable Long id, Model model, Authentication authentication) {
        PropertyRegistration registration = registrationService.getRegistrationById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        model.addAttribute("registration", registration);
        model.addAttribute("username", authentication.getName());
        return "registration/view";
    }

    @GetMapping("/approve/{id}")
    public String approveRegistration(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            registrationService.approveRegistration(id, 1L); // Admin ID hardcoded for demo
            redirectAttributes.addFlashAttribute("success", "Registration approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve registration: " + e.getMessage());
        }
        return "redirect:/registrations";
    }

    @GetMapping("/reject/{id}")
    public String rejectRegistration(@PathVariable Long id,
                                     @RequestParam(required = false) String remarks,
                                     RedirectAttributes redirectAttributes) {
        try {
            registrationService.rejectRegistration(id, remarks != null ? remarks : "Rejected by admin");
            redirectAttributes.addFlashAttribute("success", "Registration rejected!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject registration: " + e.getMessage());
        }
        return "redirect:/registrations";
    }

    @GetMapping("/payment/{id}")
    public String markPaymentComplete(@PathVariable Long id,
                                      @RequestParam String paymentReference,
                                      RedirectAttributes redirectAttributes) {
        try {
            registrationService.markPaymentComplete(id, paymentReference);
            redirectAttributes.addFlashAttribute("success", "Payment marked as complete!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process payment: " + e.getMessage());
        }
        return "redirect:/registrations";
    }
}