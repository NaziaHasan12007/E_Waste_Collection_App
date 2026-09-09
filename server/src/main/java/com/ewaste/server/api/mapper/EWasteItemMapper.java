package com.ewaste.server.api.mapper;

import com.ewaste.server.api.dto.request.EWasteItemRequestDto;
import com.ewaste.server.api.dto.response.EWasteItemResponseDto;
import com.ewaste.server.domain.model.ewaste.ApplianceWaste;
import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.DisplayWaste;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.ewaste.MobileWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;
import com.ewaste.server.domain.repository.EWasteCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EWasteItemMapper {

    private final EWasteCategoryRepository categoryRepository;

    public EWasteItemMapper(EWasteCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public EWasteItem toEntity(EWasteItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        EWasteCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with ID: " + dto.getCategoryId()));
        WasteCondition condition = WasteCondition.valueOf(dto.getWasteCondition().trim().toUpperCase(Locale.ROOT));
        String type = category.getCategoryName().trim().toUpperCase(Locale.ROOT);
        double weight = dto.getWeightKg();

        EWasteItem item = switch (type) {
            case "LAPTOP" -> new LaptopWaste(category, dto.getModelName(), condition, weight,
                    attributeBoolean(dto.getSpecificAttributes(), "hasBattery"),
                    attributeBoolean(dto.getSpecificAttributes(), "hasHardDrive"),
                    attributeDouble(dto.getSpecificAttributes(), "screenSizeInches"));
            case "MOBILE", "MOBILE PHONE" -> new MobileWaste(category, dto.getModelName(), condition, weight,
                    attributeBoolean(dto.getSpecificAttributes(), "hasSimCard"),
                    attributeInt(dto.getSpecificAttributes(), "storageGb"));
            case "BATTERY" -> new BatteryWaste(category, dto.getModelName(), condition, weight,
                    attributeEnum(dto.getSpecificAttributes(), "batteryType", BatteryWaste.BatteryType.OTHER),
                    attributeDouble(dto.getSpecificAttributes(), "capacityMah"),
                    attributeBoolean(dto.getSpecificAttributes(), "swollenOrLeaking"));
            case "DISPLAY" -> new DisplayWaste(category, dto.getModelName(), condition, weight,
                    attributeEnum(dto.getSpecificAttributes(), "displayType", DisplayWaste.DisplayType.LCD),
                    attributeDouble(dto.getSpecificAttributes(), "screenSizeInches"));
            case "APPLIANCE" -> new ApplianceWaste(category, dto.getModelName(), condition, weight,
                    attributeEnum(dto.getSpecificAttributes(), "applianceType", ApplianceWaste.ApplianceType.OTHER),
                    attributeBoolean(dto.getSpecificAttributes(), "hasRefrigerant"),
                    attributeDouble(dto.getSpecificAttributes(), "powerRatingWatts"));
            default -> throw new IllegalArgumentException("Unknown e-waste category: " + category.getCategoryName());
        };
        item.setDescription(dto.getSpecificAttributes());
        return item;
    }

    public EWasteItemResponseDto toResponse(EWasteItem item) {
        if (item == null) {
            return null;
        }

        EWasteItemResponseDto dto = new EWasteItemResponseDto();
        dto.setItemId(item.getId());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getCategoryName());
        dto.setModelName(item.getModelName());
        dto.setWeightKg(item.getWeightKg());
        dto.setIsHazardous(item.isHazardous());
        dto.setWasteCondition(item.getCondition().name());
        dto.setDescription(item.getDescription());
        dto.setSpecificAttributes(item.toString());
        return dto;
    }

    private static String value(String attributes, String key) {
        if (attributes == null) {
            return null;
        }
        String marker = "\"" + key + "\":";
        int start = attributes.indexOf(marker);
        if (start < 0) {
            return null;
        }
        start += marker.length();
        while (start < attributes.length() && Character.isWhitespace(attributes.charAt(start))) {
            start++;
        }
        int end = start;
        boolean quoted = start < attributes.length() && attributes.charAt(start) == '"';
        if (quoted) {
            start++;
            end = attributes.indexOf('"', start);
            return end < 0 ? null : attributes.substring(start, end);
        }
        while (end < attributes.length() && attributes.charAt(end) != ',' && attributes.charAt(end) != '}') {
            end++;
        }
        return attributes.substring(start, end).trim();
    }

    private static boolean attributeBoolean(String attributes, String key) {
        return Boolean.parseBoolean(value(attributes, key));
    }

    private static int attributeInt(String attributes, String key) {
        try {
            return Integer.parseInt(value(attributes, key));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static double attributeDouble(String attributes, String key) {
        try {
            return Double.parseDouble(value(attributes, key));
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    private static <E extends Enum<E>> E attributeEnum(String attributes, String key, E fallback) {
        String raw = value(attributes, key);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
