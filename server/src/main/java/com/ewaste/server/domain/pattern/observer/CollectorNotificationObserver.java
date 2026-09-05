package com.ewaste.server.domain.pattern.observer;

import com.ewaste.server.application.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dispatches operational task assignments, route updates, and route cancellations to collectors.
 */
@Component
public class CollectorNotificationObserver implements PickupEventListener {

    private static final Logger log = LoggerFactory.getLogger(CollectorNotificationObserver.class);
    private final NotificationService notificationService;

    public CollectorNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onPickupEvent(PickupEvent event) {
        if (event == null || event.getCollectorId() == null) {
            return;
        }

        String title = "Collector Route Alert: Task #" + event.getPickupId();
        String message;

        switch (event.getCurrentStatus()) {
            case ASSIGNED -> message = "New assignment: Pickup #" + event.getPickupId() + " added to your collection queue.";
            case CANCELLED -> message = "Task cancelled: Pickup #" + event.getPickupId() + " has been removed from your active route.";
            default -> {
                return; // Collectors only need task dispatch and route cancellation alerts
            }
        }

        log.debug("Dispatching collector alert to collector ID {}", event.getCollectorId());
        notificationService.createNotification(event.getCollectorId(), "COLLECTOR", title, message);
    }
}