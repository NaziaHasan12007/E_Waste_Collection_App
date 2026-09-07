package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import java.util.List;
import java.util.Optional;

public interface EWasteItemRepository {
    /**
     * Save an e-waste item to the database
     * @param item The item to save
     * @return The saved item with generated ID
     */
    EWasteItem save(EWasteItem item);

    /**
     * Find an item by its ID
     * @param itemId The item ID
     * @return Optional containing the item if found
     */
    Optional<EWasteItem> findById(Long itemId);

    /**
     * Find all items
     * @return List of all items
     */
    List<EWasteItem> findAll();

    /**
     * Find items by category ID
     * @param categoryId The category ID
     * @return List of items in the specified category
     */
    List<EWasteItem> findByCategoryId(Long categoryId);

    /**
     * Find items by hazard status
     * @param isHazardous The hazard status
     * @return List of items with the specified hazard status
     */
    List<EWasteItem> findByHazardousStatus(boolean isHazardous);

    /**
     * Find items by pickup request ID
     * @param pickupId The pickup request ID
     * @return List of items associated with the pickup
     */
    List<EWasteItem> findByPickupId(Long pickupId);

    /**
     * Delete an item by its ID
     * @param itemId The item ID to delete
     */
    void deleteById(Long itemId);

    /**
     * Calculate total weight of items in a pickup
     * @param pickupId The pickup ID
     * @return The total weight in kilograms
     */
    double calculateTotalWeightByPickupId(Long pickupId);
}