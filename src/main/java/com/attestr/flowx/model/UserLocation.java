package com.attestr.flowx.model;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 04/11/21
 **/
public class UserLocation {

    private static final String TAG = "User_Location";
    private FusedLocationProviderClient mFusedLocationClient;
    private Activity mActivity;
    private static UserLocation userLocationClass;

    public static UserLocation getInstance(boolean newInstance) {
        if (newInstance) {
            userLocationClass = new UserLocation();
            return userLocationClass;
        } else {
            if (userLocationClass == null) {
                userLocationClass = new UserLocation();
            }
        }
        return userLocationClass;
    }

    public void setData(Activity activity) {
        try {
            this.mActivity = activity;
            if (isGooglePlayServicesAvailable(activity)){
                this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
                getUserLocation();
            }
        } catch (Exception e){
            HandleException.handleInternalException("User Location Exception: "+ e);
        }
    }

    private void getUserLocation() {
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        CancellationToken cancellationToken = cancellationTokenSource.getToken();

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken);
            locationTask.addOnSuccessListener(location -> {
                if (location != null){
                    GlobalVariables.detectedLatitude = location.getLatitude();
                    GlobalVariables.detectedLongitude = location.getLongitude();
                } else {
                    cancellationTokenSource.cancel();
                    HandleException.handleInternalException("Location not found!");
                }
            }).addOnCompleteListener(task -> {
                task.addOnSuccessListener(location -> {
                    if (location != null){
                        GlobalVariables.detectedLatitude = location.getLatitude();
                        GlobalVariables.detectedLongitude = location.getLongitude();
                    }
                }).addOnCompleteListener(task1 -> {
                    if (task1.getResult() != null){
                        GlobalVariables.detectedLatitude = task1.getResult().getLatitude();
                        GlobalVariables.detectedLongitude = task1.getResult().getLongitude();
                    }
                }).addOnFailureListener(e -> {
                    cancellationTokenSource.cancel();
                    HandleException.handleInternalException(e.toString());
                }).addOnCanceledListener(() -> {
                    cancellationTokenSource.cancel();
                    HandleException.handleInternalException("Location not found!");
                });
            }).addOnCanceledListener(() -> {
                cancellationTokenSource.cancel();
                HandleException.handleLocationException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getGpsError500());
            }).addOnFailureListener(e -> {
                cancellationTokenSource.cancel();
                HandleException.handleLocationException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getGpsError500());
            });
        } else {
            HandleException.handleLocationException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getGpsError102());
        }


    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                Objects.requireNonNull(googleApiAvailability.getErrorDialog(
                        activity,
                        status,
                        2404))
                        .show();
            }
            return false;
        }
        return true;
    }

}


