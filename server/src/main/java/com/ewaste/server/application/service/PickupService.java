package com.ewaste.server.application.service;

import com.ewaste.server.common.exception.ResourceNotFoundException;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.repository.PickupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupService {

    private final PickupRepository pickupRepository;

    public PickupService(PickupRepository pickupRepository) {
        this.pickupRepository = pickupRepository;
    }

    public PickupRequest save(PickupRequest pickup) {
        return pickupRepository.save(pickup);
    }

    public PickupRequest update(PickupRequest pickup) {
        return pickupRepository.update(pickup);
    }

    public PickupRequest findById(Long pickupId) {
        return pickupRepository.findById(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", pickupId));
    }

    public List<PickupRequest> findAll() {
        return pickupRepository.findAll();
    }

    public List<PickupRequest> findByUserId(Long userId) {
        return pickupRepository.findByUserId(userId);
    }

    public List<PickupRequest> findByCollectorId(Long collectorId) {
        return pickupRepository.findByCollectorId(collectorId);
    }

    public List<PickupRequest> findByStatus(PickupStatus status) {
        return pickupRepository.findByStatus(status);
    }
}