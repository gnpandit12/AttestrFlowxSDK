package com.attestr.flowx.view.fragments.digitalAddressCheck;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.DigitalAddressCheckFragmentTwoBinding;
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
import com.attestr.flowx.model.response.locale.DigitalAddressLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
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
public class DigitalAddressCheckFragmentTwo extends Fragment
        implements View.OnClickListener, ImageSizeExceededListener,
        PermissionsListener, MediaHandshakeListener, MediaImagesListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private DigitalAddressLocale digitalAddressLocale;
    private List<String> docOptionsList;
    private DigitalAddressCheckFragmentTwoBinding fragmentTwoBinding;
    private LinearLayout backgroundLinearLayout;
    private EventListener eventListener;
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout coordinatorLayout;
    private static DigitalAddressCheckFragmentTwo digitalAddressCheckFragmentTwo;
    private Activity mActivity;
    private ArrayList<String> documentsList = new ArrayList<>();
    private Spinner documentListSpinner;
    private String selectedDocument;
    private boolean isFrontDocumentSelected =false;
    private boolean isBackDocumentSelected = false;
    private Map<String, String> mDataMap;
    private LinearLayout documentFront, documentBack;
    private TextView stageTitleTextView, documentListLabel, frontDocumentTextView,
            backDocumentTextView, submitButtonTextView, houseExteriorTextView, buildingExteriorTextView, documentDataErrorTextView;
    private ImageView frontDocumentImageView, backDocumentImageView, houseExteriorImageView, buildingExteriorImageView;
    private LinearLayout proceedLinearLayout;
    private ProgressBar submitProgressBar;
    private View bottomSheet;
    private Map<String, String> docOptionsMap;
    private Map<String, Object> digitalAddressLocaleMap;
    private String docType, frontDoc, backDoc, extOne, extTwo;
    private boolean isBottomSheetExpanded = false;
    private boolean ipGeolocationEnabled = false;
    private boolean videoEnabled = false;
    private boolean mapEnabled = false;
    private NestedScrollView nestedScrollView;
    private ImageView spinnerLabelIcon, frontDocLabelIcon, backDocLabelIcon;
    private ActivityResultLauncher<Intent> storageResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private boolean isFromOnCreate = false;
    private ArrayList<MediaDataClass> entranceImagesDataList, buildingImagesDataList;
    private String selectedImageType = null;
    private int entranceImageUploadCount = -1;
    private int buildingImageUploadCount = -1;
    private byte[] selectedFileByteArray;
    private ArrayList<String> entranceImagesMediaIDList, buildingImagesMediaIDList;
    private byte[] capturedImageByteArray;
    private Bitmap selectedImageBitmap;
    private MediaRecyclerViewAdapter entranceMediaRecyclerViewAdapter;
    private MediaRecyclerViewAdapter buildingMediaRecyclerViewAdapter;
    private RecyclerView entranceRecyclerView, buildingRecyclerView;
    private PermissionsHandler permissionsHandler;
    private TextView filePickerCameraTextView, filePickerGalleryTextView, filePickerChooseFileTextView;
    private CommonLocale commonLocale;

    public static DigitalAddressCheckFragmentTwo getInstance(boolean newInstance) {
        if (newInstance) {
            digitalAddressCheckFragmentTwo = new DigitalAddressCheckFragmentTwo();
            return digitalAddressCheckFragmentTwo;
        } else {
            if (digitalAddressCheckFragmentTwo == null) {
                digitalAddressCheckFragmentTwo = new DigitalAddressCheckFragmentTwo();
            }
        }
        return digitalAddressCheckFragmentTwo;
    }

    public void setData(Activity activity, Map<String, String> dataMap) {
        this.mActivity = activity;
        this.eventListener = (EventListener) activity;
        this.mDataMap = dataMap;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTwoBinding = DigitalAddressCheckFragmentTwoBinding.inflate(inflater, container, false);
        return fragmentTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isFromOnCreate = true;

        digitalAddressLocale = GlobalVariables.handshakeReadyResponse.getLocale().getDigitalAddressLocale();
        docOptionsList = new ArrayList<>();
        docOptionsMap = new HashMap<>();
        documentListSpinner = fragmentTwoBinding.digitalAddCheckDocumentSpinner;

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        entranceImagesMediaIDList = new ArrayList<>(6);
        buildingImagesMediaIDList = new ArrayList<>(6);

        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        docType = stageReadyResponse.getData().getState().get("docType");
        frontDoc = stageReadyResponse.getData().getState().get("frontDoc");
        backDoc = stageReadyResponse.getData().getState().get("backDoc");
        extOne = stageReadyResponse.getData().getState().get("extOne");
        extTwo = stageReadyResponse.getData().getState().get("extTwo");
        ipGeolocationEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled");
        mapEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("mapEnabled");
        videoEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("videoEnabled");

        entranceRecyclerView = fragmentTwoBinding.digitalAddressCheckEntranceRecyclerView;
        buildingRecyclerView = fragmentTwoBinding.digitalAddressCheckBuildingRecyclerView;

        if (frontDoc != null) {
            GlobalVariables.digitalAddressFrontDocSource = frontDoc;
        }
        if (backDoc != null) {
            GlobalVariables.digitalAddressBackDocSource = backDoc;
        }
        if (extOne != null) {
            GlobalVariables.digitalAddressExtOneSource = extOne;
        }
        if (extTwo != null) {
            GlobalVariables.digitalAddressExtTwoSource = extTwo;
        }

        try {
            digitalAddressLocaleMap = CommonUtils.jsonToMap(CommonUtils.getGson().toJson(digitalAddressLocale));
        } catch (JSONException e) {
            HandleException.handleInternalException("Digital address locale json to map: " + e);
        }

        docOptionsList = (ArrayList<String>) stageReadyResponse.getData().getMetadata().get("docOptions");
        ipGeolocationEnabled = (boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled");

        documentFront = fragmentTwoBinding.digitalAddCheckFrontDocLinearLayout;
        documentBack = fragmentTwoBinding.digitalAddCheckBackDocLinearLayout;
        submitProgressBar = fragmentTwoBinding.digitalAddCheckTwoSubmitProgressBar;
        submitButtonTextView = fragmentTwoBinding.digitalAddCheckTwoSubmitButtonTextView;
        proceedLinearLayout = fragmentTwoBinding.digitalAddCheckTwoSubmitRelativeLayout;
        frontDocumentImageView = fragmentTwoBinding.digitalAddCheckFrontDocImageView;
        backDocumentImageView = fragmentTwoBinding.digitalAddCheckBackDocImageView;
        houseExteriorImageView = fragmentTwoBinding.digitalAddressCheckHouseExteriorImageView;
        buildingExteriorImageView = fragmentTwoBinding.digitalAddressCheckBuildingExteriorImageView;
        houseExteriorTextView = fragmentTwoBinding.digitalAddCheckHouseExteriorLabel;
        buildingExteriorTextView = fragmentTwoBinding.digitalAddCheckBuildingExteriorLabel;
        documentListLabel = fragmentTwoBinding.digitalAddCheckDocLabel;
        frontDocumentTextView = fragmentTwoBinding.digitalAddCheckFrontDocTextView;
        backDocumentTextView = fragmentTwoBinding.digitalAddCheckBackDocTextView;
        coordinatorLayout = fragmentTwoBinding.coordinatorLayout;
        backgroundLinearLayout = fragmentTwoBinding.backgroundLinearLayout;
        nestedScrollView = fragmentTwoBinding.digitalAddressCheckTwoNestedScrollView;
        stageTitleTextView = fragmentTwoBinding.digitalAddressCheckVerificationTitle;
        documentDataErrorTextView = fragmentTwoBinding.documentDataErrorTextView;
        stageTitleTextView.setText(digitalAddressLocale.getTitle());

        documentDataErrorTextView.setText(digitalAddressLocale.getFieldDocPickerDataerror());
        documentDataErrorTextView.setVisibility(View.GONE);

        backgroundLinearLayout.setAlpha(1);

        Bitmap addIcon = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.add);
        MediaDataClass mediaDataClass = new MediaDataClass();
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
            entranceImagesDataList = new ArrayList<>(6);
            mediaDataClass.setmTag("ADD_IMAGE");
            mediaDataClass.setSelectedImageBitmap(addIcon);
            entranceImagesDataList.add(mediaDataClass);
            entranceMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, entranceImagesDataList, GlobalVariables.ENTRANCE_RECYCLER_VIEW, this, 6);
            entranceRecyclerView.setAdapter(entranceMediaRecyclerViewAdapter);
            entranceRecyclerView.setLayoutManager(gridLayoutManager);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        try {
            MediaDataClass mBuildingImagesDataClass = new MediaDataClass();
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 3);
            buildingImagesDataList = new ArrayList<>(6);
            mBuildingImagesDataClass.setmTag("ADD_IMAGE");
            mBuildingImagesDataClass.setSelectedImageBitmap(addIcon);
            buildingImagesDataList.add(mBuildingImagesDataClass);
            buildingMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, buildingImagesDataList, GlobalVariables.BUILDING_RECYCLER_VIEW, this, 6);
            buildingRecyclerView.setAdapter(buildingMediaRecyclerViewAdapter);
            buildingRecyclerView.setLayoutManager(mGridLayoutManager);
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }

        spinnerLabelIcon = fragmentTwoBinding.digitalAddressCheckSpinnerLabelIcon;
        frontDocLabelIcon = fragmentTwoBinding.digitalAddressCheckFrontDocLabelIcon;
        backDocLabelIcon = fragmentTwoBinding.digitalAddressCheckBackDocLabelIcon;

        spinnerLabelIcon.setBackgroundResource(R.drawable.id_card);
        spinnerLabelIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        frontDocLabelIcon.setBackgroundResource(R.drawable.id_card);
        frontDocLabelIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        backDocLabelIcon.setBackgroundResource(R.drawable.id_card);
        backDocLabelIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));

        documentFront.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
        documentBack.setBackgroundResource(R.drawable.dotted_rectangle_boarder);

        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet_linear_layout);

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
            permissionsHandler.setData(mActivity,  DigitalAddressCheckFragmentTwo.this);
            permissionsHandler.requestPermissions();
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
                                onDocumentSelected((byte[]) null);
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            ProgressIndicator.setProgressBarInvisible();
                            onDocumentSelected((byte[]) null);
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
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        isBottomSheetExpanded = true;
                        documentListSpinner.setEnabled(false);
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
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        isBottomSheetExpanded = false;
                        documentListSpinner.setEnabled(true);
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

        eventListener.changeFooterLayoutVisibility(true);
        submitProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        documentListLabel.setText(digitalAddressLocale.getFieldDocLabel());
        frontDocumentTextView.setText(digitalAddressLocale.getFieldFrontDocPlaceholder());
        backDocumentTextView.setText(digitalAddressLocale.getFieldBackDocPlaceholder());
        submitButtonTextView.setText(digitalAddressLocale.getButtonProceed());
        houseExteriorTextView.setText(digitalAddressLocale.getHouseExteriorLabel());
        buildingExteriorTextView.setText(digitalAddressLocale.getBuildingExteriorLabel());

        for (int i = 0; i < docOptionsList.size(); i++) {
            String key = docOptionsList.get(i) + "_DOC_LABEL";
            String documentOption = docOptionsList.get(i);
            documentsList.add(Objects.requireNonNull(digitalAddressLocaleMap.get(key)).toString());
            docOptionsMap.put(Objects.requireNonNull(digitalAddressLocaleMap.get(key)).toString(), documentOption);
        }

        ArrayAdapter<String> documentSpinnerAdapter = new ArrayAdapter<>(mActivity,
                R.layout.document_spinner_items, documentsList);
        documentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentListSpinner.setAdapter(documentSpinnerAdapter);
        if (docType != null) {
            try {
                selectedDocument = docType;
                String selection = Objects.requireNonNull(digitalAddressLocaleMap.get(docType + "_DOC_LABEL")).toString();
                documentListSpinner.setSelection(documentSpinnerAdapter.getPosition(selection));
                documentDataErrorTextView.setVisibility(View.GONE);
            } catch (Exception e) {
                HandleException.handleInternalException("Digital address  document type exception: " + e);
            }
        }

