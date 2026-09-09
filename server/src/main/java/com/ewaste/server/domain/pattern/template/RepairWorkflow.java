package com.ewaste.server.domain.pattern.template;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

/** Attempts to fix a moderately damaged, non-hazardous item instead of scrapping it. */
public class RepairWorkflow extends AbstractProcessingTemplate {

    @Override
    protected boolean isEligible(EWasteItem item) {
        return !item.isHazardous() && item.getCondition().isRepairable();
    }

    @Override
    protected ProcessingOutcome determineProcessingOutcome(EWasteItem item, WasteCondition inspectedCondition) {
        String notes = "Diagnosed and repaired based on inspected condition (" + inspectedCondition + "). "
                + item.getProcessingInstructions();
        return new ProcessingOutcome(EWasteItem.ProcessingType.REPAIR, notes);
    }

    @Override
    public String getWorkflowName() {
        return "Repair";
    }
}
