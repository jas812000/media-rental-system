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

    @Test
    void loadMedia_parsesValidRecordWithoutWhitespace(@TempDir Path tempDir) throws IOException {
    	Files.writeString(
            	tempDir.resolve("MusicCD-OK01.txt"),
            	"OK01,Album,Artist,1999,true"
    	);
    
    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());
                
    	assertEquals(1, manager.getMediaList().size());
    	assertEquals(1999, manager.getMediaList().get(0).getYear());
    }
                
    @Test   
	void loadMedia_invalidYear_skipsMalformedFile(@TempDir Path tempDir) throws IOException {
    	Files.writeString(
            	tempDir.resolve("MusicCD-BAD01.txt"),
            	"BAD01,Album,Artist,abcd,true"
    	);

    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());

    	// Invalid year should cause the file to be skipped
    	assertEquals(0, manager.getMediaList().size());
    }
     
    @Test
    void loadMedia_missingFields_skipsMalformedFile(@TempDir Path tempDir) throws IOException {
    	Files.writeString(
            	tempDir.resolve("MusicCD-BAD02.txt"),
            	"BAD02,AlbumOnly"
    	);

    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());

    	// Malformed record should be skipped
    	assertEquals(0, manager.getMediaList().size());
    }   
        
    @Test           
    void loadMedia_emptyDirectory_loadsNothing(@TempDir Path tempDir) throws IOException {
    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());
            
    	assertEquals(0, manager.getMediaList().size());
    } 

    @Test
    void rentMedia_availableItem_becomesUnavailable() {
    	Media media = new MusicCD("ID1", "Album", "Artist", 2000, true);
    	media.setIsAvail(false);

    	assertFalse(media.getIsAvail());
    }

    @Test
    void returnMedia_unavailableItem_becomesAvailable() {
    	Media media = new MusicCD("ID2", "Album", "Artist", 2000, false);
    	media.setIsAvail(true);

    	assertTrue(media.getIsAvail());
    }

    @Test
    void rentMedia_alreadyRented_remainsUnavailable() {
    	Media media = new MovieDVD("ID3", "Movie", "Director", 2001, false);
    	media.setIsAvail(false);

    	assertFalse(media.getIsAvail());
    }

    @Test
    void returnMedia_alreadyAvailable_remainsAvailable() {
    	Media media = new eBook("ID4", "Book", "Author", 2002, true);
    	media.setIsAvail(true);

    	assertTrue(media.getIsAvail());
    }

    @Test
    void loadMedia_calledTwice_doesNotDuplicate(@TempDir Path tempDir) throws IOException {
    	Files.writeString(
            	tempDir.resolve("MusicCD-ONCE.txt"),
            	"ONCE,Album,Artist,1995,true"
    	);

    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());
    	manager.loadMedia(tempDir.toString());

    	assertEquals(1, manager.getMediaList().size());
    }

    @Test
    void loadMedia_multipleValidFiles_allLoaded(@TempDir Path tempDir) throws IOException {
    	Files.writeString(tempDir.resolve("MusicCD-A.txt"),
            	"A,Album,Artist,1990,true");
    	Files.writeString(tempDir.resolve("MovieDVD-B.txt"),
            	"B,Movie,Director,1991,true");
    	Files.writeString(tempDir.resolve("eBook-C.txt"),
            	"C,Book,Author,1992,true");

    	Manager manager = new Manager();
    	manager.loadMedia(tempDir.toString());

    	assertEquals(3, manager.getMediaList().size());
    }

}

