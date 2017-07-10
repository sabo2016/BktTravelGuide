package com.assignment.project.travelguide.database.model;

/**
 * Created by ukesh on 3/22/17.
 */

public class RouteDetail extends BaseModel {
    private int id;
    private int routeId;
    private double latitude;
    private double longitude;

    public RouteDetail(int routeId, double latitude, double longitude, int id) {
        this.routeId = routeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public RouteDetail() {
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
