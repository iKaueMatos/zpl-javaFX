package com.novasoftware.shared.dev;

import com.novasoftware.shared.database.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerDev {
    private static final String SQLITE_DB_URL = "jdbc:sqlite:dev-database.sqlite";

    private static final String CREATE_LABELS_TABLE = """
            CREATE TABLE IF NOT EXISTS labels (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ean TEXT NOT NULL,
                sku TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                user_id INTEGER,
                CONSTRAINT uq_ean_sku UNIQUE (ean, sku),
                CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
            );
        """;

    private static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active INTEGER DEFAULT 1,
                token TEXT
            );
        """;

    public static Connection connect() throws SQLException {
        if (!DatabaseManager.isDevelopment()) {
            throw new IllegalStateException("Modo de desenvolvimento desabilitado. Não é possível usar SQLite.");
        }
        return DriverManager.getConnection(SQLITE_DB_URL);
    }

    public static void initializeDatabase() {
        if (DatabaseManager.isDevelopment()) {
            try (Connection connection = connect()) {
                initializeTables(connection);
                System.out.println("Tabelas criadas ou já existem no banco SQLite.");
            } catch (SQLException e) {
                System.err.println("Erro ao inicializar o banco de dados SQLite.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Modo de desenvolvimento desabilitado. Nenhuma inicialização do banco SQLite foi feita.");
        }
    }

    private static void initializeTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_LABELS_TABLE);
        } catch (SQLException e) {
            System.err.println("Erro ao criar as tabelas no banco SQLite.");
            e.printStackTrace();
        }
    }
}
