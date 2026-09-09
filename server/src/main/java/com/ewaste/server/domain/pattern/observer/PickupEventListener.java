package com.ewaste.server.domain.pattern.observer;

/**
 * Subscriber interface for observing pickup state transitions and operational alerts.
 */
@FunctionalInterface
public interface PickupEventListener {

    /**
     * Invoked when a pickup lifecycle event is published.
     *
     * @param event the event payload containing transition metadata
     */
    void onPickupEvent(PickupEvent event);
}