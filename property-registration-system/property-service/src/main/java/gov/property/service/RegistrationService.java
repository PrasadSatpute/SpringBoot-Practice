package gov.property.service;

import gov.property.common.Constants;
import gov.property.model.entity.Property;
import gov.property.model.entity.PropertyRegistration;
import gov.property.repository.PropertyRegistrationRepository;
import gov.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final PropertyRegistrationRepository registrationRepository;
    private final PropertyRepository propertyRepository;

    public PropertyRegistration createRegistration(PropertyRegistration registration) {
        // Validate property exists
        Property property = propertyRepository.findById(registration.getProperty().getId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        registration.setProperty(property);

        // Generate registration number if not provided
        if (registration.getRegistrationNumber() == null || registration.getRegistrationNumber().isEmpty()) {
            registration.setRegistrationNumber(generateRegistrationNumber());
        }

        if (registrationRepository.existsByRegistrationNumber(registration.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        // Calculate taxes
        calculateTaxes(registration);

        // Set initial status
        if (registration.getRegistrationStatus() == null) {
            registration.setRegistrationStatus(Constants.PENDING);
        }

        if (registration.getPaymentStatus() == null) {
            registration.setPaymentStatus("PENDING");
        }

        if (registration.getRegistrationDate() == null) {
            registration.setRegistrationDate(LocalDate.now());
        }

        return registrationRepository.save(registration);
    }

    public PropertyRegistration updateRegistration(Long id, PropertyRegistration registrationDetails) {
        PropertyRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + id));

        registration.setTransactionValue(registrationDetails.getTransactionValue());
        registration.setBuyerName(registrationDetails.getBuyerName());
        registration.setBuyerAadhar(registrationDetails.getBuyerAadhar());
        registration.setBuyerPan(registrationDetails.getBuyerPan());
        registration.setBuyerPhone(registrationDetails.getBuyerPhone());
        registration.setBuyerEmail(registrationDetails.getBuyerEmail());
        registration.setBuyerAddress(registrationDetails.getBuyerAddress());
        registration.setSellerName(registrationDetails.getSellerName());
        registration.setSellerAadhar(registrationDetails.getSellerAadhar());
        registration.setSellerPan(registrationDetails.getSellerPan());
        registration.setRemarks(registrationDetails.getRemarks());

        // Recalculate taxes if transaction value changed
        calculateTaxes(registration);

        return registrationRepository.save(registration);
    }

    public PropertyRegistration approveRegistration(Long id, Long approvedBy) {
        PropertyRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!registration.getRegistrationStatus().equals(Constants.PENDING)) {
            throw new RuntimeException("Only pending registrations can be approved");
        }

        registration.setRegistrationStatus(Constants.APPROVED);
        registration.setApprovedBy(approvedBy);
        registration.setApprovedDate(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    public PropertyRegistration rejectRegistration(Long id, String remarks) {
        PropertyRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setRegistrationStatus(Constants.REJECTED);
        registration.setRemarks(remarks);

        return registrationRepository.save(registration);
    }

    public PropertyRegistration markPaymentComplete(Long id, String paymentReference) {
        PropertyRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!registration.getRegistrationStatus().equals(Constants.APPROVED)) {
            throw new RuntimeException("Only approved registrations can be paid");
        }

        registration.setPaymentStatus("PAID");
        registration.setPaymentReference(paymentReference);
        registration.setPaymentDate(LocalDateTime.now());
        registration.setRegistrationStatus(Constants.COMPLETED);

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public Optional<PropertyRegistration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PropertyRegistration> getRegistrationByNumber(String registrationNumber) {
        return registrationRepository.findByRegistrationNumber(registrationNumber);
    }

    @Transactional(readOnly = true)
    public List<PropertyRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PropertyRegistration> getRegistrationsByStatus(String status) {
        return registrationRepository.findByRegistrationStatus(status);
    }

    @Transactional(readOnly = true)
    public List<PropertyRegistration> getRegistrationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return registrationRepository.findByDateRange(startDate, endDate);
    }

    private void calculateTaxes(PropertyRegistration registration) {
        BigDecimal transactionValue = registration.getTransactionValue();

        // Calculate Stamp Duty
        BigDecimal stampDuty = transactionValue
                .multiply(BigDecimal.valueOf(Constants.STAMP_DUTY_RATE / 100))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate Registration Fee
        BigDecimal registrationFee = transactionValue
                .multiply(BigDecimal.valueOf(Constants.REGISTRATION_FEE_RATE / 100))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate Transfer Duty
        BigDecimal transferDuty = transactionValue
                .multiply(BigDecimal.valueOf(Constants.TRANSFER_DUTY_RATE / 100))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate Total Tax
        BigDecimal totalTax = stampDuty.add(registrationFee).add(transferDuty);

        registration.setStampDuty(stampDuty);
        registration.setRegistrationFee(registrationFee);
        registration.setTransferDuty(transferDuty);
        registration.setTotalTax(totalTax);
    }

    private String generateRegistrationNumber() {
        return Constants.REGISTRATION_NUMBER_PREFIX + "-" +
                LocalDate.now().getYear() + "-" +
                System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}