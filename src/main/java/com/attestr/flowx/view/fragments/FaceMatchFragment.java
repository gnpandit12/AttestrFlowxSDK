package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.listener.ImageSizeExceededListener;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.FaceMatchLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.StageReady;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckFragmentTwo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 13/09/21
 **/
public class FaceMatchFragment extends Fragment implements View.OnClickListener,
        ImageSizeExceededListener, MediaHandshakeListener {

    private static final String TAG = "FaceMatch_fragment";
    private ResponseData<StageInitData> stageReadyResponse;
    private ArrayList<String> documentsList = new ArrayList<>();
    private FaceMatchLocale faceMatchLocale;
    private NestedScrollView nestedScrollView;
    private HandshakeReadyData handShakeReadyResponse;
    private String CAMERA_FACING;
    private Activity mActivity;
    private boolean isFrontDocumentImage, showDocumentDataError;
    private LinearLayout startFrontCameraImageView, frontDocumentImageView,
            backDocumentImageView, documentPickerLinearLayout, documentSpinnerLinearLayout;
    private ImageView selfieImageView, frontImageView, backImageView;
    private TextView selfieLabel, selfiePlaceholder, fieldDocumentLabel, buttonProceedTextView,
            frontDocumentPlaceholder, backDocumentPlaceholder, dataErrorTextView,
            selfieDataErrorTextView, documentDataErrorTextView, documentImageDataErrorTextView;
    private ProgressBar proceedButtonProgressBar;
    private LinearLayout proceedLinearLayout;
    private Spinner documentSpinner;
    private boolean isImageCaptured = false;
    private double thresholdValue;
    private static FaceMatchFragment faceMatchFragment;
    private List<String> docOptionsList;
    private Map<String, String> docOptionsMap;
    private Map<String, Object> digitalAddressLocaleMap;
    private String src, docType, targetFront, targetBack, threshold;
    private LinearLayout.LayoutParams frontDocumentLayoutParam, backDocumentLayoutParam;
    private boolean isDocumentTypeAvailable, isFrontDocumentAvailable, isBackDocumentAvailable;
    private TextView stageTitleTextView;
    private ImageView captureSelfieImageView, documentSpinnerImageView, frontDocImageView, backDocImageView;
    private TextView retryAttemptsTextView;
    private int remainingRetryAttempts = 1;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private PermissionsHandler permissionsHandler;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private CoordinatorLayout coordinatorLayout;
    private ActivityResultLauncher<Intent> storageResultLauncher;
    private byte[] capturedImageByteArray;
    private byte[] selectedFileByteArray;
    private boolean isBottomSheetExpanded = false;
    private LinearLayout backgroundLinearLayout;
    private EventListener eventListener;
    private TextView filePickerCameraTextView, filePickerGalleryTextView, filePickerChooseFileTextView;
    private CommonLocale commonLocale;

    public static FaceMatchFragment getInstance(boolean newInstance) {
        if (newInstance) {
            faceMatchFragment = new FaceMatchFragment();
            return faceMatchFragment;
        } else {
            if (faceMatchFragment == null) {
                faceMatchFragment = new FaceMatchFragment();
            }
        }
        return faceMatchFragment;
    }

    public void setData(Activity activity) {
        mActivity = activity;
        ProgressIndicator.setActivity(mActivity);
        this.eventListener = (EventListener) activity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.face_match_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        stageReadyResponse = GlobalVariables.stageReadyResponse;
        faceMatchLocale = handShakeReadyResponse.getLocale().getFaceMatchLocale();
        docOptionsList = new ArrayList<>();
        docOptionsMap = new HashMap<>();

        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        frontDocumentLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        backDocumentLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        frontDocumentLayoutParam.setMarginEnd(5);
        backDocumentLayoutParam.setMarginStart(5);

        try {
            permissionsHandler = new PermissionsHandler();
            permissionsHandler.setData(mActivity,  FaceMatchFragment.this);
            permissionsHandler.requestPermissions();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        try {
            digitalAddressLocaleMap = CommonUtils.jsonToMap(CommonUtils.getGson().toJson(faceMatchLocale));
        } catch (JSONException e) {
            HandleException.handleInternalException("Digital address locale json to map: " + e);
        }
        docOptionsList = (ArrayList<String>) GlobalVariables.stageReadyResponse.getData().getMetadata().get("docOptions");

        selfieImageView = view.findViewById(R.id.face_match_selfie_image_view);
        frontImageView = view.findViewById(R.id.face_match_front_image_view);
        backImageView = view.findViewById(R.id.face_match_back_image_view);
        startFrontCameraImageView = view.findViewById(R.id.start_camera_image_view);
        frontDocumentImageView = view.findViewById(R.id.front_document_image_view);
        backDocumentImageView = view.findViewById(R.id.back_document_image_view);

        documentPickerLinearLayout = view.findViewById(R.id.document_picker_linear_layout);
        documentSpinnerLinearLayout = view.findViewById(R.id.document_spinner_linear_layout);
        startFrontCameraImageView.setBackgroundResource(0);
        startFrontCameraImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
        frontDocumentImageView.setBackgroundResource(0);
        frontDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
        backDocumentImageView.setBackgroundResource(0);
        backDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
        selfieDataErrorTextView = view.findViewById(R.id.selfie_data_error_text_view);
        documentDataErrorTextView = view.findViewById(R.id.document_data_error_text_view);
        documentImageDataErrorTextView = view.findViewById(R.id.document_image_data_error_text_view);
        selfieDataErrorTextView.setVisibility(View.GONE);
        documentDataErrorTextView.setVisibility(View.GONE);
        documentImageDataErrorTextView.setVisibility(View.GONE);
        bottomSheet = view.findViewById(R.id.face_match_bottom_sheet_linear_layout);

        retryAttemptsTextView = view.findViewById(R.id.face_mathc_retry_attempts_text_view);
        retryAttemptsTextView.setVisibility(View.GONE);

        nestedScrollView = view.findViewById(R.id.face_match_nested_scroll_view);
        selfieLabel = view.findViewById(R.id.selfie_label);
        selfiePlaceholder = view.findViewById(R.id.selfie_placeholder);
        fieldDocumentLabel = view.findViewById(R.id.field_document_label);
        documentSpinner = view.findViewById(R.id.document_spinner);
        frontDocumentPlaceholder = view.findViewById(R.id.front_document_placeholder);
        backDocumentPlaceholder = view.findViewById(R.id.back_document_placeholder);
        buttonProceedTextView = view.findViewById(R.id.face_match_submit_button);
        proceedButtonProgressBar = view.findViewById(R.id.face_match_submit_progress_bar);
        proceedLinearLayout = view.findViewById(R.id.face_match_submit_Relative_layout);
        dataErrorTextView = view.findViewById(R.id.data_error_text_view);
        stageTitleTextView = view.findViewById(R.id.face_match_verification_title);
        coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        stageTitleTextView.setText(faceMatchLocale.getTitle());
        dataErrorTextView.setVisibility(View.GONE);

        captureSelfieImageView = view.findViewById(R.id.capture_selfie);
        documentSpinnerImageView = view.findViewById(R.id.document_spinner_image_view);
        frontDocImageView = view.findViewById(R.id.front_doc_image_view);
        backDocImageView = view.findViewById(R.id.back_doc_image_view);
        backgroundLinearLayout = view.findViewById(R.id.face_match_background_linear_layout);

        captureSelfieImageView.setBackgroundResource(R.drawable.capture_selfie);
        captureSelfieImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        documentSpinnerImageView.setBackgroundResource(R.drawable.id_card);
        documentSpinnerImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        frontDocImageView.setBackgroundResource(R.drawable.image);
        frontDocImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        backDocImageView.setBackgroundResource(R.drawable.image);
        backDocImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));

        documentSpinnerLinearLayout.setVisibility(View.VISIBLE);
        documentPickerLinearLayout.setVisibility(View.VISIBLE);
        documentSpinner.setEnabled(true);

        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        src = stageReadyResponse.getData().getState().get("src");
        docType = stageReadyResponse.getData().getState().get("type");
        targetFront = stageReadyResponse.getData().getState().get("targetFront");
        targetBack = stageReadyResponse.getData().getState().get("targetBack");
        threshold = stageReadyResponse.getData().getState().get("threshold");

        filePickerCameraTextView = bottomSheet.findViewById(R.id.file_picker_option_camera);
        filePickerGalleryTextView = bottomSheet.findViewById(R.id.file_picker_option_gallery);
        filePickerChooseFileTextView = bottomSheet.findViewById(R.id.file_picker_title);

        filePickerCameraTextView.setText(commonLocale.getFilePickerOptionCamera());
        filePickerGalleryTextView.setText(commonLocale.getFilePickerOptionGallery());
        filePickerChooseFileTextView.setText(commonLocale.getFilePickerTitle());

        for (int i = 0; i < docOptionsList.size(); i++) {
            String key = docOptionsList.get(i) + "_DOC_LABEL";
            String documentOption = docOptionsList.get(i);
            documentsList.add(Objects.requireNonNull(digitalAddressLocaleMap.get(key)).toString());
            docOptionsMap.put(Objects.requireNonNull(digitalAddressLocaleMap.get(key)).toString(), documentOption);
        }

        ArrayAdapter<String> documentSpinnerAdapter = new ArrayAdapter<>(mActivity,
                R.layout.document_spinner_items, documentsList);
        documentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentSpinner.setAdapter(documentSpinnerAdapter);
        if (docType != null) {
            try {
                if (digitalAddressLocaleMap.get(docType + "_DOC_LABEL") != null) {
                    String selection = Objects.requireNonNull(digitalAddressLocaleMap.get(docType + "_DOC_LABEL")).toString();
                    documentSpinner.setSelection(documentSpinnerAdapter.getPosition(selection));
                } else {
                    documentSpinner.setSelection(0);
                }
                documentDataErrorTextView.setVisibility(View.GONE);
            } catch (Exception e) {
                HandleException.handleInternalException("Face match document type exception: " + e);
            }
        }
        documentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                docType = docOptionsMap.get(documentsList.get(position));
                documentDataErrorTextView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                docType = docOptionsMap.get(documentsList.get(0));
                documentDataErrorTextView.setVisibility(View.GONE);
            }
        });
        documentSpinner.setPrompt(faceMatchLocale.getFieldDocPickerText());

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        storageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            ProgressIndicator.setProgressBarVisible();
                            if (result.getData() != null) {
                                Uri selectedDocumentUri = result.getData().getData();
                                GlobalVariables.selectedFileName = CommonUtils.getFileName(mActivity, selectedDocumentUri);
                                String selectedFileMimeType = CommonUtils.getMimeType(mActivity, selectedDocumentUri);
                                switch (selectedFileMimeType) {
                                    case "jpg":
                                        GlobalVariables.selectedMediaMimeType = "jpg";
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.capturedImageByteArray = selectedFileByteArray;
                                        break;
                                    case "png":
                                        GlobalVariables.selectedMediaMimeType = "png";
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.capturedImageByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "pdf":
                                        GlobalVariables.selectedMediaMimeType = "pdf";
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.querySelectedPDFByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "xml":
                                        GlobalVariables.selectedMediaMimeType = "xml";
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
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
                            onDocumentSelected((byte[]) null);
                            break;
                    }
                });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        isBottomSheetExpanded = true;
                        backgroundLinearLayout.setAlpha(0.5f);
                        eventListener.changeFooterLayoutVisibility(false);
                        try {
                            View openCameraImageView = bottomSheet.findViewById(R.id.open_camera_image_view);
                            View openGalleryImageView = bottomSheet.findViewById(R.id.open_gallery_image_view);
                            openCameraImageView.setOnClickListener(v -> startCameraActivity(CAMERA_FACING));
                            openGalleryImageView.setOnClickListener(v -> selectDocument());
                        } catch (Exception e) {
                            HandleException.handleInternalException("Bottom Sheet Exception: " + e);
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        isBottomSheetExpanded = false;
                        eventListener.changeFooterLayoutVisibility(true);
                        backgroundLinearLayout.setAlpha(1);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (docType != null) {
            isDocumentTypeAvailable = true;
            if (targetFront != null && targetBack != null) {
                documentSpinnerLinearLayout.setVisibility(View.GONE);
                documentPickerLinearLayout.setVisibility(View.GONE);
                showDocumentDataError = false;
                isFrontDocumentAvailable = true;
                isBackDocumentAvailable = true;
            } else if (targetFront != null) {
                documentSpinner.setEnabled(false);
                frontDocumentImageView.setVisibility(View.GONE);
                backDocumentImageView.setVisibility(View.VISIBLE);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = true;
                isBackDocumentAvailable = false;
            } else if (targetBack != null) {
                documentSpinner.setEnabled(false);
                frontDocumentImageView.setVisibility(View.VISIBLE);
                backDocumentImageView.setVisibility(View.GONE);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = false;
                isBackDocumentAvailable = true;
            } else {
                documentSpinner.setEnabled(false);
                frontDocumentImageView.setVisibility(View.VISIBLE);
                backDocumentImageView.setVisibility(View.VISIBLE);
                frontDocumentImageView.setLayoutParams(frontDocumentLayoutParam);
                backDocumentImageView.setLayoutParams(backDocumentLayoutParam);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = false;
                isBackDocumentAvailable = false;
            }
        } else {
            isDocumentTypeAvailable = false;
            if (targetFront != null && targetBack != null) {
                documentSpinnerLinearLayout.setVisibility(View.GONE);
                docType = docOptionsMap.get(documentsList.get(0));
                documentPickerLinearLayout.setVisibility(View.GONE);
                showDocumentDataError = false;
                isFrontDocumentAvailable = true;
                isBackDocumentAvailable = true;
            } else if (targetFront != null) {
                documentSpinnerLinearLayout.setVisibility(View.VISIBLE);
                documentSpinner.setEnabled(true);
                frontDocumentImageView.setVisibility(View.GONE);
                backDocumentImageView.setVisibility(View.VISIBLE);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = true;
                isBackDocumentAvailable = false;
            } else if (targetBack != null) {
                documentSpinnerLinearLayout.setVisibility(View.VISIBLE);
                documentSpinner.setEnabled(true);
                frontDocumentImageView.setVisibility(View.VISIBLE);
                backDocumentImageView.setVisibility(View.GONE);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = false;
                isBackDocumentAvailable = true;
            } else {
                documentSpinnerLinearLayout.setVisibility(View.VISIBLE);
                documentSpinner.setEnabled(true);
                frontDocumentImageView.setVisibility(View.VISIBLE);
                backDocumentImageView.setVisibility(View.VISIBLE);
                frontDocumentImageView.setLayoutParams(frontDocumentLayoutParam);
                backDocumentImageView.setLayoutParams(backDocumentLayoutParam);
                documentDataErrorTextView.setVisibility(View.VISIBLE);
                showDocumentDataError = true;
                isFrontDocumentAvailable = false;
                isBackDocumentAvailable = false;
            }
        }

        PermissionsHandler permissionsHandler = new PermissionsHandler();
        permissionsHandler.setData(mActivity, FaceMatchFragment.this);

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
                                }
                            }
                            break;
                    }
                });

        selfieImageView.setBackgroundResource(0);
        selfieImageView.setBackgroundResource(R.drawable.start_camera);
        selfieImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        frontImageView.setBackgroundResource(0);
        frontImageView.setBackgroundResource(R.drawable.upload_image);
        frontImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        backImageView.setBackgroundResource(0);
        backImageView.setBackgroundResource(R.drawable.upload_image);
        backImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        startFrontCameraImageView.setOnClickListener(this);
        frontDocumentImageView.setOnClickListener(this);
        backDocumentImageView.setOnClickListener(this);
        proceedLinearLayout.setOnClickListener(this);
        proceedButtonProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        proceedLinearLayout.setClickable(true);

        selfieLabel.setText(faceMatchLocale.getFieldSelfieLabel());
        selfiePlaceholder.setText(faceMatchLocale.getFieldSelfiePlaceholder());
        fieldDocumentLabel.setText(faceMatchLocale.getFieldDocLabel());
        frontDocumentPlaceholder.setText(faceMatchLocale.getFieldFrontDocPlaceholder());
        backDocumentPlaceholder.setText(faceMatchLocale.getFieldBackDocPlaceholder());
        buttonProceedTextView.setText(faceMatchLocale.getButtonProceed());
        thresholdValue = Double.parseDouble(Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("threshold")).toString());
    }

    private void onDocumentSelected(byte[] selectedFileByteArray) {
        if (isBottomSheetExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        sendMediaHandshakeRequest(selectedFileByteArray);
    }

    @Override
    public void onClick(View view) {
        if (view == frontDocumentImageView) {
            CAMERA_FACING = GlobalVariables.CAMERA_FACING_BACK;
            if (isBottomSheetExpanded) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
                eventListener.changeFooterLayoutVisibility(false);
                isFrontDocumentImage = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (view == backDocumentImageView) {
            CAMERA_FACING = GlobalVariables.CAMERA_FACING_BACK;
            if (isBottomSheetExpanded) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
                eventListener.changeFooterLayoutVisibility(false);
                isFrontDocumentImage = false;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (view == startFrontCameraImageView) {
            CAMERA_FACING = GlobalVariables.CAMERA_FACING_FRONT;
            startCameraActivity(CAMERA_FACING);
        } else if (view == proceedLinearLayout) {
            proceed();
        }
    }

    private void proceed() {
        dataErrorTextView.setVisibility(View.GONE);
        if (docType != null) {
            documentDataErrorTextView.setVisibility(View.GONE);
            dataErrorTextView.setVisibility(View.GONE);
            if (isFrontDocumentAvailable && isBackDocumentAvailable) {
                if (isSelfieSourceValid()) {
                    documentImageDataErrorTextView.setVisibility(View.GONE);
                    selfieDataErrorTextView.setVisibility(View.GONE);
                    CommonUtils.setButtonAlphaLow(proceedLinearLayout);
                    proceedButtonProgressBar.setVisibility(View.VISIBLE);
                    CommonUtils.sendRequest(AttestrRequest.actionFaceMatchSubmit(
                            docType,
                            thresholdValue,
                            targetFront,
                            targetBack));
                } else {
                    selfieDataErrorTextView.setText(faceMatchLocale.getFieldSelfieDataerror());
                    selfieDataErrorTextView.setVisibility(View.VISIBLE);
                }
            } else if (isFrontDocumentAvailable) {
                if (isSelfieSourceValid() && isTargetBackValid()) {
                    documentImageDataErrorTextView.setVisibility(View.GONE);
                    selfieDataErrorTextView.setVisibility(View.GONE);
                    CommonUtils.setButtonAlphaLow(proceedLinearLayout);
                    proceedButtonProgressBar.setVisibility(View.VISIBLE);
                    CommonUtils.sendRequest(AttestrRequest.actionFaceMatchSubmit(
                            docType,
                            thresholdValue,
                            targetFront,
                            GlobalVariables.targetBack));
                } else {
                    if (!isSelfieSourceValid()){
                        selfieDataErrorTextView.setText(faceMatchLocale.getFieldSelfieDataerror());
                        selfieDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                    if (!isTargetBackValid()) {
                        documentImageDataErrorTextView.setText(faceMatchLocale.getFieldDocDataerror());
                        documentImageDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            } else if (isBackDocumentAvailable) {
                if (isTargetFrontValid() && isSelfieSourceValid()) {
                    documentImageDataErrorTextView.setVisibility(View.GONE);
                    selfieDataErrorTextView.setVisibility(View.GONE);
                    CommonUtils.setButtonAlphaLow(proceedLinearLayout);
                    proceedButtonProgressBar.setVisibility(View.VISIBLE);
                    CommonUtils.sendRequest(AttestrRequest.actionFaceMatchSubmit(
                            docType,
                            thresholdValue,
                            GlobalVariables.targetFront,
                            targetBack));
                } else {
                    if (!isSelfieSourceValid()){
                        selfieDataErrorTextView.setText(faceMatchLocale.getFieldSelfieDataerror());
                        selfieDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                    if (!isTargetFrontValid()) {
                        documentImageDataErrorTextView.setText(faceMatchLocale.getFieldDocDataerror());
                        documentImageDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (isSelfieSourceValid() && isTargetFrontValid() && isTargetBackValid()) {
                    documentImageDataErrorTextView.setVisibility(View.GONE);
                    selfieDataErrorTextView.setVisibility(View.GONE);
                    CommonUtils.setButtonAlphaLow(proceedLinearLayout);
                    proceedButtonProgressBar.setVisibility(View.VISIBLE);
                    CommonUtils.sendRequest(AttestrRequest.actionFaceMatchSubmit(
                            docType,
                            thresholdValue,
                            GlobalVariables.targetFront,
                            GlobalVariables.targetBack));
                } else {
                    if (!isSelfieSourceValid()){
                        selfieDataErrorTextView.setText(faceMatchLocale.getFieldSelfieDataerror());
                        selfieDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                    if (!isTargetBackValid() || !isTargetFrontValid()) {
                        documentImageDataErrorTextView.setText(faceMatchLocale.getFieldDocDataerror());
                        documentImageDataErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
            proceedButtonProgressBar.setVisibility(View.GONE);
            proceedLinearLayout.setClickable(true);
            documentDataErrorTextView.setText(faceMatchLocale.getFieldDocPickerDataerror());
            documentDataErrorTextView.setVisibility(View.VISIBLE);
            if (!isSelfieSourceValid()){
                selfieDataErrorTextView.setText(faceMatchLocale.getFieldSelfieDataerror());
                selfieDataErrorTextView.setVisibility(View.VISIBLE);
            }
            if (!isTargetBackValid() || !isTargetFrontValid()) {
                documentImageDataErrorTextView.setText(faceMatchLocale.getFieldDocDataerror());
                documentImageDataErrorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isSelfieSourceValid(){
        return UserInputValidation.isSelfieSourceValid();
    }

    private boolean isTargetFrontValid(){
        return UserInputValidation.isTargetFrontValid();
    }

    private boolean isTargetBackValid() {
        return UserInputValidation.isTargetBackValid();
    }

    private void startCameraActivity(String cameraFacing) {
        GlobalVariables.isCameraSelected = true;
        GlobalVariables.isStorageSelected = false;
        Intent intent = new Intent(mActivity, CameraActivity.class);
        intent.putExtra("CAMERA_FACING", cameraFacing);
        intent.putExtra("capture_type", GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE);
        intent.putExtra("current_stage", this.getClass().getSimpleName());
        cameraActivityResultLauncher.launch(intent);
    }

    private void selectDocument() {
        GlobalVariables.isCameraSelected = false;
        GlobalVariables.isStorageSelected = true;
        Intent selectDocumentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectDocumentIntent.setType("*/*");
        selectDocumentIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpg", "image/jpeg"});
        Intent selectXmlFileIntent = Intent.createChooser(selectDocumentIntent, "Select Document");
        storageResultLauncher.launch(selectXmlFileIntent);
    }

    @Override
    public void onImageSizeExceeded(String meta) {
        dataErrorTextView.setText(handShakeReadyResponse.getLocale().getCommon().getMediaSizeExceeded());
        dataErrorTextView.setVisibility(View.VISIBLE);
        proceedLinearLayout.setClickable(true);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        proceedButtonProgressBar.setVisibility(View.GONE);
        switch (meta) {
            case GlobalVariables.FACE_MATCH_METADATA_SELFIE:
                startFrontCameraImageView.setBackgroundResource(0);
                startFrontCameraImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                selfieImageView.setBackgroundResource(0);
                selfieImageView.setBackgroundResource(R.drawable.start_camera);
                selfieImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
                break;
            case GlobalVariables.FACE_MATCH_METADATA_FRONT:
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                frontImageView.setBackgroundResource(0);
                frontImageView.setBackgroundResource(R.drawable.upload_image);
                frontImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
                break;
            case GlobalVariables.FACE_MATCH_METADATA_BACK:
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                backImageView.setBackgroundResource(0);
                backImageView.setBackgroundResource(R.drawable.upload_image);
                backImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
                break;
        }
    }

    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (isBottomSheetExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        sendMediaHandshakeRequest(capturedImageByteArray);
    }

    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    private void sendMediaHandshakeRequest(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null) {
            dataErrorTextView.setVisibility(View.GONE);
            if (GlobalVariables.CAMERA_FACING_FRONT.equals(CAMERA_FACING)) {
                startFrontCameraImageView.setBackgroundResource(0);
                startFrontCameraImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                selfieDataErrorTextView.setVisibility(View.GONE);
                selfieImageView.setBackgroundResource(0);
                selfieImageView.setBackgroundTintList(null);
                selfieImageView.setBackgroundResource(R.drawable.check);
                CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.FACE_MATCH_METADATA_SELFIE));
            } else if (GlobalVariables.CAMERA_FACING_BACK.equals(CAMERA_FACING)) {
                documentImageDataErrorTextView.setVisibility(View.GONE);
                if (isFrontDocumentImage) {
                    frontDocumentImageView.setBackgroundResource(0);
                    frontDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                    frontImageView.setBackgroundResource(0);
                    frontImageView.setBackgroundTintList(null);
                    frontImageView.setBackgroundResource(R.drawable.check);
                    CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.FACE_MATCH_METADATA_FRONT));
                } else {
                    backDocumentImageView.setBackgroundResource(0);
                    backDocumentImageView.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                    backImageView.setBackgroundResource(0);
                    backImageView.setBackgroundTintList(null);
                    backImageView.setBackgroundResource(R.drawable.check);
                    CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.FACE_MATCH_METADATA_BACK));
                }
            }
            isImageCaptured = false;
        } else {
            ProgressIndicator.setProgressBarInvisible();
        }
    }

    public void OnDataError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
            proceedButtonProgressBar.setVisibility(View.GONE);
            proceedLinearLayout.setClickable(true);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            dataErrorTextView.setText(dataError.getData().getError().getMessage());
            dataErrorTextView.setVisibility(View.VISIBLE);
            nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
            retryAttemptsTextView.setText((int) remainingRetryAttempts + " attempts left");
            retryAttemptsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if (remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
            proceedButtonProgressBar.setVisibility(View.GONE);
            proceedLinearLayout.setClickable(true);
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            dataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            dataErrorTextView.setVisibility(View.VISIBLE);
            nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
        }
    }

    @Override
    public void onDocumentUploaded(String metadata, String mediaID, String mimeType) {
        switch (metadata) {
            case GlobalVariables.FACE_MATCH_METADATA_SELFIE:
                GlobalVariables.selfieSource = mediaID;
                break;
            case GlobalVariables.FACE_MATCH_METADATA_FRONT:
                GlobalVariables.targetFront = mediaID;
                break;
            case GlobalVariables.FACE_MATCH_METADATA_BACK:
                GlobalVariables.targetBack = mediaID;
                break;
        }
        ProgressIndicator.setProgressBarInvisible();
    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {
        switch (metadata) {
            case GlobalVariables.FACE_MATCH_METADATA_SELFIE:
                GlobalVariables.selfieSource = mediaID;
                break;
            case GlobalVariables.FACE_MATCH_METADATA_FRONT:
                GlobalVariables.targetFront = mediaID;
                break;
            case GlobalVariables.FACE_MATCH_METADATA_BACK:
                GlobalVariables.targetBack = mediaID;
                break;
        }
        ProgressIndicator.setProgressBarInvisible();
    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {

    }

}

