package com.maharashtra.gov.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maharashtra.gov.entity.District;

public interface  DistrictRepo extends JpaRepository<District, Long> {
	
	
}