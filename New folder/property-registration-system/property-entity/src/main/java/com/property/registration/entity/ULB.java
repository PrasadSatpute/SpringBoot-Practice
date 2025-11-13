package com.property.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ulb")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ULB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ulbCode;

    @Column(nullable = false, length = 200)
    private String ulbName;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String zone;

    @Column(nullable = false, length = 100)
    private String ward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ULBLevel ulbLevel;

    @Column(length = 500)
    private String address;

    @Column(length = 15)
    private String contactNumber;

    @Column(length = 100)
    private String contactEmail;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "ulb", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "assignedUlb", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PropertyApplication> applications = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
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

