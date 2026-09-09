package com.ewaste.server.infrastructure.persistence.sqlite;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseConnectionManager {

    private static final String DB_URL = "jdbc:sqlite:ewaste.db";
    private Connection connection;

    public DatabaseConnectionManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            enableForeignKeys();
            createTables();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private void enableForeignKeys() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
    }

    private void createTables() throws SQLException {
        // Read schema.sql and execute it
        // This should be done through a schema loader
        // For simplicity, we'll assume schema is already created
        // In production, use a proper migration tool
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            enableForeignKeys();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log error but don't throw
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
