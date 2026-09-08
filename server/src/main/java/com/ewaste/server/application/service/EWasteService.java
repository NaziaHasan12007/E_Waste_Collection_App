package com.ewaste.server.application.service;

import com.ewaste.server.api.dto.request.EWasteItemRequestDto;
import com.ewaste.server.api.dto.response.EWasteCategoryResponse;
import com.ewaste.server.api.dto.response.EWasteItemResponseDto;
import com.ewaste.server.api.mapper.EWasteItemMapper;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.PickupItem;
import com.ewaste.server.domain.model.PickupRequest;
import com.ewaste.server.domain.repository.EWasteCategoryRepository;
import com.ewaste.server.domain.repository.EWasteItemRepository;
import com.ewaste.server.domain.repository.PickupItemRepository;
import com.ewaste.server.domain.repository.PickupRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EWasteService {

    private final EWasteItemRepository itemRepository;
    private final EWasteCategoryRepository categoryRepository;
    private final PickupItemRepository pickupItemRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final EWasteItemMapper itemMapper;

    public EWasteService(EWasteItemRepository itemRepository,
                         EWasteCategoryRepository categoryRepository,
                         PickupItemRepository pickupItemRepository,
                         PickupRequestRepository pickupRequestRepository,
                         EWasteItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.pickupItemRepository = pickupItemRepository;
        this.pickupRequestRepository = pickupRequestRepository;
        this.itemMapper = itemMapper;
    }

    /**
     * Create a new e-waste item
     */
    @Transactional
    public EWasteItemResponseDto createItem(EWasteItemRequestDto dto) {
        // Validate category exists
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // Convert DTO to entity
        EWasteItem item = itemMapper.toEntity(dto);

        // Save item
        EWasteItem savedItem = itemRepository.save(item);

        // Return response
        return itemMapper.toResponse(savedItem);
    }

    /**
     * Get all e-waste items
     */
    public List<EWasteItemResponseDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get e-waste item by ID
     */
    public EWasteItemResponseDto getItemById(Long itemId) {
        EWasteItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        return itemMapper.toResponse(item);
    }

    /**
     * Get items by category
     */
    public List<EWasteItemResponseDto> getItemsByCategory(Long categoryId) {
        // Validate category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        return itemRepository.findByCategoryId(categoryId).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get items by pickup request
     */
    public List<EWasteItemResponseDto> getItemsByPickupId(Long pickupId) {
        // Validate pickup exists
        pickupRequestRepository.findById(pickupId)
                .orElseThrow(() -> new RuntimeException("Pickup not found with ID: " + pickupId));

        return itemRepository.findByPickupId(pickupId).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get items by hazard status
     */
    public List<EWasteItemResponseDto> getItemsByHazardStatus(boolean isHazardous) {
        return itemRepository.findByHazardousStatus(isHazardous).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an e-waste item
     */
    @Transactional
    public EWasteItemResponseDto updateItem(Long itemId, EWasteItemRequestDto dto) {
        // Find existing item
        EWasteItem existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));

        // Validate category exists
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // Update fields
        existingItem.setCategoryId(dto.getCategoryId());
        existingItem.setModelName(dto.getModelName());
        existingItem.setWeightKg(dto.getWeightKg());
        existingItem.setHazardous(dto.getIsHazardous() != null ? dto.getIsHazardous() : existingItem.isHazardous());
        existingItem.setWasteCondition(dto.getWasteCondition());
        existingItem.setSpecificAttributes(dto.getSpecificAttributes());

        // Save updated item
        EWasteItem updatedItem = itemRepository.save(existingItem);

        return itemMapper.toResponse(updatedItem);
    }

    /**
     * Delete an e-waste item
     */
    @Transactional
    public void deleteItem(Long itemId) {
        // Check if item exists
        if (!itemRepository.findById(itemId).isPresent()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }

        // Delete item (cascade will handle pickup_items)
        itemRepository.deleteById(itemId);
    }

    /**
     * Calculate total weight for a pickup
     */
    public double calculateTotalWeight(Long pickupId) {
        return itemRepository.calculateTotalWeightByPickupId(pickupId);
    }

    /**
     * Get all categories
     */
    public List<EWasteCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public EWasteCategoryResponse getCategoryById(Long categoryId) {
        EWasteCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        return mapCategoryToResponse(category);
    }

    /**
     * Create a new category
     */
    @Transactional
    public EWasteCategoryResponse createCategory(EWasteCategoryResponse dto) {
        // Check if category name exists
        if (categoryRepository.existsByName(dto.getCategoryName())) {
            throw new RuntimeException("Category name already exists: " + dto.getCategoryName());
        }

        EWasteCategory category = new EWasteCategory();
        category.setCategoryName(dto.getCategoryName());
        category.setBasePointsPerKg(dto.getBasePointsPerKg());
        category.setHazardousDefault(dto.getIsHazardousDefault());

        EWasteCategory savedCategory = categoryRepository.save(category);
        return mapCategoryToResponse(savedCategory);
    }

    /**
     * Map Category entity to Response DTO
     */
    private EWasteCategoryResponse mapCategoryToResponse(EWasteCategory category) {
        return new EWasteCategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getBasePointsPerKg(),
                category.isHazardousDefault()
        );
    }
}