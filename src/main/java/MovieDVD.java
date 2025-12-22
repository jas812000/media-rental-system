/**
 * Represents a movie stored on DVD media.
 *
 * <p>A MovieDVD is a type of Media with a fixed daily rental fee.
 * This class initializes the rental fee upon construction and
 * provides a formatted string representation of the movie.</p>
 */
public class MovieDVD extends Media {

    /*
     * Instance variables specific to MovieDVD:
     * MOVIE_DVD_FEE - daily rental fee for movie DVDs
     */
    private static final double MOVIE_DVD_FEE = 6.99;

    /**
     * Constructs a MovieDVD object with the specified attributes.
     *
     * @param id     unique alphanumeric identifier
     * @param title  title of the movie
     * @param artist director or primary actor
     * @param year   year of release
     * @param bool   availability status
     */
    protected MovieDVD(String id, String title, String artist, int year, boolean bool) {
        super(id, title, artist, year, bool);
        setRentFee(MOVIE_DVD_FEE);
    }

    /**
     * Returns a formatted string representation of the movie DVD.
     *
     * @return string describing the movie DVD
     */
    @Override
    public String toString() {
        return "Movie (DVD) [ " +
                "id: " + getId() + ", " +
                "Title: " + getTitle() + ", " +
                "Actor: " + getArtist() + ", " +
                "Year: " + getYear() + ", " +
                "Availability: " + getIsAvail() +
                " ]";
    }
}
