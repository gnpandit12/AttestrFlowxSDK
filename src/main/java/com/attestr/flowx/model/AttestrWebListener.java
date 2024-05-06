package com.attestr.flowx.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.attestr.flowx.listener.AttestrWebViewListener;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;

import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 26/09/21
 **/
public class AttestrWebListener {

    private static final String TAG = "AttestrWebListener";
    private Activity mActivity;
    private Map<String, Object> response;
    private String status;
    private AttestrWebViewListener attestrWebViewListener;

    public AttestrWebListener(Activity activity) {
        this.mActivity = activity;
        this.attestrWebViewListener = (AttestrWebViewListener) activity;
    }

    @JavascriptInterface
    public void dispatch(String message) {
        if (message != null){
            try {
                response = CommonUtils.jsonToMap(message);
                if (response.containsKey("status")){
                    status = Objects.requireNonNull(response.get("status")).toString();
                    switch (status) {
                        case GlobalVariables.STATUS_DLAUTH_RESPONSE_READY:
                            Intent intent = new Intent();
                            mActivity.setResult(Activity.RESULT_OK, intent);
                            mActivity.finish();
                            break;
                        case GlobalVariables.SESSION_ERROR:
                            attestrWebViewListener.onSessionError(message);
                            break;
                    }
                }
            } catch (Exception e){
                HandleException.handleInternalException("AttestrWebListener Exception: "+ e);
            }
        }
    }

}
