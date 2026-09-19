package com.jamesstevens.mediarental;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Manages the media catalog and coordinates persistence and CLI operations.
 */
public class Manager {

    private final ArrayList<Media> mediaList;
    private String directory;

    /**
     * Constructs an empty media manager.
     */
    protected Manager() {
        mediaList = new ArrayList<>();
    }

    /**
     * Displays the application's main menu.
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
        System.out.print("Enter your selection: ");
    }

    /**
     * Displays available search criteria.
     */
    public void displayFindMenu() {
        System.out.println("Enter the search parameters:");
        System.out.println("\t1: Title");
        System.out.println("\t2: ID");
        System.out.println("\t3: Creator / Performer / Actor / Director");
        System.out.print("Enter your selection: ");
    }

    /**
     * Returns an unmodifiable view of the managed catalog.
     *
     * @return read-only media list
     */
    public List<Media> getMediaList() {
        return Collections.unmodifiableList(mediaList);
    }

    /**
     * Loads media from the default data directory.
     *
     * @throws IOException if the directory cannot be accessed
     */
    protected void loadMedia() throws IOException {
        loadMedia("./data");
    }

    /**
     * Loads media records from a directory.
     *
     * <p>eBook and MusicCD records contain five fields:
     * id, title, creator, year, availability. MovieDVD records contain six:
     * id, title, actor, year, director, availability.</p>
     *
     * <p>Malformed files are reported and skipped without preventing valid
     * records from loading.</p>
     *
     * @param directory directory containing media files
     * @throws IOException if the directory cannot be accessed
     */
    protected void loadMedia(String directory) throws IOException {
        if (!mediaList.isEmpty()) {
            System.out.println(mediaList.size() + " media files have already been loaded.");
            return;
        }

        File dirPath = new File(directory);
        if (!dirPath.isDirectory()) {
            throw new IOException("Media directory does not exist: " + directory);
        }

        this.directory = directory;

        File[] files = dirPath.listFiles();
        if (files == null) {
            throw new IOException("Unable to access media directory: " + directory);
        }

        Arrays.sort(files, Comparator.comparing(File::getName,
                String.CASE_INSENSITIVE_ORDER));

        int count = 0;

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            try {
                Media media = parseMediaFile(file);

                if (media == null) {
                    continue;
                }

                if (findMediaById(media.getId()) != null) {
                    System.out.println("Skipping duplicate media ID: " + file.getName());
                    continue;
                }

                mediaList.add(media);
                count++;
            } catch (RuntimeException | IOException e) {
                System.out.println("Skipping malformed file: " + file.getName());
            }
        }

