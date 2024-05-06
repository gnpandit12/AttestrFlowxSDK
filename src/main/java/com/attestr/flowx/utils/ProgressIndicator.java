package com.attestr.flowx.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class ProgressIndicator {

    private static final String TAG = "ProgressIndicator";
    private static ProgressBar progressView;
    private static ViewGroup viewGroup;
    private static ViewGroup rootView;
    private static RelativeLayout progressBarLayout;
    private static RelativeLayout.LayoutParams params;
    private static Activity mActivity;
    
    public static void setActivity (Activity activity) {
        mActivity = activity;
        if (CommonUtils.isSmallDevice(activity)) {
            params = new RelativeLayout.LayoutParams(50, 50);
        }else {
            params = new RelativeLayout.LayoutParams(100, 100);
        }
        params = new RelativeLayout.LayoutParams(100, 100);
        progressBarLayout = new RelativeLayout(activity);
        progressView = new ProgressBar(activity, null, android.R.attr.progressBarStyleSmall);
        progressView.setIndeterminate(true);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBarLayout.addView(progressView, params);
        viewGroup = activity.findViewById(android.R.id.content);
        rootView = (ViewGroup) activity.getWindow().getDecorView().getRootView();
    }

    public static void setProgressBarVisible() {
        try {
            mActivity.runOnUiThread(() -> {
                viewGroup.addView(progressBarLayout);
                applyDim(rootView, 0.4f);
                progressView.setAlpha(1f);
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }

    public static void setProgressBarInvisible() {
        try {
            mActivity.runOnUiThread(() -> {
                viewGroup.removeView(progressBarLayout);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                clearDim(rootView);
            });
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }

    public static int getProgressViewChildCount() {
        int viewGroupChildCount = 0;
        try {
            viewGroupChildCount = viewGroup.getChildCount();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return viewGroupChildCount;
    }

    private static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
        int color = Color.parseColor("#000000");
        Drawable dim = new ColorDrawable(color);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    private static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }
    
}
