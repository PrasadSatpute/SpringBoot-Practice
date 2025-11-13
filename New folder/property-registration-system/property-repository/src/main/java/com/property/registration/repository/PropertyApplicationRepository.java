package com.property.registration.repository;
import com.property.registration.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PropertyApplicationRepository extends JpaRepository<PropertyApplication, Long> {
    Optional<PropertyApplication> findByApplicationNumber(String applicationNumber);
    List<PropertyApplication> findByCitizen(User citizen);
    List<PropertyApplication> findByStatus(ApplicationStatus status);
    List<PropertyApplication> findByAssignedUlb(ULB ulb);

    @Query("SELECT pa FROM PropertyApplication pa WHERE pa.assignedUlb = :ulb AND pa.status = :status")
    List<PropertyApplication> findByUlbAndStatus(@Param("ulb") ULB ulb, @Param("status") ApplicationStatus status);

    @Query("SELECT pa FROM PropertyApplication pa WHERE pa.city = :city AND pa.zone = :zone AND pa.ward = :ward")
    List<PropertyApplication> findByCityAndZoneAndWard(@Param("city") String city,
                                                       @Param("zone") String zone,
                                                       @Param("ward") String ward);

    @Query("SELECT pa FROM PropertyApplication pa WHERE pa.assignedUlb.id = :ulbId AND pa.status IN :statuses")
    List<PropertyApplication> findByUlbIdAndStatusIn(@Param("ulbId") Long ulbId,
                                                     @Param("statuses") List<ApplicationStatus> statuses);
}
