package com.attestr.flowx.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.attestr.flowx.databinding.ActivityCameraBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.IPVLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.view.fragments.FaceMatchFragment;
import com.attestr.flowx.view.fragments.InPersonVerificationFragment;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 **/
public class CameraActivity extends AppCompatActivity
        implements View.OnClickListener, CameraAvailabilityListener {

    private static final String TAG = "CameraActivity";
    private CameraSelector cameraSelector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageView captureImageButton, retakeButton, uploadButton;
    private PreviewView mPreviewView;
    private Activity mActivity;
    private ImageCapture imageCapture;
    private Camera camera;
    private String mCameraFacing;
    private LinearLayout retakeSubmitLinearLayout;
    private Preview preview;
    private ProcessCameraProvider processCameraProvider;
    private ImageProxy imageProxy;
    private VideoCapture<Recorder> videoCapture;
    private String mCaptureType;
    private String mCurrentStage;
    private Recording recording;
    private long bytesRecorded = 0;
    private int count = 0;
    private Handler handler;
    private long startTimeStamp, endTimeStamp;
    private double minDuration, maxDuration;
    private ResponseData<StageInitData> stageReadyResponse;
    private Runnable runnable;
    private RelativeLayout recordVideoTimeRelativeLayout, startRecordingRelativeLayout;
    private TextView timerTextView;
    private HandshakeReadyData handShakeReadyResponse;
    private IPVLocale ipvLocale;
    private boolean voiceOTPEnabled, handwrittenOTPEnabled, geolocationEnabled;
    private TextView fieldAudioVideoLabelTextView, fieldOTPLabelTextView;
    private MediaStoreOutputOptions mediaStoreOutputOptions;
    private ContentValues contentValues;
    private RelativeLayout ipvLabelRelativeLayout;
    private CameraAvailabilityListener mCameraAvailabilityListener;
    private CommonLocale commonLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandleException.setCurrentActivity(CameraActivity.this);
        ActivityCameraBinding cameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(cameraBinding.getRoot());
        mActivity = this;
        mCameraAvailabilityListener = this;

        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        ipvLocale = handShakeReadyResponse.getLocale().getIpvLocale();
        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        captureImageButton = cameraBinding.captureSelfieImageView;
        retakeButton = cameraBinding.retakeSelfieImageView;
        uploadButton = cameraBinding.submitSelfieImageView;
        mPreviewView = cameraBinding.previewView;
        retakeSubmitLinearLayout = cameraBinding.retakeSubmitLinearLayout;
        recordVideoTimeRelativeLayout = cameraBinding.recordVideoTimerRelativeLayout;
        startRecordingRelativeLayout = cameraBinding.startRecordingRelativeLayout;
        ipvLabelRelativeLayout = cameraBinding.fieldLabelRelativelayout;
        timerTextView = cameraBinding.recordVideoTimerTextView;
        startRecordingRelativeLayout.setVisibility(View.GONE);
        recordVideoTimeRelativeLayout.setVisibility(View.GONE);
        captureImageButton.setVisibility(View.GONE);

        if (getIntent() != null) {
            mCameraFacing = getIntent().getStringExtra("CAMERA_FACING");
            mCaptureType = getIntent().getStringExtra("capture_type");
            mCurrentStage = getIntent().getStringExtra("current_stage");
            preview = new Preview.Builder()
                    .build();
            checkCamera(mCameraFacing);
        } else {
            onException("Camera get intent null");
        }

        if (GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO.equals(mCaptureType)) {
            ipvLabelRelativeLayout.setVisibility(View.VISIBLE);
            handler = new Handler();
            stageReadyResponse = GlobalVariables.stageReadyResponse;
            minDuration = (double) stageReadyResponse.getData().getMetadata().get("minDuration");
            maxDuration = (double) stageReadyResponse.getData().getMetadata().get("maxDuration");
            count = (int) maxDuration;
            startRecordingRelativeLayout.setVisibility(View.VISIBLE);
            captureImageButton.setVisibility(View.GONE);
            retakeSubmitLinearLayout.setVisibility(View.GONE);
            fieldAudioVideoLabelTextView = cameraBinding.fieldVideoAudioLabel;
            fieldOTPLabelTextView = cameraBinding.fieldOtpLabel;
            fieldOTPLabelTextView.setText(GlobalVariables.ipvOTP);

            voiceOTPEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled");
            handwrittenOTPEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("handwrittenOTPEnabled");
            geolocationEnabled = (boolean) stageReadyResponse.getData().getMetadata().get("geolocationEnabled");

            if (handwrittenOTPEnabled && voiceOTPEnabled) {
                fieldAudioVideoLabelTextView.setText(ipvLocale.getInstructionAudioVideo());
            } else if (handwrittenOTPEnabled) {
                fieldAudioVideoLabelTextView.setText(ipvLocale.getInstructionVideo());
            } else if (voiceOTPEnabled) {
                fieldAudioVideoLabelTextView.setText(ipvLocale.getInstructionAudio());
            } else {
                fieldAudioVideoLabelTextView.setText(ipvLocale.getInstructionAudio());
            }
        } else if (GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE.equals(mCaptureType)) {
            ipvLabelRelativeLayout.setVisibility(View.GONE);
            startRecordingRelativeLayout.setVisibility(View.GONE);
            captureImageButton.setVisibility(View.VISIBLE);
            retakeSubmitLinearLayout.setVisibility(View.GONE);
        }

        mPreviewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        captureImageButton.setOnClickListener(this);
        retakeButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        startRecordingRelativeLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == captureImageButton) {
            captureUserImage();
        } else if (view == startRecordingRelativeLayout) {
            startRecordingRelativeLayout.setVisibility(View.GONE);
            recordVideoTimeRelativeLayout.setVisibility(View.VISIBLE);
            String time = (int) maxDuration + " s";
            timerTextView.setText(time);
            startRecording();
        } else if (view == retakeButton) {
            startCamera();
            switch (mCaptureType) {
                case GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE:
                    retakeSubmitLinearLayout.setVisibility(View.GONE);
                    captureImageButton.setVisibility(View.VISIBLE);
                    break;
                case GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO:
                    count = (int) maxDuration;
                    if (bytesRecorded != 0) {
                        bytesRecorded = 0;
                    }
                    startRecordingRelativeLayout.setVisibility(View.VISIBLE);
                    recordVideoTimeRelativeLayout.setVisibility(View.GONE);
                    retakeSubmitLinearLayout.setVisibility(View.GONE);
                    captureImageButton.setVisibility(View.GONE);
                    break;
            }
        } else if (view == uploadButton) {
            upload();
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(mActivity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                onException(e.toString());
            }
        }, ContextCompat.getMainExecutor(mActivity));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        this.processCameraProvider = cameraProvider;

        if (GlobalVariables.CAMERA_FACING_FRONT.equals(mCameraFacing)) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
        } else if (GlobalVariables.CAMERA_FACING_BACK.equals(mCameraFacing)) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
        }

        mPreviewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        ImageCapture.Builder builder = new ImageCapture.Builder();
        processCameraProvider.unbindAll();
        switch (mCaptureType) {
            case GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE:
                imageCapture = builder
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                break;
            case GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO:
                imageCapture = builder
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                QualitySelector qualitySelector = QualitySelector.from(Quality.SD);
                Recorder recorder = new Recorder.Builder()
                        .setExecutor(ContextCompat.getMainExecutor(this))
                        .setQualitySelector(qualitySelector)
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);
                processCameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);
                break;
        }
    }


    private void startRecording() {

        try {

            captureImageButton.setVisibility(View.GONE);

            String videoName = "cam_" + getRandomString() + ".mp4";

            contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, videoName);

            mediaStoreOutputOptions = new MediaStoreOutputOptions.Builder(
                    this.getContentResolver(),
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .setContentValues(contentValues)
                    .build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                PendingRecording pendingRecording = videoCapture.getOutput()
                        .prepareRecording(this, mediaStoreOutputOptions)
                        .withAudioEnabled();

                startTimeStamp = System.currentTimeMillis();
                recording = pendingRecording.start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                    bytesRecorded = videoRecordEvent.getRecordingStats().getNumBytesRecorded();
                });

            } else {
                HandleException.handleCameraException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getCamError102());
            }

            runOnUiThread(() -> {
                runnable = () -> {
                    handler.postDelayed(runnable, 1000);
                    runOnUiThread(() -> {
                        String time = count + " s";
                        timerTextView.setText(time);
                    });
                    if (count == 0) {
                        try {
                            stopRecording();
                        } catch (Exception e) {
                            HandleException.handleInternalException("Stop recording exception: " + e);
                        }
                    }
                    count--;
                };
                handler.post(runnable);
            });
        } catch (Exception e) {
            HandleException.handleInternalException("Record video exception" + e);
        }

    }

    private void stopRecording() {
        try {
            handler.removeCallbacks(runnable);
            recording.stop();
            endTimeStamp = System.currentTimeMillis();
            captureUserImage();
        } catch (Exception e) {
            HandleException.handleInternalException("Stop recording exception: " + e);
        }
    }

    private String getFilePathFromContentUri(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result.replace(" (1)", "");
    }

    public byte[] convertVideoToBytes(String path) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                byteArrayOutputStream.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void captureUserImage() {
        try {
            if (videoCapture != null && processCameraProvider.isBound(videoCapture)) {
                processCameraProvider.unbind(videoCapture);
            }

            if (imageCapture != null) {
                processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                imageCapture.takePicture(ContextCompat.getMainExecutor(mActivity), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        processCameraProvider.unbind(preview);
                        imageProxy = image;
                        if (image.getFormat() == ImageFormat.JPEG) {
                            jpegImageToByteArray(image);
                        }
                        image.close();
                        super.onCaptureSuccess(image);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        onException(exception.toString());
                    }
                });
            }

        } catch (Exception e) {
            onException(e.toString());
        }
    }

    private void upload() {
        try {
            Intent intent = new Intent();
            switch (mCaptureType) {
                case GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE:
                    if (imageProxy != null) {
                        intent.putExtra("captureType", GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE);
                        intent.putExtra("cameraFacing", mCameraFacing);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
                case GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO:
                    File videoFile;
                    ContentResolver resolver = getContentResolver();
                    Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    Uri recordedVideoUri = resolver.insert(collection, contentValues);
                    String videoFilePath = getFilePathFromContentUri(recordedVideoUri);
                    GlobalVariables.recordedVideoBytes = convertVideoToBytes(videoFilePath);
                    videoFile = new File(videoFilePath);
                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                    intent.putExtra("captureType", GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO);
                    intent.putExtra("start_time_stamp", startTimeStamp);
                    intent.putExtra("end_time_stamp", endTimeStamp);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    private void jpegImageToByteArray(ImageProxy image) {
        try {
            image.getFormat();
            ImageProxy.PlaneProxy[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] data = new byte[buffer.capacity()];
            buffer.rewind();
            buffer.get(data);
            GlobalVariables.selectedFileName = "cam_" + getRandomString();
            GlobalVariables.capturedImageByteArray = data;
            GlobalVariables.selectedMediaMimeType = "jpg";
            recordVideoTimeRelativeLayout.setVisibility(View.GONE);
            retakeSubmitLinearLayout.setVisibility(View.VISIBLE);
            captureImageButton.setVisibility(View.GONE);
        } catch (Exception e) {
            onException(e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cameraFacing", mCameraFacing);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCameraFacing = savedInstanceState.getString("cameraFacing");
    }

    private void onException(String exception) {
        camSkipAlertDialog(exception);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "onBackPressed");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void onConnectionTimeOut() {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "TimeOutException");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private String getRandomString() {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        if (saltStr.length() > 5) {
            saltStr = saltStr.substring(0, 5);
        }
        return saltStr;
    }
    public void camSkipAlertDialog(String exception) {
        CommonLocale commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            handler.post(() -> {
                alertDialog.setMessage(commonLocale.getCamSkip());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, commonLocale.getCamError102ButtonContinue(), (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.putExtra("RESULT", exception);
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                });
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    private void checkCamera(String cameraFacing) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mActivity);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                if (GlobalVariables.CAMERA_FACING_FRONT.equals(cameraFacing)) {
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();
                } else if (GlobalVariables.CAMERA_FACING_BACK.equals(cameraFacing)) {
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                }
                if (cameraSelector != null) {
                    try {
                        mCameraAvailabilityListener.isCameraAvailable(cameraProvider.hasCamera(cameraSelector), cameraFacing);
                    } catch (CameraInfoUnavailableException e) {
                        mCameraAvailabilityListener.isCameraAvailable(false, cameraFacing);
                        if (!cameraProviderFuture.isCancelled()) {
                            cameraProviderFuture.cancel(true);
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                mCameraAvailabilityListener.isCameraAvailable(false, cameraFacing);
                if (!cameraProviderFuture.isCancelled()) {
                    cameraProviderFuture.cancel(true);
                }
            }
        }, ContextCompat.getMainExecutor(mActivity));
    }

    @Override
    public void isCameraAvailable(boolean hasCamera, String lensFacing) {
        if (hasCamera) {
            startCamera();
        } else {
            if (mCurrentStage != null) {
                if ("FaceMatchFragment".equals(mCurrentStage)) {
                    if (cameraProviderFuture != null && !cameraProviderFuture.isCancelled()) {
                        cameraProviderFuture.cancel(true);
                    }
                    camSkipAlertDialog("GENERATE_QR");
                } else {
                    if (cameraProviderFuture != null && !cameraProviderFuture.isCancelled()) {
                        cameraProviderFuture.cancel(true);
                    }
                    camSkipAlertDialog("onBackPressed");
                }
            }
        }
    }

}
interface CameraAvailabilityListener {
    void isCameraAvailable(boolean hasCamera, String lensFacing);
}
