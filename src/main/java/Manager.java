/*
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



import java.io.*;
import java.util.*;

public class Manager {

    // instance variables (attributes) for this class
    private ArrayList<Media> mediaList;																									// implementation of the ArrayList
    private String directory = null;																									// declaring variable for inputting the menu choice
    File [] fileslist;

    // constructor to initialize the objects
    protected Manager() {
        mediaList = new ArrayList<Media>();																								// constructor creates an empty ArrayList
    }

    // method to display selection, a.k.a. menu
    public void displayMenu() {
        System.out.println("Welcome to the Media Rental System");
        System.out.println();
        System.out.println("1: Add Individual Media Object");
        System.out.println("2: Find Media Object");
        System.out.println("3: Rent Media Object");
        System.out.println("4: Return Media Object");
        System.out.println("9: Quit");
        System.out.println();
        System.out.println("Enter your selection: ");
    }

    // method to display selections to search for items, a.k.a. find menu
    public void displayFindMenu() {
        System.out.println("Enter the search parameters: ");
        System.out.println("\t1: Title");
        System.out.println("\t2: ID");
        System.out.println("\t3: Artist");
        System.out.print("Enter your selection: ");
    }


    public java.util.ArrayList<Media> getMediaList() {
    	return mediaList;   
    }

    protected void loadMedia() throws IOException {
    	loadMedia("./data");
    	}

    // method to open or "load" all the files that contain the media
    protected void loadMedia(String directory) throws IOException {

        if (mediaList.size() > 0) {																										// determines if there are any items in the list. if items are present,......
            System.out.println(mediaList.size() + " media files have been loaded.");													// .....notifies user files have been loaded
            return;
        }
        try {
            this.directory = directory; 																								// object reference: refers to the instance the current constructor is creating
            File dirPath = new File(directory);																							// creates a new file object
            fileslist = dirPath.listFiles();  																							// creates an array (list) of abstract pathnames (files and directories)

            if (fileslist == null) {																									// determines if there are any items in the list. if items are not present,......
                System.out.println("Could not load files");																				// ......notifies the user of absence
                return;
            }

            int count = 0;																										        // initialize the count variable for the counter
            for (File file : fileslist) {																								// for each name (file) in the paths array (directory)

                Media med = null;																										// declaring variable for inputting the files
                Scanner scan = new Scanner(file);																						// constructor to produce value from the file stream
                String line = scan.nextLine();																							// moves the scanner past the current line and returns the input

                StringTokenizer st = new StringTokenizer(line,",");																		// constructor to produce values, creating "tokens" from the string

                String id = st.nextToken().trim();																				// assigns the second value of the of the string
                String title = st.nextToken().trim();																							// assigns the first value of the of the string
                String artist = st.nextToken().trim();
                int year = Integer.parseInt(st.nextToken().trim());																// assigns the third value of the of the string, parsing to integer
                boolean isAvail = Boolean.parseBoolean(st.nextToken().trim());															// converts the forth value to boolean

                if (file.getName().startsWith("eBook"))																					// if the file name begins with "eBook", then......
                    med = new eBook(id, title, artist, year, isAvail);																	//....assigned to eBook group.....
                if (file.getName().startsWith("MovieDVD"))																				// if the file name begins with "MovieDVD", then......
                    med = new MovieDVD(id, title, artist, year, isAvail); 																//....assigned to MovieDVD group.....
                if (file.getName().startsWith("MusicCD"))																				// if the file name begins with "MusicCD", then......
                    med = new MusicCD(id, title, artist, year, isAvail); 																//....it is assigned to MusicCD group.....

                mediaList.add(med);																										// ....then adds the item to the mediaList
                count++;																												// counter enumerates how many files are added

            }
            if (mediaList.size() > 0) {																									// determines if there are any items in the list. if items are present,......
                System.out.println(count + " media files successfully loaded.");														// ......enumerates the files	and notifies user of successful loading of files
                System.out.println();
            }

        }catch(NoSuchElementException e){
            System.out.println("Cannot locate the file(s)!");																			// error message; unable to locate the file
            e.printStackTrace();
        }catch(IllegalStateException e) {
            System.out.println("Scanner was closed. Please select a file.");															// error message; scanner was closed without selecting a file
            e.printStackTrace();
        }catch(NullPointerException e) {
            System.out.println("A path was not entered. Please enter a valid path name");												// error message; pathname is null
            e.printStackTrace();
        }
    }


    // method to add a media item
    protected void addMedia() {

        if (mediaList.size() == 0) {																									// determines if there are any items in the list. if items are present,......
            System.out.println("There are no media files. Please load media.");															// .....notifies user to load files
            return;
        }
        try {
            Media med = null;
            String file8 = null;
            Scanner scan = new Scanner(System.in);																							// constructor to produce value from the input stream

            System.out.println("Enter the media item: \n\t1: eBook\n\t2: MovieDVD\n\t3: MusicCD\n");										// request user to enter requested info
            int media5 = scan.nextInt();																									// accepts user input for menu selection, assigning it to "media5"

            // scanInt() does not read newline when using "Enter", so it will continue to write data in the
            // initial input (media5).To ensure movement to the next line, added "scan.nextLine()
            scan.nextLine();

            System.out.println("Enter the 6 character alphanumeric ID: ");																	// message notifying user to enter requested info
            String id5 = scan.nextLine();																									// accepts user input for menu selection, assigning it to "id5"
            System.out.print("Enter the title: ");																							// message notifying user to enter requested info
            String title5 = scan.nextLine();																								// accepts user input for menu selection, assigning it to "title5"
            System.out.print("Enter the artist name: ");																					// message notifying user to enter requested info
            String artist5 = scan.nextLine();																								// accepts user input for menu selection, assigning it to "artist5"
            System.out.print("Enter the year of release: ");																				// message notifying user to enter requested info
            String year4 = scan.nextLine();																									    // accepts user input for menu selection, assigning it to "year5"
            int year5 = Integer.parseInt(year4);																							// convert string to integer
            String isAvail4 = "true";																										// assigning string as true (available) since it is a new addition
            boolean isAvail5 = Boolean.parseBoolean(isAvail4);    																			// convert the value to boolean
            switch(media5) {
                case 1:
                    file8 = "eBook"+"-"+id5;  																									// filename created for the eBook
                    med = new eBook(id5, title5, artist5, year5, isAvail5);																		//....it is assigned to eBook group.....
                    break;
                case 2:
                    file8 = "MovieDVD"+"-"+id5;  																								// filename created for the MovieDVD
                    med = new MovieDVD(id5, title5, artist5, year5, isAvail5);																	//....it is assigned to MovieDVD group.....
                    break;
                case 3:
                    file8 = "MusicCD"+"-"+id5;  																								// filename created for the MusicCD
                    med = new MusicCD(id5, title5, artist5, year5, isAvail5);																	//....it is assigned to MusicCD group.....
                    break;
                default:
                    break;
            }
            mediaList.add(med);																												// then adds the item to the mediaList
            FileOutputStream fs;
            try {
                File myObj = new File(directory+"/"+file8+".txt");																			// creates a new file
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());																	// notifies user file was created
                }
                else {
                    System.out.println("File already exists.");																			// notifies user of existing file
                }
                fs = new FileOutputStream(myObj);																							// creates an instance of the file output stream with the location of the file
                PrintWriter outFS = new PrintWriter(fs);																					// creates an instance of the PrintWriter
                outFS.println(id5+","+title5+","+artist5+","+year5+","+isAvail5);															// the string to be printed to the file
                outFS.flush();																												// ensures the file is saved
                outFS.close();																												// closes the PrintWriter
                fs.close();																													// closes the file output stream
                System.out.println("File(s) stored successfully!");  																		// notifies user files were stored successfully
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }catch(NoSuchElementException e){
            System.out.println("Cannot locate the file(s)!");																				// error message; unable to locate the file
        }catch(IllegalStateException e) {
            System.out.println("Scanner was closed. Please select a file.");																// error message; scanner was closed without selecting a file
        }catch(NullPointerException e) {
            System.out.println("A path was not entered. Please enter a valid path name");													// error message; pathname is null
        }
        return;
    }


    // method to find media item
    protected void findMedia() {

        if (mediaList.size() == 0) {																									// determines if there are any items in the list. if items are present,......
            System.out.println("There are no media files. Please load media.");															// .....notifies user to load files
            return;
        }
        int select;																														// initialize the input variable
        do {
            Scanner scan = new Scanner(System.in);																						// constructor to produce value from the input stream

            displayFindMenu();																											// method calling displayFindMenu();

            try {
                select = scan.nextInt();																								// accepts user input for menu selection, assigning it to "select"
                switch(select) {																										// the switch statement accepts input of selection
                    case 1:
                        System.out.print("Enter the title: ");																				// message notifying user to enter requested info
                        String title1 = scan.next();																						// accepts user input for menu selection, assigning it to "title1"
                        for (int i = 0; i < mediaList.size(); i++) {																		// loop to search the media list of items
                            // The if statement checks the title of each item in the media list in lower
                            // case contains the same items as the user input converted to lower case
                            if ((mediaList.get(i).getTitle()).toLowerCase().contains(title1.toLowerCase())) {
                                Media item = mediaList.get(i);																				// displays item using toString method
                                System.out.println();																						// prints an empty line for easier reading
                                System.out.println("Item Found: " + item.toString());														// displays item using toString method
                            }
                        }
                        break;
                    case 2:
                        System.out.print("Enter the media item id: ");																		// message notifying user to enter requested info
                        String id1 = scan.next();																							// accepts user input for menu selection, assigning it to "id1"
                        for (int i = 0; i < mediaList.size(); i++) {																		// loop to search the media list of items
                            // The if statement checks the title of each item in the media list in lower
                            // case contains the same items as the user input converted to lower case.
                            if ((mediaList.get(i).getId()).toLowerCase().contains(id1.toLowerCase())) {
                                Media item = mediaList.get(i);																				// items containing requested info assigned to "item"
                                System.out.println();																						// prints an empty line for easier reading
                                System.out.println("Item Found: " + item.toString());														// displays item using toString method
                            }
                        }
                        break;
                    case 3:
                        System.out.print("Enter the artist: ");																				// message notifying user to enter requested info
                        String artist1 = scan.next();																						// accepts user input for menu selection, assigning it to "artist1"
                        for (int i = 0; i < mediaList.size(); i++) {																		// loop to search the media list of items
                            // The if statement checks the title of each item in the media list in lower
                            // case contains the same items as the user input converted to lower case.
                            if ((mediaList.get(i).getArtist()).toLowerCase().contains(artist1.toLowerCase())) {
                                Media item = mediaList.get(i);																				// items containing requested info assigned to "item"
                                System.out.println();																						// prints an empty line for easier reading
                                System.out.println("Item Found: " + item.toString());														// displays item using toString method
                            }
                        }
                        break;
                    default:																												// default option message to user
                        System.out.println("Please enter a valid menu choice.");															// notifies user of invalid option
                        break;
                }
            }catch(NoSuchElementException e){
                System.out.println("Cannot locate the file(s)!");																		// error message; unable to locate the file
            }catch(IllegalStateException e) {
                System.out.println("Scanner was closed. Please select a file.");														// error message; scanner was closed without selecting a file
            }catch(NullPointerException e) {
                System.out.println("A path was not entered. Please enter a valid path name");											// error message; path name is null
            }
            return;
        }while (select != 9);																											// condition that allows exiting the program
    }

    // method to "rent" a media item (make a specific media item unavailable for rental)
    protected void rentMedia() {

        if (mediaList.size() == 0) {																									// determines if there are any items in the list. if items are present,......
            System.out.println("There are no media files. Please load media.");															// .....notifies user to load files
            return;
        }
        try {
            Scanner scan = new Scanner(System.in);																						// constructor to produce value from the input stream
            boolean assign = false;																										// request user to enter directory location or select directory

            System.out.println("Enter the media ID: ");																					// request user to enter directory location or select directory
            String id = scan.nextLine();																								// request user input, stored within variable "id"
            Media dia = null;
            int index1 = 0 ;																												// initialize the variable index1
            for (int a = 0; a < mediaList.size(); a++) {																					// loop searches the mediaList array
                if(mediaList.get(a).getId().equals(id)) {																				// searches the array for the specific item by id
                    dia = mediaList.get(a);																								// assigns the file location of the item
                    index1 = a;																											// the indexed item is assigned
                    boolean avail = mediaList.get(a).getIsAvail();																		// assigns item availability to the boolean variable "avail"
                    if (avail == true) {																								// if the media item is available (true).......
                        int select1 = 0;																								// initialize the variable select1
                        do {																											// enters do-while loop until condition is met
                            System.out.println("Item available for rental.");															// notifies user of availability to rent
                            System.out.println("Would you like to rent this item? \n\t1: Yes \n\t2: No");								// requests if user to decide if they want to rent item
                            select1 = scan.nextInt();																					// request user input for selection
                            switch(select1) {																							// the switch statement accepts input of "select1"
                                case 1:
                                    mediaList.get(a).setIsAvail(false);																		// sets the media item to unavailable (false)
                                    assign = false;																							// assigns false to assign for future use
                                    System.out.println();																					// prints an empty line for easier reading
                                    System.out.println("\tItem: " + mediaList.get(a));														// displays the item being rented
                                    System.out.println("\tItem successfully rented. Rental fee is " + mediaList.get(a).getRentFee()+".");   // Notifies user of successful rental and displays rental fee
                                    break;
                                case 2:
                                    System.out.println("You have elected not to rent this item.");											// user declined to rent item
                                    break;
                                default:
                                    System.out.println("Please enter a valid menu choice.");												// default option message to user
                                    break;
                            }
                        }while (select1 != 1 && select1 != 2);																			// condition to exit the loop
                    }
                    else {
                        System.out.println("Media selected is not available for rental. Please select another item.");					// advises user media item is unavailable for rental
                    }
                }
            }
            if (assign == false) {																										// if boolean assigned is true,......
                FileOutputStream fs;
                try {
                    fs = new FileOutputStream(fileslist[index1]);																		// creates an instance of the file output stream with the location of the file
                    PrintWriter outFS = new PrintWriter(fs);																			// creates an instance of the PrintWriter
                    outFS.print(dia.getId()+","+dia.getTitle()+","+dia.getArtist()+","+dia.getYear()+","+dia.getIsAvail());				// the string to be printed to the file
                    outFS.close();																										// closes the PrintWriter
                    fs.close();																											// closes the file output stream
                    System.out.println("File(s) stored successfully!");  																// notifies user files were stored successfully
                }catch(IOException e){
                    System.out.println("Unable to save file(s)!");																		// error message; unable to save the file
                }catch(NoSuchElementException e){
                    System.out.println("Cannot locate the file(s)!");																	// error message; unable to locate the file
                }catch(IllegalStateException e) {
                    System.out.println("Scanner was closed. Please select a file.");													// error message; scanner was closed without selecting a file
                }catch(NullPointerException e) {
                    System.out.println("No data entered.");																				// error message; no data entered
                }
                return;
            }
            System.out.println();
        }catch(NoSuchElementException e){
            System.out.println("Cannot locate the file(s)!");																			// error message; unable to locate the file
        }catch(IllegalStateException e) {
            System.out.println("Scanner was closed. Please select a file.");															// error message; scanner was closed without selecting a file
        }catch(NullPointerException e) {
            System.out.println("No data entered.");																						// error message; no data entered
        }
        return;
    }

    // method to "return" a media item (make a specific media item available for rental)
    protected void returnMedia() {

        if (mediaList.size() == 0) {																									// determines if there are any items in the list. if items are present,......
            System.out.println("There are no media files. Please load media.");															// .....notifies user to load files
            return;
        }
        try {
            for (int a = 0; a < mediaList.size(); a++) {
                boolean avail = mediaList.get(a).getIsAvail();																			// assigns item availability to the boolean variable "avail"
                if (mediaList.get(a).getIsAvail()) {
                    if (avail == false) {																								// if the media item is not available (false).......
                        System.out.println("Item available for return.");																// notifies user of availability to return
                    }
                }
            }
            Scanner scan = new Scanner(System.in);																						// constructor to produce value from the input stream
            boolean assign = false;																										// request user to enter directory location or select directory

            System.out.println("Enter the media ID: ");																					// request user to enter directory location or select directory
            String id = scan.nextLine();																								// request user input, stored within variable "id"
            Media dia = null;
            int index1 = 0 ;																											// initialize the variable index1
            for (int a = 0; a < mediaList.size(); a++) {																				// loop searches the mediaList array
                if(mediaList.get(a).getId().equals(id)) {																				// searches the array for the specific item by id
                    dia = mediaList.get(a);																								// assigns the file location of the item
                    index1 = a;																											// the indexed item is assigned
                    boolean avail = mediaList.get(a).getIsAvail();																		// assigns item availability to the boolean variable "avail"
                    if (avail == false) {																								// if the media item is not available (false).......
                        int select2 = 0;																								// initialize the variable index2
                        do {																											// enters do-while loop until condition is met
                            System.out.println("Item available for return.");																// notifies user of availability to return
                            System.out.println("Would you like to return this item? \n\t1: Yes \n\t2: No");									// requests if user to decide if they want to return item
                            select2 = scan.nextInt();																						// request user input for selection
                            switch(select2) {																							// the switch statement accepts input of "select1"
                                case 1:
                                    mediaList.get(a).setIsAvail(true);																		// sets the media item to unavailable (false)
                                    assign = true;																							// assigns true to assign for future use
                                    System.out.println();																					// prints an empty line for easier reading
                                    System.out.println("\tItem: " + mediaList.get(a));														// displays the item being rented
                                    System.out.println("\tItem successfully returned" );    												// Notifies user of successful rental return
                                    break;
                                case 2:
                                    System.out.println("You have elected not to return this item.");										// user declined to return item
                                    break;
                                default:
                                    System.out.println("Please enter a valid menu choice.");												// default option message to user
                                    break;
                            }
                        }while (select2 != 1 && select2 != 2);																			// condition to exit the loop
                    }
                    else {
                        System.out.println("There are no media items to be returned.");   												// notifies user that no items are listed to be returned, i.e. nothing was rented
                    }
                }
            }
            if (assign == true) {																										// if boolean assigned is true,......
                FileOutputStream fs;
                try {
                    fs = new FileOutputStream(fileslist[index1]);																		// creates an instance of the file output stream with the location of the file
                    PrintWriter outFS = new PrintWriter(fs);																			// creates an instance of the PrintWriter
                    outFS.print(dia.getId()+","+dia.getTitle()+","+dia.getArtist()+","+dia.getYear()+","+dia.getIsAvail());				// the string to be printed to the file
                    outFS.close();																										// closes the PrintWriter
                    fs.close();																											// closes the file output stream
                    System.out.println("File(s) stored successfully!");  																// notifies user files were stored successfully
                }catch(IOException e){
                    System.out.println("Unable to save file(s)!");																		// error message; unable to save the file
                }catch(NoSuchElementException e){
                    System.out.println("Cannot locate the file(s)!");																	// error message; unable to locate the file
                }catch(IllegalStateException e) {
                    System.out.println("Scanner was closed. Please select a file.");													// error message; scanner was closed without selecting a file
                }catch(NullPointerException e) {
                    System.out.println("A path was not entered. Please enter a valid path name");										// error message; pathname is null
                }
                return;
            }
            System.out.println();
        }catch(NoSuchElementException e){
            System.out.println("Cannot locate the file(s)!");																			// error message; unable to locate the file
        }catch(IllegalStateException e) {
            System.out.println("Scanner was closed. Please select a file.");															// error message; scanner was closed without selecting a file
        }catch(NullPointerException e) {
            System.out.println("A path was not entered. Please enter a valid path name");												// error message; pathname is null
        }
        return;
    }
}

