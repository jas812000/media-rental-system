/*
 * CMIS 242 7380
 *
 * Version 1.0
 *
 * 14 December 2022
 *
 * © 2022, James Stevens, All rights reserved.
 *
 * This program, Media Rental System, gives the user the ability to rent media items (eBook, MovieDVD, MusicCD). The user can
 * search for items by ID, title or artist. The user can also rent the media item. The function to return the item is also
 * available. The files are initially uploaded before all options can be utilized.
 *
 */


//package Media_Rental_System;

public class MusicCD extends Media {

    // constant variable establishing rental fee
    private final double MUSIC_CD_FEE = 6.99; 														// rental fee per day

    // constructor to initialize the objects; this calls parent/superclass (Media) constructor)
    protected MusicCD (String id, String title, String artist, int year, boolean bool) {
        super (id, title, artist, year, bool);														// refers to parent (superclass) methods and constructors
        setRentFee(MUSIC_CD_FEE);																	// sets the rent fee with the constructor
    }

    // method to display results
    @Override
    public String toString() {
        String strdisplay = "Music (CD) [ ";
        strdisplay += "id: " + getId() + ", ";
        strdisplay += "Title: " + getTitle() + ", ";
        strdisplay += "Performer: " + getArtist() + ", ";
        strdisplay += "Year: " + getYear() + ", ";
        strdisplay += "Availability: " + getIsAvail() + ".";
        strdisplay += " ]";
        return strdisplay;
    }
}
