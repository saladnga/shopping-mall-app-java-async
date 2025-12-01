package com.repository;

import com.common.Database;
import com.entities.Item;
import com.entities.ItemRanking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLiteItemRepository implements ItemRepository {

    private final Database db;

    public SQLiteItemRepository(Database db) {
        this.db = db;
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                rs.getInt("like_count"));
    }

    @Override
    public Item findById(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        return db.queryOne(sql, rs -> mapRow(rs), id);
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM items";
        return db.queryList(sql, rs -> mapRow(rs));
    }

    @Override
    public int insert(Item item) {
        String sql = "INSERT INTO items(name, description, price, stock_quantity, like_count) VALUES (?, ?, ?, ?, ?)";
        return db.executeInsertReturnId(sql,
                item.getName(), item.getDescription(), item.getPrice(), item.getStockQuantity(), item.getLikeCount());
    }

    @Override
    public void update(Item item) {
        String sql = "UPDATE items SET name=?, description=?, price=?, stock_quantity=?, like_count=? WHERE id=?";
        db.executeUpdate(sql, item.getName(), item.getDescription(), item.getPrice(), item.getStockQuantity(),
                item.getLikeCount(), item.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        db.executeUpdate(sql, id);
    }

    @Override
    public List<Item> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM items WHERE name LIKE ? OR description LIKE ?";
        String pattern = "%" + keyword + "%";
        return db.queryList(sql, rs -> mapRow(rs), pattern, pattern);
    }

    @Override
    public boolean existsLike(int userId, int itemId) {
        String sql = "SELECT 1 FROM liked_item WHERE customer_id = ? AND item_id = ? LIMIT 1";
        Integer r = db.queryOne(sql, rs -> rs.getInt(1), userId, itemId);
        return r != null && r == 1;
    }

    @Override
    public void insertLike(int userId, int itemId) {
        String sql = "INSERT OR IGNORE INTO liked_item(customer_id, item_id) VALUES (?, ?)";
        db.executeUpdate(sql, userId, itemId);
        incrementLikeCount(itemId);
    }

    @Override
    public void incrementLikeCount(int itemId) {
        String sql = "UPDATE items SET like_count = COALESCE(like_count,0) + 1 WHERE id = ?";
        db.executeUpdate(sql, itemId);
    }

    @Override
    public void updateStock(int itemId, int newStock) {
        String sql = "UPDATE items SET stock_quantity = ? WHERE id = ?";
        db.executeUpdate(sql, newStock, itemId);
    }

    @Override
    public void increaseStock(int itemId, int amount) {
        String sql = "UPDATE items SET stock_quantity = stock_quantity + ? WHERE id = ?";
        db.executeUpdate(sql, amount, itemId);
    }

    @Override
    public void decreaseStock(int itemId, int amount) {
        String sql = "UPDATE items SET stock_quantity = MAX(0, stock_quantity - ?) WHERE id = ?";
        db.executeUpdate(sql, amount, itemId);
    }

    @Override
    public List<ItemRanking> computeRanking() {
        String sql = "SELECT id, name, like_count FROM items ORDER BY like_count DESC LIMIT 10";
        return db.queryList(sql,
                rs -> new ItemRanking(rs.getInt("id"), rs.getString("name"), rs.getInt("like_count"), 0));
    }
}
