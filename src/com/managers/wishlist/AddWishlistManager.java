package com.managers.wishlist;

import com.entities.Item;
import com.entities.Wishlist;
import com.repository.ItemRepository;
import com.repository.WishlistRepository;

public class AddWishlistManager {

    private final WishlistRepository wishlistRepo;
    private final ItemRepository itemRepo;

    public AddWishlistManager(WishlistRepository wishlistRepo, ItemRepository itemRepo) {
        this.wishlistRepo = wishlistRepo;
        this.itemRepo = itemRepo;
    }

    /**
     * Add an item to wishlist (quantity always = 1)
     */
    public String addToWishlist(int userId, int itemId) {

        Item item = itemRepo.findById(itemId);

        if (item == null)
            return "Item does not exist.";

        if (item.getStockQuantity() <= 0)
            return "Item is out of stock.";

        Wishlist existing = wishlistRepo.findByCustomerAndItem(userId, itemId);

        if (existing != null)
            return "Item already exists in wishlist.";

        Wishlist wl = new Wishlist(
                0,
                userId,
                itemId,
                1,
                System.currentTimeMillis());

        wishlistRepo.insert(wl);

        return "Item added to wishlist.";
    }
}