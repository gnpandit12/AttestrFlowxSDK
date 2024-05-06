package com.attestr.flowx.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.databinding.LocaleLayoutBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.LocaleAdapter;
import com.attestr.flowx.model.response.LocaleLanguagesData;
import com.attestr.flowx.utils.Attestr;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.OkHttpWebSocket;
import com.attestr.flowx.utils.ProgressIndicator;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class LocaleActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "LocaleActivity";
    private LocaleAdapter mLocaleAdapter;
    private ImageView continueImageView;
    private String selectedLocale;
    private boolean isFlowStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleLayoutBinding localeLayoutBinding = LocaleLayoutBinding.inflate(getLayoutInflater());
        setContentView(localeLayoutBinding.getRoot());
        try
        {
            if (this.getSupportActionBar() != null) {
                this.getSupportActionBar().hide();
            }
        }
        catch (Exception e){
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }

        ProgressIndicator.setActivity(this);
        continueImageView = localeLayoutBinding.continueArrowImageView;
        RecyclerView mLocaleRecyclerView = localeLayoutBinding.localeRecyclerView;
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        mLocaleRecyclerView.setLayoutManager(mGridLayoutManager);

        Intent i = this.getIntent();
        if (i != null) {
            LocaleLanguagesData[] localeLanguagesDataArray = (LocaleLanguagesData[]) i.getSerializableExtra("LocaleLanguagesDataArray");
            mLocaleAdapter = new LocaleAdapter(this, localeLanguagesDataArray);
            selectedLocale = localeLanguagesDataArray[0].getKey();
            mLocaleRecyclerView.setAdapter(mLocaleAdapter);
        }

        mLocaleAdapter.setOnItemClickListener(localeKey -> selectedLocale = localeKey);
        continueImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == continueImageView) {
            try {
                isFlowStarted = true;
                GlobalVariables.locale = selectedLocale;
                ProgressIndicator.setProgressBarVisible();
                continueImageView.setVisibility(View.GONE);
                OkHttpWebSocket okHttpWebSocket = new OkHttpWebSocket();
                okHttpWebSocket.setData(Attestr.getClientAppActivity());
                okHttpWebSocket.connect(AttestrRequest.actionInit());
                GlobalVariables.webSocket = okHttpWebSocket;
            } catch (Exception e) {
                HandleException.handleInternalException("Locale activity exception: "+ e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFlowStarted) {
            super.onBackPressed();
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    }

}