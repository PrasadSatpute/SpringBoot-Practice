package com.example.Document.controller;


import com.example.Document.info.Document;
import com.example.Document.info.DocumentStatus;
import com.example.Document.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    // Show form to create new document
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("statuses", DocumentStatus.values());
        return "document_form";
    }

    // Handle form submission
    @PostMapping("/create")
    public String createDocument(@ModelAttribute Document document) {
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);
        return "redirect:/documents/list";
    }

    // Show list of all documents (basic)
    @GetMapping("/list")
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        return "document_list";
    }
}
