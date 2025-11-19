package com.lokayukta.cims.controller;

import com.lokayukta.cims.dto.FreshComplaintDTO;
import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.service.FreshComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class FreshComplaintController {

    private final FreshComplaintService complaintService;

    @GetMapping("/fresh")
    public String showFreshComplaintForm(Model model) {
        model.addAttribute("complaintDTO", new FreshComplaintDTO());
        model.addAttribute("currentYear", Year.now().getValue());
        model.addAttribute("sections", User.Section.values());
        model.addAttribute("tableNumbers", generateTableNumbers());
        return "complaints/fresh-complaint";
    }

    @PostMapping("/fresh")
    public String submitFreshComplaint(
            @Valid @ModelAttribute("complaintDTO") FreshComplaintDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("tableNumbers", generateTableNumbers());
            model.addAttribute("error", "MSG125: Mandatory fields missing");
            return "complaints/fresh-complaint";
        }

        try {
            String username = authentication.getName();
            FreshComplaint complaint = complaintService.createComplaint(dto, username);

            redirectAttributes.addFlashAttribute("success",
                    "Complaint registered successfully. Complaint Number: " +
                            complaint.getComplaintNumber());

            return "redirect:/complaints/view/" + complaint.getId();

        } catch (Exception e) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("tableNumbers", generateTableNumbers());
            model.addAttribute("error", "Error creating complaint: " + e.getMessage());
            return "complaints/fresh-complaint";
        }
    }

    @GetMapping("/list")
    public String listComplaints(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) User.Section section,
            @RequestParam(required = false) Integer tableNo,
            Model model) {

        List<FreshComplaint> complaints;

        if (year != null) {
            complaints = complaintService.getComplaintsByYear(year);
        } else if (section != null) {
            complaints = complaintService.getComplaintsBySection(section);
        } else if (tableNo != null) {
            complaints = complaintService.getComplaintsByTable(tableNo);
        } else {
            complaints = complaintService.getAllComplaints();
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("sections", User.Section.values());
        model.addAttribute("tableNumbers", generateTableNumbers());

        return "complaints/complaint-list";
    }

    @GetMapping("/view/{id}")
    public String viewComplaint(@PathVariable Long id, Model model) {
        try {
            FreshComplaint complaint = complaintService.getComplaintById(id);
            model.addAttribute("complaint", complaint);
            return "complaints/complaint-view";
        } catch (Exception e) {
            model.addAttribute("error", "Complaint not found");
            return "redirect:/complaints/list";
        }
    }

    @GetMapping("/search")
    public String searchComplaints(
            @RequestParam(required = false) String complaintNumber,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<FreshComplaint> complaints;

        if (complaintNumber != null && !complaintNumber.isEmpty()) {
            try {
                FreshComplaint complaint = complaintService.getComplaintByNumber(complaintNumber);
                complaints = List.of(complaint);
            } catch (Exception e) {
                model.addAttribute("error", "MSG201: Complaint Number not found");
                complaints = List.of();
            }
        } else if (keyword != null && !keyword.isEmpty()) {
            complaints = complaintService.searchComplaints(keyword);
        } else {
            complaints = List.of();
        }

        model.addAttribute("complaints", complaints);
        return "complaints/search-results";
    }

    private List<Integer> generateTableNumbers() {
        return java.util.stream.IntStream.rangeClosed(1, 18)
                .boxed()
                .collect(Collectors.toList());
    }
}