package com.attestr.flowx.view.fragments;

import android.app.Activity;
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
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 29/08/21
 **/
public class PanVerificationFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private String panDataErrorString;
    private LinearLayout submitPanLinearLayout;
    private ProgressBar submitPanProgressBar;
    private TextView panDataError, panLabelTextView, panDataErrorTextView, submitPanButtonTextView,
                        panVerificationTitle;
    private EditText panPlaceHolderEditText;
    private boolean isPanNumberValid;
    private boolean extended;
    private ResponseData<StageInitData> stageReadyResponse;
    private TextView retryAttemptsTextView;
    private static PanVerificationFragment mPanVerificationFragment;
    private Activity mActivity;
    private ImageView panLabelIcon;
    private Double remainingRetryAttempts;
    private int retryAttempts = 0;

    public static PanVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            mPanVerificationFragment = new PanVerificationFragment();
            return mPanVerificationFragment;
        } else {
            if (mPanVerificationFragment == null){
                mPanVerificationFragment = new PanVerificationFragment();
            }
        }
        return mPanVerificationFragment;
    }

    public void setData(Activity activity) {
        this.mActivity = activity;
        ProgressIndicator.setActivity(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pan_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        stageReadyResponse = GlobalVariables.stageReadyResponse;
        String panNumber = stageReadyResponse.getData().getState().get("pan");
        panDataErrorString = handShakeReadyResponse.getLocale().getStagePan().getFieldPanDataerror();

        extended = (boolean) stageReadyResponse.getData().getMetadata().get("extended");

        submitPanLinearLayout = view.findViewById(R.id.submit_pan_linear_layout);
        CommonUtils.setButtonAlphaHigh(submitPanLinearLayout);
        submitPanProgressBar = view.findViewById(R.id.submit_pan_progress_bar);
        submitPanProgressBar.setVisibility(View.GONE);
        panDataError = view.findViewById(R.id.pan_data_error);
        panDataError.setVisibility(View.GONE);
        panPlaceHolderEditText = view.findViewById(R.id.field_pan_placeholder);
        panLabelIcon = view.findViewById(R.id.pan_label_icon);

        panLabelIcon.setBackgroundResource(R.drawable.id_card);
        panLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));


        remainingRetryAttempts = (Double) GlobalVariables.stageReadyResponse.getData().getMetadata().get("retryAttempts");
        if (remainingRetryAttempts != null) {
            retryAttempts = remainingRetryAttempts.intValue();
        }

        if (panNumber != null) {
            panPlaceHolderEditText.setText(panNumber);
        }
        panPlaceHolderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        panPlaceHolderEditText.setBackgroundResource(0);
        panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
        panDataError.setVisibility(View.GONE);
        panLabelTextView = view.findViewById(R.id.field_pan_label);
        panDataErrorTextView = view.findViewById(R.id.field_pan_data_error_text_view);
        submitPanButtonTextView = view.findViewById(R.id.submit_pan_button);
        submitPanButtonTextView.setTextColor(GlobalVariables.textColor);
        panVerificationTitle = view.findViewById(R.id.pan_verification_title);
        retryAttemptsTextView = view.findViewById(R.id.pan_retry_attempts_text_view);
        retryAttemptsTextView.setTextColor(Color.parseColor(GlobalVariables.themeColor));
        retryAttemptsTextView.setVisibility(View.GONE);
        panVerificationTitle.setText(GlobalVariables.handshakeReadyResponse.getLocale().getStagePan().getTitle());
        submitPanProgressBar.setVisibility(View.GONE);
        panDataErrorTextView.setVisibility(View.GONE);
        panLabelTextView.setText(handShakeReadyResponse.getLocale().getStagePan().getFieldPanLabel());
        panPlaceHolderEditText.setHint(handShakeReadyResponse.getLocale().getStagePan().getFieldPanPlaceholder());
        submitPanButtonTextView.setText(handShakeReadyResponse.getLocale().getStagePan().getButtonPanProceed());
        panDataErrorTextView.setText(panDataErrorString);
        submitPanLinearLayout.setClickable(true);
        panPlaceHolderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                panDataError.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(submitPanLinearLayout);
                sendPanVerificationRequest();
                return false;
            }
            return false;
        });

        panPlaceHolderEditText.addTextChangedListener(new InputTextValidator(panPlaceHolderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isPanNumberValid = UserInputValidation.isPanValid(s.toString());
                if (isPanNumberValid){
                    panDataErrorTextView.setVisibility(View.GONE);
                    panPlaceHolderEditText.setBackgroundResource(0);
                    panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitPanLinearLayout.setClickable(true);
                    submitPanProgressBar.setVisibility(View.GONE);
                    panPlaceHolderEditText.setBackgroundResource(0);
                    panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    panDataErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        submitPanLinearLayout.setOnClickListener(v -> {
            panDataError.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(submitPanLinearLayout);
            sendPanVerificationRequest();
        });
    }

    private void sendPanVerificationRequest() {
        String userPanNumber = panPlaceHolderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(userPanNumber)) {
            if (UserInputValidation.isPanValid(userPanNumber)) {
                submitPanProgressBar.setVisibility(View.VISIBLE);
                submitPanLinearLayout.setClickable(false);
                panPlaceHolderEditText.setBackgroundResource(0);
                panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                CommonUtils.sendRequest(AttestrRequest.actionSubmitPan(userPanNumber, extended));
            } else {
                CommonUtils.setButtonAlphaHigh(submitPanLinearLayout);
                panPlaceHolderEditText.setBackgroundResource(0);
                panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                submitPanProgressBar.setVisibility(View.GONE);
                panDataErrorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            CommonUtils.setButtonAlphaHigh(submitPanLinearLayout);
            submitPanLinearLayout.setClickable(true);
            submitPanProgressBar.setVisibility(View.GONE);
            panPlaceHolderEditText.setBackgroundResource(0);
            panPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
            panDataErrorTextView.setVisibility(View.VISIBLE);
        }

    }

    public void OnDataError(String response) {
        isPanNumberValid = false;
        if (retryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            panPlaceHolderEditText.getText().clear();
            submitPanProgressBar.setVisibility(View.GONE);
            submitPanLinearLayout.setClickable(true);
            panDataErrorTextView.setText(panDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            panDataErrorTextView.setVisibility(View.GONE);
            panDataError.setText(dataError.getData().getError().getMessage());
            panDataError.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setVisibility(View.VISIBLE);
            submitPanLinearLayout.setClickable(true);
            CommonUtils.setButtonAlphaHigh(submitPanLinearLayout);
        }
        retryAttempts--;
    }

    public void OnSessionError(String response) {
        if (retryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            panDataErrorTextView.setVisibility(View.GONE);
            panDataError.setText(dataError.getData().getOriginal().getMessage());
            panDataError.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setVisibility(View.VISIBLE);
            submitPanProgressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(submitPanLinearLayout);
            submitPanLinearLayout.setClickable(true);
        }
        retryAttempts--;
    }

}


