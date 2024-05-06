package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.WorkVerificationFragmentBinding;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.EmploymentVerificationLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.view.activity.JobDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 20/12/21
 **/
public class WorkVerificationFragment extends Fragment implements View.OnClickListener {

    private static WorkVerificationFragment workVerificationFragment;
    private WorkVerificationFragmentBinding workVerificationFragmentBinding;
    private EmploymentVerificationLocale employmentVerificationLocale;
    private Activity mActivity;
    private TextView formTitleTextView, submitRecordButtonTextView, allowInternshipTextView;
    private ProgressBar submitRecordProgressBar;
    private ResponseData<StageInitData> stageReadyResponse;
    private String criteriaValue, references, criteria, formTitle;
    private boolean allowInternships, noExperience;
    private final String LAST_X_JOBS = "LAST_X_JOBS";
    private final String LAST_Y_YEARS = "LAST_Y_YEARS";
    private CheckBox noExperienceCheckBox;
    private ActivityResultLauncher<Intent> jobDetailsActivityResultLauncher;
    private LinearLayout jobDetailsCardLinearLayout;
    private LayoutInflater layoutInflater;
    private JSONArray mJobDetailsList;
    private LinearLayout.LayoutParams layoutParams;
    private int marginBottom = 30;
    private List<View> jobDetailsViewList;
    private List<HashMap<String, String>> documentsMapList;
    private LinearLayout addRecordSubmitButtonLinearLayout;
    private RelativeLayout addRecordButtonRelativeLayout;
    private LinearLayout workVerificationButtonLinearLayout;
    private LinearLayout submitRecordLinearLayout;
    private TextView workVerificationButtonTextView, addRecordButtonTextView;
    private ProgressBar workVerificationSubmitButtonProgressBar;

    public static WorkVerificationFragment getInstance(boolean newInstance) {
        if (newInstance){
            workVerificationFragment = new WorkVerificationFragment();
            return workVerificationFragment;
        } else {
            if (workVerificationFragment == null){
                workVerificationFragment = new WorkVerificationFragment();
            }
        }
        return workVerificationFragment;
    }

