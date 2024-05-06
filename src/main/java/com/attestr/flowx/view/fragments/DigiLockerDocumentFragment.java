package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.BaseData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.StageReady;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 11/10/21
 **/
public class DigiLockerDocumentFragment extends Fragment implements View.OnClickListener {

    private ResponseData<StageInitData> stageReadyResponse;
    private static DigiLockerDocumentFragment digiLockerDocumentFragment;
    private TextView documentTitleTextView, issuerTitleTextView, dataErrorTextView;
    private JsonArray mParamsArray;
    private Map<String, String> paramsMap;
    private LinearLayout paramsLinearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private EditText placeholderEditText;
    private TextView labelTextView, inputErrorTextView;
    private LinearLayout linearLayout;
    private int blackColor;
    private String errorText = "Please fill in this field";
    private List<EditText> placeholderList = new ArrayList<>();
    private List<TextView> inputErrorTextViewList = new ArrayList<>();
    private LinearLayout digiDocSubmitLinearLayout;
    private ProgressBar progressBar;
    private TextView digiDocSubmitButtonTextView;
    private HashMap<String, String> requestMap = new HashMap<>();
    private int lastPlaceHolder;
    private int minHeight = 96;
    private int textSize = 16;
    private int dataErrorTextSize;
    private int placeholderLeftPadding = 20;
    private NestedScrollView nestedScrollView;
    private Activity mActivity;
    private String issuer, document, params;
    private int remainingRetryAttempts = 1;
    private DisplayMetrics displayMetrics;

    public static DigiLockerDocumentFragment getInstance(boolean newInstance) {
        if (newInstance){
            digiLockerDocumentFragment = new DigiLockerDocumentFragment();
        } else {
            if (digiLockerDocumentFragment == null) {
            digiLockerDocumentFragment = new DigiLockerDocumentFragment();
            }
        }
        return digiLockerDocumentFragment;
    }

