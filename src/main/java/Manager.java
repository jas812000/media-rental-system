import java.io.*;
import java.util.*;

/**
 * Manages media objects within the Media Rental System.
 *
 * <p>This class is responsible for loading media from files,
 * maintaining the media collection, and coordinating user
 * interactions such as adding, finding, renting, and returning
 * media items.</p>
 */
public class Manager {

    /*
     * Instance variables used by the Manager:
     * mediaList - collection of all loaded media objects
     * directory - directory path used for loading and storing media files
     * fileslist - array of files found in the media directory
     */
    private final ArrayList<Media> mediaList;
    private String directory;
    private File[] fileslist;

    /**
     * Constructs a Manager object and initializes the media list.
     */
    protected Manager() {
        mediaList = new ArrayList<>();
    }

    /**
     * Displays the main menu options for the Media Rental System.
     */
    public void displayMenu() {
        System.out.println("Welcome to the Media Rental System");
        System.out.println();
        System.out.println("1: Add Individual Media Object");
        System.out.println("2: Find Media Object");
        System.out.println("3: Rent Media Object");
        System.out.println("4: Return Media Object");
        System.out.println("5: List Media");
        System.out.println("9: Quit");
        System.out.println();
        System.out.println("Enter your selection: ");
    }

    /**
     * Displays the search menu options used when finding media items.
     */
    public void displayFindMenu() {
        System.out.println("Enter the search parameters: ");
        System.out.println("\t1: Title");
        System.out.println("\t2: ID");
        System.out.println("\t3: Artist");
        System.out.print("Enter your selection: ");
    }

    /**
     * Returns the list of media items currently managed by the system.
     *
     * @return list of media objects
     */
    public ArrayList<Media> getMediaList() {
        return mediaList;
    }

    /**
     * Loads media records from the default data directory.
     *
     * <p>This method delegates to {@link #loadMedia(String)} using
     * the default "./data" directory.</p>
     *
     * @throws IOException if the directory cannot be accessed
     */
    protected void loadMedia() throws IOException {
        loadMedia("./data");
    }

    /**
     * Loads media records from the specified directory into the media list.
     *
     * <p>Each file is expected to contain a single CSV record in the format:
     * <br>id, title, artist, year, isAvail</p>
     *
     * <p>The media type is determined by the file name prefix:
     * eBook*, MovieDVD*, or MusicCD*</p>
     *
     * <p>Malformed or unreadable files are skipped and do not stop processing.</p>
     *
     * @param directory directory path containing media files
     * @throws IOException if the directory cannot be accessed
     */
    protected void loadMedia(String directory) throws IOException {

        // Prevent duplicate loading
        if (!mediaList.isEmpty()) {
            System.out.println(mediaList.size() + " media files have already been loaded.");
            return;
        }

        this.directory = directory;

        File dirPath = new File(directory);
        fileslist = dirPath.listFiles(); // assign to instance field

        if (fileslist == null) {
            System.out.println("Could not load files. Check that the directory exists: " + directory);
            return;
        }

        int count = 0;

        for (File file : fileslist) {

            Media med;

            try (Scanner scan = new Scanner(file)) {

                if (!scan.hasNextLine()) {
                    System.out.println("Skipping empty file: " + file.getName());
                    continue;
                }

                String line = scan.nextLine();
                StringTokenizer st = new StringTokenizer(line, ",");

                if (st.countTokens() < 5) {
                    System.out.println("Skipping malformed file (missing fields): " + file.getName());
                    continue;
                }

                String id = st.nextToken().trim();
                String title = st.nextToken().trim();
                String artist = st.nextToken().trim();
                int year = Integer.parseInt(st.nextToken().trim());
                boolean isAvail = Boolean.parseBoolean(st.nextToken().trim());

                if (file.getName().startsWith("eBook")) {
                    med = new eBook(id, title, artist, year, isAvail);
                } else if (file.getName().startsWith("MovieDVD")) {
                    med = new MovieDVD(id, title, artist, year, isAvail);
                } else if (file.getName().startsWith("MusicCD")) {
                    med = new MusicCD(id, title, artist, year, isAvail);
                } else {
                    System.out.println("Skipping unrecognized media type: " + file.getName());
                    continue;
                }

            } catch (NumberFormatException e) {
                System.out.println("Skipping malformed file (invalid year): " + file.getName());
                continue;
            } catch (RuntimeException e) {
                System.out.println("Skipping malformed file: " + file.getName());
                continue;
            }

            // At this point, med is guaranteed to be non-null
            mediaList.add(med);
            count++;
        }

        if (!mediaList.isEmpty()) {
            System.out.println(count + " media files successfully loaded.");
            System.out.println();
        } else {
            System.out.println("No valid media files were loaded.");
        }
    }

