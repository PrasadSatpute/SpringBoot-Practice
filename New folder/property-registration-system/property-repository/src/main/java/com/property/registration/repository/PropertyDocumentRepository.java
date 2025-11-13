package com.property.registration.repository;
import com.property.registration.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PropertyDocumentRepository extends JpaRepository<PropertyDocument, Long> {
    List<PropertyDocument> findByApplication(PropertyApplication application);
    List<PropertyDocument> findByApplicationAndDocumentType(PropertyApplication application, DocumentType documentType);
}