    public void setData(
            Activity activity,
            JsonArray paramsArray,
            Map<String, String> params,
            boolean isTablet
    ){
        this.mActivity = activity;
        this.mParamsArray = paramsArray;
        this.paramsMap = params;
        lastPlaceHolder = paramsArray.size() - 1;
        if (isTablet) {
            displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            float scaleFactor = displayMetrics.density;
            float widthDp = widthPixels / scaleFactor;
            float heightDp = heightPixels / scaleFactor;
            float smallestWidth = Math.min(widthDp, heightDp);
            if (smallestWidth > 720) {
                //Device is a 10" tablet
                minHeight = 240;
                textSize = 26;
                dataErrorTextSize = 22;
                placeholderLeftPadding = 30;
            }
            else if (smallestWidth > 600) {
                //Device is a 7" tablet
                minHeight = 192;
                textSize = 26;
                dataErrorTextSize = 22;
                placeholderLeftPadding = 30;
            }
        } else {
            minHeight = 144;
            textSize = 16;
            dataErrorTextSize = 12;
            placeholderLeftPadding = 20;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digilocker_document_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stageReadyResponse = GlobalVariables.stageReadyResponse;

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,5,0,15);
        documentTitleTextView = view.findViewById(R.id.title_certificate);
        issuerTitleTextView = view.findViewById(R.id.title_issure);
        dataErrorTextView = view.findViewById(R.id.digi_doc_data_error_text_view);
        paramsLinearLayout = view.findViewById(R.id.params_linear_layout);
        digiDocSubmitLinearLayout = view.findViewById(R.id.digi_doc_submit_Relative_layout);
        CommonUtils.setButtonAlphaHigh(digiDocSubmitLinearLayout);
        progressBar = view.findViewById(R.id.digi_doc_submit_progress_bar);
        nestedScrollView = view.findViewById(R.id.digi_doc_nested_scroll_view);
        progressBar.setVisibility(View.GONE);
        digiDocSubmitButtonTextView = view.findViewById(R.id.digi_doc_submit_button);
        digiDocSubmitButtonTextView.setText(GlobalVariables.handshakeReadyResponse.getLocale().getDigilockerDocumentLocale().getButtonProceed());
        paramsLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dataErrorTextView.setVisibility(View.GONE);

        documentTitleTextView.setText(paramsMap.get("certificate"));
        issuerTitleTextView.setText(paramsMap.get("issuer"));
        linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            blackColor = requireActivity().getResources().getColor(android.R.color.black, null);
        }else {
            blackColor = requireActivity().getResources().getColor(android.R.color.black);
        }
        Typeface poppinsMedium = ResourcesCompat.getFont(mActivity, R.font.poppins_medium);
        for (int i = 0; i< mParamsArray.size(); i++){
            labelTextView = new TextView(getActivity());
            inputErrorTextView = new TextView(getActivity());
            placeholderEditText = new EditText(getActivity());
            placeholderEditText.setSingleLine(true);
            if (lastPlaceHolder == i) {
                placeholderEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
            } else {
                placeholderEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
            placeholderEditText.setTag(mParamsArray.get(i).getAsJsonObject().get("paramname").getAsString());
            inputErrorTextView.setTag(mParamsArray.get(i).getAsJsonObject().get("paramname").getAsString());
            labelTextView.setTextColor(blackColor);
            labelTextView.setTypeface(poppinsMedium);
            labelTextView.setTextSize(textSize);
            placeholderEditText.setTextSize(textSize);
            placeholderEditText.setTypeface(poppinsMedium);
            inputErrorTextView.setTextSize(textSize);
            inputErrorTextView.setTypeface(poppinsMedium);
            dataErrorTextView.setTypeface(poppinsMedium);
            labelTextView.setLayoutParams(layoutParams);
            placeholderEditText.setLayoutParams(layoutParams);
            inputErrorTextView.setLayoutParams(layoutParams);
            labelTextView.setText(mParamsArray.get(i).getAsJsonObject().get("label").getAsString());
            placeholderEditText.setHint(mParamsArray.get(i).getAsJsonObject().get("example").getAsString());
            inputErrorTextView.setText(errorText);
            inputErrorTextView.setTextColor(getResources().getColor(R.color.data_error));
            inputErrorTextView.setVisibility(View.INVISIBLE);
            placeholderEditText.setBackgroundResource(0);
            placeholderEditText.setBackgroundResource(R.drawable.edit_text_background);
            placeholderEditText.setPadding(placeholderLeftPadding,0,0,0);
            placeholderEditText.setMinHeight(minHeight);
            placeholderList.add(placeholderEditText);
            inputErrorTextViewList.add(inputErrorTextView);
            linearLayout.addView(labelTextView);
            linearLayout.addView(placeholderEditText);
            linearLayout.addView(inputErrorTextView);
        }
        paramsLinearLayout.setGravity(Gravity.TOP);
        paramsLinearLayout.addView(linearLayout);

        digiDocSubmitLinearLayout.setOnClickListener(this);
        StageReady stageReady = new StageReady();
        remainingRetryAttempts = stageReady.getRemainingRetryAttempts();

        placeholderList.get(lastPlaceHolder).setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                requestMap.clear();
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                sendRequest();
                return false;
            }
            return false;
        });

        issuer = stageReadyResponse.getData().getState().get("issuer");
        document = stageReadyResponse.getData().getState().get("document");
        params = stageReadyResponse.getData().getState().get("params");

        if (issuer != null){
            issuerTitleTextView.setText(issuer);
        }
        if (document != null){
            documentTitleTextView.setText(document);
        }

    }


    @Override
    public void onClick(View view) {
        if (view == digiDocSubmitLinearLayout){
            requestMap.clear();
            sendRequest();
        }
    }

    private void sendRequest() {
        dataErrorTextView.setVisibility(View.GONE);
        for (int i = 0; i< placeholderList.size(); i++){
            String value = placeholderList.get(i).getText().toString();
            String key = placeholderList.get(i).getTag().toString();
            if (!TextUtils.isEmpty(value)){
                if (key.equals(inputErrorTextViewList.get(i).getTag())){
                    placeholderList.get(i).setBackgroundResource(0);
                    placeholderList.get(i).setBackgroundResource(R.drawable.edit_text_background);
                    placeholderList.get(i).setMinHeight(minHeight);
                    inputErrorTextViewList.get(i).setVisibility(View.INVISIBLE);
                }
                requestMap.put(key, value);
            } else {
                if (key.equals(inputErrorTextViewList.get(i).getTag())){
                    inputErrorTextViewList.get(i).setVisibility(View.VISIBLE);
                }
                placeholderList.get(i).setBackgroundResource(0);
                placeholderList.get(i).setBackgroundResource(R.drawable.edit_text_error_background);
                placeholderList.get(i).setMinHeight(minHeight);
            }
            if (requestMap.size() == placeholderList.size()){
                progressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(digiDocSubmitLinearLayout);
                CommonUtils.sendRequest(AttestrRequest.actionDigiDocumentSubmit(requestMap));
            }
        }
    }

    public void OnDataError(String response) {
        if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("retryInvalid")) {
            if (remainingRetryAttempts == 0) {
                HandleException.handleSessionError(response);
            } else {
                ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>() {
                }.getType());
                dataErrorTextView.setText(dataError.getData().getError().getMessage());
                dataErrorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                CommonUtils.setButtonAlphaHigh(digiDocSubmitLinearLayout);
                nestedScrollView.post(() -> {
                    nestedScrollView.fling(0);
                    nestedScrollView.smoothScrollTo(0, 0);
                });
            }
            remainingRetryAttempts--;
        } else {
            HandleException.handleSessionError(response);
        }
    }

    public void OnSessionError(String response) {
        remainingRetryAttempts--;
        if ((int) remainingRetryAttempts == 0) {
            HandleException.handleSessionError(response);
        } else {
            ResponseData<BaseData> dataError = CommonUtils.gsonBuilder.create().fromJson(response, new TypeToken<ResponseData<BaseData>>(){}.getType());
            dataErrorTextView.setText(dataError.getData().getOriginal().getMessage());
            dataErrorTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            CommonUtils.setButtonAlphaHigh(digiDocSubmitLinearLayout);
            nestedScrollView.post(() -> {
                nestedScrollView.fling(0);
                nestedScrollView.smoothScrollTo(0,0);
            });
        }
    }


}
