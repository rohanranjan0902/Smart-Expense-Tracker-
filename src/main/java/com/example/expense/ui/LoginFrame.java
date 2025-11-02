package com.example.expense.ui;

import com.example.expense.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        super("Smart Expense Tracker - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
            setVisible(false);
        });

        JPanel btns = new JPanel();
        btns.add(loginBtn);
        btns.add(registerBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(btns, gbc);

        setContentPane(panel);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        Integer userId = userDAO.login(username, password);
        if (userId != null) {
            new DashboardFrame(userId, username).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }
}
