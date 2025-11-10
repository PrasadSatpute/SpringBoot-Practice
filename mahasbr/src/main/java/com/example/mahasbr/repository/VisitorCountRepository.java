package com.example.mahasbr.repository;

import com.example.mahasbr.entity.VisitorCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitorCountRepository extends JpaRepository<VisitorCount, Long> {

    Optional<VisitorCount> findByDate(LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT vc FROM VisitorCount vc WHERE vc.date = :date")
    Optional<VisitorCount> findByDateWithLock(@Param("date") LocalDate date);

    Optional<VisitorCount> findTopByOrderByDateDesc();
}