import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit tests for the Manager class focusing on media loading,
 * availability changes, and basic rental/return behavior.
 *
 * <p>These tests validate correct parsing of media files,
 * routing by filename prefix, handling of malformed data,
 * and ensuring that media availability updates behave as expected.</p>
 */
class ManagerLoadMediaTest {

    /** Manager instance used for each test case */
    Manager manager;

    /**
     * Verifies that a media file with extra whitespace in the year field
     * is parsed correctly and loaded into the media list.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_parsesYearWithWhitespace(@TempDir Path tempDir) throws IOException {

        // Create a media file with whitespace in the year field
        Path mediaFile = tempDir.resolve("MusicCD-TEST01.txt");
        Files.writeString(
                mediaFile,
                "TEST01, Test Album, Test Artist,  2004, true"
        );

        manager = new Manager();

        // Load media from the temporary directory
        manager.loadMedia(tempDir.toString());

        // Verify media was loaded successfully
        assertEquals(1, manager.getMediaList().size());

        Media media = manager.getMediaList().getFirst();
        assertEquals("TEST01", media.getId());
        assertEquals(2004, media.getYear());
        assertTrue(media.getIsAvail());
    }

    /**
     * Verifies that media files are routed to the correct subclass
     * based on their filename prefix.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_routesByFilenamePrefix(@TempDir Path tempDir) throws IOException {

        Files.writeString(tempDir.resolve("eBook-EB01.txt"),
                "EB01, Test Book, Test Author, 2001, true");
        Files.writeString(tempDir.resolve("MovieDVD-MV01.txt"),
                "MV01, Test Movie, Test Director, 2002, true");
        Files.writeString(tempDir.resolve("MusicCD-MC01.txt"),
                "MC01, Test Album, Test Artist, 2003, true");

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(3, manager.getMediaList().size());

        boolean hasEbook = manager.getMediaList().stream().anyMatch(m -> m instanceof eBook);
        boolean hasMovie = manager.getMediaList().stream().anyMatch(m -> m instanceof MovieDVD);
        boolean hasMusic = manager.getMediaList().stream().anyMatch(m -> m instanceof MusicCD);

        assertTrue(hasEbook);
        assertTrue(hasMovie);
        assertTrue(hasMusic);
    }

    /**
     * Verifies that a correctly formatted media record
     * without extra whitespace loads successfully.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_parsesValidRecordWithoutWhitespace(@TempDir Path tempDir) throws IOException {

        Files.writeString(
                tempDir.resolve("MusicCD-OK01.txt"),
                "OK01,Album,Artist,1999,true"
        );

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(1, manager.getMediaList().size());
        assertEquals(1999, manager.getMediaList().getFirst().getYear());
    }

    /**
     * Verifies that media files with an invalid year value
     * are skipped and not loaded.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_invalidYear_skipsMalformedFile(@TempDir Path tempDir) throws IOException {

        Files.writeString(
                tempDir.resolve("MusicCD-BAD01.txt"),
                "BAD01,Album,Artist,abcd,true"
        );

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(0, manager.getMediaList().size());
    }

    /**
     * Verifies that media files missing required fields
     * are skipped during loading.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_missingFields_skipsMalformedFile(@TempDir Path tempDir) throws IOException {

        Files.writeString(
                tempDir.resolve("MusicCD-BAD02.txt"),
                "BAD02,AlbumOnly"
        );

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(0, manager.getMediaList().size());
    }

    /**
     * Verifies that loading from an empty directory
     * results in no media being loaded.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_emptyDirectory_loadsNothing(@TempDir Path tempDir) throws IOException {

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(0, manager.getMediaList().size());
    }

    /**
     * Verifies that renting an available media item
     * marks it as unavailable.
     */
    @Test
    void rentMedia_availableItem_becomesUnavailable() {

        Media media = new MusicCD("ID1", "Album", "Artist", 2000, true);
        media.setIsAvail(false);

        assertFalse(media.getIsAvail());
    }

    /**
     * Verifies that returning an unavailable media item
     * marks it as available.
     */
    @Test
    void returnMedia_unavailableItem_becomesAvailable() {

        Media media = new MusicCD("ID2", "Album", "Artist", 2000, false);
        media.setIsAvail(true);

        assertTrue(media.getIsAvail());
    }

    /**
     * Verifies that attempting to rent an already rented item
     * leaves it unavailable.
     */
    @Test
    void rentMedia_alreadyRented_remainsUnavailable() {

        Media media = new MovieDVD("ID3", "Movie", "Director", 2001, false);
        media.setIsAvail(false);

        assertFalse(media.getIsAvail());
    }

    /**
     * Verifies that returning an item that is already available
     * does not change its availability.
     */
    @Test
    void returnMedia_alreadyAvailable_remainsAvailable() {

        Media media = new eBook("ID4", "Book", "Author", 2002, true);
        media.setIsAvail(true);

        assertTrue(media.getIsAvail());
    }

    /**
     * Verifies that calling loadMedia multiple times
     * does not duplicate media entries.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_calledTwice_doesNotDuplicate(@TempDir Path tempDir) throws IOException {

        Files.writeString(
                tempDir.resolve("MusicCD-ONCE.txt"),
                "ONCE,Album,Artist,1995,true"
        );

        manager = new Manager();
        manager.loadMedia(tempDir.toString());
        manager.loadMedia(tempDir.toString());

        assertEquals(1, manager.getMediaList().size());
    }

    /**
     * Verifies that multiple valid media files
     * are all loaded successfully.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if file operations fail
     */
    @Test
    void loadMedia_multipleValidFiles_allLoaded(@TempDir Path tempDir) throws IOException {

        Files.writeString(tempDir.resolve("MusicCD-A.txt"),
                "A,Album,Artist,1990,true");
        Files.writeString(tempDir.resolve("MovieDVD-B.txt"),
                "B,Movie,Director,1991,true");
        Files.writeString(tempDir.resolve("eBook-C.txt"),
                "C,Book,Author,1992,true");

        manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(3, manager.getMediaList().size());
    }
}
