package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import com.attestr.flowx.model.response.locale.DrivingLicenseLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 09/01/23
 **/
public class DrivingLicenseVerificationFragment extends Fragment {

    private static DrivingLicenseVerificationFragment mDrivingLicenseVerificationFragment;
    private HandshakeReadyData handShakeReadyResponse;
    private Activity mActivity;
    private TextView drivingLicenseStageTitleTextView, drivingLicenseVerificationLableTextView, drivingLicenseVerificationDataErrorTextView,
            dateOfBirthInputErrorTextView, drivingLicenseVerificationInputDataErrorTextView, drivingLicenseVerificationSubmitButtonTextView;
    private EditText drivingLicensePlaceholer, dateOfBirthPlaceholder;
    private ProgressBar drivingLicenseVerificationSubmitProgressBar;
    private String drivingLicenseVerificationDataErrorString;
    private int remainingRetryAttempts = 1;
    private TextView retryAttemptsTextView;
    private DrivingLicenseLocale drivingLicenseLocale;
    private LinearLayout drivingLicenseSubmitLinearLayout;
    private String drivingLicenseNumber, dateOfBirth;
    private boolean isDrivingLicenseValid, isDateOfBirthvalid;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private String dateFormat = "dd-MM-yyyy";
    private SimpleDateFormat simpleDateFormat;
    private ImageView drivingLicenseLableIcon;

    public static DrivingLicenseVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            mDrivingLicenseVerificationFragment = new DrivingLicenseVerificationFragment();
            return mDrivingLicenseVerificationFragment;
        } else {
            if (mDrivingLicenseVerificationFragment == null){
                mDrivingLicenseVerificationFragment = new DrivingLicenseVerificationFragment();
            }
        }
        return mDrivingLicenseVerificationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.driving_license_verification_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        drivingLicenseLocale = handShakeReadyResponse.getLocale().getDrivingLicenseLocale();

        drivingLicenseVerificationDataErrorString = drivingLicenseLocale.getFieldDlDataerror();

        drivingLicenseSubmitLinearLayout = view.findViewById(R.id.driving_license_submit_linear_layout);
        CommonUtils.setButtonAlphaHigh(drivingLicenseSubmitLinearLayout);
        drivingLicenseVerificationSubmitProgressBar = view.findViewById(R.id.driving_license_submit_progress_bar);
        drivingLicenseVerificationDataErrorTextView = view.findViewById(R.id.driving_license_data_error_text_view);
        dateOfBirthInputErrorTextView = view.findViewById(R.id.driving_license_date_of_birth_input_error_text_view);
        drivingLicenseLableIcon.setBackgroundResource(R.drawable.ipv_start_camera);
        drivingLicenseLableIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        dateOfBirthInputErrorTextView.setText(drivingLicenseLocale.getFieldDobDataerror());
        dateOfBirthInputErrorTextView.setVisibility(View.GONE);

