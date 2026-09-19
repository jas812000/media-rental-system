package com.jamesstevens.mediarental;

import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point for the Media Rental System.
 */
public final class MediaRentalSystem {

    /**
     * Prevents instantiation because this class provides only the application
     * entry point.
     */
    private MediaRentalSystem() {
    }

    /**
     * Loads the catalog and runs the command-line menu.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Manager manager = new Manager();

        try {
            manager.loadMedia();
        } catch (IOException e) {
            System.out.println("Unable to load the media catalog: " + e.getMessage());
            return;
        }

        Scanner scan = new Scanner(System.in);

        while (true) {
            manager.displayMenu();

            String input = scan.nextLine().trim();
            int selection;

            try {
                selection = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid menu choice.");
                System.out.println();
                continue;
            }

            switch (selection) {
                case 1 -> manager.addMedia(scan);
                case 2 -> manager.findMedia(scan);
                case 3 -> manager.rentMedia(scan);
                case 4 -> manager.returnMedia(scan);
                case 5 -> manager.listMedia(scan);
                case 9 -> {
                    System.out.println("Thank you for using the program. Goodbye!");
                    return;
                }
                default -> System.out.println("Please enter a valid menu choice.");
            }

            System.out.println();
        }
    }
}
