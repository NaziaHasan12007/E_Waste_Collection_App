package com.ewaste.server.api.mapper;

import com.ewaste.server.api.dto.request.CreatePickupRequestDto;
import com.ewaste.server.api.dto.response.PickupResponseDto;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.pattern.state.SubmittedState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PickupMapper {

    public PickupRequest toEntity(CreatePickupRequestDto dto) {
        if (dto == null) {
            return null;
        }

        PickupRequest entity = new PickupRequest();
        entity.setUserId(dto.getUserId());
        entity.setAddress(dto.getAddress());
        entity.setPreferredDate(dto.getPreferredDate());
        entity.setPreferredTime(dto.getPreferredTime());
        entity.setState(new SubmittedState());
        entity.setPriorityScore(0.0);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public PickupResponseDto toResponseDto(PickupRequest entity) {
        if (entity == null) {
            return null;
        }

        PickupResponseDto dto = new PickupResponseDto();
        dto.setPickupId(entity.getPickupId());
        dto.setUserId(entity.getUserId());
        dto.setCollectorId(entity.getCollectorId());
        dto.setAddress(entity.getAddress());
        dto.setPreferredDate(entity.getPreferredDate());
        dto.setPreferredTime(entity.getPreferredTime());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setPriorityScore(entity.getPriorityScore());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);

        List<Long> itemIds = new ArrayList<>();
        if (entity.getItems() != null) {
            itemIds = entity.getItems().stream()
                    .filter(pi -> pi != null && pi.getItemId() != null)
                    .map(PickupItem::getItemId)
                    .collect(Collectors.toList());
        }
        dto.setItemIds(itemIds);

        return dto;
    }
}