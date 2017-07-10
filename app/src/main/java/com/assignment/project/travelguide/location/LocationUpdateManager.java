package com.assignment.project.travelguide.location;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocationUpdateManager {

    private static final String TAG = LocationUpdateManager.class.getSimpleName() + "==>: ";
    private static List<LocationChangeListener> locationChangeListeners = new ArrayList<>();
    private static LocationUpdateManager LocationUpdateManager = null;
    private Location lastKnownLocation = null;

    private LocationUpdateManager() {

    }

    public static LocationUpdateManager getInstance() {
        if (LocationUpdateManager == null) {
            LocationUpdateManager = new LocationUpdateManager();
        }
        return LocationUpdateManager;
    }

    public void setLocationChangeListener(LocationChangeListener locationChangeListener) {
        if (locationChangeListener != null) {
            locationChangeListeners.add(locationChangeListener);
        }
    }

    public void removeLocationChangeListener(LocationChangeListener locationChangeListener) {
        if (locationChangeListeners.contains(locationChangeListener)) {
            locationChangeListeners.remove(locationChangeListener);
        }
    }

    public synchronized void callListeners(Location location) {
        for (LocationChangeListener listener : locationChangeListeners) {
            try {
                if (listener != null) {
                    listener.onLocationChangeCalled(location);
                }
                if (capture) {
                    locations.add(location);
                }
            } catch (Exception e) {
                Log.d(TAG, "callListeners: " + e.getMessage());
            }
        }
    }


    public void clearListeners() {
        locationChangeListeners = new ArrayList<>();
    }

    public class ListenerNotAliveException extends Exception {

        public ListenerNotAliveException(String detailMessage) {
            super(detailMessage);
        }
    }


    private ArrayList<Location> locations = new ArrayList<Location>();
    private boolean capture = false;

    public void toggleCapture(boolean flag) {
        this.capture = flag;
    }

    public boolean getCaptureStatus() {
        return this.capture;
    }

    public void clearCapture() {
        locations.clear();
    }

    public ArrayList<Location> getCapturedLocations() {
        return this.locations;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }
}
