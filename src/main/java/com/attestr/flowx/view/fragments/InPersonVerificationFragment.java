package com.attestr.flowx.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.listener.PermissionsListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.IPVLocale;
import com.attestr.flowx.model.response.locale.ModalLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 12/10/22
 **/
public class InPersonVerificationFragment extends Fragment
        implements View.OnClickListener, MediaHandshakeListener {

    private static InPersonVerificationFragment inPersonVerificationFragment;
    private Activity mActivity;
    private ResponseData<StageInitData> stageReadyResponse;
    private HandshakeReadyData handShakeReadyResponse;
    private TextView inPersonDataErrorTextView, cameraPlaceholder, cameraLabel;
    private ConstraintLayout openCamera;
    private ImageView recordVideoSuccessImageView, captureVideoImageView;
    private String ipvImageMediaID, ipvVideoMediaID;
    private long mStartTimeStamp = 0;
    private long mEndTimeStamp = 0;
    private IPVLocale ipvLocale;
    private LinearLayout buttonCancelRelativeLayout;
    private LinearLayout buttonSubmitIpvLinearLayout;
    private TextView buttonCancelTextView, buttonSubmitIpvTextView;
    private WebView otpWebView;
    private byte[] recordedVideoBytes = null;
    private ProgressBar submitIpvProgressBar;
    private String otp = null;
    private boolean geolocationEnabled = false;
    private boolean voiceOTPEnabled, handwrittenOTPEnabled;
    private TextView fieldCaptureVideoLabel;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private PermissionsHandler permissionsHandler;
    private boolean isFromOnCreate = false;

    public static InPersonVerificationFragment getInstance(boolean newInstance) {
        if (newInstance) {
            inPersonVerificationFragment = new InPersonVerificationFragment();
            return inPersonVerificationFragment;
        } else {
            if (inPersonVerificationFragment == null) {
                inPersonVerificationFragment = new InPersonVerificationFragment();
            }
        }
        return inPersonVerificationFragment;
    }

    public void setData(Activity activity) {
        try {
            this.mActivity = activity;
            ProgressIndicator.setActivity(mActivity);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.in_person_verification_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        stageReadyResponse = GlobalVariables.stageReadyResponse;


        isFromOnCreate = true;

        voiceOTPEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled");
        handwrittenOTPEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("handwrittenOTPEnabled");
        boolean showQR = (boolean) stageReadyResponse.getData().getMetadata().get("showQR");
        boolean retryInvalid = (boolean) stageReadyResponse.getData().getMetadata().get("retryInvalid");
        geolocationEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("geolocationEnabled");
        ipvLocale = handShakeReadyResponse.getLocale().getIpvLocale();

        openCamera = view.findViewById(R.id.in_person_verification_open_camera);
        recordVideoSuccessImageView = view.findViewById(R.id.video_capture_success_image_view);
        captureVideoImageView = view.findViewById(R.id.capture_video_image_view);
        inPersonDataErrorTextView = view.findViewById(R.id.field_video_data_error);
        cameraLabel = view.findViewById(R.id.field_video_label);
        cameraPlaceholder = view.findViewById(R.id.field_video_placeholer);
        buttonCancelRelativeLayout = view.findViewById(R.id.in_person_verification_button_cancel_relative_layout);
        buttonSubmitIpvLinearLayout = view.findViewById(R.id.in_person_verification_button_proceed_relative_layout);
        buttonCancelTextView = view.findViewById(R.id.in_person_verification_button_cancel_text_view);
        buttonSubmitIpvTextView = view.findViewById(R.id.in_person_verification_button_proceed_text_view);
        submitIpvProgressBar = view.findViewById(R.id.in_person_verification_button_proceed_progress_bar);
        fieldCaptureVideoLabel = view.findViewById(R.id.field_capture_video_label);
        fieldCaptureVideoLabel.setText(ipvLocale.getFieldCaptureVideoLabel());

        try {
            submitIpvProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        Random rand = new Random();
        otp = String.format("%04d", rand.nextInt(10000));
        GlobalVariables.ipvOTP = otp;

        otpWebView = view.findViewById(R.id.in_person_verification_svg_web_view);
        otpWebView.setBackgroundColor(getResources().getColor(R.color.background));

        otpWebView.loadDataWithBaseURL(
                null,
                GlobalVariables.IPV_SVG_URL.replace("otp", otp),
                "text/html",
                "UTF-8",
                "");

        if (handwrittenOTPEnabled) {
            otpWebView.setVisibility(View.VISIBLE);
        } else {
            otpWebView.setVisibility(View.GONE);
        }

        inPersonDataErrorTextView.setVisibility(View.GONE);

        cameraPlaceholder.setText(ipvLocale.getFieldVideoPlaceholder());
        buttonSubmitIpvTextView.setText(ipvLocale.getButtonProceed());
        ModalLocale modalLocale = handShakeReadyResponse.getLocale().getModal();
        buttonCancelTextView.setText(modalLocale.getSkipCancel());

        recordVideoSuccessImageView.setBackgroundResource(0);
        recordVideoSuccessImageView.setBackgroundResource(R.drawable.ipv_start_camera);
        recordVideoSuccessImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        captureVideoImageView.setBackgroundResource(0);
        captureVideoImageView.setBackgroundResource(R.drawable.record_video);
        captureVideoImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        cameraPlaceholder.setTextColor(Color.parseColor(GlobalVariables.themeColor));

        CommonUtils.setButtonAlphaHigh(buttonSubmitIpvLinearLayout);
        buttonSubmitIpvLinearLayout.setClickable(true);
        buttonSubmitIpvLinearLayout.setOnClickListener(this);
        buttonCancelRelativeLayout.setOnClickListener(this);
        buttonCancelRelativeLayout.setClickable(true);

        openCamera.setOnClickListener(this);

        permissionsHandler = new PermissionsHandler();
        permissionsHandler.setData(mActivity, InPersonVerificationFragment.this);
        permissionsHandler.requestPermissions();

        if (voiceOTPEnabled && handwrittenOTPEnabled) {
            cameraLabel.setText(ipvLocale.getFieldAudioVideoLabel());
        } else if (voiceOTPEnabled) {
            cameraLabel.setText(ipvLocale.getFieldAudioLabel());
        } else if (handwrittenOTPEnabled) {
            cameraLabel.setText(ipvLocale.getFieldVideoLabel());
        }

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    HandleException.setCurrentActivity(mActivity);
                    byte[] capturedImageByteArray;
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            HandleException.setCurrentActivity(mActivity);
                            if (result.getData() != null) {
                                String captureType = result.getData().getStringExtra("captureType");
                                if (captureType != null) {
                                    ProgressIndicator.setProgressBarVisible();
                                    switch (captureType) {
                                        case GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE:
                                            capturedImageByteArray = GlobalVariables.capturedImageByteArray;
                                            onImageCaptured(capturedImageByteArray);
                                            break;
                                        case GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO:
                                            onImageCaptured(GlobalVariables.capturedImageByteArray);
                                            long startTimeStamp = result.getData().getLongExtra("start_time_stamp", 0);
                                            long endTimeStamp = result.getData().getLongExtra("end_time_stamp", 0);
                                            onVideoRecorded(GlobalVariables.recordedVideoBytes, startTimeStamp, endTimeStamp);
                                            break;
                                    }
                                }
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            if (result.getData() != null) {
                                switch (result.getData().getStringExtra("RESULT")) {
                                    case "Exception":
                                        HandleException.handleInternalException(result.getData().getStringExtra("exception"));
                                        break;
                                    case "onBackPressed":
                                        break;
                                    case "TimeOutException":
                                        HandleException.exitLaunchActivity();
                                        break;
                                    case "GENERATE_QR":
                                        if (Boolean.parseBoolean(Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("showQR")).toString())) {
                                            ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, null));
                                            CommonUtils.sendRequest(AttestrRequest.actionQRGenerate(false));
                                        } else {
                                            HandleException.handleCameraException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getCamError103());
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                });

    }

    private void sendImageMediaHandshakeRequest(byte[] imageByteArray) {
        GlobalVariables.ipvImageMimetype = "jpg";
        GlobalVariables.capturedImageByteArray = imageByteArray;
        CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.IPV_META_SELFIE));
    }

    public void sendVideoMediaHandshakeRequest(byte[] recordedVideoBytes) {
        try {
            GlobalVariables.selfieVideoByteArray = recordedVideoBytes;
            GlobalVariables.ipvVideoMimetype = ".mp4";
            CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.IPV_META_VIDEO));
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == openCamera) {
            startCameraActivity();
        } else if (v == buttonSubmitIpvLinearLayout) {
            submitIpv();
        } else if (v == buttonCancelRelativeLayout) {
            ModalLocale modalLocale = handShakeReadyResponse.getLocale().getModal();
            CommonUtils.showTerminateAlertMessage(
                    mActivity,
                    modalLocale.getSkipTile(),
                    modalLocale.getSkipDescription(),
                    modalLocale.getSkipProcceed(),
                    modalLocale.getSkipCancel());
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(mActivity, CameraActivity.class);
        intent.putExtra("CAMERA_FACING", GlobalVariables.CAMERA_FACING_FRONT);
        intent.putExtra("capture_type", GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO);
        intent.putExtra("current_stage", this.getClass().getSimpleName());
        cameraActivityResultLauncher.launch(intent);
    }

    private void submitIpv() {
        if (ipvVideoMediaID != null && ipvImageMediaID != null) {
            inPersonDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(buttonSubmitIpvLinearLayout);
            buttonSubmitIpvLinearLayout.setClickable(false);
            submitIpvProgressBar.setVisibility(View.VISIBLE);
            if (geolocationEnabled && isLocationDetected()) {
                CommonUtils.sendRequest(AttestrRequest.actionIpvSubmit(
                        ipvVideoMediaID,
                        ipvImageMediaID,
                        otp,
                        String.valueOf(mStartTimeStamp),
                        String.valueOf(mEndTimeStamp),
                        String.valueOf(GlobalVariables.detectedLatitude),
                        String.valueOf(GlobalVariables.detectedLongitude)));
            } else {
                CommonUtils.sendRequest(AttestrRequest.actionIpvSubmit(
                        ipvVideoMediaID,
                        ipvImageMediaID,
                        otp,
                        String.valueOf(mStartTimeStamp),
                        String.valueOf(mEndTimeStamp),
                        null,
                        null));
            }
        } else {
            CommonUtils.setButtonAlphaHigh(buttonSubmitIpvLinearLayout);
            buttonSubmitIpvLinearLayout.setClickable(true);
            submitIpvProgressBar.setVisibility(View.GONE);
            inPersonDataErrorTextView.setVisibility(View.VISIBLE);
            inPersonDataErrorTextView.setText(ipvLocale.getFieldVideoDataerror());
        }
    }

    private boolean isLocationDetected() {
        return !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLatitude)) &&
                !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLongitude));
    }

    @Override
    public void onDocumentUploaded(String mediaID, String documentName, String mimeType) {

    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {
        try {
            ipvImageMediaID = mediaID;
            if (recordedVideoBytes != null) {
                sendVideoMediaHandshakeRequest(recordedVideoBytes);
            }
        } catch (Exception e) {
            HandleException.handleInternalException("IPV OnImageUploaded Exception: " + e);
        }
    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {
        try {
            ipvVideoMediaID = mediaID;
            ProgressIndicator.setProgressBarInvisible();
            recordVideoSuccessImageView.setBackgroundResource(0);
            recordVideoSuccessImageView.setBackgroundResource(R.drawable.check);
            cameraPlaceholder.setText(ipvLocale.getFieldVideoReplace());
        } catch (Exception e) {
            HandleException.handleInternalException("IPV OnVideoUploaded Exception: " + e);
        }
    }

    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null) {
            sendImageMediaHandshakeRequest(capturedImageByteArray);
        }
    }

    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {
        recordedVideoBytes = numberOfBytesRecorded;
        mStartTimeStamp = startTimeStamp;
        mEndTimeStamp = endTimeStamp;
        inPersonDataErrorTextView.setVisibility(View.GONE);

        Date startDate = new Date(mStartTimeStamp);
        DateFormat startDateFormate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String startedAt = startDateFormate.format(startDate);

        Date endDate = new Date(mEndTimeStamp);
        DateFormat endDateFormate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String completedAt = endDateFormate.format(endDate);

    }

    public void OnDataError(String response) {
        CommonUtils.setButtonAlphaHigh(buttonSubmitIpvLinearLayout);
        submitIpvProgressBar.setVisibility(View.GONE);
        submitIpvProgressBar.setClickable(true);
        ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>() {
        }.getType());
        inPersonDataErrorTextView.setText(dataError.getData().getError().getMessage());
        inPersonDataErrorTextView.setVisibility(View.VISIBLE);
    }

    public void OnSessionError(String response) {
        HandleException.handleSessionError(response);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFromOnCreate & geolocationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    CommonUtils.showLocationPermissionAlertDialog(mActivity);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isFromOnCreate = false;
    }


}
