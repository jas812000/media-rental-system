import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Entry point for the Media Rental System application.
 *
 * <p>This class contains the {@code main} method and is responsible
 * for launching the program, displaying the main menu, and delegating
 * user actions to the {@link Manager} class.</p>
 */
public class MediaRentalSystem {

    /**
     * Main method that starts the Media Rental System.
     *
     * <p>This method displays a menu, accepts user input, and calls
     * appropriate Manager methods based on the selected option.</p>
     *
     * @param args command-line arguments (not used)
     * @throws IOException if an error occurs while loading media files
     * @throws NoSuchElementException if invalid input is encountered
     */
    public static void main(String[] args) throws IOException, NoSuchElementException {

        Manager manager = new Manager();
        manager.loadMedia();

        Scanner scan = new Scanner(System.in);

        while (true) {
            manager.displayMenu();

            if (!scan.hasNextInt()) {
                System.out.println("Please enter a valid menu choice.");
                scan.nextLine(); // discard invalid input
                continue;
            }

            int selection = scan.nextInt();
            scan.nextLine(); // consume newline

            try {
                switch (selection) {
                    case 1:
                        manager.addMedia(scan);
                        break;
                    case 2:
                        manager.findMedia(scan);
                        break;
                    case 3:
                        manager.rentMedia(scan);
                        break;
                    case 4:
                        manager.returnMedia(scan);
                        break;
                    case 5:
                        manager.listMedia(scan);
                        break;
                    case 9:
                        System.out.print("Thank you for using the program. Goodbye!!!");
                        return;
                    default:
                        System.out.println("Please enter a valid menu choice.");
                        break;
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println("An unexpected error occurred.");
            }
        }
    }
}
