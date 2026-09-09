package com.ewaste.server.infrastructure.config;

import com.ewaste.server.domain.pattern.strategy.assignment.AssignmentStrategy;
import com.ewaste.server.domain.pattern.strategy.assignment.LeastBusyCollectorStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.CompositePriorityStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.PriorityStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PriorityStrategy priorityStrategy() {
        return new CompositePriorityStrategy();
    }

    @Bean
    public AssignmentStrategy assignmentStrategy() {
        return new LeastBusyCollectorStrategy();
    }
}