    /**
     * Prompts the user to create a new media item and stores it in the system.
     *
     * <p>The user selects the media type, enters the required metadata,
     * and the new media item is written to disk and added to the media list.</p>
     *
     * @param scan shared Scanner instance for user input
     */
    protected void addMedia(Scanner scan) {

        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        Media med;
        String fileBaseName;

        System.out.println("Enter the media item: \n\t1: eBook\n\t2: MovieDVD\n\t3: MusicCD");
        int mediaChoice = scan.nextInt();
        scan.nextLine(); // consume newline

        System.out.println("Enter the 6 character alphanumeric ID: ");
        String id = scan.nextLine().trim();

        System.out.print("Enter the title: ");
        String title = scan.nextLine().trim();

        System.out.print("Enter the artist name: ");
        String artist = scan.nextLine().trim();

        System.out.print("Enter the year of release: ");
        int year;
        try {
            year = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year entered. Media not added.");
            return;
        }

        // Newly added media is always available
        boolean isAvail = true;

        switch (mediaChoice) {
            case 1 -> {
                fileBaseName = "eBook-" + id;
                med = new eBook(id, title, artist, year, isAvail);
            }
            case 2 -> {
                fileBaseName = "MovieDVD-" + id;
                med = new MovieDVD(id, title, artist, year, isAvail);
            }
            case 3 -> {
                fileBaseName = "MusicCD-" + id;
                med = new MusicCD(id, title, artist, year, isAvail);
            }
            default -> {
                System.out.println("Invalid media type selection.");
                return;
            }
        }

        mediaList.add(med);

        File mediaFile = new File(directory + "/" + fileBaseName + ".txt");

        try {
            if (mediaFile.createNewFile()) {
                System.out.println("File created: " + mediaFile.getName());
            } else {
                System.out.println("File already exists.");
            }

            try (FileOutputStream fs = new FileOutputStream(mediaFile);
                 PrintWriter outFS = new PrintWriter(fs)) {

                outFS.println(
                        med.getId() + "," +
                                med.getTitle() + "," +
                                med.getArtist() + "," +
                                med.getYear() + "," +
                                med.getIsAvail()
                );
            }

            System.out.println("File(s) stored successfully!");

        } catch (IOException e) {
            System.out.println("Unable to create or write the media file.");
        }
    }

    /**
     * Allows the user to search the media list by title, ID, or artist.
     *
     * <p>This method prompts the user for a search type, then scans the
     * media list and prints any matching items.</p>
     */
    protected void findMedia(Scanner scan) {

        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        displayFindMenu();
        int select = scan.nextInt();
        scan.nextLine();

        switch (select) {
            case 1: {
                System.out.print("Enter the title: ");
                String title1 = scan.next();

                for (Media element : mediaList) {
                    if (element.getTitle().toLowerCase().contains(title1.toLowerCase())) {
                        System.out.println();
                        System.out.println("Item Found: " + element);
                    }
                }
                break;
            }
            case 2: {
                System.out.print("Enter the media item id: ");
                String id1 = scan.next();

                for (Media element : mediaList) {
                    if (element.getId().toLowerCase().contains(id1.toLowerCase())) {
                        System.out.println();
                        System.out.println("Item Found: " + element);
                    }
                }
                break;
            }
            case 3: {
                System.out.print("Enter the artist: ");
                String artist1 = scan.next();

                for (Media element : mediaList) {
                    if (element.getArtist().toLowerCase().contains(artist1.toLowerCase())) {
                        System.out.println();
                        System.out.println("Item Found: " + element);
                    }
                }
                break;
            }
            default:
                System.out.println("Please enter a valid menu choice.");
                break;
        }
    }

    /**
     * Prompts the user for a media ID and attempts to rent the matching item.
     *
     * <p>If the item is found and available, the user may confirm the rental.
     * Successful rentals mark the item unavailable and update its backing file.</p>
     *
     * @param scan shared Scanner instance for user input
     */
    protected void rentMedia(Scanner scan) {

        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        System.out.println("Enter the media ID: ");
        String idInput = scan.nextLine().trim();

        MediaSearchResult result = findMediaById(idInput);
        if (result == null) {
            System.out.println("No media item found with ID: " + idInput);
            return;
        }

        Media selected = result.media;
        int selectedIndex = result.index;

        if (!selected.getIsAvail()) {
            System.out.println("Media selected is not available for rental. Please select another item.");
            return;
        }

        while (true) {
            System.out.println("Item available for rental.");
            System.out.println("Would you like to rent this item? \n\t1: Yes \n\t2: No");

            int choice = scan.nextInt();
            scan.nextLine(); // consume newline

            if (choice == 1) {
                selected.setIsAvail(false);

                System.out.println();
                System.out.println("\tItem: " + selected);
                System.out.println("\tItem successfully rented. Rental fee is " + selected.getRentFee() + ".");

                try {
                    saveMediaToFile(selected, selectedIndex);
                    System.out.println("File(s) stored successfully!");
                } catch (IOException e) {
                    System.out.println("Warning: Rental succeeded but file could not be updated.");
                }
                return;

            } else if (choice == 2) {
                System.out.println("You have elected not to rent this item.");
                return;

            } else {
                System.out.println("Please enter a valid menu choice.");
            }
        }
    }

