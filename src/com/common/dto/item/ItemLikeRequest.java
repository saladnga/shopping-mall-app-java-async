package com.common.dto.item;

public class ItemLikeRequest {

    private final int userId;
    private final int itemId;

    public ItemLikeRequest(int userId, int itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "ItemLikeRequest{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                '}';
    }
}