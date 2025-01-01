package com.novasoftware.shared.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:zpl.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS labels (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ean TEXT NOT NULL," +
                "sku TEXT NOT NULL," +
                "quantity INTEGER NOT NULL" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
