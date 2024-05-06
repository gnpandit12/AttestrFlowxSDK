package com.attestr.flowx.view.fragments.digitalAddressCheck;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.DigitalAddressCheckGoogleMapsFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.DigitalAddressLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 10/11/21
 **/
public class DigitalAddressCheckGoogleMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private DigitalAddressCheckGoogleMapsFragmentBinding fragmentBinding;
    private DigitalAddressLocale digitalAddressLocale;
    private static DigitalAddressCheckGoogleMapFragment digitalAddressCheckGoogleMapFragment;
    private Activity mActivity;
    private Map<String, String> mDataMap;
    private SupportMapFragment supportMapFragment;
    private TextView mapMarkerTextView, submitButtonTextView;
    private LinearLayout submitLinearLayout;
    private ProgressBar submitProgressBar;
    private double detectedLatitude, detectedLongitude;
    private JSONArray mEntranceImagesMediaIDList;
    private JSONArray mBuildingImagesMediaIDList;


    public DigitalAddressCheckGoogleMapFragment() {}

    public static DigitalAddressCheckGoogleMapFragment getInstance(boolean newInstance) {
        if (newInstance) {
            digitalAddressCheckGoogleMapFragment = new DigitalAddressCheckGoogleMapFragment();
            return digitalAddressCheckGoogleMapFragment;
        } else {
            if (digitalAddressCheckGoogleMapFragment == null) {
                digitalAddressCheckGoogleMapFragment = new DigitalAddressCheckGoogleMapFragment();
            }
        }
        return digitalAddressCheckGoogleMapFragment;
    }

    public void setData(Activity activity,
                        Map<String, String> dataMap,
                        JSONArray entranceImagesMediaIDList,
                        JSONArray buildingImagesMediaIDList) {
        this.mActivity = activity;
        this.mDataMap = dataMap;
        this.mEntranceImagesMediaIDList = entranceImagesMediaIDList;
        this.mBuildingImagesMediaIDList = buildingImagesMediaIDList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DigitalAddressCheckGoogleMapsFragmentBinding.inflate(inflater, container, false);
        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        detectedLatitude = GlobalVariables.detectedLatitude;
        detectedLongitude = GlobalVariables.detectedLongitude;
        GlobalVariables.markerDragLatitude = detectedLatitude;
        GlobalVariables.markerDragLongitude = detectedLongitude;

        digitalAddressLocale = GlobalVariables.handshakeReadyResponse.getLocale().getDigitalAddressLocale();

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_maps_fragment);
        if (supportMapFragment != null){
            supportMapFragment.getMapAsync(this);
        } else {
            HandleException.handleInternalException("Digital address check supportMapFragment null");
        }

        mapMarkerTextView = fragmentBinding.mapMarkerInstructionTextView;
        submitButtonTextView = fragmentBinding.googleMapsSubmitButtonTextView;
        submitProgressBar = fragmentBinding.googleMapsSubmitProgressBar;
        submitLinearLayout = fragmentBinding.googleMapsSubmitRelativeLayout;

        CommonUtils.setButtonAlphaHigh(submitLinearLayout);
        submitLinearLayout.setClickable(true);
        submitProgressBar.setVisibility(View.GONE);
        submitLinearLayout.setOnClickListener(this);

        mapMarkerTextView.setText(digitalAddressLocale.getMapMarkerInstruction());
        submitButtonTextView.setText(digitalAddressLocale.getButtonSubmit());

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if ((ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng userLocation = new LatLng(GlobalVariables.detectedLatitude, GlobalVariables.detectedLongitude);
        MarkerOptions markerOptions = new MarkerOptions().position(userLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        Marker marker = googleMap.addMarker(markerOptions);
        if (marker != null){
            marker.setDraggable(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                GlobalVariables.markerDragLatitude = marker.getPosition().latitude;
                GlobalVariables.markerDragLongitude = marker.getPosition().longitude;
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                GlobalVariables.markerDragLatitude = marker.getPosition().latitude;
                GlobalVariables.markerDragLongitude = marker.getPosition().longitude;
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                GlobalVariables.markerDragLatitude = marker.getPosition().latitude;
                GlobalVariables.markerDragLongitude = marker.getPosition().longitude;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == submitLinearLayout){
            if (mDataMap != null && isUserDetectedLocationValid()){
                sendRequest(mDataMap);
            }
        }
    }

    private boolean isUserDetectedLocationValid() {
        return  !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLatitude))&&
                !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLongitude));
    }

    public void sendRequest(Map<String, String> mDataMap) {
        submitProgressBar.setVisibility(View.VISIBLE);
        submitLinearLayout.setClickable(false);
        CommonUtils.setButtonAlphaLow(submitLinearLayout);
        mDataMap.put("origLatitude", String.valueOf(GlobalVariables.detectedLatitude));
        mDataMap.put("origLongitude", String.valueOf(GlobalVariables.detectedLongitude));
        mDataMap.put("latitude",
                GlobalVariables.markerDragLatitude == 0.0 ?
                        String.valueOf(GlobalVariables.detectedLatitude) :
                        String.valueOf(GlobalVariables.markerDragLatitude));
        mDataMap.put("longitude",
                GlobalVariables.markerDragLongitude == 0.0 ?
                        String.valueOf(GlobalVariables.detectedLongitude) :
                        String.valueOf(GlobalVariables.markerDragLongitude));
        CommonUtils.sendRequest(AttestrRequest.actionDigitalAddressSubmit(mDataMap, mEntranceImagesMediaIDList, mBuildingImagesMediaIDList));
    }


}
