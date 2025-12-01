package com.common.dto.wishlist;

/**
 * DTO used when a user removes an item from their wishlist.
 *
 * Sent with:
 * EventType.WISHLIST_REMOVE_REQUESTED
 */
public class WishlistRemoveRequest {

    private final int userId;
    private final int itemId;

    public WishlistRemoveRequest(int userId, int itemId) {
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
        return "WishlistRemoveRequest{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                '}';
    }
}