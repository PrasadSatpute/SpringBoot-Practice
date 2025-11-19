package com.lokayukta.cims.repository;

import com.lokayukta.cims.entity.FreshComplaint;
import com.lokayukta.cims.entity.TapalEntry;
import com.lokayukta.cims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TapalEntryRepository extends JpaRepository<TapalEntry, Long> {

    // Find all Tapal entries for a specific complaint
    List<TapalEntry> findByFreshComplaintOrderByCreatedAtDesc(FreshComplaint complaint);

    // NEW: Find by FreshComplaint entity (needed for deletion)
    List<TapalEntry> findByFreshComplaint(FreshComplaint complaint);

    // Find by complaint number
    List<TapalEntry> findByComplaintNumberOrderByCreatedAtDesc(String complaintNumber);

    // Find by section
    List<TapalEntry> findBySection(User.Section section);

    // Find by tapal type
    List<TapalEntry> findByTapalType(TapalEntry.TapalType tapalType);

    // Find by letter received date range
    List<TapalEntry> findByLetterReceivedDateBetween(LocalDate startDate, LocalDate endDate);

    // Get max serial number for new entries by section and year
    @Query("SELECT MAX(CAST(SUBSTRING(t.complaintNumber, LOCATE('-', t.complaintNumber, LOCATE('-', t.complaintNumber) + 1) + 1) AS int)) " +
            "FROM TapalEntry t WHERE t.section = :section AND t.year = :year AND t.tapalType = 'NEW_ENTRY'")
    Integer findMaxSerialNumberBySectionAndYear(@Param("section") User.Section section, @Param("year") Integer year);

    // Count by section
    long countBySection(User.Section section);

    // Count by year
    long countByYear(Integer year);

    // Find recent Tapal entries
    @Query("SELECT t FROM TapalEntry t ORDER BY t.createdAt DESC")
    List<TapalEntry> findRecentEntries();
}