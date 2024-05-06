package com.attestr.flowx.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class Attestr extends Application {

    private static Activity mActivity;

    public static Activity getClientAppActivity() {
        return mActivity;
    }

    public static void setClientAppActivity(Activity activity) {
        mActivity = activity;
    }

}
