package com.common.dto.wishlist;

/**
 * DTO used when user requests to view their wishlist.
 *
 * Sent with:
 * EventType.WISHLIST_VIEW_REQUESTED
 *
 * ViewWishlistManager will:
 * - load wishlist from DB
 * - publish WISHLIST_DETAILS_RETURNED
 */

public class WishlistViewRequest {

    private final int userId;

    public WishlistViewRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "WishlistViewRequest{userId=" + userId + '}';
    }
}