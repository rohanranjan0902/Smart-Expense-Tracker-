package com.example.expense.util;

import com.example.expense.model.Expense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public static File exportExpenses(List<Expense> expenses, File outputDir) throws IOException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + outputDir);
        }
        File out = new File(outputDir, "expenses.csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
            bw.write("ID,Category,Amount,Date,Description\n");
            for (Expense e : expenses) {
                String line = String.format("%d,%s,%.2f,%s,%s",
                        e.getId(),
                        escape(e.getCategory()),
                        e.getAmount(),
                        e.getDate(),
                        escape(e.getDescription() == null ? "" : e.getDescription()));
                bw.write(line);
                bw.write("\n");
            }
        }
        return out;
    }

    private static String escape(String s) {
        if (s == null) return "";
        String q = s.replace("\"", "\"\"");
        if (q.contains(",") || q.contains("\n") || q.contains("\r")) {
            return "\"" + q + "\"";
        }
        return q;
    }
}
