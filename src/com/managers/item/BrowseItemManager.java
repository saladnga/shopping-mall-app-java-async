package com.managers.item;

import com.entities.Item;
import com.repository.ItemRepository;
import java.util.List;

/**
 * Handles browsing items (return all active items).
 */
public class BrowseItemManager {

    private final ItemRepository repo;

    public BrowseItemManager(ItemRepository repo) {
        this.repo = repo;
    }

    public List<Item> browseAll() {
        return repo.findAll();
    }

    public List<Item> search(String keyword) {
        return repo.searchByKeyword(keyword);
    }
}