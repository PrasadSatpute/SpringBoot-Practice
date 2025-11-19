package com.lokayukta.cims.dto;
import com.lokayukta.cims.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class FreshComplaintDTO {

    private Long id;
    private String complaintNumber;

    @NotNull(message = "MSG101: Please select Complaint Year")
    @Min(value = 2000, message = "Invalid year")
    @Max(value = 2100, message = "Invalid year")
    private Integer complaintYear;

    @NotNull(message = "MSG102: Received Date cannot be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receivedDate;

    @NotNull(message = "MSG104: Invalid Complaint Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate complaintDate;

    @NotNull(message = "MSG105: Please select Section (Lokayukta / Up-Lokayukta)")
    private User.Section section;

    @NotNull(message = "MSG106: Table No. must be between 1-18")
    @Min(value = 1, message = "MSG106: Table No. must be between 1-18")
    @Max(value = 18, message = "MSG106: Table No. must be between 1-18")
    private Integer tableNo;

    // Complainant Details
    @NotBlank(message = "MSG107: Complainant Surname cannot be empty")
    @Size(max = 100, message = "Surname too long")
    private String complainantSurname;

    @NotBlank(message = "MSG108: Complainant First Name cannot be empty")
    @Size(max = 100, message = "First name too long")
    private String complainantFirstName;

    @Size(max = 100, message = "Middle name too long")
    private String complainantMiddleName;

    @Pattern(regexp = "^[0-9]{10}$", message = "MSG110: Mobile Number must contain 10 digits")
    private String complainantMobile;

    @Email(message = "MSG111: Invalid Email format")
    private String complainantEmail;

    @NotBlank(message = "MSG112: Address cannot be empty")
    private String complainantAddress;

    // Against Party Details
    @NotBlank(message = "MSG113: Against Party Surname cannot be empty")
    @Size(max = 100, message = "Surname too long")
    private String againstPartySurname;

    @NotBlank(message = "MSG114: Against Party First Name cannot be empty")
    @Size(max = 100, message = "First name too long")
    private String againstPartyFirstName;

    @Size(max = 100, message = "Middle name too long")
    private String againstPartyMiddleName;

    @Pattern(regexp = "^[0-9]{10}$", message = "MSG116: Mobile Number must contain 10 digits")
    private String againstPartyMobile;

    @Email(message = "MSG117: Invalid Email format")
    private String againstPartyEmail;

    @NotBlank(message = "MSG118: Against Party Address cannot be empty")
    private String againstPartyAddress;

    @NotBlank(message = "MSG119: District cannot be empty")
    private String district;

    @NotBlank(message = "MSG120: Division cannot be empty")
    private String division;

    @NotBlank(message = "MSG121: Please select Designation or enter manually")
    private String designation;

    @NotBlank(message = "MSG122: Please select Complaint Category or enter manually")
    private String complaintCategory;

    @NotBlank(message = "MSG123: Subject cannot be empty")
    private String subject;

    private String remarks;

    private String status;
    private String createdByUsername;
}