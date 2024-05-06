package com.attestr.flowx.view.fragments.emailOTPVerification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.EmailOtpFragmentTwoBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.EmailOTPLocale;
import com.attestr.flowx.model.response.locale.OTPGenReadyLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 04/04/22
 **/
public class EmailOTPFragmentTwo extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private EmailOtpFragmentTwoBinding fragmentTwoBinding;
    private static EmailOTPFragmentTwo emailOTPFragmentTwo;
    private Activity mActivity;
    private RelativeLayout resendOtpRelativeLayout;
    private LinearLayout submitOtpLinearLayout;
    private TextView labelTextView, dataErrorTextView, resendButtonTextView,
            submitButtonTextView, emailOtpDataErrorTextView;
    private EditText otpPlaceHolder;
    private ProgressBar progressBar;
    private EmailOTPLocale emailOTPLocale;
    private boolean isOtpValid;
    private String otp;
    private ResponseData<OTPGenReadyLocale> emailOTPGenReadyLocaleResponseData;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView emailOtpTwoLabelIcon;

    public static EmailOTPFragmentTwo getInstance(boolean newInstance){
        if (newInstance){
            emailOTPFragmentTwo = new EmailOTPFragmentTwo();
            return emailOTPFragmentTwo;
        } else {
            if (emailOTPFragmentTwo == null){
                emailOTPFragmentTwo = new EmailOTPFragmentTwo();
            }
        }
        return emailOTPFragmentTwo;
    }

    public void setData(Activity activity, String response) {
        this.mActivity = activity;
        emailOTPGenReadyLocaleResponseData = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<OTPGenReadyLocale>>(){}.getType());
        ProgressIndicator.setActivity(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTwoBinding = EmailOtpFragmentTwoBinding.inflate(inflater, container, false);
        return fragmentTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailOTPLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEmailOTPLocale();
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        otp = stageReadyResponse.getData().getState().get("otp");

        labelTextView = fragmentTwoBinding.emailOtpTwoLabel;
        otpPlaceHolder = fragmentTwoBinding.emailOtpTwoPlaceholder;
        dataErrorTextView = fragmentTwoBinding.emailOtpTwoErrorTextView;
        resendOtpRelativeLayout = fragmentTwoBinding.emailOtpTwoRecendOtpRelativeLayout;
        submitOtpLinearLayout = fragmentTwoBinding.emailOtpTwoSubmitRelativeLayout;
        submitButtonTextView = fragmentTwoBinding.emailOtpTwoSubmitButton;
        resendButtonTextView = fragmentTwoBinding.emailOtpTwoResendOtpButton;
        progressBar = fragmentTwoBinding.emailOtpTwoSubmitProgressBar;
        emailOtpDataErrorTextView = fragmentTwoBinding.emailOtpDataErrorTextView;
        retryAttemptsTextView = fragmentTwoBinding.emailOtpRetryAttemptsTextView;
        emailOtpTwoLabelIcon = fragmentTwoBinding.emailOtpTwoLabelIcon;
        retryAttemptsTextView.setVisibility(View.GONE);
        emailOtpDataErrorTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        emailOtpTwoLabelIcon.setBackgroundResource(R.drawable.id_card);
        emailOtpTwoLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        otpPlaceHolder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        CommonUtils.setButtonAlphaHigh(submitOtpLinearLayout);
        submitOtpLinearLayout.setClickable(false);
        dataErrorTextView.setVisibility(View.GONE);

        resendOtpRelativeLayout.setClickable(true);
        resendOtpRelativeLayout.setOnClickListener(this);
        submitOtpLinearLayout.setOnClickListener(this);

        labelTextView.setText(emailOTPLocale.getFieldOtpLabel());
        dataErrorTextView.setText(emailOTPLocale.getFieldOtpDataerror());
        otpPlaceHolder.setHint(emailOTPLocale.getFieldOtpPlaceholder());
        resendButtonTextView.setText(emailOTPLocale.getButtonResend());
        submitButtonTextView.setText(emailOTPLocale.getButtonProceed());

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        if (otp != null) {
            otpPlaceHolder.setText(otp);
        }

        otpPlaceHolder.addTextChangedListener(new InputTextValidator(otpPlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isOtpValid = UserInputValidation.validateOtp(s.toString());
                if (isOtpValid){
                    otpValid(false);
                } else {
                    otpInvalid();
                }
            }
        });

        otpPlaceHolder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (isOtpValid){
                    otpValid(true);
                } else {
                    otpInvalid();
                }
                return false;
            }
            return false;
        });

        otpPlaceHolder.setBackgroundResource(0);
        otpPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

    }

    private void otpInvalid() {
        otpPlaceHolder.setBackgroundResource(0);
        otpPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
        dataErrorTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        resendOtpRelativeLayout.setClickable(true);
        submitOtpLinearLayout.setClickable(true);
    }

    private void otpValid(boolean sendRequest) {
        otpPlaceHolder.setBackgroundResource(0);
        otpPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        dataErrorTextView.setVisibility(View.GONE);
        if (sendRequest) {
            emailOtpDataErrorTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String otp = otpPlaceHolder.getText().toString().trim();
            CommonUtils.setButtonAlphaLow(submitOtpLinearLayout);
            submitOtpLinearLayout.setClickable(false);
            CommonUtils.sendRequest(
                    AttestrRequest.actionVerifyEmailOtp(
                            otp,
                            emailOTPGenReadyLocaleResponseData.getData().getId()));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == resendOtpRelativeLayout) {
            emailOtpDataErrorTextView.setVisibility(View.GONE);
            resendOtpRelativeLayout.setClickable(false);
            ProgressIndicator.setProgressBarVisible();
            if (GlobalVariables.isOTPExpired) {
                CommonUtils.sendRequest(AttestrRequest.actionGenerateEmailOtp());
            } else {
                CommonUtils.sendRequest(AttestrRequest.actionResendEmailOtp(emailOTPGenReadyLocaleResponseData.getData().getId()));
            }
            resendOtpRelativeLayout.setClickable(true);
        } else if (v == submitOtpLinearLayout){
            if (isOtpValid){
                otpValid(true);
            } else {
                otpInvalid();
            }
        }
    }

    public void setOTPStatus(boolean otpExpired) {
        GlobalVariables.isOTPExpired = otpExpired;
        if (otpExpired){
            resendButtonTextView.setText(emailOTPLocale.getButtonRegen());
        } else {
            resendButtonTextView.setText(emailOTPLocale.getButtonResend());
            ProgressIndicator.setProgressBarInvisible();
        }
    }

    public void OnDataError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            progressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(submitOtpLinearLayout);
            submitOtpLinearLayout.setClickable(true);
            resendOtpRelativeLayout.setClickable(true);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            emailOtpDataErrorTextView.setText(dataError.getData().getError().getMessage());
            emailOtpDataErrorTextView.setVisibility(View.VISIBLE);
            if (dataError.getData().getError().getHttpStatusCode() == 400 &&
                    dataError.getData().getError().getCode() == 40012){
                setOTPStatus(true);
            }
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            progressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(submitOtpLinearLayout);
            submitOtpLinearLayout.setClickable(true);
            resendOtpRelativeLayout.setClickable(true);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            emailOtpDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            emailOtpDataErrorTextView.setVisibility(View.VISIBLE);
            if (dataError.getData().getError().getHttpStatusCode() == 400 &&
                    dataError.getData().getError().getCode() == 40012){
                setOTPStatus(true);
            }
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

}

