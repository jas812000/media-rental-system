package com.jamesstevens.mediarental;

/**
 * Abstract base class representing a rentable media item.
 *
 * <p>Each media item has immutable descriptive metadata and a rental fee.
 * Availability is mutable because renting and returning an item changes its
 * current state.</p>
 */
public abstract class Media {

    private final String id;
    private final String title;
    private final String artist;
    private final int year;
    private boolean available;
    private final double rentalFee;

    /**
     * Constructs a media item.
     *
     * @param id unique six-character alphanumeric identifier
     * @param title media title
     * @param artist author, performer, or primary actor
     * @param year release year
     * @param available current availability
     * @param rentalFee daily rental fee
     */
    protected Media(String id, String title, String artist, int year,
                    boolean available, double rentalFee) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.available = available;
        this.rentalFee = rentalFee;
    }

    protected String getId() {
        return id;
    }

    protected String getTitle() {
        return title;
    }

    protected String getArtist() {
        return artist;
    }

    protected int getYear() {
        return year;
    }

    protected boolean getIsAvail() {
        return available;
    }

    /**
     * Changes availability when an item is rented or returned.
     *
     * @param available new availability state
     */
    protected void setIsAvail(boolean available) {
        this.available = available;
    }

    protected double getRentFee() {
        return rentalFee;
    }

    @Override
    public abstract String toString();
}
