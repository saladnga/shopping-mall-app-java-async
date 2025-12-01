package com.ui;

import com.Main;
import com.broker.AsyncMessageBroker;
import com.broker.EventType;

import java.util.List;
import java.util.Scanner;

public class StaffUI {

    public static void showMenu(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.clear();

        UIHelper.box(
                UIHelper.color("STAFF MENU", UIHelper.GREEN),
                List.of(
                        "1. Browse Items",
                        "2. Refill Inventory",
                        "3. Upload Item",
                        "4. Edit Item",
                        "5. Reply to Messages",
                        "6. Logout",
                        "Q. Quit"));

        System.out.print(UIHelper.YELLOW + "Select: " + UIHelper.RESET);
        String input = scanner.nextLine();

        switch (input) {
            case "1" -> browse(scanner, broker);
            case "2" -> refill(scanner, broker);
            case "3" -> upload(scanner, broker);
            case "4" -> edit(scanner, broker);
            case "5" -> reply(scanner, broker);
            case "6" -> Main.currentUser = null;
            case "Q", "q" -> System.exit(0);
            default -> {
                System.out.println(UIHelper.RED + "Invalid option!" + UIHelper.RESET);
                UIHelper.pause();
            }
        }
    }

    public static void browse(Scanner scanner, AsyncMessageBroker broker) {

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
    }

    public static void refill(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.box(
                UIHelper.color("REFILL INVENTORY", UIHelper.GREEN),
                List.of(
                        "Provide the item ID and quantity to restock."));

        Integer id = readPositiveInt(scanner, "Item ID: ");
        if (id == null)
            return;

        Integer qty = readPositiveInt(scanner, "Add Quantity: ");
        if (qty == null)
            return;

        broker.publish(EventType.ITEM_REFILL_REQUESTED, "ID:" + id + ",Qty:" + qty);

        UIHelper.loading("Updating inventory");
        System.out.println(UIHelper.GREEN + "Inventory updated successfully!" + UIHelper.RESET);
        UIHelper.pause();
    }

    public static void upload(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.box(
                UIHelper.color("UPLOAD NEW ITEM", UIHelper.GREEN),
                List.of("Fill out the new item details below."));

        System.out.print("Name: ");
        String name = scanner.nextLine();

        Double price = readPositiveDouble(scanner, "Price: ");
        if (price == null)
            return;

        Integer stock = readPositiveInt(scanner, "Stock: ");
        if (stock == null)
            return;

        broker.publish(EventType.ITEM_UPLOAD_REQUESTED,
                "Name:" + name + ",Price:" + price + ",Stock:" + stock);

        UIHelper.loading("Uploading item");
        System.out.println(UIHelper.GREEN + "Item uploaded!" + UIHelper.RESET);
        UIHelper.pause();
    }

    public static void edit(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.box(
                UIHelper.color("EDIT ITEM", UIHelper.GREEN),
                List.of("Update the item details below."));

        System.out.print("Item ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("New Name: ");
        String name = scanner.nextLine();

        System.out.print("New Description: ");
        String desc = scanner.nextLine();

        System.out.print("New Price: ");
        String price = scanner.nextLine().trim();

        System.out.print("New Stock: ");
        String stock = scanner.nextLine().trim();

        broker.publish(EventType.ITEM_EDIT_REQUESTED,
                "ID:" + id + ",Name:" + name + ",Desc:" + desc + ",Price:" + price + ",Stock:" + stock);

        UIHelper.loading("Saving changes");
        System.out.println(UIHelper.GREEN + "Item updated." + UIHelper.RESET);
        UIHelper.pause();
    }

    public static void reply(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.box(
                UIHelper.color("REPLY TO MESSAGES", UIHelper.GREEN),
                List.of("Load customer messages to respond."));
        UIHelper.loading("Retrieving messages");
        broker.publish(EventType.MESSAGE_REPLY_REQUESTED, null);
        UIHelper.pause();
    }

    private static Integer readPositiveInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0)
                throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException e) {
            System.out.println(UIHelper.RED + "Invalid number." + UIHelper.RESET);
            UIHelper.pause();
            return null;
        }
    }

    private static Double readPositiveDouble(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        try {
            double parsed = Double.parseDouble(value);
            if (parsed <= 0)
                throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException e) {
            System.out.println(UIHelper.RED + "Invalid amount." + UIHelper.RESET);
            UIHelper.pause();
            return null;
        }
    }
}