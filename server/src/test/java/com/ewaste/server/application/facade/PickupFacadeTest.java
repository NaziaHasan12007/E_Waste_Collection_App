package com.ewaste.server.application.facade;

import com.ewaste.server.api.dto.request.CreatePickupRequestDto;
import com.ewaste.server.api.dto.response.PickupResponseDto;
import com.ewaste.server.api.mapper.PickupMapper;
import com.ewaste.server.application.service.PickupService;
import com.ewaste.server.common.validation.PickupValidator;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.pattern.observer.PickupEvent;
import com.ewaste.server.domain.pattern.observer.PickupEventPublisher;
import com.ewaste.server.domain.pattern.strategy.assignment.AssignmentStrategy;
import com.ewaste.server.domain.pattern.strategy.priority.PriorityStrategy;
import com.ewaste.server.domain.repository.CollectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying end-to-end orchestration in PickupFacade using Mockito.
 */
class PickupFacadeTest {

    private PickupValidator pickupValidator;
    private PickupMapper pickupMapper;
    private PickupService pickupService;
    private CollectorRepository collectorRepository;
    private PriorityStrategy priorityStrategy;
    private AssignmentStrategy assignmentStrategy;
    private PickupEventPublisher eventPublisher;

    private PickupFacade pickupFacade;

    @BeforeEach
    void setUp() {
        pickupValidator = Mockito.mock(PickupValidator.class);
        pickupMapper = Mockito.mock(PickupMapper.class);
        pickupService = Mockito.mock(PickupService.class);
        collectorRepository = Mockito.mock(CollectorRepository.class);
        priorityStrategy = Mockito.mock(PriorityStrategy.class);
        assignmentStrategy = Mockito.mock(AssignmentStrategy.class);
        eventPublisher = Mockito.mock(PickupEventPublisher.class);

        pickupFacade = new PickupFacade(
                pickupValidator,
                pickupMapper,
                pickupService,
                collectorRepository,
                priorityStrategy,
                assignmentStrategy,
                eventPublisher
        );
    }

    @Test
    @DisplayName("Should validate, prioritize, persist, and queue new pickup in REQUESTED state")
    void testCreatePickupWithCollectorAssigned() {
        CreatePickupRequestDto requestDto = new CreatePickupRequestDto();
        requestDto.setUserId(1L);
        requestDto.setItemIds(List.of(10L, 20L));

        PickupRequest entity = new PickupRequest();
        entity.setUserId(1L);

        PickupResponseDto responseDto = new PickupResponseDto();
        responseDto.setPickupId(100L);
        responseDto.setStatus("REQUESTED");

        when(pickupMapper.toEntity(requestDto)).thenReturn(entity);
        when(priorityStrategy.calculatePriority(entity)).thenReturn(75.5);
        when(pickupService.save(entity)).thenReturn(entity);
        when(pickupMapper.toResponseDto(entity)).thenReturn(responseDto);

        PickupResponseDto result = pickupFacade.createPickup(requestDto);

        assertNotNull(result);
        assertEquals("REQUESTED", result.getStatus());

        verify(pickupValidator).validateCreateRequest(requestDto);
        verify(priorityStrategy).calculatePriority(entity);
        verify(collectorRepository, never()).update(any());
        verify(assignmentStrategy, never()).selectCollector(any(), anyList());
        verify(pickupService).save(entity);
        verify(eventPublisher).publish(any(PickupEvent.class));
    }

    @Test
    @DisplayName("Should queue pickup in REQUESTED status if no collector matches criteria")
    void testCreatePickupWithoutCollectorQueuesRequested() {
        CreatePickupRequestDto requestDto = new CreatePickupRequestDto();
        requestDto.setUserId(1L);

        PickupRequest entity = new PickupRequest();
        entity.setUserId(1L);

        PickupResponseDto responseDto = new PickupResponseDto();
        responseDto.setStatus("REQUESTED");

        when(pickupMapper.toEntity(requestDto)).thenReturn(entity);
        when(priorityStrategy.calculatePriority(entity)).thenReturn(30.0);
        when(pickupService.save(entity)).thenReturn(entity);
        when(pickupMapper.toResponseDto(entity)).thenReturn(responseDto);

        PickupResponseDto result = pickupFacade.createPickup(requestDto);

        assertNotNull(result);
        assertEquals("REQUESTED", result.getStatus());
        verify(collectorRepository, never()).update(any());
        verify(assignmentStrategy, never()).selectCollector(any(), anyList());
        verify(pickupService).save(entity);
        verify(eventPublisher).publish(any(PickupEvent.class));
    }
}