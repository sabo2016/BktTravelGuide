package com.assignment.project.travelguide.database.model;

/**
 * Created by ukesh on 3/21/17.
 */

public class Place  extends BaseModel{

    private int placeId;
    private String placeName;
    private double latitude;
    private double longitude;

    public Place() {
    }

    public Place(int placeId, String placeName, double latitude, double longitude) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
