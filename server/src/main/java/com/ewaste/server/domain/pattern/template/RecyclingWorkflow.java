package com.ewaste.server.domain.pattern.template;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

/** Breaks a non-hazardous, non-repairable item down into raw material streams. */
public class RecyclingWorkflow extends AbstractProcessingTemplate {

    @Override
    protected boolean isEligible(EWasteItem item) {
        return !item.isHazardous();
    }

    @Override
    protected ProcessingOutcome determineProcessingOutcome(EWasteItem item, WasteCondition inspectedCondition) {
        String notes = "Dismantled and sorted into material streams (metal, plastic, glass) for recycling. "
                + item.getProcessingInstructions();
        return new ProcessingOutcome(EWasteItem.ProcessingType.RECYCLE, notes);
    }

    @Override
    public String getWorkflowName() {
        return "Recycling";
    }
}
