package com.maharashtra.gov.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maharashtra.gov.entity.Complaint;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>{

    @Query("SELECT MAX(c.id) FROM Complaint c")
    Long getMaxId();

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    @Query("SELECT c FROM Complaint c WHERE c.complaintYear = :year")
    List<Complaint> findByComplaintYear(@Param("year") String year);

    List<Complaint> findByTableNo(String tableNo);

    List<Complaint> findByReceivedDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM Complaint c WHERE c.sectionName = :section")
    List<Complaint> findBySection(@Param("section") String section);

    @Query("SELECT c FROM Complaint c WHERE " +
            "LOWER(c.complSurname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.complFirstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.complMobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Complaint> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complaintYear = :year")
    long countByComplaintYear(@Param("year") String year);
}