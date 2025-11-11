package com.example.Document.util;

import com.example.Document.entity.AadhaarCard;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;

public class AadhaarPdfGenerator {

    public static boolean generatePdf(AadhaarCard card) {
        try {
            // Define output directory and ensure it exists
            String directoryPath = "src/main/resources/pdf";
            File dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Define file path
            String filePath = directoryPath + "/Aadhaar_" + card.getAadhaarNumber() + ".pdf";

            // Create PDF writer
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to PDF
            document.add(new Paragraph("Aadhaar Card Details"));
            document.add(new Paragraph("Aadhaar Number: " + card.getAadhaarNumber()));
            document.add(new Paragraph("Full Name: " + card.getFullName()));
            document.add(new Paragraph("Date of Birth: " + card.getDateOfBirth()));
            document.add(new Paragraph("Gender: " + card.getGender()));
            document.add(new Paragraph("Address: " + card.getAddress()));
            document.add(new Paragraph("Mobile Number: " + card.getMobileNumber()));
            document.add(new Paragraph("Issue Date: " + card.getIssueDate()));

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

