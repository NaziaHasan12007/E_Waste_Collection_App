package com.ewaste.server.application.facade;

import com.ewaste.server.api.dto.request.InspectionOutcomeRequest;
import com.ewaste.server.api.mapper.ProcessingRecordMapper;
import com.ewaste.server.application.service.ProcessingService;
import com.ewaste.server.application.service.RewardService;
import com.ewaste.server.common.exception.BusinessRuleException;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.model.processing.ProcessingResult;
import com.ewaste.server.domain.model.processing.RecyclingCenter;
import com.ewaste.server.domain.repository.EWasteItemRepository;
import com.ewaste.server.domain.repository.PickupRepository;
import com.ewaste.server.domain.repository.RecyclingCenterRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.DatabaseConnectionManager;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteProcessingRecordRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteRewardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessingRewardIntegrationTest {

    @TempDir
    Path tempDirectory;

    private DataSource dataSource;
    private DatabaseConnectionManager connectionManager;
    private PickupRepository pickupRepository;
    private RecyclingCenterRepository centerRepository;
    private EWasteItemRepository itemRepository;
    private PickupRequest pickup;

    @BeforeEach
    void setUp() throws Exception {
        Path databasePath = tempDirectory.resolve("processing-reward.db");
        connectionManager = new DatabaseConnectionManager(databasePath.toString());

        SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
        sqliteDataSource.setUrl("jdbc:sqlite:" + databasePath);
        dataSource = sqliteDataSource;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("""
                    CREATE TABLE users (
                        user_id INTEGER PRIMARY KEY,
                        full_name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        role TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE pickup_requests (
                        pickup_id INTEGER PRIMARY KEY,
                        customer_id INTEGER NOT NULL,
                        current_state TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE recycling_centers (
                        center_id INTEGER PRIMARY KEY,
                        center_name TEXT NOT NULL,
                        address TEXT NOT NULL,
                        processing_capacity_kg REAL NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE processing_records (
                        record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        pickup_id INTEGER NOT NULL,
                        center_id INTEGER NOT NULL,
                        workflow_type TEXT NOT NULL,
                        points_awarded INTEGER NOT NULL,
                        processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.execute("""
                    CREATE TABLE rewards (
                        reward_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_id INTEGER NOT NULL,
                        points_earned INTEGER NOT NULL,
                        balance INTEGER NOT NULL
                    )
                    """);
            statement.execute("""
                    INSERT INTO users (user_id, full_name, email, password_hash, role)
                    VALUES (7, 'Integration Customer', 'integration@example.com', 'hash', 'CUSTOMER')
                    """);
            statement.execute("""
                    INSERT INTO pickup_requests (pickup_id, customer_id, current_state)
                    VALUES (42, 7, 'DELIVERED')
                    """);
            statement.execute("""
                    INSERT INTO recycling_centers (center_id, center_name, address, processing_capacity_kg)
                    VALUES (1, 'Integration Center', 'Test Road', 1000)
                    """);
        }

        pickupRepository = mock(PickupRepository.class);
        centerRepository = mock(RecyclingCenterRepository.class);
        itemRepository = mock(EWasteItemRepository.class);

        pickup = new PickupRequest();
        pickup.setPickupId(42L);
        pickup.setUserId(7L);
        pickup.setState(PickupStatus.DELIVERED);

        RecyclingCenter center = new RecyclingCenter();
        center.setCenterId(1L);
        center.setCenterName("Integration Center");
        center.setAddress("Test Road");
        center.setCapacityKg(1000);

        EWasteCategory category = new EWasteCategory("Laptop", 50, false);
        category.setId(1L);
        LaptopWaste item = new LaptopWaste(
                category, "Integration Laptop", WasteCondition.WORKING,
                2.0, true, true, 15.6);
        item.setId(11L);

        when(pickupRepository.findByItemId(11L)).thenReturn(Optional.of(pickup));
        when(centerRepository.findById(1L)).thenReturn(Optional.of(center));
        when(itemRepository.findById(11L)).thenReturn(Optional.of(item));
    }

    @Test
    void processingPersistsRecordAndAwardsCustomerPoints() throws Exception {
        SqliteProcessingRecordRepository recordRepository =
                new SqliteProcessingRecordRepository(dataSource);
        SqliteRewardRepository rewardRepository =
                new SqliteRewardRepository(connectionManager);

        ProcessingFacade facade = new ProcessingFacade(
                new ProcessingService(recordRepository),
                new RewardService(rewardRepository),
                centerRepository,
                itemRepository,
                pickupRepository,
                new ProcessingRecordMapper()
        );

        InspectionOutcomeRequest request =
                new InspectionOutcomeRequest(11L, 1L, "Passed inspection", "RECYCLE");

        var response = facade.processItem(request);

        assertEquals(42L, response.getPickupId());
        assertEquals(11L, response.getItemId());
        assertEquals(20, response.getPointsAwarded());
        assertEquals(20, rewardRepository.getCurrentBalanceByCustomerId(7L));
        assertEquals(1, recordRepository.findAll().size());
        assertEquals(ProcessingResult.RECYCLE,
                recordRepository.findAll().get(0).getProcessingResult());
        assertEquals(PickupStatus.COMPLETED, pickup.getStatus());
        verify(pickupRepository).update(pickup);
    }

    @Test
    void duplicateProcessingIsRejectedBeforeAnotherRewardIsAwarded() throws Exception {
        SqliteProcessingRecordRepository recordRepository =
                new SqliteProcessingRecordRepository(dataSource);
        SqliteRewardRepository rewardRepository =
                new SqliteRewardRepository(connectionManager);

        ProcessingFacade facade = new ProcessingFacade(
                new ProcessingService(recordRepository),
                new RewardService(rewardRepository),
                centerRepository,
                itemRepository,
                pickupRepository,
                new ProcessingRecordMapper()
        );

        InspectionOutcomeRequest request =
                new InspectionOutcomeRequest(11L, 1L, "Passed inspection", "RECYCLE");

        facade.processItem(request);

        BusinessRuleException error = assertThrows(
                BusinessRuleException.class,
                () -> facade.processItem(request));

        assertTrue(error.getMessage().contains("already processed"));
        assertEquals(1, recordRepository.findAll().size());
        assertEquals(20, rewardRepository.getCurrentBalanceByCustomerId(7L));
    }
}