    /**
     * Prompts the user for a media ID and attempts to return the matching item.
     *
     * <p>If the item is found and currently rented, the user may confirm the return.
     * Successful returns mark the item available and update its backing file.</p>
     *
     * @param scan shared Scanner instance for user input
     */
    protected void returnMedia(Scanner scan) {

        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        System.out.println("Enter the media ID: ");
        String idInput = scan.nextLine().trim();

        MediaSearchResult result = findMediaById(idInput);
        if (result == null) {
            System.out.println("No media item found with ID: " + idInput);
            return;
        }

        Media selected = result.media;
        int selectedIndex = result.index;

        if (selected.getIsAvail()) {
            System.out.println("This item is already available. Nothing to return.");
            return;
        }

        while (true) {
            System.out.println("Item available for return.");
            System.out.println("Would you like to return this item? \n\t1: Yes \n\t2: No");

            int choice = scan.nextInt();
            scan.nextLine(); // consume newline

            if (choice == 1) {
                selected.setIsAvail(true);

                System.out.println();
                System.out.println("\tItem: " + selected);
                System.out.println("\tItem successfully returned.");

                try {
                    saveMediaToFile(selected, selectedIndex);
                    System.out.println("File(s) stored successfully!");
                } catch (IOException e) {
                    System.out.println("Warning: Return succeeded but file could not be updated.");
                }
                return;

            } else if (choice == 2) {
                System.out.println("You have elected not to return this item.");
                return;

            } else {
                System.out.println("Please enter a valid menu choice.");
            }
        }
    }

    /**
     * Lists media items based on user-selected filters and sorting.
     *
     * <p>The user can filter by media type (all/movies/ebooks/music),
     * filter by availability (all/available/rented), and choose a sort order.</p>
     *
     * @param scan shared Scanner instance for user input
     */
    protected void listMedia(Scanner scan) {

        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        // ---- Type filter ----
        System.out.println("List media by type:");
        System.out.println("\t1: All Media");
        System.out.println("\t2: Movies");
        System.out.println("\t3: eBooks");
        System.out.println("\t4: Music CDs");
        System.out.print("Enter your selection: ");
        int typeChoice = scan.nextInt();
        scan.nextLine(); // consume newline

        // ---- Availability filter ----
        System.out.println("Filter by availability:");
        System.out.println("\t1: All");
        System.out.println("\t2: Available only");
        System.out.println("\t3: Rented only");
        System.out.print("Enter your selection: ");
        int availChoice = scan.nextInt();
        scan.nextLine(); // consume newline

        // ---- Sorting ----
        System.out.println("Sort by:");
        System.out.println("\t1: Title");
        System.out.println("\t2: Year");
        System.out.println("\t3: ID");
        System.out.print("Enter your selection: ");
        int sortChoice = scan.nextInt();
        scan.nextLine(); // consume newline

        System.out.println("Sort direction:");
        System.out.println("\t1: Ascending");
        System.out.println("\t2: Descending");
        System.out.print("Enter your selection: ");
        int dirChoice = scan.nextInt();
        scan.nextLine(); // consume newline

        // Build filtered list (do not mutate original mediaList)
        ArrayList<Media> filtered = new ArrayList<>();

        for (Media m : mediaList) {

            // Type filter
            boolean typeOk = switch (typeChoice) {
                case 1 -> true;
                case 2 -> m instanceof MovieDVD;
                case 3 -> m instanceof eBook;
                case 4 -> m instanceof MusicCD;
                default -> false;
            };
            if (!typeOk) {
                if (typeChoice < 1 || typeChoice > 4) {
                    System.out.println("Invalid type selection.");
                    return;
                }
                continue;
            }

            // Availability filter
            boolean availOk = switch (availChoice) {
                case 1 -> true;                 // all
                case 2 -> m.getIsAvail();       // available only
                case 3 -> !m.getIsAvail();      // rented only
                default -> false;
            };
            if (!availOk) {
                if (availChoice < 1 || availChoice > 3) {
                    System.out.println("Invalid availability selection.");
                    return;
                }
                continue;
            }

            filtered.add(m);
        }

        if (filtered.isEmpty()) {
            System.out.println("No media items match your filters.");
            return;
        }

        // Sorting comparator
        Comparator<Media> comp = switch (sortChoice) {
            case 1 -> Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER);
            case 2 -> Comparator.comparingInt(Media::getYear);
            case 3 -> Comparator.comparing(Media::getId, String.CASE_INSENSITIVE_ORDER);
            default -> null;
        };

