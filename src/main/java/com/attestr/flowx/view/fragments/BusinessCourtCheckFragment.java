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
import com.attestr.flowx.databinding.BusinessCourtCheckFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.EcourtBusinessLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;

import java.util.HashMap;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 24/10/21
 **/
public class BusinessCourtCheckFragment extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private HandshakeReadyData handShakeReadyResponse;
    private EcourtBusinessLocale ecourtBusinessLocale;
    private BusinessCourtCheckFragmentBinding businessCourtCheckFragmentBinding;
    private static BusinessCourtCheckFragment businessCourtCheckFragment;
    private LinearLayout submitRelativeLayout;
    private ProgressBar submitProgressBar;
    private TextView submitButtonTextView, businessNameLabel, registrationNumberLabel,
            addressLabel;
    private TextView companyInputError, addressInputError;
    private EditText businessNamePlaceholder, registrationNumberPlaceholder, addressPlaceholder;
    private boolean isCompanyNameValid, isAddressValid, extended;
    private String tag, businessName, reg, address, priority;
    private ImageView businessNameIcon, businessRegIcon, businessAddressIcon;

    public static BusinessCourtCheckFragment getInstance(boolean newInstance){
        if (newInstance){
            businessCourtCheckFragment = new BusinessCourtCheckFragment();
            return businessCourtCheckFragment;
        } else {
            if (businessCourtCheckFragment == null){
                businessCourtCheckFragment = new BusinessCourtCheckFragment();
            }
        }
        return businessCourtCheckFragment;
    }

    public void setData(Activity activity){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        businessCourtCheckFragmentBinding = BusinessCourtCheckFragmentBinding.inflate(inflater, container, false);
        return businessCourtCheckFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        stageReadyResponse = GlobalVariables.stageReadyResponse;
        ecourtBusinessLocale = handShakeReadyResponse.getLocale().geteCourtBusiness();

        tag = stageReadyResponse.getData().getState().get("tag");
        businessName = stageReadyResponse.getData().getState().get("businessName");
        reg = stageReadyResponse.getData().getState().get("reg");
        address = stageReadyResponse.getData().getState().get("address");
        priority = stageReadyResponse.getData().getState().get("priority");

        extended = (boolean) stageReadyResponse.getData().getMetadata().get("extended");

        submitRelativeLayout = businessCourtCheckFragmentBinding.businessCourtCheckSubmitRelativeLayout;
        submitProgressBar = businessCourtCheckFragmentBinding.businessCourtCheckSubmitProgressBar;
        submitButtonTextView = businessCourtCheckFragmentBinding.businessCourtCheckSubmitButton;
        businessNameLabel = businessCourtCheckFragmentBinding.businessCompanyNameLabel;
        businessNamePlaceholder = businessCourtCheckFragmentBinding.businessCompanyNamePlaceholder;
        companyInputError = businessCourtCheckFragmentBinding.businessCompanyNameInputErrorTextView;
        registrationNumberLabel = businessCourtCheckFragmentBinding.businessRegistrationNumberLabel;
        registrationNumberPlaceholder = businessCourtCheckFragmentBinding.businessRegistrationNumberPlaceholder;
        addressLabel = businessCourtCheckFragmentBinding.businessAddressLabel;
        addressPlaceholder = businessCourtCheckFragmentBinding.businessAddressPlaceholder;
        businessNameIcon = businessCourtCheckFragmentBinding.companyNameIcon;
        businessRegIcon = businessCourtCheckFragmentBinding.businessRegNumberIcon;
        businessAddressIcon = businessCourtCheckFragmentBinding.businessAddressIcon;


        businessNameIcon.setBackgroundResource(R.drawable.id_card);
        businessNameIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        businessRegIcon.setBackgroundResource(R.drawable.id_card);
        businessRegIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        businessAddressIcon.setBackgroundResource(R.drawable.id_card);
        businessAddressIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));


        TextView businessCourtTitle = businessCourtCheckFragmentBinding.businessCourtCheckVerificationTitle;
        businessCourtTitle.setText(handShakeReadyResponse.getLocale().geteCourtBusiness().getTitle());

        if (businessName != null){
            businessNamePlaceholder.setText(businessName);
        }
        if (reg != null){
            registrationNumberPlaceholder.setText(reg);
        }
        if (address != null){
            addressPlaceholder.setText(address);
        }

        businessNameLabel.setText(ecourtBusinessLocale.getFieldEntityLabel());
        registrationNumberLabel.setText(ecourtBusinessLocale.getFieldRegLabel());
        addressLabel.setText(ecourtBusinessLocale.getFieldAddressLabel());
        submitButtonTextView.setText(ecourtBusinessLocale.getButtonProceed());

        businessNamePlaceholder.setHint(ecourtBusinessLocale.getFieldEntityPlaceholder());
        registrationNumberPlaceholder.setHint(ecourtBusinessLocale.getFieldRegPlaceholder());
        addressPlaceholder.setHint(ecourtBusinessLocale.getFieldAddressPlaceholder());

        companyInputError.setVisibility(View.GONE);
        addressInputError.setVisibility(View.GONE);

        companyInputError.setText(ecourtBusinessLocale.getFieldEntityDataerror());
        addressInputError.setText(ecourtBusinessLocale.getFieldAddressDataerror());

        businessNamePlaceholder.setBackgroundResource(0);
        businessNamePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        registrationNumberPlaceholder.setBackgroundResource(0);
        registrationNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        addressPlaceholder.setBackgroundResource(0);
        addressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        businessNamePlaceholder.setMinHeight(96);
        registrationNumberPlaceholder.setMinHeight(96);
        addressPlaceholder.setMinHeight(384);

        businessNamePlaceholder.setPadding(20,0,0,0);
        registrationNumberPlaceholder.setPadding(20,0,0,0);
        addressPlaceholder.setPadding(20,0,0,0);

        businessNamePlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        registrationNumberPlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        addressPlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4192)});

        businessNamePlaceholder.addTextChangedListener(new InputTextValidator(businessNamePlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isCompanyNameValid = UserInputValidation.isNameValid(s.toString());
                if (isCompanyNameValid){
                    companyInputError.setVisibility(View.GONE);
                    businessNamePlaceholder.setBackgroundResource(0);
                    businessNamePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitRelativeLayout.setClickable(true);
                    submitProgressBar.setVisibility(View.GONE);
                    businessNamePlaceholder.setBackgroundResource(0);
                    businessNamePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    companyInputError.setVisibility(View.VISIBLE);
                }
            }
        });

        addressPlaceholder.addTextChangedListener(new InputTextValidator(addressPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isAddressValid = UserInputValidation.isAddressValid(s.toString());
                if (isAddressValid){
                    addressInputError.setVisibility(View.GONE);
                    addressPlaceholder.setBackgroundResource(0);
                    addressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitRelativeLayout.setClickable(true);
                    submitProgressBar.setVisibility(View.GONE);
                    addressPlaceholder.setBackgroundResource(0);
                    addressPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    addressInputError.setVisibility(View.VISIBLE);
                }
            }
        });

        addressPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                sendRequest();
                return false;
            }
            return false;
        });

        CommonUtils.setButtonAlphaHigh(submitRelativeLayout);
        submitProgressBar.setVisibility(View.GONE);
        submitRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == submitRelativeLayout){
            sendRequest();
        }
    }


    private void sendRequest(){
        isCompanyNameValid = UserInputValidation.isNameValid(businessNamePlaceholder.getText().toString().trim());
        isAddressValid = UserInputValidation.isAddressValid(addressPlaceholder.getText().toString().trim());
        if (isCompanyNameValid && isAddressValid){
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("businessName", businessNamePlaceholder.getText().toString().trim());
            requestMap.put("reg",
                    TextUtils.isEmpty(registrationNumberPlaceholder.getText().toString().trim())
            ? null: registrationNumberPlaceholder.getText().toString().trim());
            requestMap.put("address", addressPlaceholder.getText().toString().trim());
            if (priority != null){
                requestMap.put("priority", priority);
            }
            requestMap.put("extended", String.valueOf(extended));
            submitRelativeLayout.setClickable(false);
            CommonUtils.setButtonAlphaLow(submitRelativeLayout);
            submitProgressBar.setVisibility(View.VISIBLE);
            companyInputError.setVisibility(View.GONE);
            addressInputError.setVisibility(View.GONE);
            CommonUtils.sendRequest(AttestrRequest.actionEcourtBusinessSubmit(requestMap));
        } else {
            submitRelativeLayout.setClickable(true);
            CommonUtils.setButtonAlphaHigh(submitRelativeLayout);
            submitProgressBar.setVisibility(View.GONE);
            if (!isCompanyNameValid){
                companyInputError.setVisibility(View.VISIBLE);
                businessNamePlaceholder.setBackgroundResource(0);
                businessNamePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
            }
            if (!isAddressValid){
                addressInputError.setVisibility(View.VISIBLE);
                addressPlaceholder.setBackgroundResource(0);
                addressPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
            }
        }
    }


}

