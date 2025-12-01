package com.managers.item;

import com.entities.Item;
import com.repository.ItemRepository;

public class LikeManager {

    private final ItemRepository repo;

    public LikeManager(ItemRepository repo) {
        this.repo = repo;
    }

    public boolean likeItem(int userId, int itemId) throws Exception {

        // Check duplicate like
        if (repo.existsLike(userId, itemId))
            throw new Exception("You already liked this item");

        // Check item exists
        Item item = repo.findById(itemId);
        if (item == null)
            throw new Exception("Item not found");

        // Insert like record
        repo.insertLike(userId, itemId);

        // Update like_count column
        repo.incrementLikeCount(itemId);

        return true;
    }
}