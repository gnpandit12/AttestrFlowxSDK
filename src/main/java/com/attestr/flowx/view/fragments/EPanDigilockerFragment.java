package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
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
import com.attestr.flowx.model.response.common.StageInitData;
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
 * @Since 30/01/23
 **/
public class EPanDigilockerFragment extends Fragment {
    private HandshakeReadyData handShakeReadyResponse;
    private String ePanDataErrorString;
    private LinearLayout submitePanLinearLayout;
    private ProgressBar submitePanProgressBar;
    private TextView ePanDataError, ePanLabelTextView, ePanDataErrorTextView, submitePanButtonTextView,
            ePanVerificationTitle;
    private EditText ePanPlaceHolderEditText;
    private boolean isePanNumberValid;
    private ResponseData<StageInitData> stageReadyResponse;
    private static EPanDigilockerFragment ePanDigilockerFragment;
    private Activity mActivity;
    private ImageView ePanLabelIcon;
    private Double remainingRetryAttempts;
    private int retryAttempts = 0;

    public static EPanDigilockerFragment getInstance(boolean newInstance) {
        if (newInstance){
            ePanDigilockerFragment = new EPanDigilockerFragment();
            return ePanDigilockerFragment;
        } else {
            if (ePanDigilockerFragment == null){
                ePanDigilockerFragment = new EPanDigilockerFragment();
            }
        }
        return ePanDigilockerFragment;
    }

