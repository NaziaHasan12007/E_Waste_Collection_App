package com.ewaste.client.api;

import com.ewaste.client.dto.request.EWasteItemClientRequest;
import com.ewaste.client.dto.response.EWasteCategoryClientResponse;
import com.ewaste.client.dto.response.EWasteItemClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;

/**
 * Client for item catalog and submission endpoints (/ewaste).
 */
public class EWasteApiClient extends ApiClient {

    private static EWasteApiClient instance;
    private static final String BASE_PATH = "/ewaste";

    private EWasteApiClient() {
        super();
    }

    public static EWasteApiClient getInstance() {
        if (instance == null) {
            instance = new EWasteApiClient();
        }
        return instance;
    }

    public List<EWasteCategoryClientResponse> getCategories() {
        return get(BASE_PATH + "/categories", new TypeReference<List<EWasteCategoryClientResponse>>() {});
    }

    public EWasteItemClientResponse submitItem(EWasteItemClientRequest request) {
        return post(BASE_PATH + "/items", request, EWasteItemClientResponse.class);
    }

    public List<EWasteItemClientResponse> getItemsForCustomer(long customerId) {
        return get(BASE_PATH + "/items/customer/" + customerId, new TypeReference<List<EWasteItemClientResponse>>() {});
    }

    public EWasteItemClientResponse getItem(long itemId) {
        return get(BASE_PATH + "/items/" + itemId, EWasteItemClientResponse.class);
    }

    // ========== ADDITIONAL HELPER METHODS ==========

    public EWasteCategoryClientResponse getCategoryById(long categoryId) {
        List<EWasteCategoryClientResponse> categories = getCategories();
        if (categories == null) return null;
        return categories.stream()
                .filter(c -> c.getId() != null && c.getId().equals(categoryId))
                .findFirst()
                .orElse(null);
    }

    public EWasteCategoryClientResponse getCategoryByName(String name) {
        List<EWasteCategoryClientResponse> categories = getCategories();
        if (categories == null) return null;
        return categories.stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<EWasteItemClientResponse> getItemsByCategory(long categoryId) {
        // This would require a server endpoint for items by category
        // For now, return empty list
        return Collections.emptyList();
    }

    public double calculateTotalWeight(List<EWasteItemClientResponse> items) {
        if (items == null) return 0.0;
        return items.stream()
                .mapToDouble(item -> item.getWeightKg() != null ? item.getWeightKg() : 0.0)
                .sum();
    }

    public int calculateTotalPoints(List<EWasteItemClientResponse> items) {
        if (items == null) return 0;
        return items.stream()
                .mapToInt(item -> item.getRewardPoints() != null ? item.getRewardPoints() : 0)
                .sum();
    }
}