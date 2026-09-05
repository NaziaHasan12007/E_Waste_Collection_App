package com.ewaste.server.domain.pattern.observer;

import com.ewaste.server.application.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Alerts administrators to critical workflow exceptions, toxic hazardous arrivals, and cancellations.
 */
@Component
public class AdminNotificationObserver implements PickupEventListener {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationObserver.class);
    private final NotificationService notificationService;

    public AdminNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onPickupEvent(PickupEvent event) {
        if (event == null) {
            return;
        }

        // Admins are notified about critical events: cancellations, hazardous handovers, and delivered items awaiting inspection
        switch (event.getCurrentStatus()) {
            case DELIVERED -> {
                String title = "Facility Inbound: Pickup #" + event.getPickupId();
                String message = "Cargo for pickup #" + event.getPickupId() + " has arrived at the facility and is pending inspection.";
                broadcastAdminAlert(title, message);
            }
            case CANCELLED -> {
                String title = "Pickup Aborted: #" + event.getPickupId();
                String message = "Pickup #" + event.getPickupId() + " was cancelled. Reason: " + event.getDescription();
                broadcastAdminAlert(title, message);
            }
            default -> {
                // Non-critical lifecycle steps are tracked on dashboards rather than direct admin alerts
            }
        }
    }

    private void broadcastAdminAlert(String title, String message) {
        log.info("System Admin Alert - {}: {}", title, message);
        notificationService.createNotification(1L, "ADMIN", title, message);
    }
}