package com.ewaste.server.application.facade;

import com.ewaste.server.api.dto.request.CreatePickupRequestDto;
import com.ewaste.server.api.dto.response.PickupResponseDto;
import com.ewaste.server.api.mapper.PickupMapper;
import com.ewaste.server.application.service.PickupService;
import com.ewaste.server.common.exception.BusinessRuleException;
import com.ewaste.server.common.validation.PickupValidator;
import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.pattern.observer.PickupEvent;
import com.ewaste.server.domain.pattern.observer.PickupEventPublisher;
import com.ewaste.server.domain.pattern.strategy.assignment.AssignmentStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.PriorityStrategy;
import com.ewaste.server.domain.repository.CollectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * High-level application facade coordinating validation, prioritization,
 * collector assignment, persistence, and event dispatching.
 */
@Component
public class PickupFacade {

    private static final Logger log = LoggerFactory.getLogger(PickupFacade.class);

    private final PickupValidator pickupValidator;
    private final PickupMapper pickupMapper;
    private final PickupService pickupService;
    private final CollectorRepository collectorRepository;
    private final PriorityStrategy priorityStrategy;
    private final AssignmentStrategy assignmentStrategy;
    private final PickupEventPublisher eventPublisher;

    public PickupFacade(PickupValidator pickupValidator,
                        PickupMapper pickupMapper,
                        PickupService pickupService,
                        CollectorRepository collectorRepository,
                        PriorityStrategy priorityStrategy,
                        AssignmentStrategy assignmentStrategy,
                        PickupEventPublisher eventPublisher) {
        this.pickupValidator = pickupValidator;
        this.pickupMapper = pickupMapper;
        this.pickupService = pickupService;
        this.collectorRepository = collectorRepository;
        this.priorityStrategy = priorityStrategy;
        this.assignmentStrategy = assignmentStrategy;
        this.eventPublisher = eventPublisher;
    }

    public PickupResponseDto createPickup(CreatePickupRequestDto requestDto) {
        log.info("Executing pickup creation workflow for user {}", requestDto.getUserId());

        pickupValidator.validateCreateRequest(requestDto);
        PickupRequest pickup = pickupMapper.toEntity(requestDto);

        if (requestDto.getItemIds() != null) {
            for (Long itemId : requestDto.getItemIds()) {
                pickup.getItems().add(new PickupItem(null, itemId));
            }
        }

        // 1. Calculate Priority Score via Strategy
        double priorityScore = priorityStrategy.calculatePriority(pickup);
        pickup.setPriorityScore(priorityScore);
        log.debug("Computed priority score: {}", priorityScore);

        // 2. New requests remain queued until an administrator assigns a collector.
        if (pickup.getStatus() == PickupStatus.SUBMITTED) {
            pickup.request();
        }
        log.info("Pickup queued in REQUESTED state for administrator assignment");

        // 3. Persist entity
        PickupRequest saved = pickupService.save(pickup);

        // 4. Publish Observer Event
        eventPublisher.publish(new PickupEvent(saved, saved.getStatus(), "Pickup created and queued."));

        return pickupMapper.toResponseDto(saved);
    }

    public void advanceState(Long pickupId, String action, Long paramId) {
        PickupRequest pickup = pickupService.findById(pickupId);
        PickupStatus oldStatus = pickup.getStatus();
        Long oldCollectorId = pickup.getCollectorId();

        switch (action.toUpperCase()) {
            case "ASSIGN" -> {
                if (paramId == null) {
                    throw new BusinessRuleException("Collector ID required for assignment.");
                }
                if (pickup.getStatus() == PickupStatus.SUBMITTED) {
                    pickup.request();
                }
                pickup.assign(paramId);
            }
            case "COLLECT", "COLLECTED" -> pickup.collect();
            case "DELIVER", "DELIVERED" -> {
                if (paramId == null) throw new BusinessRuleException("Recycling Center ID required for delivery.");
                pickup.deliver(paramId);
            }
            case "PROCESS", "PROCESSING" -> pickup.process();
            case "COMPLETE", "COMPLETED" -> pickup.complete();
            case "CANCEL" -> pickup.cancel();
            default -> throw new BusinessRuleException("Unknown state transition action: " + action);
        }

        updateCollectorWorkload(pickup, action.toUpperCase(), oldCollectorId);
        pickupService.update(pickup);
        eventPublisher.publish(new PickupEvent(pickup, oldStatus, "Action applied: " + action));
    }

    private void updateCollectorWorkload(PickupRequest pickup, String action, Long oldCollectorId) {
        if ("ASSIGN".equals(action) && pickup.getCollectorId() != null
                && !pickup.getCollectorId().equals(oldCollectorId)) {
            if (oldCollectorId != null) {
                adjustCollectorWorkload(oldCollectorId, -pickup.getTotalWeight());
            }
            adjustCollectorWorkload(pickup.getCollectorId(), pickup.getTotalWeight());
        } else if (("DELIVER".equals(action) || "CANCEL".equals(action)
                || "COMPLETE".equals(action)) && oldCollectorId != null) {
            adjustCollectorWorkload(oldCollectorId, -pickup.getTotalWeight());
        }
    }

    private void adjustCollectorWorkload(Long collectorId, double weightDelta) {
        Collector collector = collectorRepository.findById(collectorId)
                .orElseThrow(() -> new BusinessRuleException("Collector not found: " + collectorId));
        if (weightDelta >= 0) {
            collector.addWorkload(weightDelta);
        } else {
            collector.removeWorkload(-weightDelta);
        }
        collectorRepository.update(collector);
    }
}