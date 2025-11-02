package com.example.expense.ui;

import com.example.expense.dao.ExpenseDAO;
import com.example.expense.model.Expense;
import com.example.expense.util.CSVExporter;
import com.example.expense.util.PDFExporter;
import com.lowagie.text.DocumentException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final int userId;
    private final String username;
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    private final JComboBox<String> categoryFilter = new JComboBox<>();
    private final JTextField fromField = new JTextField(10); // yyyy-MM-dd
    private final JTextField toField = new JTextField(10);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID","Category","Amount","Date","Description"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public DashboardFrame(int userId, String username) {
        super("Smart Expense Tracker - " + username);
        this.userId = userId;
        this.username = username;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel filters = new JPanel();
        filters.add(new JLabel("Category:"));
        filters.add(categoryFilter);
        filters.add(new JLabel("From:"));
        filters.add(fromField);
        filters.add(new JLabel("To:"));
        filters.add(toField);
        JButton apply = new JButton("Apply Filters");
        apply.addActionListener(e -> refresh());
        filters.add(apply);

        JPanel actions = new JPanel();
        JButton addBtn = new JButton("Add Expense");
        JButton editBtn = new JButton("Edit Selected");
        JButton delBtn = new JButton("Delete Selected");
        JButton csvBtn = new JButton("Export CSV");
        JButton pdfBtn = new JButton("Generate PDF");
        JButton logoutBtn = new JButton("Logout");

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        csvBtn.addActionListener(e -> onExportCSV());
        pdfBtn.addActionListener(e -> onExportPDF());
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        actions.add(addBtn); actions.add(editBtn); actions.add(delBtn);
        actions.add(csvBtn); actions.add(pdfBtn); actions.add(logoutBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        setLayout(new BorderLayout());
        add(filters, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loadCategories();
        prefillDatesCurrentMonth();
        refresh();
    }

    private void loadCategories() {
        categoryFilter.removeAllItems();
        categoryFilter.addItem("All");
        for (String c : expenseDAO.listCategories(userId)) categoryFilter.addItem(c);
    }

    private void prefillDatesCurrentMonth() {
        LocalDate now = LocalDate.now();
        LocalDate first = now.withDayOfMonth(1);
        fromField.setText(first.toString());
        toField.setText(now.toString());
    }

    private void refresh() {
        String category = (String) categoryFilter.getSelectedItem();
        Date from = parseDate(fromField.getText().trim());
        Date to = parseDate(toField.getText().trim());
        List<Expense> data = expenseDAO.listExpenses(userId, category, from, to);
        tableModel.setRowCount(0);
        for (Expense e : data) {
            tableModel.addRow(new Object[]{e.getId(), e.getCategory(), e.getAmount(), e.getDate(), e.getDescription()});
        }
    }

    private Date parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Date.valueOf(s); } catch (Exception e) { return null; }
    }

    private void onAdd() {
        AddExpenseDialog dlg = new AddExpenseDialog(this, userId, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) { loadCategories(); refresh(); }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row to edit"); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);
        Expense e = new Expense();
        e.setId(id);
        e.setUserId(userId);
        e.setCategory((String) tableModel.getValueAt(modelRow, 1));
        e.setAmount((Double) tableModel.getValueAt(modelRow, 2));
        e.setDate((Date) tableModel.getValueAt(modelRow, 3));
        e.setDescription((String) tableModel.getValueAt(modelRow, 4));
        AddExpenseDialog dlg = new AddExpenseDialog(this, userId, e);
        dlg.setVisible(true);
        if (dlg.isSaved()) { loadCategories(); refresh(); }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row to delete"); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (new ExpenseDAO().deleteExpense(id, userId)) refresh();
            else JOptionPane.showMessageDialog(this, "Delete failed");
        }
    }

    private void onExportCSV() {
        String category = (String) categoryFilter.getSelectedItem();
        Date from = parseDate(fromField.getText().trim());
        Date to = parseDate(toField.getText().trim());
        List<Expense> data = expenseDAO.listExpenses(userId, category, from, to);
        try {
            File f = CSVExporter.exportExpenses(data, new File("output/exports"));
            JOptionPane.showMessageDialog(this, "CSV saved: " + f.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "CSV export failed: " + ex.getMessage());
        }
    }

    private void onExportPDF() {
        String category = (String) categoryFilter.getSelectedItem();
        Date from = parseDate(fromField.getText().trim());
        Date to = parseDate(toField.getText().trim());
        List<Expense> data = expenseDAO.listExpenses(userId, category, from, to);
        try {
            File f = PDFExporter.exportSummary(data, new File("output/reports"));
            JOptionPane.showMessageDialog(this, "PDF saved: " + f.getAbsolutePath());
        } catch (IOException | DocumentException ex) {
            JOptionPane.showMessageDialog(this, "PDF export failed: " + ex.getMessage());
        }
    }
}
