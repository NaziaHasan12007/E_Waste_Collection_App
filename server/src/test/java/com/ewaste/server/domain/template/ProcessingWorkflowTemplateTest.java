package com.ewaste.server.domain.template;

import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.model.processing.ProcessingResult;
import com.ewaste.server.domain.model.processing.RecyclingCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the Template Method processing algorithm sequence and outcome determinations.
 */
class ProcessingWorkflowTemplateTest {

    private RecyclingCenter center;

    @BeforeEach
    void setUp() {
        center = new RecyclingCenter();
        center.setCenterId(10L);
        center.setCenterName("Central EcoFacility");
        center.setCapacityKg(5000.0);
    }

    @Test
    @DisplayName("RecyclingWorkflow executes invariant lifecycle and yields RECYCLE result")
    void testRecyclingWorkflowExecution() {
        RecyclingWorkflow workflow = new RecyclingWorkflow();

        LaptopWaste laptop = new LaptopWaste();
        laptop.setItemId(55L);
        laptop.setWeightKg(2.5);
        laptop.setCondition(WasteCondition.DAMAGED);

        ProcessingRecord record = workflow.executeProcessing(laptop, center);

        assertNotNull(record);
        assertEquals(55L, record.getItemId());
        assertEquals(10L, record.getCenterId());
        assertEquals(ProcessingResult.RECYCLE, record.getProcessingResult());
        assertNotNull(record.getProcessedAt());
    }

    @Test
    @DisplayName("HazardousDisposalWorkflow safely handles toxic materials with HAZARDOUS_DISPOSAL result")
    void testHazardousDisposalWorkflowExecution() {
        HazardousDisposalWorkflow workflow = new HazardousDisposalWorkflow();

        BatteryWaste battery = new BatteryWaste();
        battery.setItemId(77L);
        battery.setWeightKg(1.2);
        battery.setHazardous(true);
        battery.setCondition(WasteCondition.DAMAGED);

        ProcessingRecord record = workflow.executeProcessing(battery, center);

        assertNotNull(record);
        assertEquals(ProcessingResult.HAZARDOUS_DISPOSAL, record.getProcessingResult());
    }

    @Test
    @DisplayName("RefurbishWorkflow assigns REFURBISH result for usable devices")
    void testRefurbishWorkflowExecution() {
        RefurbishWorkflow workflow = new RefurbishWorkflow();

        LaptopWaste usableLaptop = new LaptopWaste();
        usableLaptop.setItemId(88L);
        usableLaptop.setWeightKg(1.8);
        usableLaptop.setCondition(WasteCondition.USED);

        ProcessingRecord record = workflow.executeProcessing(usableLaptop, center);

        assertNotNull(record);
        assertEquals(ProcessingResult.REFURBISH, record.getProcessingResult());
    }
}