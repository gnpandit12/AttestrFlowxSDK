package com.attestr.flowx.view.fragments.mobileOtpVerification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.attestr.flowx.databinding.MobileOtpFragmentOneBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.MobileOtpLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 15/11/21
 **/
public class MobileOtpFragmentOne extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private MobileOtpFragmentOneBinding mobileOtpFragmentOneBinding;
    private static MobileOtpFragmentOne mobileOtpFragmentOne;
    private Activity mActivity;
    private TextView labelTextView, dataErrorTextView, sendOtpButtonTextView;
    private EditText mobileNumberPlaceholder;
    private LinearLayout sentOtpLinearLayout;
    private ProgressBar progressBar;
    private MobileOtpLocale mobileOtpLocale;
    private boolean isMobileNumberValid;
    private String mobile;
    private boolean allowUpdate;
    private TextView stageTitleTextView;
    private ImageView mobileOtpOneLabelIcon;

    public static MobileOtpFragmentOne getInstance(boolean newInstance) {
        if (newInstance){
            mobileOtpFragmentOne = new MobileOtpFragmentOne();
            return mobileOtpFragmentOne;
        } else {
            if (mobileOtpFragmentOne == null){
                mobileOtpFragmentOne = new MobileOtpFragmentOne();
            }
        }
        return mobileOtpFragmentOne;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mobileOtpFragmentOneBinding = MobileOtpFragmentOneBinding.inflate(inflater, container, false);
        return mobileOtpFragmentOneBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mobileOtpLocale = GlobalVariables.handshakeReadyResponse.getLocale().getMobileOtpLocale();

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        mobile = stageReadyResponse.getData().getState().get("mobile");

        labelTextView = mobileOtpFragmentOneBinding.mobileOtpOneLabel;
        dataErrorTextView = mobileOtpFragmentOneBinding.mobileOtpOneErrorTextView;
        sendOtpButtonTextView = mobileOtpFragmentOneBinding.mobileOtpOneSubmitButton;
        sentOtpLinearLayout = mobileOtpFragmentOneBinding.mobileOtpOneSubmitRelativeLayout;
        progressBar = mobileOtpFragmentOneBinding.mobileOtpOneSubmitProgressBar;
        mobileNumberPlaceholder = mobileOtpFragmentOneBinding.mobileOtpOnePlaceholder;
        stageTitleTextView = mobileOtpFragmentOneBinding.mobileOtpOneVerificationTitle;
        mobileOtpOneLabelIcon = mobileOtpFragmentOneBinding.mobileOtpOneLabelIcon;
        stageTitleTextView.setText(mobileOtpLocale.getTitle());
        progressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(sentOtpLinearLayout);
        sentOtpLinearLayout.setClickable(true);

        allowUpdate = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("allowUpdate")).toString());
        mobileNumberPlaceholder.setEnabled(allowUpdate);

        mobileOtpOneLabelIcon.setBackgroundResource(R.drawable.id_card);
        mobileOtpOneLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        labelTextView.setText(mobileOtpLocale.getFieldMobileLabel());
        dataErrorTextView.setText(mobileOtpLocale.getFieldMobileDataerror());
        dataErrorTextView.setVisibility(View.GONE);
        sendOtpButtonTextView.setText(mobileOtpLocale.getButtonGen());
        mobileNumberPlaceholder.setHint(mobileOtpLocale.getFieldMobilePlaceholder());

        if (mobile != null){
            mobileNumberPlaceholder.setText(mobile);
        }

        mobileNumberPlaceholder.addTextChangedListener(new InputTextValidator(mobileNumberPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isMobileNumberValid = UserInputValidation.validateMobileNumber(s.toString());
                if (isMobileNumberValid){
                    mobileNumberValid(false);
                } else {
                    mobileNumberInvalid();
                }
            }
        });

        mobileNumberPlaceholder.setBackgroundResource(0);
        mobileNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        sentOtpLinearLayout.setOnClickListener(this);

        mobileNumberPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (isMobileNumberValid){
                    mobileNumberValid(true);
                } else {
                    mobileNumberInvalid();
                }
                return false;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        if (v == sentOtpLinearLayout){
            isMobileNumberValid = UserInputValidation.validateMobileNumber(mobileNumberPlaceholder.getText().toString().trim());
            if (isMobileNumberValid){
                mobileNumberValid(true);
            } else {
                mobileNumberInvalid();
            }
        }
    }

    private void mobileNumberValid(boolean sendRequest) {
        mobileNumberPlaceholder.setBackgroundResource(0);
        mobileNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        dataErrorTextView.setVisibility(View.GONE);
        if (sendRequest) {
            progressBar.setVisibility(View.VISIBLE);
            GlobalVariables.mobileOTPVerificationNumber = mobileNumberPlaceholder.getText().toString().trim();
            CommonUtils.setButtonAlphaLow(sentOtpLinearLayout);
            sentOtpLinearLayout.setClickable(false);
            CommonUtils.sendRequest(AttestrRequest.actionGenerateMobileOtp());
        }
    }

    private void mobileNumberInvalid(){
        mobileNumberPlaceholder.setBackgroundResource(0);
        mobileNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        dataErrorTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        sentOtpLinearLayout.setClickable(true);
    }

}
