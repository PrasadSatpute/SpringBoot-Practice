package com.lokayukta.cims.controller;

import com.lokayukta.cims.dto.TapalEntryDTO;
import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.TapalEntry;
import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.service.FreshComplaintService;
import com.lokayukta.cims.service.TapalEntryService;
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
import java.util.Optional;

@Controller
@RequestMapping("/tapal")
@RequiredArgsConstructor
public class TapalController {

    private final TapalEntryService tapalService;
    private final FreshComplaintService complaintService;

    /**
     * Display Tapal / Institution Search page
     */
    @GetMapping("/search")
    public String showTapalSearchForm(Model model) {
        model.addAttribute("tapalDTO", new TapalEntryDTO());
        model.addAttribute("currentYear", Year.now().getValue());
        model.addAttribute("sections", User.Section.values());
        model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
        model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
        return "tapal/tapal-search";
    }

    /**
     * Handle search for existing complaint - ENHANCED WITH NEW LOGIC
     */
    @PostMapping("/search")
    public String searchComplaint(
            @ModelAttribute("tapalDTO") TapalEntryDTO searchDTO,
            Model model) {

        model.addAttribute("currentYear", Year.now().getValue());
        model.addAttribute("sections", User.Section.values());
        model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
        model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());

        // Perform enhanced search
        List<FreshComplaint> searchResults = tapalService.searchComplaints(searchDTO);

