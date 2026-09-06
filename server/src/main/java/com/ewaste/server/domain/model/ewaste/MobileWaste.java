package com.ewaste.server.domain.model.ewaste;

/** Mobile / smartphone. */
public class MobileWaste extends EWasteItem {

    private static final double BASE_POINTS_PER_KG = 20.0;

    private boolean hasSimCard;
    private int storageGb;

    public MobileWaste(EWasteCategory category, String modelName, WasteCondition condition, double weightKg,
                       boolean hasSimCard, int storageGb) {
        super(category, modelName, condition, weightKg);
        this.hasSimCard = hasSimCard;
        this.storageGb = storageGb;
    }

    @Override
    public double calculateRewardPoints() {
        double points = getWeightKg() * BASE_POINTS_PER_KG * getCondition().getRewardMultiplier();
        return Math.round(points * 100.0) / 100.0;
    }

    @Override
    public boolean isHazardous() {
        return getCondition() == WasteCondition.SCRAP_ONLY;
    }

    @Override
    public String getProcessingInstructions() {
        StringBuilder sb = new StringBuilder("Remove the built-in battery before shredding/refurbishment.");
        if (hasSimCard) {
            sb.append(" Confirm the SIM card has been removed and returned to the customer.");
        }
        return sb.toString();
    }

    public boolean isHasSimCard() {
        return hasSimCard;
    }

    public void setHasSimCard(boolean hasSimCard) {
        this.hasSimCard = hasSimCard;
    }

    public int getStorageGb() {
        return storageGb;
    }

    public void setStorageGb(int storageGb) {
        this.storageGb = storageGb;
    }
}
