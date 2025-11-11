package com.example.Document.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "aadhaar_cards")
public class AadhaarCard {

    @Id
    @Column(name = "aadhaar_number", length = 12, unique = true, nullable = false)
    private String aadhaarNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    @Column(name = "mobile_number", length = 10)
    private String mobileNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    // Constructors
    public AadhaarCard() {}

    public AadhaarCard(String aadhaarNumber, String fullName, LocalDate dateOfBirth,
                       String gender, String address, String mobileNumber, LocalDate issueDate) {
        this.aadhaarNumber = aadhaarNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "AadhaarCard{" +
                "aadhaarNumber='" + aadhaarNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", issueDate=" + issueDate +
                '}';
    }
}
