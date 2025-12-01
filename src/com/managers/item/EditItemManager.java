package com.managers.item;

import com.entities.Item;
import com.repository.ItemRepository;

public class EditItemManager {

    private final ItemRepository repo;

    public EditItemManager(ItemRepository repo) {
        this.repo = repo;
    }

    public Item editItem(int itemId, String newName, String newDescription,
            Double newPrice, Integer newStock) throws Exception {

        Item item = repo.findById(itemId);

        if (item == null)
            throw new Exception("Item not found");

        if (newName != null && !newName.isBlank())
            item.setName(newName);

        if (newDescription != null)
            item.setDescription(newDescription);

        if (newPrice != null) {
            if (newPrice <= 0)
                throw new Exception("Invalid price");
            item.setPrice(newPrice);
        }

        if (newStock != null) {
            if (newStock < 0)
                throw new Exception("Invalid stock quantity");
            item.setStockQuantity(newStock);

            // sync stock to DB using repo method
            repo.updateStock(itemId, newStock);
        }

        repo.update(item);
        return item;
    }
}