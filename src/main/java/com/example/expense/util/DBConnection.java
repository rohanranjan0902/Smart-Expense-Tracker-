package com.example.expense.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class DBConnection {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = getenvOrProp("EXPENSE_DB_URL", "db.url");
        String user = getenvOrProp("EXPENSE_DB_USER", "db.user");
        String pass = getenvOrProp("EXPENSE_DB_PASS", "db.password");
        Objects.requireNonNull(url, "DB URL is required");
        return DriverManager.getConnection(url, user, pass);
    }

    private static String getenvOrProp(String env, String prop) {
        String v = System.getenv(env);
        if (v != null && !v.isEmpty()) return v;
        return props.getProperty(prop);
    }
}
