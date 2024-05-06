package com.attestr.flowx.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 29/08/21
 **/
public class VerificationSuccessFragment extends Fragment {

    private ProgressBar continueVerificationProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verification_successful_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HandshakeReadyData handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        String verificationSuccess = GlobalVariables.currentVerificationStage + " " +
                handShakeReadyResponse.getLocale().getSuccess().getLabel();
        TextView continueVerificationButton = view.findViewById(R.id.continue_verification_button);
        ImageView verificationImageView = view.findViewById(R.id.verification_success_image_view);
        CommonUtils.startAnimation(verificationImageView);
        TextView verificationSuccessfulTextView = view.findViewById(R.id.verification_successful_text_view);
        TextView verificationSuccessDescription = view.findViewById(R.id.verification_success_description);
        verificationSuccessfulTextView.setText(verificationSuccess);
        verificationSuccessDescription.setText(handShakeReadyResponse.getLocale().getSuccess().getDescription());
        LinearLayout continueVerificationLinearLayout = view.findViewById(R.id.continue_relative_layout);
        CommonUtils.setButtonAlphaHigh(continueVerificationLinearLayout);
        continueVerificationProgressBar = view.findViewById(R.id.verification_continue_progress_bar);
        continueVerificationProgressBar.setVisibility(View.GONE);
        continueVerificationButton.setText(handShakeReadyResponse.getLocale().getSuccess().getButtonProceed());
        continueVerificationButton.setTextColor(GlobalVariables.textColor);
        continueVerificationLinearLayout.setOnClickListener(v -> {
            CommonUtils.setButtonAlphaLow(continueVerificationLinearLayout);
            continueVerificationProgressBar.setVisibility(View.VISIBLE);
            CommonUtils.sendRequest(AttestrRequest.stageNext());
        });
    }
}
