package com.entities;

/**
 * Represents a user's payment card.
 * Only masked card numbers should be stored to comply with PCI-DSS standards.
 */
public class PaymentCard {

    private int id;
    private int userId;
    private String cardType; // VISA, MasterCard, ...
    private String maskedCardNumber; // **** **** **** 1234
    private String expiryDate; // MM/YY
    private String cardHolderName;
    private boolean isDefault;

    public PaymentCard() {
    }

    public PaymentCard(int id, int userId, String cardType,
            String maskedCardNumber, String expiryDate,
            String cardHolderName, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.cardType = cardType;
        this.maskedCardNumber = maskedCardNumber;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
        this.isDefault = isDefault;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getCardType() {
        return cardType;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    /** Returns last 4 digits for display/logging */
    public String getLast4Digits() {
        if (maskedCardNumber == null || maskedCardNumber.length() < 4)
            return "";
        return maskedCardNumber.substring(maskedCardNumber.length() - 4);
    }

    @Override
    public String toString() {
        return "PaymentCard{" +
                "id=" + id +
                ", userId=" + userId +
                ", cardType='" + cardType + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", maskedCardNumber='" + maskedCardNumber + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}