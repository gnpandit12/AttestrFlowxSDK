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

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 01/09/21
 **/
public class MasterDirectorFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private TextView masterDirectorLabelTextView, masterDirectorDataErrorTextView, masterDirectorInputErrorTextView, masterDirectorSubmitButtonTextView;
    private EditText masterDirectorPlaceholderEditText;
    private LinearLayout masterDirectorSubmitLinearLayout;
    private ProgressBar masterDirectorSubmitProgressBar;
    private String masterDirectorDataErrorString;
    private boolean isDinNumberValid;
    private static MasterDirectorFragment mMasterDirectorFragment;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView mcaDirectorLabelIcon;

    public static MasterDirectorFragment getInstance(boolean newInstance) {
        if (newInstance){
            mMasterDirectorFragment = new MasterDirectorFragment();
            return mMasterDirectorFragment;
        } else {
            if (mMasterDirectorFragment == null){
                mMasterDirectorFragment = new MasterDirectorFragment();
            }
        }
        return mMasterDirectorFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_director_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        String dinNumber = GlobalVariables.stageReadyResponse.getData().getState().get("reg");
        masterDirectorDataErrorString = handShakeReadyResponse.getLocale().getMasterDirectorLocale().getFieldRegDataerror();

        masterDirectorSubmitLinearLayout = view.findViewById(R.id.submit_master_director_relative_layout);
        CommonUtils.setButtonAlphaHigh(masterDirectorSubmitLinearLayout);
        masterDirectorSubmitProgressBar = view.findViewById(R.id.submit_master_director_progress_bar);
        masterDirectorDataErrorTextView = view.findViewById(R.id.master_director_data_error);
        masterDirectorPlaceholderEditText = view.findViewById(R.id.master_director_placeholder);
        TextView mcaDirectorDataStageTitle = view.findViewById(R.id.mca_director_verification_title);
        mcaDirectorLabelIcon = view.findViewById(R.id.master_director_label_icon);
        mcaDirectorDataStageTitle.setText(handShakeReadyResponse.getLocale().getMasterDirectorLocale().getTitle());

        mcaDirectorLabelIcon.setBackgroundResource(R.drawable.ipv_start_camera);
        mcaDirectorLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        if (dinNumber != null){
            masterDirectorPlaceholderEditText.setText(dinNumber);
        }
        masterDirectorPlaceholderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        masterDirectorPlaceholderEditText.setBackgroundResource(0);
        masterDirectorPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        masterDirectorDataErrorTextView.setVisibility(View.GONE);
        masterDirectorLabelTextView = view.findViewById(R.id.master_director_label);
        masterDirectorInputErrorTextView = view.findViewById(R.id.master_director_error_text_view);
        masterDirectorSubmitButtonTextView = view.findViewById(R.id.submit_master_director_button);
        masterDirectorSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
        masterDirectorLabelTextView.setText(handShakeReadyResponse.getLocale().getMasterDirectorLocale().getFieldRegLabel());
        masterDirectorPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getMasterDirectorLocale().getFieldRegPlaceholder());
        masterDirectorSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getMasterDirectorLocale().getButtonRegProceed());
        masterDirectorSubmitProgressBar.setVisibility(View.GONE);
        masterDirectorInputErrorTextView.setVisibility(View.GONE);
        masterDirectorInputErrorTextView.setText(masterDirectorDataErrorString);
        masterDirectorSubmitLinearLayout.setClickable(true);
        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        retryAttemptsTextView = view.findViewById(R.id.master_director_retry_attempts_text_view);
        retryAttemptsTextView.setVisibility(View.GONE);

        masterDirectorPlaceholderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                masterDirectorDataErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(masterDirectorSubmitLinearLayout);
                sendGstinVerificationRequest();
                return false;
            }
            return false;
        });

        masterDirectorPlaceholderEditText.addTextChangedListener(new InputTextValidator(masterDirectorPlaceholderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isDinNumberValid = UserInputValidation.isMcaDirectorDataNumberValid(s.toString());
                if (isDinNumberValid){
                    masterDirectorInputErrorTextView.setVisibility(View.GONE);
                    masterDirectorPlaceholderEditText.setBackgroundResource(0);
                    masterDirectorPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    masterDirectorSubmitLinearLayout.setClickable(true);
                    masterDirectorSubmitProgressBar.setVisibility(View.GONE);
                    masterDirectorPlaceholderEditText.setBackgroundResource(0);
                    masterDirectorPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    masterDirectorInputErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        masterDirectorSubmitLinearLayout.setOnClickListener(v -> {
            masterDirectorDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(masterDirectorSubmitLinearLayout);
            sendGstinVerificationRequest();
        });

    }


    private void sendGstinVerificationRequest(){
        String dinNumber = masterDirectorPlaceholderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(dinNumber)) {
            if (UserInputValidation.isMcaDirectorDataNumberValid(dinNumber)) {
                sendRequest(dinNumber);
            } else {
                userInputError();
            }
        } else {
            userInputError();
        }
    }

    private void sendRequest(String dinNumber) {
        masterDirectorSubmitProgressBar.setVisibility(View.VISIBLE);
        masterDirectorSubmitLinearLayout.setClickable(false);
        masterDirectorPlaceholderEditText.setBackgroundResource(0);
        masterDirectorPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        CommonUtils.sendRequest(AttestrRequest.actionSubmitMasterDirector(dinNumber));
    }

    private void userInputError(){
        CommonUtils.setButtonAlphaHigh(masterDirectorSubmitLinearLayout);
        masterDirectorSubmitLinearLayout.setClickable(true);
        masterDirectorPlaceholderEditText.setBackgroundResource(0);
        masterDirectorPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
        masterDirectorSubmitProgressBar.setVisibility(View.GONE);
        masterDirectorInputErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnDataError(String response) {
        remainingRetryAttempts--;
        isDinNumberValid = false;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(masterDirectorSubmitLinearLayout);
            masterDirectorPlaceholderEditText.getText().clear();
            masterDirectorSubmitProgressBar.setVisibility(View.GONE);
            masterDirectorSubmitLinearLayout.setClickable(true);
            masterDirectorInputErrorTextView.setText(masterDirectorDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            masterDirectorInputErrorTextView.setVisibility(View.GONE);
            masterDirectorDataErrorTextView.setText(dataError.getData().getError().getMessage());
            masterDirectorDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(masterDirectorSubmitLinearLayout);
            masterDirectorPlaceholderEditText.getText().clear();
            masterDirectorSubmitProgressBar.setVisibility(View.GONE);
            masterDirectorSubmitLinearLayout.setClickable(true);
            masterDirectorInputErrorTextView.setText(masterDirectorDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            masterDirectorInputErrorTextView.setVisibility(View.GONE);
            masterDirectorDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            masterDirectorDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

}
