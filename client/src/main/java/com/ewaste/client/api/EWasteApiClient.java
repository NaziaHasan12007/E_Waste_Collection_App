package com.ewaste.client.api;

import com.ewaste.client.dto.request.EWasteItemClientRequest;
import com.ewaste.client.dto.response.EWasteCategoryClientResponse;
import com.ewaste.client.dto.response.EWasteItemClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * Client for item catalog and submission endpoints (/ewaste).
 */
public class EWasteApiClient extends ApiClient {

    private static final String BASE_PATH = "/ewaste";

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
}