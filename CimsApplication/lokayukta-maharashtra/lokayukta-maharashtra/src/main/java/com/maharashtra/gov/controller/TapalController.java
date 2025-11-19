package com.maharashtra.gov.controller;

import com.maharashtra.gov.dto.TapalEntryDTO;
import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.entity.TapalEntry;
import com.maharashtra.gov.repo.ComplaintRepository;
import com.maharashtra.gov.service.TapalEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/tapal")
@RequiredArgsConstructor
public class TapalController {

    private final TapalEntryService tapalService;
    private final ComplaintRepository complaintRepository;

    // Define sections as constants
    private static final List<String> SECTIONS = Arrays.asList(
            "Lokayukta",
            "Up-Lokayukta",
            "RTI",
            "CASH",
            "ADMINISTRATIVE",
            "NA"
    );

    /**
     * Display Tapal / Institution Search page
     */
    @GetMapping("/search")
    public String showTapalSearchForm(Model model) {
        model.addAttribute("tapalDTO", new TapalEntryDTO());
        model.addAttribute("currentYear", Year.now().getValue());
        model.addAttribute("sections", SECTIONS);
        model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
        model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
        return "tapal/tapal-search";
    }

    /**
     * Handle search for existing complaint
     */
    @PostMapping("/search")
    public String searchComplaint(
            @ModelAttribute("tapalDTO") TapalEntryDTO searchDTO,
            Model model) {

        model.addAttribute("currentYear", Year.now().getValue());
        model.addAttribute("sections", SECTIONS);
        model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
        model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());

        // Perform search
        List<Complaint> searchResults = tapalService.searchComplaint(searchDTO);

