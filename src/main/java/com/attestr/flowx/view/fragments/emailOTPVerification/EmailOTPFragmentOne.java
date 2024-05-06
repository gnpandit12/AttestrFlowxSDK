package com.attestr.flowx.view.fragments.emailOTPVerification;

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
import com.attestr.flowx.databinding.EmailOtpFragmentOneBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.EmailOTPLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 04/04/22
 **/
public class EmailOTPFragmentOne extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private EmailOtpFragmentOneBinding emailOtpFragmentOneBinding;
    private static EmailOTPFragmentOne emailOTPFragmentOne;
    private Activity mActivity;
    private TextView labelTextView, dataErrorTextView, sendOtpButtonTextView;
    private EditText emailAddressPlaceholder;
    private LinearLayout sentOtpLinearLayout;
    private ProgressBar progressBar;
    private EmailOTPLocale emailOTPLocale;
    private boolean isEmailAddressValid;
    private String emailAddress, otp;
    private boolean allowUpdate;
    private ImageView emailOtpOneLabelIcon;

    public EmailOTPFragmentOne(){}

    public static EmailOTPFragmentOne getInstance(boolean newInstance) {
        if (newInstance){
            emailOTPFragmentOne = new EmailOTPFragmentOne();
            return emailOTPFragmentOne;
        } else {
            if (emailOTPFragmentOne == null){
                emailOTPFragmentOne = new EmailOTPFragmentOne();
            }
        }
        return emailOTPFragmentOne;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        emailOtpFragmentOneBinding = EmailOtpFragmentOneBinding.inflate(inflater, container, false);
        return emailOtpFragmentOneBinding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailOTPLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEmailOTPLocale();

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        emailAddress = stageReadyResponse.getData().getState().get("email");
        otp = stageReadyResponse.getData().getState().get("otp");

        labelTextView = emailOtpFragmentOneBinding.emailOtpOneLabel;
        dataErrorTextView = emailOtpFragmentOneBinding.emailOtpOneErrorTextView;
        sendOtpButtonTextView = emailOtpFragmentOneBinding.emailOtpOneSubmitButton;
        sentOtpLinearLayout = emailOtpFragmentOneBinding.emailOtpOneSubmitRelativeLayout;
        progressBar = emailOtpFragmentOneBinding.emailOtpOneSubmitProgressBar;
        emailAddressPlaceholder = emailOtpFragmentOneBinding.emailOtpOnePlaceholder;
        emailOtpOneLabelIcon = emailOtpFragmentOneBinding.emailOtpOneLabelIcon;

        emailOtpOneLabelIcon.setBackgroundResource(R.drawable.id_card);
        emailOtpOneLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        progressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(sentOtpLinearLayout);
        sentOtpLinearLayout.setClickable(true);

        allowUpdate = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("allowUpdate")).toString());
        emailAddressPlaceholder.setEnabled(allowUpdate);

        labelTextView.setText(emailOTPLocale.getFieldEmailLabel());
        dataErrorTextView.setText(emailOTPLocale.getFieldEmailDataerror());
        dataErrorTextView.setVisibility(View.GONE);
        sendOtpButtonTextView.setText(emailOTPLocale.getButtonGen());
        emailAddressPlaceholder.setHint(emailOTPLocale.getFieldEmailPlaceholder());

        if (emailAddress != null){
            emailAddressPlaceholder.setText(emailAddress);
        }

        emailAddressPlaceholder.addTextChangedListener(new InputTextValidator(emailAddressPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isEmailAddressValid = UserInputValidation.validateEmailID(s.toString());
                if (isEmailAddressValid){
                    emailAddressValid(false);
                } else {
                    emailAddressInvalid();
                }
            }
        });

        emailAddressPlaceholder.setBackgroundResource(0);
        emailAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        sentOtpLinearLayout.setOnClickListener(this);

        emailAddressPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (isEmailAddressValid){
                    emailAddressValid(true);
                } else {
                    emailAddressInvalid();
                }
                return false;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        if (v == sentOtpLinearLayout){
            isEmailAddressValid = UserInputValidation.validateEmailID(emailAddressPlaceholder.getText().toString().trim());
            if (isEmailAddressValid){
                emailAddressValid(true);
            } else {
                emailAddressInvalid();
            }
        }
    }

    private void emailAddressValid(boolean sendRequest) {
        emailAddressPlaceholder.setBackgroundResource(0);
        emailAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        dataErrorTextView.setVisibility(View.GONE);
        if (sendRequest) {
            progressBar.setVisibility(View.VISIBLE);
            CommonUtils.setButtonAlphaLow(sentOtpLinearLayout);
            sentOtpLinearLayout.setClickable(false);
            GlobalVariables.emailOTPVerificationAddress = emailAddressPlaceholder.getText().toString().trim();
            CommonUtils.sendRequest(AttestrRequest.actionGenerateEmailOtp());
        }
    }

    private void emailAddressInvalid() {
        emailAddressPlaceholder.setBackgroundResource(0);
        emailAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        dataErrorTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        sentOtpLinearLayout.setClickable(true);
    }
    
}
