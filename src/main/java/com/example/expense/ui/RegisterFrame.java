package com.example.expense.ui;

import com.example.expense.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);

    private final UserDAO userDAO = new UserDAO();
    private final JFrame loginFrame;

    public RegisterFrame(JFrame loginFrame) {
        super("Register - Smart Expense Tracker");
        this.loginFrame = loginFrame;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 260);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Confirm Password"), gbc);
        gbc.gridx = 1; panel.add(confirmField, gbc);

        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");

        registerBtn.addActionListener(e -> doRegister());
        cancelBtn.addActionListener(e -> {
            if (loginFrame != null) loginFrame.setVisible(true);
            dispose();
        });

        JPanel btns = new JPanel();
        btns.add(registerBtn);
        btns.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(btns, gbc);
        setContentPane(panel);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        if (username.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists");
            return;
        }
        boolean ok = userDAO.register(username, pass);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Registered successfully. Please login.");
            if (loginFrame != null) loginFrame.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed");
        }
    }
}
