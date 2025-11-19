package com.lokayukta.cims.dto;

import com.lokayukta.cims.entity.TapalEntry;
import com.lokayukta.cims.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class TapalEntryDTO {

    private Long id;

    // Search Fields
    private String searchComplaintNumber;
    private String searchComplainantName;
    private String searchMobileNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchLetterReceivedDate;

    private User.Section searchSection;

    // Existing Complaint Details (Read-only, populated after search)
    private Long existingComplaintId;
    private String existingComplaintNumber;
    private Integer existingComplaintYear;
    private String existingComplainantName;
    private String existingAgainstPartyName;
    private String existingSubject;
    private User.Section existingSection;

    // Tapal Entry Fields (for existing complaints)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate letterReceivedDate;

    private String subjectLine;

    private TapalEntry.LetterFrom letterReceivedFrom;

    private TapalEntry.LetterRelatedTo letterRelatedTo;

    private String tapalRemarks;

    // New Entry Fields (for RTI/Cash/Administrative/NA)
    @Min(value = 2000, message = "Invalid year")
    @Max(value = 2100, message = "Invalid year")
    private Integer newEntryYear;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate newEntryReceivedDate;

    private String newEntryInwardNumber;

    private String newEntryReceiverName;

    private String newEntrySubjectLine;

    private User.Section newEntrySection;

    private String newEntryRemarks;

    // Generated Complaint Number (for new entries)
    private String generatedComplaintNumber;

    // Metadata
    private TapalEntry.TapalType tapalType;
    private String createdByUsername;
    private LocalDate createdDate;
}