package com.ewaste.server.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePickupRequestDto {

    private Long userId;
    private String address;
    private String preferredDate;
    private String preferredTime;
    private List<Long> itemIds = new ArrayList<>();

    public CreatePickupRequestDto() {}

    public CreatePickupRequestDto(Long userId, String address, String preferredDate,
                                  String preferredTime, List<Long> itemIds) {
        this.userId = userId;
        this.address = address;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        if (itemIds != null) {
            this.itemIds = itemIds;
        }
    }

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
        this.itemIds = itemIds != null ? itemIds : new ArrayList<>();
    }
}