/*
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
public class Media {

    // instance variables (attributes) that will be inherited
    private String id;															// instance variable (attribute) for id (a combination of numbers and letters)
    private String title;														// instance variable (attribute) for title: the name of the item
    private String artist; 														// instance variable (attribute) for artist
    private int year; 															// instance variable (attribute) for year: the year the item was released to the market
    private boolean isAvail;													// instance variable (attribute) for isAvail: determines the availability of the media item
    private double rentFee; 													// instance variable (attribute) for rentFee: cost for renting the media for a day

    // constructor to initialize the objects
    protected Media (String id, String title, String artist, int year, boolean bool) {
        // object reference: refers to the instance the current constructor is creating
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.isAvail = bool;
    }

    // method to return media id choice (accessors, aka "getters")
    protected String getId() {
        return id;
    }

    // method to update media id choice (mutators, aka "setters")
    protected void setId(String id) {
        this.id = id;
    }

    // method to return media title choice (accessors, aka "getters")
    protected String getTitle() {
        return title;
    }

    // method to update media title choice (mutators, aka "setters")
    protected void setTitle(String title) {
        this.title = title;
    }

    // method to return musical artist choice (accessors, aka "getters")
    protected String getArtist() {
        return artist;
    }

    // method to update musical artist choice (mutators, aka "setters")
    protected void setArtist(String artist) {
        this.artist = artist;
    }

    // method to return media year of release choice (accessors, aka "getters")
    protected int getYear() {
        return year;
    }

    // method to update media year of release choice (mutators, aka "setters")
    protected void setYear(int year) {
        this.year = year;
    }

    // method to return selected media availability (accessors, aka "getters")
    protected boolean getIsAvail() {
        return isAvail;
    }

    // method to update selected media availability (mutators, aka "setters")
    protected void setIsAvail(boolean bool) {
        this.isAvail = bool;
    }

    // method to return price (accessors, aka "getters")
    protected double getRentFee() {
        //setRentFee(getRentFee());
        return rentFee;
    }

    // method to update price (mutators, aka "setters")
    protected void setRentFee(double rentFee) {
        this.rentFee = rentFee;
    }

    public String getPublicId() { return getId(); }
    public int getPublicYear() { return getYear(); }
    public boolean isAvailable() { return getIsAvail(); }

    // method to display results
    public String toString() {
        String strdisplay = null;
        return strdisplay;
    }
}

