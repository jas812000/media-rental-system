/**
 * Represents an electronic book (eBook) media item.
 *
 * <p>An eBook is a type of Media with a fixed daily rental fee.
 * This class sets the rental fee upon construction and provides
 * a formatted string representation of the eBook.</p>
 */
public class eBook extends Media {

    /*
     * Instance variables specific to eBook:
     * EBOOK_FEE - daily rental fee for eBooks
     */
    private static final double EBOOK_FEE = 3.99;

    /**
     * Constructs an eBook object with the specified attributes.
     *
     * @param id     unique alphanumeric identifier
     * @param title  title of the eBook
     * @param artist author of the eBook
     * @param year   year of release
     * @param bool   availability status
     */
    protected eBook(String id, String title, String artist, int year, boolean bool) {
        super(id, title, artist, year, bool);
        setRentFee(EBOOK_FEE);
    }

    /**
     * Returns a formatted string representation of the eBook.
     *
     * @return string describing the eBook
     */
    @Override
    public String toString() {
        return "eBook [ " +
                "id: " + getId() + ", " +
                "Title: " + getTitle() + ", " +
                "Author: " + getArtist() + ", " +
                "Year: " + getYear() + ", " +
                "Availability: " + getIsAvail() +
                " ]";
    }
}

