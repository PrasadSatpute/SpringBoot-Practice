package gov.property.repository;

import gov.property.model.entity.PropertyRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRegistrationRepository extends JpaRepository<PropertyRegistration, Long> {

    Optional<PropertyRegistration> findByRegistrationNumber(String registrationNumber);

    List<PropertyRegistration> findByRegistrationStatus(String status);

    List<PropertyRegistration> findByPaymentStatus(String paymentStatus);

    List<PropertyRegistration> findByPropertyId(Long propertyId);

    List<PropertyRegistration> findByBuyerAadhar(String buyerAadhar);

    List<PropertyRegistration> findBySellerAadhar(String sellerAadhar);

    @Query("SELECT pr FROM PropertyRegistration pr WHERE pr.registrationDate BETWEEN :startDate AND :endDate")
    List<PropertyRegistration> findByDateRange(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(pr) FROM PropertyRegistration pr WHERE pr.registrationStatus = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT SUM(pr.totalTax) FROM PropertyRegistration pr WHERE " +
            "pr.paymentStatus = 'PAID' AND pr.registrationDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    boolean existsByRegistrationNumber(String registrationNumber);
}