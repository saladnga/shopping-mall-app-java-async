package com.repository;

import com.entities.Item;
import com.entities.ItemRanking;

import java.util.List;

public interface ItemRepository {

    // ===== CRUD =====
    Item findById(int id);

    List<Item> findAll();

    int insert(Item item);

    void update(Item item);

    void delete(int id);

    // ===== SEARCH =====
    List<Item> searchByKeyword(String keyword);

    // ===== LIKE SYSTEM =====
    boolean existsLike(int userId, int itemId);

    void insertLike(int userId, int itemId);

    void incrementLikeCount(int itemId);

    // ===== STOCK =====
    void updateStock(int itemId, int newStock);

    void increaseStock(int itemId, int amount);

    void decreaseStock(int itemId, int amount);

    // ===== RANKING =====
    List<ItemRanking> computeRanking();
}