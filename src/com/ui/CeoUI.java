package com.ui;

import com.Main;

import com.broker.EventType;

import java.util.List;
import java.util.Scanner;

public class CeoUI {

    public static void showMenu(Scanner scanner, AsyncMessageBroker broker) {
        UIHelper.clear();

        UIHelper.box(
            UIHelper.color("CEO MENU", UIHelper.MAGENTA),
            List.of(
                "1. View Daily Reports",
                "2. View Monthly Reports",
                "3. Logout",
                "Q. Quit"
            )
        );

        System.out.print(UIHelper.YELLOW + "Select: " + UIHelper.RESET);
        String input = scanner.nextLine();

        switch (input) {
            case "1" -> viewDailyReports(broker);
            case "2" -> viewMonthlyReports(broker);
            case "3" -> Main.currentUser = null;
            case "Q", "q" -> System.exit(0);
            default -> {
                System.out.println(UIHelper.RED + "Invalid option!" + UIHelper.RESET);
                UIHelper.pause();   
            }
        }
    }


    // ============================================================
    // VIEW DAILY REPORTS
    // ============================================================

    private static void viewDailyReports(AsyncMessageBroker broker) {
        UIHelper.loading("Fetching reports");
        // broker.publish(EventType.REPORT_VIEW_REQUESTED, null);

        // Fake
        List<String> list = Main.reports.getDailyReports();

        System.out.println(UIHelper.CYAN + "--- DAILY REPORTS ---" + UIHelper.RESET);

        if (list.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "No daily reports found." + UIHelper.RESET);
        } else {
            for (String r : list) {
                System.out.println("• " + r);
            }
        }

        UIHelper.pause();
    }

    // ============================================================
    // VIEW MONTHLY REPORTS
    // ============================================================

    private static void viewMonthlyReports(AsyncMessageBroker broker) {
        UIHelper.loading("Fetching reports");
        // broker.publish(EventType.REPORT_VIEW_REQUESTED, null);

        // Fake
        List<String> list = Main.reports.getMonthlyReports();

        System.out.println(UIHelper.CYAN + "--- MONTHLY REPORTS ---" + UIHelper.RESET);

        if (list.isEmpty()) {
            System.out.println(UIHelper.YELLOW + "No monthly reports found." + UIHelper.RESET);
        } else {
            for (String r : list) {
                System.out.println("• " + r);
            }
        }

        UIHelper.pause();
    }
}