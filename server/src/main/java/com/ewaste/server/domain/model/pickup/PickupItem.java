package com.ewaste.server.domain.model.pickup;

import com.ewaste.server.domain.model.ewaste.EWasteItem;

import java.util.Objects;

/**
 * Link entity between PickupRequest and EWasteItem.
 * Represents an item included in a pickup request.
 */
public class PickupItem {

    private Long pickupItemId;
    private Long pickupId;
    private PickupRequest pickup;
    private Long itemId;
    private EWasteItem item;

    public PickupItem() {}

    public PickupItem(Long pickupId, Long itemId) {
        this.pickupId = pickupId;
        this.itemId = itemId;
    }

    public PickupItem(PickupRequest pickup, EWasteItem item) {
        this.pickup = pickup;
        this.item = item;
        if (pickup != null) {
            this.pickupId = pickup.getPickupId();
        }
        if (item != null) {
            this.itemId = item.getItemId();
        }
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getPickupItemId() {
        return pickupItemId;
    }

    public void setPickupItemId(Long pickupItemId) {
        this.pickupItemId = pickupItemId;
    }

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public PickupRequest getPickup() {
        return pickup;
    }

    public void setPickup(PickupRequest pickup) {
        this.pickup = pickup;
        if (pickup != null) {
            this.pickupId = pickup.getPickupId();
        }
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public EWasteItem getItem() {
        return item;
    }

    public void setItem(EWasteItem item) {
        this.item = item;
        if (item != null) {
            this.itemId = item.getItemId();
        }
    }

    // ========== HELPER METHODS ==========

    public double getWeightKg() {
        return item != null ? item.getWeightKg() : 0.0;
    }

    public boolean isHazardous() {
        return item != null && item.getIsHazardous();
    }

    public String getCategoryName() {
        return item != null && item.getCategory() != null ?
                item.getCategory().getCategoryName() : "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickupItem)) return false;
        PickupItem that = (PickupItem) o;
        return Objects.equals(pickupItemId, that.pickupItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickupItemId);
    }

    @Override
    public String toString() {
        return "PickupItem{" +
                "pickupItemId=" + pickupItemId +
                ", pickupId=" + pickupId +
                ", itemId=" + itemId +
                '}';
    }
}