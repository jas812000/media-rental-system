package com.jamesstevens.mediarental;

/**
 * Represents a movie stored on DVD.
 *
 * <p>Movies retain both the primary actor and director because both values
 * are part of the persisted MovieDVD record.</p>
 */
public class MovieDVD extends Media {

    private static final double MOVIE_DVD_FEE = 6.99;

    private final String director;

    /**
     * Constructs a movie DVD.
     *
     * @param id unique six-character alphanumeric identifier
     * @param title movie title
     * @param actor primary actor
     * @param year release year
     * @param director movie director
     * @param available current availability
     */
    protected MovieDVD(String id, String title, String actor, int year,
                       String director, boolean available) {
        super(id, title, actor, year, available, MOVIE_DVD_FEE);
        this.director = director;
    }

    protected String getDirector() {
        return director;
    }

    @Override
    public String toString() {
        return "Movie (DVD) [ " +
                "id: " + getId() + ", " +
                "Title: " + getTitle() + ", " +
                "Actor: " + getArtist() + ", " +
                "Director: " + director + ", " +
                "Year: " + getYear() + ", " +
                "Availability: " + getIsAvail() +
                " ]";
    }
}
