package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.QueryFragmentBinding;
import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.listener.ImageSizeExceededListener;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.listener.MediaImagesListener;
import com.attestr.flowx.listener.PermissionsListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.MediaDataClass;
import com.attestr.flowx.model.MediaRecyclerViewAdapter;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.QueryLocale;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 06/12/21
 **/
public class QueryFragment extends Fragment implements View.OnClickListener,
        MediaHandshakeListener, PermissionsListener, ImageSizeExceededListener, MediaImagesListener {

    private QueryLocale queryLocale;
    private QueryFragmentBinding queryFragmentBinding;
    private HandshakeReadyData handshakeReadyResponse;
    private TextView queryLabel, queryText, fieldResponseLabel,
            fieldResponseDataError, fieldDocumentLabel, querySubmitButtonTextView, fieldDocumentDataError;
    private EditText fieldResponsePlaceholder;
    private LinearLayout submitQueryRelativeLayout;
    private ProgressBar submitProgressbar;
    private boolean isQueryEntered;
    private Activity mActivity;
    private static QueryFragment queryFragment;
    private JSONArray documentsArray;
    private int marginBottom = 10;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout.LayoutParams imageViewLayoutParams;
    private List<String> documentMediaIDList;
    private PermissionsHandler permissionsHandler;
    private LinearLayout backgroundLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout coordinatorLayout;
    private View bottomSheet;
    private boolean isBottomSheetExpanded = false;
    private CommonLocale commonLocale;
    private EventListener eventListener;
    private NestedScrollView nestedScrollView;
    private LayoutInflater layoutInflater;
    private ActivityResultLauncher<Intent> storageResultLauncher;
    private RecyclerView queryRecyclerView;
    private MediaRecyclerViewAdapter mediaRecyclerViewAdapter;
    private ArrayList<MediaDataClass> imagesArrayList;
    private byte[] selectedFileByteArray;
    private byte[] capturedImageByteArray;
    private File selectedDocumentFile;
    private ImageView documentLabelImageView;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private TextView filePickerCameraTextView, filePickerGalleryTextView, filePickerChooseFileTextView;

    public static QueryFragment getInstance(boolean newInstance) {
        if (newInstance){
            queryFragment = new QueryFragment();
            return queryFragment;
        } else {
            if (queryFragment == null){
                queryFragment = new QueryFragment();
            }
        }
        return queryFragment;
    }

    public void setData(Activity activity,
                        boolean isTablet){
        this.mActivity = activity;
        eventListener = (EventListener) activity;
        if (CommonUtils.isTablet(activity)){
            marginBottom = 40;
        } else {
            marginBottom = 25;
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        queryFragmentBinding = QueryFragmentBinding.inflate(inflater, container, false);
        return queryFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        documentMediaIDList = new ArrayList<>(10);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0, marginBottom);

        handshakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        layoutInflater = LayoutInflater.from(mActivity);
        queryLocale = GlobalVariables.handshakeReadyResponse.getLocale().getQueryLocale();
        queryLabel = queryFragmentBinding.queryLabel;
        queryText = queryFragmentBinding.queryText;
        fieldResponseLabel = queryFragmentBinding.fieldResponseLabel;
        fieldResponseDataError = queryFragmentBinding.fieldResponseDataError;
        fieldDocumentLabel = queryFragmentBinding.fieldDocumentLabel;
        querySubmitButtonTextView = queryFragmentBinding.querySubmitButtonTextView;
        fieldResponsePlaceholder = queryFragmentBinding.fieldResponsePlaceholder;
        submitQueryRelativeLayout = queryFragmentBinding.querySubmitRelativeLayout;
        submitProgressbar = queryFragmentBinding.querySubmitProgressBar;
        nestedScrollView = queryFragmentBinding.queryNestedScrollView;
        fieldDocumentDataError = queryFragmentBinding.fieldResponseDataError;
        queryRecyclerView = queryFragmentBinding.queryRecyclerView;
        nestedScrollView.smoothScrollTo(0,0);
        CommonUtils.setButtonAlphaHigh(submitQueryRelativeLayout);
        submitProgressbar.setVisibility(View.GONE);
        submitQueryRelativeLayout.setClickable(true);
        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        backgroundLinearLayout = queryFragmentBinding.queryBackgroundLinearLayout;
        coordinatorLayout = queryFragmentBinding.queryCoordinatorLayout;
        bottomSheet = coordinatorLayout.findViewById(R.id.query_bottom_sheet_linear_layout);

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
            permissionsHandler.setData(mActivity,  QueryFragment.this);
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
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    isBottomSheetExpanded = true;
                    backgroundLinearLayout.setAlpha(0.5f);
                    eventListener.changeFooterLayoutVisibility(false);
                    try {
                        View openCameraImageView = bottomSheet.findViewById(R.id.open_camera_image_view);
                        View openGalleryImageView = bottomSheet.findViewById(R.id.open_gallery_image_view);
                        openCameraImageView.setOnClickListener(v -> startCameraActivity());
                        openGalleryImageView.setOnClickListener(v -> selectDocument());
                    } catch (Exception e) {
                        HandleException.handleInternalException("Bottom Sheet Exception: " + e.toString());
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
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

        queryLabel.setText(queryLocale.getLabelText());
        queryText.setText(GlobalVariables.queryText);
        fieldResponseLabel.setText(queryLocale.getFieldResponseLabel());
        fieldResponseDataError.setText(queryLocale.getFieldResponseDataerror());
        fieldDocumentLabel.setText(queryLocale.getFieldDocumentsLabel());
        querySubmitButtonTextView.setText(queryLocale.getButtonSubmit());
        fieldResponseDataError.setText(queryLocale.getFieldResponseDataerror());
        fieldResponsePlaceholder.setHint(queryLocale.getFieldResponsePlaceholder());

        submitQueryRelativeLayout.setOnClickListener(this);

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
            queryRecyclerView.setAdapter(mediaRecyclerViewAdapter);
            queryRecyclerView.setLayoutManager(gridLayoutManager);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        fieldResponsePlaceholder.addTextChangedListener(new InputTextValidator(fieldResponsePlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isQueryEntered = UserInputValidation.isStringValid(s.toString());
                if (isQueryEntered){
                    queryValid(false, null);
                } else {
                    queryInvalid();
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

    private void selectDocument() {
        GlobalVariables.isCameraSelected = false;
        GlobalVariables.isStorageSelected = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpg","image/jpeg", "application/pdf"});
        Intent selectXmlFileIntent = Intent.createChooser(intent, "Select Document");
        storageResultLauncher.launch(selectXmlFileIntent);
    }

    @Override
    public void onClick(View v) {
        if (v == submitQueryRelativeLayout){
            if (isQueryEntered){
                queryValid(true, fieldResponsePlaceholder.getText().toString().trim());
            } else {
                queryInvalid();
            }
        }
    }

    private void queryValid(boolean sendRequest, String response){
        fieldResponsePlaceholder.setBackgroundResource(0);
        fieldResponsePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        fieldResponseDataError.setVisibility(View.INVISIBLE);
        if (sendRequest) {
            if (documentMediaIDList.size() != 0){
                documentsArray = new JSONArray(documentMediaIDList);
            } else {
                documentsArray = null;
            }
            submitProgressbar.setVisibility(View.VISIBLE);
            CommonUtils.setButtonAlphaLow(submitQueryRelativeLayout);
            submitQueryRelativeLayout.setClickable(false);
            CommonUtils.sendRequest(AttestrRequest.actionQuerySubmit(response, documentsArray == null ? null : documentsArray));
        }
    }

    private void queryInvalid(){
        fieldResponsePlaceholder.setBackgroundResource(0);
        fieldResponsePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        fieldResponseDataError.setVisibility(View.VISIBLE);
        submitProgressbar.setVisibility(View.GONE);
        submitQueryRelativeLayout.setClickable(true);
    }

    @Override
    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null){
            sendMediaHandshakeRequest();
        }
    }

    @Override
    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    @Override
    public  void onDocumentSelected(byte[] selectedFileByteArray){
        if (selectedFileByteArray != null){
            sendMediaHandshakeRequest();
        }
    }

    private void sendMediaHandshakeRequest() {
        CommonUtils.sendRequest(AttestrRequest.actionDocumentMediaHandshake(true));
    }

    @Override
    public void onDocumentUploaded(String mediaID, String documentName, String meta) {
        try {
            updateRecyclerView(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Work verification onDocument uploaded: "+ e);
        }
    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {

    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {

    }

    public void hideBottomSheet(MotionEvent event){
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
    public void selectImage(String type) {
        nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void updateRecyclerView(String mediaID) {
        fieldDocumentDataError.setVisibility(View.GONE);
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
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, null, this, 10);
            mActivity.runOnUiThread(() -> queryRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
            documentMediaIDList.add(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Digital Address exception: " + e);
        }
    }

    @Override
    public void removeImageAndMediaId(int position, String type) {
        try {
            imagesArrayList.remove(position);
            documentMediaIDList.remove(position);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList,  null, this, 10);
            mActivity.runOnUiThread(() -> queryRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            HandleException.handleInternalException("removeImageAndMediaId Exception: " + e);
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
