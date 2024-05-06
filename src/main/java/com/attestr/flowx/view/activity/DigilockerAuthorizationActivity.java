package com.attestr.flowx.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.attestr.flowx.databinding.ActivityEaadhaarBinding;
import com.attestr.flowx.model.AttestrWebListener;
import com.attestr.flowx.listener.AttestrWebViewListener;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class DigilockerAuthorizationActivity extends AppCompatActivity implements AttestrWebViewListener {

    private ActivityEaadhaarBinding eaadhaarBinding;
    private WebView webView;
    private String digest;
    private String URL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandleException.setCurrentActivity(DigilockerAuthorizationActivity.this);
        try
        {
            if (this.getSupportActionBar() != null) {
                this.getSupportActionBar().hide();
            }
        }
        catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        eaadhaarBinding = ActivityEaadhaarBinding.inflate(getLayoutInflater());
        setContentView(eaadhaarBinding.getRoot());

        webView = eaadhaarBinding.webView;
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AttestrWebListener(this), "AttestrWebListener");

        Intent intent = this.getIntent();
        digest = intent.getStringExtra("digest_value");
        URL = GlobalVariables.DIGILOCKER_URL + "ext=Y" + "&hs="+ GlobalVariables.handshakeId+"&cl="+GlobalVariables.clientKey+"&lc="
                + GlobalVariables.locale+"&digest="+digest;
        webView.loadUrl(URL);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("timeout_exception", false);
        intent.putExtra("result", "OnBackPressed");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onSessionError(String response) {
        Intent intent = new Intent();
        intent.putExtra("timeout_exception", false);
        intent.putExtra("result", "Deny");
        intent.putExtra("session_error_response", response);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void onConnectionTimeOut(){
        Intent intent = new Intent();
        intent.putExtra("timeout_exception", true);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}