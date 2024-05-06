package com.attestr.flowx.view.fragments;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.FssaiVerificationFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.FssaiLocale;
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
 * @Since 05/04/22
 **/
public class FssaiVerificationFragment extends Fragment
        implements View.OnClickListener {

    private static FssaiVerificationFragment fssaiVerificationFragment;
    private Activity mActivity;
    private ResponseData<StageInitData> stageReadyResponse;
    private FssaiVerificationFragmentBinding fssaiVerificationFragmentBinding;
    private TextView labelTextView, dataErrorTextView,
            inputErrorTextView, submitButtonTextView, fssaiStageTitleTextView;
    private EditText fssaiNumberPlaceholder;
    private LinearLayout submitButtonLinearLayout;
    private ProgressBar submitButtonProgressBar;
    private FssaiLocale fssaiLocale;
    private boolean isFssaiNumberValid;
    private String fssaiNumber;
    private boolean fetchProducts, retryInvalid;
    private ImageView fssaiLabelIcon;

    public static FssaiVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            fssaiVerificationFragment = new FssaiVerificationFragment();
            return fssaiVerificationFragment;
        } else {
            if (fssaiVerificationFragment == null){
                fssaiVerificationFragment = new FssaiVerificationFragment();
            }
        }
        return fssaiVerificationFragment;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fssaiVerificationFragmentBinding = FssaiVerificationFragmentBinding.inflate(inflater, container, false);
        return fssaiVerificationFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fssaiLocale = GlobalVariables.handshakeReadyResponse.getLocale().getFssaiLocale();

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        fssaiNumber = stageReadyResponse.getData().getState().get("reg");

        labelTextView = fssaiVerificationFragmentBinding.fssaiLabel;
        dataErrorTextView = fssaiVerificationFragmentBinding.fssaiDataErrorTextView;
        submitButtonLinearLayout = fssaiVerificationFragmentBinding.fssaiSubmitLinearLayout;
        inputErrorTextView = fssaiVerificationFragmentBinding.fssaiInputErrorTextView;
        submitButtonProgressBar = fssaiVerificationFragmentBinding.fssaiSubmitProgressBar;
        fssaiNumberPlaceholder = fssaiVerificationFragmentBinding.fssaiNumberPlaceholder;
        submitButtonTextView = fssaiVerificationFragmentBinding.fssaiSubmitButton;
        fssaiStageTitleTextView = fssaiVerificationFragmentBinding.fssaiVerificationTitle;
        fssaiLabelIcon = fssaiVerificationFragmentBinding.fssaiLabelIcon;
        fssaiStageTitleTextView.setText(GlobalVariables.handshakeReadyResponse.getLocale().getFssaiLocale().getTitle());

        submitButtonProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(submitButtonLinearLayout);
        submitButtonLinearLayout.setClickable(true);

        fssaiLabelIcon.setBackgroundResource(R.drawable.id_card);
        fssaiLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        StageReady stageReady = new StageReady();

        fetchProducts = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("fetchProducts")).toString());
        retryInvalid = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("retryInvalid")).toString());

        labelTextView.setText(fssaiLocale.getFieldFssaiLabel());
        dataErrorTextView.setText(fssaiLocale.getFieldFssaiDataerror());
        dataErrorTextView.setVisibility(View.GONE);
        submitButtonTextView.setText(fssaiLocale.getButtonProceed());
        inputErrorTextView.setText(fssaiLocale.getInvalidFssai());
        fssaiNumberPlaceholder.setHint(fssaiLocale.getFieldFssaiPlaceholder());

        if (fssaiNumber != null) {
            fssaiNumberPlaceholder.setText(fssaiNumber);
        }

        fssaiNumberPlaceholder.addTextChangedListener(new InputTextValidator(fssaiNumberPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isFssaiNumberValid = UserInputValidation.validateFssaiNumber(fssaiNumberPlaceholder.getText().toString().trim());
                if (isFssaiNumberValid) {
                    fssaiNumberValid(false);
                } else {
                    fssaiNumberInvalid();
                }
            }
        });

        fssaiNumberPlaceholder.setBackgroundResource(0);
        fssaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        fssaiNumberPlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        submitButtonLinearLayout.setOnClickListener(this);

        fssaiNumberPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (isFssaiNumberValid){
                    fssaiNumberValid(true);
                } else {
                    fssaiNumberInvalid();
                }
                return false;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        if (v == submitButtonLinearLayout) {
            isFssaiNumberValid = UserInputValidation.validateFssaiNumber(fssaiNumberPlaceholder.getText().toString().trim());
            if (isFssaiNumberValid) {
                fssaiNumberValid(true);
            } else {
                fssaiNumberInvalid();
            }
        }
    }

    private void fssaiNumberValid(boolean sendRequest) {
        fssaiNumberPlaceholder.setBackgroundResource(0);
        fssaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        dataErrorTextView.setVisibility(View.GONE);
        inputErrorTextView.setVisibility(View.GONE);
        if (sendRequest) {
            submitButtonProgressBar.setVisibility(View.VISIBLE);
            GlobalVariables.mobileOTPVerificationNumber = fssaiNumberPlaceholder.getText().toString().trim();
            CommonUtils.setButtonAlphaLow(submitButtonLinearLayout);
            submitButtonLinearLayout.setClickable(false);
            CommonUtils.sendRequest(AttestrRequest.actionFssaiSubmit(
                    fssaiNumberPlaceholder.getText().toString().trim(),
                    fetchProducts));
        }
    }

    private void fssaiNumberInvalid() {
        fssaiNumberPlaceholder.setBackgroundResource(0);
        fssaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        inputErrorTextView.setVisibility(View.VISIBLE);
        submitButtonProgressBar.setVisibility(View.GONE);
        submitButtonLinearLayout.setClickable(true);
    }

    public void OnDataError(String response) {
        CommonUtils.setButtonAlphaHigh(submitButtonLinearLayout);
        submitButtonProgressBar.setVisibility(View.GONE);
        submitButtonLinearLayout.setClickable(true);
        ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
        dataErrorTextView.setText(dataError.getData().getError().getMessage());
        dataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnSessionError(String response) {
        HandleException.handleSessionError(response);
    }


}
