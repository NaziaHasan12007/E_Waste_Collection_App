package com.ewaste.server.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configures SQLite DataSource with foreign keys enabled, WAL journaling, and busy timeout handling.
 */
@Configuration
public class DatabaseConfig {

    @Value("${ewaste.db.path}")
    private String databasePath;

    @Bean
    public DataSource dataSource() {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setBusyTimeout(5000);

        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        Path path = Path.of(databasePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create database directory: " + path.getParent(), e);
        }
        dataSource.setUrl("jdbc:sqlite:" + path);
        return dataSource;
    }
}