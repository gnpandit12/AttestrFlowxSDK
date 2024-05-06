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
import com.attestr.flowx.model.response.locale.VoterIDLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 * @Since 08/01/23
 **/
public class  VoterIDVerificationFragment extends Fragment {

    private static VoterIDVerificationFragment mVoterIDVerificationFragment;
    private HandshakeReadyData handShakeReadyResponse;
    private Activity mActivity;
    private TextView voterIDStageTitleTextView, voterIDVerificationLableTextView,
            voterIDVerificationDataErrorTextView, voterIDVerificationInputDataErrorTextView, voterIDVerificationSubmitButtonTextView;
    private EditText voterIDVerificationPlaceholderEditText;
    private ProgressBar voterIDVerificationSubmitProgressBar;
    private String voterIDVerificationDataErrorString;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private VoterIDLocale voterIDLocale;
    private LinearLayout voterIDSubmitLinearLayout;
    private String voterIDNumber;
    private boolean isVoterIDValid = false;
    private ImageView voterIDLabelIcon;

    public static VoterIDVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            mVoterIDVerificationFragment = new VoterIDVerificationFragment();
            return mVoterIDVerificationFragment;
        } else {
            if (mVoterIDVerificationFragment == null){
                mVoterIDVerificationFragment = new VoterIDVerificationFragment();
            }
        }
        return mVoterIDVerificationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.voter_id_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        voterIDLocale = handShakeReadyResponse.getLocale().getVoterIDLocale();

        voterIDVerificationDataErrorString = handShakeReadyResponse.getLocale().getVoterIDLocale().getFieldEpicDataerror();

        voterIDSubmitLinearLayout = view.findViewById(R.id.voter_id_submit_linear_layout);
        CommonUtils.setButtonAlphaHigh(voterIDSubmitLinearLayout);
        voterIDVerificationSubmitProgressBar = view.findViewById(R.id.voter_id_submit_progress_bar);
        voterIDVerificationDataErrorTextView = view.findViewById(R.id.voter_id_data_error_text_view);
        voterIDLabelIcon = view.findViewById(R.id.field_vap_label_icon);

        if (voterIDNumber != null) {
            voterIDVerificationPlaceholderEditText.setText(voterIDNumber);
        }

        voterIDLabelIcon.setBackgroundResource(R.drawable.id_card);
        voterIDLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();
        voterIDVerificationPlaceholderEditText = view.findViewById(R.id.voter_id_placeholder);
        voterIDVerificationPlaceholderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
        voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        voterIDVerificationDataErrorTextView.setVisibility(View.GONE);
        voterIDStageTitleTextView = view.findViewById(R.id.voter_id_stage_title);
        voterIDVerificationLableTextView = view.findViewById(R.id.voter_id_label);
        voterIDVerificationInputDataErrorTextView = view.findViewById(R.id.voter_id_input_error_text_view);
        voterIDVerificationSubmitButtonTextView = view.findViewById(R.id.voter_id_submit_button);
        voterIDSubmitLinearLayout = view.findViewById(R.id.voter_id_submit_linear_layout);
        voterIDVerificationSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
        voterIDStageTitleTextView.setText(voterIDLocale.getTitle());
        voterIDVerificationLableTextView.setText(voterIDLocale.getFieldEpicLabel());
        voterIDVerificationPlaceholderEditText.setHint(voterIDLocale.getFieldEpicPlaceholder());
        voterIDVerificationSubmitButtonTextView.setText(voterIDLocale.getButtonEpicProceed());
        voterIDVerificationSubmitProgressBar.setVisibility(View.GONE);
        voterIDVerificationInputDataErrorTextView.setVisibility(View.GONE);
        voterIDVerificationInputDataErrorTextView.setText(voterIDVerificationDataErrorString);
        voterIDSubmitLinearLayout.setClickable(true);

        retryAttemptsTextView = view.findViewById(R.id.voter_id_retry_attempt_text_view);
        retryAttemptsTextView.setVisibility(View.GONE);

        voterIDVerificationPlaceholderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                voterIDVerificationInputDataErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(voterIDSubmitLinearLayout);
                sendVoterIDVerificationRequest();
                return false;
            }
            return false;
        });

        voterIDVerificationPlaceholderEditText.addTextChangedListener(new InputTextValidator(voterIDVerificationPlaceholderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isVoterIDValid = UserInputValidation.isVoterIDNumberValid(s.toString());
                if (isVoterIDValid){
                    voterIDVerificationInputDataErrorTextView.setVisibility(View.INVISIBLE);
                    voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
                    voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    voterIDSubmitLinearLayout.setClickable(true);
                    voterIDVerificationSubmitProgressBar.setVisibility(View.GONE);
                    voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
                    voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    voterIDVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
                }
                voterIDVerificationInputDataErrorTextView.setVisibility(View.INVISIBLE);
                voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
                voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
            }
        });

        voterIDSubmitLinearLayout.setOnClickListener(v -> {
            voterIDVerificationInputDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(voterIDSubmitLinearLayout);
            sendVoterIDVerificationRequest();
        });

    }

    public void setData(Activity activity) {this.mActivity = activity;}

    private void sendVoterIDVerificationRequest() {
        String voterID = voterIDVerificationPlaceholderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(voterID)) {
            if (UserInputValidation.isVoterIDNumberValid(voterID)) {
                sendRequest(voterID);
            } else {
                userInputError();
            }
        } else {
            userInputError();
        }
    }

    private void sendRequest(String voterIDNumber) {
        voterIDVerificationSubmitProgressBar.setVisibility(View.VISIBLE);
        voterIDSubmitLinearLayout.setClickable(false);
        voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
        voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        CommonUtils.sendRequest(AttestrRequest.actionSubmitVoterID(voterIDNumber));
    }

    private void userInputError(){
        CommonUtils.setButtonAlphaHigh(voterIDSubmitLinearLayout);
        voterIDSubmitLinearLayout.setClickable(true);
        voterIDVerificationPlaceholderEditText.setBackgroundResource(0);
        voterIDVerificationPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
        voterIDVerificationSubmitProgressBar.setVisibility(View.GONE);
        voterIDVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnDataError(String response) {
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(voterIDSubmitLinearLayout);
            voterIDVerificationPlaceholderEditText.getText().clear();
            voterIDVerificationSubmitProgressBar.setVisibility(View.GONE);
            voterIDSubmitLinearLayout.setClickable(true);
            voterIDVerificationInputDataErrorTextView.setText(voterIDVerificationDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            voterIDVerificationInputDataErrorTextView.setVisibility(View.GONE);
            voterIDVerificationDataErrorTextView.setText(dataError.getData().getError().getMessage());
            voterIDVerificationDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
        remainingRetryAttempts--;
    }

    public void OnSessionError(String response) {
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(voterIDSubmitLinearLayout);
            voterIDVerificationPlaceholderEditText.getText().clear();
            voterIDVerificationSubmitProgressBar.setVisibility(View.GONE);
            voterIDSubmitLinearLayout.setClickable(true);
            voterIDVerificationInputDataErrorTextView.setText(voterIDVerificationDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            voterIDVerificationInputDataErrorTextView.setVisibility(View.GONE);
            voterIDVerificationDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            voterIDVerificationDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
        remainingRetryAttempts--;
    }


}
