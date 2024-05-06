package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.databinding.EAadhaarDataErrorFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.locale.EaadhaarLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 26/10/21
 **/
public class EAadhaarDataErrorFragment extends Fragment implements View.OnClickListener {

    private EAadhaarDataErrorFragmentBinding eAadhaarDataErrorFragmentBinding;
    private EaadhaarLocale eaadhaarLocale;
    private HandshakeReadyData handshakeReadyData;
    private TextView dataError, stepOneLinkLabelTextView, stepTwoLinkLabelTextView, stepThree, submitButtonTextView;
    private LinearLayout submitLinearLayout;
    private ProgressBar submitProgressBar;
    private static EAadhaarDataErrorFragment eAadhaarDataErrorFragment;
    private Activity mActivity;

    public EAadhaarDataErrorFragment (){}

    public static EAadhaarDataErrorFragment getInstance(boolean newInstance){
        if (newInstance){
            eAadhaarDataErrorFragment = new EAadhaarDataErrorFragment();
            return eAadhaarDataErrorFragment;
        } else {
            if (eAadhaarDataErrorFragment == null){
                eAadhaarDataErrorFragment = new EAadhaarDataErrorFragment();
            }
        }
        return eAadhaarDataErrorFragment;
    }

    public void setData(Activity activity){
        this.mActivity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eAadhaarDataErrorFragmentBinding = EAadhaarDataErrorFragmentBinding.inflate(inflater, container, false);
        return eAadhaarDataErrorFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handshakeReadyData = GlobalVariables.handshakeReadyResponse;
        eaadhaarLocale = handshakeReadyData.getLocale().getEaadhaarLocale();

        String[] strings = eaadhaarLocale.getStepTwo().split("\\{0\\}");
        submitLinearLayout = eAadhaarDataErrorFragmentBinding.eAadhaarDataErrorSubmitRelativeLayout;
        submitProgressBar = eAadhaarDataErrorFragmentBinding.eAadhaarDataErrorSubmitProgressBar;
        submitButtonTextView = eAadhaarDataErrorFragmentBinding.eAadhaarDataErrorSubmitButton;
        dataError = eAadhaarDataErrorFragmentBinding.fieldDataErrorTextView;
        stepOneLinkLabelTextView = eAadhaarDataErrorFragmentBinding.stepOneLinkLabel;
        stepTwoLinkLabelTextView = eAadhaarDataErrorFragmentBinding.stepTwoLinkLabel;
        stepThree = eAadhaarDataErrorFragmentBinding.stepThree;

        submitProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(submitLinearLayout);

        submitButtonTextView.setText(eaadhaarLocale.getButtonProceed());
        dataError.setText(eaadhaarLocale.getFieldDataerror());
        String stepOne = eaadhaarLocale.getStepOne();
        String stepOneLinkLabel = stepOne.replace("{0}", eaadhaarLocale.getStepOneLinkLabel());
//        String stepOneLinkLabel = stepOneString + eaadhaarLocale.getStepOneLinkLabel();
        String stepTwoLinkLabel = strings[0] + eaadhaarLocale.getStepTwoLinkLabel() + strings[1];

        try {
            SpannableString spannableStringOne = new SpannableString(stepOneLinkLabel);
            ClickableSpan clickableSpanOne = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(eaadhaarLocale.getStepOneLink()));
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(Color.parseColor("#024ef3"));
                }
            };
            spannableStringOne.setSpan(
                    clickableSpanOne,
                    stepOneLinkLabel.indexOf(eaadhaarLocale.getStepOneLinkLabel()),
                    (stepOneLinkLabel.indexOf(eaadhaarLocale.getStepOneLinkLabel()))
                            + eaadhaarLocale.getStepOneLinkLabel().length() + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            stepOneLinkLabelTextView.setText(spannableStringOne);
            stepOneLinkLabelTextView.setMovementMethod(LinkMovementMethod.getInstance());

            SpannableString spannableStringTwo = new SpannableString(stepTwoLinkLabel);
            ClickableSpan clickableSpanTwo = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(eaadhaarLocale.getStepTwoLink()));
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(Color.parseColor("#024ef3"));
                }
            };
            spannableStringTwo.setSpan(
                    clickableSpanTwo,
                    stepTwoLinkLabel.indexOf(eaadhaarLocale.getStepTwoLinkLabel()) - 1,
                    (stepTwoLinkLabel.indexOf(eaadhaarLocale.getStepTwoLinkLabel()) - 1) + eaadhaarLocale.getStepTwoLinkLabel().length() +1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            stepTwoLinkLabelTextView.setText(spannableStringTwo);
            stepTwoLinkLabelTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }

        stepThree.setText(eaadhaarLocale.getStepThree());
        submitLinearLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == submitLinearLayout){
            CommonUtils.sendRequest(AttestrRequest.actionEaadhaarSubmit());
        }
    }

}
