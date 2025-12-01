package com.common.dto.wishlist;

public class WishlistAddRequest {

    private final int userId;
    private final int itemId;
    private final int quantity;

    public WishlistAddRequest(int userId, int itemId, int quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}