        if (comp == null) {
            System.out.println("Invalid sort selection.");
            return;
        }

        if (dirChoice == 2) {
            comp = comp.reversed();
        } else if (dirChoice != 1) {
            System.out.println("Invalid sort direction selection.");
            return;
        }

        filtered.sort(comp);

        // Display results
        System.out.println();
        for (Media m : filtered) {
            printMediaDetails(m);
        }
        System.out.println();
    }

    /**
     * Writes the given media object's current state back to its backing file.
     *
     * @param media the media object to persist
     * @param index index into the fileslist array that corresponds to the media file
     * @throws IOException if writing fails
     */
    private void saveMediaToFile(Media media, int index) throws IOException {
        if (fileslist == null) {
            throw new IOException("File list is not available. Did you call loadMedia()?");
        }
        if (index < 0 || index >= fileslist.length) {
            throw new IOException("Invalid file index: " + index);
        }

        try (FileOutputStream fs = new FileOutputStream(fileslist[index]);
             PrintWriter outFS = new PrintWriter(fs)) {

            outFS.print(media.getId() + "," +
                    media.getTitle() + "," +
                    media.getArtist() + "," +
                    media.getYear() + "," +
                    media.getIsAvail());
        }
    }
    /**
     * Finds a media item by its ID.
     *
     * @param id media ID to search for
     * @return an array containing the media item and its index, or null if not found
     */
    private MediaSearchResult findMediaById(String id) {
        for (int i = 0; i < mediaList.size(); i++) {
            String currentId = mediaList.get(i).getId();
            if (currentId != null && currentId.equals(id)) {
                return new MediaSearchResult(mediaList.get(i), i);
            }
        }
        return null;
    }

    /**
     * Holds the result of a media search operation.
     */
    private static class MediaSearchResult {
        Media media;
        int index;

        MediaSearchResult(Media media, int index) {
            this.media = media;
            this.index = index;
        }
    }

    /**
     * Prints a media item in a multi-line formatted display.
     *
     * @param media media item to print
     */
    private void printMediaDetails(Media media) {

        if (media == null) {
            System.out.println("======================");
            System.out.println();
            System.out.println("Media Type: Media");
            System.out.println("No media details available.");
            System.out.println();
            System.out.println("======================");
            System.out.println();
            return;
        }

        System.out.println("======================");
        System.out.println();

        switch (media) {
            case MusicCD m -> {
                System.out.println("Media Type: Music (CD)");
                System.out.println("Media ID: " + m.getId());
                System.out.println("Title: " + m.getTitle());
                System.out.println("Performer: " + m.getArtist());
                System.out.println("Year: " + m.getYear());
                System.out.println("Availability: " +
                        (m.getIsAvail() ? "Available" : "Not Available"));
            }
            case MovieDVD m -> {
                System.out.println("Media Type: Movie (DVD)");
                System.out.println("Media ID: " + m.getId());
                System.out.println("Title: " + m.getTitle());
                System.out.println("Actor: " + m.getArtist());
                System.out.println("Year: " + m.getYear());
                System.out.println("Availability: " +
                        (m.getIsAvail() ? "Available" : "Not Available"));
            }
            case eBook m -> {
                System.out.println("Media Type: eBook");
                System.out.println("Media ID: " + m.getId());
                System.out.println("Title: " + m.getTitle());
                System.out.println("Author: " + m.getArtist());
                System.out.println("Year: " + m.getYear());
                System.out.println("Availability: " +
                        (m.getIsAvail() ? "Available" : "Not Available"));
            }
            default -> {
                System.out.println("Media Type: Media");
                System.out.println("Media ID: " + media.getId());
                System.out.println("Title: " + media.getTitle());
                System.out.println("Artist: " + media.getArtist());
                System.out.println("Year: " + media.getYear());
                System.out.println("Availability: " +
                        (media.getIsAvail() ? "Available" : "Not Available"));
            }
        }

        System.out.println();
        System.out.println("======================");
        System.out.println();
    }
}

