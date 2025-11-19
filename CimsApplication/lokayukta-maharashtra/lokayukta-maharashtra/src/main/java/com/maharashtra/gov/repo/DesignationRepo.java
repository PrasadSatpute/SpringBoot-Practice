package com.maharashtra.gov.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maharashtra.gov.entity.Designation;

public interface DesignationRepo extends JpaRepository<Designation, Long>{

}
