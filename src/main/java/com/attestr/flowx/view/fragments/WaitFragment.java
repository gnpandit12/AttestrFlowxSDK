package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.databinding.EAadhaarWaitLayoutBinding;
import com.attestr.flowx.databinding.WaitFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.view.activity.DigilockerAuthorizationActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 29/08/21
 **/
public class WaitFragment extends Fragment {

    private WaitFragmentBinding waitFragmentBinding;
    private EAadhaarWaitLayoutBinding eAadhaarWaitLayoutBinding;
    private View view;
    private TextView waitTitleTextView, waitSubTitleTextView;
    private TextView eAadharWaitTitleTextView, eAadharWaitSubTitleTextView;
    private String mHandshakeReadyResponse;
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private HandshakeReadyData handShakeReadyResponse;
    private Map<String, String> mDataMap;
    private Activity mActivity;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RelativeLayout.LayoutParams layoutParams;
    private boolean normalWaitLayout;
    private boolean starWebView;
    private ProgressBar waitProgressBar, eAadharWaitProgressBar;

    public WaitFragment(Activity activity, Map<String, String> dataMap) {
        this.mActivity = activity;
        this.mDataMap = dataMap;
        this.mHandshakeReadyResponse = GlobalVariables.handshakeReadyResponseString;
        if (mDataMap != null){
            if (Boolean.parseBoolean(mDataMap.get("startWebView"))) {
                normalWaitLayout = true;
                starWebView = true;
            } else {
                normalWaitLayout = false;
                starWebView = false;
            }
        } else {
            normalWaitLayout = true;
            starWebView = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            if (ProgressIndicator.getProgressViewChildCount() == 2){
                ProgressIndicator.setProgressBarInvisible();
            }
        } catch (Exception e){
            HandleException.handleInternalException("Wait fragment progressBar invisible: "+ e);
        }

        if (normalWaitLayout) {
            waitFragmentBinding = WaitFragmentBinding.inflate(inflater, container, false);
            view = waitFragmentBinding.getRoot();
        } else {
            eAadhaarWaitLayoutBinding = EAadhaarWaitLayoutBinding.inflate(inflater, container, false);
            view = eAadhaarWaitLayoutBinding.getRoot();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,0);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            HandleException.setCurrentActivity(mActivity);
                            try {
                                CommonUtils.sendRequest(AttestrRequest.actionInit());
                            } catch (Exception e){
                                HandleException.handleInternalException(e.toString());
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            HandleException.setCurrentActivity(mActivity);
                            if (result.getData() != null){
                                if (result.getData().getBooleanExtra("timeout_exception", false)){
                                    HandleException.exitLaunchActivity();
                                } else {
                                    switch (result.getData().getStringExtra("result")){
                                        case "OnBackPressed":
                                            DigiLockerAuthorizationFragment digiLockerAuthorizationFragment =
                                                    DigiLockerAuthorizationFragment.getInstance(true);
                                            digiLockerAuthorizationFragment.setData(mActivity);
                                            ((LaunchActivity) mActivity).addFragment(digiLockerAuthorizationFragment);
                                            break;
                                        case "Deny":
                                            String sessionErrorResponse = result.getData().getStringExtra("session_error_response");
                                            if (sessionErrorResponse != null){
                                                HandleException.handleSessionError(sessionErrorResponse);
                                            }
                                            break;
                                    }
                                }
                            }
                            break;
                    }
                });

        handShakeReadyResponse = gsonBuilder.create().fromJson(mHandshakeReadyResponse, HandshakeReadyData.class);
        GlobalVariables.handshakeReadyResponse = handShakeReadyResponse;

        if (normalWaitLayout && starWebView){
            waitTitleTextView = waitFragmentBinding.waitTitle;
            waitSubTitleTextView = waitFragmentBinding.waitSubTitle;
            waitProgressBar = waitFragmentBinding.waitProgressBar;
            waitTitleTextView.setText(handShakeReadyResponse.getLocale().getWait().getTitle());
            waitSubTitleTextView.setText(handShakeReadyResponse.getLocale().getWait().getSubtitle());
            waitProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(GlobalVariables.themeColor), android.graphics.PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(mActivity, DigilockerAuthorizationActivity.class);
            intent.putExtra("digest_value", mDataMap.get("digest"));
            activityResultLauncher.launch(intent);
        } else if (!normalWaitLayout && !starWebView) {
            try {
                eAadharWaitTitleTextView = eAadhaarWaitLayoutBinding.eAadhaarWaitTitle;
                eAadharWaitSubTitleTextView = eAadhaarWaitLayoutBinding.eAadhaarWaitSubTitle;
                eAadharWaitProgressBar = eAadhaarWaitLayoutBinding.eAadhaarWaitProgressBar;
                eAadharWaitProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(GlobalVariables.themeColor), android.graphics.PorterDuff.Mode.SRC_IN);
                eAadharWaitTitleTextView.setText(handShakeReadyResponse.getLocale().getWait().getTitle());
                eAadharWaitSubTitleTextView.setText(handShakeReadyResponse.getLocale().getEaadhaarLocale().getWaitText());
                CommonUtils.sendRequest(AttestrRequest.actionEaadhaarSubmit());
            } catch (Exception e){
                HandleException.handleInternalException(e.toString());
            }
        } else if (normalWaitLayout){
            waitTitleTextView = waitFragmentBinding.waitTitle;
            waitSubTitleTextView = waitFragmentBinding.waitSubTitle;
            waitTitleTextView.setText(handShakeReadyResponse.getLocale().getWait().getTitle());
            waitSubTitleTextView.setText(handShakeReadyResponse.getLocale().getWait().getSubtitle());
            waitProgressBar = waitFragmentBinding.waitProgressBar;
            waitProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(GlobalVariables.themeColor), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }



}
