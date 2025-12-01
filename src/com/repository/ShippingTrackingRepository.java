package com.repository;

import com.entities.ShippingTracking;
import java.util.concurrent.ConcurrentHashMap;

public class ShippingTrackingRepository {

    private final ConcurrentHashMap<Integer, ShippingTracking> repo = new ConcurrentHashMap<>();

    public void save(ShippingTracking t) {
        repo.put(t.getOrderId(), t);
    }

    public ShippingTracking find(int orderId) {
        return repo.get(orderId);
    }

    public void update(ShippingTracking t) {
        repo.put(t.getOrderId(), t);
    }
}