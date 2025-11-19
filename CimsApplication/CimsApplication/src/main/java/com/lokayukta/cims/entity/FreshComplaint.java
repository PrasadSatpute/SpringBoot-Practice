package com.lokayukta.cims.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fresh_complaints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class FreshComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String complaintNumber; // Format: YYYY/XXXX

    @Column(nullable = false)
    private Integer complaintYear;

    @Column(nullable = false)
    private LocalDate receivedDate;

    @Column(nullable = false)
    private LocalDate complaintDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.Section section;

    @Column(nullable = false)
    private Integer tableNo; // 1-18

    // Complainant Details
    @Column(nullable = false, length = 100)
    private String complainantSurname;

    @Column(nullable = false, length = 100)
    private String complainantFirstName;

    @Column(length = 100)
    private String complainantMiddleName;

    @Column(length = 15)
    private String complainantMobile;

    @Column(length = 100)
    private String complainantEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String complainantAddress;

    // Against Party Details
    @Column(nullable = false, length = 100)
    private String againstPartySurname;

    @Column(nullable = false, length = 100)
    private String againstPartyFirstName;

    @Column(length = 100)
    private String againstPartyMiddleName;

    @Column(length = 15)
    private String againstPartyMobile;

    @Column(length = 100)
    private String againstPartyEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String againstPartyAddress;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String division;

    @Column(nullable = false, length = 150)
    private String designation;

    @Column(nullable = false, length = 150)
    private String complaintCategory;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

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

    public String getComplainantFullName() {
        return complainantSurname + " " + complainantFirstName +
                (complainantMiddleName != null ? " " + complainantMiddleName : "");
    }

    public String getAgainstPartyFullName() {
        return againstPartySurname + " " + againstPartyFirstName +
                (againstPartyMiddleName != null ? " " + againstPartyMiddleName : "");
    }

    public enum ComplaintStatus {
        PENDING,
        UNDER_REVIEW,
        IN_PROCESS,
        ACTIONED,
        CLOSED,
        FORWARDED
    }
}