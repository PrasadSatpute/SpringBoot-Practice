package com.property.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_ulb_id")
    private ULB assignedUlb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Column(nullable = false, length = 500)
    private String propertyAddress;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String zone;

    @Column(nullable = false, length = 100)
    private String ward;

    @Column(length = 50)
    private String surveyNumber;

    @Column(length = 50)
    private String plotNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal propertyArea;

    @Column(length = 20)
    private String areaUnit;

    @Column(precision = 12, scale = 2)
    private BigDecimal propertyValue;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal registrationFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(length = 100)
    private String paymentTransactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level1_approver_id")
    private User level1Approver;

    private LocalDateTime level1ApprovalDate;

    @Column(length = 1000)
    private String level1Remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level2_approver_id")
    private User level2Approver;

    private LocalDateTime level2ApprovalDate;

    @Column(length = 1000)
    private String level2Remarks;

    @Column(length = 500)
    private String rejectionReason;

    @Column(length = 200)
    private String certificatePath;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyDocument> documents = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        generateApplicationNumber();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private void generateApplicationNumber() {
        if (applicationNumber == null) {
            this.applicationNumber = "APP" + System.currentTimeMillis();
        }
    }
}

enum PropertyType {
    RESIDENTIAL,
    COMMERCIAL,
    AGRICULTURAL,
    INDUSTRIAL,
    PLOT
}

