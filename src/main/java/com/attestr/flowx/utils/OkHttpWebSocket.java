package com.attestr.flowx.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.attestr.flowx.listener.AttestrFlowXListener;
import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.listener.NetworkChangeListener;
import com.attestr.flowx.model.ApiRequest;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.HandshakeData;
import com.attestr.flowx.model.response.common.IPGeolLocationData;
import com.attestr.flowx.model.response.common.Query;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.common.StageNextData;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.AadharOfflineXMLVerificationFragment;
import com.attestr.flowx.view.fragments.BankAccountVerificationFragment;
import com.attestr.flowx.view.fragments.DigiLockerAuthorizationFragment;
import com.attestr.flowx.view.fragments.DigiLockerDocumentFragment;
import com.attestr.flowx.view.fragments.EPanDigilockerFragment;
import com.attestr.flowx.view.fragments.FaceMatchFragment;
import com.attestr.flowx.view.fragments.FssaiVerificationFragment;
import com.attestr.flowx.view.fragments.GstinCheckFragment;
import com.attestr.flowx.view.fragments.InPersonVerificationFragment;
import com.attestr.flowx.view.fragments.MasterBusinessFragment;
import com.attestr.flowx.view.fragments.MasterDirectorFragment;
import com.attestr.flowx.view.fragments.PanVerificationFragment;
import com.attestr.flowx.view.fragments.QueryFragment;
import com.attestr.flowx.view.fragments.UpiVerificationFragment;
import com.attestr.flowx.view.fragments.VoterIDVerificationFragment;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckGoogleMapFragment;
import com.attestr.flowx.view.fragments.emailOTPVerification.EmailOTPFragmentTwo;
import com.attestr.flowx.view.fragments.mobileOtpVerification.MobileOtpFragmentTwo;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 **/

public class OkHttpWebSocket implements NetworkChangeListener {

    private static final String TAG = "OKHttpWebSocket";
    private EventListener eventListener;
    private Activity mActivity, mClientAppActivity, mLaunchActivity;
    private WebSocket mWebSocket;
    private final Handler handler;
    private AttestrFlowXListener mAttestrFlowXListener;
    private String status, stageType;
    private boolean isNetworkAvailable;
    private final AttestrApplication attestrApplication;
    private boolean isWebSocketConnected = false;
    private boolean isNetworkAlertDialogVisible = false;
    private String currentRequest;

    public OkHttpWebSocket() {
        super();
        handler = new Handler(Looper.getMainLooper());
        // Life cycle observer network connection status throughout the application
        attestrApplication = AttestrApplication.getInstance(true);
        attestrApplication.setNetworkChangeListener(this);
    }

    public void setData(Activity currentActivity) {
        this.mActivity = currentActivity;
        ProgressIndicator.setActivity(mActivity);
        if (currentActivity instanceof LaunchActivity) {
            this.mLaunchActivity = currentActivity;
            this.eventListener = (EventListener) mLaunchActivity;
            attestrApplication.setActivity(currentActivity);
        } else {
            this.mClientAppActivity = currentActivity;
            this.mAttestrFlowXListener = (AttestrFlowXListener) currentActivity;
        }
    }

