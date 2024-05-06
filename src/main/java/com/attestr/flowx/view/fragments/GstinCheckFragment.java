package com.attestr.flowx.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 31/08/21
 **/
public class GstinCheckFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private TextView gstinLableTextView, gstinDataErrorTextView,
            gstinInputDataErrorTextView, gstinSubmitButtonTextView;
    private EditText gstinPlaceholderEditText;
    private LinearLayout gstinLinearLayout;
    private ProgressBar gstinSubmitProgressBar;
    private String gstinDataErrorString;
    private boolean isGstinNumberValid, fetchFilling;
    private static GstinCheckFragment mGstinCheckFragment;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView gstinLabelIcon;

    public static GstinCheckFragment getInstance(boolean newInstance) {
        if (newInstance){
            mGstinCheckFragment = new GstinCheckFragment();
            return mGstinCheckFragment;
        } else {
            if (mGstinCheckFragment == null){
                mGstinCheckFragment = new GstinCheckFragment();
            }
        }
        return mGstinCheckFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gstin_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        String gstinNumber = GlobalVariables.stageReadyResponse.getData().getState().get("gstin");
        fetchFilling = Boolean.parseBoolean(Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("fetchFilings")).toString());
        gstinDataErrorString = handShakeReadyResponse.getLocale().getStageGstin().getFieldGstinDataerror();

        gstinLinearLayout = view.findViewById(R.id.submit_gstin_Relative_layout);
        CommonUtils.setButtonAlphaHigh(gstinLinearLayout);
        gstinSubmitProgressBar = view.findViewById(R.id.submit_gstin_progress_bar);
        gstinDataErrorTextView = view.findViewById(R.id.gstin_data_error);
        gstinPlaceholderEditText = view.findViewById(R.id.gstin_placeholder);
        if (gstinNumber != null){
            gstinPlaceholderEditText.setText(gstinNumber);
        }
        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();
        gstinPlaceholderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        gstinPlaceholderEditText.setBackgroundResource(0);
        gstinPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        gstinDataErrorTextView.setVisibility(View.GONE);
        gstinLableTextView = view.findViewById(R.id.gstin_label);
        gstinInputDataErrorTextView = view.findViewById(R.id.gstin_data_error_text_view);
        gstinSubmitButtonTextView = view.findViewById(R.id.submit_gstin_button);
        gstinLabelIcon = view.findViewById(R.id.gstin_label_icon);
        gstinSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
        gstinLableTextView.setText(handShakeReadyResponse.getLocale().getStageGstin().getFieldGstinLabel());
        gstinPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getStageGstin().getFieldGstinPlaceholder());
        gstinSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getStageGstin().getButtonGstinProceed());

        gstinSubmitProgressBar.setVisibility(View.GONE);
        gstinInputDataErrorTextView.setVisibility(View.GONE);
        gstinInputDataErrorTextView.setText(gstinDataErrorString);
        gstinLinearLayout.setClickable(true);

        gstinLabelIcon.setBackgroundResource(R.drawable.id_card);
        gstinLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        retryAttemptsTextView = view.findViewById(R.id.gstin_retry_attempts_text_view);
        retryAttemptsTextView.setVisibility(View.GONE);

        gstinPlaceholderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                gstinDataErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(gstinLinearLayout);
                sendGstinVerificationRequest();
                return false;
            }
            return false;
        });

        gstinPlaceholderEditText.addTextChangedListener(new InputTextValidator(gstinPlaceholderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isGstinNumberValid = UserInputValidation.isGstinNumberValid(s.toString());
                if (isGstinNumberValid){
                    gstinInputDataErrorTextView.setVisibility(View.INVISIBLE);
                    gstinPlaceholderEditText.setBackgroundResource(0);
                    gstinPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    gstinLinearLayout.setClickable(true);
                    gstinSubmitProgressBar.setVisibility(View.GONE);
                    gstinPlaceholderEditText.setBackgroundResource(0);
                    gstinPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    gstinInputDataErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        gstinLinearLayout.setOnClickListener(v -> {
            gstinDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(gstinLinearLayout);
            sendGstinVerificationRequest();
        });

    }

    private void sendGstinVerificationRequest(){
        String gstinNumber = gstinPlaceholderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(gstinNumber)) {
            if (UserInputValidation.isGstinNumberValid(gstinNumber)) {
                sendRequest(gstinNumber);
            } else {
                userInputError();
            }
        } else {
            userInputError();
        }
    }

    private void sendRequest(String gstinNumber) {
        gstinSubmitProgressBar.setVisibility(View.VISIBLE);
        gstinLinearLayout.setClickable(false);
        gstinPlaceholderEditText.setBackgroundResource(0);
        gstinPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        CommonUtils.sendRequest(AttestrRequest.actionSubmitVoterID(gstinNumber));
    }

    private void userInputError(){
        CommonUtils.setButtonAlphaHigh(gstinLinearLayout);
        gstinLinearLayout.setClickable(true);
        gstinPlaceholderEditText.setBackgroundResource(0);
        gstinPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
        gstinSubmitProgressBar.setVisibility(View.GONE);
        gstinInputDataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnDataError(String response) {
        remainingRetryAttempts--;
        isGstinNumberValid = false;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(gstinLinearLayout);
            gstinPlaceholderEditText.getText().clear();
            gstinSubmitProgressBar.setVisibility(View.GONE);
            gstinLinearLayout.setClickable(true);
            gstinInputDataErrorTextView.setText(gstinDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            gstinInputDataErrorTextView.setVisibility(View.GONE);
            gstinDataErrorTextView.setText(dataError.getData().getError().getMessage());
            gstinDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(gstinLinearLayout);
            gstinPlaceholderEditText.getText().clear();
            gstinSubmitProgressBar.setVisibility(View.GONE);
            gstinLinearLayout.setClickable(true);
            gstinInputDataErrorTextView.setText(gstinDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            gstinInputDataErrorTextView.setVisibility(View.GONE);
            gstinDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            gstinDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }


}

