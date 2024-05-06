package com.attestr.flowx.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.attestr.flowx.listener.AttestrFlowXListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.DigilockerAuthorizationActivity;
import com.attestr.flowx.view.activity.JobDetailsActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.WaitFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/

public class HandleException {

    private static final String TAG = "Handle_exception";
    private static AttestrFlowXListener mAttestrFlowXListener;
    private static Map<String, Object> exceptionMap;
    private static Activity mActivity;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Intent intent = new Intent();
    private static Activity mCurrentActivity;

    public static void init(Activity activity) {
        mActivity = activity;
        if (activity instanceof LaunchActivity){
            setCurrentActivity(activity);
        }else {
            mAttestrFlowXListener = (AttestrFlowXListener) mActivity;
        }
    }

    public static void handleInitializationException(final String param) {
        exceptionMap  = new HashMap<>();
        exceptionMap.put("code", "40050");
        exceptionMap.put("httpStatusCode", "400");
        if (null != param) {
            exceptionMap.put("message", "Missing or invalid parameter: "+param);
        } else {
            exceptionMap.put("message", "Missing or invalid parameter");
        }
        ProgressIndicator.setProgressBarInvisible();
        if (Attestr.getClientAppActivity() instanceof AttestrFlowXListener) {
            handler.post(() -> {
                mAttestrFlowXListener.onFlowXError(exceptionMap);
            });
        }
    }

    public static void handleInternalException(String exception) {
        String exp = exception;
        exceptionMap  = new HashMap<>();
        exceptionMap.put("code", "5001");
        exceptionMap.put("httpStatusCode", "500");
        exceptionMap.put("message", GlobalVariables.reqNotProcessed);
        GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_ERRORED;
        GlobalVariables.sessionErrorDataMap = new JSONObject(exceptionMap);
        CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(GlobalVariables.sessionErrorDataMap, GlobalVariables.NOTIFY_EVENT_ERRORED));
    }

    public static void handleTimeoutException() {
        exceptionMap = new HashMap<>();
        exceptionMap.put("code", "4081");
        exceptionMap.put("httpStatusCode", "408");
        exceptionMap.put("message", GlobalVariables.REQ_TIMEOUT);
        exceptionMap.put("stage", GlobalVariables.currentStageId);
        try {
            if (mCurrentActivity instanceof CameraActivity){
                ((CameraActivity) mCurrentActivity).onConnectionTimeOut();
            } else if (mCurrentActivity instanceof DigilockerAuthorizationActivity){
                ((DigilockerAuthorizationActivity) mCurrentActivity).onConnectionTimeOut();
            } else if (mCurrentActivity instanceof JobDetailsActivity){
                ((JobDetailsActivity) mCurrentActivity).onConnectionTimeOut();
            } else if (mCurrentActivity instanceof LaunchActivity){
                exitLaunchActivity();
            }
        } catch (Exception e){
            handleInternalException(e.toString());
        }
    }

    public static void handleCameraException(String message) {
        if (mActivity instanceof LaunchActivity) {
            ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, null));
        }
        exceptionMap = new HashMap<>();
        exceptionMap.put("code", "4121");
        exceptionMap.put("httpStatusCode", "412");
        exceptionMap.put("message", message);
        exceptionMap.put("stage", GlobalVariables.currentStageId);
        GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_ERRORED;
        GlobalVariables.sessionErrorDataMap = new JSONObject(exceptionMap);
        CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(GlobalVariables.sessionErrorDataMap, GlobalVariables.NOTIFY_EVENT_ERRORED));
    }

    public static void handleStoragePermissionException(String message) {
        if (mActivity instanceof LaunchActivity){
            ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, null));
        }
        exceptionMap = new HashMap<>();
        exceptionMap.put("code", "4122");
        exceptionMap.put("httpStatusCode", "412");
        exceptionMap.put("message", message);
        exceptionMap.put("stage", GlobalVariables.currentStageId);
        GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_ERRORED;
        GlobalVariables.sessionErrorDataMap = new JSONObject(exceptionMap);
        CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(GlobalVariables.sessionErrorDataMap, GlobalVariables.NOTIFY_EVENT_ERRORED));
    }

    public static void handleLocationException(String message){
        if (mActivity instanceof LaunchActivity){
            ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, null));
        }
        exceptionMap = new HashMap<>();
        exceptionMap.put("code", "4123");
        exceptionMap.put("httpStatusCode", "412");
        exceptionMap.put("message", message);
        exceptionMap.put("stage", GlobalVariables.currentStageId);
        GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_ERRORED;
        GlobalVariables.sessionErrorDataMap = new JSONObject(exceptionMap);
        CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(GlobalVariables.sessionErrorDataMap, GlobalVariables.NOTIFY_EVENT_ERRORED));
    }

    public static void handleSessionError(String response) {
        String res = response;
        try {
            JSONObject data = new JSONObject(response).getJSONObject("data");
            Map<String, Object> dataMap = CommonUtils.jsonToMap(response);
            GlobalVariables.currentEvent = GlobalVariables.NOTIFY_EVENT_ERRORED;
            GlobalVariables.currentDataMap = dataMap;
            if (dataMap.get("data") != null) {
                GlobalVariables.sessionErrorDataMap = data;
                CommonUtils.sendRequest(AttestrRequest.actionNotifyEvent(GlobalVariables.sessionErrorDataMap, GlobalVariables.NOTIFY_EVENT_ERRORED));
            }
        }catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }

    public static void exitLaunchActivity() {
        try {
            if (mActivity instanceof LaunchActivity){
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }
            if (Attestr.getClientAppActivity() instanceof AttestrFlowXListener) {
                handler.post(() -> {
                    mAttestrFlowXListener.onFlowXError(exceptionMap);
                });
            }
            CommonUtils.cleanUp();
        } catch (Exception e){
            HandleException.handleInternalException("Exit Launch Activity Exception: "+e.toString());
        }
    }

    public static void setCurrentActivity(Activity currentActivity){
        try {
            mCurrentActivity = currentActivity;
        } catch (Exception e){
            handleInternalException(e.toString());
        }
    }

}