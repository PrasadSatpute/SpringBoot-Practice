package com.maharashtra.gov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.maharashtra.gov.entity.Complaint;
import com.maharashtra.gov.service.ComplaintService;

@Controller
@RequestMapping("/inward")
public class ComplaintController {
	
	
	@Autowired
    private ComplaintService complaintService;

    @GetMapping("/register")
    public String showForm(Model model) {

        model.addAttribute("complaint", new Complaint());

        model.addAttribute("districtList", complaintService.getAllDistricts());
        model.addAttribute("divisionList", complaintService.getAllDivisions());
        model.addAttribute("designationList", complaintService.getAllDesignations());
        model.addAttribute("categoryList", complaintService.getAllCategories());
        
        

        return "complaint-registration";
    }
    
    @PostMapping("/save")
    public String saveComplaintFrom(@ModelAttribute("complaint") Complaint complaint,
            RedirectAttributes redirectAttributes,
            Model model) 
    {


    	try {
            complaintService.saveComplaint(complaint);
            redirectAttributes.addFlashAttribute("msg", "Complaint saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error while saving complaint!");
        }

        return "redirect:/complaint/register";
    }

}
