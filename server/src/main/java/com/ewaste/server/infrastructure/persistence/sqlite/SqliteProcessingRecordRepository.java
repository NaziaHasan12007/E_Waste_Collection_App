package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.model.processing.ProcessingResult;
import com.ewaste.server.domain.repository.ProcessingRecordRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteProcessingRecordRepository implements ProcessingRecordRepository {

    private final DataSource dataSource;

    public SqliteProcessingRecordRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ProcessingRecord save(ProcessingRecord record) {
        String sql = "INSERT INTO processing_records (pickup_id, center_id, workflow_type, points_awarded, processed_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, record.getPickupId() != null ? record.getPickupId() : 0L);
            ps.setLong(2, record.getCenterId());
            ps.setString(3, record.getProcessingResult() != null ? record.getProcessingResult().name() : "RECYCLE");
            ps.setInt(4, record.getPointsAwarded() != null ? record.getPointsAwarded() : 0);
            ps.setString(5, record.getProcessedAt() != null ? record.getProcessedAt().toString() : LocalDateTime.now().toString());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setRecordId(keys.getLong(1));
                }
            }
            return record;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert processing record", e);
        }
    }

    @Override
    public Optional<ProcessingRecord> findById(Long recordId) {
        String sql = "SELECT record_id, pickup_id, center_id, workflow_type, points_awarded, processed_at " +
                "FROM processing_records WHERE record_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find processing record by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ProcessingRecord> findByCenterId(Long centerId) {
        String sql = "SELECT record_id, pickup_id, center_id, workflow_type, points_awarded, processed_at " +
                "FROM processing_records WHERE center_id = ?";
        List<ProcessingRecord> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, centerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find processing records by center ID", e);
        }
        return list;
    }

    @Override
    public List<ProcessingRecord> findAll() {
        String sql = "SELECT record_id, pickup_id, center_id, workflow_type, points_awarded, processed_at FROM processing_records";
        List<ProcessingRecord> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all processing records", e);
        }
        return list;
    }

    private ProcessingRecord mapRow(ResultSet rs) throws SQLException {
        ProcessingRecord record = new ProcessingRecord();
        record.setRecordId(rs.getLong("record_id"));
        record.setPickupId(rs.getLong("pickup_id"));
        record.setCenterId(rs.getLong("center_id"));
        String workflow = rs.getString("workflow_type");
        if (workflow != null) {
            record.setProcessingResult(ProcessingResult.valueOf(workflow));
        }
        record.setPointsAwarded(rs.getInt("points_awarded"));
        String processedAt = rs.getString("processed_at");
        if (processedAt != null) {
            try {
                record.setProcessedAt(LocalDateTime.parse(processedAt));
            } catch (Exception ignored) {
                record.setProcessedAt(LocalDateTime.now());
            }
        }
        return record;
    }
}