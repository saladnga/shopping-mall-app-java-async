package com.subsystems;

import com.broker.AsyncMessageBroker;
import com.broker.EventType;
import com.broker.Listener;
import com.broker.Message;
import com.common.dto.wishlist.WishlistAddRequest;
import com.common.dto.wishlist.WishlistRemoveRequest;
import com.entities.Item;
import com.entities.Wishlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * WishlistManagement
 * -------------------------------------------------------
 * - Add item to wishlist (with stock & unique constraint)
 * - Remove item
 * - View wishlist
 * - Emits async events through broker
 *
 * NOTE:
 * Current implementation uses in-memory list as placeholder.
 * Replace with Database repo when integrating SQLite.
 */
public class WishlistManagement implements Subsystems {

    private static final Logger LOGGER = Logger.getLogger(WishlistManagement.class.getName());

    private AsyncMessageBroker broker;

    // Temporary in-memory mock storage
    private final List<Wishlist> wishlistStore = new ArrayList<>();

    private final Listener handleAdd = this::onAddRequested;
    private final Listener handleRemove = this::onRemoveRequested;
    private final Listener handleView = this::onViewRequested;

    @Override
    public void init(AsyncMessageBroker broker) {
        this.broker = broker;

        broker.registerListener(EventType.WISHLIST_ADD_REQUESTED, handleAdd);
        broker.registerListener(EventType.WISHLIST_REMOVE_REQUESTED, handleRemove);
        broker.registerListener(EventType.WISHLIST_VIEW_REQUESTED, handleView);

        LOGGER.info("[WishlistManagement] Subsystem initialized.");
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        broker.unregisterListener(EventType.WISHLIST_ADD_REQUESTED, handleAdd);
        broker.unregisterListener(EventType.WISHLIST_REMOVE_REQUESTED, handleRemove);
        broker.unregisterListener(EventType.WISHLIST_VIEW_REQUESTED, handleView);

        LOGGER.info("[WishlistManagement] Subsystem shutdown.");
    }

    /**
     * ----------------------------
     * HANDLE ADD TO WISHLIST
     * -----------------------------
     */
    private CompletableFuture<Void> onAddRequested(Message message) {
        return CompletableFuture.runAsync(() -> {

            if (!(message.getPayload() instanceof WishlistAddRequest req)) {
                LOGGER.warning("[Wishlist] Invalid DTO for add request");
                return;
            }

            LOGGER.info("[Wishlist] Add requested: user=" + req.getUserId() +
                    ", item=" + req.getItemId());

            // Business rule 1: check stock
            Item item = mockGetItemById(req.getItemId());
            if (item.getStockQuantity() <= 0) {
                broker.publish(EventType.WISHLIST_ADD_FAILED,
                        "Cannot add — item is out of stock.");
                return;
            }

            // Business rule 2: unique pair (userId, itemId)
            boolean exists = wishlistStore.stream()
                    .anyMatch(w -> w.getCustomerId() == req.getUserId()
                            && w.getItemId() == req.getItemId());

            if (exists) {
                broker.publish(EventType.WISHLIST_ADD_FAILED,
                        "Item already exists in wishlist.");
                return;
            }

            // Add wishlist entry
            Wishlist entry = new Wishlist(
                    generateWishlistId(),
                    req.getUserId(),
                    req.getItemId(),
                    req.getQuantity(),
                    System.currentTimeMillis());

            wishlistStore.add(entry);

            broker.publish(EventType.WISHLIST_ADD_SUCCESS, entry);
            LOGGER.info("[Wishlist] Added successfully.");
        });
    }

    /**
     * ----------------------------
     * HANDLE REMOVE FROM WISHLIST
     * -----------------------------
     */
    private CompletableFuture<Void> onRemoveRequested(Message message) {
        return CompletableFuture.runAsync(() -> {

            if (!(message.getPayload() instanceof WishlistRemoveRequest req)) {
                LOGGER.warning("[Wishlist] Invalid DTO for remove request");
                return;
            }

            LOGGER.info("[Wishlist] Remove requested: user=" + req.getUserId()
                    + ", item=" + req.getItemId());

            Wishlist found = wishlistStore.stream()
                    .filter(w -> w.getCustomerId() == req.getUserId()
                            && w.getItemId() == req.getItemId())
                    .findFirst().orElse(null);

            if (found == null) {
                broker.publish(EventType.WISHLIST_REMOVE_FAILED,
                        "Item not found in wishlist.");
                return;
            }

            wishlistStore.remove(found);

            broker.publish(EventType.WISHLIST_REMOVE_SUCCESS, found);
            LOGGER.info("[Wishlist] Removed successfully.");
        });
    }

    /**
     * ----------------------------
     * HANDLE VIEW WISHLIST
     * -----------------------------
     */
    private CompletableFuture<Void> onViewRequested(Message message) {
        return CompletableFuture.runAsync(() -> {

            Integer userId = (Integer) message.getPayload();
            LOGGER.info("[Wishlist] View requested for user=" + userId);

            List<Wishlist> result = wishlistStore.stream()
                    .filter(w -> w.getCustomerId() == userId)
                    .toList();

            broker.publish(EventType.WISHLIST_DETAILS_RETURNED, result);
        });
    }

    /** MOCKS + HELPERS */

    private int generateWishlistId() {
        return wishlistStore.size() + 1;
    }

    // Mock: replace with DB query
    private Item mockGetItemById(int id) {
        return new Item(id, "Item #" + id,
                "desc", 10.0, 10, 0);
    }
}