package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.RetryInvalidData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 24/09/21
 **/
public class DigiLockerAuthorizationFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = "eAadhaar_fragment";
    private HandshakeReadyData handShakeReadyResponse;
    private TextView infoTextView, descriptionTextView, proceedButtonTextView;
    private LinearLayout proceedLinearLayout;
    private ProgressBar proceedProgressBar;
    private Activity mActivity;
    private String digest;
    private Map<String, String> dataMap;
    private static DigiLockerAuthorizationFragment digiLockerAuthorizationFragment;

    public static DigiLockerAuthorizationFragment getInstance(boolean newInstance){
        if (newInstance){
            digiLockerAuthorizationFragment = new DigiLockerAuthorizationFragment();
            return digiLockerAuthorizationFragment;
        } else {
            if (digiLockerAuthorizationFragment == null){
                digiLockerAuthorizationFragment = new DigiLockerAuthorizationFragment();
            }
        }
        return digiLockerAuthorizationFragment;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digilocker_authorization_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        digest = handShakeReadyResponse.getDigest();
        dataMap = new HashMap<>();
        infoTextView = view.findViewById(R.id.e_aadhaar_info_text_view);
        descriptionTextView = view.findViewById(R.id.e_aadhaar_description_text_view);
        proceedButtonTextView = view.findViewById(R.id.proceed_button_text_view);
        proceedLinearLayout = view.findViewById(R.id.proceed_relative_layout);
        proceedProgressBar = view.findViewById(R.id.proceed_progress_bar);

        proceedLinearLayout.setOnClickListener(this);
        proceedProgressBar.setVisibility(View.INVISIBLE);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        proceedLinearLayout.setClickable(true);

        infoTextView.setText(handShakeReadyResponse.getLocale().getDigiLockerLocale().getInfo());
        descriptionTextView.setText(handShakeReadyResponse.getLocale().getDigiLockerLocale().getDesc());
        proceedButtonTextView.setText(handShakeReadyResponse.getLocale().getDigiLockerLocale().getDlProceedLabel());

    }

    @Override
    public void onClick(View view) {
        if (view == proceedLinearLayout) {
            if (GlobalVariables.digiLockerDigest != null) {
                CommonUtils.setButtonAlphaLow(proceedLinearLayout);
                proceedLinearLayout.setClickable(false);
                proceedProgressBar.setVisibility(View.VISIBLE);
                dataMap.put("startWebView", "true");
                dataMap.put("digest", GlobalVariables.digiLockerDigest);
                ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, dataMap));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        proceedProgressBar.setVisibility(View.INVISIBLE);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        proceedLinearLayout.setClickable(true);
    }

    public void OnDataError(String response) {
        ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
        if ("4041".equals(dataError.getData().getError().getCode().toString())) {
            EAadhaarDataErrorFragment eAadhaarDataErrorFragment = EAadhaarDataErrorFragment.getInstance(true);
            eAadhaarDataErrorFragment.setData(mActivity);
            ((LaunchActivity) mActivity).addFragment(eAadhaarDataErrorFragment);
        }
    }

}



