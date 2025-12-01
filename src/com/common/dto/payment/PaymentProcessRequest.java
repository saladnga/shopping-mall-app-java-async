package com.common.dto.payment;

public class PaymentProcessRequest {
    private final int orderId;
    private final int userId;
    private final double amount;

    public PaymentProcessRequest(int orderId, int userId, double amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "PaymentProcessRequest{orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount + '}';
    }
}