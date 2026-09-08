package com.ewaste.server.domain.pattern.builder;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;


public abstract class AbstractEWasteItemBuilder<T extends EWasteItem, B extends AbstractEWasteItemBuilder<T, B>> {

    protected EWasteCategory category;
    protected String modelName;
    protected WasteCondition condition = WasteCondition.WORKING;
    protected double weightKg;

    public B category(EWasteCategory category) {
        this.category = category;
        return self();
    }

    public B modelName(String modelName) {
        this.modelName = modelName;
        return self();
    }

    public B condition(WasteCondition condition) {
        this.condition = condition;
        return self();
    }

    public B weightKg(double weightKg) {
        this.weightKg = weightKg;
        return self();
    }

    /** Returns {@code this}, typed as the concrete builder subclass, so chaining preserves that type. */
    protected abstract B self();

    /** Validates the attributes common to every EWasteItem; called by each subclass's build(). */
    protected void validateMandatory() {
        if (category == null) {
            throw new IllegalStateException("category is required");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalStateException("modelName is required");
        }
        if (weightKg <= 0) {
            throw new IllegalStateException("weightKg must be greater than zero");
        }
    }

    /** Validates mandatory and type-specific fields, then constructs the item. */
    public abstract T build();
}
