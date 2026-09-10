package com.ewaste.server.domain.template;

import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;
import com.ewaste.server.domain.pattern.template.AbstractProcessingTemplate;
import com.ewaste.server.domain.pattern.template.HazardousDisposalWorkflow;
import com.ewaste.server.domain.pattern.template.RecyclingWorkflow;
import com.ewaste.server.domain.pattern.template.RefurbishWorkflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the Template Method processing algorithm sequence and outcome determinations.
 */
class ProcessingWorkflowTemplateTest {

    private EWasteCategory laptopCategory;
    private EWasteCategory batteryCategory;

    @BeforeEach
    void setUp() {
        laptopCategory = new EWasteCategory("Laptop", 12.0, false);
        batteryCategory = new EWasteCategory("Battery", 8.0, true);
    }

    @Test
    @DisplayName("RecyclingWorkflow executes invariant lifecycle and yields RECYCLE result")
    void testRecyclingWorkflowExecution() {
        RecyclingWorkflow workflow = new RecyclingWorkflow();

        LaptopWaste laptop = new LaptopWaste(
                laptopCategory, "ThinkPad", WasteCondition.MAJOR_DAMAGE, 2.5, false, true, 14.0
        );
        laptop.setId(55L);

        AbstractProcessingTemplate.ProcessingRecord record = workflow.executeProcessing(laptop);

        assertNotNull(record);
        assertEquals(55L, record.getItemId());
        assertEquals("Recycling", record.getWorkflowType());
        assertNotNull(record.getProcessedAt());
    }

    @Test
    @DisplayName("HazardousDisposalWorkflow safely handles toxic materials with HAZARDOUS_DISPOSAL result")
    void testHazardousDisposalWorkflowExecution() {
        HazardousDisposalWorkflow workflow = new HazardousDisposalWorkflow();

        BatteryWaste battery = new BatteryWaste(
                batteryCategory, "Li-Ion", WasteCondition.NON_FUNCTIONAL, 1.2,
                BatteryWaste.BatteryType.LITHIUM_ION, 3200, true
        );
        battery.setId(77L);

        AbstractProcessingTemplate.ProcessingRecord record = workflow.executeProcessing(battery);

        assertNotNull(record);
        assertEquals("Hazardous Disposal", record.getWorkflowType());
    }

    @Test
    @DisplayName("RefurbishWorkflow assigns REFURBISH result for usable devices")
    void testRefurbishWorkflowExecution() {
        RefurbishWorkflow workflow = new RefurbishWorkflow();

        LaptopWaste usableLaptop = new LaptopWaste(
                laptopCategory, "EliteBook", WasteCondition.WORKING, 1.8, false, true, 13.0
        );
        usableLaptop.setId(88L);

        AbstractProcessingTemplate.ProcessingRecord record = workflow.executeProcessing(usableLaptop);

        assertNotNull(record);
        assertEquals("Refurbishment", record.getWorkflowType());
    }
}