package com.services;

import com.broker.*;
import com.entities.ShippingTracking;
import com.repository.ShippingTrackingRepository;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ShippingService {

    private static final Logger LOGGER = Logger.getLogger(ShippingService.class.getName());

    private final AsyncMessageBroker broker;
    private final ShippingTrackingRepository repo;

    public ShippingService(AsyncMessageBroker broker, ShippingTrackingRepository repo) {
        this.broker = broker;
        this.repo = repo;
    }

    public void registerHandlers() {

        // ===== SHIPPING_REQUESTED =====
        broker.registerListener(EventType.SHIPPING_REQUESTED, msg -> CompletableFuture.runAsync(() -> {
            int orderId = (int) msg.getPayload();
            LOGGER.info("[Shipping] SHIPPING_REQUESTED → order " + orderId);

            ShippingTracking t = new ShippingTracking(orderId);
            t.markShipRequested();
            repo.save(t);

            broker.publish(EventType.SHIPPING_STATUS_UPDATED, t);
        }));

        // ===== SHIPPING_PICKED_UP =====
        broker.registerListener(EventType.SHIPPING_PICKED_UP, msg -> CompletableFuture.runAsync(() -> {
            int orderId = (int) msg.getPayload();
            ShippingTracking t = repo.find(orderId);
            t.markPickedUp();
            repo.update(t);

            broker.publish(EventType.SHIPPING_STATUS_UPDATED, t);
        }));

        // ===== SHIPPING_IN_TRANSIT =====
        broker.registerListener(EventType.SHIPPING_IN_TRANSIT, msg -> CompletableFuture.runAsync(() -> {
            int orderId = (int) msg.getPayload();
            ShippingTracking t = repo.find(orderId);
            t.markInTransit();
            repo.update(t);

            broker.publish(EventType.SHIPPING_STATUS_UPDATED, t);
        }));

        // ===== SHIPPING_OUT_FOR_DELIVERY =====
        broker.registerListener(EventType.SHIPPING_OUT_FOR_DELIVERY, msg -> CompletableFuture.runAsync(() -> {
            int orderId = (int) msg.getPayload();
            ShippingTracking t = repo.find(orderId);
            t.markOutForDelivery();
            repo.update(t);

            broker.publish(EventType.SHIPPING_STATUS_UPDATED, t);
        }));

        // ===== SHIPPING_DELIVERED =====
        broker.registerListener(EventType.SHIPPING_DELIVERED, msg -> CompletableFuture.runAsync(() -> {
            int orderId = (int) msg.getPayload();
            ShippingTracking t = repo.find(orderId);
            t.markDelivered();
            repo.update(t);

            broker.publish(EventType.SHIPPING_STATUS_UPDATED, t);
        }));
    }
}