package com.property.registration.repository;
import com.property.registration.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ULBRepository extends JpaRepository<ULB, Long> {
    Optional<ULB> findByUlbCode(String ulbCode);
    List<ULB> findByCity(String city);
    List<ULB> findByCityAndZone(String city, String zone);
    List<ULB> findByCityAndZoneAndWard(String city, String zone, String ward);
    List<ULB> findByUlbLevel(ULBLevel ulbLevel);
    boolean existsByUlbCode(String ulbCode);
}