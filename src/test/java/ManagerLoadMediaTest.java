import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ManagerLoadMediaTest {

    @Test
    void loadMedia_parsesYearWithWhitespace(@TempDir Path tempDir) throws IOException {
        // Arrange: create a media file with whitespace in the year field
        Path mediaFile = tempDir.resolve("MusicCD-TEST01.txt");
        Files.writeString(
                mediaFile,
                "TEST01, Test Album, Test Artist,  2004, true"
        );

        Manager manager = new Manager();

        // Act: load media from temp directory
        manager.loadMedia(tempDir.toString());

        // Assert: media was loaded successfully
        assertEquals(1, manager.getMediaList().size());

        Media media = manager.getMediaList().get(0);
        assertEquals("TEST01", media.getId());
        assertEquals(2004, media.getYear());
        assertTrue(media.getIsAvail());
    }

    @Test
    void loadMedia_routesByFilenamePrefix(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("eBook-EB01.txt"),
                "EB01, Test Book, Test Author, 2001, true");
        Files.writeString(tempDir.resolve("MovieDVD-MV01.txt"),
                "MV01, Test Movie, Test Director, 2002, true");
        Files.writeString(tempDir.resolve("MusicCD-MC01.txt"),
                "MC01, Test Album, Test Artist, 2003, true");

        Manager manager = new Manager();
        manager.loadMedia(tempDir.toString());

        assertEquals(3, manager.getMediaList().size());

        boolean hasEbook = manager.getMediaList().stream().anyMatch(m -> m instanceof eBook);
        boolean hasMovie = manager.getMediaList().stream().anyMatch(m -> m instanceof MovieDVD);
        boolean hasMusic = manager.getMediaList().stream().anyMatch(m -> m instanceof MusicCD);

        assertTrue(hasEbook);
        assertTrue(hasMovie);
        assertTrue(hasMusic);
    }


}

