package com.novasoftware.tools.infrastructure.database;


public class DatabaseInitializer {
    public static void initialize() {
        DatabaseManager.createTables();
    }
}
