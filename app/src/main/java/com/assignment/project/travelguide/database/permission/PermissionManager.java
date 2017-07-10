package com.assignment.project.travelguide.database.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by ukesh on 3/27/17.
 */

public class PermissionManager {
    public static int PER_REQ_WRITE_EXTERNAL_STORAGE = 1;
    public static int PER_REQ_ACCESS_FINE_LOCATION = 2;

    public static void checkPermission(Context context, String checkFor, PermissionInterface permissionInterface, int permissionRequest) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, checkFor);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionInterface.onGranted();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    checkFor)) {
                showRationale(context, checkFor, permissionRequest);

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{checkFor},
                        permissionRequest);
            }
        }
    }

    public static void showRationale(final Context context, final String checkFor, final int permissionRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Application Notification")
                .setMessage("You should grant this permission inorder to work this fine.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{checkFor},
                                permissionRequest);
                    }
                });
        builder.show();
    }
}
