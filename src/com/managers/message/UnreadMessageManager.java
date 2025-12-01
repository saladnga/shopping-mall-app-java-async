package com.managers.message;

import com.broker.*;
import com.common.dto.message.UnreadMessagesRequest;
import com.entities.UserMessage;
import com.repository.MessageRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UnreadMessageManager implements Listener {

    private final AsyncMessageBroker broker;
    private final MessageRepository repo;

    public UnreadMessageManager(AsyncMessageBroker broker, MessageRepository repo) {
        this.broker = broker;
        this.repo = repo;
        broker.registerListener(EventType.UNREAD_MESSAGES_REQUESTED, this);
    }

    @Override
    public CompletableFuture<Void> onMessage(Message message) {
        return CompletableFuture.runAsync(() -> {

            UnreadMessagesRequest req = (UnreadMessagesRequest) message.getPayload();

            // Get unread messages for staff (messages with recipientId = -1 or specific staff ID)
            List<UserMessage> unreadMessages = repo.getUnreadMessagesForStaff(req.getStaffId());

            broker.publish(EventType.UNREAD_MESSAGES_RETURNED, unreadMessages);
            System.out.println("[UnreadMessageManager] Found " + unreadMessages.size() + " unread messages for staff ID: " + req.getStaffId());
        });
    }
}