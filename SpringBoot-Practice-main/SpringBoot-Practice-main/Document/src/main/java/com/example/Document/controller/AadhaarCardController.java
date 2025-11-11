package com.example.Document.controller;

import com.example.Document.entity.AadhaarCard;
import com.example.Document.repository.adhar.AadhaarCardRepository;
import com.example.Document.util.AadhaarPdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/aadhaar")
public class AadhaarCardController {

    @Autowired
    private AadhaarCardRepository aadhaarCardRepository;

    // Show form to create a new Aadhaar card
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("aadhaarCard", new AadhaarCard());
        return "aadhaar_form";
    }

    // Save a new or edited Aadhaar card
    @PostMapping("/save")
    public String saveAadhaarCard(@ModelAttribute("aadhaarCard") AadhaarCard aadhaarCard) {
        try {
            aadhaarCardRepository.save(aadhaarCard);

            boolean pdfCreated = AadhaarPdfGenerator.generatePdf(aadhaarCard);
            if (pdfCreated) {
                System.out.println("Success");
            } else {
                System.out.println("Saved but PDF generation failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed");;
        }
        return "redirect:/aadhaar/list";
    }


    // List all Aadhaar cards
    @GetMapping("/list")
    public String listAadhaarCards(Model model) {
        model.addAttribute("aadhaarCards", aadhaarCardRepository.findAll());
        for (AadhaarCard aadhaar : aadhaarCardRepository.findAll())
        {
            System.out.println("Aadhaar Detail = " + aadhaar.toString());
        }
        return "aadhaar_list";
    }

    // Edit Aadhaar card
    @GetMapping("/edit/{aadhaarNumber}")
    public String editAadhaarCard(@PathVariable String aadhaarNumber, Model model) {
        Optional<AadhaarCard> card = aadhaarCardRepository.findById(aadhaarNumber);
        card.ifPresent(a -> model.addAttribute("aadhaarCard", a));
        return "aadhaar_form";
    }

    // Delete Aadhaar card
    @GetMapping("/delete/{aadhaarNumber}")
    public String deleteAadhaarCard(@PathVariable String aadhaarNumber) {
        aadhaarCardRepository.deleteById(aadhaarNumber);
        return "redirect:/aadhaar/list";
    }

    @GetMapping("/showPopup")
    public String showPopup(Model model) {
        model.addAttribute("dynamicMessage", "Message from the server!");
        return "myPage"; // Assuming 'myPage.html' contains the modal
    }
}
