package com.managers.message;

import com.broker.*;
import com.common.dto.message.MessageSendRequest;
import com.entities.UserMessage;
import com.repository.MessageRepository;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SendMessageManager implements Listener {

    private final AsyncMessageBroker broker;
    private final MessageRepository repo;

    public SendMessageManager(AsyncMessageBroker broker, MessageRepository repo) {
        this.broker = broker;
        this.repo = repo;
        broker.registerListener(EventType.MESSAGE_SEND_REQUESTED, this);
    }

    @Override
    public CompletableFuture<Void> onMessage(Message message) {
        return CompletableFuture.runAsync(() -> {

            MessageSendRequest req = (MessageSendRequest) message.getPayload();

            // If recipientId is -1, this is a customer broadcasting to all staff
            if (req.getRecipientId() == -1) {
                // Create a single message that represents the broadcast
                // But store it as sender=customer, recipient=-1 so staff can see it
                UserMessage msg = new UserMessage(
                        0,
                        req.getSenderId(), // Customer ID
                        -1, // Broadcast to all staff
                        req.getSubject(),
                        req.getContent(),
                        UserMessage.MessageStatus.UNREAD,
                        System.currentTimeMillis());

                repo.save(msg);
                broker.publish(EventType.MESSAGE_SENT_CONFIRMATION, msg);

                // Notify all staff about new message from this customer
                broker.publish(EventType.STAFF_NOTIFIED_NEW_MESSAGE, msg);

                System.out.println(
                        "[SendMessageManager] Customer " + req.getSenderId() + " sent broadcast message to all staff");
            } else {
                // Direct message to specific recipient
                UserMessage msg = new UserMessage(
                        0,
                        req.getSenderId(),
                        req.getRecipientId(),
                        req.getSubject(),
                        req.getContent(),
                        UserMessage.MessageStatus.UNREAD,
                        System.currentTimeMillis());

                repo.save(msg);
                broker.publish(EventType.MESSAGE_SENT_CONFIRMATION, msg);

                // If this is a staff reply to a customer, mark original messages as read
                if (req.getSenderId() > 0 && req.getRecipientId() > 0) {
                    // Mark customer's messages to this staff as read when staff replies
                    repo.markCustomerMessagesAsReadByStaff(req.getRecipientId(), req.getSenderId());
                    broker.publish(EventType.MESSAGE_MARKED_AS_READ,
                            Map.of("staffId", req.getSenderId(), "customerId", req.getRecipientId()));
                }

                // Notify customer of staff reply
                broker.publish(EventType.CUSTOMER_NOTIFIED_NEW_REPLY, msg);

                System.out.println("[SendMessageManager] Direct message from " + req.getSenderId() + " to "
                        + req.getRecipientId());
            }
        });
    }
}