package com.lokayukta.cims.service;
import com.lokayukta.cims.dto.FreshComplaintDTO;
import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.repository.FreshComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreshComplaintService {

    private final FreshComplaintRepository complaintRepository;
    private final UserService userService;

    @Transactional
    public FreshComplaint createComplaint(FreshComplaintDTO dto, String username) {
        User createdBy = userService.getUserByUsername(username);

        // Generate complaint number
        String complaintNumber = generateComplaintNumber(dto.getComplaintYear());

        FreshComplaint complaint = FreshComplaint.builder()
                .complaintNumber(complaintNumber)
                .complaintYear(dto.getComplaintYear())
                .receivedDate(dto.getReceivedDate())
                .complaintDate(dto.getComplaintDate())
                .section(dto.getSection())
                .tableNo(dto.getTableNo())
                .complainantSurname(dto.getComplainantSurname())
                .complainantFirstName(dto.getComplainantFirstName())
                .complainantMiddleName(dto.getComplainantMiddleName())
                .complainantMobile(dto.getComplainantMobile())
                .complainantEmail(dto.getComplainantEmail())
                .complainantAddress(dto.getComplainantAddress())
                .againstPartySurname(dto.getAgainstPartySurname())
                .againstPartyFirstName(dto.getAgainstPartyFirstName())
                .againstPartyMiddleName(dto.getAgainstPartyMiddleName())
                .againstPartyMobile(dto.getAgainstPartyMobile())
                .againstPartyEmail(dto.getAgainstPartyEmail())
                .againstPartyAddress(dto.getAgainstPartyAddress())
                .district(dto.getDistrict())
                .division(dto.getDivision())
                .designation(dto.getDesignation())
                .complaintCategory(dto.getComplaintCategory())
                .subject(dto.getSubject())
                .remarks(dto.getRemarks())
                .status(FreshComplaint.ComplaintStatus.PENDING)
                .createdBy(createdBy)
                .build();

        return complaintRepository.save(complaint);
    }

    private synchronized String generateComplaintNumber(Integer year) {
        Integer maxSerial = complaintRepository.findMaxSerialNumberByYear(year);
        int nextSerial = (maxSerial == null) ? 1 : maxSerial + 1;
        return String.format("%d/%04d", year, nextSerial);
    }

    public FreshComplaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public FreshComplaint getComplaintByNumber(String complaintNumber) {
        return complaintRepository.findByComplaintNumber(complaintNumber)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public List<FreshComplaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<FreshComplaint> getComplaintsByYear(Integer year) {
        return complaintRepository.findByComplaintYear(year);
    }

    public List<FreshComplaint> getComplaintsBySection(User.Section section) {
        return complaintRepository.findBySection(section);
    }

    public List<FreshComplaint> getComplaintsByTable(Integer tableNo) {
        return complaintRepository.findByTableNo(tableNo);
    }

    public List<FreshComplaint> searchComplaints(String keyword) {
        return complaintRepository.searchByKeyword(keyword);
    }

    @Transactional
    public FreshComplaint updateComplaintStatus(Long complaintId,
                                                FreshComplaint.ComplaintStatus status) {
        FreshComplaint complaint = getComplaintById(complaintId);
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    public long getComplaintCount() {
        return complaintRepository.count();
    }

    public long getComplaintCountByYear(Integer year) {
        return complaintRepository.countByComplaintYear(year);
    }

    // Convert entity to DTO
    public FreshComplaintDTO toDTO(FreshComplaint complaint) {
        return FreshComplaintDTO.builder()
                .id(complaint.getId())
                .complaintNumber(complaint.getComplaintNumber())
                .complaintYear(complaint.getComplaintYear())
                .receivedDate(complaint.getReceivedDate())
                .complaintDate(complaint.getComplaintDate())
                .section(complaint.getSection())
                .tableNo(complaint.getTableNo())
                .complainantSurname(complaint.getComplainantSurname())
                .complainantFirstName(complaint.getComplainantFirstName())
                .complainantMiddleName(complaint.getComplainantMiddleName())
                .complainantMobile(complaint.getComplainantMobile())
                .complainantEmail(complaint.getComplainantEmail())
                .complainantAddress(complaint.getComplainantAddress())
                .againstPartySurname(complaint.getAgainstPartySurname())
                .againstPartyFirstName(complaint.getAgainstPartyFirstName())
                .againstPartyMiddleName(complaint.getAgainstPartyMiddleName())
                .againstPartyMobile(complaint.getAgainstPartyMobile())
                .againstPartyEmail(complaint.getAgainstPartyEmail())
                .againstPartyAddress(complaint.getAgainstPartyAddress())
                .district(complaint.getDistrict())
                .division(complaint.getDivision())
                .designation(complaint.getDesignation())
                .complaintCategory(complaint.getComplaintCategory())
                .subject(complaint.getSubject())
                .remarks(complaint.getRemarks())
                .status(complaint.getStatus().name())
                .createdByUsername(complaint.getCreatedBy() != null ?
                        complaint.getCreatedBy().getUsername() : null)
                .build();
    }
}