package com.novasoftware.shared.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Properties;

public class DatabaseManager {

    private static Properties properties = new Properties();
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Não foi possível encontrar o arquivo de propriedades.");
            }
            properties.load(input);

            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");

            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IllegalArgumentException("Propriedades de banco de dados ausentes.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() throws SQLException {
        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new SQLException("Propriedades de conexão não configuradas corretamente.");
        }
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}
