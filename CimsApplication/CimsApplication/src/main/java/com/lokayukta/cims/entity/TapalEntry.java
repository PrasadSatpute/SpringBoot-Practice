package com.lokayukta.cims.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tapal_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class TapalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Fresh Complaint (null for new RTI/Cash/Administrative/NA entries)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private FreshComplaint freshComplaint;

    @Column(length = 20)
    private String complaintNumber; // For existing complaints or auto-generated for new entries

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate letterReceivedDate;

    @Column(length = 50)
    private String inwardNumber; // Only for new entries (RTI/Cash/Admin/NA)

    @Column(length = 150)
    private String receiverName; // Only for new entries

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subjectLine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.Section section;

    @Enumerated(EnumType.STRING)
    private LetterFrom letterReceivedFrom; // COMPLAINANT or AGAINST_PARTY

    @Enumerated(EnumType.STRING)
    private LetterRelatedTo letterRelatedTo; // COMPLAINANT or AGAINST_PARTY

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    private TapalType tapalType; // EXISTING_COMPLAINT or NEW_ENTRY

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

    public enum LetterFrom {
        COMPLAINANT,
        AGAINST_PARTY
    }

    public enum LetterRelatedTo {
        COMPLAINANT,
        AGAINST_PARTY
    }

    public enum TapalType {
        EXISTING_COMPLAINT,  // Tapal entry for existing LA/ULA complaint
        NEW_ENTRY           // New entry for RTI/Cash/Administrative/NA
    }
}