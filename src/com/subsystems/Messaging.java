package com.subsystems;

import com.broker.AsyncMessageBroker;
import com.repository.MessageRepository;
import com.managers.message.*;

public class Messaging implements Subsystems {

    private AsyncMessageBroker broker;
    private MessageRepository repo;

    @Override
    public void init(AsyncMessageBroker broker) {
        this.broker = broker;
        this.repo = new MessageRepository();

        new SendMessageManager(broker, repo);
        new ReplyMessageManager(broker, repo);
        new LoadConversationManager(broker, repo);
        new ConversationListManager(broker, repo);
        new UnreadMessageManager(broker, repo);

        System.out.println("[Messaging] Initialized.");
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
    }
}