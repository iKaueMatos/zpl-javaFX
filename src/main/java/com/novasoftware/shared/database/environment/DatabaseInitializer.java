package com.novasoftware.shared.database.environment;


import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize() throws SQLException {
        DatabaseManager.connect();
    }
}
