package com;

import com.broker.AsyncMessageBroker;
import com.broker.EventType;
import com.common.Database;
import com.entities.*;
import com.services.*;
import com.repository.*;
import com.subsystems.*;
import com.common.dto.account.*;
import com.common.dto.auth.*;
import com.common.dto.item.*;
import com.common.dto.message.*;
import com.common.dto.order.*;
import com.common.dto.payment.*;
import com.common.dto.wishlist.*;
import com.managers.account.*;
import com.managers.item.*;
import com.managers.message.*;
import com.managers.order.*;
import com.managers.payment.*;
import com.managers.wishlist.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.Scanner;

public class Main {

    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);

    // Subsystems
    private static AccountManagement account;
    private static ItemManagement item;
    private static Messaging messaging;
    private static OrderManagement order;
    private static PaymentService payment;
    private static Reporting reporting;
    private static WishlistManagement wishlist;
    private static BrowseItemManager browseItem;
    private static CreateOrderManager createOrder;
    private static ViewAccountManager viewAccount;

    // Broker + DB
    private static AsyncMessageBroker broker;
    private static Database database;

    public static void main(String[] args) {

        System.out.println("[Main] Shopping Mall Application Starting...");

        // --------------------------------------------------------------
        // 1. Init Broker
        // --------------------------------------------------------------
        broker = new AsyncMessageBroker(1000, 8);
        broker.start();

        // --------------------------------------------------------------
        // 2. Init DB
        // --------------------------------------------------------------
        database = new Database();
        database.connect("jdbc:sqlite:shopping_mall.db");

        // --------------------------------------------------------------
        // 3. Init Repositories + Services
        // --------------------------------------------------------------
        // AuthenticationService has no-arg constructor; create it first
        AuthenticationService authService = new AuthenticationService();

        // SQLiteUserRepository requires Database + AuthenticationService
        UserRepository userRepo = new SQLiteUserRepository(database, authService);

        // Managers for account subsystem
        RegisterManager registerManager = new RegisterManager(userRepo,
                authService);
        LoginManager loginManager = new LoginManager(userRepo, authService);
        EditAccountManager editAccountManager = new EditAccountManager(userRepo, authService);
        ViewAccountManager viewAccountManager = new ViewAccountManager(userRepo);

        // Item repository (persisted in SQLite)
        ItemRepository itemRepo = new SQLiteItemRepository(database);
        ItemRankingRepository itemRankingRepo = new com.repository.InMemoryItemRankingRepository();

        BrowseItemManager browseItemManager = new BrowseItemManager(itemRepo);
        CreateOrderManager createOrderManager = new CreateOrderManager(itemRepo, null, null);
        EditItemManager editItemManager = new EditItemManager(itemRepo);
        LikeManager likeManager = new LikeManager(itemRepo);
        RankingManager rankingManager = new RankingManager(itemRankingRepo, itemRepo);

        // --------------------------------------------------------------
        // 4. Init Subsystems
        // --------------------------------------------------------------
        account = new AccountManagement(registerManager, loginManager, viewAccountManager);
        item = new ItemManagement(itemRepo);
        messaging = new Messaging();
        order = new OrderManagement();
        payment = new PaymentService();
        reporting = new Reporting();
        wishlist = new WishlistManagement();

        Subsystems[] modules = {
                account, item, messaging, order,
                payment, reporting, wishlist
        };

        for (Subsystems m : modules)
            m.init(broker);

        // Listen for login success/failure so Main can update currentUser and menus
        broker.registerListener(EventType.USER_LOGIN_SUCCESS, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (!(payload instanceof Map))
                return;
            Map<?, ?> map = (Map<?, ?>) payload;
            Object u = map.get("user");
            if (u instanceof User user) {
                currentUser = user;
                System.out.println("[Main] Welcome " + user.getUsername() + " (" + user.getRole() + ")");
                switch (user.getRole()) {
                    case CUSTOMER -> lastMenu = "CUSTOMER";
                    case STAFF -> lastMenu = "STAFF";
                    case CEO -> lastMenu = "CEO";
                }
            }
        }));

        broker.registerListener(EventType.ITEM_LIST_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> items) {
                System.out.println("Available items:");
                for (Object obj : items) {
                    if (obj instanceof Item item) {
                        System.out.println(item.getId() + " | " +
                                item.getName() + " | $" + item.getPrice() +
                                " | stock=" + item.getStockQuantity());
                    }
                }
                System.out.println("=======================");
            }
        }));

        broker.registerListener(EventType.USER_LOGIN_FAILED, msg -> CompletableFuture.runAsync(() ->

        {
            System.out.println("[Main] Login failed: " + msg.getPayload());
        }));

        // Handle registration responses
        broker.registerListener(EventType.USER_REGISTER_SUCCESS, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof Map<?, ?> map) {
                Object user = map.get("user");
                if (user instanceof User u) {
                    System.out.println("[Main] Registration successful! Welcome " + u.getUsername());
                    System.out.println("[Main] You can now login with your credentials.");
                }
            } else {
                System.out.println("[Main] Registration successful! You can now login.");
            }
        }));

        // Handle wishlist responses
        broker.registerListener(EventType.WISHLIST_DETAILS_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> items) {
                if (items.isEmpty()) {
                    System.out.println("Your wishlist is empty.");
                } else {
                    System.out.println("=== Your Wishlist ===");
                    for (Object obj : items) {
                        if (obj instanceof Item item) {
                            System.out.println(item.getId() + " | " + item.getName() +
                                    " | $" + item.getPrice() + " | stock=" + item.getStockQuantity());
                        }
                    }
                    System.out.println("=====================");
                }
            }
        }));

        // Handle purchase/order responses
        broker.registerListener(EventType.ORDER_CONFIRMED, msg -> CompletableFuture.runAsync(() -> {
            System.out.println("[Main] Purchase completed successfully!");
            Object payload = msg.getPayload();
            if (payload instanceof Order order) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Total: $" + order.getTotalAmount());
                System.out.println("Status: " + order.getStatus());
            }
        }));

        // Handle order history responses
        broker.registerListener(EventType.ORDER_HISTORY_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> orders) {
                if (orders.isEmpty()) {
                    System.out.println("You have no order history.");
                } else {
                    System.out.println("=== Your Order History ===");
                    for (Object obj : orders) {
                        if (obj instanceof Order order) {
                            System.out.println("Order ID: " + order.getId() +
                                    " | Date: " + order.getOrderDate() +
                                    " | Total: $" + order.getTotalAmount() +
                                    " | Status: " + order.getStatus());
                        }
                    }
                    System.out.println("==========================");
                }
            }
        }));

        // Handle account view responses
        broker.registerListener(EventType.ACCOUNT_VIEW_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof User user) {
                System.out.println("=== Account Information ===");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Role: " + user.getRole());
                System.out.println("Phone Number: " + user.getPhoneNumber());
                System.out.println("Address: " + user.getAddress());
                System.out.println("===========================");
            } else {
                System.out.println("[Main] Unable to retrieve account information.");
            }
        }));

        // Handle messaging responses
        broker.registerListener(EventType.MESSAGE_SENT_CONFIRMATION, msg -> CompletableFuture.runAsync(() -> {
            System.out.println("[Main] Message sent successfully!");
        }));

        broker.registerListener(EventType.MESSAGE_LIST_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> messages) {
                if (messages.isEmpty()) {
                    System.out.println("No messages found.");
                } else {
                    System.out.println("=== Messages ===");
                    for (Object obj : messages) {
                        if (obj instanceof UserMessage message) {
                            System.out.println("From: " + message.getSenderId() +
                                    " | Subject: " + message.getSubject() +
                                    " | Date: " + message.getTimeStamp());
                            System.out.println("Message: " + message.getContent());
                            System.out.println("----------------");
                        }
                    }
                    System.out.println("================");
                }
            }
        }));

        // Handle conversation responses
        broker.registerListener(EventType.CONVERSATION_LIST_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> conversations) {
                if (conversations.isEmpty()) {
                    System.out.println("No conversations found.");
                } else {
                    System.out.println("=== Conversations ===");
                    for (Object obj : conversations) {
                        if (obj instanceof Conversation conv) {
                            if (currentUser.getRole() == User.Role.CUSTOMER) {
                                // Customer sees staff info
                                System.out.println("With: " + conv.getStaffName() +
                                        " | Last: " + conv.getLastMessage() +
                                        " | Unread: " + conv.getUnreadCount());
                            } else {
                                // Staff sees customer ID clearly for replying
                                System.out.println("Customer ID: " + conv.getCustomerId() +
                                        " (" + conv.getCustomerName() + ")" +
                                        " | Last: " + conv.getLastMessage() +
                                        " | Unread: " + conv.getUnreadCount());
                            }
                        }
                    }
                    System.out.println("=====================");
                }
            }
        }));

        broker.registerListener(EventType.CONVERSATION_MESSAGES_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> messages) {
                if (messages.isEmpty()) {
                    System.out.println("No messages in this conversation.");
                } else {
                    System.out.println("=== Conversation Messages ===");
                    for (Object obj : messages) {
                        if (obj instanceof UserMessage message) {
                            String senderType = message.getSenderId() == currentUser.getId() ? "You" : "Other";
                            System.out.println("[" + senderType + "] " + message.getContent());
                            System.out.println("Time: " + new java.util.Date(message.getTimeStamp()));
                            System.out.println("Status: " + message.getStatus());
                            System.out.println("---");
                        }
                    }
                    System.out.println("=============================");
                }
            }
        }));

        broker.registerListener(EventType.UNREAD_MESSAGES_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof List<?> messages) {
                if (messages.isEmpty()) {
                    System.out.println("No unread messages.");
                } else {
                    System.out.println("=== Unread Messages (" + messages.size() + ") ===");
                    for (Object obj : messages) {
                        if (obj instanceof UserMessage message) {
                            System.out.println("From Customer ID: " + message.getSenderId());
                            System.out.println("Subject: " + message.getSubject());
                            System.out.println("Message: " + message.getContent());
                            System.out.println("Time: " + new java.util.Date(message.getTimeStamp()));
                            System.out.println("---");
                        }
                    }
                    System.out.println("===============================");
                }
            }
        }));

        broker.registerListener(EventType.STAFF_NOTIFIED_NEW_MESSAGE, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof UserMessage message) {
                if (currentUser != null && currentUser.getRole() == User.Role.STAFF) {
                    System.out.println("\n🔔 New message from Customer ID: " + message.getSenderId());
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("Preview: " + message.getContent().substring(0,
                            Math.min(50, message.getContent().length())) + "...");
                }
            }
        }));

        broker.registerListener(EventType.CUSTOMER_NOTIFIED_NEW_REPLY, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof UserMessage message) {
                if (currentUser != null && currentUser.getId() == message.getRecipientId()) {
                    System.out.println("\n💬 New reply from staff!");
                    System.out.println("Message: " + message.getContent());
                }
            }
        }));

        // Handle item management responses (for staff)
        broker.registerListener(EventType.ITEM_UPDATE_SUCCESS, msg -> CompletableFuture.runAsync(() -> {
            System.out.println("[Main] Item operation completed successfully!");
            Object payload = msg.getPayload();
            if (payload instanceof Item item) {
                System.out.println("Item: " + item.getName() + " | Stock: " + item.getStockQuantity());
            }
        }));

        // Handle reporting responses (for CEO)
        broker.registerListener(EventType.REPORT_DETAILS_RETURNED, msg -> CompletableFuture.runAsync(() -> {
            Object payload = msg.getPayload();
            if (payload instanceof Report report) {
                System.out.println("=== Sales Report ===");
                System.out.println("Report ID: " + report.getId());
                System.out.println("Period: " + report.getReportType());
                System.out.println("Generated: " + report.getDateGenerated());
                System.out.println("Content: " + report.getReportData());
                System.out.println("====================");
            } else {
                System.out.println("Sales report data: " + payload);
            }
        }));

        // Handle notification responses
        broker.registerListener(EventType.NOTIFICATION_SENT, msg -> CompletableFuture.runAsync(() -> {
            System.out.println("[Notification] " + msg.getPayload());
        }));

        broker.registerListener(EventType.NOTIFICATION_FAILED, msg -> CompletableFuture.runAsync(() -> {
            System.out.println("[Notification Failed] " + msg.getPayload());
        }));

        // --------------------------------------------------------------
        // 5. Main loop
        // --------------------------------------------------------------
        while (true) {

            if (currentUser == null)
                showGuestMenu();
            else {
                switch (currentUser.getRole()) {
                    case CUSTOMER -> showCustomerMenu();
                    case STAFF -> showStaffMenu();
                    case CEO -> showCEOMenu();
                }
            }

            System.out.print("> ");

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Q"))
                break;

            handleMenuInput(input);
        }

        shutdown(modules);
    }

    // ============================================================
    // MENU HANDLERS
    // ============================================================

    private static String lastMenu = "GUEST";

    private static void showGuestMenu() {
        lastMenu = "GUEST";
        System.out.println("""
                ----- Welcome -----
                1. Login
                2. Register
                3. Browse Items
                Q. Quit
                """);
    }

    private static void showCustomerMenu() {
        lastMenu = "CUSTOMER";
        System.out.println("""
                ----- Customer -----
                1. Browse Items
                2. Search Items
                3. View Wishlist
                4. Purchase Item
                5. View My Orders
                6. Send Message
                7. View Conversations
                8. View Account
                9. Logout
                Q. Quit
                """);
    }

    private static void showStaffMenu() {
        lastMenu = "STAFF";
        System.out.println("""
                ----- Staff -----
                1. Refill Inventory
                2. Upload Item
                3. Edit Item
                4. View Unread Messages
                5. View All Conversations
                6. Reply to Customer
                7. Logout
                Q. Quit
                """);
    }

    private static void showCEOMenu() {
        lastMenu = "CEO";
        System.out.println("""
                ----- CEO -----
                1. View Sales Report
                2. Logout
                Q. Quit
                """);
    }

    // ============================================================
    // INPUT ROUTING
    // ============================================================

    private static void handleMenuInput(String input) {
        switch (lastMenu) {
            case "GUEST" -> handleGuestInput(input);
            case "CUSTOMER" -> handleCustomerInput(input);
            case "STAFF" -> handleStaffInput(input);
            case "CEO" -> handleCEOInput(input);
        }
    }

    private static void handleGuestInput(String input) {
        switch (input) {
            case "1" -> login();
            case "2" -> register();
            case "3" -> browseItems();
        }
    }

    private static void handleCustomerInput(String input) {
        switch (input) {
            case "1" -> browseItems();
            case "2" -> searchItem();
            case "3" -> viewWishlist();
            case "4" -> purchaseItem();
            case "5" -> viewOrderHistory();
            case "6" -> sendMessage();
            case "7" -> viewConversations();
            case "8" -> viewAccount();
            case "9" -> logout();
        }
    }

    private static void handleStaffInput(String input) {
        switch (input) {
            case "1" -> refillItem();
            case "2" -> uploadItem();
            case "3" -> editItem();
            case "4" -> viewUnreadMessages();
            case "5" -> viewAllConversations();
            case "6" -> replyToCustomer();
            case "7" -> logout();
        }
    }

    private static void handleCEOInput(String input) {
        switch (input) {
            case "1" -> viewSalesReport();
            case "2" -> logout();
        }
    }

    // ============================================================
    // ACTIONS
    // ============================================================

    private static void login() {
        System.out.println("Username/Email:");
        String id = scanner.nextLine();
        System.out.println("Password:");
        String pw = scanner.nextLine();

        broker.publish(EventType.USER_LOGIN_REQUEST,
                new LoginRequest(id, pw));
    }

    private static void register() {
        System.out.println("Username:");
        String username = scanner.nextLine();
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Password:");
        String pw = scanner.nextLine();

        broker.publish(EventType.USER_REGISTER_REQUESTED,
                new RegistrationRequest(username, email, pw, com.entities.User.Role.CUSTOMER, null, null));
    }

    private static void logout() {
        currentUser = null;
        System.out.println("[System] Logged out.");
    }

    private static void browseItems() {
        broker.publish(EventType.ITEM_BROWSE_REQUESTED, new ItemBrowseRequest());
    }

    private static void searchItem() {
        System.out.println("Keyword:");
        String keyword = scanner.nextLine();

        broker.publish(EventType.ITEM_SEARCH_REQUESTED,
                new ItemSearchRequest(keyword));
    }

    private static void viewWishlist() {
        broker.publish(EventType.WISHLIST_VIEW_REQUESTED,
                new WishlistViewRequest(currentUser.getId()));
    }

    private static void purchaseItem() {
        System.out.println("Enter item ID:");
        int itemId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter quantity:");
        int qty = Integer.parseInt(scanner.nextLine());

        OrderCreateRequest.OrderItemRequest orderItem = new OrderCreateRequest.OrderItemRequest(itemId, qty);

        broker.publish(EventType.PURCHASE_REQUESTED,
                new OrderCreateRequest(currentUser.getId(),
                        List.of(orderItem), "Default Address"));
    }

    private static void viewOrderHistory() {
        broker.publish(EventType.ORDER_HISTORY_REQUESTED, currentUser.getId());
    }

    private static void sendMessage() {
        System.out.println("Subject:");
        String subject = scanner.nextLine();
        System.out.println("Message:");
        String content = scanner.nextLine();

        // Send to all staff (recipientId = -1 indicates broadcast to all staff)
        broker.publish(EventType.MESSAGE_SEND_REQUESTED,
                new MessageSendRequest(currentUser.getId(), -1, subject, content));
    }

    private static void viewConversations() {
        broker.publish(EventType.CONVERSATION_LIST_REQUESTED,
                new ConversationListRequest(currentUser.getId()));
    }

    private static void viewAllConversations() {
        broker.publish(EventType.CONVERSATION_LIST_REQUESTED,
                new ConversationListRequest(currentUser.getId()));
    }

    private static void viewAccount() {
        broker.publish(EventType.ACCOUNT_VIEW_REQUESTED,
                new AccountViewRequest(currentUser.getId()));
    }

    private static void refillItem() {
        System.out.println("Item ID:");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("Quantity:");
        int qty = Integer.parseInt(scanner.nextLine());

        broker.publish(EventType.ITEM_REFILL_REQUESTED,
                new ItemEditRequest(id, null, null, null, qty, null));
    }

    private static void uploadItem() {
        System.out.println("Name:");
        String name = scanner.nextLine();

        System.out.println("Description:");
        String description = scanner.nextLine();

        System.out.println("Price:");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.println("Stock:");
        int stock = Integer.parseInt(scanner.nextLine());

        broker.publish(EventType.ITEM_UPLOAD_REQUESTED,
                new ItemUploadRequest(name, description, price, stock));
    }

    private static void editItem() {
        System.out.println("Item ID:");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("New name:");
        String name = scanner.nextLine();

        System.out.println("New description:");
        String desc = scanner.nextLine();

        System.out.println("New price:");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.println("New stock:");
        int stock = Integer.parseInt(scanner.nextLine());

        broker.publish(EventType.ITEM_EDIT_REQUESTED,
                new ItemEditRequest(id, name, desc, price, stock, null));
    }

    private static void viewUnreadMessages() {
        broker.publish(EventType.UNREAD_MESSAGES_REQUESTED,
                new UnreadMessagesRequest(currentUser.getId()));
    }

    private static void replyToCustomer() {
        System.out.println("Customer ID to reply to:");
        int customerId = Integer.parseInt(scanner.nextLine());

        System.out.println("Your reply message:");
        String reply = scanner.nextLine();

        // Staff replying directly to customer (not broadcast)
        broker.publish(EventType.MESSAGE_SEND_REQUESTED,
                new MessageSendRequest(currentUser.getId(), customerId, "Reply", reply));
    }

    private static void viewSalesReport() {
        broker.publish(EventType.REPORT_VIEW_REQUESTED, null);
    }

    // ============================================================
    // SHUTDOWN
    // ============================================================

    private static void shutdown(Subsystems[] modules) {

        System.out.println("[System] Shutting down...");

        for (Subsystems m : modules)
            m.shutdown();

        database.close();
        broker.stop();
        System.out.println("[System] Application terminated.");
        System.exit(0);
    }
}