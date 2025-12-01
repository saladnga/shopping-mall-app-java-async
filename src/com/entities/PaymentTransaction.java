package com.entities;

import java.time.LocalDateTime;

public class PaymentTransaction {

    public enum Status {
        PENDING,
        AUTHORIZED,
        COMPLETED,
        FAILED
    }

    private int id;
    private int userId;
    private int orderId;
    private double amount;
    private long timestamp;
    private Status status;
    private String paymentMethod;

    // Email receipt fields
    private boolean receiptSent;
    private LocalDateTime receiptTimestamp;

    // Required for EmailService
    private String userEmail;

    public PaymentTransaction() {
    }

    public PaymentTransaction(int id, int userId, int orderId,
            double amount, long timestamp,
            String paymentMethod, Status status) {

        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    /** GETTERS */
    public int getId() {
        return id;
    }

    public int getTransactionId() {
        return id;
    } // EmailService required

    public int getUserId() {
        return userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isReceiptSent() {
        return receiptSent;
    }

    public LocalDateTime getReceiptTimestamp() {
        return receiptTimestamp;
    }

    public String getUserEmail() {
        return userEmail;
    } // EmailService required

    /** SETTERS */
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setReceiptSent(boolean sent) {
        this.receiptSent = sent;
    }

    public void setReceiptTimestamp(LocalDateTime ts) {
        this.receiptTimestamp = ts;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", receiptSent=" + receiptSent +
                '}';
    }
}