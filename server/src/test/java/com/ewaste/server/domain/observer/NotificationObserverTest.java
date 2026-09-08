package com.ewaste.server.domain.observer;

import com.ewaste.server.application.service.NotificationService;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;
import com.ewaste.server.domain.pattern.observer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Validates subscriber registration and decoupled notification broadcasts via Mockito.
 */
class NotificationObserverTest {

    private NotificationService notificationService;
    private PickupEventPublisher publisher;
    private CustomerNotificationObserver customerObserver;
    private CollectorNotificationObserver collectorObserver;

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        publisher = new PickupEventPublisher();

        customerObserver = new CustomerNotificationObserver(notificationService);
        collectorObserver = new CollectorNotificationObserver(notificationService);

        publisher.subscribe(customerObserver);
        publisher.subscribe(collectorObserver);
    }

    @Test
    @DisplayName("Should maintain accurate subscriber count upon subscribe and unsubscribe")
    void testObserverSubscriptionLifecycle() {
        assertEquals(2, publisher.getListenerCount());

        publisher.unsubscribe(collectorObserver);
        assertEquals(1, publisher.getListenerCount());
    }

    @Test
    @DisplayName("Should notify relevant observers when pickup assignment event is dispatched")
    void testPickupAssignedEventNotification() {
        PickupRequest pickup = new PickupRequest();
        pickup.setPickupId(10L);
        pickup.setUserId(99L);
        pickup.setCollectorId(77L);
        pickup.setStatus(PickupStatus.ASSIGNED);

        PickupEvent event = new PickupEvent(pickup, PickupStatus.REQUESTED, "Collector Assigned");
        publisher.publish(event);

        // Customer observer alerted
        verify(notificationService, times(1))
                .createNotification(eq(99L), eq("CUSTOMER"), anyString(), anyString());

        // Collector observer alerted
        verify(notificationService, times(1))
                .createNotification(eq(77L), eq("COLLECTOR"), anyString(), anyString());
    }
}