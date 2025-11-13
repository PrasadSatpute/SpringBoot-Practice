package com.property.registration.service;

import com.property.registration.entity.*;
import com.property.registration.repository.PropertyApplicationRepository;
import com.property.registration.repository.PropertyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyApplicationService {

    private final PropertyApplicationRepository applicationRepository;
    private final PropertyDocumentRepository documentRepository;
    private final ULBService ulbService;

    private static final String UPLOAD_DIR = "uploads/documents/";

    public PropertyApplication createApplication(PropertyApplication application, User citizen) {
        application.setCitizen(citizen);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setPaymentStatus(PaymentStatus.PENDING);

        ULB assignedULB = ulbService.findULBByLocation(
                application.getCity(),
                application.getZone(),
                application.getWard()
        );
        application.setAssignedUlb(assignedULB);

        return applicationRepository.save(application);
    }

    public PropertyApplication updateApplication(Long applicationId, PropertyApplication updatedApplication, Long userId) {
        PropertyApplication existingApp = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!existingApp.getCitizen().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (existingApp.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new RuntimeException("Cannot update application in current status");
        }

        existingApp.setPropertyType(updatedApplication.getPropertyType());
        existingApp.setPropertyAddress(updatedApplication.getPropertyAddress());
        existingApp.setSurveyNumber(updatedApplication.getSurveyNumber());
        existingApp.setPlotNumber(updatedApplication.getPlotNumber());
        existingApp.setPropertyArea(updatedApplication.getPropertyArea());
        existingApp.setAreaUnit(updatedApplication.getAreaUnit());
        existingApp.setPropertyValue(updatedApplication.getPropertyValue());

        return applicationRepository.save(existingApp);
    }

    public void deleteApplication(Long applicationId, Long userId) {
        PropertyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getCitizen().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (application.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new RuntimeException("Cannot delete application in current status");
        }

        applicationRepository.delete(application);
    }

    public PropertyDocument uploadDocument(Long applicationId, MultipartFile file, DocumentType documentType) throws IOException {
        PropertyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        PropertyDocument document = new PropertyDocument();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setDocumentName(file.getOriginalFilename());
        document.setDocumentPath(filePath.toString());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());

        return documentRepository.save(document);
    }

    public PropertyApplication processPayment(Long applicationId, String transactionId, BigDecimal paidAmount) {
        PropertyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setPaidAmount(paidAmount);
        application.setPaymentTransactionId(transactionId);
        application.setPaymentStatus(PaymentStatus.COMPLETED);
        application.setPaymentDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PAYMENT_COMPLETED);

        return applicationRepository.save(application);
    }

    public PropertyApplication level1Approval(Long applicationId, User approver, boolean approved, String remarks) {
        PropertyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.PAYMENT_COMPLETED &&
                application.getStatus() != ApplicationStatus.LEVEL1_REVIEW) {
            throw new RuntimeException("Application not ready for Level 1 approval");
        }

        application.setLevel1Approver(approver);
        application.setLevel1ApprovalDate(LocalDateTime.now());
        application.setLevel1Remarks(remarks);

        if (approved) {
            application.setStatus(ApplicationStatus.LEVEL1_APPROVED);
        } else {
            application.setStatus(ApplicationStatus.LEVEL1_REJECTED);
            application.setRejectionReason(remarks);
        }

        return applicationRepository.save(application);
    }

    public PropertyApplication level2Approval(Long applicationId, User approver, boolean approved, String remarks) {
        PropertyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.LEVEL1_APPROVED &&
                application.getStatus() != ApplicationStatus.LEVEL2_REVIEW) {
            throw new RuntimeException("Application not ready for Level 2 approval");
        }

        application.setLevel2Approver(approver);
        application.setLevel2ApprovalDate(LocalDateTime.now());
        application.setLevel2Remarks(remarks);

        if (approved) {
            application.setStatus(ApplicationStatus.APPROVED);
        } else {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReason(remarks);
        }

        return applicationRepository.save(application);
    }

    public List<PropertyApplication> getApplicationsByCitizen(User citizen) {
        return applicationRepository.findByCitizen(citizen);
    }

    public List<PropertyApplication> getApplicationsByULB(ULB ulb) {
        return applicationRepository.findByAssignedUlb(ulb);
    }

    public List<PropertyApplication> getApplicationsByULBAndStatus(ULB ulb, ApplicationStatus status) {
        return applicationRepository.findByUlbAndStatus(ulb, status);
    }

    public PropertyApplication getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public PropertyApplication getApplicationByNumber(String applicationNumber) {
        return applicationRepository.findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public List<PropertyDocument> getApplicationDocuments(Long applicationId) {
        PropertyApplication application = getApplicationById(applicationId);
        return documentRepository.findByApplication(application);
    }
}