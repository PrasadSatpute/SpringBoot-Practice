package gov.property.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import gov.property.model.entity.Property;
import gov.property.model.entity.PropertyRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class PdfGenerationService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    public byte[] generateRegistrationReceipt(PropertyRegistration registration) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Header
            addHeader(document);
            document.add(new Paragraph("\n"));

            // Title
            Paragraph title = new Paragraph("PROPERTY REGISTRATION RECEIPT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Registration Details
            addRegistrationDetails(document, registration);
            document.add(new Paragraph("\n"));

            // Property Details
            addPropertyDetails(document, registration.getProperty());
            document.add(new Paragraph("\n"));

            // Buyer and Seller Details
            addPartyDetails(document, registration);
            document.add(new Paragraph("\n"));

            // Tax Calculation
            addTaxCalculation(document, registration);
            document.add(new Paragraph("\n"));

            // Footer
            addFooter(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating registration receipt PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    public byte[] generateTaxReceipt(PropertyRegistration registration) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Header
            addHeader(document);
            document.add(new Paragraph("\n"));

            // Title
            Paragraph title = new Paragraph("TAX PAYMENT RECEIPT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Payment Details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addTableRow(table, "Receipt Number:", registration.getRegistrationNumber());
            addTableRow(table, "Payment Date:",
                    registration.getPaymentDate() != null ?
                            registration.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) : "N/A");
            addTableRow(table, "Payment Reference:", registration.getPaymentReference());
            addTableRow(table, "Payment Status:", registration.getPaymentStatus());

            document.add(table);
            document.add(new Paragraph("\n"));

            // Property Details
            addPropertyDetails(document, registration.getProperty());
            document.add(new Paragraph("\n"));

            // Tax Breakdown
            addTaxCalculation(document, registration);
            document.add(new Paragraph("\n"));

            // Total Amount Paid
            Paragraph totalPaid = new Paragraph(
                    "Total Amount Paid: " + formatCurrency(registration.getTotalTax()),
                    new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED)
            );
            totalPaid.setAlignment(Element.ALIGN_CENTER);
            document.add(totalPaid);
            document.add(new Paragraph("\n"));

            // Footer
            addFooter(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating tax receipt PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph("GOVERNMENT OF INDIA", HEADER_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph subHeader = new Paragraph("Property Registration Department", NORMAL_FONT);
        subHeader.setAlignment(Element.ALIGN_CENTER);
        document.add(subHeader);

        document.add(new LineSeparator());
    }

    private void addRegistrationDetails(Document document, PropertyRegistration registration)
            throws DocumentException {
        Paragraph sectionTitle = new Paragraph("Registration Details", HEADER_FONT);
        document.add(sectionTitle);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Registration Number:", registration.getRegistrationNumber());
        addTableRow(table, "Registration Date:",
                registration.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        addTableRow(table, "Registration Status:", registration.getRegistrationStatus());
        addTableRow(table, "Transaction Value:", formatCurrency(registration.getTransactionValue()));

        document.add(table);
    }

    private void addPropertyDetails(Document document, Property property) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("Property Details", HEADER_FONT);
        document.add(sectionTitle);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Property Number:", property.getPropertyNumber());
        addTableRow(table, "Property Type:", property.getPropertyType());
        addTableRow(table, "Address:", property.getAddress());
        addTableRow(table, "City:", property.getCity());
        addTableRow(table, "State:", property.getState());
        addTableRow(table, "Survey Number:", property.getSurveyNumber());
        addTableRow(table, "Area (Sq.Ft):", property.getAreaSqft().toString());
        addTableRow(table, "Market Value:", formatCurrency(property.getMarketValue()));

        document.add(table);
    }

    private void addPartyDetails(Document document, PropertyRegistration registration)
            throws DocumentException {
        // Buyer Details
        Paragraph buyerTitle = new Paragraph("Buyer Details", HEADER_FONT);
        document.add(buyerTitle);
        document.add(new Paragraph(" "));

        PdfPTable buyerTable = new PdfPTable(2);
        buyerTable.setWidthPercentage(100);

        addTableRow(buyerTable, "Name:", registration.getBuyerName());
        addTableRow(buyerTable, "Aadhar:", registration.getBuyerAadhar());
        addTableRow(buyerTable, "PAN:", registration.getBuyerPan());
        addTableRow(buyerTable, "Phone:", registration.getBuyerPhone());
        addTableRow(buyerTable, "Email:", registration.getBuyerEmail());

        document.add(buyerTable);
        document.add(new Paragraph("\n"));

        // Seller Details
        Paragraph sellerTitle = new Paragraph("Seller Details", HEADER_FONT);
        document.add(sellerTitle);
        document.add(new Paragraph(" "));

        PdfPTable sellerTable = new PdfPTable(2);
        sellerTable.setWidthPercentage(100);

        addTableRow(sellerTable, "Name:", registration.getSellerName());
        addTableRow(sellerTable, "Aadhar:", registration.getSellerAadhar());
        addTableRow(sellerTable, "PAN:", registration.getSellerPan());

        document.add(sellerTable);
    }

    private void addTaxCalculation(Document document, PropertyRegistration registration)
            throws DocumentException {
        Paragraph sectionTitle = new Paragraph("Tax Calculation", HEADER_FONT);
        document.add(sectionTitle);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Stamp Duty:", formatCurrency(registration.getStampDuty()));
        addTableRow(table, "Registration Fee:", formatCurrency(registration.getRegistrationFee()));
        addTableRow(table, "Transfer Duty:", formatCurrency(registration.getTransferDuty()));

        // Add total row with bold font
        PdfPCell labelCell = new PdfPCell(new Phrase("Total Tax:", BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatCurrency(registration.getTotalTax()), BOLD_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);

        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph("\n\n"));
        document.add(new LineSeparator());

        Paragraph footer = new Paragraph(
                "This is a computer-generated document and does not require a signature.\n" +
                        "For any queries, please contact the Property Registration Department.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph timestamp = new Paragraph(
                "Generated on: " + java.time.LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY)
        );
        timestamp.setAlignment(Element.ALIGN_CENTER);
        document.add(timestamp);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "₹0.00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount);
    }
}