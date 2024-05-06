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

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 30/08/21
 **/
public class BankAccountVerificationFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private TextView fieldBankAccLable, fieldBankAccDataError, fieldIfscLable, fieldIfscDataError,
            bankAccountButtonProceed, accountDataErrorTextView;
    private EditText fieldAccPlaceHolder, fieldAccIfscPlaceholder;
    private ProgressBar bankAccSubmitProgressBar;
    private LinearLayout accSubmitLinearLayout;
    private boolean isBankAccountValid, isBankIfscValid;
    private boolean validateIfsc;
    private TextView bankAccountVerificationTitle;
    private static BankAccountVerificationFragment mBankAccountVerificationFragment;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private ImageView bankAccountNumberIcon, ifscCodeNumber;
    private ImageView businessAmlNumberIcon;

    public static BankAccountVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            mBankAccountVerificationFragment = new BankAccountVerificationFragment();
            return mBankAccountVerificationFragment;
        } else {
            if (mBankAccountVerificationFragment == null){
                mBankAccountVerificationFragment = new BankAccountVerificationFragment();
            }
        }
        return mBankAccountVerificationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bank_account_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        String accountNumber = GlobalVariables.stageReadyResponse.getData().getState().get("acc");
        String ifscCode = GlobalVariables.stageReadyResponse.getData().getState().get("ifsc");
        validateIfsc = Boolean.parseBoolean(Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("validateIfsc")).toString());
        fieldBankAccLable = view.findViewById(R.id.field_acc_lable);
        fieldIfscLable = view.findViewById(R.id.field_ifsc_lable);
        fieldBankAccDataError = view.findViewById(R.id.field_acc_data_error);
        fieldIfscDataError = view.findViewById(R.id.field_ifsc_data_error);
        fieldAccPlaceHolder = view.findViewById(R.id.field_acc_placeholder);
        fieldAccIfscPlaceholder = view.findViewById(R.id.field_ifsc_placeholder);
        businessAmlNumberIcon = view.findViewById(R.id.business_aml_number_icon);

        businessAmlNumberIcon.setBackgroundResource(R.drawable.id_card);
        businessAmlNumberIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        bankAccountVerificationTitle = view.findViewById(R.id.bank_account_verification_title);
        bankAccountVerificationTitle.setText(handShakeReadyResponse.getLocale().getStageAcc().getTitle());

        if (accountNumber != null){
            fieldAccPlaceHolder.setText(accountNumber);
        }
        if (ifscCode != null){
            fieldAccIfscPlaceholder.setText(ifscCode);
        }
        bankAccountButtonProceed = view.findViewById(R.id.account_button_proceed);
        accountDataErrorTextView = view.findViewById(R.id.bank_account_data_error_text_view);
        accSubmitLinearLayout = view.findViewById(R.id.acc_submit_button_relative_layout);
        bankAccSubmitProgressBar = view.findViewById(R.id.submit_account_progress_bar);
        retryAttemptsTextView = view.findViewById(R.id.bank_account_retry_attempts_text_view);
        bankAccountNumberIcon = view.findViewById(R.id.bank_account_number_icon);
        ifscCodeNumber = view.findViewById(R.id.ifsc_account_number_icon);

        bankAccountNumberIcon.setBackgroundResource(R.drawable.id_card);
        bankAccountNumberIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        ifscCodeNumber.setBackgroundResource(R.drawable.id_card);
        ifscCodeNumber.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        retryAttemptsTextView.setVisibility(View.GONE);

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        accountDataErrorTextView.setVisibility(View.GONE);
        accSubmitLinearLayout.setClickable(true);
        CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
        bankAccSubmitProgressBar.setVisibility(View.GONE);
        fieldAccPlaceHolder.setBackgroundResource(0);
        fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        fieldAccIfscPlaceholder.setBackgroundResource(0);
        fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        fieldBankAccLable.setText(handShakeReadyResponse.getLocale().getStageAcc().getFieldAccLabel());
        fieldIfscLable.setText(handShakeReadyResponse.getLocale().getStageAcc().getFieldIfscLabel());
        fieldBankAccDataError.setText(handShakeReadyResponse.getLocale().getStageAcc().getFieldAccDataerror());
        fieldIfscDataError.setText(handShakeReadyResponse.getLocale().getStageAcc().getFieldIfscDataerror());
        fieldAccPlaceHolder.setHint(handShakeReadyResponse.getLocale().getStageAcc().getFieldAccPlaceholder());
        fieldAccIfscPlaceholder.setHint(handShakeReadyResponse.getLocale().getStageAcc().getFieldIfscPlaceholder());
        fieldBankAccDataError.setVisibility(View.GONE);
        fieldIfscDataError.setVisibility(View.GONE);
        bankAccountButtonProceed.setText(handShakeReadyResponse.getLocale().getStageAcc().getButtonAccProceed());
        accSubmitLinearLayout.setOnClickListener(view1 -> {
            accountDataErrorTextView.setVisibility(View.GONE);
            sendBankAccountVerificationRequest();
        });


        fieldAccIfscPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                accountDataErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(accSubmitLinearLayout);
                sendBankAccountVerificationRequest();
                return false;
            }
            return false;
        });

        fieldAccPlaceHolder.addTextChangedListener(new InputTextValidator(fieldAccPlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isBankAccountValid = UserInputValidation.isBankAccountNumberValid(s.toString());
                if (isBankAccountValid){
                    fieldBankAccDataError.setVisibility(View.GONE);
                    fieldAccPlaceHolder.setBackgroundResource(0);
                    fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    fieldBankAccDataError.setVisibility(View.VISIBLE);
                    fieldAccPlaceHolder.setBackgroundResource(0);
                    fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
                }
            }
        });

        fieldAccIfscPlaceholder.addTextChangedListener(new InputTextValidator(fieldAccIfscPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isBankIfscValid = UserInputValidation.isIfscCodeValid(s.toString());
                if (isBankIfscValid){
                    fieldIfscDataError.setVisibility(View.GONE);
                    fieldAccIfscPlaceholder.setBackgroundResource(0);
                    fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    fieldIfscDataError.setVisibility(View.VISIBLE);
                    fieldAccIfscPlaceholder.setBackgroundResource(0);
                    fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                }
            }
        });
    }

    private void sendBankAccountVerificationRequest() {
        isBankAccountValid = UserInputValidation.isBankAccountNumberValid(fieldAccPlaceHolder.getText().toString().trim());
        isBankIfscValid = UserInputValidation.isIfscCodeValid(fieldAccIfscPlaceholder.getText().toString().trim());
        bankAccSubmitProgressBar.setVisibility(View.VISIBLE);
        CommonUtils.setButtonAlphaLow(accSubmitLinearLayout);
        String userBankAccountNumber = fieldAccPlaceHolder.getText().toString().trim();
        String userBankIfscNumber = fieldAccIfscPlaceholder.getText().toString().trim();
        if (isBankAccountValid && isBankIfscValid) {
            fieldBankAccDataError.setVisibility(View.GONE);
            fieldIfscDataError.setVisibility(View.GONE);
            bankAccSubmitProgressBar.setVisibility(View.VISIBLE);
            accSubmitLinearLayout.setClickable(false);
            CommonUtils.setButtonAlphaLow(accSubmitLinearLayout);
            CommonUtils.sendRequest(AttestrRequest.actionSubmitBankAccount(userBankAccountNumber, userBankIfscNumber));
        } else {
            accSubmitLinearLayout.setClickable(true);
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            fieldBankAccDataError.setVisibility(View.VISIBLE);
            fieldIfscDataError.setVisibility(View.VISIBLE);

            fieldAccPlaceHolder.setBackgroundResource(0);
            fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
            fieldAccIfscPlaceholder.setBackgroundResource(0);
            fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }

        if (!isBankAccountValid){
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            fieldBankAccDataError.setVisibility(View.VISIBLE);
            fieldAccPlaceHolder.setBackgroundResource(0);
            fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
        } else {
            fieldAccPlaceHolder.setBackgroundResource(0);
            fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
            fieldBankAccDataError.setVisibility(View.GONE);
        }

        if (!isBankIfscValid){
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            fieldIfscDataError.setVisibility(View.VISIBLE);
            fieldAccIfscPlaceholder.setBackgroundResource(0);
            fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        } else {
            fieldAccIfscPlaceholder.setBackgroundResource(0);
            fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
            fieldIfscDataError.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(userBankAccountNumber)){
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            fieldBankAccDataError.setVisibility(View.VISIBLE);
            fieldAccPlaceHolder.setBackgroundResource(0);
            fieldAccPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
        }

        if (TextUtils.isEmpty(userBankIfscNumber)){
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            fieldIfscDataError.setVisibility(View.VISIBLE);
            fieldAccIfscPlaceholder.setBackgroundResource(0);
            fieldAccIfscPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }
    }

    public void OnDataError(String response) {
        CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
        bankAccSubmitProgressBar.setVisibility(View.GONE);
        accSubmitLinearLayout.setClickable(true);
        isBankAccountValid = false;
        isBankIfscValid = false;
        ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
        String message = dataError.getData().getError().getMessage();
        int code = dataError.getData().getError().getCode();
        switch (code) {
            case 40011:
                // IFSC data error
                fieldAccIfscPlaceholder.getText().clear();
                accountDataErrorTextView.setText(message);
                break;
            case 40012:
                // Bank account number data error
                fieldAccPlaceHolder.getText().clear();
                accountDataErrorTextView.setText(message);
                break;
        }
        accountDataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnSessionError(String response) {
        isBankAccountValid = false;
        isBankIfscValid = false;
        remainingRetryAttempts--;
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            accSubmitLinearLayout.setClickable(true);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            accountDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            accountDataErrorTextView.setVisibility(View.VISIBLE);
            bankAccSubmitProgressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(accSubmitLinearLayout);
            accSubmitLinearLayout.setClickable(true);
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

}