//        if (drivingLicenseNumber != null) {
//            drivingLicensePlaceholer.setText(drivingLicenseNumber);
//        }

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();
        drivingLicensePlaceholer = view.findViewById(R.id.driving_license_placeholder);
        drivingLicensePlaceholer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        drivingLicensePlaceholer.setBackgroundResource(0);
        drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_background);
        drivingLicenseVerificationDataErrorTextView.setVisibility(View.GONE);
        drivingLicenseStageTitleTextView = view.findViewById(R.id.driving_license_stage_title);
        dateOfBirthPlaceholder = view.findViewById(R.id.driving_license_date_of_birth_placeholder);
        dateOfBirthPlaceholder.setHint(drivingLicenseLocale.getFieldDobPlaceholder());
        drivingLicenseVerificationLableTextView = view.findViewById(R.id.driving_license_label);
        drivingLicenseVerificationInputDataErrorTextView = view.findViewById(R.id.driving_license_input_error_text_view);
        drivingLicenseVerificationSubmitButtonTextView = view.findViewById(R.id.driving_license_submit_button);
        drivingLicenseSubmitLinearLayout = view.findViewById(R.id.driving_license_submit_linear_layout);
        drivingLicenseVerificationSubmitButtonTextView.setTextColor(GlobalVariables.textColor);
        drivingLicenseStageTitleTextView.setText(drivingLicenseLocale.getTitle());
        drivingLicenseVerificationLableTextView.setText(drivingLicenseLocale.getFieldDlLabel());
        drivingLicensePlaceholer.setHint(drivingLicenseLocale.getFieldDlPlaceholder());
        drivingLicenseVerificationSubmitButtonTextView.setText(drivingLicenseLocale.getButtonDlProceed());

        drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
        drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.GONE);
        drivingLicenseVerificationInputDataErrorTextView.setText(drivingLicenseVerificationDataErrorString);
        drivingLicenseSubmitLinearLayout.setClickable(true);

        dateOfBirthPlaceholder.setOnClickListener(view1 -> openCalender());

        retryAttemptsTextView = view.findViewById(R.id.driving_license_retry_attempt_text_view);
        retryAttemptsTextView.setVisibility(View.GONE);

        dateOfBirthPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                dateOfBirthInputErrorTextView.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(drivingLicenseSubmitLinearLayout);
                sendRequest();
                return false;
            }
            return false;
        });

        drivingLicensePlaceholer.addTextChangedListener(new InputTextValidator(drivingLicensePlaceholer) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isDrivingLicenseValid = UserInputValidation.isDrivingLicenseValid(s.toString());
                if (isDrivingLicenseValid){
                    drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.INVISIBLE);
                    drivingLicensePlaceholer.setBackgroundResource(0);
                    drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    drivingLicenseSubmitLinearLayout.setClickable(true);
                    drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
                    drivingLicensePlaceholer.setBackgroundResource(0);
                    drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_error_background);
                    drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
                }
                drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.INVISIBLE);
                drivingLicensePlaceholer.setBackgroundResource(0);
                drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_background);
            }
        });

        dateOfBirthPlaceholder.addTextChangedListener(new InputTextValidator(dateOfBirthPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isDateOfBirthvalid = UserInputValidation.isEnteredDateFormatValid(s.toString());
                if (isDateOfBirthvalid){
                    dateOfBirthInputErrorTextView.setVisibility(View.INVISIBLE);
                    dateOfBirthPlaceholder.setBackgroundResource(0);
                    dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    drivingLicenseSubmitLinearLayout.setClickable(true);
                    drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
                    dateOfBirthPlaceholder.setBackgroundResource(0);
                    dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    dateOfBirthInputErrorTextView.setVisibility(View.VISIBLE);
                }
                dateOfBirthInputErrorTextView.setVisibility(View.INVISIBLE);
                dateOfBirthPlaceholder.setBackgroundResource(0);
                dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
            }
        });

        drivingLicenseSubmitLinearLayout.setOnClickListener(v -> sendRequest());

        datePickerDialog = new DatePickerDialog(mActivity, (datePicker, year, month, date) -> {
            calendar.set(year, month, date);
            dateOfBirthPlaceholder.setText(simpleDateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dateOfBirthPlaceholder.setShowSoftInputOnFocus(false);

    }

    public void setData(Activity activity) {this.mActivity = activity;}

    private void openCalender() {
        if (!datePickerDialog.isShowing()){
            datePickerDialog.show();
        }
    }

    private void sendRequest() {
        String drivingLicenseNumber = drivingLicensePlaceholer.getText().toString().trim();
        String dateOfBirth = dateOfBirthPlaceholder.getText().toString().trim();
        if (!TextUtils.isEmpty(drivingLicensePlaceholer.getText().toString()) &&
                !TextUtils.isEmpty(dateOfBirthPlaceholder.getText().toString())) {
            if (!isDrivingLicenseValid && !isDateOfBirthvalid) {
                drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
                drivingLicenseSubmitLinearLayout.setClickable(true);
                drivingLicensePlaceholer.setBackgroundResource(0);
                dateOfBirthPlaceholder.setBackgroundResource(0);
                dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_error_background);
            } else if (!isDrivingLicenseValid) {
                drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
                drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
                drivingLicensePlaceholer.setBackgroundResource(0);
                drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_error_background);
            } else if (!isDateOfBirthvalid) {
                drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
                dateOfBirthInputErrorTextView.setVisibility(View.VISIBLE);
                dateOfBirthPlaceholder.setBackgroundResource(0);
                dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
            } else {
                drivingLicenseVerificationSubmitProgressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(drivingLicenseSubmitLinearLayout);
                drivingLicenseSubmitLinearLayout.setClickable(false);
                drivingLicensePlaceholer.setBackgroundResource(0);
                dateOfBirthPlaceholder.setBackgroundResource(0);
                drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_background);
                dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                CommonUtils.sendRequest(AttestrRequest.actionSubmitDrivingLicense(drivingLicenseNumber, dateOfBirth));
            }
        } else if (TextUtils.isEmpty(drivingLicensePlaceholer.getText().toString()) &&
                TextUtils.isEmpty(dateOfBirthPlaceholder.getText().toString())) {
            drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
            dateOfBirthInputErrorTextView.setVisibility(View.VISIBLE);
            drivingLicensePlaceholer.setBackgroundResource(0);
            drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_error_background);
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
            dateOfBirthPlaceholder.setBackgroundResource(0);
            dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(drivingLicensePlaceholer.getText().toString())) {
            drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.VISIBLE);
            drivingLicensePlaceholer.setBackgroundResource(0);
            drivingLicensePlaceholer.setBackgroundResource(R.drawable.edit_text_error_background);
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(dateOfBirthPlaceholder.getText().toString())) {
            dateOfBirthInputErrorTextView.setVisibility(View.VISIBLE);
            dateOfBirthPlaceholder.setBackgroundResource(0);
            dateOfBirthPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
        }
    }

    public void OnDataError(String response) {
        isDrivingLicenseValid = false;
        isDateOfBirthvalid = false;
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(drivingLicenseSubmitLinearLayout);
            drivingLicensePlaceholer.getText().clear();
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
            drivingLicenseSubmitLinearLayout.setClickable(true);
            drivingLicenseVerificationInputDataErrorTextView.setText(drivingLicenseVerificationDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.GONE);
            drivingLicenseVerificationDataErrorTextView.setText(dataError.getData().getError().getMessage());
            drivingLicenseVerificationDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText(remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
        remainingRetryAttempts--;
    }

    public void OnSessionError(String response) {
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(drivingLicenseSubmitLinearLayout);
            drivingLicensePlaceholer.getText().clear();
            drivingLicenseVerificationSubmitProgressBar.setVisibility(View.GONE);
            drivingLicenseSubmitLinearLayout.setClickable(true);
            drivingLicenseVerificationInputDataErrorTextView.setText(drivingLicenseVerificationDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            drivingLicenseVerificationInputDataErrorTextView.setVisibility(View.GONE);
            drivingLicenseVerificationDataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            drivingLicenseVerificationDataErrorTextView.setVisibility(View.VISIBLE);
            retryAttemptsTextView.setText(remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
        remainingRetryAttempts--;
    }


}
