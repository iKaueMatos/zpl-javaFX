package com.novasoftware.shared.database;


import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize() throws SQLException {
        DatabaseManager.connect();
    }
}