        if (count > 0) {
            System.out.println(count + " media files successfully loaded.");
            System.out.println();
        } else {
            System.out.println("No valid media files were loaded.");
        }
    }

    /**
     * Parses one persisted media record.
     *
     * @param file backing media file
     * @return parsed media item, or null for an unsupported file type
     * @throws IOException if the file cannot be read
     */
    private Media parseMediaFile(File file) throws IOException {
        String line = Files.readString(file.toPath()).lines()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Empty media file"));

        String[] fields = line.split(",", -1);
        String name = file.getName();

        if (name.startsWith("MovieDVD-")) {
            if (fields.length != 6) {
                throw new IllegalArgumentException("MovieDVD requires six fields");
            }

            String id = clean(fields[0]);
            String title = clean(fields[1]);
            String actor = clean(fields[2]);
            int year = parseYear(fields[3]);
            String director = clean(fields[4]);
            boolean available = parseBoolean(fields[5]);

            validateCommon(id, title, actor, year);
            validateText(director, "Director");
            validateFileName(file, "MovieDVD", id);

            return new MovieDVD(id, title, actor, year, director, available);
        }

        if (name.startsWith("eBook-") || name.startsWith("MusicCD-")) {
            if (fields.length != 5) {
                throw new IllegalArgumentException("Media record requires five fields");
            }

            String id = clean(fields[0]);
            String title = clean(fields[1]);
            String artist = clean(fields[2]);
            int year = parseYear(fields[3]);
            boolean available = parseBoolean(fields[4]);

            validateCommon(id, title, artist, year);

            if (name.startsWith("eBook-")) {
                validateFileName(file, "eBook", id);
                return new EBook(id, title, artist, year, available);
            }

            validateFileName(file, "MusicCD", id);
            return new MusicCD(id, title, artist, year, available);
        }

        System.out.println("Skipping unrecognized media type: " + name);
        return null;
    }

    /**
     * Prompts for and persists a new media item.
     *
     * @param scan shared scanner
     */
    protected void addMedia(Scanner scan) {
        if (!hasStorageDirectory()) {
            System.out.println("Media storage is not available. Please load media first.");
            return;
        }

        System.out.println("Enter the media item:");
        System.out.println("\t1: eBook");
        System.out.println("\t2: MovieDVD");
        System.out.println("\t3: MusicCD");

        Integer mediaChoice = readInteger(scan, "Enter your selection: ");
        if (mediaChoice == null || mediaChoice < 1 || mediaChoice > 3) {
            System.out.println("Invalid media type selection.");
            return;
        }

        System.out.print("Enter the 6 character alphanumeric ID: ");
        String id = scan.nextLine().trim().toUpperCase(Locale.ROOT);

        if (!isValidId(id)) {
            System.out.println("Invalid ID. Enter exactly 6 alphanumeric characters.");
            return;
        }

        if (findMediaById(id) != null) {
            System.out.println("A media item with that ID already exists.");
            return;
        }

        System.out.print("Enter the title: ");
        String title = normalizeText(scan.nextLine());

        if (!isValidText(title)) {
            System.out.println("Title cannot be blank or contain commas.");
            return;
        }

        String creatorLabel = switch (mediaChoice) {
            case 1 -> "author";
            case 2 -> "actor";
            default -> "performer";
        };

        System.out.print("Enter the " + creatorLabel + " name: ");
        String artist = normalizeText(scan.nextLine());

        if (!isValidText(artist)) {
            System.out.println(capitalize(creatorLabel)
                    + " cannot be blank or contain commas.");
            return;
        }

        Integer year = readInteger(scan, "Enter the year of release: ");
        if (year == null || !isValidYear(year)) {
            System.out.println("Invalid year entered. Media not added.");
            return;
        }

        String director = null;
        if (mediaChoice == 2) {
            System.out.print("Enter the director name: ");
            director = normalizeText(scan.nextLine());

            if (!isValidText(director)) {
                System.out.println("Director cannot be blank or contain commas.");
                return;
            }
        }

        Media media = switch (mediaChoice) {
            case 1 -> new EBook(id, title, artist, year, true);
            case 2 -> new MovieDVD(id, title, artist, year, director, true);
            case 3 -> new MusicCD(id, title, artist, year, true);
            default -> throw new IllegalStateException("Unexpected media type");
        };

        try {
            File mediaFile = getMediaFile(media);

            if (mediaFile.exists()) {
                System.out.println("A media file with that ID already exists.");
                return;
            }

            saveMediaToFile(media);
            mediaList.add(media);
            System.out.println("Media added and stored successfully.");
        } catch (IOException e) {
            System.out.println("Unable to create or write the media file.");
        }
    }

    /**
     * Searches by title, ID, or creator information.
     *
     * @param scan shared scanner
     */
    protected void findMedia(Scanner scan) {
        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        displayFindMenu();
        Integer selection = readInteger(scan, "");

        if (selection == null || selection < 1 || selection > 3) {
            System.out.println("Please enter a valid menu choice.");
            return;
        }

        String prompt = switch (selection) {
            case 1 -> "Enter the title: ";
            case 2 -> "Enter the media item ID: ";
            default -> "Enter the creator, performer, actor, or director: ";
        };

        System.out.print(prompt);
        String query = scan.nextLine().trim().toLowerCase(Locale.ROOT);

        if (query.isBlank()) {
            System.out.println("Search value cannot be blank.");
            return;
        }

        boolean found = false;

        for (Media media : mediaList) {
            boolean match = switch (selection) {
                case 1 -> media.getTitle().toLowerCase(Locale.ROOT).contains(query);
                case 2 -> media.getId().toLowerCase(Locale.ROOT).contains(query);
                case 3 -> creatorMatches(media, query);
                default -> false;
            };

            if (match) {
                System.out.println();
                System.out.println("Item Found: " + media);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No media items matched your search.");
        }
    }

    /**
     * Rents an available media item and persists the state change.
     *
     * @param scan shared scanner
     */
    protected void rentMedia(Scanner scan) {
        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        System.out.print("Enter the media ID: ");
        String idInput = scan.nextLine().trim();

        Media selected = findMediaById(idInput);

        if (selected == null) {
            System.out.println("No media item found with ID: " + idInput);
            return;
        }

        if (!selected.getIsAvail()) {
            System.out.println("Media selected is not available for rental.");
            return;
        }

        System.out.println("Item available for rental.");
        Integer choice = readInteger(scan,
                "Would you like to rent this item?\n\t1: Yes\n\t2: No\nEnter your selection: ");

        if (choice == null || (choice != 1 && choice != 2)) {
            System.out.println("Please enter a valid menu choice.");
            return;
        }

        if (choice == 2) {
            System.out.println("You have elected not to rent this item.");
            return;
        }

        selected.setIsAvail(false);

        try {
            saveMediaToFile(selected);
            System.out.println();
            System.out.println("\tItem: " + selected);
            System.out.printf("\tItem successfully rented. Rental fee is $%.2f.%n",
                    selected.getRentFee());
            System.out.println("File stored successfully.");
        } catch (IOException e) {
            selected.setIsAvail(true);
            System.out.println("Rental could not be completed because the media file could not be updated.");
        }
    }

    /**
     * Returns a rented media item and persists the state change.
     *
     * @param scan shared scanner
     */
    protected void returnMedia(Scanner scan) {
        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        System.out.print("Enter the media ID: ");
        String idInput = scan.nextLine().trim();

        Media selected = findMediaById(idInput);

        if (selected == null) {
            System.out.println("No media item found with ID: " + idInput);
            return;
        }

        if (selected.getIsAvail()) {
            System.out.println("This item is already available. Nothing to return.");
            return;
        }

        Integer choice = readInteger(scan,
                "Would you like to return this item?\n\t1: Yes\n\t2: No\nEnter your selection: ");

        if (choice == null || (choice != 1 && choice != 2)) {
            System.out.println("Please enter a valid menu choice.");
            return;
        }

        if (choice == 2) {
            System.out.println("You have elected not to return this item.");
            return;
        }

        selected.setIsAvail(true);

        try {
            saveMediaToFile(selected);
            System.out.println();
            System.out.println("\tItem: " + selected);
            System.out.println("\tItem successfully returned.");
            System.out.println("File stored successfully.");
        } catch (IOException e) {
            selected.setIsAvail(false);
            System.out.println("Return could not be completed because the media file could not be updated.");
        }
    }

    /**
     * Lists media using type, availability, sorting, and direction filters.
     *
     * @param scan shared scanner
     */
    protected void listMedia(Scanner scan) {
        if (mediaList.isEmpty()) {
            System.out.println("There are no media files. Please load media.");
            return;
        }

        System.out.println("List media by type:");
        System.out.println("\t1: All Media");
        System.out.println("\t2: Movies");
        System.out.println("\t3: eBooks");
        System.out.println("\t4: Music CDs");
        Integer typeChoice = readInteger(scan, "Enter your selection: ");

        if (typeChoice == null || typeChoice < 1 || typeChoice > 4) {
            System.out.println("Invalid type selection.");
            return;
        }

        System.out.println("Filter by availability:");
        System.out.println("\t1: All");
        System.out.println("\t2: Available only");
        System.out.println("\t3: Rented only");
        Integer availChoice = readInteger(scan, "Enter your selection: ");

        if (availChoice == null || availChoice < 1 || availChoice > 3) {
            System.out.println("Invalid availability selection.");
            return;
        }

        System.out.println("Sort by:");
        System.out.println("\t1: Title");
        System.out.println("\t2: Year");
        System.out.println("\t3: ID");
        Integer sortChoice = readInteger(scan, "Enter your selection: ");

        if (sortChoice == null || sortChoice < 1 || sortChoice > 3) {
            System.out.println("Invalid sort selection.");
            return;
        }

        System.out.println("Sort direction:");
        System.out.println("\t1: Ascending");
        System.out.println("\t2: Descending");
        Integer directionChoice = readInteger(scan, "Enter your selection: ");

        if (directionChoice == null || (directionChoice != 1 && directionChoice != 2)) {
            System.out.println("Invalid sort direction selection.");
            return;
        }

        ArrayList<Media> filtered = new ArrayList<>();

        for (Media media : mediaList) {
            boolean typeMatches = switch (typeChoice) {
                case 1 -> true;
                case 2 -> media instanceof MovieDVD;
                case 3 -> media instanceof EBook;
                case 4 -> media instanceof MusicCD;
                default -> false;
            };

            boolean availabilityMatches = switch (availChoice) {
                case 1 -> true;
                case 2 -> media.getIsAvail();
                case 3 -> !media.getIsAvail();
                default -> false;
            };

            if (typeMatches && availabilityMatches) {
                filtered.add(media);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No media items match your filters.");
            return;
        }

        Comparator<Media> comparator = switch (sortChoice) {
            case 1 -> Comparator.comparing(Media::getTitle,
                    String.CASE_INSENSITIVE_ORDER);
            case 2 -> Comparator.comparingInt(Media::getYear);
            case 3 -> Comparator.comparing(Media::getId,
                    String.CASE_INSENSITIVE_ORDER);
            default -> throw new IllegalStateException("Unexpected sort option");
        };

        if (directionChoice == 2) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);

        System.out.println();
        for (Media media : filtered) {
            printMediaDetails(media);
        }
    }

    /**
     * Persists a media item using the schema appropriate to its media type.
     *
     * @param media media item to persist
     * @throws IOException if persistence fails
     */
    private void saveMediaToFile(Media media) throws IOException {
        File mediaFile = getMediaFile(media);

        try (FileOutputStream stream = new FileOutputStream(mediaFile);
             PrintWriter writer = new PrintWriter(stream)) {

            if (media instanceof MovieDVD movie) {
                writer.print(movie.getId() + "," +
                        movie.getTitle() + "," +
                        movie.getArtist() + "," +
                        movie.getYear() + "," +
                        movie.getDirector() + "," +
                        movie.getIsAvail());
            } else {
                writer.print(media.getId() + "," +
                        media.getTitle() + "," +
                        media.getArtist() + "," +
                        media.getYear() + "," +
                        media.getIsAvail());
            }

            if (writer.checkError()) {
                throw new IOException("Unable to write media file");
            }
        }
    }

    /**
     * Builds the backing file path for a media item.
     *
     * @param media media item
     * @return backing file
     * @throws IOException if storage is unavailable
     */
    private File getMediaFile(Media media) throws IOException {
        if (!hasStorageDirectory()) {
            throw new IOException("Media directory is not available.");
        }

        String prefix = switch (media) {
            case EBook ignored -> "eBook";
            case MovieDVD ignored -> "MovieDVD";
            case MusicCD ignored -> "MusicCD";
            default -> throw new IOException(
                    "Unsupported media type: " + media.getClass().getSimpleName());
        };

        return new File(directory, prefix + "-" + media.getId() + ".txt");
    }

    /**
     * Finds a media item by ID without case sensitivity.
     *
     * @param id ID to locate
     * @return matching media item, or null
     */
    private Media findMediaById(String id) {
        for (Media media : mediaList) {
            if (media.getId().equalsIgnoreCase(id)) {
                return media;
            }
        }
        return null;
    }

    /**
     * Determines whether creator-oriented search text matches a media item.
     */
    private boolean creatorMatches(Media media, String query) {
        if (media.getArtist().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }

        return media instanceof MovieDVD movie
                && movie.getDirector().toLowerCase(Locale.ROOT).contains(query);
    }

    /**
     * Prints a formatted media record.
     */
    private void printMediaDetails(Media media) {
        System.out.println("======================");
        System.out.println();

        if (media instanceof MusicCD music) {
            System.out.println("Media Type: Music (CD)");
            System.out.println("Media ID: " + music.getId());
            System.out.println("Title: " + music.getTitle());
            System.out.println("Performer: " + music.getArtist());
            System.out.println("Year: " + music.getYear());
        } else if (media instanceof MovieDVD movie) {
            System.out.println("Media Type: Movie (DVD)");
            System.out.println("Media ID: " + movie.getId());
            System.out.println("Title: " + movie.getTitle());
            System.out.println("Actor: " + movie.getArtist());
            System.out.println("Director: " + movie.getDirector());
            System.out.println("Year: " + movie.getYear());
        } else if (media instanceof EBook book) {
            System.out.println("Media Type: eBook");
            System.out.println("Media ID: " + book.getId());
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getArtist());
            System.out.println("Year: " + book.getYear());
        }

        System.out.println("Availability: " +
                (media.getIsAvail() ? "Available" : "Not Available"));
        System.out.println();
        System.out.println("======================");
        System.out.println();
    }

    /**
     * Reads an integer from one complete input line.
     */
    private Integer readInteger(Scanner scan, String prompt) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }

        String value = scan.nextLine().trim();

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validates shared persisted media fields.
     */
    private void validateCommon(String id, String title, String artist, int year) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Invalid ID");
        }
        validateText(title, "Title");
        validateText(artist, "Creator");
        if (!isValidYear(year)) {
            throw new IllegalArgumentException("Invalid year");
        }
    }

    /**
     * Validates a text field used by the simple comma-delimited persistence format.
     */
    private void validateText(String value, String fieldName) {
        if (!isValidText(value)) {
            throw new IllegalArgumentException(fieldName + " is invalid");
        }
    }

    private boolean isValidText(String value) {
        return value != null && !value.isBlank() && !value.contains(",");
    }

    private boolean isValidId(String id) {
        return id != null && id.matches("[A-Za-z0-9]{6}");
    }

    private boolean isValidYear(int year) {
        return year >= 1000 && year <= 9999;
    }

    private int parseYear(String value) {
        return Integer.parseInt(clean(value));
    }

    private boolean parseBoolean(String value) {
        String cleaned = clean(value);
        if ("true".equalsIgnoreCase(cleaned)) {
            return true;
        }
        if ("false".equalsIgnoreCase(cleaned)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid availability");
    }

    private String clean(String value) {
        return value.trim();
    }

    /**
     * Verifies that a persisted record's ID agrees with its backing filename.
     */
    private void validateFileName(File file, String prefix, String id) {
        String expected = prefix + "-" + id + ".txt";
        if (!file.getName().equals(expected)) {
            throw new IllegalArgumentException("Filename does not match media ID");
        }
    }

    private boolean hasStorageDirectory() {
        return directory != null
                && !directory.isBlank()
                && new File(directory).isDirectory();
    }

    /**
     * Normalizes user-entered text for consistent display and persistence.
     * Leading and trailing whitespace is removed, repeated whitespace is
     * collapsed, and words are stored with consistent capitalization.
     *
     * @param value text entered by the user
     * @return normalized text
     */
    private String normalizeText(String value) {
        String cleaned = value.trim().replaceAll("\\s+", " ");

        if (cleaned.isEmpty()) {
            return cleaned;
        }

        String[] words = cleaned.toLowerCase(Locale.ROOT).split(" ");
        StringBuilder normalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (!normalized.isEmpty()) {
                    normalized.append(' ');
                }

                normalized.append(Character.toUpperCase(word.charAt(0)));

                if (word.length() > 1) {
                    normalized.append(word.substring(1));
                }
            }
        }

        return normalized.toString();
    }

    /**
     * Capitalizes the first character of a label used in validation messages.
     *
     * @param value label to capitalize
     * @return label with an uppercase first character
     */
    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

}
