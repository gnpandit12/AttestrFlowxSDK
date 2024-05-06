package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 02/09/21
 **/
public class PersonAMLFragment extends Fragment {

    private HandshakeReadyData handShakeReadyResponse;
    private LinearLayout submitPersonAMLLinearLayout;
    private ProgressBar submitPersonAMLProgressBar;
    private TextView fNameLabel, mNameLabel, lNameLabel, fNameDataError, mNameDataError, lNameDataError,
    dobLabel, dobDataError, submitPersonAMLButtonTextView;
    private EditText fNamePlaceHolder, mNamePlaceHolder, lNamePlaceHolder, dobPlaceHolder;
    private Calendar calendar;
    private String dateFormat = "dd-MM-yyyy";
    private SimpleDateFormat simpleDateFormat;
    private Activity mActivity;
    private boolean isFirstNameValid, isMiddleNameValid, isLastNameValid, isDobValid;
    private DatePickerDialog datePickerDialog;
    private String threshold, businessName, address, city, state, country, zip;

    public PersonAMLFragment(Activity activity) {
        this.mActivity = activity;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_aml_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        threshold = GlobalVariables.stageReadyResponse.getData().getState().get("threshold");
        String firstName = GlobalVariables.stageReadyResponse.getData().getState().get("firstName");
        String middleName = GlobalVariables.stageReadyResponse.getData().getState().get("middleName");
        String lastName = GlobalVariables.stageReadyResponse.getData().getState().get("lastName");
        String dateOfBirth = GlobalVariables.stageReadyResponse.getData().getState().get("dob");
        address = GlobalVariables.stageReadyResponse.getData().getState().get("address");
        city = GlobalVariables.stageReadyResponse.getData().getState().get("city");
        state = GlobalVariables.stageReadyResponse.getData().getState().get("state");
        country = GlobalVariables.stageReadyResponse.getData().getState().get("country");
        zip = GlobalVariables.stageReadyResponse.getData().getState().get("zip");

        fNameLabel = view.findViewById(R.id.field_Fname_lable);
        mNameLabel = view.findViewById(R.id.field_Mname_lable);
        lNameLabel = view.findViewById(R.id.field_Lname_lable);
        dobLabel = view.findViewById(R.id.field_DOB_lable);
        fNamePlaceHolder = view.findViewById(R.id.field_Fname_placeholder);
        mNamePlaceHolder = view.findViewById(R.id.field_Mname_placeholder);
        lNamePlaceHolder = view.findViewById(R.id.field_Lname_placeholder);
        dobPlaceHolder = view.findViewById(R.id.field_DOB_placeholder);
        if (firstName != null){
            fNamePlaceHolder.setText(firstName);
        }
        if (middleName != null){
            mNamePlaceHolder.setText(middleName);
        }
        if (lastName != null){
            lNamePlaceHolder.setText(lastName);
        }
        if (dateOfBirth != null){
            dobPlaceHolder.setText(dateOfBirth);
        }
        dobPlaceHolder.setShowSoftInputOnFocus(false);
        fNameDataError = view.findViewById(R.id.Fname_error_text_view);
        mNameDataError = view.findViewById(R.id.Mname_error_text_view);
        lNameDataError = view.findViewById(R.id.Lname_error_text_view);
        dobDataError = view.findViewById(R.id.DOB_error_text_view);
        submitPersonAMLLinearLayout = view.findViewById(R.id.person_aml_submit_relative_layout);
        submitPersonAMLProgressBar = view.findViewById(R.id.person_aml_submit_progress_bar);
        submitPersonAMLProgressBar.setVisibility(View.GONE);
        submitPersonAMLButtonTextView = view.findViewById(R.id.person_aml_submit_button_text_view);

        TextView personAMLTitle = view.findViewById(R.id.person_aml_verification_title);
        personAMLTitle.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getTitle());

