package com.managers.message;

import com.broker.*;
import com.common.dto.message.ConversationListRequest;
import com.entities.Conversation;
import com.repository.MessageRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConversationListManager implements Listener {

    private final AsyncMessageBroker broker;
    private final MessageRepository repo;

    public ConversationListManager(AsyncMessageBroker broker, MessageRepository repo) {
        this.broker = broker;
        this.repo = repo;
        broker.registerListener(EventType.CONVERSATION_LIST_REQUESTED, this);
    }

    @Override
    public CompletableFuture<Void> onMessage(Message message) {
        return CompletableFuture.runAsync(() -> {

            ConversationListRequest req = (ConversationListRequest) message.getPayload();
            List<Conversation> conversations = repo.getConversationsForUser(req.getUserId());

            broker.publish(EventType.CONVERSATION_LIST_RETURNED, conversations);
            System.out.println("[ConversationListManager] Found " + conversations.size() + " conversations for user ID: " + req.getUserId());
        });
    }
}