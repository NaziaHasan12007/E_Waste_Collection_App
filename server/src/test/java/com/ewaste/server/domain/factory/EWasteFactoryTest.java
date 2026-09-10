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

    private EWasteCategory category;

    @BeforeEach
    void setUp() {
        category = new EWasteCategory("General", 10.0, false);
    }

    @Test
    @DisplayName("Should instantiate LaptopWaste with proper category and attributes")
    void testCreateLaptopWaste() {
        EWasteFactory.EWasteItemRequest request = new EWasteFactory.EWasteItemRequest()
                .setType(EWasteFactory.EWasteType.LAPTOP)
                .setCategory(category)
                .setModelName("ThinkPad X1")
                .setWeightKg(1.4)
                .setCondition(WasteCondition.MINOR_DAMAGE)
                .setHasBattery(true)
                .setHasHardDrive(true)
                .setScreenSizeInches(14.0);
        EWasteItem item = EWasteFactory.createEWasteItem(request);

        assertNotNull(item);
        assertInstanceOf(LaptopWaste.class, item);
        assertEquals("ThinkPad X1", item.getModelName());
        assertEquals(1.4, item.getWeightKg());
    }

    @Test
    @DisplayName("Should instantiate BatteryWaste and flag chemical hazard defaults")
    void testCreateBatteryWasteHazardousFlag() {
        EWasteFactory.EWasteItemRequest request = new EWasteFactory.EWasteItemRequest()
                .setType(EWasteFactory.EWasteType.BATTERY)
                .setCategory(category)
                .setModelName("Li-Ion Pack")
                .setWeightKg(0.5)
                .setCondition(WasteCondition.MAJOR_DAMAGE)
                .setBatteryType(BatteryWaste.BatteryType.LITHIUM_ION)
                .setCapacityMah(2500)
                .setSwollenOrLeaking(false);
        EWasteItem item = EWasteFactory.createEWasteItem(request);

        assertNotNull(item);
        assertInstanceOf(BatteryWaste.class, item);
        assertTrue(item.isHazardous(), "Battery waste must be initialized as hazardous by default.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given unrecognized category name")
    void testInvalidCategoryThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                EWasteFactory.EWasteType.fromString("NUCLEAR_REACTOR"));
    }
}