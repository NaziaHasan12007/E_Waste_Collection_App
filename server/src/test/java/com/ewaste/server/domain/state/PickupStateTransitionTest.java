package com.ewaste.server.domain.state;

import com.ewaste.server.common.exception.InvalidPickupStateException;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.pattern.state.SubmittedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests lifecycle state transitions and verifies invalid state transitions are blocked.
 */
class PickupStateTransitionTest {

    private PickupRequest pickup;

    @BeforeEach
    void setUp() {
        pickup = new PickupRequest();
        pickup.setState(new SubmittedState());
    }

    @Test
    @DisplayName("Should execute valid lifecycle transitions from SUBMITTED through COMPLETED")
    void testValidLifecycleWorkflow() {
        assertEquals(PickupStatus.SUBMITTED, pickup.getStatus());

        pickup.request();
        assertEquals(PickupStatus.REQUESTED, pickup.getStatus());

        pickup.assign(101L);
        assertEquals(PickupStatus.ASSIGNED, pickup.getStatus());
        assertEquals(101L, pickup.getCollectorId());

        pickup.collect();
        assertEquals(PickupStatus.COLLECTED, pickup.getStatus());

        pickup.deliver(501L);
        assertEquals(PickupStatus.DELIVERED, pickup.getStatus());

        pickup.process();
        assertEquals(PickupStatus.PROCESSING, pickup.getStatus());

        pickup.complete();
        assertEquals(PickupStatus.COMPLETED, pickup.getStatus());
    }

    @Test
    @DisplayName("Should fail when attempting an illegal direct jump from SUBMITTED to COMPLETED")
    void testInvalidTransitionSubmittedToCompleted() {
        assertThrows(InvalidPickupStateException.class, () -> pickup.complete());
        assertEquals(PickupStatus.SUBMITTED, pickup.getStatus());
    }

    @Test
    @DisplayName("Should block delivery attempt before waste collection occurs")
    void testPrematureDeliveryBlocked() {
        pickup.request();
        pickup.assign(101L);
        assertThrows(InvalidPickupStateException.class, () -> pickup.deliver(501L));
        assertEquals(PickupStatus.ASSIGNED, pickup.getStatus());
    }

    @Test
    @DisplayName("Should allow cancellation prior to collection, but disallow actions thereafter")
    void testCancellationLifecycle() {
        pickup.request();
        pickup.cancel();
        assertEquals(PickupStatus.CANCELLED, pickup.getStatus());

        assertThrows(InvalidPickupStateException.class, () -> pickup.request());
        assertThrows(InvalidPickupStateException.class, () -> pickup.assign(102L));
    }
}