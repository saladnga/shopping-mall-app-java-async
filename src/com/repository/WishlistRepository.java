package com.repository;

import com.entities.Wishlist;
import java.util.List;

public interface WishlistRepository {

    Wishlist findById(int id);

    List<Wishlist> findByCustomerId(int customerId);

    Wishlist findByCustomerAndItem(int customerId, int itemId);

    /** For backward compatibility with old managers */
    default Wishlist findByUserAndItem(int userId, int itemId) {
        return findByCustomerAndItem(userId, itemId);
    }

    void insert(Wishlist wishlist);

    void update(Wishlist wishlist);

    void delete(int id);

    void deleteAllByCustomer(int customerId);
}