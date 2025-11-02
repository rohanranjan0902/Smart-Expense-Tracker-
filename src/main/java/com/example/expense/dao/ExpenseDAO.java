package com.example.expense.dao;

import com.example.expense.model.Expense;
import com.example.expense.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    public boolean addExpense(Expense e) {
        String sql = "INSERT INTO expenses (user_id, category, amount, date, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getCategory());
            ps.setDouble(3, e.getAmount());
            ps.setDate(4, e.getDate());
            ps.setString(5, e.getDescription());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateExpense(Expense e) {
        String sql = "UPDATE expenses SET category=?, amount=?, date=?, description=? WHERE id=? AND user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getCategory());
            ps.setDouble(2, e.getAmount());
            ps.setDate(3, e.getDate());
            ps.setString(4, e.getDescription());
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getUserId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteExpense(int id, int userId) {
        String sql = "DELETE FROM expenses WHERE id=? AND user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Expense> listExpenses(int userId, String category, Date from, Date to) {
        List<Expense> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT id, user_id, category, amount, date, description FROM expenses WHERE user_id = ?");
        if (category != null && !category.equals("All")) sb.append(" AND category = ?");
        if (from != null) sb.append(" AND date >= ?");
        if (to != null) sb.append(" AND date <= ?");
        sb.append(" ORDER BY date DESC, id DESC");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            int idx = 1;
            ps.setInt(idx++, userId);
            if (category != null && !category.equals("All")) ps.setString(idx++, category);
            if (from != null) ps.setDate(idx++, from);
            if (to != null) ps.setDate(idx++, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense e = new Expense();
                    e.setId(rs.getInt("id"));
                    e.setUserId(rs.getInt("user_id"));
                    e.setCategory(rs.getString("category"));
                    e.setAmount(rs.getDouble("amount"));
                    e.setDate(rs.getDate("date"));
                    e.setDescription(rs.getString("description"));
                    list.add(e);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<String> listCategories(int userId) {
        List<String> cats = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM expenses WHERE user_id = ? ORDER BY category";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cats.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (cats.isEmpty()) {
            cats.add("Food"); cats.add("Travel"); cats.add("Rent"); cats.add("Shopping"); cats.add("Misc");
        }
        return cats;
    }
}
