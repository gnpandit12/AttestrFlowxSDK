package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.EducationVerificationFragmentBinding;
import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.listener.ImageSizeExceededListener;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.listener.MediaImagesListener;
import com.attestr.flowx.listener.PermissionsListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.MediaDataClass;
import com.attestr.flowx.model.MediaRecyclerViewAdapter;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.EducationVerificationLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 12/12/21
 **/
public class EducationVerificationFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, MediaHandshakeListener,
        PermissionsListener, ImageSizeExceededListener, MediaImagesListener {

    private static EducationVerificationFragment educationVerificationFragment;
    private EducationVerificationFragmentBinding educationVerificationFragmentBinding;
    private CommonLocale commonLocale;
    private EducationVerificationLocale educationVerificationLocale;
    private ResponseData<StageInitData> stageInitData;
    private Activity mActivity;
    private TextView educationLevelLabel, courseLabel, courseDataError, regLabel,
            regDataError, durationLabel, startMonthLabel, startYearLabel, startYearDataError,
            courseProgressLabel, endMonthLabel, endYearLabel, endYearDataError, documentDataError,
            documentLabel, submitButtonTextView, fieldDocumentDataError;
    private EditText coursePlaceholder, regPlaceholder, startYearPlaceholder,
            endYearPlaceholder;
    private ProgressBar submitProgressBar;
    private Spinner startMonthSpinner, endMonthSpinner;
    private LinearLayout submitLinearLayout;
    private LinearLayout courseEndLinearLayout;
    private String selectedStartMonth, selectedEndMonth,
            startMonth, endMonth, courseLevel, course;
    private Map<String, Object> educationLocaleKeyMap;
    private int isCourseInProgress;
    private RadioGroup radioGroup;
    private RadioButton radioButton, yesRadioButton, noRadioButton;
    private boolean isCourseValid, isRegValid, isStartYearValid, isEndYearValid, isCourseEnded;
    private JSONArray documentsArray;
    private int marginBottom = 10;
    private LinearLayout.LayoutParams layoutParams;
    private ArrayList<String> documentMediaIDList, monthsList;
    private Map<String, String> monthMap;
    private static int monthCount = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout coordinatorLayout;
    private View bottomSheet;
    private boolean isBottomSheetExpanded = false;
    private EventListener eventListener;
    private LinearLayout backgroundLinearLayout;
    private NestedScrollView nestedScrollView;
    private PermissionsHandler permissionsHandler;
    private int currentYear;
    private LayoutInflater layoutInflater;
    private TextView stageTitleTextView;
    private ImageView educationLevelLabelImageIcon, fieldCourseLabelIcon, regLabelIcon, fieldDurationIcon, fieldCourseProgramLabelIcon;
    private ActivityResultLauncher<Intent> storageResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ImageView documentImageView;
    private String selectedImageType = null;
    private RecyclerView educationVerificationRecyclerView;
    private MediaRecyclerViewAdapter mediaRecyclerViewAdapter;
    private ArrayList<MediaDataClass> imagesArrayList;
    private byte[] selectedFileByteArray;
    private byte[] capturedImageByteArray;
    private File selectedDocumentFile;
    private TextView filePickerCameraTextView, filePickerGalleryTextView, filePickerChooseFileTextView;

    public static EducationVerificationFragment getInstance(boolean newInstance) {
        if (newInstance) {
            educationVerificationFragment = new EducationVerificationFragment();
            return educationVerificationFragment;
        } else {
            if (educationVerificationFragment == null) {
                educationVerificationFragment = new EducationVerificationFragment();
            }
        }
        return educationVerificationFragment;
    }

    public void setData(Activity activity, boolean isTablet) {
        this.mActivity = activity;
        this.eventListener = (EventListener) activity;
        if (CommonUtils.isTablet(activity)) {
            marginBottom = 40;
        } else {
            marginBottom = 25;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        educationVerificationFragmentBinding = EducationVerificationFragmentBinding.inflate(inflater, container, false);
        return educationVerificationFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        educationVerificationLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEducationVerificationLocale();
        stageInitData = GlobalVariables.stageReadyResponse;
        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        monthMap = new HashMap<>();

        layoutInflater = LayoutInflater.from(mActivity);
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

        for (int i = 0; i < monthsList.size(); i++) {
            monthCount = monthCount + 1;
            monthMap.put(monthsList.get(i), String.valueOf(monthCount));
        }

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, marginBottom);

        startMonth = stageInitData.getData().getState().get("startMonth");
        endMonth = stageInitData.getData().getState().get("endedMonth");
        course = String.valueOf(stageInitData.getData().getMetadata().get("course"));
        courseLevel = String.valueOf(stageInitData.getData().getMetadata().get("level"));

        educationLevelLabel = educationVerificationFragmentBinding.eductionLevelLabel;
        courseLabel = educationVerificationFragmentBinding.fieldCourseLabel;
        coursePlaceholder = educationVerificationFragmentBinding.fieldCoursePlaceholder;
        courseDataError = educationVerificationFragmentBinding.fieldCourseDataError;
        regLabel = educationVerificationFragmentBinding.fieldRegLabel;
        regPlaceholder = educationVerificationFragmentBinding.fieldRegPlaceholder;
        regDataError = educationVerificationFragmentBinding.fieldRegDataError;
        durationLabel = educationVerificationFragmentBinding.fieldDurationLabel;
        startMonthLabel = educationVerificationFragmentBinding.filedStartMonthLabel;
        startYearLabel = educationVerificationFragmentBinding.fieldStartYearLabel;
        startYearDataError = educationVerificationFragmentBinding.fieldStartYearDataError;
        courseProgressLabel = educationVerificationFragmentBinding.fieldCourseProgressLabel;
        endMonthLabel = educationVerificationFragmentBinding.fieldEndMonthLabel;
        endYearLabel = educationVerificationFragmentBinding.endYearLabel;
        endYearDataError = educationVerificationFragmentBinding.fieldEndYearDataError;
        documentLabel = educationVerificationFragmentBinding.educationVerifcationFieldDocumentLabel;
        submitButtonTextView = educationVerificationFragmentBinding.educationVerificationSubmitTextView;
        startYearPlaceholder = educationVerificationFragmentBinding.fieldStartYearPlaceholder;
        endYearPlaceholder = educationVerificationFragmentBinding.fieldEndYearPlaceholder;
        submitProgressBar = educationVerificationFragmentBinding.educationVerificationSubmitProgressbar;
        startMonthSpinner = educationVerificationFragmentBinding.startMonthSpinner;
        endMonthSpinner = educationVerificationFragmentBinding.endMonthSpinner;
        submitLinearLayout = educationVerificationFragmentBinding.educationVerificationSubmitRelativeLayout;
        courseEndLinearLayout = educationVerificationFragmentBinding.courseEndLinearLayout;
        radioGroup = educationVerificationFragmentBinding.radioGroup;
        yesRadioButton = educationVerificationFragmentBinding.fieldCourseProgressYes;
        noRadioButton = educationVerificationFragmentBinding.fieldCourseProgressNo;
        documentDataError = educationVerificationFragmentBinding.fieldDocumentDataError;
        backgroundLinearLayout = educationVerificationFragmentBinding.backgroundLinearLayout;
        nestedScrollView = educationVerificationFragmentBinding.educationVerificationNestedScrollView;
        fieldDocumentDataError = educationVerificationFragmentBinding.fieldDocumentDataError;
        stageTitleTextView = educationVerificationFragmentBinding.educationVerificationTitleTextView;
        educationLevelLabelImageIcon = educationVerificationFragmentBinding.fieldCourseLabelIcon;
        fieldCourseLabelIcon = educationVerificationFragmentBinding.fieldCourseLabelIcon;
        regLabelIcon = educationVerificationFragmentBinding.fieldRegLabelIcon;
        fieldDurationIcon = educationVerificationFragmentBinding.fieldDurationIcon;
        fieldCourseProgramLabelIcon = educationVerificationFragmentBinding.fieldCourseProgressLabelIcon;
        documentImageView = educationVerificationFragmentBinding.educationVerifcationDocumentImageView;
        educationVerificationRecyclerView = educationVerificationFragmentBinding.educationVerifcationRecyclerView;

        educationLevelLabelImageIcon.setBackgroundResource(R.drawable.id_card);
        educationLevelLabelImageIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        fieldCourseLabelIcon.setBackgroundResource(R.drawable.id_card);
        fieldCourseLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        regLabelIcon.setBackgroundResource(R.drawable.id_card);
        regLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        fieldDurationIcon.setBackgroundResource(R.drawable.id_card);
        fieldDurationIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        fieldCourseProgramLabelIcon.setBackgroundResource(R.drawable.id_card);
        fieldCourseProgramLabelIcon.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
        documentImageView.setBackgroundResource(R.drawable.id_card);
        documentImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        stageTitleTextView.setText(educationVerificationLocale.getTitle());
        nestedScrollView.smoothScrollTo(0, 0);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(this);

        startMonthSpinner.setBackgroundResource(0);
        startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
        endMonthSpinner.setBackgroundResource(0);
        endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);

        coordinatorLayout = educationVerificationFragmentBinding.coordinatorLayout;
        bottomSheet = coordinatorLayout.findViewById(R.id.education_verification_bottom_sheet_linear_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        filePickerCameraTextView = bottomSheet.findViewById(R.id.file_picker_option_camera);
        filePickerGalleryTextView = bottomSheet.findViewById(R.id.file_picker_option_gallery);
        filePickerChooseFileTextView = bottomSheet.findViewById(R.id.file_picker_title);

        filePickerCameraTextView.setText(commonLocale.getFilePickerOptionCamera());
        filePickerGalleryTextView.setText(commonLocale.getFilePickerOptionGallery());
        filePickerChooseFileTextView.setText(commonLocale.getFilePickerTitle());

        try {
            permissionsHandler = new PermissionsHandler();
            permissionsHandler.setData(mActivity, EducationVerificationFragment.this);
            permissionsHandler.requestPermissions();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        documentMediaIDList = new ArrayList<>(10);
        Bitmap addIcon = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.add);
        MediaDataClass mediaDataClass = new MediaDataClass();
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
            imagesArrayList = new ArrayList<>(10);
            mediaDataClass.setmTag("ADD_IMAGE");
            mediaDataClass.setSelectedImageBitmap(addIcon);
            imagesArrayList.add(mediaDataClass);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, null, this, 10);
            educationVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter);
            educationVerificationRecyclerView.setLayoutManager(gridLayoutManager);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

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
                                selectedDocumentFile = new File(selectedDocumentUri.getPath());
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


        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    HandleException.setCurrentActivity(mActivity);
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
                                        if (isBottomSheetExpanded) {
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }
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


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    isBottomSheetExpanded = true;
                    backgroundLinearLayout.setAlpha(0.5f);
                    eventListener.changeFooterLayoutVisibility(false);
                    try {
                        View openCameraImageView = bottomSheet.findViewById(R.id.open_camera_image_view);
                        View openGalleryImageView = bottomSheet.findViewById(R.id.open_gallery_image_view);
                        openCameraImageView.setOnClickListener(v -> startCameraActivity());
                        openGalleryImageView.setOnClickListener(v -> selectDocument());
                    } catch (Exception e) {
                        HandleException.handleInternalException("Bottom Sheet Exception: " + e);
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isBottomSheetExpanded = false;
                    eventListener.changeFooterLayoutVisibility(true);
                    backgroundLinearLayout.setAlpha(1);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        eventListener.changeFooterLayoutVisibility(true);

        yesRadioButton.setText(educationVerificationLocale.getFieldCourseProgressYes());
        noRadioButton.setText(educationVerificationLocale.getFieldCourseProgressNo());
        courseEndLinearLayout.setVisibility(View.VISIBLE);
        documentDataError.setText(educationVerificationLocale.getFieldDocumentsDataerror());
        CommonUtils.setButtonAlphaHigh(submitLinearLayout);
        submitProgressBar.setVisibility(View.GONE);
        submitLinearLayout.setClickable(true);

        courseLabel.setText(educationVerificationLocale.getFieldCourseLabel());
        coursePlaceholder.setHint(educationVerificationLocale.getFieldCoursePlaceholder());
        courseDataError.setText(educationVerificationLocale.getFieldCourseDataerror());
        regLabel.setText(educationVerificationLocale.getFieldRegLabel());
        regPlaceholder.setHint(educationVerificationLocale.getFieldRegPlaceholder());
        regDataError.setText(educationVerificationLocale.getFieldRegDataerror());
        durationLabel.setText(educationVerificationLocale.getFieldDurationLabel());
        startMonthLabel.setText(educationVerificationLocale.getFieldStartMonthLabel());
        startYearLabel.setText(educationVerificationLocale.getFieldStartYearLabel());
        startYearPlaceholder.setHint(educationVerificationLocale.getFieldStartYearPlaceholder());
        startYearDataError.setText(educationVerificationLocale.getFieldStartYearDataerror());
        courseProgressLabel.setText(educationVerificationLocale.getFieldCourseProgressLabel());
        endMonthLabel.setText(educationVerificationLocale.getFieldEndMonthLabel());
        endYearLabel.setText(educationVerificationLocale.getFieldEndYearLabel());
        endYearPlaceholder.setHint(educationVerificationLocale.getFieldEndYearPlaceholder());
        endYearDataError.setText(educationVerificationLocale.getFieldEndYearDataerror());
        documentLabel.setText(educationVerificationLocale.getFieldDocumentsLabel());
        submitButtonTextView.setText(educationVerificationLocale.getButtonSubmit());

        submitLinearLayout.setOnClickListener(this);

        courseDataError.setVisibility(View.INVISIBLE);
        regDataError.setVisibility(View.INVISIBLE);
        startYearDataError.setVisibility(View.INVISIBLE);
        endYearDataError.setVisibility(View.INVISIBLE);
        documentDataError.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.document_spinner_items, monthsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startMonthSpinner.setAdapter(arrayAdapter);
        if (startMonth != null) {
            selectedStartMonth = monthMap.get(startMonth);
            startMonthSpinner.setSelection(arrayAdapter.getPosition(startMonth));
        } else {
            selectedStartMonth = monthMap.get(monthsList.get(0));
        }
        startMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStartMonth = monthMap.get(monthsList.get(position));
                String startYear = startYearPlaceholder.getText().toString().trim();
                String endYear = endYearPlaceholder.getText().toString().trim();
                if (!TextUtils.isEmpty(startYear) && !TextUtils.isEmpty(endYear)) {
                    if (Integer.parseInt(startYear) == Integer.parseInt(endYear)) {
                        if (Integer.parseInt(selectedStartMonth) == Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = false;
                            isEndYearValid = false;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                        } else if (Integer.parseInt(selectedStartMonth) > Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = false;
                            isEndYearValid = true;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        } else if (Integer.parseInt(selectedStartMonth) < Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = true;
                            isEndYearValid = true;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStartMonth = monthMap.get(monthsList.get(0));
            }
        });

        ArrayAdapter<String> endMonthArrayAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.document_spinner_items, monthsList);
        endMonthArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endMonthSpinner.setAdapter(endMonthArrayAdapter);
        if (endMonth != null) {
            selectedEndMonth = monthMap.get(endMonth);
            endMonthSpinner.setSelection(endMonthArrayAdapter.getPosition(endMonth));
        } else {
            selectedEndMonth = monthMap.get(monthsList.get(0));
        }
        endMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEndMonth = monthMap.get(monthsList.get(position));
                String startYear = startYearPlaceholder.getText().toString().trim();
                String endYear = endYearPlaceholder.getText().toString().trim();
                if (!TextUtils.isEmpty(startYear) && !TextUtils.isEmpty(endYear)) {
                    if (Integer.parseInt(startYear) == Integer.parseInt(endYear)) {
                        if (Integer.parseInt(selectedStartMonth) == Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = false;
                            isEndYearValid = false;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                        } else if (Integer.parseInt(selectedStartMonth) > Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = false;
                            isEndYearValid = true;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        } else if (Integer.parseInt(selectedStartMonth) < Integer.parseInt(selectedEndMonth)) {
                            isStartYearValid = true;
                            isEndYearValid = true;
                            startMonthSpinner.setBackgroundResource(0);
                            startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                            endMonthSpinner.setBackgroundResource(0);
                            endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEndMonth = monthMap.get(monthsList.get(0));
            }
        });

        try {
            educationLocaleKeyMap = CommonUtils.jsonToMap(CommonUtils.getGson().toJson(educationVerificationLocale));
        } catch (JSONException e) {
            HandleException.handleInternalException("Education locale json to map: " + e.toString());
        }

        try {
            if ("OTHER".equals(courseLevel) && course != null) {
                educationLevelLabel.setText(course);
            } else {
                String courseLevelKey = courseLevel + "_DOC";
                educationLevelLabel.setText(Objects.requireNonNull(educationLocaleKeyMap.get(courseLevelKey)).toString());
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        noRadioButton.setChecked(true);
        isCourseEnded = true;
        isCourseInProgress = 1;

        coursePlaceholder.addTextChangedListener(new InputTextValidator(coursePlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isCourseValid = UserInputValidation.isStringValid(s.toString());
                if (isCourseValid) {
                    courseDataError.setVisibility(View.INVISIBLE);
                    coursePlaceholder.setBackgroundResource(0);
                    coursePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitLinearLayout.setClickable(true);
                    submitProgressBar.setVisibility(View.GONE);
                    coursePlaceholder.setBackgroundResource(0);
                    coursePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    courseDataError.setVisibility(View.VISIBLE);
                }
            }
        });

        regPlaceholder.addTextChangedListener(new InputTextValidator(regPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isRegValid = UserInputValidation.isStringValid(s.toString());
                if (isRegValid) {
                    regDataError.setVisibility(View.INVISIBLE);
                    regPlaceholder.setBackgroundResource(0);
                    regPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                } else {
                    submitLinearLayout.setClickable(true);
                    submitProgressBar.setVisibility(View.GONE);
                    regPlaceholder.setBackgroundResource(0);
                    regPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    regDataError.setVisibility(View.VISIBLE);
                }
            }
        });

        startYearPlaceholder.addTextChangedListener(new InputTextValidator(startYearPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(endYearPlaceholder.getText().toString().trim())) {
                    String endYear = endYearPlaceholder.getText().toString().trim();
                    String startYear = s.toString();
                    if (startYear.length() == 4 && Integer.parseInt(startYear) <= currentYear) {
                        startMonthSpinner.setBackgroundResource(0);
                        startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        endMonthSpinner.setBackgroundResource(0);
                        endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        if (Integer.parseInt(startYear) == Integer.parseInt(endYear)) {
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            startYearDataError.setVisibility(View.INVISIBLE);
                            endYearDataError.setVisibility(View.INVISIBLE);
                            int startMonth = Integer.parseInt(selectedStartMonth);
                            int endMonth = Integer.parseInt(selectedEndMonth);
                            if (startMonth < endMonth) {
                                isStartYearValid = true;
                                isEndYearValid = true;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                            } else if (startMonth > endMonth) {
                                isStartYearValid = false;
                                isEndYearValid = true;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                            } else if (startMonth == endMonth) {
                                isStartYearValid = false;
                                isEndYearValid = false;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            }
                        } else if (Integer.parseInt(startYear) < Integer.parseInt(endYear)) {
                            isStartYearValid = true;
                            isEndYearValid = true;
                            startYearDataError.setVisibility(View.INVISIBLE);
                            endYearDataError.setVisibility(View.INVISIBLE);
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        } else if (Integer.parseInt(startYear) > Integer.parseInt(endYear)) {
                            isStartYearValid = false;
                            isEndYearValid = true;
                            startYearDataError.setVisibility(View.VISIBLE);
                            endYearDataError.setVisibility(View.INVISIBLE);
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        }
                    } else {
                        isStartYearValid = false;
                        startYearPlaceholder.setBackgroundResource(0);
                        startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        startYearDataError.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (UserInputValidation.isStartYearValid(s.toString())) {
                        isStartYearValid = true;
                        startYearDataError.setVisibility(View.INVISIBLE);
                        startYearPlaceholder.setBackgroundResource(0);
                        startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    } else {
                        isStartYearValid = false;
                        submitLinearLayout.setClickable(true);
                        submitProgressBar.setVisibility(View.GONE);
                        startYearPlaceholder.setBackgroundResource(0);
                        startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        startYearDataError.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        endYearPlaceholder.addTextChangedListener(new InputTextValidator(endYearPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(startYearPlaceholder.getText().toString().trim())) {
                    String startYear = startYearPlaceholder.getText().toString().trim();
                    String endYear = s.toString();
                    if (endYear.length() == 4 && Integer.parseInt(endYear) <= currentYear) {
                        startMonthSpinner.setBackgroundResource(0);
                        startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        endMonthSpinner.setBackgroundResource(0);
                        endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                        if (Integer.parseInt(endYear) == Integer.parseInt(startYear)) {
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            startYearDataError.setVisibility(View.INVISIBLE);
                            endYearDataError.setVisibility(View.INVISIBLE);
                            int startMonth = Integer.parseInt(selectedStartMonth);
                            int endMonth = Integer.parseInt(selectedEndMonth);
                            if (endMonth < startMonth) {
                                isStartYearValid = true;
                                isEndYearValid = false;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            } else if (endMonth > startMonth) {
                                isStartYearValid = true;
                                isEndYearValid = true;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_background);
                            } else if (endMonth == startMonth) {
                                isStartYearValid = false;
                                isEndYearValid = false;
                                startMonthSpinner.setBackgroundResource(0);
                                startMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                                endMonthSpinner.setBackgroundResource(0);
                                endMonthSpinner.setBackgroundResource(R.drawable.edit_text_error_background);
                            }
                        } else if (Integer.parseInt(endYear) < Integer.parseInt(startYear)) {
                            isStartYearValid = true;
                            isEndYearValid = false;
                            startYearDataError.setVisibility(View.INVISIBLE);
                            endYearDataError.setVisibility(View.VISIBLE);
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        } else if (Integer.parseInt(endYear) > Integer.parseInt(startYear)) {
                            isStartYearValid = true;
                            isEndYearValid = true;
                            startYearDataError.setVisibility(View.INVISIBLE);
                            endYearDataError.setVisibility(View.INVISIBLE);
                            startYearPlaceholder.setBackgroundResource(0);
                            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            endYearPlaceholder.setBackgroundResource(0);
                            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        }
                    } else {
                        isEndYearValid = false;
                        endYearDataError.setVisibility(View.VISIBLE);
                        endYearPlaceholder.setBackgroundResource(0);
                        endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                } else {
                    if (UserInputValidation.isYearValid(s.toString())) {
                        isEndYearValid = true;
                        endYearDataError.setVisibility(View.INVISIBLE);
                        endYearPlaceholder.setBackgroundResource(0);
                        endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    } else {
                        isEndYearValid = false;
                        submitLinearLayout.setClickable(true);
                        submitProgressBar.setVisibility(View.GONE);
                        endYearPlaceholder.setBackgroundResource(0);
                        endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        endYearDataError.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void startCameraActivity() {
        GlobalVariables.isCameraSelected = true;
        GlobalVariables.isStorageSelected = false;
        Intent intent = new Intent(mActivity, CameraActivity.class);
        intent.putExtra("CAMERA_FACING", GlobalVariables.CAMERA_FACING_BACK);
        intent.putExtra("capture_type", GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE);
        intent.putExtra("current_stage", this.getClass().getSimpleName());
        cameraActivityResultLauncher.launch(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == submitLinearLayout) {
            isCourseValid = UserInputValidation.isStringValid(coursePlaceholder.getText().toString().trim());
            isRegValid = UserInputValidation.isStringValid(regPlaceholder.getText().toString().trim());
            if (isCourseEnded) {
                if (isStartYearValid && isEndYearValid && isCourseValid && isRegValid && !documentMediaIDList.isEmpty()) {
                    String startYear = startYearPlaceholder.getText().toString().trim();
                    String endYear = endYearPlaceholder.getText().toString().trim();
                    if (Integer.parseInt(startYear) > Integer.parseInt(endYear)) {
                        startYearDataError.setVisibility(View.VISIBLE);
                        isStartYearValid = false;
                    } else {
                        isStartYearValid = true;
                        isEndYearValid = true;
                        startYearDataError.setVisibility(View.INVISIBLE);
                        endYearDataError.setVisibility(View.INVISIBLE);
                        submitLinearLayout.setClickable(false);
                        CommonUtils.setButtonAlphaLow(submitLinearLayout);
                        submitProgressBar.setVisibility(View.VISIBLE);
                        inputsValid();
                    }
                } else {
                    inputsInvalid();
                }
            } else {
                if (isCourseValid && isRegValid && isStartYearValid && !documentMediaIDList.isEmpty()) {
                    submitLinearLayout.setClickable(false);
                    CommonUtils.setButtonAlphaLow(submitLinearLayout);
                    submitProgressBar.setVisibility(View.VISIBLE);
                    inputsValid();
                } else {
                    inputsInvalid();
                }
            }
        }
    }

    private void sendMediaHandshakeRequest() {
        CommonUtils.sendRequest(AttestrRequest.actionDocumentMediaHandshake(false));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int buttonID = radioGroup.getCheckedRadioButtonId();
        radioButton = radioGroup.findViewById(buttonID);
        isCourseInProgress = radioGroup.indexOfChild(radioButton);
        if (isCourseInProgress == 0) {
            isCourseEnded = false;
            courseEndLinearLayout.setVisibility(View.GONE);
        } else {
            isCourseEnded = true;
            courseEndLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void inputsValid() {
        documentDataError.setVisibility(View.INVISIBLE);
        coursePlaceholder.setBackgroundResource(0);
        coursePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        regPlaceholder.setBackgroundResource(0);
        regPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        startYearPlaceholder.setBackgroundResource(0);
        startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        endYearPlaceholder.setBackgroundResource(0);
        endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        courseDataError.setVisibility(View.INVISIBLE);
        regDataError.setVisibility(View.INVISIBLE);
        startYearDataError.setVisibility(View.INVISIBLE);
        endYearDataError.setVisibility(View.INVISIBLE);
        documentDataError.setVisibility(View.INVISIBLE);

        documentsArray = new JSONArray(documentMediaIDList);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("level", courseLevel);
        requestMap.put("course", course);
        requestMap.put("courseName", coursePlaceholder.getText().toString().trim());
        requestMap.put("reg", regPlaceholder.getText().toString().trim());
        requestMap.put("startMonth", selectedStartMonth);
        requestMap.put("startYear", startYearPlaceholder.getText().toString().trim());
        requestMap.put("ended", String.valueOf(isCourseEnded));
        if (isCourseEnded) {
            requestMap.put("endedMonth", selectedEndMonth);
            requestMap.put("endedYear", endYearPlaceholder.getText().toString().trim());
        } else {
            requestMap.put("endedMonth", null);
            requestMap.put("endedYear", null);
        }
        CommonUtils.sendRequest(AttestrRequest.actionEducationSubmit(requestMap, documentsArray));
    }

    private void inputsInvalid() {
        if (documentMediaIDList.isEmpty()) {
            documentDataError.setVisibility(View.VISIBLE);
        }
        if (!isCourseValid) {
            courseDataError.setVisibility(View.VISIBLE);
            coursePlaceholder.setBackgroundResource(0);
            coursePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        if (!isRegValid) {
            regDataError.setVisibility(View.VISIBLE);
            regPlaceholder.setBackgroundResource(0);
            regPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        if (!isStartYearValid) {
            startYearDataError.setVisibility(View.VISIBLE);
            startYearPlaceholder.setBackgroundResource(0);
            startYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        if (!isEndYearValid) {
            endYearDataError.setVisibility(View.VISIBLE);
            endYearPlaceholder.setBackgroundResource(0);
            endYearPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        }
    }

    @Override
    public void onDocumentUploaded(String mediaID, String documentName, String meta) {
        try {
            fieldDocumentDataError.setVisibility(View.GONE);
            updateRecyclerView(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Work verification onDocument uploaded: " + e);
        }
    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {

    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {

    }

    public void hideBottomSheet(MotionEvent event) {
        if (bottomSheet.isEnabled()) {
            Rect outRect = new Rect();
            bottomSheet.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onImageSizeExceeded(String meta) {

    }

    @Override
    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null) {
            sendMediaHandshakeRequest();
        }
    }

    @Override
    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    @Override
    public void onDocumentSelected(byte[] selectedFileByteArray) {
        if (selectedFileByteArray != null) {
            sendMediaHandshakeRequest();
        }
    }

    private void selectDocument() {
        GlobalVariables.isCameraSelected = false;
        GlobalVariables.isStorageSelected = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpg", "image/jpeg", "application/pdf"});
        Intent selectXmlFileIntent = Intent.createChooser(intent, "Select Document");
        storageResultLauncher.launch(selectXmlFileIntent);
    }

    @Override
    public void selectImage(String type) {
        nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectedImageType = type;
    }

    @Override
    public void removeImageAndMediaId(int position, String type) {
        try {
            imagesArrayList.remove(position);
            documentMediaIDList.remove(position);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, selectedImageType, this, 10);
            mActivity.runOnUiThread(() -> educationVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            HandleException.handleInternalException("removeImageAndMediaId Exception: " + e);
        }
    }

    private void updateRecyclerView(String mediaID) {
        Bitmap selectedImageBitmap = null;
        if (GlobalVariables.isCameraSelected) {
            selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(capturedImageByteArray, 500);
        } else if (GlobalVariables.isStorageSelected) {
            if (GlobalVariables.selectedMediaMimeType.equals("pdf")) {
                selectedImageBitmap = CommonUtils.getPdfBitmap(selectedDocumentFile);
            } else if (GlobalVariables.selectedMediaMimeType.equals("jpg") || GlobalVariables.selectedMediaMimeType.equals("png")) {
                selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(selectedFileByteArray, 500);
            }
        }
        try {
            MediaDataClass entranceImagesDataClass = new MediaDataClass();
            entranceImagesDataClass.setSelectedImageBitmap(selectedImageBitmap);
            entranceImagesDataClass.setmTag("SELECTED_IMAGE");
            imagesArrayList.add(entranceImagesDataClass);
            Collections.swap(imagesArrayList, imagesArrayList.size() - 2, imagesArrayList.size() - 1);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, selectedImageType, this, 10);
            mActivity.runOnUiThread(() -> educationVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
            documentMediaIDList.add(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Digital Address exception: " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBottomSheetExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

}