    public void connect(String requestJson) {
        try {
            Request request = new Request.Builder().url(GlobalVariables.socketURL).get().build();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            okHttpClient.newWebSocket(request, webSocketListener(requestJson));
            okHttpClient.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    private WebSocketListener webSocketListener(String request) {
        return new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                isWebSocketConnected = true;
                mWebSocket = webSocket;
                if (request != null) {
                    webSocket.send(request);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull final String message) {
                super.onMessage(webSocket, message);
                String response = message;
                try {
                    ResponseData<HandshakeData> handshakeData = CommonUtils.gsonBuilder.create().fromJson(message, new TypeToken<ResponseData<HandshakeData>>() {}.getType());
                    if (handshakeData.getType() != null) {
                        stageType = handshakeData.getType();
                        GlobalVariables.currentVerificationStageType = stageType;
                    }
                    if (handshakeData.getStatus() != null) {
                        status = handshakeData.getStatus();
                        switch (status) {
                            case GlobalVariables.HANDSHAKE_READY:
                            case GlobalVariables.STATUS_DLAUTH_READY:
                                GlobalVariables.currentConnectID = handshakeData.getData().getCid();
                                if (eventListener != null) {
                                    CommonUtils.sendRequest(AttestrRequest.stageNext());
                                } else {
                                    GlobalVariables.digiLockerDigest = handshakeData.getData().getDigest();
                                    ApiRequest.getInstance(true).apiRequest(status, mClientAppActivity, handshakeData.getData().getSignature(), null);
                                }
                                break;
                            case GlobalVariables.STAGE_NEXT_READY:
                                stageNextReady(message);
                                break;
                            case GlobalVariables.STAGE_READY:
                                stageReady(message);
                                break;
                            case GlobalVariables.DATA_READY:
                                dataReady();
                                break;
                            case GlobalVariables.STAGE_SKIP:
                                stageSkip();
                                break;
                            case GlobalVariables.SKIP_END:
                                skipEnd(message);
                                break;
                            case GlobalVariables.HANDSHAKE_END:
                                handShakeEnd(message);
                                break;
                            case GlobalVariables.RECONNECT_READY:
                                String cid = handshakeData.getData().getCid();
                                GlobalVariables.currentConnectID = handshakeData.getData().getCid();
                                if (currentRequest != null) {
                                    sendRequest(currentRequest);
                                }
                                break;
                            case GlobalVariables.DATA_ERROR:
                                dataError(message);
                                break;
                            case GlobalVariables.MEDIA_HANDSHAKE_READY:
                                mediaHandshakeReady(message, stageType);
                                break;
                            case GlobalVariables.IP_GEOLOCATION_READY:
                                ResponseData<IPGeolLocationData> ipGeoLocationDataResponseData = CommonUtils.gsonBuilder.create().fromJson(message, new TypeToken<ResponseData<IPGeolLocationData>>() {}.getType());
                                switch (ipGeoLocationDataResponseData.getType()) {
                                    case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                                    case GlobalVariables.STAGE_TYPE_IPV:
                                        GlobalVariables.detectedLatitude = Double.valueOf(ipGeoLocationDataResponseData.getData().getLatitude());
                                        GlobalVariables.detectedLongitude = Double.valueOf(ipGeoLocationDataResponseData.getData().getLongitude());
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case GlobalVariables.QR_GEN_READY:
                                handler.post(() -> eventListener.updateUI(GlobalVariables.QR_GEN_READY, stageType, null, message));
                                break;
                            case GlobalVariables.QR_VALIDATE_READY:
                                ProgressIndicator.setProgressBarVisible();
                                break;
                            case GlobalVariables.STATUS_MOBILE_OTP_GEN_READY:
                                handler.post(() -> {
                                    if (GlobalVariables.isOTPExpired) {
                                        MobileOtpFragmentTwo mobileOtpFragmentTwo = MobileOtpFragmentTwo.getInstance(false);
                                        mobileOtpFragmentTwo.setOTPStatus(false);
                                    } else {
                                        MobileOtpFragmentTwo mobileOtpFragmentTwo = MobileOtpFragmentTwo.getInstance(true);
                                        mobileOtpFragmentTwo.setData(mLaunchActivity, message);
                                        ((LaunchActivity) mLaunchActivity).addFragment(mobileOtpFragmentTwo);
                                    }
                                });
                                break;
                            case GlobalVariables.STATUS_EMAIL_OTP_GEN_READY:
                                handler.post(() -> {
                                    if (GlobalVariables.isOTPExpired) {
                                        EmailOTPFragmentTwo emailOTPFragmentTwo = EmailOTPFragmentTwo.getInstance(false);
                                        emailOTPFragmentTwo.setOTPStatus(false);
                                    } else {
                                        EmailOTPFragmentTwo emailOTPFragmentTwo = EmailOTPFragmentTwo.getInstance(true);
                                        emailOTPFragmentTwo.setData(mLaunchActivity, message);
                                        ((LaunchActivity) mLaunchActivity).addFragment(emailOTPFragmentTwo);
                                    }
                                });
                                break;
                            case GlobalVariables.STATUS_MOBILE_OTP_RESEND_READY:
                            case GlobalVariables.STATUS_EMAIL_OTP_RESEND_READY:
                                ProgressIndicator.setProgressBarInvisible();
                                break;
                            case GlobalVariables.STATUS_QR_REDIRECT:
                                CommonUtils.showQRRedirectAlertDialog(mActivity);
                                break;
                            case GlobalVariables.QUERY_SUBMIT_READY:
                                ResponseData<StageInitData> queryResponse = CommonUtils.gsonBuilder.create().fromJson(message, new TypeToken<ResponseData<StageInitData>>() {
                                }.getType());
                                if (queryResponse.getData().getQuery() != null) {
                                    GlobalVariables.queryID = queryResponse.getData().getQuery().getId();
                                    GlobalVariables.queryText = queryResponse.getData().getQuery().getText();
                                    handler.post(() -> {
                                        QueryFragment queryFragment = QueryFragment.getInstance(true);
                                        queryFragment.setData(mActivity, CommonUtils.isTablet(mActivity));
                                        ((LaunchActivity) mActivity).addFragment(queryFragment);
                                    });
                                } else {
                                    handler.post(() -> ((LaunchActivity) mLaunchActivity).addFragment(new WaitFragment(mLaunchActivity, null)));
                                    GlobalVariables.currentStageId = null;
                                    CommonUtils.sendRequest(AttestrRequest.actionDestroy());
                                }
                                break;
                            case GlobalVariables.SESSION_ERROR:
                                sessionError(message);
                                break;
                        }
                    }
                } catch (Exception e) {
                    HandleException.handleInternalException(e.toString());
                }

            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                webSocket.cancel();
                isWebSocketConnected = false;
                String exceptionMessage = t.getMessage();
                if (null != exceptionMessage) {
                    switch (exceptionMessage) {
                        case "Software caused connection abort":
                        case "Socket closed":
                            if (!isNetworkAvailable && !isNetworkAlertDialogVisible) {
                                showNetworkConnectionAlertDialog();
                            }
                            break;
                    }
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
                // TODO remove Keep Alive
                if (code == 1000) {
                    // Code 1000
                    // Notify event successful
                    // WebSocket closed from server
                    callListener(GlobalVariables.currentEvent);
                } else if (code == 1001) {
                    // "Connection timeout"
                    // Send reconnect request before any new request
                    isWebSocketConnected = false;
                } else {
                    // Error 5001, 500
                    HandleException.handleInternalException("Web Socket onClosing error");
                }
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                mWebSocket.close(code, reason);
            }

        };
    }

    public void sendRequest(String jsonString) {
        currentRequest = jsonString;
        if (this.mWebSocket != null) {
            if (isWebSocketConnected) {
                this.mWebSocket.send(jsonString);
            } else {
                if (isNetworkAvailable) {
                    connect(AttestrRequest.actionReconnect());
                } else {
                    exitSDK();
                }
            }
        }
    }

    public void navigateToClientActivity() {
        if (mLaunchActivity != null) {
            Intent intent = new Intent();
            mLaunchActivity.setResult(Activity.RESULT_OK, intent);
            mLaunchActivity.finish();
        }
    }

    private void stageNextReady(String response) {
        try {
            ResponseData<StageNextData> stageNextReady = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<StageNextData>>() {}.getType());
            String stageID = stageNextReady.getData().getStage();
            Query query = stageNextReady.getData().getQuery();
            if (stageID != null) {
                GlobalVariables.currentStageId = stageID;
                CommonUtils.sendRequest(AttestrRequest.stageInit(stageID));
            } else {
                if (query != null) {
                    GlobalVariables.queryID = query.getId();
                    GlobalVariables.queryText = query.getText();
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_QUERY, null, response));
                } else {
                    GlobalVariables.currentStageId = null;
                    CommonUtils.sendRequest(AttestrRequest.actionDestroy());
                }
            }
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }

    private void stageReady(String response) {
        try {
            GlobalVariables.stageInitResponse = response;
            ResponseData<StageInitData> stageReady = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<StageInitData>>() {
            }.getType());
            String stageType = stageReady.getType();
            GlobalVariables.stageReadyResponse = stageReady;
            switch (stageType) {
                case GlobalVariables.STAGE_TYPE_PAN:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_PAN, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_VPA:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_VPA, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_BANK_ACCOUNT:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_BANK_ACCOUNT, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_GSTIN:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_GSTIN, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_MASTER_BUSINESS:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_MASTER_BUSINESS, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_AML_BUSINESS:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_AML_BUSINESS, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_AML_PERSON:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_AML_PERSON, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_UIDAI:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_UIDAI, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_FACE_MATCH, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_EAADHAAR:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_EAADHAAR, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_DIGILOCKER_DOC:
                    ApiRequest.getInstance(false).digilockerRequest(mActivity);
                    break;
                case GlobalVariables.STAGE_TYPE_ECOURT_PERSON:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_ECOURT_PERSON, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_ECOURT_BUSINESS:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_ECOURT_BUSINESS, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_MOBILE_OTP:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_MOBILE_OTP, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_EMAIL_OTP:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_EMAIL_OTP, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_EDUCATION:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_EDUCATION, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_EMPLOYMENT:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_EMPLOYMENT, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_FSSAI:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_FSSAI, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_IPV:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_IPV, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_VOTER_ID:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_VOTER_ID, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_DRIVING_LICENSE:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_DRIVING_LICENSE, null, response));
                    break;
                case GlobalVariables.STAGE_TYPE_EPAN:
                    handler.post(() -> eventListener.updateUI(GlobalVariables.STAGE_READY, GlobalVariables.STAGE_TYPE_EPAN, null, response));
                    break;
            }
        } catch (Exception e) {
            HandleException.handleInternalException("OkHttpWebSocket Stage Ready "+e);
        }
    }

    public void dataError(String response) {
        try {
            Map<String, Object> responseMap = CommonUtils.jsonToMap(response);
            if (responseMap.containsKey("type")) {
                String type = String.valueOf(responseMap.get("type"));
                switch (type) {
                    case GlobalVariables.STAGE_TYPE_PAN:
                        handler.post(() -> PanVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_VPA:
                        handler.post(() -> UpiVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_BANK_ACCOUNT:
                        handler.post(() -> BankAccountVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_GSTIN:
                        handler.post(() -> GstinCheckFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MASTER_BUSINESS:
                        handler.post(() -> MasterBusinessFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR:
                        handler.post(() -> MasterDirectorFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_UIDAI:
                        handler.post(() -> AadharOfflineXMLVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                        handler.post(() -> FaceMatchFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_EAADHAAR:
                        handler.post(() -> {
                            DigiLockerAuthorizationFragment digiLockerAuthorizationFragment = DigiLockerAuthorizationFragment.getInstance(false);
                            digiLockerAuthorizationFragment.OnDataError(response);
                        });
                        break;
                    case GlobalVariables.STAGE_TYPE_DIGILOCKER_DOC:
                        handler.post(() -> DigiLockerDocumentFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MOBILE_OTP:
                        handler.post(() -> MobileOtpFragmentTwo.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_EMAIL_OTP:
                        handler.post(() -> EmailOTPFragmentTwo.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_FSSAI:
                        handler.post(() -> FssaiVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_IPV:
                        handler.post(() -> InPersonVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_VOTER_ID:
                        handler.post(() -> VoterIDVerificationFragment.getInstance(false).OnDataError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_EPAN:
                        handler.post(() -> EPanDigilockerFragment.getInstance(false).OnDataError(response));
                        break;
                }
            }
        } catch (Exception e) {
            HandleException.handleInternalException("OkHttpWebSocket Data Error"+ e);
        }
    }

    public void sessionError(String response) {
        try {
            Map<String, Object> responseMap = CommonUtils.jsonToMap(response);
            if (responseMap.containsKey("type")) {
                String type = String.valueOf(responseMap.get("type"));
                switch (type) {
                    case GlobalVariables.STAGE_TYPE_PAN:
                        handler.post(() -> PanVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_VPA:
                        handler.post(() -> UpiVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_BANK_ACCOUNT:
                        handler.post(() -> BankAccountVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_GSTIN:
                        handler.post(() -> GstinCheckFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MASTER_BUSINESS:
                        handler.post(() -> MasterBusinessFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR:
                        handler.post(() -> MasterDirectorFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                        handler.post(() -> FaceMatchFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_DIGILOCKER_DOC:
                        handler.post(() -> DigiLockerDocumentFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_MOBILE_OTP:
                        handler.post(() -> MobileOtpFragmentTwo.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_EMAIL_OTP:
                        handler.post(() -> EmailOTPFragmentTwo.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_FSSAI:
                        handler.post(() -> FssaiVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_IPV:
                        handler.post(() -> InPersonVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_VOTER_ID:
                        handler.post(() -> VoterIDVerificationFragment.getInstance(false).OnSessionError(response));
                        break;
                    case GlobalVariables.STAGE_TYPE_EPAN:
                        handler.post(() -> EPanDigilockerFragment.getInstance(false).OnSessionError(response));
                        break;
                    default:
                        HandleException.handleSessionError(response);
                        break;
                }
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public void stageSkip() {
        CommonUtils.sendRequest(AttestrRequest.stageNext());
    }

    public void skipEnd(String response) {
        try {
            GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_SKIPPED;
            HashMap skipEndMap = CommonUtils.getGson().fromJson(response, HashMap.class);
            if (skipEndMap.get("data") != null) {
                CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(new JSONObject(Objects.requireNonNull(skipEndMap.get("data")).toString()), GlobalVariables.NOTIFY_EVENT_SKIPPED));
            } else {
                CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(null, GlobalVariables.NOTIFY_EVENT_SKIPPED));
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    private void handShakeEnd(String response) {
        try {
            JSONObject data = new JSONObject(response).getJSONObject("data");
            GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_COMPLETED;
            GlobalVariables.handshakeEndDataMap = CommonUtils.jsonToMap(Objects.requireNonNull(data).toString());
            CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(data, GlobalVariables.NOTIFY_EVENT_COMPLETED));
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public void dataReady() {
        handler.post(() -> eventListener.updateUI(GlobalVariables.DATA_READY, null, null, null));
    }

    private void mediaHandshakeReady(String response, String stageType) {
        ResponseData<HandshakeData> mediaHandshakeData = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<HandshakeData>>(){}.getType());
        String signature = mediaHandshakeData.getData().getSignature();
        if (signature != null) {
            switch (stageType) {
                case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                case GlobalVariables.STAGE_TYPE_IPV:
                    String meta = mediaHandshakeData.getData().getMeta();
                    if (meta != null) {
                        ApiRequest.getInstance(false).apiRequest(stageType, mClientAppActivity, signature, meta);
                    }
                    break;
                default:
                    ApiRequest.getInstance(false).apiRequest(stageType, mClientAppActivity, signature, null);
                    break;
            }
        }
    }

    public void callListener(String CURRENT_EVENT) {
        if (mClientAppActivity instanceof AttestrFlowXListener) {
            switch (CURRENT_EVENT) {
                case GlobalVariables.NOTIFY_EVENT_COMPLETED:
                    if (GlobalVariables.handshakeEndDataMap != null) {
                        handler.post(() -> {
                            navigateToClientActivity();
                            mAttestrFlowXListener.onFlowXComplete(GlobalVariables.handshakeEndDataMap);
                            CommonUtils.cleanUp();
                        });
                    }
                    break;
                case GlobalVariables.NOTIFY_EVENT_SKIPPED:
                    final HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("stage", GlobalVariables.currentStageId);
                    if (GlobalVariables.skipDataMap != null) {
                        hashMap.putAll(GlobalVariables.skipDataMap);
                    }
                    handler.post(() -> {
                        navigateToClientActivity();
                        mAttestrFlowXListener.onFlowXSkip(hashMap);
                        CommonUtils.cleanUp();
                    });
                    break;
                case GlobalVariables.NOTIFY_EVENT_ERRORED:
                    if (GlobalVariables.sessionErrorDataMap != null) {
                        handler.post(() -> {
                            navigateToClientActivity();
                            ProgressIndicator.setProgressBarInvisible();
                            mAttestrFlowXListener.onFlowXError(CommonUtils.getGson().fromJson(GlobalVariables.sessionErrorDataMap.toString(), HashMap.class));
                            CommonUtils.cleanUp();
                        });
                    }
                    break;
            }
        }
    }

    @Override
    public void onNetworkAvailable() {
        isNetworkAvailable = true;
    }

    @Override
    public void onNetworkLost() {
        if (mWebSocket != null && isWebSocketConnected) {
            mWebSocket.cancel();
        }
        isNetworkAvailable = false;
        isWebSocketConnected = false;
        if (!isNetworkAlertDialogVisible) {
            showNetworkConnectionAlertDialog();
        }
    }

    public void showNetworkConnectionAlertDialog() {
        String connectionErrorTitle = GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getConnectionErrorTitle();
        String connectionErrorMessage = GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getConnectionError();
        isNetworkAlertDialogVisible = true;
        try {
            handler.post(() -> {
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
                alertDialog.setTitle(connectionErrorTitle != null ? connectionErrorTitle : "No internet connection");
                alertDialog.setMessage(connectionErrorMessage != null ? connectionErrorMessage : "Our servers could not be reached. " +
                        "Please make sure you have an active internet connection or try again later.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (dialogInterface, i) -> {
                    alertDialog.dismiss();
                    exitSDK();
                    isNetworkAlertDialogVisible = false;
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    private void exitSDK() {
        try {
            HashMap<String, Object> exceptionMap = new HashMap<>();
            exceptionMap.put("code", "500");
            exceptionMap.put("httpStatusCode", "12029");
            if (null != GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getConnectionError()) {
                exceptionMap.put("message", GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getConnectionError());
            } else {
                exceptionMap.put("message", GlobalVariables.CONNECTION_ERROR);
            }
            handler.post(() -> {
                navigateToClientActivity();
                mAttestrFlowXListener.onFlowXError(exceptionMap);
                CommonUtils.cleanUp();
            });
        } catch (Exception e) {
            String exception = e.toString();
        }
    }

}