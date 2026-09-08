package com.ewaste.client.dto.request;

import java.util.List;

public class CreatePickupClientRequest {

    private Long userId;
    private String address;
    private String preferredDate;
    private String preferredTime;
    private List<Long> itemIds;

    public CreatePickupClientRequest() {}

    public CreatePickupClientRequest(Long userId, String address, String preferredDate, List<Long> itemIds) {
        this.userId = userId;
        this.address = address;
        this.preferredDate = preferredDate;
        this.itemIds = itemIds;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    @Override
    public String toString() {
        return "CreatePickupClientRequest{" +
                "userId=" + userId +
                ", address='" + address + '\'' +
                ", preferredDate='" + preferredDate + '\'' +
                ", preferredTime='" + preferredTime + '\'' +
                ", itemIds=" + itemIds +
                '}';
    }
}