package gov.property.web.controller.view;

import gov.property.model.entity.Property;
import gov.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyViewController {

    private final PropertyService propertyService;

    @GetMapping
    public String listProperties(Model model, Authentication authentication) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        model.addAttribute("username", authentication.getName());
        return "property/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Authentication authentication) {
        model.addAttribute("property", new Property());
        model.addAttribute("username", authentication.getName());
        return "property/add";
    }

    @PostMapping("/add")
    public String addProperty(@ModelAttribute Property property,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        try {
            // Get user ID from authentication - simplified for demo
            property.setCreatedBy(1L);
            propertyService.createProperty(property);
            redirectAttributes.addFlashAttribute("success", "Property added successfully!");
            return "redirect:/properties";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add property: " + e.getMessage());
            return "redirect:/properties/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        model.addAttribute("property", property);
        model.addAttribute("username", authentication.getName());
        return "property/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProperty(@PathVariable Long id,
                                 @ModelAttribute Property property,
                                 RedirectAttributes redirectAttributes) {
        try {
            propertyService.updateProperty(id, property);
            redirectAttributes.addFlashAttribute("success", "Property updated successfully!");
            return "redirect:/properties";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update property: " + e.getMessage());
            return "redirect:/properties/edit/" + id;
        }
    }

    @GetMapping("/view/{id}")
    public String viewProperty(@PathVariable Long id, Model model, Authentication authentication) {
        Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        model.addAttribute("property", property);
        model.addAttribute("username", authentication.getName());
        return "property/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("success", "Property deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete property: " + e.getMessage());
        }
        return "redirect:/properties";
    }

    @GetMapping("/search")
    public String searchProperties(@RequestParam String keyword, Model model, Authentication authentication) {
        List<Property> properties = propertyService.searchProperties(keyword);
        model.addAttribute("properties", properties);
        model.addAttribute("keyword", keyword);
        model.addAttribute("username", authentication.getName());
        return "property/list";
    }
}