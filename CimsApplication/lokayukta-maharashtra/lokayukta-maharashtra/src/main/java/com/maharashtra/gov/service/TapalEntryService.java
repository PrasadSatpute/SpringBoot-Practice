package com.maharashtra.gov.service;

import com.maharashtra.gov.dto.TapalEntryDTO;
import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.entity.TapalEntry;
import com.maharashtra.gov.repo.ComplaintRepository;
import com.maharashtra.gov.repo.TapalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TapalEntryService {

    private final TapalEntryRepository tapalRepository;
    private final ComplaintRepository complaintRepository;

    /**
     * Search for existing complaint by various criteria
     */
    public List<Complaint> searchComplaint(TapalEntryDTO searchDTO) {
        List<Complaint> results = new java.util.ArrayList<>();

        // Search by complaint number (highest priority - exact match)
        if (searchDTO.getSearchComplaintNumber() != null && !searchDTO.getSearchComplaintNumber().isEmpty()) {
            Optional<Complaint> complaint = complaintRepository.findByComplaintNumber(searchDTO.getSearchComplaintNumber().trim());
            return complaint.map(List::of).orElse(List.of());
        }

        // Search by mobile number
        if (searchDTO.getSearchMobileNumber() != null && !searchDTO.getSearchMobileNumber().isEmpty()) {
            results.addAll(complaintRepository.searchByKeyword(searchDTO.getSearchMobileNumber().trim()));
        }

        // Search by name
        if (searchDTO.getSearchComplainantName() != null && !searchDTO.getSearchComplainantName().isEmpty()) {
            results.addAll(complaintRepository.searchByKeyword(searchDTO.getSearchComplainantName().trim()));
        }

        // Filter by date if provided
        if (searchDTO.getSearchLetterReceivedDate() != null && !results.isEmpty()) {
            results = results.stream()
                    .filter(c -> c.getReceivedDate().equals(searchDTO.getSearchLetterReceivedDate()) ||
                            c.getReceivedDate().isBefore(searchDTO.getSearchLetterReceivedDate().plusDays(1)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by section if provided
        if (searchDTO.getSearchSection() != null && !results.isEmpty()) {
            results = results.stream()
                    .filter(c -> c.getSection().equals(searchDTO.getSearchSection()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // If only date is provided
        if (results.isEmpty() && searchDTO.getSearchLetterReceivedDate() != null) {
            results = complaintRepository.findByReceivedDateBetween(
                    searchDTO.getSearchLetterReceivedDate(),
                    searchDTO.getSearchLetterReceivedDate().plusDays(1)
            );
        }

        // If only section is provided
        if (results.isEmpty() && searchDTO.getSearchSection() != null) {
            results = complaintRepository.findBySection(searchDTO.getSearchSection());
        }

        // Remove duplicates
        return results.stream().distinct().collect(java.util.stream.Collectors.toList());
    }

    /**
     * Create Tapal entry for existing complaint (LA/ULA)
     */
    @Transactional
    public TapalEntry createTapalForExistingComplaint(TapalEntryDTO dto) {
        Complaint complaint = complaintRepository.findById(dto.getExistingComplaintId())
                .orElseThrow(() -> new RuntimeException("MSG201: Complaint not found"));

        TapalEntry tapalEntry = TapalEntry.builder()
                .freshComplaint(complaint)
                .complaintNumber(complaint.getComplaintNumber())
                .year(complaint.getComplaintYearAsInt())
                .letterReceivedDate(dto.getLetterReceivedDate())
                .subjectLine(dto.getSubjectLine())
                .section(complaint.getSection())
                .letterReceivedFrom(dto.getLetterReceivedFrom())
                .letterRelatedTo(dto.getLetterRelatedTo())
                .remarks(dto.getTapalRemarks())
                .tapalType(TapalEntry.TapalType.EXISTING_COMPLAINT)
                .createdBy("SYSTEM") // Replace with actual logged-in username when authentication is added
                .build();

        return tapalRepository.save(tapalEntry);
    }

    /**
     * Create new entry for RTI/Cash/Administrative/NA
     */
    @Transactional
    public TapalEntry createNewTapalEntry(TapalEntryDTO dto) {
        // Validate section is RTI, Cash, Administrative, or NA
        String section = dto.getNewEntrySection();
        if (section.equals("Lokayukta") || section.equals("Up-Lokayukta")) {
            throw new RuntimeException("MSG218: Cannot create new entry for Lokayukta/Up-Lokayukta sections");
        }

        // Generate complaint number based on section
        String complaintNumber = generateComplaintNumber(section, dto.getNewEntryYear());

        TapalEntry tapalEntry = TapalEntry.builder()
                .freshComplaint(null)
                .complaintNumber(complaintNumber)
                .year(dto.getNewEntryYear())
                .letterReceivedDate(dto.getNewEntryReceivedDate())
                .inwardNumber(dto.getNewEntryInwardNumber())
                .receiverName(dto.getNewEntryReceiverName())
                .subjectLine(dto.getNewEntrySubjectLine())
                .section(section)
                .remarks(dto.getNewEntryRemarks())
                .tapalType(TapalEntry.TapalType.NEW_ENTRY)
                .createdBy("SYSTEM") // Replace with actual logged-in username when authentication is added
                .build();

        return tapalRepository.save(tapalEntry);
    }

    /**
     * Generate complaint number for new entries
     * Format: SECTION-YEAR-SERIAL (e.g., RTI-2025-0001)
     */
    private synchronized String generateComplaintNumber(String section, Integer year) {
        Integer maxSerial = tapalRepository.findMaxSerialNumberBySectionAndYear(section, year);
        int nextSerial = (maxSerial == null) ? 1 : maxSerial + 1;

        String sectionPrefix = section.toUpperCase();
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
    public List<TapalEntry> getTapalEntriesBySection(String section) {
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
                .createdByUsername(tapalEntry.getCreatedBy())
                .createdDate(tapalEntry.getCreatedAt().toLocalDate())
                .build();

        if (tapalEntry.getTapalType() == TapalEntry.TapalType.EXISTING_COMPLAINT) {
            dto.setLetterReceivedFrom(tapalEntry.getLetterReceivedFrom());
            dto.setLetterRelatedTo(tapalEntry.getLetterRelatedTo());
            dto.setTapalRemarks(tapalEntry.getRemarks());

            if (tapalEntry.getFreshComplaint() != null) {
                Complaint complaint = tapalEntry.getFreshComplaint();
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
    public long getTapalCountBySection(String section) {
        return tapalRepository.countBySection(section);
    }

    public long getTapalCountByYear(Integer year) {
        return tapalRepository.countByYear(year);
    }
}