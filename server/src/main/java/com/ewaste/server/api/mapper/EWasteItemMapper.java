package com.ewaste.server.api.mapper;
import.com.ewaste.server.api.dto.EWasteItemResponseDto;
import com.ewaste.server.api.dto.request.EWasteItemRequestDto;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.repository.EWasteCategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class EWasteItemMapper {

    private final EWasteCategoryRepository categoryRepository;

    public EWasteItemMapper(EWasteCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Convert EWasteItemRequestDto to EWasteItem entity
     */
    public EWasteItem toEntity(EWasteItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        EWasteItem item = new EWasteItem();
        item.setCategoryId(dto.getCategoryId());
        item.setModelName(dto.getModelName());
        item.setWeightKg(dto.getWeightKg());

        // If isHazardous not provided, use category default
        if (dto.getIsHazardous() != null) {
            item.setHazardous(dto.getIsHazardous());
        } else {
            // Fetch category to get default hazard status
            categoryRepository.findById(dto.getCategoryId())
                    .ifPresent(category -> item.setHazardous(category.isHazardousDefault()));
        }

        item.setWasteCondition(dto.getWasteCondition());
        item.setSpecificAttributes(dto.getSpecificAttributes());

        return item;
    }

    /**
     * Convert EWasteItem entity to EWasteItemResponseDto
     */
    public EWasteItemResponseDto toResponse(EWasteItem item) {
        if (item == null) {
            return null;
        }

        EWasteItemResponseDto dto = new EWasteItemResponseDto();
        dto.setItemId(item.getId());
        dto.setCategoryId(item.getId());
        dto.setModelName(item.getModelName());
        dto.setWeightKg(item.getWeightKg());
        dto.setIsHazardous(item.isHazardous());
        dto.setWasteCondition(item.getWasteCondition());
        dto.setSpecificAttributes(item.getSpecificAttributes());

        // Fetch category name
        categoryRepository.findById(item.getId())
                .ifPresent(category -> dto.setCategoryName(category.getCategoryName()));

        return dto;
    }

    /**
     * Convert EWasteItem entity to a simplified response (for list views)
     */
    public EWasteItemSummaryDto toSummary(EWasteItem item) {
        if (item == null) {
            return null;
        }

        EWasteItemSummaryDto dto = new EWasteItemSummaryDto();
        dto.setItemId(item.getId());
        dto.setModelName(item.getModelName());
        dto.setWeightKg(item.getWeightKg());
        dto.setIsHazardous(item.isHazardous());

        // Fetch category name
        categoryRepository.findById(item.getCategoryId())
                .ifPresent(category -> dto.setCategoryName(category.getCategoryName()));

        return dto;
    }

    // Inner class for detailed item response
    public static class EWasteItemResponseDto {
        private Long itemId;
        private Long categoryId;
        private String categoryName;
        private String modelName;
        private Double weightKg;
        private Boolean isHazardous;
        private String wasteCondition;
        private String specificAttributes;

        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public Double getWeightKg() { return weightKg; }
        public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

        public Boolean getIsHazardous() { return isHazardous; }
        public void setIsHazardous(Boolean isHazardous) { this.isHazardous = isHazardous; }

        public String getWasteCondition() { return wasteCondition; }
        public void setWasteCondition(String wasteCondition) { this.wasteCondition = wasteCondition; }

        public String getSpecificAttributes() { return specificAttributes; }
        public void setSpecificAttributes(String specificAttributes) { this.specificAttributes = specificAttributes; }

        @Override
        public String toString() {
            return "EWasteItemResponseDto{" +
                    "itemId=" + itemId +
                    ", categoryId=" + categoryId +
                    ", categoryName='" + categoryName + '\'' +
                    ", modelName='" + modelName + '\'' +
                    ", weightKg=" + weightKg +
                    ", isHazardous=" + isHazardous +
                    ", wasteCondition='" + wasteCondition + '\'' +
                    ", specificAttributes='" + specificAttributes + '\'' +
                    '}';
        }
    }

    // Inner class for summary response
    public static class EWasteItemSummaryDto {
        private Long itemId;
        private String modelName;
        private String categoryName;
        private Double weightKg;
        private Boolean isHazardous;

        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public Double getWeightKg() { return weightKg; }
        public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

        public Boolean getIsHazardous() { return isHazardous; }
        public void setIsHazardous(Boolean isHazardous) { this.isHazardous = isHazardous; }
    }
}