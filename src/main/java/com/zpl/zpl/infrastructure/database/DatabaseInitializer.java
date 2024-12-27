package com.zpl.zpl.infrastructure.database;


public class DatabaseInitializer {
    public static void initialize() {
        DatabaseManager.createTables();
    }
}
