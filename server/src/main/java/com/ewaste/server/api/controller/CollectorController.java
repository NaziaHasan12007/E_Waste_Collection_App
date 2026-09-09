package com.ewaste.server.api.controller;

import com.ewaste.server.api.dto.response.CollectorSummaryResponse;
import com.ewaste.server.application.service.CollectorService;
import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/collectors")
@CrossOrigin(origins = "*")
public class CollectorController {

    private final CollectorService collectorService;
    private final UserRepository userRepository;

    public CollectorController(CollectorService collectorService, UserRepository userRepository) {
        this.collectorService = collectorService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CollectorSummaryResponse>> getAllCollectors() {
        List<CollectorSummaryResponse> response = collectorService.findAll().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<CollectorSummaryResponse>> getAvailableCollectors() {
        List<CollectorSummaryResponse> response = collectorService.findAvailable().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{collectorId}")
    public ResponseEntity<CollectorSummaryResponse> getCollectorById(@PathVariable Long collectorId) {
        Collector collector = collectorService.findById(collectorId);
        return ResponseEntity.ok(mapToSummaryResponse(collector));
    }

    @PatchMapping("/{collectorId}/availability")
    public ResponseEntity<CollectorSummaryResponse> updateAvailability(@PathVariable Long collectorId,
                                                                       @RequestBody Map<String, Boolean> body) {
        boolean isAvailable = body.getOrDefault("available", true);
        collectorService.setAvailability(collectorId, isAvailable);
        Collector updated = collectorService.findById(collectorId);
        return ResponseEntity.ok(mapToSummaryResponse(updated));
    }

    private CollectorSummaryResponse mapToSummaryResponse(Collector collector) {
        CollectorSummaryResponse dto = new CollectorSummaryResponse();
        dto.setCollectorId(collector.getCollectorId());
        dto.setUserId(collector.getUserId());
        userRepository.findById(collector.getUserId()).ifPresent(user -> {
            dto.setName(user.getFullName());
            dto.setEmail(user.getEmail());
        });
        dto.setArea(collector.getArea());
        dto.setVehicleType(collector.getVehicleType() != null ? collector.getVehicleType().name() : null);
        dto.setIsAvailable(collector.isAvailable());
        dto.setCurrentWorkloadKg(collector.getCurrentWorkloadKg());
        dto.setMaxCapacityKg(collector.getMaxCapacityKg());
        return dto;
    }
}