package com.lokayukta.cims.service;

import com.lokayukta.cims.dto.TapalEntryDTO;
import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.TapalEntry;
import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.repository.FreshComplaintRepository;
import com.lokayukta.cims.repository.TapalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TapalEntryService {

    private final TapalEntryRepository tapalRepository;
    private final FreshComplaintRepository complaintRepository;
    private final UserService userService;

    /**
     * Enhanced search logic:
     * 1. If ONLY complaint number is provided -> exact single match
     * 2. If complaint number + other fields -> search with OR logic, return multiple results
     * 3. If no complaint number but other fields -> search with OR logic
     */
    public List<FreshComplaint> searchComplaints(TapalEntryDTO searchDTO) {
        List<FreshComplaint> results = new ArrayList<>();

        String complaintNumber = searchDTO.getSearchComplaintNumber();
        String complainantName = searchDTO.getSearchComplainantName();
        String mobileNumber = searchDTO.getSearchMobileNumber();
        User.Section section = searchDTO.getSearchSection();

        // Check if ONLY complaint number is provided (all other fields are empty)
        boolean onlyComplaintNumber =
                (complaintNumber != null && !complaintNumber.trim().isEmpty()) &&
                        (complainantName == null || complainantName.trim().isEmpty()) &&
                        (mobileNumber == null || mobileNumber.trim().isEmpty()) &&
                        (section == null);

        // Case 1: ONLY complaint number provided -> Single exact match
        if (onlyComplaintNumber) {
            Optional<FreshComplaint> exactMatch = complaintRepository
                    .findByComplaintNumber(complaintNumber.trim());

            if (exactMatch.isPresent()) {
                results.add(exactMatch.get());
            }
            return results;
        }

        // Case 2: Complaint number + other fields OR no complaint number
        // Use OR logic across all provided fields
        results = searchByMultipleFields(searchDTO);

        return results;
    }

    /**
     * Search complaints using OR logic across multiple fields
     */
    private List<FreshComplaint> searchByMultipleFields(TapalEntryDTO searchDTO) {
        List<FreshComplaint> allComplaints = complaintRepository.findAll();
        List<FreshComplaint> matchedComplaints = new ArrayList<>();

        String complaintNumber = searchDTO.getSearchComplaintNumber();
        String searchName = searchDTO.getSearchComplainantName();
        String searchMobile = searchDTO.getSearchMobileNumber();
        User.Section searchSection = searchDTO.getSearchSection();

        for (FreshComplaint complaint : allComplaints) {
            boolean matches = false;

            // Match by complaint number (partial match)
            if (complaintNumber != null && !complaintNumber.trim().isEmpty()) {
                if (complaint.getComplaintNumber().toLowerCase()
                        .contains(complaintNumber.trim().toLowerCase())) {
                    matches = true;
                }
            }

            // Match by complainant name (case-insensitive, partial match)
            if (searchName != null && !searchName.trim().isEmpty()) {
                String fullName = complaint.getComplainantFullName().toLowerCase();
                if (fullName.contains(searchName.trim().toLowerCase())) {
                    matches = true;
                }
            }

            // Match by mobile number (exact or partial)
            if (searchMobile != null && !searchMobile.trim().isEmpty()) {
                if (complaint.getComplainantMobile() != null &&
                        complaint.getComplainantMobile().contains(searchMobile.trim())) {
                    matches = true;
                }
            }

            // Match by section
            if (searchSection != null && complaint.getSection() == searchSection) {
                matches = true;
            }

            if (matches) {
                matchedComplaints.add(complaint);
            }
        }

        return matchedComplaints;
    }

    /**
     * OLD METHOD - kept for backward compatibility
     */
    public Optional<FreshComplaint> searchComplaint(TapalEntryDTO searchDTO) {
        List<FreshComplaint> results = searchComplaints(searchDTO);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Delete a complaint by ID
     */
    @Transactional
    public void deleteComplaint(Long complaintId) {
        FreshComplaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("MSG301: Complaint not found"));

        // First delete all associated tapal entries
        List<TapalEntry> tapals = tapalRepository.findByFreshComplaint(complaint);
        if (!tapals.isEmpty()) {
            tapalRepository.deleteAll(tapals);
        }

        // Then delete the complaint
        complaintRepository.delete(complaint);
    }

    /**
     * Create Tapal entry for existing complaint (LA/ULA)
     */
    @Transactional
    public TapalEntry createTapalForExistingComplaint(TapalEntryDTO dto, String username) {
        FreshComplaint complaint = complaintRepository.findById(dto.getExistingComplaintId())
                .orElseThrow(() -> new RuntimeException("MSG201: Complaint not found"));

        User createdBy = userService.getUserByUsername(username);

        TapalEntry tapalEntry = TapalEntry.builder()
                .freshComplaint(complaint)
                .complaintNumber(complaint.getComplaintNumber())
                .year(complaint.getComplaintYear())
                .letterReceivedDate(dto.getLetterReceivedDate())
                .subjectLine(dto.getSubjectLine())
                .section(complaint.getSection())
                .letterReceivedFrom(dto.getLetterReceivedFrom())
                .letterRelatedTo(dto.getLetterRelatedTo())
                .remarks(dto.getTapalRemarks())
                .tapalType(TapalEntry.TapalType.EXISTING_COMPLAINT)
                .createdBy(createdBy)
                .build();

        return tapalRepository.save(tapalEntry);
    }

    /**
     * Create new entry for RTI/Cash/Administrative/NA
     */
    @Transactional
    public TapalEntry createNewTapalEntry(TapalEntryDTO dto, String username) {
        // Validate section is RTI, Cash, Administrative, or NA
        if (dto.getNewEntrySection() == User.Section.LOKAYUKTA ||
                dto.getNewEntrySection() == User.Section.UP_LOKAYUKTA) {
            throw new RuntimeException("MSG218: Cannot create new entry for Lokayukta/Up-Lokayukta sections");
        }

        User createdBy = userService.getUserByUsername(username);

        // Generate complaint number based on section
        String complaintNumber = generateComplaintNumber(dto.getNewEntrySection(), dto.getNewEntryYear());

        TapalEntry tapalEntry = TapalEntry.builder()
                .freshComplaint(null) // No linked complaint
                .complaintNumber(complaintNumber)
                .year(dto.getNewEntryYear())
                .letterReceivedDate(dto.getNewEntryReceivedDate())
                .inwardNumber(dto.getNewEntryInwardNumber())
                .receiverName(dto.getNewEntryReceiverName())
                .subjectLine(dto.getNewEntrySubjectLine())
                .section(dto.getNewEntrySection())
                .remarks(dto.getNewEntryRemarks())
                .tapalType(TapalEntry.TapalType.NEW_ENTRY)
                .createdBy(createdBy)
                .build();

        return tapalRepository.save(tapalEntry);
    }

    /**
     * Generate complaint number for new entries
     * Format: SECTION-YEAR-SERIAL (e.g., RTI-2025-0001)
     */
    private synchronized String generateComplaintNumber(User.Section section, Integer year) {
        Integer maxSerial = tapalRepository.findMaxSerialNumberBySectionAndYear(section, year);
        int nextSerial = (maxSerial == null) ? 1 : maxSerial + 1;

        String sectionPrefix = section.name();
        return String.format("%s-%d-%04d", sectionPrefix, year, nextSerial);
    }

    /**
     * Get all Tapal entries for a complaint
     */
    public List<TapalEntry> getTapalEntriesByComplaint(String complaintNumber) {
        return tapalRepository.findByComplaintNumberOrderByCreatedAtDesc(complaintNumber);
    }

    /**
     * Get all Tapal entries by section
     */
    public List<TapalEntry> getTapalEntriesBySection(User.Section section) {
        return tapalRepository.findBySection(section);
    }

    /**
     * Get Tapal entry by ID
     */
    public TapalEntry getTapalEntryById(Long id) {
        return tapalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tapal entry not found"));
    }

    /**
     * Get all Tapal entries
     */
    public List<TapalEntry> getAllTapalEntries() {
        return tapalRepository.findRecentEntries();
    }

    /**
     * Convert entity to DTO
     */
    public TapalEntryDTO toDTO(TapalEntry tapalEntry) {
        TapalEntryDTO dto = TapalEntryDTO.builder()
                .id(tapalEntry.getId())
                .generatedComplaintNumber(tapalEntry.getComplaintNumber())
                .tapalType(tapalEntry.getTapalType())
                .letterReceivedDate(tapalEntry.getLetterReceivedDate())
                .subjectLine(tapalEntry.getSubjectLine())
                .createdByUsername(tapalEntry.getCreatedBy() != null ?
                        tapalEntry.getCreatedBy().getUsername() : null)
                .createdDate(tapalEntry.getCreatedAt().toLocalDate())
                .build();

        if (tapalEntry.getTapalType() == TapalEntry.TapalType.EXISTING_COMPLAINT) {
            dto.setLetterReceivedFrom(tapalEntry.getLetterReceivedFrom());
            dto.setLetterRelatedTo(tapalEntry.getLetterRelatedTo());
            dto.setTapalRemarks(tapalEntry.getRemarks());

            if (tapalEntry.getFreshComplaint() != null) {
                FreshComplaint complaint = tapalEntry.getFreshComplaint();
                dto.setExistingComplaintNumber(complaint.getComplaintNumber());
                dto.setExistingComplainantName(complaint.getComplainantFullName());
            }
        } else {
            dto.setNewEntryYear(tapalEntry.getYear());
            dto.setNewEntryInwardNumber(tapalEntry.getInwardNumber());
            dto.setNewEntryReceiverName(tapalEntry.getReceiverName());
            dto.setNewEntrySection(tapalEntry.getSection());
            dto.setNewEntryRemarks(tapalEntry.getRemarks());
        }

        return dto;
    }

    /**
     * Get statistics
     */
    public long getTapalCountBySection(User.Section section) {
        return tapalRepository.countBySection(section);
    }

    public long getTapalCountByYear(Integer year) {
        return tapalRepository.countByYear(year);
    }
}