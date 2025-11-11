package gov.property.web.controller;

import gov.property.common.ApiResponse;
import gov.property.model.entity.PropertyRegistration;
import gov.property.service.PdfGenerationService;
import gov.property.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final PdfGenerationService pdfGenerationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    public ResponseEntity<ApiResponse<PropertyRegistration>> createRegistration(
            @Valid @RequestBody PropertyRegistration registration) {
        try {
            PropertyRegistration savedRegistration = registrationService.createRegistration(registration);
            return ResponseEntity.ok(ApiResponse.success("Registration created successfully", savedRegistration));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create registration: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<PropertyRegistration>> updateRegistration(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRegistration registration) {
        try {
            PropertyRegistration updatedRegistration = registrationService.updateRegistration(id, registration);
            return ResponseEntity.ok(ApiResponse.success("Registration updated successfully", updatedRegistration));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update registration: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<PropertyRegistration>> approveRegistration(
            @PathVariable Long id,
            @RequestParam Long approvedBy) {
        try {
            PropertyRegistration registration = registrationService.approveRegistration(id, approvedBy);
            return ResponseEntity.ok(ApiResponse.success("Registration approved successfully", registration));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to approve registration: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<PropertyRegistration>> rejectRegistration(
            @PathVariable Long id,
            @RequestParam String remarks) {
        try {
            PropertyRegistration registration = registrationService.rejectRegistration(id, remarks);
            return ResponseEntity.ok(ApiResponse.success("Registration rejected", registration));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reject registration: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<ApiResponse<PropertyRegistration>> markPaymentComplete(
            @PathVariable Long id,
            @RequestParam String paymentReference) {
        try {
            PropertyRegistration registration = registrationService.markPaymentComplete(id, paymentReference);
            return ResponseEntity.ok(ApiResponse.success("Payment marked as complete", registration));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process payment: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyRegistration>> getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id)
                .map(registration -> ResponseEntity.ok(ApiResponse.success("Registration found", registration)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{registrationNumber}")
    public ResponseEntity<ApiResponse<PropertyRegistration>> getRegistrationByNumber(
            @PathVariable String registrationNumber) {
        return registrationService.getRegistrationByNumber(registrationNumber)
                .map(registration -> ResponseEntity.ok(ApiResponse.success("Registration found", registration)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyRegistration>>> getAllRegistrations() {
        List<PropertyRegistration> registrations = registrationService.getAllRegistrations();
        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PropertyRegistration>>> getRegistrationsByStatus(
            @PathVariable String status) {
        List<PropertyRegistration> registrations = registrationService.getRegistrationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<PropertyRegistration>>> getRegistrationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PropertyRegistration> registrations = registrationService.getRegistrationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
    }

    @GetMapping("/{id}/pdf/registration")
    public ResponseEntity<byte[]> downloadRegistrationReceipt(@PathVariable Long id) {
        try {
            PropertyRegistration registration = registrationService.getRegistrationById(id)
                    .orElseThrow(() -> new RuntimeException("Registration not found"));

            byte[] pdfBytes = pdfGenerationService.generateRegistrationReceipt(registration);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "registration_" + registration.getRegistrationNumber() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/pdf/tax")
    public ResponseEntity<byte[]> downloadTaxReceipt(@PathVariable Long id) {
        try {
            PropertyRegistration registration = registrationService.getRegistrationById(id)
                    .orElseThrow(() -> new RuntimeException("Registration not found"));

            if (!"PAID".equals(registration.getPaymentStatus())) {
                throw new RuntimeException("Payment not completed yet");
            }

            byte[] pdfBytes = pdfGenerationService.generateTaxReceipt(registration);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "tax_receipt_" + registration.getRegistrationNumber() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}