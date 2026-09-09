package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.reward.Reward;
import com.ewaste.server.domain.repository.RewardRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteRewardRepository implements RewardRepository {

    private final DatabaseConnectionManager connectionManager;

    public SqliteRewardRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Reward save(Reward reward) {
        String sql;
        boolean isUpdate = reward.getRewardId() != null;

        if (isUpdate) {
            sql = "UPDATE rewards SET customer_id = ?, points_earned = ?, balance = ? WHERE reward_id = ?";
        } else {
            sql = "INSERT INTO rewards (customer_id, points_earned, balance) VALUES (?, ?, ?)";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, reward.getCustomerId());
            stmt.setInt(2, reward.getPointsEarned());
            stmt.setInt(3, reward.getBalance());

            if (isUpdate) {
                stmt.setLong(4, reward.getRewardId());
                stmt.executeUpdate();
            } else {
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reward.setRewardId(rs.getLong(1));
                    }
                }
            }

            return reward;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save reward: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Reward> findById(Long rewardId) {
        String sql = "SELECT * FROM rewards WHERE reward_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rewardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReward(rs));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reward by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reward> findAll() {
        String sql = "SELECT * FROM rewards ORDER BY reward_id DESC";
        List<Reward> rewards = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rewards.add(mapResultSetToReward(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all rewards: " + e.getMessage(), e);
        }
        return rewards;
    }

    @Override
    public List<Reward> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM rewards WHERE customer_id = ? ORDER BY reward_id DESC";
        List<Reward> rewards = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rewards.add(mapResultSetToReward(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rewards by customer ID: " + e.getMessage(), e);
        }
        return rewards;
    }

    @Override
    public int getCurrentBalanceByCustomerId(Long customerId) {
        String sql = "SELECT balance FROM rewards WHERE customer_id = ? ORDER BY reward_id DESC LIMIT 1";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get current balance: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Reward> findByPointsEarnedGreaterThanEqual(int minPoints) {
        String sql = "SELECT * FROM rewards WHERE points_earned >= ? ORDER BY points_earned DESC";
        List<Reward> rewards = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minPoints);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rewards.add(mapResultSetToReward(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rewards by minimum points: " + e.getMessage(), e);
        }
        return rewards;
    }

    @Override
    public void deleteById(Long rewardId) {
        String sql = "DELETE FROM rewards WHERE reward_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rewardId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reward: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByCustomerId(Long customerId) {
        String sql = "DELETE FROM rewards WHERE customer_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete rewards for customer: " + e.getMessage(), e);
        }
    }

    private Reward mapResultSetToReward(ResultSet rs) throws SQLException {
        Reward reward = new Reward();
        reward.setRewardId(rs.getLong("reward_id"));
        reward.setCustomerId(rs.getLong("customer_id"));
        reward.setPointsEarned(rs.getInt("points_earned"));
        reward.setBalance(rs.getInt("balance"));
        return reward;
    }
}