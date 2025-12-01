package com.managers.message;

import com.broker.*;
import com.common.dto.message.MessageReplyRequest;
import com.entities.UserMessage;
import com.repository.MessageRepository;

import java.util.concurrent.CompletableFuture;

public class ReplyMessageManager implements Listener {

    private final AsyncMessageBroker broker;
    private final MessageRepository repo;

    public ReplyMessageManager(AsyncMessageBroker broker, MessageRepository repo) {
        this.broker = broker;
        this.repo = repo;
        broker.registerListener(EventType.MESSAGE_REPLY_REQUESTED, this);
    }

    @Override
    public CompletableFuture<Void> onMessage(Message message) {
        return CompletableFuture.runAsync(() -> {

            MessageReplyRequest req = (MessageReplyRequest) message.getPayload();

            UserMessage msg = new UserMessage(
                    0,
                    req.getStaffId(),
                    req.getUserId(),
                    "Reply",
                    req.getContent(),
                    UserMessage.MessageStatus.UNREAD,
                    System.currentTimeMillis());

            repo.save(msg);
            broker.publish(EventType.MESSAGE_SENT_CONFIRMATION, msg);
        });
    }
}