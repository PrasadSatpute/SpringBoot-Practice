package com.lokayukta.cims.repository;

import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreshComplaintRepository extends JpaRepository<FreshComplaint, Long> {

    Optional<FreshComplaint> findByComplaintNumber(String complaintNumber);

    @Query("SELECT MAX(CAST(SUBSTRING(c.complaintNumber, 6) AS int)) " +
            "FROM FreshComplaint c WHERE c.complaintYear = :year")
    Integer findMaxSerialNumberByYear(@Param("year") Integer year);

    List<FreshComplaint> findByComplaintYear(Integer year);

    List<FreshComplaint> findBySection(User.Section section);

    List<FreshComplaint> findByTableNo(Integer tableNo);

    List<FreshComplaint> findByReceivedDateBetween(LocalDate startDate, LocalDate endDate);

    List<FreshComplaint> findByStatus(FreshComplaint.ComplaintStatus status);

    @Query("SELECT c FROM FreshComplaint c WHERE " +
            "LOWER(c.complainantSurname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.complainantFirstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FreshComplaint> searchByKeyword(@Param("keyword") String keyword);

    long countByComplaintYear(Integer year);

    long countBySection(User.Section section);

    long countByStatus(FreshComplaint.ComplaintStatus status);
}