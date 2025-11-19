package com.maharashtra.gov.repo;

import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.entity.TapalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TapalEntryRepository extends JpaRepository<TapalEntry, Long> {

    List<TapalEntry> findByFreshComplaintOrderByCreatedAtDesc(Complaint complaint);

    List<TapalEntry> findByComplaintNumberOrderByCreatedAtDesc(String complaintNumber);

    List<TapalEntry> findByTapalType(TapalEntry.TapalType tapalType);

    List<TapalEntry> findByLetterReceivedDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM TapalEntry t WHERE t.section = :section ORDER BY t.createdAt DESC")
    List<TapalEntry> findBySection(@Param("section") String section);

    long countByYear(Integer year);

    @Query("SELECT COUNT(t) FROM TapalEntry t WHERE t.section = :section")
    long countBySection(@Param("section") String section);

    @Query("SELECT t FROM TapalEntry t ORDER BY t.createdAt DESC")
    List<TapalEntry> findRecentEntries();

    @Query("SELECT MAX(CAST(SUBSTRING(t.complaintNumber, LOCATE('-', t.complaintNumber, LOCATE('-', t.complaintNumber) + 1) + 1) AS int)) " +
            "FROM TapalEntry t WHERE t.section = :section AND t.year = :year")
    Integer findMaxSerialNumberBySectionAndYear(@Param("section") String section, @Param("year") Integer year);
}