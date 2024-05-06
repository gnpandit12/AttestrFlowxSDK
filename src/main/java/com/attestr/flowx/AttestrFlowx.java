package com.attestr.flowx;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.LocaleLanguagesData;
import com.attestr.flowx.utils.Attestr;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.OkHttpWebSocket;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.activity.LocaleActivity;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class AttestrFlowx {
    private Activity clientAppActivity;

    private static final String STAGING_ENVIRONMENT = "Staging";
    private static final String PRODUCTION_ENVIRONMENT = "Production";
    private static final OkHttpClient client = new OkHttpClient();
    private String handshakeID, clientKey;
;
    public AttestrFlowx() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        GlobalVariables.setEnvironment(STAGING_ENVIRONMENT);
    }

    /**
     * Initialises an instance of AttestrFlowx
     * @param cl Mandatory client key
     * @param hs Mandatory handshake key
     * @param activity Activity on which flow is to be rendered
     */
    public void init (
            String cl,
            String hs,
            Activity activity
    ) {
        this.handshakeID = hs;
        this.clientKey = cl;
        this.clientAppActivity = activity;
    }

    /**
     * This function launches the flow with the following specifications
     * @param lc Mandatory language code eg. 'en' for English.
     * @param retry Mandatory parameter to set retry as true if re-running the flow for a previously used handshake.
     * @param qr Optional query parameters.
     */
    public void launch(
            String lc,
            boolean retry,
            Map<String, String> qr
    ) {
        GlobalVariables.isRetry = retry;
        GlobalVariables.queryMap = qr;
        if (isActivityDefined(clientAppActivity)) {
            ProgressIndicator.setActivity(clientAppActivity);
            Attestr.setClientAppActivity(clientAppActivity);
            HandleException.init(clientAppActivity);
            if (isHandshakeValid(handshakeID) && isClientKeyValid(clientKey)) {
                GlobalVariables.handshakeId = handshakeID;
                GlobalVariables.clientKey = clientKey;
                if (isLocaleValid(lc)) {
                    GlobalVariables.locale = lc;
                    ProgressIndicator.setProgressBarVisible();
                    try {
                        OkHttpWebSocket okHttpWebSocket = new OkHttpWebSocket();
                        okHttpWebSocket.setData(clientAppActivity);
                        okHttpWebSocket.connect(AttestrRequest.actionInit());
                        GlobalVariables.webSocket = okHttpWebSocket;
                    } catch (Exception e) {
                        HandleException.handleInternalException(e.toString());
                    }
                } else {
                    ProgressIndicator.setProgressBarVisible();
                    HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(GlobalVariables.LOCAL_LANGUAGES_URL)).newBuilder();
                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .get()
                            .header("Content-Type", "application/json")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            HandleException.handleInternalException("OkHttp get locale languages exception: "+ e);
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            if (response.isSuccessful()) {
                                try {
                                    String res = Objects.requireNonNull(response.body()).string();
                                    LocaleLanguagesData[] localeLanguagesDataArray =
                                            CommonUtils.getGson().fromJson(res, LocaleLanguagesData[].class);
                                    Intent intent = new Intent(clientAppActivity, LocaleActivity.class);
                                    intent.putExtra("LocaleLanguagesDataArray", localeLanguagesDataArray);
                                    ProgressIndicator.setProgressBarInvisible();
                                    clientAppActivity.startActivityForResult(intent, 1);
                                } catch (Exception e) {
                                    HandleException.handleInternalException(e.toString());
                                }
                            }
                        }
                    });
                }
            } else if (!isHandshakeValid(handshakeID)) {
                HandleException.handleInitializationException("hs");
            } else if (!isClientKeyValid(clientKey)) {
                HandleException.handleInitializationException("cl");
            }
        } else {
            HandleException.handleInitializationException("activity");
        }
    }

    private boolean isLocaleValid(String locale) {
        return !TextUtils.isEmpty(locale);
    }

    private boolean isHandshakeValid(String handshakeID) {
        return UserInputValidation.isHandshakeValid(handshakeID);
    }

    private boolean isClientKeyValid(String clientKey) {
        return UserInputValidation.isClientKeyValid(clientKey);
    }

    private boolean isActivityDefined(Activity clientActivity) {
        return clientActivity != null;
    }

}


