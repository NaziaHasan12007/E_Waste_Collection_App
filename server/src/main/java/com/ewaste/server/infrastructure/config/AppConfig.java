package com.ewaste.server.infrastructure.config;

import com.ewaste.server.infrastructure.persistence.sqlite.DatabaseConnectionManager;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteEWasteCategoryRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteEWasteItemRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteNotificationRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteRewardRepository;
import com.ewaste.server.infrastructure.persistence.sqlite.SqliteUserRepository;
import com.ewaste.server.domain.repository.EWasteCategoryRepository;
import com.ewaste.server.domain.repository.EWasteItemRepository;
import com.ewaste.server.domain.repository.NotificationRepository;
import com.ewaste.server.domain.repository.RewardRepository;
import com.ewaste.server.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public DatabaseConnectionManager databaseConnectionManager() {
        return new DatabaseConnectionManager();
    }

    @Bean
    public UserRepository userRepository(DatabaseConnectionManager connectionManager) {
        return new SqliteUserRepository(connectionManager);
    }

    @Bean
    public EWasteCategoryRepository eWasteCategoryRepository(DatabaseConnectionManager connectionManager) {
        return new SqliteEWasteCategoryRepository(connectionManager);
    }

    @Bean
    public EWasteItemRepository eWasteItemRepository(DatabaseConnectionManager connectionManager) {
        return new SqliteEWasteItemRepository(connectionManager);
    }

    @Bean
    public NotificationRepository notificationRepository(DatabaseConnectionManager connectionManager) {
        return new SqliteNotificationRepository(connectionManager);
    }

    @Bean
    public RewardRepository rewardRepository(DatabaseConnectionManager connectionManager) {
        return new SqliteRewardRepository(connectionManager);
    }
}