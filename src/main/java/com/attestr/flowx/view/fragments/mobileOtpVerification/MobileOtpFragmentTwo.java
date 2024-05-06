package com.attestr.flowx.view.fragments.mobileOtpVerification;

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
import com.attestr.flowx.databinding.MobileOtpFragmentTwoBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.OTPGenReadyLocale;
import com.attestr.flowx.model.response.locale.MobileOtpLocale;
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
 * @Since 15/11/21
 **/
public class MobileOtpFragmentTwo extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private MobileOtpFragmentTwoBinding fragmentTwoBinding;
    private static MobileOtpFragmentTwo mobileOtpFragmentTwo;
    private Activity mActivity;
    private RelativeLayout resendOtpRelativeLayout;
    private LinearLayout submitOtpLinearLayout;
    private TextView labelTextView, dataErrorTextView, resendButtonTextView,
            submitButtonTextView, mobileOtpDataErrorTextView;
    private EditText otpPlaceHolder;
    private ProgressBar progressBar;
    private MobileOtpLocale mobileOtpLocale;
    private boolean isOtpValid;
    private String otp;
    private ResponseData<OTPGenReadyLocale> mobileOTPGenReadyLocaleResponseData;
    private TextView stageTitleTextView;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView mobileOtpTwoLabelIcon;

    public static MobileOtpFragmentTwo getInstance(boolean newInstance){
        if (newInstance){
            mobileOtpFragmentTwo = new MobileOtpFragmentTwo();
            return mobileOtpFragmentTwo;
        } else {
            if (mobileOtpFragmentTwo == null){
                mobileOtpFragmentTwo = new MobileOtpFragmentTwo();
            }
        }
        return mobileOtpFragmentTwo;
    }

    public void setData(Activity activity, String response) {
        this.mActivity = activity;
        mobileOTPGenReadyLocaleResponseData = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<OTPGenReadyLocale>>(){}.getType());
        ProgressIndicator.setActivity(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTwoBinding = MobileOtpFragmentTwoBinding.inflate(inflater, container, false);
        return fragmentTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mobileOtpLocale = GlobalVariables.handshakeReadyResponse.getLocale().getMobileOtpLocale();
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        otp = stageReadyResponse.getData().getState().get("otp");

        labelTextView = fragmentTwoBinding.mobileOtpTwoLabel;
        otpPlaceHolder = fragmentTwoBinding.mobileOtpTwoPlaceholder;
        dataErrorTextView = fragmentTwoBinding.mobileOtpTwoErrorTextView;
        resendOtpRelativeLayout = fragmentTwoBinding.mobileOtpTwoRecentOtpRelativeLayout;
        submitOtpLinearLayout = fragmentTwoBinding.mobileOtpTwoSubmitRelativeLayout;
        submitButtonTextView = fragmentTwoBinding.mobileOtpTwoSubmitButton;
        resendButtonTextView = fragmentTwoBinding.mobileOtpTwoResendOtpButton;
        progressBar = fragmentTwoBinding.mobileOtpTwoSubmitProgressBar;
        mobileOtpDataErrorTextView = fragmentTwoBinding.mobileOtpDataErrorTextView;
        stageTitleTextView = fragmentTwoBinding.mobileOtpTwoVerificationTitle;
        retryAttemptsTextView = fragmentTwoBinding.mobileOtpRetryAttemptsTextView;
        mobileOtpTwoLabelIcon = fragmentTwoBinding.mobileOtpTwoLabelIcon;
        retryAttemptsTextView.setVisibility(View.GONE);
        stageTitleTextView.setText(mobileOtpLocale.getTitle());
        mobileOtpDataErrorTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        otpPlaceHolder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        CommonUtils.setButtonAlphaHigh(submitOtpLinearLayout);
        submitOtpLinearLayout.setClickable(false);
        dataErrorTextView.setVisibility(View.GONE);

        resendOtpRelativeLayout.setClickable(true);
        resendOtpRelativeLayout.setOnClickListener(this);
        submitOtpLinearLayout.setOnClickListener(this);

        mobileOtpTwoLabelIcon.setBackgroundResource(R.drawable.id_card);
        mobileOtpTwoLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        labelTextView.setText(mobileOtpLocale.getFieldOtpLabel());
        dataErrorTextView.setText(mobileOtpLocale.getFieldOtpDataerror());
        otpPlaceHolder.setHint(mobileOtpLocale.getFieldOtpPlaceholder());
        resendButtonTextView.setText(mobileOtpLocale.getButtonResend());
        submitButtonTextView.setText(mobileOtpLocale.getButtonProceed());

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
            mobileOtpDataErrorTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String otp = otpPlaceHolder.getText().toString().trim();
            CommonUtils.setButtonAlphaLow(submitOtpLinearLayout);
            submitOtpLinearLayout.setClickable(false);
            CommonUtils.sendRequest(AttestrRequest.actionVerifyMobileOtp(
                    otp,
                    mobileOTPGenReadyLocaleResponseData.getData().getId()));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == resendOtpRelativeLayout){
            mobileOtpDataErrorTextView.setVisibility(View.GONE);
            resendOtpRelativeLayout.setClickable(false);
            ProgressIndicator.setProgressBarVisible();
            if (GlobalVariables.isOTPExpired){
                CommonUtils.sendRequest(AttestrRequest.actionGenerateMobileOtp());
            } else {
                CommonUtils.sendRequest(AttestrRequest.actionResendMobileOtp(mobileOTPGenReadyLocaleResponseData.getData().getId()));
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
            resendButtonTextView.setText(mobileOtpLocale.getButtonRegen());
        } else {
            resendButtonTextView.setText(mobileOtpLocale.getButtonResend());
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
            mobileOtpDataErrorTextView.setText(dataError.getData().getError().getMessage());
            mobileOtpDataErrorTextView.setVisibility(View.VISIBLE);
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
            mobileOtpDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            mobileOtpDataErrorTextView.setVisibility(View.VISIBLE);
            if (dataError.getData().getError().getHttpStatusCode() == 400 &&
                    dataError.getData().getError().getCode() == 40012){
                setOTPStatus(true);
            }
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

}
