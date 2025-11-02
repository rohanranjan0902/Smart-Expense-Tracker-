package com.example.expense.dao;

import com.example.expense.model.User;
import com.example.expense.util.DBConnection;
import com.example.expense.util.HashUtil;

import java.sql.*;

public class UserDAO {
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String plainPassword) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        String hash = HashUtil.sha256(plainPassword);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer login(String username, String plainPassword) {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    String hash = HashUtil.sha256(plainPassword);
                    if (stored != null && stored.equals(hash)) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
