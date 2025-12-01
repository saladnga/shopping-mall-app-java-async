package com.repository;

import com.common.Database;
import com.entities.Wishlist;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLiteWishlistRepository implements WishlistRepository {
    private final Database db;

    public SQLiteWishlistRepository(Database db) {
        this.db = db;
    }

    private Wishlist mapRow(ResultSet result) throws SQLException {
        return new Wishlist(
                result.getInt("id"),
                result.getInt("customer_id"),
                result.getInt("item_id"),
                result.getInt("quantity"),
                result.getLong("added_at"));
    }

    @Override
    public Wishlist findById(int id) {
        String sql = "SELECT * FROM wishlist WHERE id = ?";
        return db.queryOne(sql, rs -> mapRow(rs), id);
    }

    @Override
    public List<Wishlist> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM wishlist WHERE customer_id = ? ORDER BY added_at DESC";
        return db.queryList(sql, rs -> mapRow(rs), customerId);
    }

    @Override
    public Wishlist findByCustomerAndItem(int customerId, int itemId) {
        String sql = "SELECT * FROM wishlist WHERE customer_id = ? AND item_id = ?";
        return db.queryOne(sql, rs -> mapRow(rs), customerId, itemId);
    }

    @Override
    public void insert(Wishlist wishlist) {
        String sql = "INSERT INTO wishlist(customer_id, item_id, quantity, added_at) VALUES (?, ?, ?, ?)";
        int id = db.executeInsertReturnId(sql,
                wishlist.getCustomerId(),
                wishlist.getItemId(),
                wishlist.getQuantity(),
                wishlist.getAddedAt());
        wishlist.setId(id);
    }

    @Override
    public void update(Wishlist wishlist) {
        String sql = "UPDATE wishlist SET quantity=?, added_at=? WHERE id=?";
        db.executeUpdate(sql, wishlist.getQuantity(), wishlist.getAddedAt(), wishlist.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM wishlist WHERE id = ?";
        db.executeUpdate(sql, id);
    }

    @Override
    public void deleteAllByCustomer(int customerId) {
        String sql = "DELETE FROM wishlist WHERE customer_id = ?";
        db.executeUpdate(sql, customerId);
    }
}