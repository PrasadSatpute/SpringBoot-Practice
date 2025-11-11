package gov.property.repository;

import gov.property.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findByPropertyNumber(String propertyNumber);

    List<Property> findByPropertyType(String propertyType);

    List<Property> findByCity(String city);

    List<Property> findByOwnerAadhar(String ownerAadhar);

    List<Property> findByOwnerPan(String ownerPan);

    @Query("SELECT p FROM Property p WHERE " +
            "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.ownerName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Property> searchProperties(@Param("keyword") String keyword);

    boolean existsByPropertyNumber(String propertyNumber);
}