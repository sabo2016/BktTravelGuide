package com.assignment.project.travelguide.database.model;

/**
 * Created by ukesh on 3/21/17.
 */

public class Route extends BaseModel {
    private int routeId;
    private int placeOne;
    private int placeTwo;
    private double distance;

    public Route(int routeId, int placeOne, int placeTwo, double distance) {
        this.routeId = routeId;
        this.placeOne = placeOne;
        this.placeTwo = placeTwo;
        this.distance = distance;
    }

    public Route() {
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getPlaceOne() {
        return placeOne;
    }

    public void setPlaceOne(int placeOne) {
        this.placeOne = placeOne;
    }

    public int getPlaceTwo() {
        return placeTwo;
    }

    public void setPlaceTwo(int placeTwo) {
        this.placeTwo = placeTwo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
