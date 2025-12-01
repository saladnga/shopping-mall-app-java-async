package com.common.dto.account;

import com.entities.User;

public class CustomerAccessRequest {

    private final int requesterId;
    private final User.Role requesterRole;
    private final int customerId;

    public CustomerAccessRequest(int requesterId, User.Role requesterRole, int customerId) {
        this.requesterId = requesterId;
        this.requesterRole = requesterRole;
        this.customerId = customerId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public User.Role getRequesterRole() {
        return requesterRole;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "CustomerAccessRequest{" +
                "requesterId=" + requesterId +
                ", requesterRole=" + requesterRole +
                ", customerId=" + customerId +
                '}';
    }
}