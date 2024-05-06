package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.listener.AadhaarOfflineXmlListener;
import com.attestr.flowx.listener.PermissionsListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.RetryInvalidData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 03/09/21
 **/
public class AadharOfflineXMLVerificationFragment extends Fragment
        implements View.OnClickListener, AadhaarOfflineXmlListener, PermissionsListener{

    private static final String TAG = "aadhaar_offline_xml";
    private NestedScrollView nestedScrollView;
    private HandshakeReadyData handShakeReadyResponse;
    private TextView uploadXmlLabel, uploadXmlErrorTextView, uidaiNumberLabel, uidaiNumberErrorTextView,
    shareCodeLabel, shareCodeErrorTextView, emailLabel, emailErrorTextView, mobileLabel, mobileErrorTextView,
    submitButtonTextView, uploadXmlPlaceholder, xmlDataErrorTextView;
    private ProgressBar submitButtonProgressBar;
    private LinearLayout submitXmlLinearLayout;
    private EditText uidaiNumberPlaceholder, shareCodePlaceholder, emailPlaceholder, mobilePlaceholder;
    private LinearLayout shareCodeLinearLayout, emailLinearLayout, mobileLinearLayout;
    private ImageView selectXmlImageView;
    boolean isEmailValid, isMobileValid, isAadhaarLastFourDigitsEntered, isShareCodeEntered;
    private boolean isXmlFileSelected;
    private Activity mActivity;
    private Uri mSelectedXmlFileUri;
    private byte[] byteArray;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ResponseData<StageInitData> stageReady;
    private String userAadhaarNumber, userEmailID, userMobileNo, code;
    private LinearLayout selectXMLLinearLayout;
    private static AadharOfflineXMLVerificationFragment aadharOfflineXMLVerificationFragment;
    private PermissionsHandler permissionsHandler;
    private ImageView xmlFileIcon, aadharIcon, otpIcon, emailIcon, smartphoneIcon;
    private ActivityResultLauncher<Intent> storageResultLauncher;

    public static AadharOfflineXMLVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            aadharOfflineXMLVerificationFragment = new AadharOfflineXMLVerificationFragment();
            return aadharOfflineXMLVerificationFragment;
        } else {
            if (aadharOfflineXMLVerificationFragment == null){
                aadharOfflineXMLVerificationFragment = new AadharOfflineXMLVerificationFragment();
            }
        }
        return aadharOfflineXMLVerificationFragment;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aadhaar_offline_xml_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stageReady = CommonUtils.gsonBuilder.create().fromJson(GlobalVariables.stageInitResponse, new TypeToken<ResponseData<StageInitData>>(){}.getType());
        GlobalVariables.isValidateEmail = Boolean.parseBoolean(Objects.requireNonNull(stageReady.getData().getMetadata().get("validateEmail")).toString());
        GlobalVariables.isValidateMobile = Boolean.parseBoolean(Objects.requireNonNull(stageReady.getData().getMetadata().get("validateMobile")).toString());
        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;

        userAadhaarNumber = GlobalVariables.stageReadyResponse.getData().getState().get("uuid");
        userEmailID = GlobalVariables.stageReadyResponse.getData().getState().get("email");
        userMobileNo = GlobalVariables.stageReadyResponse.getData().getState().get("mobile");
        code = GlobalVariables.stageReadyResponse.getData().getState().get("code");

        shareCodeLinearLayout = view.findViewById(R.id.share_code_linear_layout);
        emailLinearLayout = view.findViewById(R.id.uidai_email_linear_layout);
        mobileLinearLayout = view.findViewById(R.id.uidai_mobile_linear_layout);

        shareCodeLinearLayout.setVisibility(View.GONE);
        emailLinearLayout.setVisibility(View.GONE);
        mobileLinearLayout.setVisibility(View.GONE);

        selectXMLLinearLayout = view.findViewById(R.id.select_xml_linear_layout);
        xmlDataErrorTextView = view.findViewById(R.id.xml_data_error_text_view);
        xmlDataErrorTextView.setVisibility(View.GONE);
        uploadXmlLabel = view.findViewById(R.id.uidai_xml_label);
        uploadXmlPlaceholder = view.findViewById(R.id.uidai_xml_placeholder);
        uploadXmlErrorTextView = view.findViewById(R.id.uidai_xml_error_text_view);
        uidaiNumberLabel = view.findViewById(R.id.uidai_number_label);
        uidaiNumberPlaceholder = view.findViewById(R.id.uidai_number_placeholder);
        uidaiNumberErrorTextView = view.findViewById(R.id.uidai_number_error_text_view);
        shareCodeLabel = view.findViewById(R.id.share_code_label);
        shareCodePlaceholder = view.findViewById(R.id.share_code_placeholder);
        shareCodeErrorTextView = view.findViewById(R.id.share_code_error_text_view);
        emailLabel = view.findViewById(R.id.uidai_email_label);
        emailPlaceholder = view.findViewById(R.id.uidai_email_placeholder);
        emailErrorTextView = view.findViewById(R.id.uidai_email_error_text_view);
        mobileLabel = view.findViewById(R.id.uidai_mobile_label);
        mobilePlaceholder = view.findViewById(R.id.uidai_mobile_placeholder);
        mobileErrorTextView = view.findViewById(R.id.uidai_mobile_error_text_view);
        submitXmlLinearLayout = view.findViewById(R.id.uidai_offline_xml_submit_Relative_layout);
        submitButtonProgressBar = view.findViewById(R.id.uidai_offline_xml_submit_progress_bar);
        submitButtonTextView = view.findViewById(R.id.uidai_offline_xml_submit_button);
        selectXmlImageView = view.findViewById(R.id.select_xml_image_view);
        nestedScrollView = view.findViewById(R.id.uidai_offline_xml_nested_scroll_view);
        xmlFileIcon = view.findViewById(R.id.xml_file_icon);
        aadharIcon = view.findViewById(R.id.aadhar_icon);
        otpIcon = view.findViewById(R.id.otp_icon);
        emailIcon = view.findViewById(R.id.email_icon);
        smartphoneIcon = view.findViewById(R.id.smartphone_icon);

        TextView aadharOfflineXMLTitle = view.findViewById(R.id.aadhar_offline_verification_title);
        aadharOfflineXMLTitle.setText(handShakeReadyResponse.getLocale().getStageUidai().getTitle());

        if (userAadhaarNumber != null){
            uidaiNumberPlaceholder.setText(userAadhaarNumber);
        }
        if (userEmailID != null){
            emailPlaceholder.setText(userEmailID);
        }
        if (userMobileNo != null){
            mobilePlaceholder.setText(userMobileNo);
        }
        if (code != null){
            shareCodePlaceholder.setText(code);
        }

        submitButtonProgressBar.setVisibility(View.GONE);
        uploadXmlErrorTextView.setVisibility(View.GONE);
        uidaiNumberErrorTextView.setVisibility(View.GONE);
        shareCodeErrorTextView.setVisibility(View.GONE);
        emailErrorTextView.setVisibility(View.GONE);
        mobileErrorTextView.setVisibility(View.GONE);

        uidaiNumberPlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        shareCodePlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        mobilePlaceholder.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        uploadXmlLabel.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidXmlLabel());
        uidaiNumberLabel.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidLabel());
        shareCodeLabel.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldShareCodeLabel());
        emailLabel.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidEmailLabel());
        mobileLabel.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidMobileLabel());

        uploadXmlPlaceholder.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidXmlPlaceholder());
        uidaiNumberPlaceholder.setHint(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidPlaceholder());
        shareCodePlaceholder.setHint(handShakeReadyResponse.getLocale().getStageUidai().getFieldShareCodePlaceholder());
        emailPlaceholder.setHint(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidEmailPlaceholder());
        mobilePlaceholder.setHint(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidMobilePlaceholder());

        uploadXmlErrorTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidXmlDataerror());
        uidaiNumberErrorTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidDataerror());
        shareCodeErrorTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldShareCodeDataerror());
        emailErrorTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidEmailDataerror());
        mobileErrorTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidMobileDataerror());

        submitButtonTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getButtonUuidProceed());

        xmlFileIcon.setBackgroundResource(R.drawable.xml_file_icon);
        xmlFileIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        aadharIcon.setBackgroundResource(R.drawable.aadhaar_icon);
        aadharIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        otpIcon.setBackgroundResource(R.drawable.otp);
        otpIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        emailIcon.setBackgroundResource(R.drawable.email);
        emailIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        smartphoneIcon .setBackgroundResource(R.drawable.smartphone);
        smartphoneIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        selectXmlImageView.setBackgroundResource(0);
        selectXmlImageView.setBackgroundResource(R.drawable.upload_file_icon);

        CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);

        selectXMLLinearLayout.setOnClickListener(this);
        submitXmlLinearLayout.setClickable(true);
        submitXmlLinearLayout.setOnClickListener(this);
        submitButtonTextView.setTextColor(GlobalVariables.textColor);

        uidaiNumberPlaceholder.setBackgroundResource(0);
        uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        emailPlaceholder.setBackgroundResource(0);
        emailPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        mobilePlaceholder.setBackgroundResource(0);
        mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        shareCodePlaceholder.setBackgroundResource(0);
        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        aadhaarXmlVerification();

        try {
            permissionsHandler = new PermissionsHandler();
            permissionsHandler.setData(mActivity,  AadharOfflineXMLVerificationFragment.this);
            permissionsHandler.requestPermissions();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        storageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            byte[] selectedFileByteArray = null;
                            ProgressIndicator.setProgressBarVisible();
                            if (result.getData() != null) {
                                Uri selectedDocumentUri = result.getData().getData();
                                GlobalVariables.selectedFileName = CommonUtils.getFileName(mActivity, selectedDocumentUri);
                                String selectedFileMimeType = CommonUtils.getMimeType(mActivity, selectedDocumentUri);
                                switch (selectedFileMimeType) {
                                    case "jpg":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "jpg";
                                        GlobalVariables.capturedImageByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "png":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "png";
                                        GlobalVariables.capturedImageByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "pdf":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "pdf";
                                        GlobalVariables.querySelectedPDFByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "xml":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "xml";
                                        GlobalVariables.selectedXMLFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;

                                }
                                onDocumentSelected(selectedFileByteArray);
                            } else {
                                onDocumentSelected(null);
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            ProgressIndicator.setProgressBarInvisible();
                            onDocumentSelected(null);
                            break;
                    }
                });

    }

    private void aadhaarXmlVerification() {
        if (GlobalVariables.isValidateEmail && GlobalVariables.isValidateMobile) {
            emailLinearLayout.setVisibility(View.VISIBLE);
            mobileLinearLayout.setVisibility(View.VISIBLE);
            shareCodeLinearLayout.setVisibility(View.VISIBLE);
            emailPlaceholder.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            mobilePlaceholder.setImeOptions(EditorInfo.IME_ACTION_SEND);
            mobilePlaceholder.setOnEditorActionListener((v, actionId, event) -> {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                    sendAadhaarXmlVerificationRequest();
                    return false;
                }
                return false;
            });
            emailPlaceholder.addTextChangedListener(new InputTextValidator(emailPlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isEmailValid = UserInputValidation.validateEmailID(s.toString());
                    if (isEmailValid){
                        emailPlaceholder.setBackgroundResource(0);
                        emailPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        emailErrorTextView.setVisibility(View.GONE);
                    } else {
                        emailPlaceholder.setBackgroundResource(0);
                        emailPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        emailErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });

            mobilePlaceholder.addTextChangedListener(new InputTextValidator(mobilePlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isMobileValid = UserInputValidation.validateMobileNumber(s.toString());
                    if (isMobileValid){
                        mobilePlaceholder.setBackgroundResource(0);
                        mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        mobileErrorTextView.setVisibility(View.GONE);
                    } else {
                        mobilePlaceholder.setBackgroundResource(0);
                        mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        mobileErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });

            shareCodePlaceholder.addTextChangedListener(new InputTextValidator(shareCodePlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isShareCodeEntered = UserInputValidation.isShareCodeEntered(s.toString());
                    if (isShareCodeEntered){
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        shareCodeErrorTextView.setVisibility(View.GONE);
                    } else {
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        shareCodeErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });

        } else if (GlobalVariables.isValidateMobile) {
            mobileLinearLayout.setVisibility(View.VISIBLE);
            shareCodeLinearLayout.setVisibility(View.VISIBLE);
            mobilePlaceholder.setImeOptions(EditorInfo.IME_ACTION_SEND);
            mobilePlaceholder.setOnEditorActionListener((v, actionId, event) -> {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                    sendAadhaarXmlVerificationRequest();
                    return false;
                }
                return false;
            });
            mobilePlaceholder.addTextChangedListener(new InputTextValidator(mobilePlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isMobileValid = UserInputValidation.validateMobileNumber(s.toString());
                    if (isMobileValid){
                        mobilePlaceholder.setBackgroundResource(0);
                        mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        mobileErrorTextView.setVisibility(View.GONE);
                    } else {
                        mobilePlaceholder.setBackgroundResource(0);
                        mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        mobileErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });

            shareCodePlaceholder.addTextChangedListener(new InputTextValidator(shareCodePlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isShareCodeEntered = UserInputValidation.isShareCodeEntered(s.toString());
                    if (isShareCodeEntered){
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        shareCodeErrorTextView.setVisibility(View.GONE);
                    } else {
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        shareCodeErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });

        } else if (GlobalVariables.isValidateEmail) {
            emailLinearLayout.setVisibility(View.VISIBLE);
            shareCodeLinearLayout.setVisibility(View.VISIBLE);
            emailPlaceholder.setImeOptions(EditorInfo.IME_ACTION_SEND);
            emailPlaceholder.addTextChangedListener(new InputTextValidator(emailPlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isEmailValid = UserInputValidation.validateEmailID(s.toString());
                    if (isEmailValid){
                        emailPlaceholder.setBackgroundResource(0);
                        emailPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        emailErrorTextView.setVisibility(View.GONE);
                    } else {
                        emailPlaceholder.setBackgroundResource(0);
                        emailPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        emailErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });
            emailPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                    sendAadhaarXmlVerificationRequest();
                    return false;
                }
                return false;
            });

            shareCodePlaceholder.addTextChangedListener(new InputTextValidator(shareCodePlaceholder) {
                @Override
                public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                    isShareCodeEntered = UserInputValidation.isShareCodeEntered(s.toString());
                    if (isShareCodeEntered){
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        shareCodeErrorTextView.setVisibility(View.GONE);
                    } else {
                        shareCodePlaceholder.setBackgroundResource(0);
                        shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        shareCodeErrorTextView.setVisibility(View.VISIBLE);
                        submitButtonProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        if (!GlobalVariables.isValidateEmail && !GlobalVariables.isValidateMobile){
            uidaiNumberPlaceholder.setImeOptions(EditorInfo.IME_ACTION_SEND);
        }else {
            uidaiNumberPlaceholder.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
        uidaiNumberPlaceholder.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                sendAadhaarXmlVerificationRequest();
                return true;
            }
            return false;
        });

        uidaiNumberPlaceholder.addTextChangedListener(new InputTextValidator(uidaiNumberPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isAadhaarLastFourDigitsEntered = UserInputValidation.isAadhaarLastFourDigitsEntered(s.toString());
                if (isAadhaarLastFourDigitsEntered){
                    uidaiNumberPlaceholder.setBackgroundResource(0);
                    uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    uidaiNumberErrorTextView.setVisibility(View.GONE);
                } else {
                    uidaiNumberPlaceholder.setBackgroundResource(0);
                    uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    uidaiNumberErrorTextView.setVisibility(View.VISIBLE);
                    submitButtonProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == selectXMLLinearLayout){
            selectDocument(new String[]{"text/xml"});
        } else if (view == submitXmlLinearLayout){
            submitXmlLinearLayout.setClickable(false);
            xmlDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
            submitButtonProgressBar.setVisibility(View.VISIBLE);
            sendAadhaarXmlVerificationRequest();
        }
    }

    private void selectDocument(String[] documentsMimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, documentsMimeType);
        Intent selectXmlFileIntent = Intent.createChooser(intent, "Select Document");
        storageResultLauncher.launch(selectXmlFileIntent);
    }

    private void sendAadhaarXmlVerificationRequest() {
        String aadhaarNumber, shareCode, aadhaarLinkedEmail, aadhaarLinkedMobile;
        if (GlobalVariables.isValidateEmail && GlobalVariables.isValidateMobile){
            aadhaarNumber = uidaiNumberPlaceholder.getText().toString().trim();
            shareCode = shareCodePlaceholder.getText().toString().trim();
            aadhaarLinkedEmail = emailPlaceholder.getText().toString().trim();
            aadhaarLinkedMobile = mobilePlaceholder.getText().toString().trim();
            boolean isShareCodeNull = TextUtils.isEmpty(shareCode);
            if (UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber) && UserInputValidation.validateEmailID(aadhaarLinkedEmail) && UserInputValidation.validateMobileNumber(aadhaarLinkedMobile) && !isShareCodeNull && isXmlFileSelected) {
                submitButtonProgressBar.setVisibility(View.VISIBLE);
                GlobalVariables.userAadhaarNumber = aadhaarNumber;
                GlobalVariables.userAadhaarLinkedEmail = aadhaarLinkedEmail;
                GlobalVariables.userAadhaarLinkedMobile = aadhaarLinkedMobile;
                GlobalVariables.xmlShareCode = shareCode;
                CommonUtils.sendRequest(AttestrRequest.actionSubmitMediaHandshake());
            } else {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
            }
            if (!UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                uidaiNumberPlaceholder.setBackgroundResource(0);
                uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                uidaiNumberErrorTextView.setVisibility(View.VISIBLE);
            }
            if (!UserInputValidation.validateEmailID(aadhaarLinkedEmail)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                emailPlaceholder.setBackgroundResource(0);
                emailPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                emailErrorTextView.setVisibility(View.VISIBLE);
            }
            if (!UserInputValidation.validateMobileNumber(aadhaarLinkedMobile)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                mobilePlaceholder.setBackgroundResource(0);
                mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                mobileErrorTextView.setVisibility(View.VISIBLE);
            }

            if (!isXmlFileSelected) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                uploadXmlErrorTextView.setVisibility(View.VISIBLE);
            }


            if (isShareCodeNull) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                shareCodePlaceholder.setBackgroundResource(0);
                shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                shareCodeErrorTextView.setVisibility(View.VISIBLE);
            }

        }else if (GlobalVariables.isValidateEmail){
            aadhaarLinkedEmail = emailPlaceholder.getText().toString().trim();
            aadhaarNumber = uidaiNumberPlaceholder.getText().toString().trim();
            shareCode = shareCodePlaceholder.getText().toString().trim();
            boolean isShareCodeNull = TextUtils.isEmpty(shareCode);
            if (UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber) && UserInputValidation.validateEmailID(aadhaarLinkedEmail) && !isShareCodeNull && isXmlFileSelected) {
                submitButtonProgressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(false);
                GlobalVariables.userAadhaarNumber = aadhaarNumber;
                GlobalVariables.userAadhaarLinkedEmail = aadhaarLinkedEmail;
                GlobalVariables.xmlShareCode = shareCode;
                CommonUtils.sendRequest(AttestrRequest.actionSubmitMediaHandshake());
            } else {
                submitButtonProgressBar.setVisibility(View.GONE);
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
            }

            if (!UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                submitButtonProgressBar.setVisibility(View.GONE);
                uidaiNumberPlaceholder.setBackgroundResource(0);
                uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                uidaiNumberErrorTextView.setVisibility(View.VISIBLE);
            }
            if (!UserInputValidation.validateEmailID(aadhaarLinkedEmail)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                submitButtonProgressBar.setVisibility(View.GONE);
                emailPlaceholder.setBackgroundResource(0);
                emailPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                emailErrorTextView.setVisibility(View.VISIBLE);
            }

            if (isShareCodeNull) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                submitButtonProgressBar.setVisibility(View.GONE);
                shareCodePlaceholder.setBackgroundResource(0);
                shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                shareCodeErrorTextView.setVisibility(View.VISIBLE);
            }

            if (!isXmlFileSelected) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                uploadXmlErrorTextView.setVisibility(View.VISIBLE);
            }

        }else if (GlobalVariables.isValidateMobile){
            aadhaarLinkedMobile = mobilePlaceholder.getText().toString().trim();
            aadhaarNumber = uidaiNumberPlaceholder.getText().toString().trim();
            shareCode = shareCodePlaceholder.getText().toString().trim();
            boolean isShareCodeNull = TextUtils.isEmpty(shareCode);
            if (UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber) && UserInputValidation.validateMobileNumber(aadhaarLinkedMobile) && !isShareCodeNull && isXmlFileSelected) {
                submitButtonProgressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(false);
                GlobalVariables.userAadhaarNumber = aadhaarNumber;
                GlobalVariables.userAadhaarLinkedMobile = aadhaarLinkedMobile;
                GlobalVariables.xmlShareCode = shareCode;
                CommonUtils.sendRequest(AttestrRequest.actionSubmitMediaHandshake());
            } else {
                submitButtonProgressBar.setVisibility(View.GONE);
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
            }

            if (!UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                uidaiNumberPlaceholder.setBackgroundResource(0);
                submitButtonProgressBar.setVisibility(View.GONE);
                uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                uidaiNumberErrorTextView.setVisibility(View.VISIBLE);
            }
            if (!UserInputValidation.validateMobileNumber(aadhaarLinkedMobile)) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                submitButtonProgressBar.setVisibility(View.GONE);
                mobilePlaceholder.setBackgroundResource(0);
                mobilePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                mobileErrorTextView.setVisibility(View.VISIBLE);
            }

            if (isShareCodeNull) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
                submitButtonProgressBar.setVisibility(View.GONE);
                shareCodePlaceholder.setBackgroundResource(0);
                shareCodePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                shareCodeErrorTextView.setVisibility(View.VISIBLE);
            }

            if (!isXmlFileSelected) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                uploadXmlErrorTextView.setVisibility(View.VISIBLE);
            }
        }

        if (!GlobalVariables.isValidateEmail && !GlobalVariables.isValidateMobile){
            aadhaarNumber = uidaiNumberPlaceholder.getText().toString().trim();
            if (UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber) && isXmlFileSelected) {
                GlobalVariables.userAadhaarNumber = aadhaarNumber;
                submitButtonProgressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(false);
                CommonUtils.sendRequest(AttestrRequest.actionSubmitMediaHandshake());
            } else {
                submitButtonProgressBar.setVisibility(View.GONE);
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
            }

            if (!UserInputValidation.isAadhaarLastFourDigitsEntered(aadhaarNumber)) {
                uidaiNumberPlaceholder.setBackgroundResource(0);
                uidaiNumberPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                uidaiNumberErrorTextView.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitXmlLinearLayout.setClickable(true);
            }

            if (!isXmlFileSelected) {
                CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
                submitButtonProgressBar.setVisibility(View.GONE);
                submitXmlLinearLayout.setClickable(true);
                uploadXmlErrorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFileSizeExceeded() {
        CommonUtils.mediaUploadAlertDialog(mActivity);
        nestedScrollView.smoothScrollTo(0,0);
        CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
        submitXmlLinearLayout.setClickable(true);
        submitButtonProgressBar.setVisibility(View.GONE);
        uidaiNumberPlaceholder.clearFocus();
        shareCodePlaceholder.clearFocus();
        mobilePlaceholder.clearFocus();
        emailPlaceholder.clearFocus();
        selectXmlImageView.setBackgroundResource(0);
        selectXmlImageView.setBackgroundResource(R.drawable.upload_file_icon);
        xmlDataErrorTextView.setText(handShakeReadyResponse.getLocale().getCommon().getMediaSizeExceeded());
        xmlDataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnDataError(String response) {
        nestedScrollView.smoothScrollTo(0,0);
        isAadhaarLastFourDigitsEntered = false;
        isEmailValid = false;
        isMobileValid = false;
        isShareCodeEntered = false;
        isXmlFileSelected = false;
        CommonUtils.setButtonAlphaHigh(submitXmlLinearLayout);
        submitButtonProgressBar.setVisibility(View.GONE);
        submitXmlLinearLayout.setClickable(true);
        ResponseData<RetryInvalidData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<RetryInvalidData>>(){}.getType());
        uploadXmlErrorTextView.setVisibility(View.GONE);
        xmlDataErrorTextView.setText(dataError.getData().getError().getMessage());
        xmlDataErrorTextView.setVisibility(View.VISIBLE);
        selectXmlImageView.setBackgroundResource(0);
        selectXmlImageView.setBackgroundResource(R.drawable.upload_file_icon);
        uploadXmlPlaceholder.setText(handShakeReadyResponse.getLocale().getStageUidai().getButtonUuidXmlReplace());
    }

    @Override
    public void onImageCaptured(byte[] capturedImageByteArray) {

    }

    @Override
    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    @Override
    public void onDocumentSelected(byte[] selectedFileByteArray) {
        ProgressIndicator.setProgressBarInvisible();
        if (selectedFileByteArray != null) {
            xmlDataErrorTextView.setVisibility(View.GONE);
            isXmlFileSelected = true;
            xmlDataErrorTextView.setVisibility(View.GONE);
            uploadXmlErrorTextView.setVisibility(View.GONE);
            selectXmlImageView.setBackgroundResource(0);
            selectXmlImageView.setBackgroundResource(R.drawable.check);
            uploadXmlPlaceholder.setText(handShakeReadyResponse.getLocale().getStageUidai().getButtonUuidXmlReplace());
        } else {
            isXmlFileSelected = false;
            uploadXmlErrorTextView.setVisibility(View.GONE);
            selectXmlImageView.setBackgroundResource(0);
            selectXmlImageView.setBackgroundResource(R.drawable.upload_file_icon);
            uploadXmlPlaceholder.setText(handShakeReadyResponse.getLocale().getStageUidai().getFieldUuidXmlPlaceholder());
        }
    }

}
