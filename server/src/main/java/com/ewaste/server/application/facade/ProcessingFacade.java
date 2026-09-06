package com.ewaste.server.application.facade;

import com.ewaste.server.api.dto.request.InspectionOutcomeRequest;
import com.ewaste.server.api.dto.response.ProcessingOutcomeResponse;
import com.ewaste.server.api.mapper.ProcessingRecordMapper;
import com.ewaste.server.application.service.ProcessingService;
import com.ewaste.server.application.service.RewardService;
import com.ewaste.server.common.exception.BusinessRuleException;
import com.ewaste.server.common.exception.ResourceNotFoundException;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.model.processing.ProcessingResult;
import com.ewaste.server.domain.model.processing.RecyclingCenter;
import com.ewaste.server.domain.pattern.template.*;
import com.ewaste.server.domain.repository.EWasteItemRepository;
import com.ewaste.server.domain.repository.RecyclingCenterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Coordinates facility inspection, Template Method workflow execution,
 * reward accrual, and facility capacity adjustments.
 */
@Component
public class ProcessingFacade {

    private static final Logger log = LoggerFactory.getLogger(ProcessingFacade.class);

    private final ProcessingService processingService;
    private final RewardService rewardService;
    private final RecyclingCenterRepository centerRepository;
    private final EWasteItemRepository itemRepository;
    private final ProcessingRecordMapper mapper;

    public ProcessingFacade(ProcessingService processingService,
                            RewardService rewardService,
                            RecyclingCenterRepository centerRepository,
                            EWasteItemRepository itemRepository,
                            ProcessingRecordMapper mapper) {
        this.processingService = processingService;
        this.rewardService = rewardService;
        this.centerRepository = centerRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public ProcessingOutcomeResponse processItem(InspectionOutcomeRequest request) {
        log.info("Processing inspection outcome for item {}", request.getItemId());

        RecyclingCenter center = centerRepository.findById(request.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("RecyclingCenter", request.getCenterId()));

        EWasteItem item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("EWasteItem", request.getItemId()));

        ProcessingResult result;
        try {
            result = ProcessingResult.valueOf(request.getProcessingResult().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid processing result: " + request.getProcessingResult());
        }

        // 1. Resolve workflow via Template Method Pattern
        AbstractProcessingTemplate workflow = resolveWorkflow(result);
        workflow.executeProcessing(item, center);

        // 2. Persist outcome
        ProcessingRecord record = new ProcessingRecord();
        record.setItemId(item.getItemId());
        record.setCenterId(center.getCenterId());
        record.setInspectionNotes(request.getInspectionNotes());
        record.setProcessingResult(result);
        record.setProcessedAt(LocalDateTime.now());

        // 3. Calculate and apply reward points
        int points = calculatePoints(item, result);
        record.setPointsAwarded(points);
        if (points > 0 && item.getUserId() != null) {
            rewardService.awardPoints(item.getUserId(), null, points, "Recycling: " + result.name());
        }

        ProcessingRecord saved = processingService.saveRecord(record);

        // 4. Update recycling center capacity load
        center.setCurrentUtilizationKg(center.getCurrentUtilizationKg() + item.getWeightKg());
        centerRepository.update(center);

        return mapper.toResponseDto(saved);
    }

    private AbstractProcessingTemplate resolveWorkflow(ProcessingResult result) {
        return switch (result) {
            case RECYCLE -> new RecyclingWorkflow();
            case REFURBISH -> new RefurbishWorkflow();
            case REPAIR -> new RepairWorkflow();
            case HAZARDOUS_DISPOSAL -> new HazardousDisposalWorkflow();
            default -> new RecyclingWorkflow();
        };
    }

    private int calculatePoints(EWasteItem item, ProcessingResult result) {
        int base = (int) Math.round(item.getWeightKg() * 10);
        return switch (result) {
            case REFURBISH -> base * 3;
            case REPAIR -> base * 2;
            case RECYCLE -> base;
            case HAZARDOUS_DISPOSAL -> 5;
            default -> 0;
        };
    }
}