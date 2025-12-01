package com.subsystems;

import com.broker.*;
import com.entities.Order;
import com.entities.Order.OrderStatus;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OrderManagement implements Subsystems {

    private AsyncMessageBroker broker;

    private final Listener handleOrderCreate = this::onOrderCreate;
    private final Listener handleOrderCancel = this::onCancel;
    private final Listener handleAuthSuccess = this::onPaymentAuthorized;
    private final Listener handlePurchase = this::onPurchase;
    private final Listener handleHistory = this::onHistoryRequest;

    @Override
    public void init(AsyncMessageBroker broker) {
        this.broker = broker;

        broker.registerListener(EventType.ORDER_CREATED_REQUESTED, handleOrderCreate);
        broker.registerListener(EventType.ORDER_CANCEL_REQUESTED, handleOrderCancel);
        broker.registerListener(EventType.PAYMENT_AUTHORIZED, handleAuthSuccess);
        broker.registerListener(EventType.PURCHASE_REQUESTED, handlePurchase);
        broker.registerListener(EventType.ORDER_HISTORY_REQUESTED, handleHistory);

        System.out.println("[OrderManagement] Initialized.");
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        broker.unregisterListener(EventType.ORDER_CREATED_REQUESTED, handleOrderCreate);
        broker.unregisterListener(EventType.ORDER_CANCEL_REQUESTED, handleOrderCancel);
        broker.unregisterListener(EventType.PAYMENT_AUTHORIZED, handleAuthSuccess);
        broker.unregisterListener(EventType.PURCHASE_REQUESTED, handlePurchase);
        broker.unregisterListener(EventType.ORDER_HISTORY_REQUESTED, handleHistory);

        System.out.println("[OrderManagement] Shutdown complete.");
    }

    // ============================================================
    // ORDER_CREATED_REQUESTED
    // ============================================================
    private CompletableFuture<Void> onOrderCreate(Message m) {

        return CompletableFuture.runAsync(() -> {
            Order order = (Order) m.getPayload();

            System.out.println("[OrderManagement] Creating order " + order.getId());

            // publish correctly with broker.publish(...)
            broker.publish(EventType.PURCHASE_REQUESTED, order);

            System.out.println("[OrderManagement] Order created.");
        });
    }

    // ============================================================
    // PURCHASE_REQUESTED
    // ============================================================
    private CompletableFuture<Void> onPurchase(Message m) {

        return CompletableFuture.runAsync(() -> {
            Order order = (Order) m.getPayload();

            System.out.println("[OrderManagement] Purchase started for order " + order.getId());

            broker.publish(EventType.PAYMENT_AUTHORIZATION_REQUESTED, order);
        });
    }

    // ============================================================
    // PAYMENT_AUTHORIZED
    // ============================================================
    private CompletableFuture<Void> onPaymentAuthorized(Message m) {

        return CompletableFuture.runAsync(() -> {
            Order order = (Order) m.getPayload();

            // FIX: OrderStatus.PLACE does NOT exist → use PLACED
            order.setStatus(OrderStatus.PLACED);

            System.out.println("[OrderManagement] Payment authorized – confirming order...");

            broker.publish(EventType.ORDER_CONFIRMED, order);
            broker.publish(EventType.EMAIL_RECEIPT_REQUESTED, order);
            broker.publish(EventType.SHIPPING_REQUESTED, order.getId());
        });
    }

    // ============================================================
    // ORDER_CANCEL_REQUESTED
    // ============================================================
    private CompletableFuture<Void> onCancel(Message m) {

        return CompletableFuture.runAsync(() -> {
            Order order = (Order) m.getPayload();

            System.out.println("[OrderManagement] Cancelling order " + order.getId());

            broker.publish(EventType.REFUND_PROCESS_REQUESTED, order);
            broker.publish(EventType.ORDER_CANCEL_SUCCESS, order);
        });
    }

    // ============================================================
    // ORDER_HISTORY_REQUESTED
    // ============================================================
    private CompletableFuture<Void> onHistoryRequest(Message message) {

        return CompletableFuture.runAsync(() -> {

            List<Order> data = Arrays.asList(
                    new Order(1, 1, System.currentTimeMillis(), OrderStatus.DELIVERED, 89.5, "A1 Street"),
                    new Order(2, 1, System.currentTimeMillis(), OrderStatus.PLACED, 129.0, "A1 Street"));

            broker.publish(EventType.ORDER_HISTORY_RETURNED, data);

            System.out.println("[OrderManagement] Order history returned.");
        });
    }
}