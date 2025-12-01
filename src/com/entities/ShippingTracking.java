package com.entities;

public class ShippingTracking {

    private int orderId;
    private String status;
    private long updatedAt;

    public ShippingTracking(int orderId) {
        this.orderId = orderId;
        this.status = "REQUESTED";
        this.updatedAt = System.currentTimeMillis();
    }

    public int getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    private void update(String newStatus) {
        this.status = newStatus;
        this.updatedAt = System.currentTimeMillis();
    }

    public void markShipRequested() {
        update("REQUESTED");
    }

    public void markPickedUp() {
        update("PICKED_UP");
    }

    public void markInTransit() {
        update("IN_TRANSIT");
    }

    public void markOutForDelivery() {
        update("OUT_FOR_DELIVERY");
    }

    public void markDelivered() {
        update("DELIVERED");
    }

    @Override
    public String toString() {
        return "ShippingTracking{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}