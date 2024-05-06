package com.attestr.flowx.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.attestr.flowx.listener.AadhaarOfflineXmlListener;
import com.attestr.flowx.listener.DigilockerListener;
import com.attestr.flowx.listener.ImageSizeExceededListener;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.view.activity.JobDetailsActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.AadharOfflineXMLVerificationFragment;
import com.attestr.flowx.view.fragments.EducationVerificationFragment;
import com.attestr.flowx.view.fragments.FaceMatchFragment;
import com.attestr.flowx.view.fragments.InPersonVerificationFragment;
import com.attestr.flowx.view.fragments.QueryFragment;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckFragmentTwo;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 04/09/21
 **/
public class ApiRequest {

    private static final String TAG = "ApiRequest";
    private static OkHttpClient client = new OkHttpClient();
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static String getRequestResponse;
    private static String URL = "https://facade.attestr.com/digilocker-issuers?";
    private static DigilockerListener digilockerListener;
    private static ImageSizeExceededListener imageSizeExceededListener;
    private static AadhaarOfflineXmlListener aadhaarOfflineXmlListener;
    private static String responseString;
    private static final String XML_ERROR_CODE = "4008";
    private static final String IMAGE_ERROR_CODE = "4005";
    private static MediaHandshakeListener mediaHandshakeListener;
    private static ApiRequest apiRequest;
    private JobDetailsActivity mJobDetailsActivity;
    private Activity mActivity;
    private static int mediaUploadFailedCount;

    public static ApiRequest getInstance(boolean newInstance) {
        if (newInstance) {
            apiRequest = new ApiRequest();
            return apiRequest;
        } else {
            if (apiRequest == null) {
                apiRequest = new ApiRequest();
            }
        }
        return apiRequest;
    }

    public void setData(Activity activity) {
        this.mActivity = activity;
    }

