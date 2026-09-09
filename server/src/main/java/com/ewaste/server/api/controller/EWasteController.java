package com.ewaste.server.api.controller;

import com.ewaste.server.api.dto.request.EWasteItemRequestDto;
import com.ewaste.server.api.dto.response.EWasteCategoryResponse;
import com.ewaste.server.api.dto.response.EWasteItemResponseDto;
import com.ewaste.server.application.service.EWasteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ewaste")
public class EWasteController {

    private final EWasteService eWasteService;

    public EWasteController(EWasteService eWasteService) {
        this.eWasteService = eWasteService;
    }

    // ==================== Item Endpoints ====================

    /**
     * Get all e-waste items
     */
    @GetMapping("/items")
    public ResponseEntity<List<EWasteItemResponseDto>> getAllItems() {
        return ResponseEntity.ok(eWasteService.getAllItems());
    }

    /**
     * Get e-waste item by ID
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<EWasteItemResponseDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(eWasteService.getItemById(itemId));
    }

    /**
     * Get items by category
     */
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<EWasteItemResponseDto>> getItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(eWasteService.getItemsByCategory(categoryId));
    }

    /**
     * Get items by pickup ID
     */
    @GetMapping("/items/pickup/{pickupId}")
    public ResponseEntity<List<EWasteItemResponseDto>> getItemsByPickup(@PathVariable Long pickupId) {
        return ResponseEntity.ok(eWasteService.getItemsByPickupId(pickupId));
    }

    /**
     * Get items by hazard status
     */
    @GetMapping("/items/hazardous")
    public ResponseEntity<List<EWasteItemResponseDto>> getHazardousItems(@RequestParam boolean hazardous) {
        return ResponseEntity.ok(eWasteService.getItemsByHazardStatus(hazardous));
    }

    /**
     * Create a new e-waste item
     */
    @PostMapping("/items")
    public ResponseEntity<EWasteItemResponseDto> createItem(@Valid @RequestBody EWasteItemRequestDto request) {
        EWasteItemResponseDto response = eWasteService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an e-waste item
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<EWasteItemResponseDto> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody EWasteItemRequestDto request) {
        return ResponseEntity.ok(eWasteService.updateItem(itemId, request));
    }

    /**
     * Delete an e-waste item
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        eWasteService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calculate total weight for a pickup
     */
    @GetMapping("/items/weight/{pickupId}")
    public ResponseEntity<Double> calculateTotalWeight(@PathVariable Long pickupId) {
        return ResponseEntity.ok(eWasteService.calculateTotalWeight(pickupId));
    }

    // ==================== Category Endpoints ====================

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<EWasteCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(eWasteService.getAllCategories());
    }

    /**
     * Get category by ID
     */
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<EWasteCategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(eWasteService.getCategoryById(categoryId));
    }

    /**
     * Create a new category
     */
    @PostMapping("/categories")
    public ResponseEntity<EWasteCategoryResponse> createCategory(
            @Valid @RequestBody EWasteCategoryResponse request) {
        EWasteCategoryResponse response = eWasteService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}