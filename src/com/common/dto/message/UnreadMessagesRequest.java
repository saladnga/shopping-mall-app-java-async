package com.common.dto.message;

/**
 * DTO for unread message count and staff notifications
 */
public class UnreadMessagesRequest {
    private final int staffId;

    public UnreadMessagesRequest(int staffId) {
        this.staffId = staffId;
    }

    public int getStaffId() {
        return staffId;
    }

    @Override
    public String toString() {
        return "UnreadMessagesRequest{staffId=" + staffId + "}";
    }
}