package com.entities;

import java.util.List;

/**
 * Represents a conversation between a customer and staff
 */

public class Conversation {
    private int customerId;
    private int staffId;
    private String customerName;
    private String staffName;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private List<UserMessage> messages;

    public Conversation() {
    }

    public Conversation(int customerId, int staffId, String customerName, String staffName,
            String lastMessage, long lastMessageTime, int unreadCount) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.customerName = customerName;
        this.staffName = staffName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<UserMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<UserMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "customerId=" + customerId +
                ", staffId=" + staffId +
                ", customerName='" + customerName + '\'' +
                ", staffName='" + staffName + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                ", unreadCount=" + unreadCount +
                '}';
    }
}