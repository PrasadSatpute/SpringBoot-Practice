package gov.property.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "transaction_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal transactionValue;

    @Column(name = "buyer_name", nullable = false, length = 200)
    private String buyerName;

    @Column(name = "buyer_aadhar", length = 12)
    private String buyerAadhar;

    @Column(name = "buyer_pan", length = 10)
    private String buyerPan;

    @Column(name = "buyer_phone", length = 15)
    private String buyerPhone;

    @Column(name = "buyer_email", length = 100)
    private String buyerEmail;

    @Column(name = "buyer_address")
    private String buyerAddress;

    @Column(name = "seller_name", nullable = false, length = 200)
    private String sellerName;

    @Column(name = "seller_aadhar", length = 12)
    private String sellerAadhar;

    @Column(name = "seller_pan", length = 10)
    private String sellerPan;

    @Column(name = "registration_status", nullable = false, length = 20)
    private String registrationStatus; // PENDING, APPROVED, REJECTED, COMPLETED

    @Column(name = "stamp_duty", precision = 15, scale = 2)
    private BigDecimal stampDuty;

    @Column(name = "registration_fee", precision = 15, scale = 2)
    private BigDecimal registrationFee;

    @Column(name = "transfer_duty", precision = 15, scale = 2)
    private BigDecimal transferDuty;

    @Column(name = "total_tax", precision = 15, scale = 2)
    private BigDecimal totalTax;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus; // PENDING, PAID, FAILED

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
}