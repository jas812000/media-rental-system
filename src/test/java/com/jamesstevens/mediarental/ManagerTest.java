package com.jamesstevens.mediarental;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Media Rental System manager, including loading, validation,
 * searching, adding, renting, returning, filtering, sorting, and persistence.
 */
class ManagerTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream testOutput;

    /**
     * Suppresses expected CLI output so successful test runs remain readable.
     */
    @BeforeEach
    void suppressConsoleOutput() {
        originalOut = System.out;
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
    }

    /**
     * Restores standard output after each test.
     */
    @AfterEach
    void restoreConsoleOutput() {
        System.setOut(originalOut);
    }

    /**
     * Verifies that valid eBook, MovieDVD, and MusicCD records load into
     * their correct subclasses and retain their persisted values.
     */
    @Test
    void loadMedia_validRecords_loadsAllTypes(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("eBook-BOOK01.txt"),
                "BOOK01,Test Book,Test Author,2001,true");
        Files.writeString(tempDir.resolve("MovieDVD-MOV001.txt"),
                "MOV001,Test Movie,Test Actor,2002,Test Director,true");
        Files.writeString(tempDir.resolve("MusicCD-MUSIC1.txt"),
                "MUSIC1,Test Album,Test Artist,2003,false");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(3, manager.getMediaList().size());

        EBook book = (EBook) find(manager, "BOOK01");
        MovieDVD movie = (MovieDVD) find(manager, "MOV001");
        MusicCD music = (MusicCD) find(manager, "MUSIC1");

        assertNotNull(book);
        assertEquals("Test Author", book.getArtist());
        assertTrue(book.getIsAvail());

        assertNotNull(movie);
        assertEquals("Test Actor", movie.getArtist());
        assertEquals("Test Director", movie.getDirector());
        assertTrue(movie.getIsAvail());

        assertNotNull(music);
        assertEquals("Test Artist", music.getArtist());
        assertFalse(music.getIsAvail());
    }

    /**
     * Verifies whitespace surrounding persisted values is ignored.
     */
    @Test
    void loadMedia_whitespace_trimsFields(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("MusicCD-TEST01.txt"),
                " TEST01 , Test Album , Test Artist , 2004 , true ");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        Media media = manager.getMediaList().getFirst();

        assertEquals("TEST01", media.getId());
        assertEquals("Test Album", media.getTitle());
        assertEquals("Test Artist", media.getArtist());
        assertEquals(2004, media.getYear());
        assertTrue(media.getIsAvail());
    }

    /**
     * Verifies an empty directory produces an empty catalog.
     */
    @Test
    void loadMedia_emptyDirectory_loadsNothing(@TempDir Path tempDir) throws IOException {
        Manager manager = new Manager();

        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies a missing media directory is reported as an I/O failure.
     */
    @Test
    void loadMedia_missingDirectory_throwsIOException(@TempDir Path tempDir) {
        Manager manager = new Manager();
        Path missing = tempDir.resolve("missing");

        assertThrows(IOException.class,
                () -> manager.loadMedia(missing.toString()));
    }

    /**
     * Verifies malformed records do not prevent valid records from loading.
     */
    @Test
    void loadMedia_malformedRecords_skipsInvalidFiles(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MusicCD-GOOD01.txt"),
                "GOOD01,Good Album,Good Artist,2000,true");
        Files.writeString(tempDir.resolve("MusicCD-BAD001.txt"),
                "BAD001,Album,Artist,not-a-year,true");
        Files.writeString(tempDir.resolve("MusicCD-BAD002.txt"),
                "BAD002,Album");
        Files.writeString(tempDir.resolve("MusicCD-BAD003.txt"),
                "BAD003,Album,Artist,2000,maybe");
        Files.writeString(tempDir.resolve("MovieDVD-BAD004.txt"),
                "BAD004,Movie,Actor,2000,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(1, manager.getMediaList().size());
        assertEquals("GOOD01", manager.getMediaList().getFirst().getId());
    }

    /**
     * Verifies malformed MovieDVD records with too many fields are rejected.
     */
    @Test
    void loadMedia_movieWithExtraField_skipsRecord(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MovieDVD-MOV001.txt"),
                "MOV001,Movie,Actor,2000,Quentin,Tarantino,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies IDs must contain exactly six alphanumeric characters.
     */
    @Test
    void loadMedia_invalidId_skipsRecord(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("MusicCD-BAD!.txt"),
                "BAD!,Album,Artist,2000,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies filenames must agree with the media ID stored in the record.
     */
    @Test
    void loadMedia_filenameIdMismatch_skipsRecord(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MusicCD-ABC123.txt"),
                "XYZ789,Album,Artist,2000,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies unsupported files are ignored.
     */
    @Test
    void loadMedia_unrecognizedFile_skipsRecord(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("Other-ABC123.txt"),
                "ABC123,Title,Creator,2000,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies subdirectories in the data directory are ignored.
     */
    @Test
    void loadMedia_subdirectory_ignoresDirectory(@TempDir Path tempDir)
            throws IOException {

        Files.createDirectory(tempDir.resolve("MusicCD-ABC123.txt"));

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies loading an already populated manager does not duplicate records.
     */
    @Test
    void loadMedia_calledTwice_doesNotDuplicate(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());
        manager.loadMedia(tempDir.toString());

        assertEquals(1, manager.getMediaList().size());
    }

    /**
     * Verifies duplicate IDs are rejected even when different media files
     * attempt to use the same identifier.
     */
    @Test
    void loadMedia_duplicateIds_loadsOnlyOne(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MusicCD-ABC123.txt"),
                "ABC123,Album,Artist,2000,true");
        Files.writeString(tempDir.resolve("eBook-ABC123.txt"),
                "ABC123,Book,Author,2001,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(1, manager.getMediaList().size());
    }

    /**
     * Verifies callers cannot structurally modify the manager's media list.
     */
    @Test
    void getMediaList_returnsUnmodifiableList(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertThrows(UnsupportedOperationException.class,
                () -> manager.getMediaList().clear());
    }

    /**
     * Verifies an eBook can be added to an initially empty catalog once its
     * storage directory has been initialized.
     */
    @Test
    void addMedia_emptyCatalog_addsAndPersistsEBook(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "1", "NEW001", "New Book", "New Author", "2020")));

        assertTrue(output.contains("Media Added:"));
        assertEquals(1, manager.getMediaList().size());

        Path file = tempDir.resolve("eBook-NEW001.txt");
        assertTrue(Files.exists(file));
        assertEquals("NEW001,New Book,New Author,2020,true",
                Files.readString(file));
    }

    /**
     * Verifies a MovieDVD persists actor and director as separate fields.
     */
    @Test
    void addMedia_movie_persistsActorAndDirector(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.addMedia(scanner(
                "2",
                "MOV001",
                "New Movie",
                "Lead Actor",
                "2021",
                "Film Director"));

        MovieDVD movie = (MovieDVD) find(manager, "MOV001");

        assertNotNull(movie);
        assertEquals("Lead Actor", movie.getArtist());
        assertEquals("Film Director", movie.getDirector());
        assertEquals(
                "MOV001,New Movie,Lead Actor,2021,Film Director,true",
                Files.readString(tempDir.resolve("MovieDVD-MOV001.txt")));
    }

    /**
     * Verifies a MusicCD can be added and persisted.
     */
    @Test
    void addMedia_music_persistsRecord(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.addMedia(scanner(
                "3", "MUS001", "New Album", "New Artist", "2022"));

        assertNotNull(find(manager, "MUS001"));
        assertEquals(
                "MUS001,New Album,New Artist,2022,true",
                Files.readString(tempDir.resolve("MusicCD-MUS001.txt")));
    }

    /**
     * Verifies duplicate IDs are rejected without overwriting existing data.
     */
    @Test
    void addMedia_duplicateId_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "ABC123", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String original = Files.readString(tempDir.resolve("MusicCD-ABC123.txt"));

        String output = capture(() -> manager.addMedia(scanner(
                "1", "abc123")));

        assertTrue(output.contains("already exists"));
        assertEquals(1, manager.getMediaList().size());
        assertEquals(original,
                Files.readString(tempDir.resolve("MusicCD-ABC123.txt")));
    }

    /**
     * Verifies invalid media type input is rejected cleanly.
     */
    @Test
    void addMedia_invalidType_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner("9")));

        assertTrue(output.contains("Please select a media type from the menu."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies nonnumeric media type input is rejected cleanly.
     */
    @Test
    void addMedia_nonNumericType_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner("abc")));

        assertTrue(output.contains("Please select a media type from the menu."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies invalid IDs are rejected.
     */
    @Test
    void addMedia_invalidId_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner("1", "BAD!")));

        assertTrue(output.contains("Please enter a valid 6-character media ID using letters and numbers only."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies invalid year input is rejected.
     */
    @Test
    void addMedia_invalidYear_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "1", "BOOK01", "Book", "Author", "abcd")));

        assertTrue(output.contains("Please enter a valid release year between 1000 and "));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies blank or comma-containing metadata is rejected because the
     * persistence format is comma-delimited without quoting.
     */
    @Test
    void addMedia_invalidText_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "3", "MUS001", "Album, Deluxe", "Artist", "2020")));

        assertTrue(output.contains("Please enter a title without commas."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies new media input is normalized before being stored in memory
     * and persisted to disk.
     */
    @Test
    void addMedia_normalizesNewEntry(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.addMedia(scanner(
                "2",
                "tst002",
                "  test   movie  ",
                "  test   actor  ",
                "2023",
                "  test   director  "));

        MovieDVD movie = (MovieDVD) find(manager, "TST002");

        assertNotNull(movie);
        assertEquals("TST002", movie.getId());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("Test Actor", movie.getArtist());
        assertEquals("Test Director", movie.getDirector());

        Path file = tempDir.resolve("MovieDVD-TST002.txt");

        assertTrue(Files.exists(file));
        assertEquals(
                "TST002,Test Movie,Test Actor,2023,Test Director,true",
                Files.readString(file));
    }

    /**
     * Verifies an invalid title is rejected immediately without consuming
     * creator or year input.
     */
    @Test
    void addMedia_invalidTitle_rejectsImmediately(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "3",
                "TST003",
                "Test Album, Deluxe",
                "SHOULD_NOT_BE_READ",
                "2022")));

        assertTrue(output.contains("Please enter a title without commas."));
        assertFalse(output.contains("performer name"));
        assertFalse(output.contains("year of release"));
        assertTrue(manager.getMediaList().isEmpty());
        assertFalse(Files.exists(tempDir.resolve("MusicCD-TST003.txt")));
    }

    /**
     * Verifies invalid creator input is rejected before the year is requested.
     */
    @Test
    void addMedia_invalidCreator_rejectsImmediately(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "1",
                "TST001",
                "Test Book",
                "Bad, Author",
                "2024")));

        assertTrue(output.contains("Please enter an author name without commas."));
        assertFalse(output.contains("year of release"));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies invalid director input is rejected without creating a movie.
     */
    @Test
    void addMedia_invalidDirector_rejectsImmediately(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "2",
                "TST002",
                "Test Movie",
                "Test Actor",
                "2023",
                "Bad, Director")));

        assertTrue(output.contains("Please enter a director name without commas."));
        assertTrue(manager.getMediaList().isEmpty());
        assertFalse(Files.exists(tempDir.resolve("MovieDVD-TST002.txt")));
    }

    /**
     * Verifies multi-word title searches are supported.
     */
    @Test
    void findMedia_multiWordTitle_findsMatch(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("eBook-BOOK01.txt"),
                "BOOK01,Kitchen Confidential,Anthony Bourdain,2001,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.findMedia(scanner("1", "Kitchen Confidential")));

        assertTrue(output.contains("Item Found"));
        assertTrue(output.contains("Kitchen Confidential"));
    }

    /**
     * Verifies creator search matches a MovieDVD director.
     */
    @Test
    void findMedia_director_findsMovie(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MovieDVD-MOV001.txt"),
                "MOV001,Movie,Actor,2000,Quentin Tarantino,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.findMedia(scanner("3", "Quentin Tarantino")));

        assertTrue(output.contains("Item Found"));
        assertTrue(output.contains("Director: Quentin Tarantino"));
    }

    /**
     * Verifies a search with no matching records reports that result.
     */
    @Test
    void findMedia_noMatch_reportsNoResults(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.findMedia(scanner("1", "Missing Title")));

        assertTrue(output.contains("No media items matched your search."));
    }

    /**
     * Verifies renting an available item changes memory and persisted state.
     */
    @Test
    void rentMedia_availableItem_updatesMemoryAndFile(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.rentMedia(scanner("music1", "1"));

        Media media = find(manager, "MUSIC1");

        assertNotNull(media);
        assertFalse(media.getIsAvail());
        assertTrue(Files.readString(tempDir.resolve("MusicCD-MUSIC1.txt"))
                .endsWith(",false"));
    }

    /**
     * Verifies declining a rental leaves the item available.
     */
    @Test
    void rentMedia_declined_leavesItemAvailable(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.rentMedia(scanner("MUSIC1", "2"));

        assertTrue(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies an unavailable item cannot be rented again.
     */
    @Test
    void rentMedia_alreadyRented_remainsUnavailable(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.rentMedia(scanner("MUSIC1")));

        assertTrue(output.contains("not available for rental"));
        assertFalse(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies a missing rental ID is reported.
     */
    @Test
    void rentMedia_unknownId_reportsNotFound(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.rentMedia(scanner("ABC123")));

        assertTrue(output.contains("No media item found"));
    }

    /**
     * Verifies a failed rental persistence operation rolls back availability.
     */
    @Test
    void rentMedia_persistenceFailure_rollsBackState(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        Files.delete(tempDir.resolve("MusicCD-MUSIC1.txt"));
        Files.delete(tempDir);

        manager.rentMedia(scanner("MUSIC1", "1"));

        assertTrue(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies returning a rented item changes memory and persisted state.
     */
    @Test
    void returnMedia_rentedItem_updatesMemoryAndFile(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.returnMedia(scanner("music1", "1"));

        Media media = find(manager, "MUSIC1");

        assertNotNull(media);
        assertTrue(media.getIsAvail());
        assertTrue(Files.readString(tempDir.resolve("MusicCD-MUSIC1.txt"))
                .endsWith(",true"));
    }

    /**
     * Verifies declining a return leaves the item rented.
     */
    @Test
    void returnMedia_declined_leavesItemRented(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        manager.returnMedia(scanner("MUSIC1", "2"));

        assertFalse(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies returning an already available item does not change its state.
     */
    @Test
    void returnMedia_alreadyAvailable_remainsAvailable(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.returnMedia(scanner("MUSIC1")));

        assertTrue(output.contains("already available"));
        assertTrue(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies failed return persistence rolls availability back.
     */
    @Test
    void returnMedia_persistenceFailure_rollsBackState(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        Files.delete(tempDir.resolve("MusicCD-MUSIC1.txt"));
        Files.delete(tempDir);

        manager.returnMedia(scanner("MUSIC1", "1"));

        assertFalse(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies media listing filters by type and availability.
     */
    @Test
    void listMedia_filtersByTypeAndAvailability(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("eBook-BOOK01.txt"),
                "BOOK01,Book Title,Author,2000,true");
        Files.writeString(tempDir.resolve("MovieDVD-MOV001.txt"),
                "MOV001,Movie Title,Actor,2001,Director,false");
        Files.writeString(tempDir.resolve("MusicCD-MUSIC1.txt"),
                "MUSIC1,Album Title,Artist,2002,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("2", "3", "1", "1")));

        assertTrue(output.contains("Movie Title"));
        assertFalse(output.contains("Book Title"));
        assertFalse(output.contains("Album Title"));
    }

    /**
     * Verifies media listing sorts results in descending year order.
     */
    @Test
    void listMedia_sortsDescendingByYear(@TempDir Path tempDir)
            throws IOException {

        Files.writeString(tempDir.resolve("MusicCD-OLD001.txt"),
                "OLD001,Older,Artist,1990,true");
        Files.writeString(tempDir.resolve("MusicCD-NEW001.txt"),
                "NEW001,Newer,Artist,2020,true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("1", "1", "2", "2")));

        assertTrue(output.indexOf("Newer") < output.indexOf("Older"));
    }

    /**
     * Verifies invalid list filter input exits cleanly.
     */
    @Test
    void listMedia_invalidType_reportsError(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("9")));

        assertTrue(output.contains("Please select a media type from the menu."));
    }


    /**
     * Verifies a future release year is rejected rather than being stored.
     */
    @Test
    void addMedia_futureYear_rejectsAddition(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        int futureYear = java.time.Year.now().getValue() + 1;

        String output = capture(() -> manager.addMedia(scanner(
                "1",
                "BOOK01",
                "Future Book",
                "Test Author",
                Integer.toString(futureYear))));

        assertTrue(output.contains(
                "Please enter a valid release year between 1000 and "
                        + java.time.Year.now().getValue() + "."));
        assertTrue(manager.getMediaList().isEmpty());
        assertFalse(Files.exists(tempDir.resolve("eBook-BOOK01.txt")));
    }

    /**
     * Verifies successful additions display the normalized media that was
     * actually stored.
     */
    @Test
    void addMedia_success_displaysNormalizedMedia(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "1",
                "tst001",
                "  testing   java  ",
                "  james   stevens  ",
                "2025")));

        assertTrue(output.contains("Media Added:"));
        assertTrue(output.contains("ID: TST001"));
        assertTrue(output.contains("Title: Testing Java"));
        assertTrue(output.contains("Author: James Stevens"));
        assertTrue(output.contains("Year: 2025"));
        assertTrue(output.contains("Available: Yes"));
        assertFalse(output.contains("File stored successfully."));
    }

    /**
     * Verifies blank titles receive a specific user-facing validation message.
     */
    @Test
    void addMedia_blankTitle_reportsFriendlyMessage(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.addMedia(scanner("1", "BOOK01", "   ")));

        assertTrue(output.contains("Please enter a title."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies blank creator names receive a specific user-facing message.
     */
    @Test
    void addMedia_blankCreator_reportsFriendlyMessage(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "1", "BOOK01", "Test Book", "   ")));

        assertTrue(output.contains("Please enter an author name."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies blank movie directors receive a specific validation message.
     */
    @Test
    void addMedia_blankDirector_reportsFriendlyMessage(@TempDir Path tempDir)
            throws IOException {

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.addMedia(scanner(
                "2",
                "MOV001",
                "Test Movie",
                "Test Actor",
                "2024",
                "   ")));

        assertTrue(output.contains("Please enter a director name."));
        assertTrue(manager.getMediaList().isEmpty());
    }

    /**
     * Verifies a blank rental ID is handled separately from an unknown ID.
     */
    @Test
    void rentMedia_blankId_reportsFriendlyMessage(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.rentMedia(scanner("   ")));

        assertTrue(output.contains("Please enter a media item ID."));
        assertFalse(output.contains("No media item found with ID:"));
        assertTrue(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies a successful rental uses the structured media display and
     * reports the rental fee without exposing persistence details.
     */
    @Test
    void rentMedia_success_displaysStructuredResult(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.rentMedia(scanner("music1", "1")));

        assertTrue(output.contains("Item Rented:"));
        assertTrue(output.contains("Music (CD)"));
        assertTrue(output.contains("ID: MUSIC1"));
        assertTrue(output.contains("Title: Test Album"));
        assertTrue(output.contains("Performer: Test Artist"));
        assertTrue(output.contains("Available: No"));
        assertTrue(output.contains("Rental fee: $6.99"));
        assertFalse(output.contains("File stored successfully."));
    }

    /**
     * Verifies a blank return ID is handled separately from an unknown ID.
     */
    @Test
    void returnMedia_blankId_reportsFriendlyMessage(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() -> manager.returnMedia(scanner("   ")));

        assertTrue(output.contains("Please enter a media item ID."));
        assertFalse(output.contains("No media item found with ID:"));
        assertFalse(find(manager, "MUSIC1").getIsAvail());
    }

    /**
     * Verifies a successful return uses the structured media display without
     * exposing persistence implementation details.
     */
    @Test
    void returnMedia_success_displaysStructuredResult(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", false);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.returnMedia(scanner("music1", "1")));

        assertTrue(output.contains("Item Returned:"));
        assertTrue(output.contains("Music (CD)"));
        assertTrue(output.contains("ID: MUSIC1"));
        assertTrue(output.contains("Title: Test Album"));
        assertTrue(output.contains("Performer: Test Artist"));
        assertTrue(output.contains("Available: Yes"));
        assertFalse(output.contains("Rental fee:"));
        assertFalse(output.contains("File stored successfully."));
    }

    /**
     * Verifies browsing reports the result count and structured media fields.
     */
    @Test
    void listMedia_results_displayCountAndStructuredFields(
            @TempDir Path tempDir) throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("4", "1", "1", "1")));

        assertTrue(output.contains("1 Music CD Found"));
        assertTrue(output.contains("Music (CD)"));
        assertTrue(output.contains("ID: MUSIC1"));
        assertTrue(output.contains("Performer: Test Artist"));
        assertTrue(output.contains("Available: Yes"));
    }

    /**
     * Verifies invalid availability filtering receives a friendly message.
     */
    @Test
    void listMedia_invalidAvailability_reportsError(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("1", "9")));

        assertTrue(output.contains(
                "Please select an availability option from the menu."));
    }

    /**
     * Verifies invalid sort-field input receives a friendly message.
     */
    @Test
    void listMedia_invalidSort_reportsError(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("1", "1", "9")));

        assertTrue(output.contains(
                "Please select a sort option from the menu."));
    }

    /**
     * Verifies invalid sort-direction input receives a friendly message.
     */
    @Test
    void listMedia_invalidDirection_reportsError(@TempDir Path tempDir)
            throws IOException {

        writeMusic(tempDir, "MUSIC1", true);

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        String output = capture(() ->
                manager.listMedia(scanner("1", "1", "1", "9")));

        assertTrue(output.contains(
                "Please select a sort direction from the menu."));
    }

    /**
     * Finds a media object in a manager for test assertions.
     */
    private Media find(Manager manager, String id) {
        return manager.getMediaList()
                .stream()
                .filter(media -> media.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a Scanner containing one line for each supplied CLI input.
     */
    private Scanner scanner(String... lines) {
        return new Scanner(String.join(System.lineSeparator(), lines)
                + System.lineSeparator());
    }

    /**
     * Creates a valid MusicCD backing file for tests that do not depend on
     * custom record contents.
     */
    private void writeMusic(Path directory, String id, boolean available)
            throws IOException {

        Files.writeString(directory.resolve("MusicCD-" + id + ".txt"),
                id + ",Test Album,Test Artist,2000," + available);
    }

    /**
     * Captures console output generated during one manager operation.
     */
    private String capture(Runnable action) {
        testOutput.reset();
        action.run();
        return testOutput.toString();
    }
}
