package com.common.dto.payment;

public class PaymentReceiptRequest {
    private final int transactionId;
    private final int userId;
    private final String email;

    public PaymentReceiptRequest(int transactionId, int userId, String email) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.email = email;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "PaymentReceiptRequest{transactionId=" + transactionId +
                ", userId=" + userId +
                ", email='" + email + "'}";
    }
}