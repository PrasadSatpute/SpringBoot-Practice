package com.maharashtra.gov.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maharashtra.gov.entity.Division;

public interface  DivisionRepo extends JpaRepository<Division, Long> {

}
