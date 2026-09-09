package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.pattern.state.*;
import com.ewaste.server.domain.repository.PickupRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlitePickupRepository implements PickupRepository {

    private final DataSource dataSource;

    public SqlitePickupRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PickupRequest save(PickupRequest pickup) {
        String sqlPickup = "INSERT INTO pickup_requests (customer_id, collector_id, current_state, priority_score, scheduled_date, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO pickup_items (pickup_id, item_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlPickup, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, pickup.getUserId());
                if (pickup.getCollectorId() != null) {
                    ps.setLong(2, pickup.getCollectorId());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setString(3, pickup.getStatus() != null ? pickup.getStatus().name() : PickupStatus.SUBMITTED.name());
                ps.setDouble(4, pickup.getPriorityScore() != null ? pickup.getPriorityScore() : 0.0);
                ps.setString(5, pickup.getPreferredDate() != null ? pickup.getPreferredDate() : "");
                ps.setString(6, pickup.getCreatedAt() != null ? pickup.getCreatedAt().toString() : LocalDateTime.now().toString());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        pickup.setPickupId(keys.getLong(1));
                    }
                }

                if (pickup.getItems() != null && !pickup.getItems().isEmpty()) {
                    try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                        for (PickupItem item : pickup.getItems()) {
                            psItem.setLong(1, pickup.getPickupId());
                            psItem.setLong(2, item.getItemId());
                            psItem.addBatch();
                        }
                        psItem.executeBatch();
                    }
                }

                conn.commit();
                return pickup;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to persist pickup request", e);
        }
    }

    @Override
    public PickupRequest update(PickupRequest pickup) {
        String sql = "UPDATE pickup_requests SET collector_id = ?, current_state = ?, priority_score = ?, scheduled_date = ? " +
                "WHERE pickup_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (pickup.getCollectorId() != null) {
                ps.setLong(1, pickup.getCollectorId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, pickup.getStatus() != null ? pickup.getStatus().name() : PickupStatus.SUBMITTED.name());
            ps.setDouble(3, pickup.getPriorityScore() != null ? pickup.getPriorityScore() : 0.0);
            ps.setString(4, pickup.getPreferredDate() != null ? pickup.getPreferredDate() : "");
            ps.setLong(5, pickup.getPickupId());
            ps.executeUpdate();
            return pickup;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update pickup request", e);
        }
    }

    @Override
    public Optional<PickupRequest> findById(Long pickupId) {
        String sql = "SELECT pickup_id, customer_id, collector_id, current_state, priority_score, scheduled_date, created_at " +
                "FROM pickup_requests WHERE pickup_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, pickupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PickupRequest pickup = mapRow(rs);
                    pickup.setItems(loadItemsForPickup(conn, pickupId));
                    return Optional.of(pickup);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find pickup by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<PickupRequest> findAll() {
        return queryPickups("SELECT pickup_id, customer_id, collector_id, current_state, priority_score, scheduled_date, created_at FROM pickup_requests", null);
    }

    @Override
    public List<PickupRequest> findByUserId(Long userId) {
        return queryPickups("SELECT pickup_id, customer_id, collector_id, current_state, priority_score, scheduled_date, created_at FROM pickup_requests WHERE customer_id = ?", userId);
    }

    @Override
    public List<PickupRequest> findByCollectorId(Long collectorId) {
        return queryPickups("SELECT pickup_id, customer_id, collector_id, current_state, priority_score, scheduled_date, created_at FROM pickup_requests WHERE collector_id = ?", collectorId);
    }

    @Override
    public List<PickupRequest> findByStatus(PickupStatus status) {
        String sql = "SELECT pickup_id, customer_id, collector_id, current_state, priority_score, scheduled_date, created_at FROM pickup_requests WHERE current_state = ?";
        List<PickupRequest> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PickupRequest p = mapRow(rs);
                    p.setItems(loadItemsForPickup(conn, p.getPickupId()));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find pickups by status", e);
        }
        return list;
    }

    private List<PickupRequest> queryPickups(String sql, Long param) {
        List<PickupRequest> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setLong(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PickupRequest p = mapRow(rs);
                    p.setItems(loadItemsForPickup(conn, p.getPickupId()));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query pickups", e);
        }
        return list;
    }

    private List<PickupItem> loadItemsForPickup(Connection conn, Long pickupId) throws SQLException {
        String sql = "SELECT pickup_item_id, item_id FROM pickup_items WHERE pickup_id = ?";
        List<PickupItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, pickupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new PickupItem(rs.getLong("pickup_item_id"), rs.getLong("item_id")));
                }
            }
        }
        return items;
    }

    private PickupRequest mapRow(ResultSet rs) throws SQLException {
        PickupRequest p = new PickupRequest();
        p.setPickupId(rs.getLong("pickup_id"));
        p.setUserId(rs.getLong("customer_id"));
        long collectorId = rs.getLong("collector_id");
        if (!rs.wasNull()) {
            p.setCollectorId(collectorId);
        }
        String stateStr = rs.getString("current_state");
        p.setState(resolveState(stateStr));
        p.setPriorityScore(rs.getDouble("priority_score"));
        p.setPreferredDate(rs.getString("scheduled_date"));
        String createdAt = rs.getString("created_at");
        if (createdAt != null) {
            try {
                p.setCreatedAt(LocalDateTime.parse(createdAt));
            } catch (Exception ignored) {
                p.setCreatedAt(LocalDateTime.now());
            }
        }
        return p;
    }

    private PickupState resolveState(String stateName) {
        if (stateName == null) return new SubmittedState();
        return switch (stateName.toUpperCase()) {
            case "REQUESTED" -> new RequestedState();
            case "ASSIGNED" -> new AssignedState();
            case "COLLECTED" -> new CollectedState();
            case "DELIVERED" -> new DeliveredState();
            case "PROCESSING" -> new ProcessingState();
            case "COMPLETED" -> new CompletedState();
            case "CANCELLED" -> new CancelledState();
            default -> new SubmittedState();
        };
    }
}