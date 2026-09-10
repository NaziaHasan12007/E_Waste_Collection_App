package com.ewaste.server.infrastructure.persistence.sqlite;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DatabaseConnectionManager {

    private final String dbUrl;
    private Connection connection;

    public DatabaseConnectionManager(@Value("${ewaste.db.path}") String databasePath) {
        Path path = Path.of(databasePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database directory: " + path.getParent(), e);
        }
        this.dbUrl = "jdbc:sqlite:" + path;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(dbUrl);
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
            connection = DriverManager.getConnection(dbUrl);
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
