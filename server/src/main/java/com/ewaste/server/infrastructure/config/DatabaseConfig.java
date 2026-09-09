package com.ewaste.server.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * Configures SQLite DataSource with foreign keys enabled, WAL journaling, and busy timeout handling.
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:jdbc:sqlite:ewaste.db}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setBusyTimeout(5000);

        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(databaseUrl);
        return dataSource;
    }
}