        if (searchResults.size() == 1) {
            // Single complaint found - populate read-only fields
            Complaint complaint = searchResults.get(0);
            searchDTO.setExistingComplaintId(complaint.getId());
            searchDTO.setExistingComplaintNumber(complaint.getComplaintNumber());
            searchDTO.setExistingComplaintYear(complaint.getComplaintYear());
            searchDTO.setExistingComplainantName(complaint.getComplainantFullName());
            searchDTO.setExistingAgainstPartyName(complaint.getAgainstPartyFullName());
            searchDTO.setExistingSubject(complaint.getSubject());
            searchDTO.setExistingSection(complaint.getSection());

            // Get existing Tapal entries for this complaint
            List<TapalEntry> existingTapals = tapalService.getTapalEntriesByComplaint(complaint.getComplaintNumber());
            model.addAttribute("existingTapals", existingTapals);
            model.addAttribute("complaintFound", true);
            model.addAttribute("success", "MSG201: Complaint record found. Details fetched successfully.");
        } else if (searchResults.size() > 1) {
            // Multiple complaints found
            model.addAttribute("multipleResults", true);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("success", "Multiple complaints found. Please select one to continue.");
        } else {
            // Complaint not found
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "MSG201: Complaint Number not found. Please verify and re-enter or create new entry.");
        }

        model.addAttribute("tapalDTO", searchDTO);
        return "tapal/tapal-search";
    }

    /**
     * Select a specific complaint from multiple results
     */
    @GetMapping("/select/{id}")
    public String selectComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Complaint complaint = complaintRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Complaint not found"));

            TapalEntryDTO dto = new TapalEntryDTO();
            dto.setExistingComplaintId(complaint.getId());
            dto.setExistingComplaintNumber(complaint.getComplaintNumber());
            dto.setExistingComplaintYear(complaint.getComplaintYear());
            dto.setExistingComplainantName(complaint.getComplainantFullName());
            dto.setExistingAgainstPartyName(complaint.getAgainstPartyFullName());
            dto.setExistingSubject(complaint.getSubject());
            dto.setExistingSection(complaint.getSection());

            List<TapalEntry> existingTapals = tapalService.getTapalEntriesByComplaint(complaint.getComplaintNumber());

            model.addAttribute("tapalDTO", dto);
            model.addAttribute("existingTapals", existingTapals);
            model.addAttribute("complaintFound", true);
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", SECTIONS);
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("success", "Complaint selected successfully.");

            return "tapal/tapal-search";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error selecting complaint: " + e.getMessage());
            return "redirect:/tapal/search";
        }
    }

    /**
     * Submit Tapal entry for existing complaint
     */
    @PostMapping("/submit-tapal")
    public String submitTapalEntry(
            @Valid @ModelAttribute("tapalDTO") TapalEntryDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", SECTIONS);
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("complaintFound", true);
            model.addAttribute("error", "Please fill all mandatory fields correctly.");
            return "tapal/tapal-search";
        }

        try {
            TapalEntry tapalEntry = tapalService.createTapalForExistingComplaint(dto);

            redirectAttributes.addFlashAttribute("success",
                    "MSG212: Tapal entry saved successfully for Complaint Number: " + tapalEntry.getComplaintNumber());

            return "redirect:/tapal/view/" + tapalEntry.getId();

        } catch (Exception e) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", SECTIONS);
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("complaintFound", true);
            model.addAttribute("error", "MSG212: Unable to save Tapal entry: " + e.getMessage());
            return "tapal/tapal-search";
        }
    }

    /**
     * Submit new Tapal entry (RTI/Cash/Administrative/NA)
     */
    @PostMapping("/submit-new-entry")
    public String submitNewEntry(
            @Valid @ModelAttribute("tapalDTO") TapalEntryDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", SECTIONS);
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "Please fill all mandatory fields correctly.");
            return "tapal/tapal-search";
        }

        try {
            TapalEntry tapalEntry = tapalService.createNewTapalEntry(dto);

            redirectAttributes.addFlashAttribute("success",
                    "MSG220: New Tapal entry created successfully. Complaint Number: " + tapalEntry.getComplaintNumber());

            return "redirect:/tapal/view/" + tapalEntry.getId();

        } catch (Exception e) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", SECTIONS);
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "MSG220: Unable to create new entry: " + e.getMessage());
            return "tapal/tapal-search";
        }
    }

    /**
     * View Tapal entry details
     */
    @GetMapping("/view/{id}")
    public String viewTapalEntry(@PathVariable Long id, Model model) {
        try {
            TapalEntry tapalEntry = tapalService.getTapalEntryById(id);
            model.addAttribute("tapalEntry", tapalEntry);

            // If linked to complaint, get all tapals for that complaint
            if (tapalEntry.getFreshComplaint() != null) {
                List<TapalEntry> relatedTapals = tapalService.getTapalEntriesByComplaint(
                        tapalEntry.getComplaintNumber());
                model.addAttribute("relatedTapals", relatedTapals);
            }

            return "tapal/tapal-view";
        } catch (Exception e) {
            model.addAttribute("error", "Tapal entry not found");
            return "redirect:/tapal/list";
        }
    }

    /**
     * List all Tapal entries
     */
    @GetMapping("/list")
    public String listTapalEntries(
            @RequestParam(required = false) String section,
            @RequestParam(required = false) TapalEntry.TapalType tapalType,
            Model model) {

        List<TapalEntry> tapalEntries;

        if (section != null && !section.isEmpty()) {
            tapalEntries = tapalService.getTapalEntriesBySection(section);
        } else {
            tapalEntries = tapalService.getAllTapalEntries();
        }

        model.addAttribute("tapalEntries", tapalEntries);
        model.addAttribute("sections", SECTIONS);
        model.addAttribute("tapalTypes", TapalEntry.TapalType.values());

        return "tapal/tapal-list";
    }

    /**
     * View correspondence history for a complaint
     */
    @GetMapping("/history/{complaintNumber}")
    public String viewCorrespondenceHistory(
            @PathVariable String complaintNumber,
            Model model) {

        try {
            Complaint complaint = complaintRepository.findByComplaintNumber(complaintNumber)
                    .orElseThrow(() -> new RuntimeException("Complaint not found"));
            List<TapalEntry> tapalHistory = tapalService.getTapalEntriesByComplaint(complaintNumber);

            model.addAttribute("complaint", complaint);
            model.addAttribute("tapalHistory", tapalHistory);

            return "tapal/tapal-history";
        } catch (Exception e) {
            model.addAttribute("error", "Complaint not found");
            return "redirect:/tapal/search";
        }
    }

    /**
     * View all complaints
     */
    @GetMapping("/complaints")
    public String listComplaints(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String section,
            Model model) {

        List<Complaint> complaints;

        if (year != null && !year.isEmpty()) {
            complaints = complaintRepository.findByComplaintYear(year);
        } else if (section != null && !section.isEmpty()) {
            complaints = complaintRepository.findBySection(section);
        } else {
            complaints = complaintRepository.findAll();
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("sections", SECTIONS);

        return "tapal/complaint-list";
    }

    /**
     * View complaint details
     */
    @GetMapping("/complaint/{id}")
    public String viewComplaint(@PathVariable Long id, Model model) {
        try {
            Complaint complaint = complaintRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Complaint not found"));
            model.addAttribute("complaint", complaint);
            return "tapal/complaint-view";
        } catch (Exception e) {
            model.addAttribute("error", "Complaint not found");
            return "redirect:/tapal/complaints";
        }
    }
}