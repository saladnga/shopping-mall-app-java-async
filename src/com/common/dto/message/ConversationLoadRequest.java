package com.common.dto.message;

/**
 * DTO for loading a specific conversation between users
 */
public class ConversationLoadRequest {
    private final int userId;
    private final int otherUserId;

    public ConversationLoadRequest(int userId, int otherUserId) {
        this.userId = userId;
        this.otherUserId = otherUserId;
    }

    public int getUserId() {
        return userId;
    }

    public int getOtherUserId() {
        return otherUserId;
    }

    @Override
    public String toString() {
        return "ConversationLoadRequest{userId=" + userId + ", otherUserId=" + otherUserId + "}";
    }
}