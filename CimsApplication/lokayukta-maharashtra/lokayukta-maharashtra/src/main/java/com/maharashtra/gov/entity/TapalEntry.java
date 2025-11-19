package com.maharashtra.gov.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint freshComplaint;

    @Column(length = 20)
    private String complaintNumber;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate letterReceivedDate;

    @Column(length = 50)
    private String inwardNumber;

    @Column(length = 150)
    private String receiverName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subjectLine;

    @Enumerated(EnumType.STRING)
    private LetterFrom letterReceivedFrom;

    @Enumerated(EnumType.STRING)
    private LetterRelatedTo letterRelatedTo;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    private TapalType tapalType;

    // Store section as String to match Complaint entity
    @Column(length = 50)
    private String section;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Store username instead of User entity reference
    @Column(length = 100)
    private String createdBy;

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
        EXISTING_COMPLAINT,
        NEW_ENTRY
    }
}