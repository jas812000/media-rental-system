/**
 * Abstract base class representing a rentable media item.
 *
 * <p>This class defines common attributes and behaviors shared by all
 * media types in the rental system. Subclasses must implement their own
 * string representation.</p>
 */
public abstract class Media {

    /*
     * Instance variables shared by all media types:
     * id        - unique identifier for the media item
     * title     - title of the media item
     * artist    - artist, author, performer, or director
     * year      - year of release
     * isAvail   - availability status of the item
     * rentFee   - rental fee per day
     */
    private String id;
    private String title;
    private String artist;
    private int year;
    private boolean isAvail;
    private double rentFee;

    /**
     * Constructs a Media object with the specified attributes.
     *
     * @param id     unique alphanumeric identifier
     * @param title  title of the media item
     * @param artist artist, author, performer, or director
     * @param year   year of release
     * @param bool   availability status
     */
    protected Media(String id, String title, String artist, int year, boolean bool) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.isAvail = bool;
    }

    /**
     * Returns the media item ID.
     *
     * @return media ID
     */
    protected String getId() {
        return id;
    }

    /**
     * Updates the media item ID.
     *
     * @param id new media ID
     */
    protected void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the title of the media item.
     *
     * @return media title
     */
    protected String getTitle() {
        return title;
    }

    /**
     * Updates the title of the media item.
     *
     * @param title new media title
     */
    protected void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the artist, author, performer, or director.
     *
     * @return artist or creator name
     */
    protected String getArtist() {
        return artist;
    }

    /**
     * Updates the artist or creator of the media item.
     *
     * @param artist new artist or creator name
     */
    protected void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Returns the release year of the media item.
     *
     * @return release year
     */
    protected int getYear() {
        return year;
    }

    /**
     * Updates the release year of the media item.
     *
     * @param year new release year
     */
    protected void setYear(int year) {
        this.year = year;
    }

    /**
     * Indicates whether the media item is available for rental.
     *
     * @return true if available, false otherwise
     */
    protected boolean getIsAvail() {
        return isAvail;
    }

    /**
     * Updates the availability status of the media item.
     *
     * @param bool availability status
     */
    protected void setIsAvail(boolean bool) {
        this.isAvail = bool;
    }

    /**
     * Returns the daily rental fee for the media item.
     *
     * @return rental fee per day
     */
    protected double getRentFee() {
        return rentFee;
    }

    /**
     * Updates the daily rental fee for the media item.
     *
     * @param rentFee rental fee per day
     */
    protected void setRentFee(double rentFee) {
        this.rentFee = rentFee;
    }

    /**
     * Returns a formatted string representation of the media item.
     * Must be implemented by all subclasses.
     *
     * @return formatted media description
     */
    @Override
    public abstract String toString();
}
