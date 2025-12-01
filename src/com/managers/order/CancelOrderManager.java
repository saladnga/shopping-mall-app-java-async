package com.managers.order;

import com.entities.Order;
import com.entities.OrderItem;
import com.repository.ItemRepository;
import com.repository.OrderItemRepository;
import com.repository.OrderRepository;

import java.util.List;

public class CancelOrderManager {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ItemRepository itemRepo;

    public CancelOrderManager(OrderRepository orderRepo,
            OrderItemRepository orderItemRepo,
            ItemRepository itemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.itemRepo = itemRepo;
    }

    public Order cancel(int userId, int orderId) throws Exception {

        Order order = orderRepo.findById(orderId);

        if (order == null)
            throw new Exception("Order does not exist");

        if (order.getCustomerId() != userId)
            throw new Exception("Cannot cancel order of another user");

        if (order.getStatus() == Order.OrderStatus.DELIVERED)
            throw new Exception("Cannot cancel delivered order");

        if (order.getStatus() == Order.OrderStatus.CANCELED)
            throw new Exception("Order already canceled");

        // Refund stock
        List<OrderItem> items = orderItemRepo.findByOrderId(orderId);

        for (OrderItem oi : items) {
            int newStock = oi.getQuantity();
            itemRepo.updateStock(oi.getItemId(),
                    itemRepo.findById(oi.getItemId()).getStockQuantity() + newStock);
        }

        // Update order status
        orderRepo.updateStatus(orderId, Order.OrderStatus.CANCELED);

        order.setStatus(Order.OrderStatus.CANCELED);
        return order;
    }
}