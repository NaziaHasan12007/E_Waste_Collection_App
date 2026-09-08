package com.ewaste.server.domain.pattern.builder;

import com.ewaste.server.domain.model.ewaste.MobileWaste;

public class MobileWasteBuilder extends AbstractEWasteItemBuilder<MobileWaste, MobileWasteBuilder> {

    private boolean hasSimCard;
    private int storageGb;

    public MobileWasteBuilder hasSimCard(boolean hasSimCard) {
        this.hasSimCard = hasSimCard;
        return this;
    }

    public MobileWasteBuilder storageGb(int storageGb) {
        this.storageGb = storageGb;
        return this;
    }

    @Override
    protected MobileWasteBuilder self() {
        return this;
    }

    @Override
    public MobileWaste build() {
        validateMandatory();
        return new MobileWaste(category, modelName, condition, weightKg, hasSimCard, storageGb);
    }
}
