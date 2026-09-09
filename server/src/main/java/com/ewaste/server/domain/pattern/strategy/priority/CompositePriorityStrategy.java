package com.ewaste.server.domain.pattern.strategy.priority;

import com.ewaste.server.domain.model.pickup.PickupRequest;

/**
 * Combines hazardous risk, cumulative weight, and queue waiting time into
 * a balanced composite priority score.
 */
public class CompositePriorityStrategy implements PriorityStrategy {

    private final PriorityStrategy hazardousStrategy;
    private final PriorityStrategy weightStrategy;
    private final PriorityStrategy waitingTimeStrategy;

    public CompositePriorityStrategy() {
        this.hazardousStrategy = new HazardousPriorityStrategy();
        this.weightStrategy = new WeightPriorityStrategy();
        this.waitingTimeStrategy = new WaitingTimePriorityStrategy();
    }

    public CompositePriorityStrategy(PriorityStrategy hazardousStrategy,
                                     PriorityStrategy weightStrategy,
                                     PriorityStrategy waitingTimeStrategy) {
        this.hazardousStrategy = hazardousStrategy;
        this.weightStrategy = weightStrategy;
        this.waitingTimeStrategy = waitingTimeStrategy;
    }

    @Override
    public double calculatePriority(PickupRequest request) {
        if (request == null) {
            return 0.0;
        }

        double hazardScore = (hazardousStrategy != null) ? hazardousStrategy.calculatePriority(request) : 0.0;
        double weightScore = (weightStrategy != null) ? weightStrategy.calculatePriority(request) : 0.0;
        double waitScore = (waitingTimeStrategy != null) ? waitingTimeStrategy.calculatePriority(request) : 0.0;

        // Weighted aggregate calculation
        return (hazardScore * 0.45) + (weightScore * 0.35) + (waitScore * 0.20);
    }
}