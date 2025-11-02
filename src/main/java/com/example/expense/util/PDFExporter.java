package com.example.expense.util;

import com.example.expense.model.Expense;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFExporter {
    public static File exportSummary(List<Expense> expenses, File outputDir) throws IOException, DocumentException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + outputDir);
        }
        String ts = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File out = new File(outputDir, "ExpenseReport_" + ts + ".pdf");

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(out));
        doc.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        doc.add(new Paragraph("Smart Expense Tracker - Summary", titleFont));
        doc.add(new Paragraph("Generated: " + LocalDate.now(), normalFont));
        doc.add(new Paragraph(" "));

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        DecimalFormat df = new DecimalFormat("0.00");
        doc.add(new Paragraph("Total Expenses: Rs. " + df.format(total), normalFont));
        doc.add(new Paragraph(" "));

        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        PdfPCell c1 = new PdfPCell(new Phrase("Category")); c1.setPadding(5); table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase("Amount")); c2.setPadding(5); table.addCell(c2);
        byCategory.forEach((cat, amt) -> {
            table.addCell(new Phrase(cat));
            table.addCell(new Phrase("Rs. " + df.format(amt)));
        });
        doc.add(table);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Items:", titleFont));

        PdfPTable items = new PdfPTable(4);
        items.setWidthPercentage(100);
        items.addCell("Date");
        items.addCell("Category");
        items.addCell("Amount");
        items.addCell("Description");
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Expense e : expenses) {
            items.addCell(e.getDate().toLocalDate().format(d));
            items.addCell(e.getCategory());
            items.addCell("Rs. " + df.format(e.getAmount()));
            items.addCell(e.getDescription() == null ? "" : e.getDescription());
        }
        doc.add(items);

        doc.close();
        return out;
    }
}
