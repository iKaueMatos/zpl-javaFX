package com.zpl.zpl.infrastructure.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<Map<String, Object>> fetchFilteredData(String eanFilter, String skuFilter, String quantityFilter) {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT * FROM labels WHERE 1=1";
        if (eanFilter != null && !eanFilter.isEmpty()) {
            sql += " AND ean LIKE ?";
        }
        if (skuFilter != null && !skuFilter.isEmpty()) {
            sql += " AND sku LIKE ?";
        }
        if (quantityFilter != null && !quantityFilter.isEmpty()) {
            sql += " AND quantity = ?";
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (eanFilter != null && !eanFilter.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + eanFilter + "%");
            }
            if (skuFilter != null && !skuFilter.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + skuFilter + "%");
            }
            if (quantityFilter != null && !quantityFilter.isEmpty()) {
                pstmt.setInt(paramIndex++, Integer.parseInt(quantityFilter));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("EAN", rs.getString("ean"));
                row.put("SKU", rs.getString("sku"));
                row.put("Quantidade", rs.getInt("quantity"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
