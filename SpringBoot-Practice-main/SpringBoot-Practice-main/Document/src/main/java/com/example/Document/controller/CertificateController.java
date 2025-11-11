package com.example.Document.controller;

import com.example.Document.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;

@Controller
public class CertificateController {

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "form";
    }

    @PostMapping("/certificate")
    public String showCertificate(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        return "certificate";
    }

    @PostMapping("/download-pdf")
    public String downloadCertificate(@ModelAttribute User user, Model model) {
        try {
            // Define folder path
            String folderPath = "src/main/resources/certificates/";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            // Create PDF file
            String fileName = folderPath + "Certificate_" + user.getName().replaceAll("\\s+", "_") + ".pdf";
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // Add content
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD, BaseColor.BLACK);
            Font subtitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL, BaseColor.DARK_GRAY);

            Paragraph logo = new Paragraph("🏢 Private Company Ltd.\n\n", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            Paragraph title = new Paragraph("Certificate of Participation\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph body = new Paragraph("This certifies that\n\n", subtitleFont);
            body.setAlignment(Element.ALIGN_CENTER);
            document.add(body);

            Paragraph name = new Paragraph(user.getName() + "\n\n", new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD, BaseColor.BLUE));
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

            Paragraph details = new Paragraph(
                    "has successfully submitted their details as part of our company program.\n\n",
                    subtitleFont
            );
            details.setAlignment(Element.ALIGN_CENTER);
            document.add(details);

            Paragraph info = new Paragraph(
                    "Age: " + user.getAge() + "\n" +
                            "Gender: " + user.getGender() + "\n" +
                            "Contact No: " + user.getContactNo() + "\n\n",
                    subtitleFont
            );
            info.setAlignment(Element.ALIGN_CENTER);
            document.add(info);

            Paragraph footer = new Paragraph("Issued by Private Company Ltd.", subtitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            model.addAttribute("user", user);
            model.addAttribute("message", "PDF saved successfully at: " + fileName);

        } catch (Exception e) {
            model.addAttribute("error", "Error generating PDF: " + e.getMessage());
        }

        return "certificate";
    }
}
