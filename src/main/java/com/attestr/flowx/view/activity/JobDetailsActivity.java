package com.attestr.flowx.view.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.databinding.ActivityJobDetailsBinding;
import com.attestr.flowx.listener.ImageSizeExceededListener;
import com.attestr.flowx.listener.MediaHandshakeListener;
import com.attestr.flowx.listener.MediaImagesListener;
import com.attestr.flowx.listener.PermissionsListener;
import com.attestr.flowx.model.ApiRequest;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.MediaDataClass;
import com.attestr.flowx.model.MediaRecyclerViewAdapter;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.EmploymentVerificationLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.PermissionsHandler;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.utils.validators.InputTextValidator;
import com.attestr.flowx.utils.validators.UserInputValidation;
import com.attestr.flowx.view.fragments.EducationVerificationFragment;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class JobDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        PermissionsListener, ImageSizeExceededListener,
        MediaHandshakeListener, MediaImagesListener {

    private Activity mActivity;
    private ActivityJobDetailsBinding jobDetailsBinding;
    private EmploymentVerificationLocale employmentVerificationLocale;
    private ResponseData<StageInitData> stageReadyResponse;
    private HandshakeReadyData handshakeReadyResponse;
    private TextView fieldEmployerLabel, fieldEmployerDataError, fieldEmployerIDLabel,
            fieldEmployerIdDataError, fieldPositionLabel, fieldPositionDataError,
            fieldStartDateLabel, fieldStartDateDataError, fieldEndDateLabel, fieldEndDateDataError,
            fieldReferenceLabel, fieldDocumentLabel, fieldDocumentDataError,
            submitButtonTextView, jobDetailsTitleTextView;
    private EditText fieldEmployerPlaceholder, fieldEmployerIDPlaceholder, fieldPositionPlaceholder,
            fieldStartDatePlaceholder, fieldEndDatePlaceholder;
    private LinearLayout submitButtonRelativeLayout;
    private ProgressBar submitProgressBar;
    private LinearLayout referencesLinearLayout;
    private boolean isEmployerValid, isEmployerIdValid, isPositionValid, isStartDateValid,
            isEndDateValid, allowInternships;
    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private Calendar calendar;
    private String dateFormat = "dd-MM-yyyy";
    private SimpleDateFormat simpleDateFormat;
    private CheckBox fieldProgressCheckBox;
    private String criteriaValue, references, criteria, referenceLabelString;
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout coordinatorLayout;
    private View bottomSheet;
    private boolean isBottomSheetExpanded = false;
    private int marginBottom = 10;
    private LinearLayout.LayoutParams layoutParams;
    private ArrayList<String> documentMediaIDList;
    private LinearLayout backgroundLinearLayout;
    private NestedScrollView nestedScrollView;
    private PermissionsHandler permissionsHandler;
    private LayoutInflater layoutInflater;
    private View referencesView;
    private List<View> referencesViewList;
    private boolean isReferenceNameValid, isReferencePositionValid, isReferenceEmailValid, isReferenceContactValid;
    private LinearLayout endDateLinearLayout;
    private boolean isEmploymentInProgress = false;
    private List<List<EditText>> referencePlaceholderList;
    private List<List<TextView>> referenceDataErrorList;
    private ImageView exitJobDetailsImageView;
    private String employer, reg, position, ended, startDate, endDate, jobDetailsIndex;
    private JSONArray documentsJSONArray, referencesJSONArray;
    private HashMap<String, String> documentsMap;
    private Date start_date, end_date, currentDate;
    private LinearLayout referencesLabelLinearLayout;
    private List<Integer> integerList;
    private List<Integer> placeholderList;
    private int referencesIntValue;
    private String endDateGreaterThanStartDateError, endDateDataError;
    private ActivityResultLauncher<Intent> storageResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    private RecyclerView workVerificationRecyclerView;
    private MediaRecyclerViewAdapter mediaRecyclerViewAdapter;
    private ArrayList<MediaDataClass> imagesArrayList;
    private byte[] selectedFileByteArray;
    private byte[] capturedImageByteArray;
    private File selectedDocumentFile;
    private ImageView documentLabelImageView;
    private TextView filePickerCameraTextView, filePickerGalleryTextView, filePickerChooseFileTextView;
    private CommonLocale commonLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandleException.setCurrentActivity(JobDetailsActivity.this);
        jobDetailsBinding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        setContentView(jobDetailsBinding.getRoot());
        mActivity = this;

        try {
            ApiRequest.getInstance(false).setCurrentActivity(JobDetailsActivity.this);
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
            currentDate = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
        } catch (ParseException e) {
            handleInternalException(e);
        }

        if (CommonUtils.isTablet(this)){
            marginBottom = 40;
        } else {
            marginBottom = 25;
        }
        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();

        handshakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        employmentVerificationLocale = handshakeReadyResponse.getLocale().getEmploymentVerificationLocale();
        stageReadyResponse = GlobalVariables.stageReadyResponse;

        layoutInflater = LayoutInflater.from(this);
        referencePlaceholderList = new ArrayList<>();
        referencesViewList = new ArrayList<>();
        imagesArrayList = new ArrayList<>(10);
        documentMediaIDList = new ArrayList<>(10);
        documentsMap = new HashMap<>();
        referenceDataErrorList = new ArrayList<>();
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0, marginBottom);

        criteriaValue = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("criteriaValue")).toString();
        references = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("references")).toString();
        criteria = Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("criteria")).toString();
        allowInternships = Boolean.parseBoolean(Objects.requireNonNull(stageReadyResponse.getData().getMetadata().get("allowInternships")).toString());
        endDateGreaterThanStartDateError = employmentVerificationLocale.getFieldEndGreaterStartDataerror();
        endDateDataError = employmentVerificationLocale.getFieldEndDataerror();

        submitProgressBar = jobDetailsBinding.jobDetailsSubmitProgressBar;
        submitProgressBar.setVisibility(View.GONE);
        submitButtonRelativeLayout = jobDetailsBinding.jobDetailsSubmitRelativeLayout;
        CommonUtils.setButtonAlphaHigh(submitButtonRelativeLayout);
        submitButtonRelativeLayout.setClickable(true);
        fieldEmployerLabel = jobDetailsBinding.fieldEmployerLabel;
        fieldEmployerDataError = jobDetailsBinding.fieldEmployerDataError;
        fieldEmployerIDLabel = jobDetailsBinding.fieldWorkRegLabel;
        fieldEmployerIdDataError = jobDetailsBinding.fieldWorkRegDateError;
        fieldPositionLabel = jobDetailsBinding.fieldPositionLabel;
        fieldPositionDataError = jobDetailsBinding.fieldPositionDataError;
        fieldStartDateLabel = jobDetailsBinding.fieldStartDateLabel;
        fieldStartDateDataError = jobDetailsBinding.fieldStartDateDataError;
        fieldEndDateLabel = jobDetailsBinding.fieldEndDateLabel;
        fieldEndDateDataError = jobDetailsBinding.fieldEndDateDataError;
        fieldReferenceLabel = jobDetailsBinding.fieldReferencesLabel;
        fieldDocumentLabel = jobDetailsBinding.workVerificationFieldDocumentLabel;
        fieldDocumentDataError = jobDetailsBinding.workVerificationUploadDocumentDataError;
        submitButtonTextView = jobDetailsBinding.jobDetailsSubmitButtonTextView;
        fieldEmployerPlaceholder = jobDetailsBinding.fieldEmployerPlaceholder;
        fieldEmployerIDPlaceholder = jobDetailsBinding.fieldWorkRegPlaceholder;
        fieldPositionPlaceholder = jobDetailsBinding.fieldPositionPlaceholder;
        fieldStartDatePlaceholder = jobDetailsBinding.fieldStartDatePlaceholder;
        fieldEndDatePlaceholder = jobDetailsBinding.fieldEndDatePlaceholder;
        referencesLinearLayout = jobDetailsBinding.referencesLinearLayout;
        fieldProgressCheckBox = jobDetailsBinding.fieldProgressCheckbox;
        backgroundLinearLayout = jobDetailsBinding.jobDetailsBackgroundLinearLayout;
        nestedScrollView = jobDetailsBinding.jobDetailsNestedScrollView;
        endDateLinearLayout = jobDetailsBinding.jobDetailsEndDataLinearLayout;
        exitJobDetailsImageView = jobDetailsBinding.exitJobDetailsImageView;
        jobDetailsTitleTextView = jobDetailsBinding.jobDetailsTitleTextView;
        referencesLabelLinearLayout = jobDetailsBinding.referencesLabelLinearLayout;
        referencesLabelLinearLayout.setVisibility(View.VISIBLE);
        endDateLinearLayout.setVisibility(View.VISIBLE);
        workVerificationRecyclerView = jobDetailsBinding.workVerificationRecyclerView;
        documentLabelImageView = jobDetailsBinding.workVerificationDocumentImageView;
        documentLabelImageView.setBackgroundResource(R.drawable.id_card);
        documentLabelImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

        nestedScrollView.smoothScrollTo(0,0);

        fieldEmployerDataError.setVisibility(View.GONE);
        fieldEmployerIdDataError.setVisibility(View.GONE);
        fieldPositionDataError.setVisibility(View.GONE);
        fieldStartDateDataError.setVisibility(View.GONE);
        fieldEndDateDataError.setVisibility(View.GONE);
        fieldDocumentDataError.setVisibility(View.GONE);

        fieldEmployerDataError.setText(employmentVerificationLocale.getFieldEmployerDataerror());
        fieldEmployerIdDataError.setText(employmentVerificationLocale.getFieldRegDataerror());
        fieldPositionDataError.setText(employmentVerificationLocale.getFieldPositionDataerror());
        fieldStartDateDataError.setText(employmentVerificationLocale.getFieldStartDataerror());
        fieldEndDateDataError.setText(endDateDataError);
        fieldDocumentDataError.setText(employmentVerificationLocale.getFieldDocumentsDataerror());
        fieldProgressCheckBox.setText(employmentVerificationLocale.getFieldProgressLabel());


        fieldEmployerLabel.setText(employmentVerificationLocale.getFieldEmployerLabel());
        fieldEmployerIDLabel.setText(employmentVerificationLocale.getFieldRegLabel());
        fieldPositionLabel.setText(employmentVerificationLocale.getFieldPositionLabel());
        fieldStartDateLabel.setText(employmentVerificationLocale.getFieldStartLabel());
        fieldEndDateLabel.setText(employmentVerificationLocale.getFieldEndLabel());
        fieldDocumentLabel.setText(employmentVerificationLocale.getFieldDocumentsLabel());
        submitButtonTextView.setText(employmentVerificationLocale.getButtonSubmit());

        fieldEmployerPlaceholder.setHint(employmentVerificationLocale.getFieldEmployerPlaceholder());
        fieldEmployerIDPlaceholder.setHint(employmentVerificationLocale.getFieldRegPlaceholder());
        fieldPositionPlaceholder.setHint(employmentVerificationLocale.getFieldPositionPlaceholder());
        fieldStartDatePlaceholder.setHint(employmentVerificationLocale.getFieldStartPlaceholder());
        fieldEndDatePlaceholder.setHint(employmentVerificationLocale.getFieldEndPlaceholder());

        fieldEmployerPlaceholder.setBackgroundResource(0);
        fieldEmployerPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        fieldEmployerIDPlaceholder.setBackgroundResource(0);
        fieldEmployerIDPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        fieldPositionPlaceholder.setBackgroundResource(0);
        fieldPositionPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        fieldStartDatePlaceholder.setBackgroundResource(0);
        fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        fieldEndDatePlaceholder.setBackgroundResource(0);
        fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);

        Intent intent = this.getIntent();
        if (intent != null){
            try {
                String jobDetails = intent.getStringExtra("JOB_DETAILS");
                if (jobDetails != null){
                    jobDetailsIndex = intent.getStringExtra("JOB_DETAILS_INDEX");
                    String references = intent.getStringExtra("REFERENCES");
                    JSONObject jobDetailsObject = new JSONObject(jobDetails);
                    employer = jobDetailsObject.get("employer").toString();
                    reg = jobDetailsObject.get("reg").toString();
                    position = jobDetailsObject.get("position").toString();
                    startDate = jobDetailsObject.get("startDate").toString();
                    ended = jobDetailsObject.get("ended").toString();
                    endDate = jobDetailsObject.get("endDate").toString();
                    documentsJSONArray = jobDetailsObject.getJSONArray("documents");
                    HashMap<String, String> documentsHashMap = (HashMap<String, String>) intent.getSerializableExtra("DOCUMENTS_MAP");
                    for (int i = 0; i < documentsJSONArray.length(); i ++){
                        String mediaID = documentsJSONArray.getString(i);
                        String documentName = documentsHashMap.get(mediaID);
                        onDocumentUploaded(mediaID, documentName, null);
                    }

                    if (Integer.parseInt(references) != 0){
                        referencesJSONArray =  jobDetailsObject.getJSONArray("references");
                    }
                }
            } catch (Exception e){
                handleInternalException(e);
            }
        }

        double referencesDouble = Double.parseDouble(references);
        referencesIntValue = (int) referencesDouble;
        if (referencesIntValue != 0) {
            for (int i = 0; i < referencesIntValue; i++) {
                ImageView toggleReferenceVisibilityImageView;
                LinearLayout referenceViewBodyLinearLayout;
                RelativeLayout toggleReferenceVisibilityRelativeLayout;
                referencesView = layoutInflater.inflate(R.layout.employment_references_fragment, null);
                referencesView.setLayoutParams(layoutParams);
                referencesLinearLayout.addView(referencesView);
                toggleReferenceVisibilityRelativeLayout = referencesView.findViewById(R.id.reference_visibility_toggle_relative_layout);
                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(0);
                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(R.drawable.edit_text_background);
                toggleReferenceVisibilityImageView = referencesView.findViewById(R.id.reference_visibility_toggle_image_view);
                referenceViewBodyLinearLayout = referencesView.findViewById(R.id.reference_view_body_linear_layout);
                referenceViewBodyLinearLayout.setVisibility(View.GONE);
                toggleReferenceVisibilityImageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_down_24));
                referencesViewList.add(referencesView);
            }

            for (int i = 0; i < referencesViewList.size(); i++) {
                ImageView toggleReferenceVisibilityImageView;
                LinearLayout referenceViewBodyLinearLayout;
                EditText referenceNamePlaceholder, referencePositionPlaceholder, referenceEmailPlaceholder,
                        referenceContactPlaceholder;
                TextView referenceLabel, referenceNameLabel, referencePositionLabel, referenceEmailLabel,
                        referenceContactLabel, referenceNameDataError, referencePositionDataError, referenceEmailDataError,
                        referenceContactDataError;

                List<EditText> placeholderList = new ArrayList<>();
                List<TextView> dataErrorList = new ArrayList<>();

                toggleReferenceVisibilityImageView = referencesViewList.get(i).findViewById(R.id.reference_visibility_toggle_image_view);
                referenceViewBodyLinearLayout = referencesViewList.get(i).findViewById(R.id.reference_view_body_linear_layout);
                toggleReferenceVisibilityImageView.setOnClickListener(v -> {
                    if (referenceViewBodyLinearLayout.getVisibility() == View.GONE) {
                        referenceViewBodyLinearLayout.setVisibility(View.VISIBLE);
                        toggleReferenceVisibilityImageView.setImageDrawable(AppCompatResources.getDrawable(mActivity, R.drawable.ic_baseline_keyboard_arrow_up_24));
                    } else if (referenceViewBodyLinearLayout.getVisibility() == View.VISIBLE) {
                        referenceViewBodyLinearLayout.setVisibility(View.GONE);
                        toggleReferenceVisibilityImageView.setImageDrawable(AppCompatResources.getDrawable(mActivity, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                });

                String label = employmentVerificationLocale.getReferenceLabel() + (i + 1);
                referenceLabel = referencesViewList.get(i).findViewById(R.id.reference_label);
                referenceLabel.setText(label);

                referenceNameLabel = referencesViewList.get(i).findViewById(R.id.field_reference_name_label);
                referenceNameLabel.setText(employmentVerificationLocale.getFieldReferenceNameLabel());
                referenceNamePlaceholder = referencesViewList.get(i).findViewById(R.id.field_reference_name_placeholder);
                referenceNamePlaceholder.setHint(employmentVerificationLocale.getFieldReferenceNamePlaceholder());
                referenceNameDataError = referencesViewList.get(i).findViewById(R.id.field_reference_name_data_error);
                referenceNameDataError.setText(employmentVerificationLocale.getFieldReferenceNameDataerror());
                referenceNameDataError.setVisibility(View.GONE);
                referenceNamePlaceholder.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                referenceNamePlaceholder.addTextChangedListener(new InputTextValidator(referenceNamePlaceholder) {
                    @Override
                    public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                        isReferenceNameValid = UserInputValidation.isStringValid(s.toString());
                        if (isReferenceNameValid) {
                            referenceNamePlaceholder.setBackgroundResource(0);
                            referenceNamePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            referenceNameDataError.setVisibility(View.GONE);
                        } else {
                            referenceNamePlaceholder.setBackgroundResource(0);
                            referenceNamePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                            submitProgressBar.setVisibility(View.GONE);
                            submitButtonRelativeLayout.setClickable(true);
                            referenceNameDataError.setVisibility(View.VISIBLE);
                        }
                    }
                });
                placeholderList.add(referenceNamePlaceholder);
                dataErrorList.add(referenceNameDataError);

                referencePositionLabel = referencesViewList.get(i).findViewById(R.id.field_reference_position_label);
                referencePositionLabel.setText(employmentVerificationLocale.getFieldReferencePositionLabel());
                referencePositionPlaceholder = referencesViewList.get(i).findViewById(R.id.field_reference_position_placeholder);
                referencePositionPlaceholder.setHint(employmentVerificationLocale.getFieldReferencePositionPlaceholder());
                referencePositionDataError = referencesViewList.get(i).findViewById(R.id.field_reference_position_data_error);
                referencePositionDataError.setText(employmentVerificationLocale.getFieldReferencePositionDataerror());
                referencePositionDataError.setVisibility(View.GONE);
                referencePositionPlaceholder.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                referencePositionPlaceholder.addTextChangedListener(new InputTextValidator(referencePositionPlaceholder) {
                    @Override
                    public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                        isReferencePositionValid = UserInputValidation.isStringValid(s.toString());
                        if (isReferencePositionValid) {
                            referencePositionPlaceholder.setBackgroundResource(0);
                            referencePositionPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            referencePositionDataError.setVisibility(View.GONE);
                        } else {
                            referencePositionPlaceholder.setBackgroundResource(0);
                            referencePositionPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                            submitProgressBar.setVisibility(View.GONE);
                            submitButtonRelativeLayout.setClickable(true);
                            referencePositionDataError.setVisibility(View.VISIBLE);
                        }
                    }
                });

                placeholderList.add(referencePositionPlaceholder);
                dataErrorList.add(referencePositionDataError);

                referenceEmailLabel = referencesViewList.get(i).findViewById(R.id.field_reference_email_label);
                referenceEmailLabel.setText(employmentVerificationLocale.getFieldReferenceEmailLabel());
                referenceEmailPlaceholder = referencesViewList.get(i).findViewById(R.id.field_reference_email_placeholder);
                referenceEmailPlaceholder.setHint(employmentVerificationLocale.getFieldReferenceEmailPlaceholder());
                referenceEmailDataError = referencesViewList.get(i).findViewById(R.id.field_reference_email_data_error);
                referenceEmailDataError.setText(employmentVerificationLocale.getFieldReferenceEmailDataerror());
                referenceEmailDataError.setVisibility(View.GONE);
                referenceEmailPlaceholder.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                referenceEmailPlaceholder.addTextChangedListener(new InputTextValidator(referenceEmailPlaceholder) {
                    @Override
                    public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                        if (count != 0) {
                            isReferenceEmailValid = UserInputValidation.validateEmailID(s.toString());
                            if (isReferenceEmailValid) {
                                referenceEmailPlaceholder.setBackgroundResource(0);
                                referenceEmailPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                                referenceEmailDataError.setVisibility(View.GONE);
                            } else {
                                referenceEmailPlaceholder.setBackgroundResource(0);
                                referenceEmailPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                                submitProgressBar.setVisibility(View.GONE);
                                submitButtonRelativeLayout.setClickable(true);
                                referenceEmailDataError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            referenceEmailPlaceholder.setBackgroundResource(0);
                            referenceEmailPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            referenceEmailDataError.setVisibility(View.GONE);
                        }
                    }
                });

                placeholderList.add(referenceEmailPlaceholder);
                dataErrorList.add(referenceEmailDataError);

                referenceContactLabel = referencesViewList.get(i).findViewById(R.id.field_reference_contact_label);
                referenceContactLabel.setText(employmentVerificationLocale.getFieldReferenceContactLabel());
                referenceContactPlaceholder = referencesViewList.get(i).findViewById(R.id.field_reference_contact_placeholder);
                referenceContactPlaceholder.setHint(employmentVerificationLocale.getFieldReferenceContactPlaceholder());
                referenceContactDataError = referencesViewList.get(i).findViewById(R.id.field_reference_contact_data_error);
                referenceContactDataError.setText(employmentVerificationLocale.getFieldReferenceContactDataerror());
                referenceContactDataError.setVisibility(View.GONE);
                referenceContactPlaceholder.setImeOptions(EditorInfo.IME_ACTION_DONE);

                referenceContactPlaceholder.addTextChangedListener(new InputTextValidator(referenceContactPlaceholder) {
                    @Override
                    public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                        isReferenceContactValid = UserInputValidation.isStringValid(s.toString());
                        if (isReferenceContactValid) {
                            referenceContactPlaceholder.setBackgroundResource(0);
                            referenceContactPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                            referenceContactDataError.setVisibility(View.GONE);
                        } else {
                            referenceContactPlaceholder.setBackgroundResource(0);
                            referenceContactPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                            submitProgressBar.setVisibility(View.GONE);
                            submitButtonRelativeLayout.setClickable(true);
                            referenceContactDataError.setVisibility(View.VISIBLE);
                        }
                    }
                });
                dataErrorList.add(referenceContactDataError);
                placeholderList.add(referenceContactPlaceholder);
                referencePlaceholderList.add(placeholderList);
                referenceDataErrorList.add(dataErrorList);

            }

            if (referencesJSONArray != null && referencesJSONArray.length() != 0) {
                String[] paramsArray = new String[]{"name", "position", "email", "contact"};
                try {
                    for (int reference = 0; reference < referencesJSONArray.length(); reference++) {
                        for (int placeholder = 0; placeholder < 4; placeholder++) {
                            String value = referencesJSONArray.getJSONObject(reference).getString(paramsArray[placeholder]);
                            if (value.equals("null")) {
                                referencePlaceholderList.get(reference).get(placeholder).setText(null);
                            } else {
                                referencePlaceholderList.get(reference).get(placeholder).setText(value);
                            }
                        }
                    }
                } catch (Exception e) {
                    handleInternalException(e);
                }
            }

            referenceLabelString = employmentVerificationLocale.getReferencesLabel();
            referenceLabelString = referenceLabelString.replace("{0}", String.valueOf(referencesIntValue));
            if (referencesIntValue == 1) {
                referenceLabelString = referenceLabelString.replace("{1}", employmentVerificationLocale.getReferenceSuffixLabelSingle());
            } else if (referencesIntValue > 1) {
                referenceLabelString = referenceLabelString.replace("{1}", employmentVerificationLocale.getReferenceSuffixLabelMultiple());
            }

            fieldReferenceLabel.setText(referenceLabelString);

        } else {
            referencesLinearLayout.setVisibility(View.GONE);
            referencesLabelLinearLayout.setVisibility(View.GONE);
        }

        if (employer != null){
            fieldEmployerPlaceholder.setText(employer);
        }
        if (position != null){
            fieldPositionPlaceholder.setText(position);
        }
        if (reg != null){
            fieldEmployerIDPlaceholder.setText(reg);
        }
        if (startDate != null){
            isStartDateValid = true;
            fieldStartDatePlaceholder.setText(startDate);
        }
        if (endDate != null && !endDate.equals("null")){
            isEndDateValid = true;
            fieldEndDatePlaceholder.setText(endDate);
        }

        if (ended != null){
            if (ended.equals("true")){
                fieldProgressCheckBox.setChecked(false);
                endDateLinearLayout.setVisibility(View.VISIBLE);
            } else if (ended.equals("false")){
                fieldProgressCheckBox.setChecked(true);
                endDateLinearLayout.setVisibility(View.GONE);
            }
        } else {
            fieldProgressCheckBox.setChecked(false);
            endDateLinearLayout.setVisibility(View.VISIBLE);
        }

        jobDetailsTitleTextView.setText(employmentVerificationLocale.getModalTitle());


        coordinatorLayout = jobDetailsBinding.jobDetailsCoordinatorLayout;
        bottomSheet = coordinatorLayout.findViewById(R.id.job_details_bottom_sheet_linear_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        filePickerCameraTextView = bottomSheet.findViewById(R.id.file_picker_option_camera);
        filePickerGalleryTextView = bottomSheet.findViewById(R.id.file_picker_option_gallery);
        filePickerChooseFileTextView = bottomSheet.findViewById(R.id.file_picker_title);

        filePickerCameraTextView.setText(commonLocale.getFilePickerOptionCamera());
        filePickerGalleryTextView.setText(commonLocale.getFilePickerOptionGallery());
        filePickerChooseFileTextView.setText(commonLocale.getFilePickerTitle());

        try {
            permissionsHandler = new PermissionsHandler();
            permissionsHandler.setData(mActivity,  null);
            permissionsHandler.requestPermissions();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        storageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            ProgressIndicator.setProgressBarVisible();
                            if (result.getData() != null) {
                                Uri selectedDocumentUri = result.getData().getData();
                                GlobalVariables.selectedFileName = CommonUtils.getFileName(mActivity, selectedDocumentUri);
                                String selectedFileMimeType = CommonUtils.getMimeType(mActivity, selectedDocumentUri);
                                switch (selectedFileMimeType) {
                                    case "jpg":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "jpg";
                                        GlobalVariables.capturedImageByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "png":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "png";
                                        GlobalVariables.capturedImageByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "pdf":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "pdf";
                                        GlobalVariables.querySelectedPDFByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;
                                    case "xml":
                                        selectedFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        GlobalVariables.selectedMediaMimeType = "xml";
                                        GlobalVariables.selectedXMLFileByteArray = CommonUtils.getByteArray(mActivity, selectedDocumentUri);
                                        break;

                                }
                                onDocumentSelected(selectedFileByteArray);
                            } else {
                                onDocumentSelected(null);
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            ProgressIndicator.setProgressBarInvisible();
                            onDocumentSelected(null);
                            break;
                    }
                });


        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    HandleException.setCurrentActivity(mActivity);
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            HandleException.setCurrentActivity(mActivity);
                            if (result.getData() != null) {
                                String captureType = result.getData().getStringExtra("captureType");
                                if (captureType != null) {
                                    ProgressIndicator.setProgressBarVisible();
                                    switch (captureType) {
                                        case GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE:
                                            capturedImageByteArray = GlobalVariables.capturedImageByteArray;
                                            onImageCaptured(capturedImageByteArray);
                                            break;
                                        case GlobalVariables.CAMERA_CAPTURE_TYPE_VIDEO:
                                            onImageCaptured(GlobalVariables.capturedImageByteArray);
                                            long startTimeStamp = result.getData().getLongExtra("start_time_stamp", 0);
                                            long endTimeStamp = result.getData().getLongExtra("end_time_stamp", 0);
                                            onVideoRecorded(GlobalVariables.recordedVideoBytes, startTimeStamp, endTimeStamp);
                                            break;
                                    }
                                }
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            if (result.getData() != null) {
                                switch (result.getData().getStringExtra("RESULT")) {
                                    case "Exception":
                                        HandleException.handleInternalException(result.getData().getStringExtra("exception"));
                                        break;
                                    case "onBackPressed":
                                        if (isBottomSheetExpanded) {
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }
                                        break;
                                    case "TimeOutException":
                                        HandleException.exitLaunchActivity();
                                        break;
                                    case "GENERATE_QR":
                                        if (Boolean.parseBoolean(Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("showQR")).toString())) {
                                            ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, null));
                                            CommonUtils.sendRequest(AttestrRequest.actionQRGenerate(false));
                                        } else {
                                            HandleException.handleCameraException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getCamError103());
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                });


        Bitmap addIcon = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.add);
        MediaDataClass mediaDataClass = new MediaDataClass();
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
            imagesArrayList = new ArrayList<>();
            mediaDataClass.setmTag("ADD_IMAGE");
            mediaDataClass.setSelectedImageBitmap(addIcon);
            imagesArrayList.add(mediaDataClass);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, null, this, 10);
            workVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter);
            workVerificationRecyclerView.setLayoutManager(gridLayoutManager);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    isBottomSheetExpanded = true;
                    backgroundLinearLayout.setAlpha(0.5f);
                    try {
                        View openCameraImageView = bottomSheet.findViewById(R.id.open_camera_image_view);
                        View openGalleryImageView = bottomSheet.findViewById(R.id.open_gallery_image_view);
                        openCameraImageView.setOnClickListener(v -> startCameraActivity());
                        openGalleryImageView.setOnClickListener(v -> selectDocument());
                    } catch (Exception e) {
                        HandleException.handleInternalException("Bottom Sheet Exception: " + e);
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    isBottomSheetExpanded = false;
                    backgroundLinearLayout.setAlpha(1);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fieldEmployerPlaceholder.addTextChangedListener(new InputTextValidator(fieldEmployerPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isEmployerValid = UserInputValidation.isStringValid(s.toString());
                if (isEmployerValid){
                    fieldEmployerPlaceholder.setBackgroundResource(0);
                    fieldEmployerPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    fieldEmployerDataError.setVisibility(View.GONE);
                } else {
                    fieldEmployerPlaceholder.setBackgroundResource(0);
                    fieldEmployerPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    fieldEmployerDataError.setVisibility(View.VISIBLE);
                    submitProgressBar.setVisibility(View.GONE);
                }
            }
        });

        fieldEmployerIDPlaceholder.addTextChangedListener(new InputTextValidator(fieldEmployerIDPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isEmployerIdValid = UserInputValidation.isStringValid(s.toString());
                if (isEmployerIdValid){
                    fieldEmployerIDPlaceholder.setBackgroundResource(0);
                    fieldEmployerIDPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    fieldEmployerIdDataError.setVisibility(View.GONE);
                } else {
                    fieldEmployerIDPlaceholder.setBackgroundResource(0);
                    fieldEmployerIDPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    fieldEmployerIdDataError.setVisibility(View.VISIBLE);
                    submitProgressBar.setVisibility(View.GONE);
                }
            }
        });

        fieldPositionPlaceholder.addTextChangedListener(new InputTextValidator(fieldPositionPlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                isPositionValid = UserInputValidation.isStringValid(s.toString());
                if (isPositionValid){
                    fieldPositionPlaceholder.setBackgroundResource(0);
                    fieldPositionPlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    fieldPositionDataError.setVisibility(View.GONE);
                } else {
                    fieldPositionPlaceholder.setBackgroundResource(0);
                    fieldPositionPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    fieldPositionDataError.setVisibility(View.VISIBLE);
                    submitProgressBar.setVisibility(View.GONE);
                }
            }
        });
        fieldStartDatePlaceholder.setShowSoftInputOnFocus(false);
        fieldEndDatePlaceholder.setShowSoftInputOnFocus(false);

        startDatePickerDialog = new DatePickerDialog(this, (datePicker, year, month, date) -> {
            calendar.set(year, month, date);
            fieldStartDatePlaceholder.setText(simpleDateFormat.format(calendar.getTime()));
            try {
                start_date = simpleDateFormat.parse(fieldStartDatePlaceholder.getText().toString().trim());
                if (!isEmploymentInProgress && !TextUtils.isEmpty(fieldEndDatePlaceholder.getText().toString().trim())) {
                    end_date = simpleDateFormat.parse(fieldEndDatePlaceholder.getText().toString().trim());
                    validateStartAndEndDates(start_date, end_date);
                } else if (start_date != null){
                    isStartDateValid = true;
                    fieldStartDatePlaceholder.setBackgroundResource(0);
                    fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                    fieldStartDateDataError.setVisibility(View.GONE);
                }
            } catch (Exception e){
                handleInternalException(e);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


        endDatePickerDialog = new DatePickerDialog(this, (datePicker, year, month, date) -> {
            calendar.set(year, month, date);
            fieldEndDatePlaceholder.setText(simpleDateFormat.format(calendar.getTime()));
            try {
                end_date = simpleDateFormat.parse(fieldEndDatePlaceholder.getText().toString().trim());
                if (!isEmploymentInProgress && !TextUtils.isEmpty(fieldStartDatePlaceholder.getText().toString().trim())) {
                    start_date = simpleDateFormat.parse(fieldStartDatePlaceholder.getText().toString().trim());
                    if (start_date != null) {
                        validateStartAndEndDates(start_date, end_date);
                    }
                } else if (end_date != null){
                    isEndDateValid = true;
                    fieldEndDateDataError.setVisibility(View.GONE);
                    fieldEndDatePlaceholder.setBackgroundResource(0);
                    fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                }
            } catch (Exception e){
                handleInternalException(e);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        endDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        fieldStartDatePlaceholder.setOnClickListener(v -> {
            if (startDatePickerDialog != null && !startDatePickerDialog.isShowing()){
                startDatePickerDialog.show();
            }
        });

        fieldEndDatePlaceholder.setOnClickListener(v -> {
            if (endDatePickerDialog != null && !endDatePickerDialog.isShowing()){
                endDatePickerDialog.show();
            }
        });

        fieldStartDatePlaceholder.addTextChangedListener(new InputTextValidator(fieldStartDatePlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    isStartDateValid = UserInputValidation.isEnteredDateFormatValid(s.toString());
                    if (isStartDateValid) {
                        fieldStartDatePlaceholder.setBackgroundResource(0);
                        fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        fieldStartDateDataError.setVisibility(View.GONE);
                    } else {
                        fieldStartDatePlaceholder.setBackgroundResource(0);
                        fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        fieldStartDateDataError.setVisibility(View.VISIBLE);
                        submitProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        fieldEndDatePlaceholder.addTextChangedListener(new InputTextValidator(fieldEndDatePlaceholder) {
            @Override
            public void validate(EditText editText, CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    isEndDateValid = UserInputValidation.isEnteredDateFormatValid(s.toString());
                    if (isEndDateValid) {
                        fieldEndDatePlaceholder.setBackgroundResource(0);
                        fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
                        fieldEndDateDataError.setVisibility(View.GONE);
                    } else {
                        fieldEndDatePlaceholder.setBackgroundResource(0);
                        fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                        fieldEndDateDataError.setVisibility(View.VISIBLE);
                        submitProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });


        submitButtonRelativeLayout.setOnClickListener(this);
        exitJobDetailsImageView.setOnClickListener(this);

        fieldProgressCheckBox.setOnClickListener(v -> {
            if (fieldProgressCheckBox.isChecked()) {
                fieldEndDatePlaceholder.getText().clear();
                isEmploymentInProgress = true;
                endDateLinearLayout.setVisibility(View.GONE);
            } else {
                isEmploymentInProgress = false;
                endDateLinearLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void validateStartAndEndDates(Date start_date, Date end_date) {
        if (start_date.compareTo(end_date) > 0) {
            // Start date is after End date
            isEndDateValid = false;
            fieldEndDateDataError.setText(endDateGreaterThanStartDateError);
            fieldEndDateDataError.setVisibility(View.VISIBLE);
            fieldEndDatePlaceholder.setBackgroundResource(0);
            fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
        } else if (start_date.compareTo(end_date) < 0 ||
                start_date.compareTo(end_date)== 0) {
            // Start date is before End date OR
            // Start date and End date are equal
            isStartDateValid = true;
            isEndDateValid = true;
            fieldEndDateDataError.setText(endDateDataError);
            fieldStartDateDataError.setVisibility(View.GONE);
            fieldEndDateDataError.setVisibility(View.GONE);
            fieldStartDatePlaceholder.setBackgroundResource(0);
            fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
            fieldEndDatePlaceholder.setBackgroundResource(0);
            fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_background);
        }
    }

    private void startCameraActivity() {
        GlobalVariables.isCameraSelected = true;
        GlobalVariables.isStorageSelected = false;
        Intent startCameraIntent = new Intent(JobDetailsActivity.this, CameraActivity.class);
        startCameraIntent.putExtra("CAMERA_FACING", GlobalVariables.CAMERA_FACING_BACK);
        startCameraIntent.putExtra("capture_type", GlobalVariables.CAMERA_CAPTURE_TYPE_IMAGE);
        startCameraIntent.putExtra("current_stage", this.getClass().getSimpleName());
        cameraActivityResultLauncher.launch(startCameraIntent);
    }

    private void selectDocument() {
        GlobalVariables.isCameraSelected = false;
        GlobalVariables.isStorageSelected = true;
        Intent selectDocumentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectDocumentIntent.setType("*/*");
        selectDocumentIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpg","image/jpeg", "application/pdf"});
        Intent selectXmlFileIntent = Intent.createChooser(selectDocumentIntent, "Select Document");
        storageResultLauncher.launch(selectXmlFileIntent);
    }

    @Override
    public void onClick(View v) {
        if (v == submitButtonRelativeLayout){
            integerList = new ArrayList<>();
            placeholderList = new ArrayList<>();
            if (referencesIntValue != 0){
                for (int reference = 0; reference < referencesViewList.size(); reference ++) {
                    integerList.add(0);
                    integerList.add(1);
                    integerList.add(3);
                    RelativeLayout toggleReferenceVisibilityRelativeLayout;
                    toggleReferenceVisibilityRelativeLayout = referencesViewList.get(reference).findViewById(R.id.reference_visibility_toggle_relative_layout);
                    for (int placeholder = 0; placeholder < 4; placeholder ++) {
                        List<EditText> editTextList = referencePlaceholderList.get(reference);
                        if (placeholder != 2) {
                            if (TextUtils.isEmpty(editTextList.get(placeholder).getText().toString())) {
                                referenceDataErrorList.get(reference).get(placeholder).setVisibility(View.VISIBLE);
                                editTextList.get(placeholder).setBackgroundResource(0);
                                editTextList.get(placeholder).setBackgroundResource(R.drawable.edit_text_error_background);
                                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(0);
                                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(R.drawable.edit_text_error_background);
                            } else {
                                placeholderList.add(placeholder);
                                referenceDataErrorList.get(reference).get(placeholder).setVisibility(View.INVISIBLE);
                                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(0);
                                toggleReferenceVisibilityRelativeLayout.setBackgroundResource(R.drawable.edit_text_background);
                                editTextList.get(placeholder).setBackgroundResource(0);
                                editTextList.get(placeholder).setBackgroundResource(R.drawable.edit_text_background);
                            }
                        }
                    }
                }
            }

            isEmploymentInProgress = fieldProgressCheckBox.isChecked();
            if (isEmploymentInProgress) {
                isEmployerValid = !TextUtils.isEmpty(fieldEmployerPlaceholder.getText().toString().trim());
                isEmployerIdValid = !TextUtils.isEmpty(fieldEmployerIDPlaceholder.getText().toString().trim());
                isPositionValid = !TextUtils.isEmpty(fieldPositionPlaceholder.getText().toString().trim());
                if (isEmployerValid && isEmployerIdValid && isPositionValid && isStartDateValid && !documentMediaIDList.isEmpty()) {
                    if (referencesIntValue != 0) {
                        for (int reference = 0; reference < referencesViewList.size(); reference++) {
                            if (placeholderList.equals(integerList)) {
                                if (reference == referencesViewList.size() - 1) {
                                    saveJobDetails();
                                }
                            }
                        }
                    } else {
                        saveJobDetails();
                    }
                } else {
                    submitProgressBar.setVisibility(View.INVISIBLE);
                    submitButtonRelativeLayout.setClickable(true);
                    CommonUtils.setButtonAlphaHigh(submitButtonRelativeLayout);
                    if (!isEmployerValid) {
                        fieldEmployerDataError.setVisibility(View.VISIBLE);
                        fieldEmployerPlaceholder.setBackgroundResource(0);
                        fieldEmployerPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isEmployerIdValid) {
                        fieldEmployerIdDataError.setVisibility(View.VISIBLE);
                        fieldEmployerIDPlaceholder.setBackgroundResource(0);
                        fieldEmployerIDPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isPositionValid) {
                        fieldPositionDataError.setVisibility(View.VISIBLE);
                        fieldPositionPlaceholder.setBackgroundResource(0);
                        fieldPositionPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isStartDateValid) {
                        fieldStartDateDataError.setVisibility(View.VISIBLE);
                        fieldStartDatePlaceholder.setBackgroundResource(0);
                        fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (documentMediaIDList.isEmpty()) {
                        fieldDocumentDataError.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                isEmployerValid = !TextUtils.isEmpty(fieldEmployerPlaceholder.getText().toString().trim());
                isEmployerIdValid = !TextUtils.isEmpty(fieldEmployerIDPlaceholder.getText().toString().trim());
                isPositionValid = !TextUtils.isEmpty(fieldPositionPlaceholder.getText().toString().trim());
                if (isEmployerValid && isEmployerIdValid && isPositionValid && isStartDateValid && isEndDateValid && !documentMediaIDList.isEmpty()) {
                    if (referencesIntValue != 0) {
                        for (int reference = 0; reference < referencesViewList.size(); reference++) {
                            if (placeholderList.equals(integerList)) {
                                if (reference == referencesViewList.size() - 1) {
                                    saveJobDetails();
                                }
                            }
                        }
                    } else {
                        saveJobDetails();
                    }
                } else {
                    submitProgressBar.setVisibility(View.INVISIBLE);
                    submitButtonRelativeLayout.setClickable(true);
                    CommonUtils.setButtonAlphaHigh(submitButtonRelativeLayout);
                    if (!isEmployerValid) {
                        fieldEmployerDataError.setVisibility(View.VISIBLE);
                        fieldEmployerPlaceholder.setBackgroundResource(0);
                        fieldEmployerPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isEmployerIdValid) {
                        fieldEmployerIdDataError.setVisibility(View.VISIBLE);
                        fieldEmployerIDPlaceholder.setBackgroundResource(0);
                        fieldEmployerIDPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isPositionValid) {
                        fieldPositionDataError.setVisibility(View.VISIBLE);
                        fieldPositionPlaceholder.setBackgroundResource(0);
                        fieldPositionPlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isStartDateValid) {
                        fieldStartDateDataError.setVisibility(View.VISIBLE);
                        fieldStartDatePlaceholder.setBackgroundResource(0);
                        fieldStartDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (!isEndDateValid) {
                        fieldEndDateDataError.setText(endDateDataError);
                        fieldEndDateDataError.setVisibility(View.VISIBLE);
                        fieldEndDatePlaceholder.setBackgroundResource(0);
                        fieldEndDatePlaceholder.setBackgroundResource(R.drawable.edit_text_error_background);
                    }
                    if (documentMediaIDList.isEmpty()) {
                        fieldDocumentDataError.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else if (v == exitJobDetailsImageView) {
            exitActivity();
        }
    }

    private void saveJobDetails() {
        try {
            submitProgressBar.setVisibility(View.VISIBLE);
            submitButtonRelativeLayout.setClickable(false);
            CommonUtils.setButtonAlphaLow(submitButtonRelativeLayout);
            String[] paramsArray = new String[]{"name", "position", "email", "contact"};
            JSONArray referencesArray = new JSONArray();
            JSONArray documentsArray = new JSONArray(documentMediaIDList);
            JSONObject jobDetailsObject = new JSONObject();
            jobDetailsObject.put("employer", fieldEmployerPlaceholder.getText().toString().trim());
            jobDetailsObject.put("reg", fieldEmployerIDPlaceholder.getText().toString().trim());
            jobDetailsObject.put("position", fieldPositionPlaceholder.getText().toString().trim());
            jobDetailsObject.put("startDate", fieldStartDatePlaceholder.getText().toString().trim());
            jobDetailsObject.put("ended", !isEmploymentInProgress);
            jobDetailsObject.put("endDate", isEmploymentInProgress ? JSONObject.NULL : fieldEndDatePlaceholder.getText().toString().trim());
            jobDetailsObject.put("documents", documentsArray);
            if (referencesIntValue != 0){
                for (int referenceCount = 0; referenceCount < referencesViewList.size(); referenceCount++){
                    JSONObject referencesObject = new JSONObject();
                    for (int placeholderCount = 0; placeholderCount < 4; placeholderCount ++){
                        referencesObject.put(paramsArray[placeholderCount],
                                TextUtils.isEmpty(referencePlaceholderList.get(referenceCount).get(placeholderCount).getText().toString())
                                        ? JSONObject.NULL : referencePlaceholderList.get(referenceCount).get(placeholderCount).getText().toString());
                    }
                    referencesArray.put(referencesObject);
                }
                jobDetailsObject.put("references", referencesArray);
            }
            Intent intent = new Intent();
            intent.putExtra("RESULT", "RESULT_OK");
            if (jobDetailsIndex != null) {
                intent.putExtra("JOB_DETAILS_INDEX", jobDetailsIndex);
            }
            intent.putExtra("DOCUMENTS_MAP", documentsMap);
            intent.putExtra("JOB_DETAILS_OBJECT", jobDetailsObject.toString());
            intent.putExtra("REFERENCES", String.valueOf(referencesIntValue));
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e){
            handleInternalException(e);
        }
    }

    @Override
    public void onImageSizeExceeded(String meta) {
        this.runOnUiThread(() -> Toast.makeText(JobDetailsActivity.this, "Uploaded document size is large", Toast.LENGTH_SHORT).show());
    }


    public void onImageCaptured(byte[] capturedImageByteArray) {
        if (capturedImageByteArray != null){
            if (isBottomSheetExpanded){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            sendMediaHandshakeRequest();
        }
    }

    public void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp) {

    }

    @Override
    public void onDocumentSelected(byte[] selectedFileByteArray) {
        if (selectedFileByteArray != null){
            if (isBottomSheetExpanded){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            sendMediaHandshakeRequest();
        }
    }

    @Override
    public void onDocumentUploaded(String mediaID, String documentName, String meta) {

    }

    @Override
    public void onImageUploaded(String metadata, String mediaID) {
        try {
            updateRecyclerView(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Work verification onDocument uploaded: "+ e);
        }
    }

    @Override
    public void onVideoUploaded(String metadata, String mediaID) {

    }

    private void sendMediaHandshakeRequest() {
        CommonUtils.sendRequest(AttestrRequest.actionDocumentMediaHandshake(false));
    }

    public void hideBottomSheet(MotionEvent event){
        if (bottomSheet.isEnabled()) {
            Rect outRect = new Rect();
            bottomSheet.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                hideBottomSheet(event);
            } catch (Exception e){
                handleInternalException(e);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void exitActivity() {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "CANCELED");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void onConnectionTimeOut(){
        Intent intent = new Intent();
        intent.putExtra("RESULT", "CONNECTION_TIMEOUT_EXCEPTION");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void handleInternalException(Exception e) {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "INTERNAL_EXCEPTION");
        intent.putExtra("EXCEPTION", e.toString());
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void selectImage(String type) {
        nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void updateRecyclerView(String mediaID) {
        fieldDocumentDataError.setVisibility(View.GONE);
        Bitmap selectedImageBitmap = null;
        if (GlobalVariables.isCameraSelected) {
            selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(capturedImageByteArray, 500);
        } else if (GlobalVariables.isStorageSelected) {
            if (GlobalVariables.selectedMediaMimeType.equals("pdf")) {
                selectedImageBitmap = CommonUtils.getPdfBitmap(selectedDocumentFile);
            } else if (GlobalVariables.selectedMediaMimeType.equals("jpg") || GlobalVariables.selectedMediaMimeType.equals("png")) {
                selectedImageBitmap = CommonUtils.convertByteArrayToBitmap(selectedFileByteArray, 500);
            }
        }
        try {
            MediaDataClass entranceImagesDataClass = new MediaDataClass();
            entranceImagesDataClass.setSelectedImageBitmap(selectedImageBitmap);
            entranceImagesDataClass.setmTag("SELECTED_IMAGE");
            imagesArrayList.add(entranceImagesDataClass);
            Collections.swap(imagesArrayList, imagesArrayList.size() - 2, imagesArrayList.size() - 1);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList, null, this, 10);
            mActivity.runOnUiThread(() -> workVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
            documentMediaIDList.add(mediaID);
        } catch (Exception e) {
            HandleException.handleInternalException("Digital Address exception: " + e);
        }
    }

    @Override
    public void removeImageAndMediaId(int position, String type) {
        try {
            imagesArrayList.remove(position);
            documentMediaIDList.remove(position);
            mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mActivity, imagesArrayList,  null, this, 10);
            mActivity.runOnUiThread(() -> workVerificationRecyclerView.setAdapter(mediaRecyclerViewAdapter));
            mediaRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            HandleException.handleInternalException("removeImageAndMediaId Exception: " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBottomSheetExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

}