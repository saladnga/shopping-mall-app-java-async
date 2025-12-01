package com.entities;

public class UserLike {

    private int itemId;
    private int likeCount;

    public UserLike(int itemId, int likeCount) {
        this.itemId = itemId;
        this.likeCount = likeCount;
    }

    public int getItemId() {
        return itemId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}