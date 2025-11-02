package com.example.expense.ui;

import com.example.expense.dao.ExpenseDAO;
import com.example.expense.model.Expense;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class AddExpenseDialog extends JDialog {
    private final int userId;
    private final ExpenseDAO dao = new ExpenseDAO();
    private final JTextField categoryField = new JTextField(15);
    private final JTextField amountField = new JTextField(10);
    private final JTextField dateField = new JTextField(10); // yyyy-MM-dd
    private final JTextArea descArea = new JTextArea(3, 20);

    private boolean saved = false;
    private Expense editing;

    public AddExpenseDialog(JFrame owner, int userId, Expense editing) {
        super(owner, editing == null ? "Add Expense" : "Edit Expense", true);
        this.userId = userId;
        this.editing = editing;
        setSize(420, 280);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Category"), gbc);
        gbc.gridx=1; panel.add(categoryField, gbc);

        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Amount"), gbc);
        gbc.gridx=1; panel.add(amountField, gbc);

        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Date (yyyy-MM-dd)"), gbc);
        gbc.gridx=1; panel.add(dateField, gbc);

        gbc.gridx=0; gbc.gridy=3; panel.add(new JLabel("Description"), gbc);
        gbc.gridx=1; gbc.fill = GridBagConstraints.BOTH; panel.add(new JScrollPane(descArea), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());
        JPanel btns = new JPanel(); btns.add(save); btns.add(cancel);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; panel.add(btns, gbc);

        setContentPane(panel);

        if (editing != null) {
            categoryField.setText(editing.getCategory());
            amountField.setText(String.valueOf(editing.getAmount()));
            dateField.setText(editing.getDate().toString());
            descArea.setText(editing.getDescription());
        } else {
            dateField.setText(new java.sql.Date(System.currentTimeMillis()).toString());
        }
    }

    private void onSave() {
        String cat = categoryField.getText().trim();
        String amtStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        String desc = descArea.getText();
        if (cat.isEmpty() || amtStr.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields");
            return;
        }
        double amt;
        try { amt = Double.parseDouble(amtStr); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid amount"); return; }
        Date d;
        try { d = Date.valueOf(dateStr); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid date format (yyyy-MM-dd)"); return; }

        boolean ok;
        if (editing == null) {
            Expense e = new Expense(0, userId, cat, amt, d, desc);
            ok = dao.addExpense(e);
        } else {
            editing.setCategory(cat);
            editing.setAmount(amt);
            editing.setDate(d);
            editing.setDescription(desc);
            ok = dao.updateExpense(editing);
        }
        if (ok) { saved = true; dispose(); }
        else JOptionPane.showMessageDialog(this, "Save failed");
    }

    public boolean isSaved() { return saved; }
}
