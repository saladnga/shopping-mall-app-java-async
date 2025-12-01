package com.common.dto.order;

import java.util.List;

/**
 * DTO for creating a new order.
 * Sent with:
 * EventType.ORDER_CREATED_REQUESTED
 *
 * CreateOrderManager will:
 * - validate stock
 * - calculate totals
 * - create Order + OrderItems
 * - publish PURCHASE_REQUESTED
 */

public class OrderCreateRequest {

    private final int userId;
    private final List<OrderItemRequest> items; // list of itemId + quantity
    private final String shippingAddress;

    public OrderCreateRequest(int userId, List<OrderItemRequest> items, String shippingAddress) {
        this.userId = userId;
        this.items = items;
        this.shippingAddress = shippingAddress;
    }

    public int getUserId() {
        return userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    @Override
    public String toString() {
        return "OrderCreateRequest{" +
                "userId=" + userId +
                ", items=" + items +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }

    /**
     * Nested DTO representing a single item in the order creation.
     */
    public static class OrderItemRequest {

        private final int itemId;
        private final int quantity;

        public OrderItemRequest(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public int getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return "{itemId=" + itemId + ", quantity=" + quantity + "}";
        }
    }
}