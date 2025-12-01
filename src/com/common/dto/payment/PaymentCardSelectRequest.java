package com.common.dto.payment;

public class PaymentCardSelectRequest {
    private final int userId;
    private final String selectedCardType;

    public PaymentCardSelectRequest(int userId, String selectedCardType) {
        this.userId = userId;
        this.selectedCardType = selectedCardType;
    }

    public int getUserId() {
        return userId;
    }

    public String getSelectedCardType() {
        return selectedCardType;
    }

    @Override
    public String toString() {
        return "PaymentCardSelectRequest{userId=" + userId +
                ", selectedCardType='" + selectedCardType + "'}";
    }
}