//        else {
//            selectedDocument = docOptionsMap.get(documentsList.get(0));
//        }

        documentListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDocument = docOptionsMap.get(documentsList.get(position));
                documentDataErrorTextView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDocument = docOptionsMap.get(documentsList.get(0));
                documentDataErrorTextView.setVisibility(View.GONE);
            }
        });

        documentFront.setOnClickListener(this);
        documentBack.setOnClickListener(this);
        proceedLinearLayout.setOnClickListener(this);

        frontDocumentImageView.setBackgroundResource(0);
        frontDocumentImageView.setBackgroundResource(R.drawable.image);
        frontDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        backDocumentImageView.setBackgroundResource(0);
        backDocumentImageView.setBackgroundResource(R.drawable.image);
        backDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        houseExteriorImageView.setBackgroundResource(0);
        houseExteriorImageView.setBackgroundResource(R.drawable.image);
        houseExteriorImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        buildingExteriorImageView.setBackgroundResource(0);
        buildingExteriorImageView.setBackgroundResource(R.drawable.image);
        buildingExteriorImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));

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
    public void onClick(View v) {
        if (v == documentFront) {
            if (isBottomSheetExpanded) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                selectedImageType = null;
                nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
                eventListener.changeFooterLayoutVisibility(false);
                isFrontDocumentSelected = true;
                isBackDocumentSelected = false;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (v == documentBack) {
            if (isBottomSheetExpanded) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                selectedImageType = null;
                nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
                eventListener.changeFooterLayoutVisibility(false);
                isFrontDocumentSelected = false;
                isBackDocumentSelected = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (v == proceedLinearLayout) {
            CommonUtils.setButtonAlphaLow(proceedLinearLayout);
            submitProgressBar.setVisibility(View.VISIBLE);
            proceed();
        }

    }

    private boolean isMediaIdsValid() {
        return UserInputValidation.isDigitalAddressFrontDocValid()
                && UserInputValidation.isDigitalAddressBackDocValid()
                && isMediaIDValid(entranceImagesMediaIDList)
                && isMediaIDValid(buildingImagesMediaIDList);
    }

    private boolean isMediaIDValid(ArrayList<String> mediaIDList) {
        if (mediaIDList.size() != 0) {
            for (String mediaID : mediaIDList) {
                return UserInputValidation.isDigitalAddressMediaIDValid(mediaID);
            }
        }
        return false;
    }



    private void proceed() {
        if (isMediaIdsValid() && selectedDocument != null) {
            documentDataErrorTextView.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaLow(proceedLinearLayout);
            proceedLinearLayout.setClickable(false);
            submitProgressBar.setVisibility(View.VISIBLE);
            mDataMap.put("docType", selectedDocument);
            JSONArray entranceJsonArray = new JSONArray(entranceImagesMediaIDList);
            JSONArray buildingJsonArray = new JSONArray(buildingImagesMediaIDList);
            if (mapEnabled) {
                DigitalAddressCheckGoogleMapFragment digitalAddressCheckGoogleMapFragment = DigitalAddressCheckGoogleMapFragment.getInstance(true);
                ((LaunchActivity) mActivity).addFragment(digitalAddressCheckGoogleMapFragment);
                digitalAddressCheckGoogleMapFragment.setData(mActivity, mDataMap, entranceJsonArray, buildingJsonArray);
            } else {
                if (mDataMap != null && isUserDetectedLocationValid()) {
                    sendRequest(mDataMap);
                }
            }
        }
        if (!isMediaIdsValid()) {
            proceedLinearLayout.setClickable(true);
            CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
            submitProgressBar.setVisibility(View.GONE);
            if (!UserInputValidation.isDigitalAddressFrontDocValid()) {
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_red_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(R.drawable.image);
                frontDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
            }
            if (!UserInputValidation.isDigitalAddressBackDocValid()) {
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_red_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(R.drawable.image);
                backDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
            }
        }
        if (selectedDocument == null) {
            documentDataErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onImageSizeExceeded(String meta) {
        proceedLinearLayout.setClickable(true);
        CommonUtils.setButtonAlphaHigh(proceedLinearLayout);
        submitProgressBar.setVisibility(View.GONE);
        switch (meta) {
            case GlobalVariables.DIGITAL_ADDRESS_META_FRONT_DOC:
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_red_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundResource(R.drawable.image);
                frontDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
                break;
            case GlobalVariables.DIGITAL_ADDRESS_META_BACK_DOC:
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_red_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundResource(R.drawable.image);
                backDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
                break;
        }
    }

    public void hideBottomSheet(MotionEvent event) {
        if (bottomSheet.isEnabled()) {
            Rect outRect = new Rect();
            bottomSheet.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null) {
            if (isFrontDocumentSelected) {
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundTintList(null);
                frontDocumentImageView.setBackgroundResource(R.drawable.check);
                CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_FRONT_DOC));
            } else if (isBackDocumentSelected) {
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundTintList(null);
                backDocumentImageView.setBackgroundResource(R.drawable.check);
                CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_BACK_DOC));
            } else {
                if (selectedImageType != null) {
                    switch (selectedImageType) {
                        case GlobalVariables.ENTRANCE_RECYCLER_VIEW:
                            entranceImageUploadCount++;
                            CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_EXT_ONE + "-" + entranceImageUploadCount));
                            break;
                        case GlobalVariables.BUILDING_RECYCLER_VIEW:
                            buildingImageUploadCount++;
                            CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_EXT_TWO + "-" + buildingImageUploadCount));
                            break;
                    }
                }
            }
        } else {
            ProgressIndicator.setProgressBarInvisible();
            if (isFrontDocumentSelected) {
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundResource(R.drawable.image);
            } else if (isBackDocumentSelected) {
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundResource(R.drawable.image);
                backDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
            }
        }
    }

    @Override
    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    @Override
    public void onDocumentSelected(byte[] selectedFileByteArray) {
        if (selectedFileByteArray != null) {
            if (isFrontDocumentSelected) {
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundTintList(null);
                frontDocumentImageView.setBackgroundResource(R.drawable.check);
                CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_FRONT_DOC));
            } else if (isBackDocumentSelected) {
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundTintList(null);
                backDocumentImageView.setBackgroundResource(R.drawable.check);
                CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_BACK_DOC));
            } else {
                if (selectedImageType != null) {
                    switch (selectedImageType) {
                        case GlobalVariables.ENTRANCE_RECYCLER_VIEW:
                            entranceImageUploadCount++;
                            CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_EXT_ONE + "-" + entranceImageUploadCount));
                            break;
                        case GlobalVariables.BUILDING_RECYCLER_VIEW:
                            buildingImageUploadCount++;
                            CommonUtils.sendRequest(AttestrRequest.actionMediaHandshake(GlobalVariables.DIGITAL_ADDRESS_META_EXT_TWO + "-" + buildingImageUploadCount));
                            break;
                    }

                }
            }
        } else {
            ProgressIndicator.setProgressBarInvisible();
            if (isFrontDocumentSelected) {
                documentFront.setBackgroundResource(0);
                documentFront.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                frontDocumentImageView.setBackgroundResource(0);
                frontDocumentImageView.setBackgroundResource(R.drawable.image);
                frontDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
            } else if (isBackDocumentSelected) {
                documentBack.setBackgroundResource(0);
                documentBack.setBackgroundResource(R.drawable.dotted_rectangle_boarder);
                backDocumentImageView.setBackgroundResource(0);
                backDocumentImageView.setBackgroundResource(R.drawable.image);
                backDocumentImageView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
            }
        }
    }

    private boolean isUserDetectedLocationValid() {
        return !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLatitude)) &&
                !TextUtils.isEmpty(String.valueOf(GlobalVariables.detectedLongitude));
    }

    public void sendRequest(Map<String, String> mDataMap) {
        mDataMap.put("origLatitude", String.valueOf(GlobalVariables.detectedLatitude));
        mDataMap.put("origLongitude", String.valueOf(GlobalVariables.detectedLongitude));
        mDataMap.put("latitude",
                GlobalVariables.markerDragLatitude == 0.0 ?
                        String.valueOf(GlobalVariables.detectedLatitude) :
                        String.valueOf(GlobalVariables.markerDragLatitude));
        mDataMap.put("longitude",
                GlobalVariables.markerDragLongitude == 0.0 ?
                        String.valueOf(GlobalVariables.detectedLongitude) :
                        String.valueOf(GlobalVariables.markerDragLongitude));
        JSONArray entranceJsonArray = new JSONArray(entranceImagesMediaIDList);
        JSONArray buildingJsonArray = new JSONArray(buildingImagesMediaIDList);
        CommonUtils.sendRequest(AttestrRequest.actionDigitalAddressSubmit(mDataMap, entranceJsonArray, buildingJsonArray));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFromOnCreate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                CommonUtils.showLocationPermissionAlertDialog(mActivity);
            }
        }

        if (isBottomSheetExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isFromOnCreate = false;
    }

    @Override
    public void onDocumentUploaded(String metadata, String mediaID, String mimeType) {
        updateRecyclerView(mediaID);
    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {
        updateRecyclerView(mediaID);
    }


    private void updateRecyclerView(String mediaID) {
        if (isFrontDocumentSelected) {
            GlobalVariables.digitalAddressFrontDocSource = mediaID;
        } else if (isBackDocumentSelected) {
            GlobalVariables.digitalAddressBackDocSource = mediaID;
        }
        if (selectedImageType != null) {
            if (GlobalVariables.isCameraSelected) {
                if (capturedImageByteArray != null) {
                    selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(capturedImageByteArray, 500);
                }
            } else if (GlobalVariables.isStorageSelected) {
                selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(selectedFileByteArray, 500);
            }
            switch (selectedImageType) {
                case GlobalVariables.ENTRANCE_RECYCLER_VIEW:
                    try {
                        MediaDataClass entranceImagesDataClass = new MediaDataClass();
                        entranceImagesDataClass.setSelectedImageBitmap(selectedImageBitmap);
                        entranceImagesDataClass.setmTag("SELECTED_IMAGE");
                        entranceImagesDataList.add(entranceImagesDataClass);
                        Collections.swap(entranceImagesDataList, entranceImagesDataList.size() - 2, entranceImagesDataList.size() - 1);
                        entranceMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, entranceImagesDataList, selectedImageType, this, 6);
                        mActivity.runOnUiThread(() -> entranceRecyclerView.setAdapter(entranceMediaRecyclerViewAdapter));
                        entranceMediaRecyclerViewAdapter.notifyDataSetChanged();
                        entranceImagesMediaIDList.add(mediaID);
                    } catch (Exception e) {
                        HandleException.handleInternalException("Digital Address exception: " + e);
                    }
                    break;
                case GlobalVariables.BUILDING_RECYCLER_VIEW:
                    try {
                        MediaDataClass buildingImageDataClass = new MediaDataClass();
                        buildingImageDataClass.setSelectedImageBitmap(selectedImageBitmap);
                        buildingImageDataClass.setmTag("SELECTED_IMAGE");
                        buildingImagesDataList.add(buildingImageDataClass);
                        Collections.swap(buildingImagesDataList, buildingImagesDataList.size() - 2, buildingImagesDataList.size() - 1);
                        buildingMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, buildingImagesDataList, selectedImageType, this, 6);
                        mActivity.runOnUiThread(() -> buildingRecyclerView.setAdapter(buildingMediaRecyclerViewAdapter));
                        buildingMediaRecyclerViewAdapter.notifyDataSetChanged();
                        buildingImagesMediaIDList.add(mediaID);
                    } catch (Exception e) {
                        HandleException.handleInternalException("Digital Address exception: " + e);
                    }
                    break;
            }
        }
    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {

    }

    @Override
    public void selectImage(String type) {
        isFrontDocumentSelected = false;
        isBackDocumentSelected = false;
        nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectedImageType = type;
    }

    @Override
    public void removeImageAndMediaId(int position, String type) {
        switch (type) {
            case GlobalVariables.ENTRANCE_RECYCLER_VIEW:
                try {
                    entranceImagesDataList.remove(position);
                    entranceImagesMediaIDList.remove(position);
                    entranceMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, entranceImagesDataList, selectedImageType, this, 6);
                    mActivity.runOnUiThread(() -> entranceRecyclerView.setAdapter(entranceMediaRecyclerViewAdapter));
                    entranceMediaRecyclerViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    HandleException.handleInternalException("removeImageAndMediaId Digital Address Two: " + e);
                }
                break;
            case GlobalVariables.BUILDING_RECYCLER_VIEW:
                try {
                    buildingImagesDataList.remove(position);
                    buildingImagesMediaIDList.remove(position);
                    buildingMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, buildingImagesDataList, selectedImageType, this, 6);
                    mActivity.runOnUiThread(() -> buildingRecyclerView.setAdapter(buildingMediaRecyclerViewAdapter));
                    buildingMediaRecyclerViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    HandleException.handleInternalException("removeImageAndMediaId Digital Address Two: " + e);
                }
                break;
        }
    }
}
