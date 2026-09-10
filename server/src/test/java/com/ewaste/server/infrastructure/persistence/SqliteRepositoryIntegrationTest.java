package com.ewaste.server.infrastructure.persistence;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.model.processing.ProcessingResult;
import com.ewaste.server.domain.model.processing.RecyclingCenter;
import com.ewaste.server.domain.pattern.state.SubmittedState;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteCollectorRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqlitePickupRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteProcessingRecordRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteRecyclingCenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test verifying real CRUD queries and relational mappings against an in-memory SQLite database.
 */
class SqliteRepositoryIntegrationTest {

    private DataSource dataSource;
    private SqliteCollectorRepository collectorRepository;
    private SqliteRecyclingCenterRepository centerRepository;
    private SqliteProcessingRecordRepository recordRepository;
    private SqlitePickupRepository pickupRepository;

    @BeforeEach
    void setUp() throws Exception {
        SQLiteDataSource ds = new SQLiteDataSource();
        // File-backed temp DB so all repository connections share the same schema/data.
        Path dbFile = Files.createTempFile("ewaste-repo-it-", ".db");
        ds.setUrl("jdbc:sqlite:" + dbFile.toAbsolutePath());
        this.dataSource = ds;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS collectors (
                    collector_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    vehicle_type TEXT NOT NULL,
                    max_capacity_kg REAL NOT NULL,
                    current_workload_kg REAL DEFAULT 0.0,
                    is_available INTEGER DEFAULT 1,
                    created_at TEXT,
                    updated_at TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS recycling_centers (
                    center_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    center_name TEXT NOT NULL,
                    address TEXT NOT NULL,
                    processing_capacity_kg REAL NOT NULL,
                    current_load_kg REAL DEFAULT 0.0,
                    is_active INTEGER DEFAULT 1,
                    created_at TEXT,
                    updated_at TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS processing_records (
                    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pickup_id INTEGER,
                    center_id INTEGER NOT NULL,
                    item_id INTEGER,
                    workflow_type TEXT NOT NULL,
                    points_awarded INTEGER DEFAULT 0,
                    processing_status TEXT,
                    notes TEXT,
                    actual_weight_kg REAL,
                    carbon_credits_earned REAL,
                    processed_at TEXT NOT NULL,
                    created_at TEXT,
                    updated_at TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pickup_requests (
                    pickup_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    collector_id INTEGER,
                    current_state TEXT NOT NULL,
                    priority_score REAL DEFAULT 0.0,
                    address TEXT,
                    scheduled_date TEXT,
                    preferred_time TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pickup_items (
                    pickup_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pickup_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL
                );
            """);
        }

        collectorRepository = new SqliteCollectorRepository(dataSource);
        centerRepository = new SqliteRecyclingCenterRepository(dataSource);
        recordRepository = new SqliteProcessingRecordRepository(dataSource);
        pickupRepository = new SqlitePickupRepository(dataSource);
    }

    @Test
    @DisplayName("Verify full CRUD operations on Collector entity")
    void testCollectorCrudOperations() {
        Collector collector = new Collector();
        collector.setUserId(10L);
        collector.setVehicleType(VehicleType.VAN);
        collector.setMaxCapacityKg(500.0);
        collector.setCurrentWorkloadKg(0.0);
        collector.setAvailable(true);

        Collector saved = collectorRepository.save(collector);
        assertNotNull(saved.getCollectorId());

        Optional<Collector> retrieved = collectorRepository.findById(saved.getCollectorId());
        assertTrue(retrieved.isPresent());
        assertEquals(VehicleType.VAN, retrieved.get().getVehicleType());

        retrieved.get().setCurrentWorkloadKg(150.0);
        collectorRepository.update(retrieved.get());

        Collector updated = collectorRepository.findById(saved.getCollectorId()).orElseThrow();
        assertEquals(150.0, updated.getCurrentWorkloadKg());
    }

    @Test
    @DisplayName("Verify persistence and query on RecyclingCenter and ProcessingRecord")
    void testCenterAndProcessingRecordPersistence() {
        RecyclingCenter center = new RecyclingCenter();
        center.setCenterName("North Green Depot");
        center.setAddress("12 Industrial Way");
        center.setCapacityKg(3000.0);

        RecyclingCenter savedCenter = centerRepository.save(center);
        assertNotNull(savedCenter.getCenterId());

        ProcessingRecord record = new ProcessingRecord();
        record.setPickupId(1L);
        record.setCenterId(savedCenter.getCenterId());
        record.setProcessingResult(ProcessingResult.RECYCLE);
        record.setPointsAwarded(40);
        record.setProcessedAt(LocalDateTime.now());

        ProcessingRecord savedRecord = recordRepository.save(record);
        assertNotNull(savedRecord.getRecordId());

        List<ProcessingRecord> records = recordRepository.findByCenterId(savedCenter.getCenterId());
        assertEquals(1, records.size());
        assertEquals(ProcessingResult.RECYCLE, records.get(0).getProcessingResult());
    }

    @Test
    @DisplayName("Verify PickupRequest creation and state-based querying")
    void testPickupLifecyclePersistence() {
        PickupRequest pickup = new PickupRequest();
        pickup.setUserId(22L);
        pickup.setState(new SubmittedState());
        pickup.setPriorityScore(88.0);
        pickup.setPreferredDate("2026-10-15");
        pickup.setCreatedAt(LocalDateTime.now());

        PickupRequest saved = pickupRepository.save(pickup);
        assertNotNull(saved.getPickupId());

        List<PickupRequest> submittedPickups = pickupRepository.findByStatus(PickupStatus.SUBMITTED);
        assertFalse(submittedPickups.isEmpty());
        assertEquals(saved.getPickupId(), submittedPickups.get(0).getPickupId());
    }
}