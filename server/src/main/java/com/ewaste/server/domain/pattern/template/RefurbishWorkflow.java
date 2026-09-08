package com.ewaste.server.domain.pattern.template;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

/** Restores a working or lightly-damaged item for resale or donation. */
public class RefurbishWorkflow extends AbstractProcessingTemplate {

    private static final double REFURBISH_BONUS_MULTIPLIER = 1.2;

    @Override
    protected boolean isEligible(EWasteItem item) {
        return !item.isHazardous()
                && (item.getCondition() == WasteCondition.WORKING || item.getCondition() == WasteCondition.MINOR_DAMAGE);
    }

    @Override
    protected ProcessingOutcome determineProcessingOutcome(EWasteItem item, WasteCondition inspectedCondition) {
        String notes = "Cleaned, functionally tested, and prepared for resale/donation. "
                + item.getProcessingInstructions();
        return new ProcessingOutcome(EWasteItem.ProcessingType.REFURBISH, notes);
    }

    @Override
    protected double calculateReward(EWasteItem item, ProcessingOutcome outcome) {
        // Refurbishable items extend a device's life instead of ending it, so they earn a small bonus.
        return Math.round(item.calculateRewardPoints() * REFURBISH_BONUS_MULTIPLIER * 100.0) / 100.0;
    }

    @Override
    public String getWorkflowName() {
        return "Refurbishment";
    }
}
