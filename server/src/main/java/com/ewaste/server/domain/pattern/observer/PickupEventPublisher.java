package com.ewaste.server.domain.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject/Publisher maintaining active observer subscriptions and broadcasting
 * lifecycle events across stakeholders.
 */
@Component
public class PickupEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PickupEventPublisher.class);
    private final List<PickupEventListener> listeners = new CopyOnWriteArrayList<>();

    public PickupEventPublisher() {}

    public PickupEventPublisher(List<PickupEventListener> initialListeners) {
        if (initialListeners != null) {
            this.listeners.addAll(initialListeners);
        }
    }

    public void subscribe(PickupEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.debug("Subscribed listener: {}", listener.getClass().getSimpleName());
        }
    }

    public void unsubscribe(PickupEventListener listener) {
        listeners.remove(listener);
        log.debug("Unsubscribed listener: {}", listener.getClass().getSimpleName());
    }

    public void publish(PickupEvent event) {
        log.info("Publishing pickup event for Pickup #{} [Status: {}]", event.getPickupId(), event.getCurrentStatus());
        for (PickupEventListener listener : listeners) {
            try {
                listener.onPickupEvent(event);
            } catch (Exception ex) {
                log.error("Error dispatching event to listener {}: {}", listener.getClass().getSimpleName(), ex.getMessage());
            }
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }
}