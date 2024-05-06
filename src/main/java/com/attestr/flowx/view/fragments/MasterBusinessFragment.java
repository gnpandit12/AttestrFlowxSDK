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
 * @Since 31/08/21
 **/
public class MasterBusinessFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private TextView mcaDataLabelTextView, mcaDataDataErrorTextView, mcaDataInputDataErrorTextView, mcaDataSubmitButtonTextView;
    private EditText mcaDataPlaceholderEditText;
    private LinearLayout mcaDataSubmitLinearLayout;
    private ProgressBar mcaDataSubmitProgressBar;
    private String mcaDataDataErrorString;
    private boolean ismcaDataNumberValid;
    private static MasterBusinessFragment mMasterBusinessFragment;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView mcaCompanyLabelIcon;

    public static MasterBusinessFragment getInstance(boolean newInstance) {
        if (newInstance){
            mMasterBusinessFragment = new MasterBusinessFragment();
            return mMasterBusinessFragment;
        } else {
            if (mMasterBusinessFragment == null){
                mMasterBusinessFragment = new MasterBusinessFragment();
            }
        }
        return mMasterBusinessFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_business_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        String registrationNumber = GlobalVariables.stageReadyResponse.getData().getState().get("reg");
        mcaDataDataErrorString = handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegDataerror();

        mcaDataSubmitLinearLayout = view.findViewById(R.id.submit_mca_company_data_Relative_layout);
        CommonUtils.setButtonAlphaHigh(mcaDataSubmitLinearLayout);
        mcaDataSubmitProgressBar = view.findViewById(R.id.submit_mca_company_data_progress_bar);
        mcaDataDataErrorTextView = view.findViewById(R.id.mca_company_data_data_error);
        mcaDataPlaceholderEditText = view.findViewById(R.id.mca_company_data_placeholder);
        TextView mcaCompanyDataStageTitle = view.findViewById(R.id.mca_company_verification_title);
        retryAttemptsTextView = view.findViewById(R.id.mca_company_data_retry_attempts_text_view);
        mcaCompanyLabelIcon = view.findViewById(R.id.mca_company_data_label_icon);
        retryAttemptsTextView.setVisibility(View.GONE);
        mcaCompanyDataStageTitle.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getTitle());


        mcaCompanyLabelIcon.setBackgroundResource(R.drawable.id_card);
        mcaCompanyLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        if (registrationNumber != null){
            mcaDataPlaceholderEditText.setText(registrationNumber);
        }
        mcaDataPlaceholderEditText.setBackgroundResource(0);
        mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        mcaDataDataErrorTextView.setVisibility(View.GONE);
        mcaDataLabelTextView = view.findViewById(R.id.mca_company_data_label);
        mcaDataInputDataErrorTextView = view.findViewById(R.id.mca_company_data_error_text_view);
        mcaDataSubmitButtonTextView = view.findViewById(R.id.submit_mca_company_data_button);
        mcaDataSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
            mcaDataSubmitProgressBar.setVisibility(View.GONE);
            mcaDataInputDataErrorTextView.setVisibility(View.GONE);
            mcaDataLabelTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegLabel());
            mcaDataPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegPlaceholder());
            mcaDataSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getButtonRegProceed());
            mcaDataInputDataErrorTextView.setText(mcaDataDataErrorString);
            mcaDataSubmitLinearLayout.setClickable(true);

        mcaDataPlaceholderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                mcaDataDataErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(mcaDataSubmitLinearLayout);
                sendMcaDataVerificationRequest();
                return false;
            }
            return false;
        });

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        mcaDataPlaceholderEditText.addTextChangedListener(new InputTextValidator(mcaDataPlaceholderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                ismcaDataNumberValid = UserInputValidation.isMcaCompanyDataNumberValid(s.toString());
                if (ismcaDataNumberValid){
                    mcaDataInputDataErrorTextView.setVisibility(View.GONE);
                    mcaDataPlaceholderEditText.setBackgroundResource(0);
                    mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    mcaDataSubmitLinearLayout.setClickable(true);
                    mcaDataSubmitProgressBar.setVisibility(View.GONE);
                    mcaDataPlaceholderEditText.setBackgroundResource(0);
                    mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    mcaDataInputDataErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        mcaDataSubmitLinearLayout.setOnClickListener(v -> {
            mcaDataDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(mcaDataSubmitLinearLayout);
            sendMcaDataVerificationRequest();
        });

    }

    private void sendMcaDataVerificationRequest(){
        String cinNumber = mcaDataPlaceholderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(cinNumber)) {
            if (UserInputValidation.isMcaCompanyDataNumberValid(cinNumber)) {
                mcaDataSubmitProgressBar.setVisibility(View.VISIBLE);
                mcaDataSubmitLinearLayout.setClickable(false);
                mcaDataPlaceholderEditText.setBackgroundResource(0);
                mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                CommonUtils.sendRequest(AttestrRequest.actionSubmitMasterBusiness(cinNumber));
            } else {
                CommonUtils.setButtonAlphaHigh(mcaDataSubmitLinearLayout);
                mcaDataPlaceholderEditText.setBackgroundResource(0);
                mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                mcaDataSubmitProgressBar.setVisibility(View.GONE);
                mcaDataInputDataErrorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            CommonUtils.setButtonAlphaHigh(mcaDataSubmitLinearLayout);
            mcaDataSubmitLinearLayout.setClickable(true);
            mcaDataSubmitProgressBar.setVisibility(View.GONE);
            mcaDataPlaceholderEditText.setBackgroundResource(0);
            mcaDataPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
            mcaDataInputDataErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnDataError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(mcaDataSubmitLinearLayout);
            mcaDataLabelTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegLabel());
            mcaDataPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegPlaceholder());
            mcaDataSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getButtonRegProceed());
            mcaDataPlaceholderEditText.getText().clear();
            mcaDataSubmitProgressBar.setVisibility(View.GONE);
            mcaDataSubmitLinearLayout.setClickable(true);
            mcaDataInputDataErrorTextView.setText(mcaDataDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            mcaDataInputDataErrorTextView.setVisibility(View.GONE);
            mcaDataDataErrorTextView.setText(dataError.getData().getError().getMessage());
            mcaDataDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(mcaDataSubmitLinearLayout);
            mcaDataLabelTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegLabel());
            mcaDataPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getFieldRegPlaceholder());
            mcaDataSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getButtonRegProceed());
            mcaDataPlaceholderEditText.getText().clear();
            mcaDataSubmitProgressBar.setVisibility(View.GONE);
            mcaDataSubmitLinearLayout.setClickable(true);
            mcaDataInputDataErrorTextView.setText(mcaDataDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            mcaDataInputDataErrorTextView.setVisibility(View.GONE);
            mcaDataDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            mcaDataDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

}
