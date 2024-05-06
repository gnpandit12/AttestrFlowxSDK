package com.attestr.flowx.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 02/09/21
 **/
public class BusinessAMLFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private TextView businessAMLLabelTextView, businessAMLInputDataErrorTextView, businessAMLSubmitButtonTextView;
    private EditText businessAMLPlaceholderEditText;
    private LinearLayout submitBusinessAMLLinearLayout;
    private ProgressBar submitBusinessAMLProgressBar;
    private String inputDataErrorString;
    private boolean isCompanyNameValid;
    private String threshold, businessName, address, city, state, country, zip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.business_aml_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        threshold = GlobalVariables.stageReadyResponse.getData().getState().get("threshold");
        businessName = GlobalVariables.stageReadyResponse.getData().getState().get("businessName");
        address = GlobalVariables.stageReadyResponse.getData().getState().get("address");
        city = GlobalVariables.stageReadyResponse.getData().getState().get("city");
        state = GlobalVariables.stageReadyResponse.getData().getState().get("state");
        country = GlobalVariables.stageReadyResponse.getData().getState().get("country");
        zip = GlobalVariables.stageReadyResponse.getData().getState().get("zip");

        inputDataErrorString = handShakeReadyResponse.getLocale().getAmlBusinessLocale().getFieldEntityDataerror();

        submitBusinessAMLLinearLayout = view.findViewById(R.id.business_aml_submit_Relative_layout);
        CommonUtils.setButtonAlphaHigh(submitBusinessAMLLinearLayout);
        submitBusinessAMLProgressBar = view.findViewById(R.id.business_aml_submit_progress_bar);
        submitBusinessAMLProgressBar.setVisibility(View.GONE);
        businessAMLPlaceholderEditText = view.findViewById(R.id.business_aml_placeholder);

        TextView businessAMLTitle = view.findViewById(R.id.business_aml_verification_title);
        businessAMLTitle.setText(handShakeReadyResponse.getLocale().getAmlBusinessLocale().getTitle());

        businessAMLPlaceholderEditText.getText().clear();
        if (businessName != null){
            businessAMLPlaceholderEditText.setText(businessName);
        }
        businessAMLPlaceholderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        businessAMLPlaceholderEditText.setBackgroundResource(0);
        businessAMLPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        businessAMLLabelTextView = view.findViewById(R.id.business_aml_label);
        businessAMLInputDataErrorTextView = view.findViewById(R.id.business_aml_error_text_view);
        businessAMLSubmitButtonTextView = view.findViewById(R.id.business_aml_submit_button);
        businessAMLSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
        businessAMLLabelTextView.setText(handShakeReadyResponse.getLocale().getAmlBusinessLocale().getFieldEntityLabel());
        businessAMLPlaceholderEditText.setHint(handShakeReadyResponse.getLocale().getAmlBusinessLocale().getFieldEntityPlaceholder());
        businessAMLSubmitButtonTextView.setText(handShakeReadyResponse.getLocale().getAmlBusinessLocale().getButtonProceed());
        businessAMLInputDataErrorTextView.setVisibility(View.GONE);
        businessAMLInputDataErrorTextView.setText(inputDataErrorString);
        submitBusinessAMLLinearLayout.setClickable(true);

        businessAMLPlaceholderEditText.addTextChangedListener(new InputTextValidator(businessAMLPlaceholderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isCompanyNameValid = UserInputValidation.isCompanyNameValid(s.toString());
                if (isCompanyNameValid){
                    businessAMLInputDataErrorTextView.setVisibility(View.GONE);
                    businessAMLPlaceholderEditText.setBackgroundResource(0);
                    businessAMLPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitBusinessAMLLinearLayout.setClickable(true);
                    submitBusinessAMLProgressBar.setVisibility(View.GONE);
                    businessAMLPlaceholderEditText.setBackgroundResource(0);
                    businessAMLPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    businessAMLInputDataErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        businessAMLPlaceholderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(submitBusinessAMLLinearLayout);
                sendBusinessAMLVerificationRequest();
                return false;
            }
            return false;
        });

        submitBusinessAMLLinearLayout.setOnClickListener(v -> {
            CommonUtils.setButtonAlphaLow(submitBusinessAMLLinearLayout);
            sendBusinessAMLVerificationRequest();
        });

    }

    private void sendBusinessAMLVerificationRequest() {
        String companyName = businessAMLPlaceholderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(companyName)) {
            sendRequest(companyName);
        } else {
            userInputError();
        }
    }

    private void sendRequest(String companyName) {
        businessAMLInputDataErrorTextView.setVisibility(View.GONE);
        submitBusinessAMLProgressBar.setVisibility(View.VISIBLE);
        submitBusinessAMLLinearLayout.setClickable(false);
        businessAMLPlaceholderEditText.setBackgroundResource(0);
        businessAMLPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_background);
        CommonUtils.sendRequest(AttestrRequest.actionSubmitBusinessAML(companyName));
    }

    private void userInputError(){
        CommonUtils.setButtonAlphaHigh(submitBusinessAMLLinearLayout);
        submitBusinessAMLLinearLayout.setClickable(true);
        businessAMLPlaceholderEditText.setBackgroundResource(0);
        businessAMLPlaceholderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
        submitBusinessAMLProgressBar.setVisibility(View.GONE);
        businessAMLInputDataErrorTextView.setVisibility(View.VISIBLE);
    }

}
