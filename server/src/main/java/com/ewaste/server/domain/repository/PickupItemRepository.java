package com.ewaste.server.domain.repository;
import com.ewaste.server.domain.model.PickupItem;
import java.util.List;
import java.util.Optional;

public interface PickupItemRepository {
    PickupItem save(PickupItem pickupItem);
    Optional<PickupItem> findById(Long pickupItemId);
    List<PickupItem> findByPickupId(Long pickupId);
    List<PickupItem> findByItemId(Long itemId);
    void deleteById(Long pickupItemId);
    void deleteByPickupId(Long pickupId);
}