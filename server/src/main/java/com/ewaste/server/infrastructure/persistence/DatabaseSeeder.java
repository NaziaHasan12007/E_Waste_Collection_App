package com.ewaste.server.infrastructure.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Initializes and executes schema.sql and seed_data.sql scripts on application startup.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final DataSource dataSource;

    public DatabaseSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        log.info("Executing database seeding routine...");
        executeSqlScript("db/schema.sql");
        ensurePickupColumns();
        executeSqlScript("db/seed_data.sql");
        log.info("Database seeding successfully finished.");
    }

    private void ensurePickupColumns() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             var columns = stmt.executeQuery("PRAGMA table_info(pickup_requests)")) {
            boolean hasAddress = false;
            boolean hasPreferredTime = false;
            while (columns.next()) {
                hasAddress |= "address".equalsIgnoreCase(columns.getString("name"));
                hasPreferredTime |= "preferred_time".equalsIgnoreCase(columns.getString("name"));
            }
            if (!hasAddress) {
                stmt.executeUpdate("ALTER TABLE pickup_requests ADD COLUMN address TEXT");
            }
            if (!hasPreferredTime) {
                stmt.executeUpdate("ALTER TABLE pickup_requests ADD COLUMN preferred_time TEXT");
            }
        } catch (Exception e) {
            log.error("Failed to migrate pickup request columns: {}", e.getMessage(), e);
        }
    }

    private void executeSqlScript(String scriptPath) {
        ClassPathResource resource = new ClassPathResource(scriptPath);
        if (!resource.exists()) {
            log.warn("SQL resource {} not found; skipping execution.", scriptPath);
            return;
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String fullSql = reader.lines().collect(Collectors.joining("\n"));
            String[] statements = fullSql.split(";");

            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            log.info("Executed script: {}", scriptPath);
        } catch (Exception e) {
            log.error("Failed executing SQL script {}: {}", scriptPath, e.getMessage(), e);
        }
    }
}