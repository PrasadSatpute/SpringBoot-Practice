package com.maharashtra.gov.service;

import java.util.List;

import com.maharashtra.gov.entity.Category;
import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.entity.Designation;
import com.maharashtra.gov.entity.District;
import com.maharashtra.gov.entity.Division;

public interface ComplaintService {
	
	List<District> getAllDistricts();
    List<Division> getAllDivisions();
    List<Designation> getAllDesignations();
    List<Category> getAllCategories();
	void saveComplaint(Complaint complaint);
	String generateComplaintNumber();

}
