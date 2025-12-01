package com.repository;

import com.entities.UserMessage;

import java.util.*;
import java.util.stream.Collectors;

public class MessageRepository {

        private final List<UserMessage> storage = new ArrayList<>();
        private int nextId = 1;

        // ===== CREATE =====
        public synchronized UserMessage save(UserMessage msg) {
                msg.setId(nextId++);
                storage.add(msg);
                return msg;
        }

        // ===== READ: full conversation =====
        public synchronized List<UserMessage> getConversation(int userId, int staffId) {
                return storage.stream()
                                .filter(m -> (m.getSenderId() == userId && m.getRecipientId() == staffId) ||
                                                (m.getSenderId() == staffId && m.getRecipientId() == userId))
                                .sorted(Comparator.comparingLong(UserMessage::getTimeStamp))
                                .collect(Collectors.toList());
        }

        // ===== READ: unread messages =====
        public synchronized List<UserMessage> getUnreadMessages(int userId, int staffId) {
                return storage.stream()
                                .filter(m -> m.getRecipientId() == staffId &&
                                                m.getSenderId() == userId &&
                                                m.getStatus() == UserMessage.MessageStatus.UNREAD)
                                .collect(Collectors.toList());
        }

        // ===== UPDATE: mark as read =====
        public synchronized void markRead(int userId, int staffId) {
                storage.stream()
                                .filter(m -> m.getRecipientId() == staffId &&
                                                m.getSenderId() == userId)
                                .forEach(m -> m.setStatus(UserMessage.MessageStatus.READ));
        }

        // ===== READ: distinct partners =====
        public synchronized List<Integer> getConversationPartners(int staffId) {
                return storage.stream()
                                .filter(m -> m.getRecipientId() == staffId || m.getSenderId() == staffId)
                                .map(m -> m.getSenderId() == staffId ? m.getRecipientId() : m.getSenderId())
                                .distinct()
                                .toList();
        }

        // ===== READ: message history for user =====
        public synchronized List<UserMessage> getMessageHistory(int userId) {
                return storage.stream()
                                .filter(m -> m.getSenderId() == userId || m.getRecipientId() == userId)
                                .sorted(Comparator.comparingLong(UserMessage::getTimeStamp))
                                .collect(Collectors.toList());
        }

        // ===== READ: conversations for user (with conversation metadata) =====
        public synchronized List<com.entities.Conversation> getConversationsForUser(int userId) {
                Map<Integer, com.entities.Conversation> conversationMap = new HashMap<>();

                for (UserMessage msg : storage) {
                        int otherUserId = -1;
                        boolean includeInConversation = false;

                        if (msg.getSenderId() == userId && msg.getRecipientId() != -1) {
                                // User sent message to someone specific
                                otherUserId = msg.getRecipientId();
                                includeInConversation = true;
                        } else if (msg.getRecipientId() == userId) {
                                // Someone sent message to this user
                                otherUserId = msg.getSenderId();
                                includeInConversation = true;
                        } else if (msg.getRecipientId() == -1 && msg.getSenderId() != userId) {
                                // This is a broadcast message from someone else - staff can see it
                                // Only show for staff (assuming staff have higher IDs or specific role check)
                                otherUserId = msg.getSenderId();
                                includeInConversation = true;
                        }

                        if (includeInConversation && otherUserId != -1) {
                                com.entities.Conversation conv = conversationMap.get(otherUserId);
                                if (conv == null) {
                                        conv = new com.entities.Conversation();
                                        // Set customer as the lower ID, staff as higher (or based on role)
                                        conv.setCustomerId(otherUserId); // The other person
                                        conv.setStaffId(userId); // Current user (if staff)
                                        conv.setCustomerName("Customer " + otherUserId);
                                        conv.setStaffName("Staff " + userId);
                                        conv.setUnreadCount(0);
                                        conversationMap.put(otherUserId, conv);
                                }

                                // Update last message info
                                if (conv.getLastMessageTime() < msg.getTimeStamp()) {
                                        conv.setLastMessage(msg.getContent());
                                        conv.setLastMessageTime(msg.getTimeStamp());
                                }

                                // Count unread messages (messages TO this user that are unread)
                                if ((msg.getRecipientId() == userId || msg.getRecipientId() == -1) &&
                                                msg.getStatus() == UserMessage.MessageStatus.UNREAD) {
                                        conv.setUnreadCount(conv.getUnreadCount() + 1);
                                }
                        }
                }

                return new ArrayList<>(conversationMap.values());
        }

        // ===== READ: conversation messages between two users =====
        public synchronized List<UserMessage> getConversationMessages(int userId1, int userId2) {
                return storage.stream()
                                .filter(m -> (m.getSenderId() == userId1 && m.getRecipientId() == userId2) ||
                                                (m.getSenderId() == userId2 && m.getRecipientId() == userId1))
                                .sorted(Comparator.comparingLong(UserMessage::getTimeStamp))
                                .collect(Collectors.toList());
        }

        // ===== UPDATE: mark messages as read between two users =====
        public synchronized void markMessagesAsRead(int readerId, int otherUserId) {
                storage.stream()
                                .filter(m -> m.getRecipientId() == readerId && m.getSenderId() == otherUserId)
                                .forEach(UserMessage::markAsRead);
        }

        // ===== READ: unread messages for staff =====
        public synchronized List<UserMessage> getUnreadMessagesForStaff(int staffId) {
                return storage.stream()
                                .filter(m -> (m.getRecipientId() == staffId || m.getRecipientId() == -1) &&
                                                m.getStatus() == UserMessage.MessageStatus.UNREAD)
                                .collect(Collectors.toList());
        }

        // ===== UPDATE: mark customer messages as read when staff replies =====
        public synchronized void markCustomerMessagesAsReadByStaff(int customerId, int staffId) {
                storage.stream()
                                .filter(m -> m.getSenderId() == customerId &&
                                                (m.getRecipientId() == staffId || m.getRecipientId() == -1) &&
                                                m.getStatus() == UserMessage.MessageStatus.UNREAD)
                                .forEach(UserMessage::markAsRead);

                System.out.println("[MessageRepository] Marked messages from customer " + customerId
                                + " as read by staff " + staffId);
        }
}