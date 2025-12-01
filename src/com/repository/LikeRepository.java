package com.repository;

public interface LikeRepository {

    boolean exists(int userId, int itemId);

    void insert(int userId, int itemId);

    void delete(int userId, int itemId);
}