        if (searchResults.isEmpty()) {
            // No complaints found
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "MSG201: No complaints found matching your search criteria. Please verify and re-enter or create new entry.");
        } else if (searchResults.size() == 1) {
            // Single complaint found - show details directly
            FreshComplaint complaint = searchResults.get(0);
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
        } else {
            // Multiple complaints found - show table
            model.addAttribute("multipleResults", true);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("success", "MSG202: Found " + searchResults.size() + " matching complaints. Please select one.");
        }

        model.addAttribute("tapalDTO", searchDTO);
        return "tapal/tapal-search";
    }

    /**
     * Select a complaint from multiple results
     */
    @GetMapping("/select/{complaintId}")
    public String selectComplaint(
            @PathVariable Long complaintId,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            FreshComplaint complaint = complaintService.getComplaintById(complaintId);

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
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("success", "MSG201: Complaint selected successfully.");

            return "tapal/tapal-search";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "MSG201: Complaint not found.");
            return "redirect:/tapal/search";
        }
    }

    /**
     * Delete a complaint
     */
    @PostMapping("/delete/{complaintId}")
    public String deleteComplaint(
            @PathVariable Long complaintId,
            RedirectAttributes redirectAttributes) {

        try {
            tapalService.deleteComplaint(complaintId);
            redirectAttributes.addFlashAttribute("success", "MSG303: Complaint deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "MSG304: Unable to delete complaint: " + e.getMessage());
        }

        return "redirect:/tapal/search";
    }

    /**
     * Submit Tapal entry for existing complaint
     */
    @PostMapping("/submit-tapal")
    public String submitTapalEntry(
            @Valid @ModelAttribute("tapalDTO") TapalEntryDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Manual validation for Tapal Entry fields
        boolean hasErrors = false;

        if (dto.getLetterReceivedDate() == null) {
            model.addAttribute("error", "MSG207: Letter Received Date cannot be blank for Tapal entry.");
            hasErrors = true;
        }

        if (dto.getSubjectLine() == null || dto.getSubjectLine().trim().isEmpty()) {
            model.addAttribute("error", "MSG208: Subject Line cannot be empty.");
            hasErrors = true;
        }

        if (dto.getLetterReceivedFrom() == null) {
            model.addAttribute("error", "MSG209: Please select the sender (Complainant / Against Party).");
            hasErrors = true;
        }

        if (dto.getLetterRelatedTo() == null) {
            model.addAttribute("error", "MSG210: Please select the related party (Complainant / Against Party).");
            hasErrors = true;
        }


        if (hasErrors) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("complaintFound", true);
            model.addAttribute("error", "Please fill all mandatory fields correctly.");
            // Reload complaint details
            if (dto.getExistingComplaintId() != null) {
                try {
                    FreshComplaint complaint = complaintService.getComplaintById(dto.getExistingComplaintId());
                    dto.setExistingComplaintNumber(complaint.getComplaintNumber());
                    dto.setExistingComplaintYear(complaint.getComplaintYear());
                    dto.setExistingComplainantName(complaint.getComplainantFullName());
                    dto.setExistingAgainstPartyName(complaint.getAgainstPartyFullName());
                    dto.setExistingSubject(complaint.getSubject());
                    dto.setExistingSection(complaint.getSection());

                    List<TapalEntry> existingTapals = tapalService.getTapalEntriesByComplaint(complaint.getComplaintNumber());
                    model.addAttribute("existingTapals", existingTapals);
                } catch (Exception e) {
                    // Handle exception
                }
            }

            model.addAttribute("tapalDTO", dto);
            return "tapal/tapal-search";
        }

        try {
            String username = authentication.getName();
            TapalEntry tapalEntry = tapalService.createTapalForExistingComplaint(dto, username);

            redirectAttributes.addFlashAttribute("success",
                    "MSG212: Tapal entry saved successfully for Complaint Number: " + tapalEntry.getComplaintNumber());

            return "redirect:/tapal/view/" + tapalEntry.getId();

        } catch (Exception e) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("letterFromOptions", TapalEntry.LetterFrom.values());
            model.addAttribute("letterRelatedOptions", TapalEntry.LetterRelatedTo.values());
            model.addAttribute("complaintFound", true);
            model.addAttribute("error", "MSG212: Unable to save Tapal entry: " + e.getMessage());
            // Reload complaint details
            if (dto.getExistingComplaintId() != null) {
                try {
                    FreshComplaint complaint = complaintService.getComplaintById(dto.getExistingComplaintId());
                    dto.setExistingComplaintNumber(complaint.getComplaintNumber());
                    dto.setExistingComplaintYear(complaint.getComplaintYear());
                    dto.setExistingComplainantName(complaint.getComplainantFullName());
                    dto.setExistingAgainstPartyName(complaint.getAgainstPartyFullName());
                    dto.setExistingSubject(complaint.getSubject());
                    dto.setExistingSection(complaint.getSection());

                    List<TapalEntry> existingTapals = tapalService.getTapalEntriesByComplaint(complaint.getComplaintNumber());
                    model.addAttribute("existingTapals", existingTapals);
                } catch (Exception ex) {
                    // Handle exception
                }
            }

            model.addAttribute("tapalDTO", dto);
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
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Manual validation for New Entry fields
        boolean hasErrors = false;

        if (dto.getNewEntryYear() == null) {
            model.addAttribute("error", "MSG213: Please select the year for new Tapal entry.");
            hasErrors = true;
        }

        if (dto.getNewEntryReceivedDate() == null) {
            model.addAttribute("error", "MSG214: Received Date cannot be blank.");
            hasErrors = true;
        }

        if (dto.getNewEntryInwardNumber() == null || dto.getNewEntryInwardNumber().trim().isEmpty()) {
            model.addAttribute("error", "MSG215: Inward Number cannot be empty or invalid.");
            hasErrors = true;
        }

        if (dto.getNewEntryReceiverName() == null || dto.getNewEntryReceiverName().trim().isEmpty()) {
            model.addAttribute("error", "MSG216: Receiver Name cannot be blank.");
            hasErrors = true;
        }

        if (dto.getNewEntrySubjectLine() == null || dto.getNewEntrySubjectLine().trim().isEmpty()) {
            model.addAttribute("error", "MSG217: Subject Line cannot be blank for new entry.");
            hasErrors = true;
        }

        if (dto.getNewEntrySection() == null) {
            model.addAttribute("error", "MSG218: Please select one Section (RTI / Cash / Administrative / NA).");
            hasErrors = true;
        }


        if (hasErrors) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "Please fill all mandatory fields correctly.");
            model.addAttribute("tapalDTO", dto);
            return "tapal/tapal-search";
        }

        try {
            String username = authentication.getName();
            TapalEntry tapalEntry = tapalService.createNewTapalEntry(dto, username);

            redirectAttributes.addFlashAttribute("success",
                    "MSG220: New Tapal entry created successfully. Complaint Number: " + tapalEntry.getComplaintNumber());

            return "redirect:/tapal/view/" + tapalEntry.getId();

        } catch (Exception e) {
            model.addAttribute("currentYear", Year.now().getValue());
            model.addAttribute("sections", User.Section.values());
            model.addAttribute("complaintFound", false);
            model.addAttribute("error", "MSG220: Unable to create new entry: " + e.getMessage());
            model.addAttribute("tapalDTO", dto);
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
            @RequestParam(required = false) User.Section section,
            @RequestParam(required = false) TapalEntry.TapalType tapalType,
            Model model) {

        List<TapalEntry> tapalEntries;

        if (section != null) {
            tapalEntries = tapalService.getTapalEntriesBySection(section);
        } else {
            tapalEntries = tapalService.getAllTapalEntries();
        }

        model.addAttribute("tapalEntries", tapalEntries);
        model.addAttribute("sections", User.Section.values());
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
            FreshComplaint complaint = complaintService.getComplaintByNumber(complaintNumber);
            List<TapalEntry> tapalHistory = tapalService.getTapalEntriesByComplaint(complaintNumber);

            model.addAttribute("complaint", complaint);
            model.addAttribute("tapalHistory", tapalHistory);

            return "tapal/tapal-history";
        } catch (Exception e) {
            model.addAttribute("error", "Complaint not found");
            return "redirect:/tapal/search";
        }
    }
}