package com.ewaste.server.domain.pattern.template;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

/** Safely disposes of items that pose a chemical or environmental hazard. */
public class HazardousDisposalWorkflow extends AbstractProcessingTemplate {

    @Override
    protected boolean isEligible(EWasteItem item) {
        return item.isHazardous();
    }

    @Override
    protected ProcessingOutcome determineProcessingOutcome(EWasteItem item, WasteCondition inspectedCondition) {
        String notes = "Handled by certified hazardous-waste personnel and disposed of per environmental regulations. "
                + item.getProcessingInstructions();
        return new ProcessingOutcome(EWasteItem.ProcessingType.HAZARDOUS_DISPOSAL, notes);
    }

    @Override
    public String getWorkflowName() {
        return "Hazardous Disposal";
    }
}