        fNameLabel.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldFnameLabel());
        mNameLabel.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldMnameLabel());
        lNameLabel.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldLnameLabel());
        dobLabel.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldDobLabel());

        fNamePlaceHolder.setHint(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldFnamePlaceholder());
        mNamePlaceHolder.setHint(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldMnamePlaceholder());
        lNamePlaceHolder.setHint(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldLnamePlaceholder());
        dobPlaceHolder.setHint(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldDobPlaceholder());

        fNameDataError.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldFnameDataerror());
        mNameDataError.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldMnameDataerror());
        lNameDataError.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldLnameDataerror());
        dobDataError.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getFieldDobDataerror());

        submitPersonAMLButtonTextView.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getButtonProceed());

        fNamePlaceHolder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        mNamePlaceHolder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        lNamePlaceHolder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        submitPersonAMLLinearLayout.setClickable(true);

        fNameDataError.setVisibility(View.GONE);
        mNameDataError.setVisibility(View.GONE);
        lNameDataError.setVisibility(View.GONE);
        dobDataError.setVisibility(View.GONE);

        fNamePlaceHolder.setBackgroundResource(0);
        fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        mNamePlaceHolder.setBackgroundResource(0);
        mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        lNamePlaceHolder.setBackgroundResource(0);
        lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        dobPlaceHolder.setBackgroundResource(0);
        dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        dobPlaceHolder.setOnClickListener(view12 -> openCalender());

        submitPersonAMLLinearLayout.setOnClickListener(view1 -> {
            CommonUtils.setButtonAlphaLow(submitPersonAMLLinearLayout);
            sendPersonAMLVerificationRequest();
        });

        fNamePlaceHolder.addTextChangedListener(new InputTextValidator(fNamePlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isFirstNameValid = !TextUtils.isEmpty(s.toString());
                if (isFirstNameValid){
                    fNameDataError.setVisibility(View.GONE);
                    fNamePlaceHolder.setBackgroundResource(0);
                    fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitPersonAMLLinearLayout.setClickable(true);
                    submitPersonAMLProgressBar.setVisibility(View.GONE);
                    fNameDataError.setVisibility(View.VISIBLE);
                    fNamePlaceHolder.setBackgroundResource(0);
                    fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                }
            }
        });

        mNamePlaceHolder.addTextChangedListener(new InputTextValidator(mNamePlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isMiddleNameValid = !TextUtils.isEmpty(s.toString());
                if (isMiddleNameValid){
                    mNameDataError.setVisibility(View.GONE);
                    mNamePlaceHolder.setBackgroundResource(0);
                    mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitPersonAMLLinearLayout.setClickable(true);
                    submitPersonAMLProgressBar.setVisibility(View.GONE);
                    mNameDataError.setVisibility(View.VISIBLE);
                    mNamePlaceHolder.setBackgroundResource(0);
                    mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                }
            }
        });

        lNamePlaceHolder.addTextChangedListener(new InputTextValidator(lNamePlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isLastNameValid = !TextUtils.isEmpty(s.toString());
                if (isLastNameValid){
                    lNameDataError.setVisibility(View.GONE);
                    lNamePlaceHolder.setBackgroundResource(0);
                    lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitPersonAMLLinearLayout.setClickable(true);
                    submitPersonAMLProgressBar.setVisibility(View.GONE);
                    lNameDataError.setVisibility(View.VISIBLE);
                    lNamePlaceHolder.setBackgroundResource(0);
                    lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                }
            }
        });

        dobPlaceHolder.addTextChangedListener(new InputTextValidator(dobPlaceHolder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isDobValid = UserInputValidation.isEnteredDateFormatValid(s.toString());
                if (isDobValid){
                    dobDataError.setVisibility(View.GONE);
                    dobPlaceHolder.setBackgroundResource(0);
                    dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitPersonAMLLinearLayout.setClickable(true);
                    submitPersonAMLProgressBar.setVisibility(View.GONE);
                    dobDataError.setVisibility(View.VISIBLE);
                    dobPlaceHolder.setBackgroundResource(0);
                    dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
                }
            }
        });

        datePickerDialog = new DatePickerDialog(mActivity, (datePicker, year, month, date) -> {
            calendar.set(year, month, date);
            dobPlaceHolder.setText(simpleDateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void openCalender() {
        if (!datePickerDialog.isShowing()){
            datePickerDialog.show();
        }
    }


    private void sendPersonAMLVerificationRequest() {
        String firstName = fNamePlaceHolder.getText().toString().trim();
        String middleName = mNamePlaceHolder.getText().toString().trim();
        String lastName = lNamePlaceHolder.getText().toString().trim();
        String dateOfBirth = dobPlaceHolder.getText().toString().trim();

        boolean isFirstNameEmpty = TextUtils.isEmpty(firstName);
        boolean isMiddleNameEmpty = TextUtils.isEmpty(middleName);
        boolean isLastNameEmpty = TextUtils.isEmpty(lastName);
        boolean isDateOfBirthEmpty = TextUtils.isEmpty(dateOfBirth);

        if (!isFirstNameEmpty && !isMiddleNameEmpty && !isLastNameEmpty && !isDateOfBirthEmpty && UserInputValidation.isEnteredDateFormatValid(dateOfBirth)){
            sendRequest(firstName, middleName, lastName, dateOfBirth);
        } else {
            CommonUtils.setButtonAlphaHigh(submitPersonAMLLinearLayout);
            submitPersonAMLLinearLayout.setClickable(true);
            submitPersonAMLProgressBar.setVisibility(View.GONE);

            fNameDataError.setVisibility(View.VISIBLE);
            fNamePlaceHolder.setBackgroundResource(0);
            fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);

            mNameDataError.setVisibility(View.VISIBLE);
            mNamePlaceHolder.setBackgroundResource(0);
            mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);

            lNameDataError.setVisibility(View.VISIBLE);
            lNamePlaceHolder.setBackgroundResource(0);
            lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);

            dobDataError.setVisibility(View.VISIBLE);
            dobPlaceHolder.setBackgroundResource(0);
            dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
        }

        if (isFirstNameEmpty){
            CommonUtils.setButtonAlphaHigh(submitPersonAMLLinearLayout);
            submitPersonAMLLinearLayout.setClickable(true);
            submitPersonAMLProgressBar.setVisibility(View.GONE);
            showFirstNameInputError();
        } else {
            fNameDataError.setVisibility(View.GONE);
            fNamePlaceHolder.setBackgroundResource(0);
            fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        }

        if (isMiddleNameEmpty){
            CommonUtils.setButtonAlphaHigh(submitPersonAMLLinearLayout);
            submitPersonAMLLinearLayout.setClickable(true);
            submitPersonAMLProgressBar.setVisibility(View.GONE);
            showMiddleNameInputError();
        } else {
            mNameDataError.setVisibility(View.GONE);
            mNamePlaceHolder.setBackgroundResource(0);
            mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        }

        if (isLastNameEmpty){
            CommonUtils.setButtonAlphaHigh(submitPersonAMLLinearLayout);
            submitPersonAMLLinearLayout.setClickable(true);
            submitPersonAMLProgressBar.setVisibility(View.GONE);
            showLastNameInputError();
        } else {
            lNameDataError.setVisibility(View.GONE);
            lNamePlaceHolder.setBackgroundResource(0);
            lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        }

        if (!isDateOfBirthEmpty && UserInputValidation.isEnteredDateFormatValid(dateOfBirth)){
            dobDataError.setVisibility(View.GONE);
            dobPlaceHolder.setBackgroundResource(0);
            dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);
        } else {
            CommonUtils.setButtonAlphaHigh(submitPersonAMLLinearLayout);
            submitPersonAMLLinearLayout.setClickable(true);
            submitPersonAMLProgressBar.setVisibility(View.GONE);
            showDOBInputError();
        }

    }

    private void sendRequest(String firstName,
                             String middleName,
                             String lastName,
                             String dateOfBirth) {

        fNameDataError.setVisibility(View.GONE);
        mNameDataError.setVisibility(View.GONE);
        lNameDataError.setVisibility(View.GONE);
        dobDataError.setVisibility(View.GONE);

        submitPersonAMLProgressBar.setVisibility(View.VISIBLE);
        submitPersonAMLLinearLayout.setClickable(false);

        fNamePlaceHolder.setBackgroundResource(0);
        fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        mNameDataError.setVisibility(View.GONE);
        mNamePlaceHolder.setBackgroundResource(0);
        mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        lNameDataError.setVisibility(View.GONE);
        lNamePlaceHolder.setBackgroundResource(0);
        lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        dobDataError.setVisibility(View.GONE);
        dobPlaceHolder.setBackgroundResource(0);
        dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_background);

        CommonUtils.sendRequest(AttestrRequest.actionSubmitPersonAML(
                firstName,
                middleName,
                lastName,
                dateOfBirth
        ));

    }

    private void showFirstNameInputError(){
        fNameDataError.setVisibility(View.VISIBLE);
        fNamePlaceHolder.setBackgroundResource(0);
        fNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
    }

    private void showMiddleNameInputError(){
        mNameDataError.setVisibility(View.VISIBLE);
        mNamePlaceHolder.setBackgroundResource(0);
        mNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
    }

    private void showLastNameInputError(){
        lNameDataError.setVisibility(View.VISIBLE);
        lNamePlaceHolder.setBackgroundResource(0);
        lNamePlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
    }

    private void showDOBInputError(){
        dobDataError.setVisibility(View.VISIBLE);
        dobPlaceHolder.setBackgroundResource(0);
        dobPlaceHolder.setBackgroundResource(R.drawable.edit_text_error_background);
    }

}

