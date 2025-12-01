package com.common.dto.account;

public class AccountViewRequest {

    private final int userId;

    public AccountViewRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "AccountViewRequest{userId=" + userId + "}";
    }
}