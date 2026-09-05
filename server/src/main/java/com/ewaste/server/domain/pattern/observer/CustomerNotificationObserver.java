package com.ewaste.server.domain.pattern.observer;

import com.ewaste.server.application.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Generates user-facing notifications for pickup progress, assignments, and completion rewards.
 */
@Component
public class CustomerNotificationObserver implements PickupEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationObserver.class);
    private final NotificationService notificationService;

    public CustomerNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onPickupEvent(PickupEvent event) {
        if (event == null || event.getCustomerId() == null) {
            return;
        }

        String title = "Pickup Update: #" + event.getPickupId();
        String message;

        switch (event.getCurrentStatus()) {
            case REQUESTED -> message = "Your pickup request #" + event.getPickupId() + " has been validated and prioritized for collection.";
            case ASSIGNED -> message = "A certified collector has been dispatched for pickup #" + event.getPickupId() + ".";
            case COLLECTED -> message = "Your electronic waste items have been retrieved by the collection personnel.";
            case DELIVERED -> message = "Your items have safely arrived at the designated recycling and inspection center.";
            case PROCESSING -> message = "Technical inspection and material categorization have commenced for your items.";
            case COMPLETED -> message = "Processing complete! Recycling yields have been finalized and reward points credited.";
            case CANCELLED -> message = "Pickup #" + event.getPickupId() + " has been cancelled: " + event.getDescription();
            default -> message = "Status changed to " + event.getCurrentStatus() + " for pickup #" + event.getPickupId();
        }

        log.debug("Dispatching customer notification to user ID {}", event.getCustomerId());
        notificationService.createNotification(event.getCustomerId(), "CUSTOMER", title, message);
    }
}