    public void setData(Activity activity) {
        this.mActivity = activity;
        ProgressIndicator.setActivity(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.epan_digilocker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse =
                GlobalVariables.handshakeReadyResponse;
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        String panNumber = stageReadyResponse.getData().getState().get("pan");
        ePanDataErrorString = handShakeReadyResponse.getLocale().getePanLocale().getFIELD_PAN_DATAERROR();

        submitePanLinearLayout = view.findViewById(R.id.submit_ePan_digilocker_linear_layout);
        CommonUtils.setButtonAlphaHigh(submitePanLinearLayout);
        submitePanProgressBar = view.findViewById(R.id.submit_ePan_digilocker_progress_bar);
        submitePanProgressBar.setVisibility(View.GONE);
        ePanDataError = view.findViewById(R.id.ePan_digilocker_data_error);
        ePanDataError.setVisibility(View.GONE);
        ePanPlaceHolderEditText = view.findViewById(R.id.ePan_digilocker_placeholder);
        ePanLabelIcon = view.findViewById(R.id.ePan_label_icon);

        ePanLabelIcon.setBackgroundResource(R.drawable.id_card);
        ePanLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        remainingRetryAttempts = (Double) GlobalVariables.stageReadyResponse.getData().getMetadata().get("retryAttempts");
        if (remainingRetryAttempts != null) {
            retryAttempts = remainingRetryAttempts.intValue();
        }

        if (panNumber != null) {
            ePanPlaceHolderEditText.setText(panNumber);
            ePanPlaceHolderEditText.setFocusable(false);
        }
        ePanPlaceHolderEditText.setFocusable(true);
        ePanPlaceHolderEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        ePanPlaceHolderEditText.setBackgroundResource(0);
        ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
        ePanDataError.setVisibility(View.GONE);
        ePanLabelTextView = view.findViewById(R.id.ePan_digilocker_label);
        ePanDataErrorTextView = view.findViewById(R.id.ePan_digilocker_data_error_text_view);
        submitePanButtonTextView = view.findViewById(R.id.submit_ePan_digilocker_button);
        submitePanButtonTextView.setTextColor(GlobalVariables.textColor);
        ePanVerificationTitle = view.findViewById(R.id.ePan_digilocker_title);
        ePanVerificationTitle.setText(GlobalVariables.handshakeReadyResponse.getLocale().getStagePan().getTitle());
        submitePanProgressBar.setVisibility(View.GONE);
        ePanDataErrorTextView.setVisibility(View.GONE);
        ePanLabelTextView.setText(handShakeReadyResponse.getLocale().getStagePan().getFieldPanLabel());
        ePanPlaceHolderEditText.setHint(handShakeReadyResponse.getLocale().getStagePan().getFieldPanPlaceholder());
        submitePanButtonTextView.setText(handShakeReadyResponse.getLocale().getStagePan().getButtonPanProceed());
        ePanDataErrorTextView.setText(ePanDataErrorString);
        submitePanLinearLayout.setClickable(true);
        ePanPlaceHolderEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                ePanDataError.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                CommonUtils.setButtonAlphaLow(submitePanLinearLayout);
                sendePanVerificationRequest();
                return false;
            }
            return false;
        });

        ePanPlaceHolderEditText.addTextChangedListener(new InputTextValidator(ePanPlaceHolderEditText) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isePanNumberValid = UserInputValidation.isPanValid(s.toString());
                if (isePanNumberValid){
                    ePanDataErrorTextView.setVisibility(View.GONE);
                    ePanPlaceHolderEditText.setBackgroundResource(0);
                    ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitePanLinearLayout.setClickable(true);
                    submitePanProgressBar.setVisibility(View.GONE);
                    ePanPlaceHolderEditText.setBackgroundResource(0);
                    ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    ePanDataErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        submitePanLinearLayout.setOnClickListener(v -> {
            ePanDataError.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(submitePanLinearLayout);
            sendePanVerificationRequest();
        });

    }

    private void sendePanVerificationRequest() {
        String ePanNumberString = ePanPlaceHolderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(ePanNumberString) && isePanNumberValid) {
            if (UserInputValidation.isPanValid(ePanNumberString)) {
                boolean downloadPdf = (boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("downloadPdf");
                submitePanProgressBar.setVisibility(View.VISIBLE);
                submitePanLinearLayout.setClickable(false);
                ePanPlaceHolderEditText.setBackgroundResource(0);
                ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_background);
                CommonUtils.sendRequest(AttestrRequest.actionePanSubmit(ePanNumberString, downloadPdf));
            } else {
                CommonUtils.setButtonAlphaHigh(submitePanLinearLayout);
                ePanPlaceHolderEditText.setBackgroundResource(0);
                ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                submitePanProgressBar.setVisibility(View.GONE);
                ePanDataErrorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            CommonUtils.setButtonAlphaHigh(submitePanLinearLayout);
            submitePanLinearLayout.setClickable(true);
            submitePanProgressBar.setVisibility(View.GONE);
            ePanPlaceHolderEditText.setBackgroundResource(0);
            ePanPlaceHolderEditText.setBackgroundResource(R.drawable.edit_text_error_background);
            ePanDataErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnDataError(String response) {
        if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("retryInvalid")) {
            isePanNumberValid = false;
            retryAttempts--;
            if (retryAttempts == 0) {
                HandleException.handleSessionError(response);
            } else {
                ePanPlaceHolderEditText.getText().clear();
                submitePanProgressBar.setVisibility(View.GONE);
                submitePanLinearLayout.setClickable(true);
                ePanDataErrorTextView.setText(ePanDataErrorString);
                ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
                ePanDataErrorTextView.setVisibility(View.GONE);
                ePanDataError.setText(dataError.getData().getError().getMessage());
                ePanDataError.setVisibility(View.VISIBLE);
                submitePanLinearLayout.setClickable(true);
                CommonUtils.setButtonAlphaHigh(submitePanLinearLayout);
            }
        } else {
            HandleException.handleSessionError(response);
        }
    }

    public void OnSessionError(String response) {
        retryAttempts--;
        if (retryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            ePanPlaceHolderEditText.getText().clear();
            submitePanProgressBar.setVisibility(View.GONE);
            submitePanLinearLayout.setClickable(true);
            ePanDataErrorTextView.setText(ePanDataErrorString);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>() {
            }.getType());
            ePanDataErrorTextView.setVisibility(View.GONE);
            ePanDataError.setText(dataError.getData().getOriginal().getMessage());
            ePanDataError.setVisibility(View.VISIBLE);
            submitePanLinearLayout.setClickable(true);
            CommonUtils.setButtonAlphaHigh(submitePanLinearLayout);
        }
    }


}
