package com.common.dto.account;

public class AccountEditRequest {

    private final int userId;

    private final String newUsername;
    private final String newEmail;
    private final String newPhone;
    private final String newAddress;
    private final String newPassword; // optional – only used if user updates password

    public AccountEditRequest(
            int userId,
            String newUsername,
            String newEmail,
            String newPhone,
            String newAddress,
            String newPassword) {

        this.userId = userId;
        this.newUsername = newUsername;
        this.newEmail = newEmail;
        this.newPhone = newPhone;
        this.newAddress = newAddress;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "AccountEditRequest{" +
                "userId=" + userId +
                ", newUsername='" + newUsername + '\'' +
                ", newEmail='" + newEmail + '\'' +
                ", newPhone='" + newPhone + '\'' +
                ", newAddress='" + newAddress + '\'' +
                ", newPassword=" + (newPassword != null ? "***" : null) +
                '}';
    }
}