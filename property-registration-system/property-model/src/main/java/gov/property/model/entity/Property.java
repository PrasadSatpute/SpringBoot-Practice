package gov.property.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_number", unique = true, nullable = false, length = 50)
    private String propertyNumber;

    @Column(name = "property_type", nullable = false, length = 50)
    private String propertyType; // RESIDENTIAL, COMMERCIAL, AGRICULTURAL, INDUSTRIAL

    @Column(nullable = false)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Column(name = "area_sqft", precision = 10, scale = 2)
    private BigDecimal areaSqft;

    @Column(name = "market_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal marketValue;

    @Column(name = "survey_number", length = 100)
    private String surveyNumber;

    @Column(name = "owner_name", nullable = false, length = 200)
    private String ownerName;

    @Column(name = "owner_aadhar", length = 12)
    private String ownerAadhar;

    @Column(name = "owner_pan", length = 10)
    private String ownerPan;

    @Column(name = "owner_phone", length = 15)
    private String ownerPhone;

    @Column(name = "owner_email", length = 100)
    private String ownerEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

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