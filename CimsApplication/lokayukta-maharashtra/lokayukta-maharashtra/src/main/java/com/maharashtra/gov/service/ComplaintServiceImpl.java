package com.maharashtra.gov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maharashtra.gov.entity.Category;
import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.entity.Designation;
import com.maharashtra.gov.entity.District;
import com.maharashtra.gov.entity.Division;
import com.maharashtra.gov.repo.CategoryRepo;
import com.maharashtra.gov.repo.ComplaintRepository;
import com.maharashtra.gov.repo.DesignationRepo;
import com.maharashtra.gov.repo.DistrictRepo;
import com.maharashtra.gov.repo.DivisionRepo;

@Service
public class ComplaintServiceImpl implements ComplaintService{
	
	@Autowired
    private DistrictRepo districtRepo;

    @Autowired DivisionRepo divisionRepo;

    @Autowired
    private DesignationRepo designationRepo;

    @Autowired
    private CategoryRepo categoryRepo;
    
    @Autowired
    private ComplaintRepository complaintRepo;

    @Override
    public List<District> getAllDistricts() {
        return districtRepo.findAll();
    }

    @Override
    public List<Division> getAllDivisions() {
        return divisionRepo.findAll();
    }

    @Override
    public List<Designation> getAllDesignations() {
        return designationRepo.findAll();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

	@Override
	public void saveComplaint(Complaint complaint) {
		
		if (complaint.getComplaintNumber() == null || complaint.getComplaintNumber().isEmpty()) {
            complaint.setComplaintNumber(generateComplaintNumber());
        }
		complaintRepo.save(complaint);
	}

	@Override
    public String generateComplaintNumber() {

        Long maxId = complaintRepo.getMaxId();
        if (maxId == null) {
            maxId = 0L;
        }

        long nextId = maxId + 1;

        String year = String.valueOf(java.time.Year.now().getValue());

        return year + "-" + String.format("%06d", nextId);
    }
}
