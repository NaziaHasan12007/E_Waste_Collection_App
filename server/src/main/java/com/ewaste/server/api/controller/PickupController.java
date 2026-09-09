package com.ewaste.server.api.controller;

import com.ewaste.server.api.dto.request.AssignCollectorRequest;
import com.ewaste.server.api.dto.request.CreatePickupRequestDto;
import com.ewaste.server.api.dto.response.PickupResponseDto;
import com.ewaste.server.api.mapper.PickupMapper;
import com.ewaste.server.application.facade.PickupFacade;
import com.ewaste.server.application.service.PickupService;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pickups")
@CrossOrigin(origins = "*")
public class PickupController {

    private final PickupFacade pickupFacade;
    private final PickupService pickupService;
    private final PickupMapper pickupMapper;

    public PickupController(PickupFacade pickupFacade,
                            PickupService pickupService,
                            PickupMapper pickupMapper) {
        this.pickupFacade = pickupFacade;
        this.pickupService = pickupService;
        this.pickupMapper = pickupMapper;
    }

    @PostMapping
    public ResponseEntity<PickupResponseDto> createPickup(@RequestBody CreatePickupRequestDto request) {
        PickupResponseDto response = pickupFacade.createPickup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{pickupId}")
    public ResponseEntity<PickupResponseDto> getPickupById(@PathVariable Long pickupId) {
        PickupRequest pickup = pickupService.findById(pickupId);
        return ResponseEntity.ok(pickupMapper.toResponseDto(pickup));
    }

    @GetMapping
    public ResponseEntity<List<PickupResponseDto>> getAllPickups(@RequestParam(required = false) String state) {
        List<PickupRequest> pickups;
        if (state != null && !state.isBlank()) {
            PickupStatus status = PickupStatus.valueOf(state.toUpperCase());
            pickups = pickupService.findByStatus(status);
        } else {
            pickups = pickupService.findAll();
        }

        List<PickupResponseDto> response = pickups.stream()
                .map(pickupMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PickupResponseDto>> getPickupsByCustomer(@PathVariable Long customerId) {
        List<PickupResponseDto> response = pickupService.findByUserId(customerId).stream()
                .map(pickupMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<List<PickupResponseDto>> getPickupsByCollector(@PathVariable Long collectorId) {
        List<PickupResponseDto> response = pickupService.findByCollectorId(collectorId).stream()
                .map(pickupMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{pickupId}/assign")
    public ResponseEntity<PickupResponseDto> assignCollector(@PathVariable Long pickupId,
                                                             @RequestBody AssignCollectorRequest request) {
        pickupFacade.advanceState(pickupId, "ASSIGN", request.getCollectorId());
        PickupRequest updated = pickupService.findById(pickupId);
        return ResponseEntity.ok(pickupMapper.toResponseDto(updated));
    }

    @PatchMapping("/{pickupId}/state")
    public ResponseEntity<PickupResponseDto> updatePickupState(@PathVariable Long pickupId,
                                                               @RequestParam String newState,
                                                               @RequestParam(required = false) Long centerId) {
        pickupFacade.advanceState(pickupId, newState, centerId);
        PickupRequest updated = pickupService.findById(pickupId);
        return ResponseEntity.ok(pickupMapper.toResponseDto(updated));
    }
}