package com.example.mahasbr.repository;

import com.example.mahasbr.entity.VisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitorCountRepository extends JpaRepository<VisitorCount, Long> {
    Optional<VisitorCount> findByDate(LocalDate date);
    Optional<VisitorCount> findTopByOrderByDateDesc();
}