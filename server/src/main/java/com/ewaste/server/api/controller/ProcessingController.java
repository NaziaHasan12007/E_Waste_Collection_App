package com.ewaste.server.api.controller;

import com.ewaste.server.api.dto.request.InspectionOutcomeRequest;
import com.ewaste.server.api.dto.response.ProcessingOutcomeResponse;
import com.ewaste.server.api.mapper.ProcessingRecordMapper;
import com.ewaste.server.application.facade.ProcessingFacade;
import com.ewaste.server.application.service.ProcessingService;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.model.processing.RecyclingCenter;
import com.ewaste.server.domain.repository.RecyclingCenterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/processing")
@CrossOrigin(origins = "*")
public class ProcessingController {

    private final ProcessingFacade processingFacade;
    private final ProcessingService processingService;
    private final ProcessingRecordMapper mapper;
    private final RecyclingCenterRepository centerRepository;

    public ProcessingController(ProcessingFacade processingFacade,
                                ProcessingService processingService,
                                ProcessingRecordMapper mapper,
                                RecyclingCenterRepository centerRepository) {
        this.processingFacade = processingFacade;
        this.processingService = processingService;
        this.mapper = mapper;
        this.centerRepository = centerRepository;
    }

    @PostMapping("/{pickupId}/process")
    public ResponseEntity<ProcessingOutcomeResponse> processItem(@PathVariable Long pickupId,
                                                                 @RequestBody InspectionOutcomeRequest request) {
        ProcessingOutcomeResponse response = processingFacade.processItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/records/{pickupId}")
    public ResponseEntity<List<ProcessingOutcomeResponse>> getProcessingRecords(@PathVariable Long pickupId) {
        List<ProcessingRecord> records = processingService.findAll().stream()
                .filter(r -> pickupId.equals(r.getPickupId()))
                .collect(Collectors.toList());

        List<ProcessingOutcomeResponse> response = records.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/centers")
    public ResponseEntity<List<RecyclingCenter>> getRecyclingCenters() {
        return ResponseEntity.ok(centerRepository.findAll());
    }
}