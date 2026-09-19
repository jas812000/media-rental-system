package com.jamesstevens.mediarental;

/**
 * Represents an electronic book available for rental.
 */
public class EBook extends Media {

    private static final double EBOOK_FEE = 3.99;

    /**
     * Constructs an electronic book.
     *
     * @param id unique six-character alphanumeric identifier
     * @param title book title
     * @param author author name
     * @param year publication year
     * @param available current availability
     */
    protected EBook(String id, String title, String author, int year,
                    boolean available) {
        super(id, title, author, year, available, EBOOK_FEE);
    }

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
