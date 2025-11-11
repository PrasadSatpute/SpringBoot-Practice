package com.example.Document.repository.adhar;


import com.example.Document.entity.AadhaarCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AadhaarCardRepository extends JpaRepository<AadhaarCard, String> {
}
