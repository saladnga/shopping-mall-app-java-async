package com.managers.item;

import com.entities.Item;
import com.repository.ItemRepository;

public class UploadItemManager {

    private final ItemRepository repo;

    public UploadItemManager(ItemRepository repo) {
        this.repo = repo;
    }

    public Item upload(String name, String description, double price, int stock) throws Exception {

        if (name == null || name.isBlank())
            throw new Exception("Item name cannot be empty");
        if (price <= 0)
            throw new Exception("Price must be positive");
        if (stock < 0)
            throw new Exception("Stock cannot be negative");

        Item item = new Item(0, name, description, price, stock, 0);

        // repo.insert() now returns int ID
        int id = repo.insert(item);
        item.setId(id);

        return item;
    }
}