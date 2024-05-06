package com.attestr.flowx.utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.attestr.flowx.listener.NetworkChangeListener;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 15/02/22
 **/
public class AttestrApplication extends Application implements DefaultLifecycleObserver {

    private static final String TAG = "AttestrApplication";
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Activity mActivity;
    private NetworkChangeListener mNetworkChangeListener;
    private static AttestrApplication mAttestrApplication;
    private BroadcastReceiver broadcastReceiver;
    private boolean hasWifi, hasCellular;

    public AttestrApplication (){}

    public static AttestrApplication getInstance(boolean newInstance) {
        if (newInstance){
            mAttestrApplication = new AttestrApplication();
            return mAttestrApplication;
        } else {
            if (mAttestrApplication == null) {
                mAttestrApplication = new AttestrApplication();
            }
        }
        return mAttestrApplication;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build();

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    mNetworkChangeListener.onNetworkAvailable();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    mNetworkChangeListener.onNetworkLost();
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);

                    hasCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

                }
            };

            try {
                if (!Settings.System.canWrite(mActivity)) {
                    connectivityManager = mActivity.getSystemService(ConnectivityManager.class);
                    connectivityManager.requestNetwork(networkRequest, networkCallback);
                }
            } catch (Exception e) {
                HandleException.handleInternalException(e.toString());
            }
        } else {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo[] networks = connectivityManager.getAllNetworkInfo();
                    for(NetworkInfo networkInfo : networks) {
                        if (networkInfo.getTypeName().equals("MOBILE")) {
                            hasWifi = false;
                            hasCellular = true;
                            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                                mNetworkChangeListener.onNetworkAvailable();
                            } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                                mNetworkChangeListener.onNetworkLost();
                            }
                            break;
                        } else if (networkInfo.getTypeName().equals("WIFI")) {
                            hasWifi = false;
                            hasCellular = true;
                            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                                mNetworkChangeListener.onNetworkAvailable();
                            } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                                mNetworkChangeListener.onNetworkLost();
                            }
                            break;
                        }
                    }
                }
            };
            mActivity.registerReceiver(broadcastReceiver, filter);
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (connectivityManager != null){
                try {
                    connectivityManager.unregisterNetworkCallback(networkCallback);
                } catch (IllegalArgumentException exception) {
                    HandleException.handleInternalException(exception.toString());
                }
            }
        } else {
            if (mActivity != null && broadcastReceiver != null) {
                mActivity.unregisterReceiver(broadcastReceiver);
            }
        }
    }

    public void setNetworkChangeListener(NetworkChangeListener networkChangeListener) {
        this.mNetworkChangeListener = networkChangeListener;
    }

}
