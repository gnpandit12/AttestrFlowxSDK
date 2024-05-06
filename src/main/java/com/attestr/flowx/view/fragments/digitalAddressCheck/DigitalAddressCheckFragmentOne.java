package com.attestr.flowx.view.fragments.digitalAddressCheck;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.DigitalAddressCheckFragmentOneBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.UserLocation;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.DigitalAddressLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 03/11/21
 **/
public class DigitalAddressCheckFragmentOne extends Fragment implements View.OnClickListener {

    private static final String TAG = "digital";
    private ResponseData<StageInitData> stageReadyResponse;
    private DigitalAddressLocale digitalAddressLocale;
    private static DigitalAddressCheckFragmentOne digitalAddressCheckFragmentOne;
    private Activity mActivity;
    private DigitalAddressCheckFragmentOneBinding digitalAddressCheckFragmentOneBinding;
    private TextView digitalAddressLable, stayingSinceLabel, addressAvailabilityPrompt,
            buttonDoLater, buttonAddressCorrect;
    private EditText digitalAddressPlaceholder, stayingSinceYearPlaceholder;
    private TextView digitalAddressInputError, stayingSinceYearInputError;
    private Spinner stayingSinceMonthSpinner;
    private RelativeLayout buttonDoLaterRelativeLayout;
    private LinearLayout buttonAddressCorrectLinearLayout;
    private String selectedMonth;
    private boolean isAddressValid, isYearValid;
    private String tag, address, stayingSinceMonth, stayingSinceYear;
    private boolean allowUpdate;
    private List<String> monthsList;
    private CommonLocale commonLocale;
    private TextView stageTitle;
    private ImageView digitalAddressLableIcon, staySinceLableIcon;

    public static DigitalAddressCheckFragmentOne getInstance(boolean newInstance) {
        if (newInstance) {
            digitalAddressCheckFragmentOne = new DigitalAddressCheckFragmentOne();
            return digitalAddressCheckFragmentOne;
        } else {
            if (digitalAddressCheckFragmentOne == null) {
                digitalAddressCheckFragmentOne = new DigitalAddressCheckFragmentOne();
            }
        }
        return digitalAddressCheckFragmentOne;
    }

