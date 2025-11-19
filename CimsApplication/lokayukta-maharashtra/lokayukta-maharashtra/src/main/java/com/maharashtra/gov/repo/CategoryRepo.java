package com.maharashtra.gov.repo;



import org.springframework.data.jpa.repository.JpaRepository;

import com.maharashtra.gov.entity.Category;


public interface CategoryRepo extends JpaRepository<Category, Long>{

}
	