package com.ewaste.server.domain.factory;

import com.ewaste.server.domain.model.ewaste.*;
import com.ewaste.server.domain.pattern.factory.EWasteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates polymorphic instantiation of concrete e-waste types by EWasteFactory.
 */
class EWasteFactoryTest {

    private EWasteFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EWasteFactory();
    }

    @Test
    @DisplayName("Should instantiate LaptopWaste with proper category and attributes")
    void testCreateLaptopWaste() {
        EWasteItem item = factory.createItem("LAPTOP", "ThinkPad X1", 1.4, WasteCondition.USED);

        assertNotNull(item);
        assertInstanceOf(LaptopWaste.class, item);
        assertEquals("ThinkPad X1", item.getModel());
        assertEquals(1.4, item.getWeightKg());
    }

    @Test
    @DisplayName("Should instantiate BatteryWaste and flag chemical hazard defaults")
    void testCreateBatteryWasteHazardousFlag() {
        EWasteItem item = factory.createItem("BATTERY", "Li-Ion Pack", 0.5, WasteCondition.DAMAGED);

        assertNotNull(item);
        assertInstanceOf(BatteryWaste.class, item);
        assertTrue(item.isHazardous(), "Battery waste must be initialized as hazardous by default.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given unrecognized category name")
    void testInvalidCategoryThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                factory.createItem("NUCLEAR_REACTOR", "Core", 5000.0, WasteCondition.DAMAGED));
    }
}