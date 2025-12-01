package com.common.dto.message;

/**
 * DTO used when a user or staff requests to view message history.
 *
 * Immutable & thread-safe.
 */

public final class MessageHistoryRequest {

    private final int userId;
    private final int staffId;

    public MessageHistoryRequest(int userId, int staffId) {
        this.userId = userId;
        this.staffId = staffId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStaffId() {
        return staffId;
    }

    @Override
    public String toString() {
        return "MessageHistoryRequest{" +
                "userId=" + userId +
                ", staffId=" + staffId +
                '}';
    }
}