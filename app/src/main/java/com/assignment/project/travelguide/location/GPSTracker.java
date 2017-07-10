package com.assignment.project.travelguide.location;


import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTracker extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private static final String TAG = GPSTracker.class.getSimpleName() + "==> ";
    private static final long MIN_DISTANCE = 1;
    private static final long MIN_TIME = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(MIN_TIME);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE);

        try {
            LocationUpdateManager.getInstance().setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient));
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(TAG + "onConnected: --> ", e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Permission to access GPS location is denied!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG + "onConSus", i + "");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG + "onConnFail", connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG + "onLocChange", location.toString());
        LocationUpdateManager.getInstance().setLastKnownLocation(location);
        LocationUpdateManager.getInstance().callListeners(location);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


}
