package com.common.dto.message;

/**
 * DTO for requesting conversation list for a user
 */
public class ConversationListRequest {
    private final int userId;

    public ConversationListRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ConversationListRequest{userId=" + userId + "}";
    }
}