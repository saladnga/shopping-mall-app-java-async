package com.ui;

import com.Main;
import com.broker.AsyncMessageBroker;
import com.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CustomerUI {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void showMenu(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.clear();

        UIHelper.box(
                UIHelper.color("CUSTOMER MENU", UIHelper.CYAN),
                List.of(
                        "1. Browse Items",
                        "2. Search Items",
                        "3. View Wishlist",
                        "4. Purchase Item",
                        "5. View My Orders",
                        "6. Send Message to Staff",
                        "7. View Account",
                        "8. Logout",
                        "Q. Quit"));

        System.out.print(UIHelper.YELLOW + "Select an option: " + UIHelper.RESET);
        String input = scanner.nextLine();

        switch (input) {
            case "1" -> browse(scanner, broker);
            case "2" -> search(scanner, broker);
            case "3" -> viewWishlist(scanner, broker);
            case "4" -> purchase(scanner, broker);
            case "5" -> viewOrders(scanner, broker);
            case "6" -> sendMessage(scanner, broker);
            case "7" -> viewAccount(broker);
            case "8" -> Main.currentUser = null;
            case "Q", "q" -> System.exit(0);
            default -> System.out.println(UIHelper.RED + "Invalid option!" + UIHelper.RESET);
        }
    }

    public static void browse(Scanner scanner, AsyncMessageBroker broker) {
        // broker.publish(EventType.ITEM_BROWSE_REQUESTED, null);
        if (data.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "No items available." + UIHelper.RESET);
            UIHelper.pause();
            return;
        }

        System.out.println(UIHelper.CYAN + "--- ALL ITEMS ---" + UIHelper.RESET);
        for (FakeItemService.Item i : data) {
            List<String> boxLines = List.of(
                    String.format("Description: %s", i.desc),
                    String.format("Price: $%.2f", i.price),
                    String.format("Stock: %d available", i.stock),
                    String.format("Likes: %d", i.likes));

            UIHelper.box(
                    UIHelper.color(String.format("#%d %s", i.id, i.name), UIHelper.GREEN),
                    boxLines);
        }

        while (true) {
            System.out.println(UIHelper.YELLOW + "Actions:" + UIHelper.RESET);
            System.out.println("1. Add to Wishlist");
            System.out.println("2. Like Item");
            System.out.println("3. Purchase");
            System.out.println("4. Back to main menu");
            System.out.print(UIHelper.YELLOW + "Select an option: " + UIHelper.RESET);

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (!requireLoggedInForAction("add items to a wishlist")) {
                        continue;
                    }
                    System.out.print("Enter ItemID to add to Wishlist: ");
                    String idInput = scanner.nextLine().trim();
                    Integer itemId = parseItemId(idInput);
                    if (itemId == null)
                        continue;

                    FakeItemService.Item selected = Main.items.getById(itemId);
                    if (selected == null) {
                        System.out.println(UIHelper.RED + "Item not found." + UIHelper.RESET);
                        continue;
                    }

                    Main.wishlist.addToWishlist(Main.currentUser.getUserName(), itemId);
                    System.out.println(
                            UIHelper.GREEN + "Added '" + selected.name + "' to your wishlist." + UIHelper.RESET);
                    UIHelper.pause();
                    // return;
                }
                case "2" -> {
                    if (!requireLoggedInForAction("like an item")) {
                        continue;
                    }
                    System.out.print("Enter ItemID to make a Like: ");
                    String idInput = scanner.nextLine().trim();
                    Integer itemId = parseItemId(idInput);
                    if (itemId == null)
                        continue;

                    FakeItemService.Item selected = Main.items.getById(itemId);
                    if (selected == null) {
                        System.out.println(UIHelper.RED + "Item not found." + UIHelper.RESET);
                        continue;
                    }

                    boolean liked = Main.items.likeItem(itemId);
                    if (!liked) {
                        System.out.println(UIHelper.RED + "Unable to register like for that item." + UIHelper.RESET);
                        continue;
                    }

                    System.out.println(UIHelper.GREEN + "You liked '" + selected.name + "'." + UIHelper.RESET);
                    UIHelper.pause();
                    // return;
                }
                case "3" -> {
                    if (!requireLoggedInForAction("purchase an item")) {
                        continue;
                    }
                    System.out.print("Enter ItemID to purchase: ");
                    String idInput = scanner.nextLine().trim();
                    Integer itemId = parseItemId(idInput);
                    if (itemId == null)
                        continue;

                    boolean completed = handlePurchaseFlow(scanner, itemId);
                    UIHelper.pause();
                    return;
                }
                case "4" -> {
                    return;
                }
                default -> System.out.println(UIHelper.RED + "Invalid Input" + UIHelper.RESET);
            }
        }
    }

    private static boolean handlePurchaseFlow(Scanner scanner, int itemId) {
        FakeItemService.Item item = Main.items.getById(itemId);

        if (item == null) {
            System.out.println(UIHelper.RED + "Invalid item." + UIHelper.RESET);
            return false;
        }

        System.out.println(UIHelper.CYAN + "--- ITEM DETAILS ---" + UIHelper.RESET);
        System.out.println("Name: " + item.name);
        System.out.println("Description: " + item.desc);
        System.out.printf("Price: $%.2f%n", item.price);
        System.out.println("Likes: " + item.likes);

        while (true) {
            System.out.print("Enter quantity to purchase: ");
            String qtyInput = scanner.nextLine().trim();
            Integer quantity = parseQuantity(qtyInput);
            if (quantity == null)
                continue;

            if (quantity > item.stock) {
                System.out.println(UIHelper.RED + "Insufficient stock available." + UIHelper.RESET);
                continue;
            }

            double total = item.price * quantity;
            System.out.printf("Total Amount: $%.2f%n", total);

            while (true) {
                System.out.println("1. Confirm purchase");
                System.out.println("2. Cancel purchase");
                System.out.print(UIHelper.YELLOW + "Select an option: " + UIHelper.RESET);
                String confirm = scanner.nextLine().trim();

                if ("1".equals(confirm)) {
                    if (attemptPurchase(itemId, quantity)) {
                        return true;
                    }
                    break; // something went wrong, restart quantity loop
                } else if ("2".equals(confirm)) {
                    System.out.println(UIHelper.YELLOW + "Purchase canceled." + UIHelper.RESET);
                    return false;
                } else {
                    System.out.println(UIHelper.RED + "Invalid Input" + UIHelper.RESET);
                }
            }
        }
    }

    private static boolean attemptPurchase(int itemId, int quantity) {
        FakeItemService.Item item = Main.items.getById(itemId);

        if (item == null) {
            System.out.println(UIHelper.RED + "Invalid item." + UIHelper.RESET);
            return false;
        }

        if (quantity <= 0) {
            System.out.println(UIHelper.RED + "Quantity must be at least 1." + UIHelper.RESET);
            return false;
        }

        if (quantity > item.stock) {
            System.out.println(UIHelper.RED + "Insufficient stock available." + UIHelper.RESET);
            return false;
        }

        List<FakeOrderService.OrderItem> orderItems = List.of(
                new FakeOrderService.OrderItem(itemId, item.name, quantity, item.price));
        Main.orders.placeOrder(Main.currentUser.getUserName(), orderItems);

        item.stock -= quantity;

        UIHelper.loading("Processing payment");
        System.out.println(UIHelper.GREEN + "Purchase successful!" + UIHelper.RESET);
        return true;
    }

    private static Integer parseItemId(String idInput) {
        return parsePositiveInt(idInput, "item ID");
    }

    private static Integer parseOrderId(String idInput) {
        return parsePositiveInt(idInput, "order ID");
    }

    private static Integer parseQuantity(String value) {
        return parsePositiveInt(value, "quantity");
    }

    private static Integer parsePositiveInt(String input, String label) {
        try {
            int parsed = Integer.parseInt(input);
            if (parsed <= 0)
                throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException e) {
            System.out.println(UIHelper.RED + "Invalid " + label + "." + UIHelper.RESET);
            return null;
        }
    }

    private static boolean requireLoggedInForAction(String action) {
        if (Main.currentUser != null) {
            return true;
        }
        System.out.println(UIHelper.RED + "Please log in to " + action + "." + UIHelper.RESET);
        return false;
    }

    public static void search(Scanner scanner, AsyncMessageBroker broker) {
        System.out.print("Enter keywords: ");
        String keyword = scanner.nextLine();

        // broker.publish(EventType.ITEM_SEARCH_REQUESTED, keyword);

        // FAKE
        List<FakeItemService.Item> result = Main.items.search(keyword);
        System.out.println(UIHelper.CYAN + "--- SEARCH RESULTS ---" + UIHelper.RESET);
        for (FakeItemService.Item i : result) {
            System.out.printf("[%d] %s - $%.2f\n", i.id, i.name, i.price);
        }

        UIHelper.pause();
    }

    public static void viewWishlist(Scanner scanner, AsyncMessageBroker broker) {
        // UIHelper.loading("Loading wishlist");
        // broker.publish(EventType.WISHLIST_VIEW_REQUESTED, null);

        // FAKE
        List<Integer> ids = Main.wishlist.getWishlist(Main.currentUser.getUserName());
        if (ids.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "Your wishlist is empty." + UIHelper.RESET);
            UIHelper.pause();
            return;
        }

        List<String> boxLines = new ArrayList<>();
        for (int id : ids) {
            FakeItemService.Item i = Main.items.getById(id);
            if (i == null)
                continue;
            boxLines.add(String.format("#%d %s - $%.2f", i.id, i.name, i.price));
        }

        if (boxLines.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "Your wishlist is empty." + UIHelper.RESET);
            UIHelper.pause();
            return;
        }

        UIHelper.box(UIHelper.color("YOUR WISHLIST", UIHelper.CYAN), boxLines);

        while (true) {
            System.out.println(UIHelper.YELLOW + "Actions:" + UIHelper.RESET);
            System.out.println("1. Purchase");
            System.out.println("2. Remove from Wishlist");
            System.out.println("3. Back to main menu");
            System.out.print(UIHelper.YELLOW + "Select an option: " + UIHelper.RESET);

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter ItemID to purchase: ");
                    String idInput = scanner.nextLine().trim();
                    Integer itemId = parseItemId(idInput);
                    if (itemId == null)
                        continue;

                    if (!ids.contains(itemId)) {
                        System.out.println(UIHelper.RED + "That item is not in your wishlist." + UIHelper.RESET);
                        continue;
                    }

                    boolean completed = handlePurchaseFlow(scanner, itemId);
                    if (completed) {
                        Main.wishlist.remove(Main.currentUser.getUserName(), itemId);
                    }
                    UIHelper.pause();
                    return;
                }
                case "2" -> {
                    System.out.print("Enter ItemID to remove from Wishlist: ");
                    String idInput = scanner.nextLine().trim();
                    Integer itemId = parseItemId(idInput);
                    if (itemId == null)
                        continue;

                    if (!ids.contains(itemId)) {
                        System.out.println(UIHelper.RED + "That item is not in your wishlist." + UIHelper.RESET);
                        continue;
                    }

                    FakeItemService.Item removed = Main.items.getById(itemId);
                    Main.wishlist.remove(Main.currentUser.getUserName(), itemId);
                    String removedName = removed != null ? removed.name : ("Item #" + itemId);
                    System.out.println(
                            UIHelper.GREEN + "Removed '" + removedName + "' from your wishlist." + UIHelper.RESET);
                    UIHelper.pause();
                    return;
                }
                case "3" -> {
                    return;
                }
                default -> System.out.println(UIHelper.RED + "Invalid Input" + UIHelper.RESET);
            }
        }
    }

    public static void purchase(Scanner scanner, AsyncMessageBroker broker) {
        // System.out.print("Item ID: ");
        // String itemId = scanner.nextLine();

        // System.out.print("Quantity: ");
        // String quantity = scanner.nextLine();

        // broker.publish(EventType.PURCHASE_REQUESTED,
        // "ItemID:" + itemId + ",Quantity:" + quantity);

        // UIHelper.loading("Processing purchase");

        // FAKE
        System.out.print("Enter ItemID to purchase: ");
        String idInput = scanner.nextLine().trim();
        Integer itemId = parseItemId(idInput);
        if (itemId == null) {
            UIHelper.pause();
            return;
        }

        handlePurchaseFlow(scanner, itemId);
        UIHelper.pause();
    }

    public static void viewOrders(Scanner scanner, AsyncMessageBroker broker) {
        // UIHelper.loading("Loading order history");
        // broker.publish(EventType.ORDER_HISTORY_REQUESTED, null);

        // FAKE
        List<FakeOrderService.Order> orders = Main.orders.getOrdersForUser(Main.currentUser.getUserName());

        if (orders.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "You have no orders." + UIHelper.RESET);
            UIHelper.pause();
            return;
        }

        List<String> summaryLines = new ArrayList<>();
        for (FakeOrderService.Order order : orders) {
            summaryLines.add(String.format("#%d | Total: $%.2f | Status: %s",
                    order.id,
                    order.total,
                    formatStatus(order.status)));
        }

        UIHelper.box(UIHelper.color("YOUR ORDERS", UIHelper.CYAN), summaryLines);

        while (true) {
            System.out.println(UIHelper.YELLOW + "Actions:" + UIHelper.RESET);
            System.out.println("1. View order details");
            System.out.println("2. Back to Customer menu");
            System.out.print(UIHelper.YELLOW + "Select an option: " + UIHelper.RESET);

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter OrderID to view details: ");
                    String orderInput = scanner.nextLine().trim();
                    Integer orderId = parseOrderId(orderInput);
                    if (orderId == null)
                        continue;

                    FakeOrderService.Order order = Main.orders.getOrderById(orderId);
                    if (order == null || !order.username.equals(Main.currentUser.getUserName())) {
                        System.out.println(UIHelper.RED + "Order not found." + UIHelper.RESET);
                        continue;
                    }

                    showOrderDetails(order);
                    return;
                }
                case "2" -> {
                    return;
                }
                default -> System.out.println(UIHelper.RED + "Invalid Input" + UIHelper.RESET);
            }
        }
    }

    private static void showOrderDetails(FakeOrderService.Order order) {
        List<String> detailLines = new ArrayList<>();
        detailLines.add(String.format("Order ID: #%d", order.id));
        detailLines.add(String.format("Order Date: %s", formatOrderDate(order.orderDate)));
        detailLines.add(String.format("Status: %s", formatStatus(order.status)));
        detailLines.add("Items:");

        if (order.items.isEmpty()) {
            detailLines.add("  (No items)");
        } else {
            for (FakeOrderService.OrderItem item : order.items) {
                detailLines.add(String.format("  - #%d %s x%d -> $%.2f",
                        item.itemId,
                        item.itemName,
                        item.quantity,
                        item.getSubTotal()));
            }
        }

        detailLines.add(String.format("Total Amount: $%.2f", order.total));

        UIHelper.box(UIHelper.color("ORDER DETAILS", UIHelper.GREEN), detailLines);
        UIHelper.pause();
    }

    private static String formatOrderDate(LocalDateTime date) {
        if (date == null)
            return "N/A";
        return ORDER_DATE_FORMAT.format(date);
    }

    private static String formatStatus(FakeOrderService.OrderStatus status) {
        if (status == null)
            return "Unknown";
        String value = status.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static void sendMessage(Scanner scanner, AsyncMessageBroker broker) {
        // UIHelper.loading("Opening messaging");
        // broker.publish(EventType.MESSAGE_SEND_REQUESTED, null);

        // FAKE
        System.out.print("Message to staff: ");
        String msg = scanner.nextLine();

        Main.messages.sendMessage(Main.currentUser.getUserName(), "staff", msg);

        System.out.println(UIHelper.GREEN + "Message sent." + UIHelper.RESET);
        UIHelper.pause();
    }

    public static void viewAccount(AsyncMessageBroker broker) {
        // UIHelper.loading("Loading account info");
        // broker.publish(EventType.ACCOUNT_VIEW_REQUESTED, null);

        if (Main.currentUser == null) {
            System.out.println(UIHelper.RED + "No user is currently logged in." + UIHelper.RESET);
            UIHelper.pause();
            return;
        }

        User user = Main.currentUser;
        List<String> info = List.of(
                "User ID: " + user.getID(),
                "Role: " + user.getRole(),
                "Username: " + safeValue(user.getUserName()),
                "Email: " + safeValue(user.getEmail()),
                "Password: " + safeValue(user.getPassword()),
                "Phone: " + safeValue(user.getPhoneNumber()),
                "Address: " + safeValue(user.getAddress()));

        UIHelper.box(UIHelper.color("ACCOUNT INFORMATION", UIHelper.CYAN), info);

        UIHelper.pause();
    }

    private static String safeValue(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}