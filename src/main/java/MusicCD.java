/**
 * Represents a music album stored on CD media.
 *
 * <p>A MusicCD is a type of Media with a fixed daily rental fee.
 * This class sets the rental fee during construction and provides
 * a formatted string representation of the music CD.</p>
 */
public class MusicCD extends Media {

    /*
     * Instance variables specific to MusicCD:
     * MUSIC_CD_FEE - daily rental fee for music CDs
     */
    private static final double MUSIC_CD_FEE = 6.99;

    /**
     * Constructs a MusicCD object with the specified attributes.
     *
     * @param id     unique alphanumeric identifier
     * @param title  title of the music album
     * @param artist performer or band name
     * @param year   year of release
     * @param bool   availability status
     */
    protected MusicCD(String id, String title, String artist, int year, boolean bool) {
        super(id, title, artist, year, bool);
        setRentFee(MUSIC_CD_FEE);
    }

    /**
     * Returns a formatted string representation of the music CD.
     *
     * @return string describing the music CD
     */
    @Override
    public String toString() {
        return "Music (CD) [ " +
                "id: " + getId() + ", " +
                "Title: " + getTitle() + ", " +
                "Performer: " + getArtist() + ", " +
                "Year: " + getYear() + ", " +
                "Availability: " + getIsAvail() +
                " ]";
    }
}