    public void digilockerRequest(Activity activity) {
        GlobalVariables.issuer = Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("issuer")).toString();
        digilockerListener = (DigilockerListener) activity;
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(URL)).newBuilder();
        httpBuilder.addQueryParameter("org", Objects.requireNonNull(GlobalVariables.issuer));
        Request request = new Request.Builder().url(httpBuilder.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                HandleException.handleInternalException("OkHttp get request exception: " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        getRequestResponse = Objects.requireNonNull(response.body()).string();
                        digilockerListener.digiDocumentResponse(getRequestResponse);
                    } catch (IOException e) {
                        HandleException.handleInternalException(e.toString());
                    }
                }
            }
        });
    }

    public void apiRequest(String status,
                           Activity clientAppActivity,
                           String signature,
                           String metadata
    ) {
        HttpUrl.Builder httpBuilder;
        Request request = null;
        String string = GlobalVariables.clientKey + ":" + signature;
        byte[] bytes = string.getBytes();
        String authToken = Base64.encodeToString(bytes, Base64.NO_WRAP);
        switch (status) {
            case GlobalVariables.HANDSHAKE_READY:
            case GlobalVariables.STATUS_DLAUTH_READY:
                httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.HANDSHAKE_URL)).newBuilder();
                httpBuilder.addQueryParameter("lc", GlobalVariables.locale);
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .get()
                        .header("Authorization", "Basic " + authToken)
                        .addHeader("Content-Type", "application/json")
                        .build();
                break;
            case GlobalVariables.STAGE_TYPE_UIDAI:
                if (GlobalVariables.selectedXMLFileByteArray != null) {
                    httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.XML_MEDIA_HANDSHAKE_URL)).newBuilder();
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                    "file",
                                    GlobalVariables.selectedFileName,
                                    RequestBody.create(GlobalVariables.selectedXMLFileByteArray, MediaType.parse("text/xml"))
                            )
                            .build();
                    request = new Request.Builder()
                            .url(httpBuilder.build())
                            .post(formBody)
                            .header("Authorization", "Basic " + authToken)
                            .addHeader("Content-Type", "multipart/form-data")
                            .build();
                }
                break;
            case GlobalVariables.STAGE_TYPE_FACE_MATCH:
            case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                if (GlobalVariables.capturedImageByteArray != null) {
                    httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.IMAGE_MEDIA_HANDSHAKE_URL)).newBuilder();
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                    "file",
                                    metadata,
                                    RequestBody.create(GlobalVariables.capturedImageByteArray, MediaType.parse("image/" + GlobalVariables.selectedMediaMimeType))
                            )
                            .build();
                    request = new Request.Builder()
                            .url(httpBuilder.build())
                            .post(formBody)
                            .header("Authorization", "Basic " + authToken)
                            .addHeader("Content-Type", "multipart/form-data")
                            .build();
                }
                break;
            case GlobalVariables.STAGE_TYPE_IPV:
                switch (metadata) {
                    case GlobalVariables.IPV_META_SELFIE:
                        if (GlobalVariables.capturedImageByteArray != null) {
                            httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.IMAGE_MEDIA_HANDSHAKE_URL)).newBuilder();
                            RequestBody formBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                            "file",
                                            metadata,
                                            RequestBody.create(GlobalVariables.capturedImageByteArray, MediaType.parse("image/" + GlobalVariables.ipvImageMimetype))
                                    )
                                    .build();
                            request = new Request.Builder()
                                    .url(httpBuilder.build())
                                    .post(formBody)
                                    .header("Authorization", "Basic " + authToken)
                                    .addHeader("Content-Type", "multipart/form-data")
                                    .build();
                        }
                        break;
                    case GlobalVariables.IPV_META_VIDEO:
                        if (GlobalVariables.selfieVideoByteArray != null) {
                            httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.VIDEO_MEDIA_HANDSHAKE_URL)).newBuilder();
                            RequestBody formBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                            "file",
                                            metadata,
                                            RequestBody.create(GlobalVariables.selfieVideoByteArray, MediaType.parse("video/" + GlobalVariables.ipvVideoMimetype))
                                    )
                                    .build();
                            request = new Request.Builder()
                                    .url(httpBuilder.build())
                                    .post(formBody)
                                    .header("Authorization", "Basic " + authToken)
                                    .addHeader("Content-Type", "multipart/form-data")
                                    .build();
                        }
                        break;
                }
                break;
            default:
                switch (GlobalVariables.selectedMediaMimeType) {
                    case "jpg":
                    case "jpeg":
                    case "png":
                        if (GlobalVariables.capturedImageByteArray != null) {
                            httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.DOCUMENT_MEDIA_HANDSHAKE_URL)).newBuilder();
                            RequestBody formBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                            "file",
                                            GlobalVariables.selectedFileName,
                                            RequestBody.create(GlobalVariables.capturedImageByteArray, MediaType.parse("image/" + GlobalVariables.selectedMediaMimeType))
                                    )
                                    .build();
                            request = new Request.Builder()
                                    .url(httpBuilder.build())
                                    .post(formBody)
                                    .header("Authorization", "Basic " + authToken)
                                    .addHeader("Content-Type", "multipart/form-data")
                                    .build();
                        }
                        break;
                    case "pdf":
                        if (GlobalVariables.querySelectedPDFByteArray != null) {
                            httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.DOCUMENT_MEDIA_HANDSHAKE_URL)).newBuilder();
                            RequestBody formBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                            "file",
                                            GlobalVariables.selectedFileName,
                                            RequestBody.create(GlobalVariables.querySelectedPDFByteArray, MediaType.parse("application/pdf"))
                                    )
                                    .build();
                            request = new Request.Builder()
                                    .url(httpBuilder.build())
                                    .post(formBody)
                                    .header("Authorization", "Basic " + authToken)
                                    .addHeader("Content-Type", "multipart/form-data")
                                    .build();
                        }
                        break;
                }
                break;
        }
        if (request != null) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    switch (status) {
                        case GlobalVariables.HANDSHAKE_READY:
                        case GlobalVariables.STATUS_DLAUTH_READY:
                            HandleException.handleInternalException("OkHttp handshake ready request exception: " + e);
                            break;
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        responseString = Objects.requireNonNull(response.body()).string();
                    } catch (Exception e) {
                        HandleException.handleInternalException("Api Request Response" + e);
                    }
                    switch (status) {
                        case GlobalVariables.HANDSHAKE_READY:
                        case GlobalVariables.STATUS_DLAUTH_READY:
                            try {
                                handler.post(() -> {
                                    try {
                                        startLaunchActivity(status, clientAppActivity, responseString);
                                    } catch (Exception e) {
                                        HandleException.handleInternalException(e.toString());
                                    }
                                });
                            } catch (Exception e) {
                                HandleException.handleInternalException(e.toString());
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_UIDAI:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    CommonUtils.sendRequest(AttestrRequest.actionSubmitUidai(mediaID));
                                } else if (response.code() == 400) {
                                    mediaUploadFailedCount++;
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (XML_ERROR_CODE.equals(code)) {
                                            handler.post(() -> {
                                                aadhaarOfflineXmlListener = AadharOfflineXMLVerificationFragment.getInstance(false);
                                                aadhaarOfflineXmlListener.onFileSizeExceeded();
                                            });
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                HandleException.handleInternalException(e.toString());
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                    mediaHandshakeListener = FaceMatchFragment.getInstance(false);
                                    if (GlobalVariables.isCameraSelected) {
                                        handler.post(() -> mediaHandshakeListener.onImageUploaded(metadata, mediaID));
                                    } else if (GlobalVariables.isStorageSelected) {
                                        handler.post(() -> mediaHandshakeListener.onDocumentUploaded(metadata, mediaID, mimeType));
                                    }
                                    ProgressIndicator.setProgressBarInvisible();
                                } else if (response.code() == 400) {
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (IMAGE_ERROR_CODE.equals(code)) {
                                            imageSizeExceededListener = FaceMatchFragment.getInstance(false);
                                            GlobalVariables.selfieSource = null;
                                            GlobalVariables.targetFront = null;
                                            GlobalVariables.targetBack = null;
                                            handler.post(() -> imageSizeExceededListener.onImageSizeExceeded(metadata));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request FACE MATCH: " + e);
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                    mediaHandshakeListener = DigitalAddressCheckFragmentTwo.getInstance(false);
                                    if (GlobalVariables.isCameraSelected) {
                                        handler.post(() -> mediaHandshakeListener.onImageUploaded(metadata, mediaID));
                                    } else if (GlobalVariables.isStorageSelected) {
                                        handler.post(() -> mediaHandshakeListener.onDocumentUploaded(metadata, mediaID, mimeType));
                                    }
                                    ProgressIndicator.setProgressBarInvisible();
                                } else if (response.code() == 400) {
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (IMAGE_ERROR_CODE.equals(code)) {
                                            imageSizeExceededListener = DigitalAddressCheckFragmentTwo.getInstance(false);
                                            handler.post(() -> imageSizeExceededListener.onImageSizeExceeded(metadata));
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request FACE MATCH: " + e);
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_EDUCATION:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    mediaHandshakeListener = EducationVerificationFragment.getInstance(false);
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                    handler.post(() -> mediaHandshakeListener.onDocumentUploaded(mediaID, originalName, metadata));
                                    ProgressIndicator.setProgressBarInvisible();
                                } else if (response.code() == 400) {
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (IMAGE_ERROR_CODE.equals(code)) {
                                            imageSizeExceededListener = EducationVerificationFragment.getInstance(false);
                                            handler.post(() -> imageSizeExceededListener.onImageSizeExceeded(metadata));
                                        }
                                    }
                                } else {
                                    CommonUtils.mediaUploadAlertDialog(mActivity);
                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request QUERY Flow " + e);
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_EMPLOYMENT:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    if (mJobDetailsActivity != null) {
                                        mediaHandshakeListener = mJobDetailsActivity;
                                        String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                        String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                        String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                        handler.post(() -> mediaHandshakeListener.onImageUploaded(metadata, mediaID));
                                    }
                                    ProgressIndicator.setProgressBarInvisible();
                                } else if (response.code() == 400) {
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (IMAGE_ERROR_CODE.equals(code) && mJobDetailsActivity != null) {
                                            imageSizeExceededListener = mJobDetailsActivity;
                                            handler.post(() -> imageSizeExceededListener.onImageSizeExceeded(metadata));
                                        }
                                    }
                                } else {
                                    CommonUtils.mediaUploadAlertDialog(mActivity);
                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request QUERY Flow " + e);
                            }
                            break;
                        case GlobalVariables.STAGE_TYPE_IPV:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                    mediaHandshakeListener = InPersonVerificationFragment.getInstance(false);
                                    if (mediaHandshakeListener != null) {
                                        switch (metadata) {
                                            case GlobalVariables.IPV_META_SELFIE:
                                                handler.post(() -> mediaHandshakeListener.onImageUploaded(metadata, mediaID));
                                                break;
                                            case GlobalVariables.IPV_META_VIDEO:
                                                handler.post(() -> mediaHandshakeListener.onVideoUploaded(metadata, mediaID));
                                                break;
                                        }
                                    }
                                } else {
                                    CommonUtils.mediaUploadAlertDialog(mActivity);
                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request IPV submit: " + e);
                            }
                            break;
                        default:
                            try {
                                Map<String, Object> responseMap = CommonUtils.jsonToMap(responseString);
                                if (response.code() == 200) {
                                    mediaUploadFailedCount = 0;
                                    mediaHandshakeListener = QueryFragment.getInstance(false);
                                    String mediaID = Objects.requireNonNull(responseMap.get("_id")).toString();
                                    String originalName = Objects.requireNonNull(responseMap.get("originalName")).toString();
                                    String mimeType = Objects.requireNonNull(responseMap.get("mimeType")).toString();
                                    handler.post(() -> mediaHandshakeListener.onDocumentUploaded(mediaID, originalName, metadata));
                                    ProgressIndicator.setProgressBarInvisible();
                                } else if (response.code() == 400) {
                                    if (mediaUploadFailedCount > 3) {
                                        HandleException.handleInternalException("Media upload exception");
                                    } else {
                                        String code = Objects.requireNonNull(responseMap.get("code")).toString();
                                        if (IMAGE_ERROR_CODE.equals(code)) {
                                            imageSizeExceededListener = QueryFragment.getInstance(false);
                                            handler.post(() -> imageSizeExceededListener.onImageSizeExceeded(metadata));
                                        }
                                    }
                                } else {
                                    CommonUtils.mediaUploadAlertDialog(mActivity);
                                }
                            } catch (Exception e) {
                                HandleException.handleInternalException("Api Request QUERY Flow " + e);
                            }
                            break;
                    }
                }
            });
        }
    }

    private void startLaunchActivity(
            String status,
            Activity clientAppActivity,
            String response) {
        try {
            GlobalVariables.currentResponse = response;
            GlobalVariables.currentStatus = status;
            Intent intent = new Intent(clientAppActivity, LaunchActivity.class);
            clientAppActivity.startActivityForResult(intent, 1);
            ProgressIndicator.setProgressBarInvisible();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public void setCurrentActivity(JobDetailsActivity jobDetailsActivity) {
        this.mJobDetailsActivity = jobDetailsActivity;
    }

}
