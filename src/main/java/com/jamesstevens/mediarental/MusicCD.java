package com.jamesstevens.mediarental;

/**
 * Represents a music album stored on CD.
 */
public class MusicCD extends Media {

    private static final double MUSIC_CD_FEE = 6.99;

    /**
     * Constructs a music CD.
     *
     * @param id unique six-character alphanumeric identifier
     * @param title album title
     * @param artist performer or band name
     * @param year release year
     * @param available current availability
     */
    protected MusicCD(String id, String title, String artist, int year,
                      boolean available) {
        super(id, title, artist, year, available, MUSIC_CD_FEE);
    }

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
