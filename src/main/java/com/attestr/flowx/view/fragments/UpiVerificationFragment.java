package com.attestr.flowx.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.locale.VpaLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 30/08/21
 **/
public class UpiVerificationFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private LinearLayout submitUpiLinearLayout;
    private boolean isUpiIdValid;
    private TextView upiLabelTextView, upiDataErrorTextView, upiDataError, stageTitleTextView;
    private EditText upiPlaceHolderEditText;
    private ProgressBar submitUpiProgressBar;
    private String upiDataErrorString;
    private VpaLocale vpaLocale;
    private static UpiVerificationFragment mUpiVerificationFragment;
    private TextView submitUpiButtonTextView;
    private int remainingRetryAttempts;
    private TextView retryAttemptsTextView;
    private ImageView upiLabelIcon;

    public static UpiVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            mUpiVerificationFragment = new UpiVerificationFragment();
            return mUpiVerificationFragment;
        } else {
            if (mUpiVerificationFragment == null){
                mUpiVerificationFragment = new UpiVerificationFragment();
            }
        }
        return mUpiVerificationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upi_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;

        vpaLocale = handShakeReadyResponse.getLocale().getStageVpa();

        String upiID = GlobalVariables.stageReadyResponse.getData().getState().get("vpa");

        submitUpiLinearLayout = view.findViewById(R.id.submit_vpa_Relative_layout);
        CommonUtils.setButtonAlphaHigh(submitUpiLinearLayout);
        submitUpiProgressBar = view.findViewById(R.id.submit_vpa_progress_bar);
        upiDataError = view.findViewById(R.id.vap_data_error);
        upiDataError.setVisibility(View.GONE);
        upiPlaceHolderEditText = view.findViewById(R.id.field_vap_placeholder);
        stageTitleTextView = view.findViewById(R.id.upi_verification_title);
        stageTitleTextView.setText(vpaLocale.getTitle());
        if (upiID != null){
            upiPlaceHolderEditText.setText(upiID);
        }
        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();
        upiPlaceHolderEditText.setBackgroundResource(0);
        upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
        upiDataErrorString = handShakeReadyResponse.getLocale().getStageVpa().getFieldVpaDataerror();
        submitUpiButtonTextView = view.findViewById(R.id.submit_vpa_button);
        submitUpiButtonTextView.setTextColor(GlobalVariables.textColor);
        upiDataErrorTextView = view.findViewById(R.id.field_vap_data_error_text_view);
        upiLabelTextView = view.findViewById(R.id.field_vap_label);
        retryAttemptsTextView = view.findViewById(R.id.vpa_retry_attempts_text_view);
        upiLabelIcon = view.findViewById(R.id.field_vap_label_icon);
        retryAttemptsTextView.setVisibility(View.GONE);

        submitUpiProgressBar.setVisibility(View.GONE);
        upiDataErrorTextView.setVisibility(View.GONE);
        submitUpiLinearLayout.setClickable(true);
        upiLabelTextView.setText(handShakeReadyResponse.getLocale().getStageVpa().getFieldVpaLabel());
        upiPlaceHolderEditText.setHint(handShakeReadyResponse.getLocale().getStageVpa().getFieldVpaPlaceholder());
        submitUpiButtonTextView.setText(handShakeReadyResponse.getLocale().getStageVpa().getButtonVpaProceed());
        upiDataErrorTextView.setText(upiDataErrorString);

        upiLabelIcon.setBackgroundResource(R.drawable.id_card);
        upiLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        upiPlaceHolderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                upiDataError.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(submitUpiLinearLayout);
                sendVpaVerificationRequest();
                return false;
            }
            return false;
        });

        upiPlaceHolderEditText.addTextChangedListener(new InputTextValidator(upiPlaceHolderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isUpiIdValid = UserInputValidation.isUpiValid(s.toString());
                if (isUpiIdValid){
                    upiDataErrorTextView.setVisibility(View.GONE);
                    upiPlaceHolderEditText.setBackgroundResource(0);
                    upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitUpiLinearLayout.setClickable(true);
                    submitUpiProgressBar.setVisibility(View.GONE);
                    upiDataErrorTextView.setVisibility(View.VISIBLE);
                    upiPlaceHolderEditText.setBackgroundResource(0);
                    upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                }
            }
        });

        submitUpiLinearLayout.setOnClickListener(v -> {
            upiDataError.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(submitUpiLinearLayout);
            sendVpaVerificationRequest();
        });
        
    }


    private void sendVpaVerificationRequest() {
        submitUpiProgressBar.setVisibility(View.VISIBLE);
        String userVPA = upiPlaceHolderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(userVPA)) {
            if (UserInputValidation.isUpiValid(userVPA)) {
                submitUpiLinearLayout.setClickable(false);
                upiPlaceHolderEditText.setBackgroundResource(0);
                upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                CommonUtils.sendRequest(AttestrRequest.actionSubmitVpa(userVPA));
            } else {
                CommonUtils.setButtonAlphaHigh(submitUpiLinearLayout);
                submitUpiProgressBar.setVisibility(View.GONE);
                upiDataErrorTextView.setVisibility(View.VISIBLE);
                upiPlaceHolderEditText.setBackgroundResource(0);
                upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
            }
        } else {
            CommonUtils.setButtonAlphaHigh(submitUpiLinearLayout);
            submitUpiProgressBar.setVisibility(View.GONE);
            submitUpiLinearLayout.setClickable(true);
            upiDataErrorTextView.setVisibility(View.VISIBLE);
            upiPlaceHolderEditText.setBackgroundResource(0);
            upiPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
        }
    }

    public void OnDataError(String response) {
        if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("retryInvalid")) {
            isUpiIdValid = false;
            if (remainingRetryAttempts == 0) {
                HandleException.handleSessionError(response);
            } else {
                upiLabelTextView.setText(handShakeReadyResponse.getLocale().getStageVpa().getFieldVpaLabel());
                CommonUtils.setButtonAlphaHigh(submitUpiLinearLayout);
                upiPlaceHolderEditText.setHint(handShakeReadyResponse.getLocale().getStageVpa().getFieldVpaPlaceholder());
                submitUpiButtonTextView.setText(handShakeReadyResponse.getLocale().getStageVpa().getButtonVpaProceed());
                upiPlaceHolderEditText.getText().clear();
                submitUpiProgressBar.setVisibility(View.GONE);
                submitUpiLinearLayout.setClickable(true);
                ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>() {
                }.getType());
                upiDataError.setText(dataError.getData().getError().getMessage());
                upiDataError.setVisibility(View.VISIBLE);
                upiDataErrorTextView.setText(upiDataErrorString);
                upiDataErrorTextView.setVisibility(View.GONE);
                retryAttemptsTextView.setText(remainingRetryAttempts + " attempts left");
                retryAttemptsTextView.setVisibility(View.VISIBLE);
            }
            remainingRetryAttempts--;
        } else {
            HandleException.handleSessionError(response);
        }

    }

    public void OnSessionError(String response) {
        int attempt = remainingRetryAttempts;
        isUpiIdValid = false;
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            upiDataErrorTextView.setVisibility(View.GONE);
            upiDataError.setText(dataError.getData().getOriginal().getMessage());
            upiDataError.setVisibility(View.VISIBLE);
            submitUpiProgressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(submitUpiLinearLayout);
            submitUpiLinearLayout.setClickable(true);
            retryAttemptsTextView.setText(remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
        remainingRetryAttempts--;
    }


}
