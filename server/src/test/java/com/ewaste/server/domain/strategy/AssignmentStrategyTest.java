package com.ewaste.server.domain.strategy;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;
import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.pattern.strategy.assignment.LeastBusyCollectorStrategy;
import com.ewaste.server.domain.pattern.strategy.assignment.NearestCollectorStrategy;
import com.ewaste.server.domain.pattern.strategy.assignment.SpecialistCollectorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates collector dispatch selection policies based on workload, location, and specialization.
 */
class AssignmentStrategyTest {

    private List<Collector> collectors;
    private PickupRequest pickup;

    @BeforeEach
    void setUp() {
        collectors = new ArrayList<>();

        Collector c1 = new Collector();
        c1.setCollectorId(1L);
        c1.setAvailable(true);
        c1.setArea("Zone North");
        c1.setCurrentWorkloadKg(80.0);
        c1.setMaxCapacityKg(100.0);
        c1.setVehicleType(VehicleType.BIKE);

        Collector c2 = new Collector();
        c2.setCollectorId(2L);
        c2.setAvailable(true);
        c2.setArea("Zone South");
        c2.setCurrentWorkloadKg(15.0);
        c2.setMaxCapacityKg(200.0);
        c2.setVehicleType(VehicleType.VAN);

        collectors.add(c1);
        collectors.add(c2);

        pickup = new PickupRequest();
        pickup.setAddress("42 Market Street, Zone South");
        pickup.setItems(new ArrayList<>());
    }

    @Test
    @DisplayName("LeastBusyCollectorStrategy selects candidate with minimal active cargo weight")
    void testLeastBusyCollectorSelection() {
        LeastBusyCollectorStrategy strategy = new LeastBusyCollectorStrategy();
        Optional<Collector> selected = strategy.selectCollector(pickup, collectors);

        assertTrue(selected.isPresent());
        assertEquals(2L, selected.get().getCollectorId());
    }

    @Test
    @DisplayName("NearestCollectorStrategy selects collector whose operational territory matches address")
    void testNearestCollectorSelection() {
        NearestCollectorStrategy strategy = new NearestCollectorStrategy();
        Optional<Collector> selected = strategy.selectCollector(pickup, collectors);

        assertTrue(selected.isPresent());
        assertEquals(2L, selected.get().getCollectorId());
    }

    @Test
    @DisplayName("SpecialistCollectorStrategy requires heavy/specialized vehicle when handling toxic cargo")
    void testSpecialistCollectorSelection() {
        SpecialistCollectorStrategy strategy = new SpecialistCollectorStrategy();

        BatteryWaste toxicItem = new BatteryWaste();
        toxicItem.setHazardous(true);
        pickup.getItems().add(new PickupItem(1L, toxicItem));

        Optional<Collector> selected = strategy.selectCollector(pickup, collectors);
        assertTrue(selected.isPresent());
        // Must select VAN/TRUCK collector rather than BIKE
        assertEquals(VehicleType.VAN, selected.get().getVehicleType());
    }
}