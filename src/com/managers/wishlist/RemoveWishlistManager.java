package com.managers.wishlist;

import com.entities.Wishlist;
import com.repository.WishlistRepository;

public class RemoveWishlistManager {

    private final WishlistRepository wishlistRepo;

    public RemoveWishlistManager(WishlistRepository wishlistRepo) {
        this.wishlistRepo = wishlistRepo;
    }

    /**
     * Remove a single wishlist entry
     */
    public String removeFromWishlist(int userId, int itemId) {

        Wishlist wl = wishlistRepo.findByCustomerAndItem(userId, itemId);

        if (wl == null) {
            return "Item not found in wishlist.";
        }

        wishlistRepo.delete(wl.getId());

        return "Item removed from wishlist.";
    }

    /**
     * Remove all wishlist items for the user
     */
    public String clearWishlist(int userId) {

        wishlistRepo.deleteAllByCustomer(userId);

        return "All wishlist items removed.";
    }
}