    public void setData(Activity activity,
                        boolean isTablet){
        this.mActivity = activity;
        if (isTablet){
            marginBottom = 50;
        } else {
            marginBottom = 30;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        workVerificationFragmentBinding = WorkVerificationFragmentBinding.inflate(inflater, container, false);
        return workVerificationFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        employmentVerificationLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEmploymentVerificationLocale();
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        layoutInflater = LayoutInflater.from(mActivity);
        jobDetailsViewList = new ArrayList<>();

        mJobDetailsList = new JSONArray();
        documentsMapList = new ArrayList<>();

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0, marginBottom);

        criteriaValue = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("criteriaValue")).toString();
        references = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("references")).toString();
        criteria = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("criteria")).toString();
        allowInternships = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("allowInternships")).toString());

        formTitle = employmentVerificationLocale.getFormTitle();
        String jobMultipleSuffix = employmentVerificationLocale.getFormTtileJobMultipleSuffix();
        String yearMultipleSuffix = employmentVerificationLocale.getFormTtileYearMultipleSuffix();
        String jobSingleSuffix = employmentVerificationLocale.getFormTtileJobSingleSuffix();
        String yearSingleSuffix = employmentVerificationLocale.getFormTtileYearSingleSuffix();

        double criteriaValueDouble = Double.parseDouble(criteriaValue);
        int criteriaValueInt = (int) criteriaValueDouble;

        switch (criteria) {
            case LAST_X_JOBS:
                if (Double.parseDouble(criteriaValue) > 1) {
                    jobMultipleSuffix = jobMultipleSuffix.replace("{0}", String.valueOf(criteriaValueInt));
                    formTitle = formTitle.replace("{0}", jobMultipleSuffix);
                } else {
                    formTitle = formTitle.replace("{0}", jobSingleSuffix);
                }
                break;
            case LAST_Y_YEARS:
                if (Double.parseDouble(criteriaValue) > 1) {
                    yearMultipleSuffix = yearMultipleSuffix.replace("{0}", String.valueOf(criteriaValueInt));
                    formTitle = formTitle.replace("{0}", yearMultipleSuffix);
                } else {
                    formTitle = formTitle.replace("{0}", yearSingleSuffix);
                }
                break;
        }

        formTitleTextView = workVerificationFragmentBinding.formTitleTextView;
        formTitleTextView.setText(formTitle);
        submitRecordButtonTextView = workVerificationFragmentBinding.workVerificationSubmitButtonTextView;
        addRecordSubmitButtonLinearLayout = workVerificationFragmentBinding.addRecordSubmitButtonLinearLayout;
        addRecordSubmitButtonLinearLayout.setVisibility(View.GONE);
        addRecordButtonRelativeLayout = workVerificationFragmentBinding.addRecordRelativeLayout;
        workVerificationButtonLinearLayout = workVerificationFragmentBinding.submitAddRecordButtonRelativeLayout;
        workVerificationButtonLinearLayout.setVisibility(View.VISIBLE);
        workVerificationButtonTextView = workVerificationFragmentBinding.addRecordSubmitButtonTextView;
        workVerificationSubmitButtonProgressBar = workVerificationFragmentBinding.addRecordSubmitSubmitButtonProgressBar;
        addRecordButtonTextView = workVerificationFragmentBinding.addRecordButtonTextView;
        submitRecordLinearLayout = workVerificationFragmentBinding.workVerificationSubmitRelativeLayout;
        submitRecordProgressBar = workVerificationFragmentBinding.workVerificationSubmitProgressBar;
        noExperienceCheckBox = workVerificationFragmentBinding.fieldNoExperienceCheckbox;
        allowInternshipTextView = workVerificationFragmentBinding.allowIntershipTextView;
        jobDetailsCardLinearLayout = workVerificationFragmentBinding.jobDetailsCardLinearLayout;
        workVerificationSubmitButtonProgressBar.setVisibility(View.GONE);
        jobDetailsCardLinearLayout.setVisibility(View.GONE);
        allowInternshipTextView.setVisibility(View.GONE);

        noExperienceCheckBox.setText(employmentVerificationLocale.getFieldNoExperience());
        submitRecordButtonTextView.setText(employmentVerificationLocale.getButtonSubmit());
        allowInternshipTextView.setText(employmentVerificationLocale.getFormInternshipTitle());
        addRecordButtonTextView.setText(employmentVerificationLocale.getFormButtonAddRecord());


        workVerificationButtonLinearLayout.setTag("ADD_RECORD");
        workVerificationButtonTextView.setText(employmentVerificationLocale.getFormButtonAddRecord());

        if (allowInternships) {
            allowInternshipTextView.setVisibility(View.VISIBLE);
        } else {
            allowInternshipTextView.setVisibility(View.GONE);
        }

        submitRecordProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(submitRecordLinearLayout);
        submitRecordLinearLayout.setClickable(true);

        noExperienceCheckBox.setOnClickListener(v -> {
            if (noExperienceCheckBox.isChecked()){
                noExperience = true;
                workVerificationButtonLinearLayout.setTag("SUBMIT");
                workVerificationButtonTextView.setText(employmentVerificationLocale.getButtonSubmit());
            } else {
                noExperience = false;
                workVerificationButtonLinearLayout.setTag("ADD_RECORD");
                workVerificationButtonTextView.setText(employmentVerificationLocale.getFormButtonAddRecord());
            }
        });

        submitRecordLinearLayout.setOnClickListener(this);
        workVerificationButtonLinearLayout.setOnClickListener(this);
        addRecordButtonRelativeLayout.setOnClickListener(this);

        jobDetailsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            if (result.getData() != null){
                                try {
                                    if (result.getData().getStringExtra("RESULT").equals("RESULT_OK")){
                                        String jobDetails = result.getData().getStringExtra("JOB_DETAILS_OBJECT");
                                        String jobDetailsIndex = result.getData().getStringExtra("JOB_DETAILS_INDEX");
                                        String references = result.getData().getStringExtra("REFERENCES");
                                        HashMap<String, String> documentsMap = (HashMap<String, String>) result.getData().getSerializableExtra("DOCUMENTS_MAP");
                                        if (jobDetailsIndex != null){
                                            int index = Integer.parseInt(jobDetailsIndex);
                                            try {
                                                mJobDetailsList.put(index, new JSONObject(jobDetails));
                                                documentsMapList.set(index, documentsMap);
                                            } catch (JSONException e) {
                                                HandleException.handleInternalException(e.toString());
                                            }
                                        } else {
                                            mJobDetailsList.put(new JSONObject(jobDetails));
                                            documentsMapList.add(documentsMap);
                                        }
                                        onJobDetailsAdded(mJobDetailsList, documentsMapList, references);
                                    }
                                } catch (Exception e){
                                    HandleException.handleInternalException(e.toString());
                                }
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            try {
                                HandleException.setCurrentActivity(mActivity);
                                if (result.getData() != null){
                                    switch (result.getData().getStringExtra("RESULT")){
                                        case "CANCELED":
                                            break;
                                        case "CONNECTION_TIMEOUT_EXCEPTION":
                                            HandleException.exitLaunchActivity();
                                            break;
                                        case "INTERNAL_EXCEPTION":
                                            HandleException.handleInternalException(result.getData().getStringExtra("EXCEPTION"));
                                            break;
                                    }
                                }
                            } catch (Exception e){
                                HandleException.handleInternalException(e.toString());
                            }
                            break;
                    }
                });

    }

    private void onJobDetailsAdded(JSONArray mJobDetailsList,
                                   List<HashMap<String, String>> documentsMapList,
                                   String references) {
        try {
            if (mJobDetailsList.length() != 0){
                View jobDetailsView;
                jobDetailsCardLinearLayout.setVisibility(View.VISIBLE);
                jobDetailsCardLinearLayout.removeAllViews();
                for (int i = 0; i < mJobDetailsList.length(); i ++ ){
                    try {
                        jobDetailsView = layoutInflater.inflate(R.layout.job_details_card_layout, null);
                        jobDetailsView.setLayoutParams(layoutParams);
                        JSONObject jobDetails = mJobDetailsList.getJSONObject(i);
                        String employer = jobDetails.get("employer").toString();
                        String startDate = jobDetails.get("startDate").toString();
                        String endDate = jobDetails.get("endDate").toString();

                        TextView companyNameTextView = jobDetailsView.findViewById(R.id.card_company_name_text_view);
                        TextView startDateTextView = jobDetailsView.findViewById(R.id.card_start_date_text_view);
                        TextView endDateTextView = jobDetailsView.findViewById(R.id.card_end_date_text_view);

                        companyNameTextView.setText(employer);
                        startDateTextView.setText(startDate);
                        if (!endDate.equals("null")){
                            endDateTextView.setText(endDate);
                        } else {
                            endDateTextView.setText(employmentVerificationLocale.getFormLabelPresent());
                        }
                        jobDetailsCardLinearLayout.addView(jobDetailsView);
                        jobDetailsViewList.add(jobDetailsView);
                        int finalI = i;
                        jobDetailsView.setOnClickListener(v -> {
                            try {
                                Intent intent = new Intent(mActivity, JobDetailsActivity.class);
                                intent.putExtra("JOB_DETAILS_INDEX", String.valueOf(finalI));
                                intent.putExtra("JOB_DETAILS", mJobDetailsList.getJSONObject(finalI).toString());
                                intent.putExtra("DOCUMENTS_MAP", documentsMapList.get(finalI));
                                intent.putExtra("REFERENCES", references);
                                jobDetailsActivityResultLauncher.launch(intent);
                            } catch (Exception e){
                                HandleException.handleInternalException(e.toString());
                            }
                        });
                    } catch (JSONException e) {
                        HandleException.handleInternalException(e.toString());
                    }
                }
                noExperienceCheckBox.setVisibility(View.GONE);
                workVerificationButtonLinearLayout.setVisibility(View.GONE);
                addRecordSubmitButtonLinearLayout.setVisibility(View.VISIBLE);
            } else {
                noExperienceCheckBox.setVisibility(View.VISIBLE);
                jobDetailsCardLinearLayout.setVisibility(View.GONE);
                workVerificationButtonLinearLayout.setVisibility(View.VISIBLE);
                addRecordSubmitButtonLinearLayout.setVisibility(View.GONE);
                workVerificationButtonLinearLayout.setTag("ADD_RECORD");
                workVerificationButtonTextView.setText(employmentVerificationLocale.getFormButtonAddRecord());
            }
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        workVerificationSubmitButtonProgressBar.setVisibility(View.GONE);
        CommonUtils.setButtonAlphaHigh(workVerificationButtonLinearLayout);
        workVerificationButtonLinearLayout.setClickable(true);
    }

    @Override
    public void onClick(View v) {
        if (v == workVerificationButtonLinearLayout){
            if (workVerificationButtonLinearLayout.getTag().equals("ADD_RECORD")){
                addRecord();
            } else if (workVerificationButtonLinearLayout.getTag().equals("SUBMIT")){
                workVerificationSubmitButtonProgressBar.setVisibility(View.VISIBLE);
                CommonUtils.setButtonAlphaLow(workVerificationButtonLinearLayout);
                workVerificationButtonLinearLayout.setClickable(false);
                submitRecord();
            }
        } else if (v == addRecordButtonRelativeLayout){
            addRecord();
        } else if (v == submitRecordLinearLayout){
            submitRecordProgressBar.setVisibility(View.VISIBLE);
            CommonUtils.setButtonAlphaLow(submitRecordLinearLayout);
            submitRecordLinearLayout.setClickable(false);
            submitRecord();
        }
    }

    private void addRecord() {
        try {
            Intent intent = new Intent(mActivity, JobDetailsActivity.class);
            jobDetailsActivityResultLauncher.launch(intent);
        } catch (Exception e){
            HandleException.handleInternalException("Add Fragment Job Details Exception: "+e.toString());
        }
    }

    private void submitRecord(){
        if (noExperience){
            CommonUtils.sendRequest(AttestrRequest.actionEmploymentSubmit(new JSONArray()));
        } else {
            if (mJobDetailsList.length() != 0){
                submitRecordProgressBar.setVisibility(View.VISIBLE);
                submitRecordLinearLayout.setClickable(false);
                CommonUtils.setButtonAlphaLow(submitRecordLinearLayout);
                try {
                    CommonUtils.sendRequest(AttestrRequest.actionEmploymentSubmit(mJobDetailsList));
                } catch (Exception e){
                    HandleException.handleInternalException(e.toString());
                }
            }
        }
    }

}
