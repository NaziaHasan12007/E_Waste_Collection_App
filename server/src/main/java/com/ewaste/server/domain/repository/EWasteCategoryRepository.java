package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import java.util.List;
import java.util.Optional;

public interface EWasteCategoryRepository {
    /**
     * Save a category to the database
     * @param category The category to save
     * @return The saved category with generated ID
     */
    EWasteCategory save(EWasteCategory category);

    /**
     * Find a category by its ID
     * @param categoryId The category ID
     * @return Optional containing the category if found
     */
    Optional<EWasteCategory> findById(Long categoryId);

    /**
     * Find a category by its name
     * @param categoryName The category name
     * @return Optional containing the category if found
     */
    Optional<EWasteCategory> findByName(String categoryName);

    /**
     * Find all categories
     * @return List of all categories
     */
    List<EWasteCategory> findAll();

    /**
     * Delete a category by its ID
     * @param categoryId The category ID to delete
     */
    void deleteById(Long categoryId);

    /**
     * Check if a category exists by name
     * @param categoryName The category name to check
     * @return true if a category with the name exists
     */
    boolean existsByName(String categoryName);
}