    public void setData(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        digitalAddressCheckFragmentOneBinding = DigitalAddressCheckFragmentOneBinding.inflate(inflater, container, false);
        return digitalAddressCheckFragmentOneBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        digitalAddressLocale = GlobalVariables.handshakeReadyResponse.getLocale().getDigitalAddressLocale();
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        monthsList = new ArrayList<>();
        monthsList.add(commonLocale.getMonthJan());
        monthsList.add(commonLocale.getMonthFeb());
        monthsList.add(commonLocale.getMonthMar());
        monthsList.add(commonLocale.getMonthApr());
        monthsList.add(commonLocale.getMonthMay());
        monthsList.add(commonLocale.getMonthJun());
        monthsList.add(commonLocale.getMonthJul());
        monthsList.add(commonLocale.getMonthAug());
        monthsList.add(commonLocale.getMonthSep());
        monthsList.add(commonLocale.getMonthOct());
        monthsList.add(commonLocale.getMonthNov());
        monthsList.add(commonLocale.getMonthDec());

        tag = stageReadyResponse.getData().getState().get("tag");
        address = stageReadyResponse.getData().getState().get("address");
        stayingSinceMonth = stageReadyResponse.getData().getState().get("stayingSinceMonth");
        stayingSinceYear = stageReadyResponse.getData().getState().get("stayingSinceYear");

        allowUpdate = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("allowUpdate")).toString());

        digitalAddressLable = digitalAddressCheckFragmentOneBinding.digitalAddressLable;
        stayingSinceLabel = digitalAddressCheckFragmentOneBinding.stayingSinceLabel;
        addressAvailabilityPrompt = digitalAddressCheckFragmentOneBinding.addressAvailabilityNominationPromptTextView;
        buttonDoLater = digitalAddressCheckFragmentOneBinding.buttonDoLaterTextView;
        buttonAddressCorrect = digitalAddressCheckFragmentOneBinding.buttonAddressCorrectTextView;
        digitalAddressInputError = digitalAddressCheckFragmentOneBinding.digitalAddressInputError;
        stayingSinceYearInputError = digitalAddressCheckFragmentOneBinding.stayingSinceYearInputError;
        stayingSinceMonthSpinner = digitalAddressCheckFragmentOneBinding.stayingSinceMonthSpinner;
        digitalAddressPlaceholder = digitalAddressCheckFragmentOneBinding.digitalAddressPlaceholder;
        stayingSinceYearPlaceholder = digitalAddressCheckFragmentOneBinding.stayingSinceYearPlaceholder;
        buttonDoLaterRelativeLayout = digitalAddressCheckFragmentOneBinding.buttonDoLaterRelativeLayout;
        buttonAddressCorrectLinearLayout = digitalAddressCheckFragmentOneBinding.buttonAddressCorrectRelativeLayout;
        digitalAddressLableIcon = digitalAddressCheckFragmentOneBinding.digitalAddressLableIcon;
        staySinceLableIcon = digitalAddressCheckFragmentOneBinding.stayingSinceLabelIcon;


        digitalAddressLableIcon.setBackgroundResource(R.drawable.id_card);
        digitalAddressLableIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        staySinceLableIcon.setBackgroundResource(R.drawable.id_card);
        staySinceLableIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        stageTitle = digitalAddressCheckFragmentOneBinding.digitalAddressCheckVerificationTitle;
        stageTitle.setText(digitalAddressLocale.getTitle());
        CommonUtils.setButtonAlphaHigh(buttonAddressCorrectLinearLayout);
        buttonAddressCorrectLinearLayout.setClickable(true);

        if (address != null) {
            digitalAddressPlaceholder.setText(address);
        }
        if (stayingSinceYear != null) {
            stayingSinceYearPlaceholder.setText(stayingSinceYear);
        }


        digitalAddressPlaceholder.setEnabled(allowUpdate);

        digitalAddressPlaceholder.setBackgroundResource(0);
        digitalAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        stayingSinceYearPlaceholder.setBackgroundResource(0);
        stayingSinceYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        digitalAddressPlaceholder.setPadding(20, 0, 0, 0);
        stayingSinceYearPlaceholder.setPadding(20, 0, 0, 0);

        digitalAddressInputError.setVisibility(View.INVISIBLE);
        stayingSinceYearInputError.setVisibility(View.INVISIBLE);

        digitalAddressLable.setText(digitalAddressLocale.getFieldAddressLabel());
        stayingSinceLabel.setText(digitalAddressLocale.getStayingSinceLabel());
        addressAvailabilityPrompt.setText(digitalAddressLocale.getAddressAvailabilityNominationPrompt());
        buttonDoLater.setText(digitalAddressLocale.getButtonDoLater());
        buttonAddressCorrect.setText(digitalAddressLocale.getButtonProceed());
        digitalAddressInputError.setText(digitalAddressLocale.getFieldAddressDataerror());
        stayingSinceYearInputError.setText(digitalAddressLocale.getStayingSinceYearDataerror());
        digitalAddressPlaceholder.setHint(digitalAddressLocale.getFieldAddressPlaceholder());
        stayingSinceYearPlaceholder.setHint(digitalAddressLocale.getStayingSinceYearPlaceholder());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.document_spinner_items, monthsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stayingSinceMonthSpinner.setAdapter(arrayAdapter);
        if (stayingSinceMonth != null) {
            selectedMonth = stayingSinceMonth;
            stayingSinceMonthSpinner.setSelection(arrayAdapter.getPosition(stayingSinceMonth));
        } else {
            selectedMonth = monthsList.get(0);
        }
        stayingSinceMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = monthsList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMonth = monthsList.get(0);
            }
        });


        digitalAddressPlaceholder.addTextChangedListener(new InputTextValidator(digitalAddressPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isAddressValid = UserInputValidation.isStringValid(s.toString());
                if (isAddressValid) {
                    digitalAddressInputError.setVisibility(View.INVISIBLE);
                    digitalAddressPlaceholder.setBackgroundResource(0);
                    digitalAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    buttonAddressCorrectLinearLayout.setClickable(true);
                    digitalAddressPlaceholder.setBackgroundResource(0);
                    digitalAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    digitalAddressInputError.setVisibility(View.VISIBLE);
                }
            }
        });

        stayingSinceYearPlaceholder.addTextChangedListener(new InputTextValidator(stayingSinceYearPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isYearValid = UserInputValidation.isYearValid(s.toString());
                if (isYearValid) {
                    stayingSinceYearInputError.setVisibility(View.INVISIBLE);
                    stayingSinceYearPlaceholder.setBackgroundResource(0);
                    stayingSinceYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    buttonAddressCorrectLinearLayout.setClickable(true);
                    stayingSinceYearPlaceholder.setBackgroundResource(0);
                    stayingSinceYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    stayingSinceYearInputError.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonDoLaterRelativeLayout.setOnClickListener(this);
        buttonAddressCorrectLinearLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == buttonDoLaterRelativeLayout) {
            ((LaunchActivity) mActivity).skipFlow();
        } else if (v == buttonAddressCorrectLinearLayout) {
            validateInputs();
        }
    }

    private void validateInputs() {
        isAddressValid = UserInputValidation.isStringValid(digitalAddressPlaceholder.getText().toString().trim());
        isYearValid = UserInputValidation.isYearValid(stayingSinceYearPlaceholder.getText().toString().trim());
        if (isAddressValid && isYearValid) {
            proceed();
        } else {
            if (!isAddressValid) {
                CommonUtils.setButtonAlphaHigh(buttonAddressCorrectLinearLayout);
                buttonAddressCorrectLinearLayout.setClickable(false);
                digitalAddressPlaceholder.setBackgroundResource(0);
                digitalAddressPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                digitalAddressInputError.setVisibility(View.VISIBLE);
            }
            if (!isYearValid) {
                CommonUtils.setButtonAlphaHigh(buttonAddressCorrectLinearLayout);
                buttonAddressCorrectLinearLayout.setClickable(false);
                stayingSinceYearPlaceholder.setBackgroundResource(0);
                stayingSinceYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                stayingSinceYearInputError.setVisibility(View.VISIBLE);
            }
        }
    }

    private void proceed() {
        CommonUtils.setButtonAlphaLow(buttonAddressCorrectLinearLayout);
        stayingSinceYearInputError.setVisibility(View.INVISIBLE);
        digitalAddressInputError.setVisibility(View.INVISIBLE);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("address", digitalAddressPlaceholder.getText().toString().trim());
        dataMap.put("stayingSinceMonth", selectedMonth);
        dataMap.put("stayingSinceYear", stayingSinceYearPlaceholder.getText().toString().trim());
        DigitalAddressCheckFragmentTwo digitalAddressCheckFragmentTwo = DigitalAddressCheckFragmentTwo.getInstance(true);
        ((LaunchActivity) mActivity).addFragment(digitalAddressCheckFragmentTwo);
        digitalAddressCheckFragmentTwo.setData(mActivity, dataMap);
    }

}
