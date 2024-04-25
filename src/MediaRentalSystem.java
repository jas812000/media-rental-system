/*
 * CMIS 242 7380
 *
 * Version 1.0
 *
 * 14 December 2022
 *
 * © 2022, James Stevens, All rights reserved.
 *
 * This program, Media Rental System, gives the user the ability to rent media items (eBook, MovieDVD, MusicCD). The user can
 * search for items by ID, title or artist. The user can also rent the media item. The function to return the item is also
 * available. The files are initially uploaded before all options can be utilized.
 *
 */


//package Media_Rental_System;

//import application programming interfaces
import java.io.*;
import java.util.*;

public class MediaRentalSystem {

    public static void main(String[] args) throws IOException, NoSuchElementException {

        Manager man = new Manager();														// Instantiation of the Manager class
        int selection = 0;																	// declaring variable for inputting the menu choice
        Scanner scan = new Scanner(System.in);												// scanner to read input
        //man.displayMenu();
        /*
         * The do-while statement will enter the loop automatically. The intent is to
         * display the menu after completion of the task in a selection. To exit the
         * loop the user must select "9". This ends the program.
         * Of note, an additional case was added (Return Media Object) not specified
         * in the project instructions. The rationale: after renting a media object, the
         * ability to return it must be available.
         *
         */

        do {
            man.displayMenu();																// calling method from Manager class to display menu
            selection = scan.nextInt();														// accepts user input for menu selection, assigning it to "selection"
            try {
                switch (selection) {													// the switch statement accepts input of selection
                    case 1: 																// "Load Media Objects": selecting will lead to the load media cascade
                        man.loadMedia();													// calls the loadMedia() method
                        break;
                    case 2: 																// "Add Individual Media Object": selecting will lead to the add media cascade
                        man.addMedia();														// calls the addMedia() method
                        break;
                    case 3: 																// "Find Media Object": selecting will lead to the find media cascade
                        man.findMedia();													// calls the findMedia() method
                        break;
                    case 4: 																// "Rent Media Object": selecting will lead to the rent media cascade
                        man.rentMedia();													// calls the rentMedia() method
                        break;
                    case 5: 																// "Return Media Object": selecting will lead to the return media cascade
                        man.returnMedia();													// calls the returnMedia() method
                        break;
                    case 9: 																// Exits the program
                        System.out.print("Thank you for using the program. Goodbye!!!"); 	// message notifying user of exiting program
                        System.exit(0);														// method that exits current program by terminating Java virtual machine
                        break;
                    default:
                        System.out.println("Please enter a valid menu choice.");			// notifies user of invalid option
                        break;
                }
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (selection != 9);  															// condition to exit the program

    }
}


