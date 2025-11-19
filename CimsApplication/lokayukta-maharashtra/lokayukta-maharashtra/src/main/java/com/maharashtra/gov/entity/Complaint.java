package com.maharashtra.gov.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "complaint")
@Data
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Complaint Details
    @Column(length = 10)
    private String complaintYear;

    private LocalDate receivedDate;

    @Column(unique = true, length = 50)
    private String complaintNumber;

    private LocalDate complaintDate;

    private String sectionName;
    private String tableNo;

    // Complainant Details
    private String complSurname;
    private String complFirstName;
    private String complMiddleName;

    @Column(length = 15)
    private String complMobile;

    private String complEmail;

    @Column(columnDefinition = "TEXT")
    private String complAddress;

    // Against Party Details
    private String agSurname;
    private String agFirstName;
    private String agMiddleName;

    @Column(length = 15)
    private String agMobile;

    private String agEmail;

    @Column(columnDefinition = "TEXT")
    private String agAddress;

    // Master Dropdown Values (Strings from backend)
    private String district;
    private String division;
    private String designation;
    private String otherDesignation;     // textbox when "Other" is selected

    // Complaint Summary
    private String category;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for Tapal functionality
    public String getComplainantFullName() {
        StringBuilder name = new StringBuilder();
        if (complSurname != null && !complSurname.isEmpty()) {
            name.append(complSurname).append(" ");
        }
        if (complFirstName != null && !complFirstName.isEmpty()) {
            name.append(complFirstName).append(" ");
        }
        if (complMiddleName != null && !complMiddleName.isEmpty()) {
            name.append(complMiddleName);
        }
        return name.toString().trim();
    }

    public String getAgainstPartyFullName() {
        StringBuilder name = new StringBuilder();
        if (agSurname != null && !agSurname.isEmpty()) {
            name.append(agSurname).append(" ");
        }
        if (agFirstName != null && !agFirstName.isEmpty()) {
            name.append(agFirstName).append(" ");
        }
        if (agMiddleName != null && !agMiddleName.isEmpty()) {
            name.append(agMiddleName);
        }
        return name.toString().trim();
    }

    // Alias for section compatibility
    public String getSection() {
        return this.sectionName;
    }

    public void setSection(String section) {
        this.sectionName = section;
    }

    // Alias for mobile compatibility
    public String getComplainantMobile() {
        return this.complMobile;
    }

    public String getAgainstPartyMobile() {
        return this.agMobile;
    }

    // Alias for email compatibility
    public String getComplainantEmail() {
        return this.complEmail;
    }

    public String getAgainstPartyEmail() {
        return this.agEmail;
    }

    // Alias for address compatibility
    public String getComplainantAddress() {
        return this.complAddress;
    }

    public String getAgainstPartyAddress() {
        return this.agAddress;
    }

    // Parse year as Integer for compatibility
    public Integer getComplaintYearAsInt() {
        if (complaintYear != null && !complaintYear.isEmpty()) {
            try {
                return Integer.parseInt(complaintYear);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}