package com.ewaste.server.domain.strategy;

import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.pattern.strategy.priority.CompositePriorityStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.HazardousPriorityStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.WaitingTimePriorityStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.WeightPriorityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates dynamic priority scoring for hazardous items, cargo weight, and queue waiting time.
 */
class PriorityStrategyTest {

    private PickupRequest pickup;

    @BeforeEach
    void setUp() {
        pickup = new PickupRequest();
        pickup.setItems(new ArrayList<>());
        pickup.setCreatedAt(LocalDateTime.now().minusHours(10));
    }

    @Test
    @DisplayName("HazardousPriorityStrategy should score toxic/hazardous items higher than non-hazardous items")
    void testHazardousPriorityScoring() {
        HazardousPriorityStrategy strategy = new HazardousPriorityStrategy();

        // Non-hazardous setup
        LaptopWaste safeLaptop = new LaptopWaste();
        safeLaptop.setHazardous(false);
        pickup.getItems().add(new PickupItem(1L, safeLaptop));
        double nonHazardousScore = strategy.calculatePriority(pickup);

        // Hazardous item addition
        BatteryWaste toxicBattery = new BatteryWaste();
        toxicBattery.setHazardous(true);
        pickup.getItems().add(new PickupItem(2L, toxicBattery));
        double hazardousScore = strategy.calculatePriority(pickup);

        assertTrue(hazardousScore > nonHazardousScore, "Hazardous cargo must receive higher operational urgency.");
    }

    @Test
    @DisplayName("WeightPriorityStrategy should scale proportionally up to benchmark cap")
    void testWeightPriorityScoring() {
        WeightPriorityStrategy strategy = new WeightPriorityStrategy();

        LaptopWaste lightItem = new LaptopWaste();
        lightItem.setWeightKg(10.0);
        pickup.getItems().add(new PickupItem(1L, lightItem));
        double lightScore = strategy.calculatePriority(pickup);

        LaptopWaste heavyItem = new LaptopWaste();
        heavyItem.setWeightKg(90.0);
        pickup.getItems().add(new PickupItem(2L, heavyItem));
        double heavyScore = strategy.calculatePriority(pickup);

        assertTrue(heavyScore > lightScore);
        assertTrue(heavyScore <= 50.0, "Score should not exceed maximum weight score limit.");
    }

    @Test
    @DisplayName("WaitingTimePriorityStrategy should reward longer pending durations")
    void testWaitingTimePriorityScoring() {
        WaitingTimePriorityStrategy strategy = new WaitingTimePriorityStrategy();
        double tenHoursScore = strategy.calculatePriority(pickup);

        pickup.setCreatedAt(LocalDateTime.now().minusHours(2));
        double twoHoursScore = strategy.calculatePriority(pickup);

        assertTrue(tenHoursScore > twoHoursScore, "Longer queue wait must escalate priority score.");
    }

    @Test
    @DisplayName("CompositePriorityStrategy aggregates individual strategy results correctly")
    void testCompositePriorityScoring() {
        CompositePriorityStrategy composite = new CompositePriorityStrategy();

        BatteryWaste hazardousBattery = new BatteryWaste();
        hazardousBattery.setHazardous(true);
        hazardousBattery.setWeightKg(25.0);
        pickup.getItems().add(new PickupItem(1L, hazardousBattery));

        double compositeScore = composite.calculatePriority(pickup);
        assertTrue(compositeScore > 0.0);
    }
}