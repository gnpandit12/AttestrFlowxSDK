package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.digilockerDoument.DigiLockerDocumentResponse;
import com.attestr.flowx.model.response.locale.DrivingLicenseLocale;
import com.attestr.flowx.model.response.locale.EducationVerificationLocale;
import com.attestr.flowx.model.response.locale.EmailOTPLocale;
import com.attestr.flowx.model.response.locale.EmploymentVerificationLocale;
import com.attestr.flowx.model.response.locale.FssaiLocale;
import com.attestr.flowx.model.response.locale.MobileOtpLocale;
import com.attestr.flowx.model.response.locale.VoterIDLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckFragmentOne;
import com.attestr.flowx.view.fragments.emailOTPVerification.EmailOTPFragmentOne;
import com.attestr.flowx.view.fragments.mobileOtpVerification.MobileOtpFragmentOne;
import com.google.gson.JsonArray;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 28/08/21
 **/
public class StageInfoFragment extends Fragment {

    private static final String TAG = "stage_info_fragment";
    private TextView stageTitleTextView, stageDescriptionTextView, acceptAndContinueTextView;
    private TextView certificateTitleTextView, issuerTitleTextView, certificateTitle, issureTitle;
    private LinearLayout acceptAndContinueLinearLayout, digilockerCertificateLinearLayout;
    private ProgressBar acceptProgressBar;
    private String STAGE_TYPE, currentVerificationStage, mDigiDocResponse;
    private HandshakeReadyData handShakeReadyResponse;
    private Activity mActivity;
    private JsonArray paramsArray = new JsonArray();
    private ImageView consentImageView;

    public StageInfoFragment(Activity activity, String stageType, String digiDocResponse) {
        this.STAGE_TYPE = stageType;
        this.mActivity = activity;
        this.mDigiDocResponse = digiDocResponse;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stage_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        stageTitleTextView = view.findViewById(R.id.stage_title_text_view);
        stageDescriptionTextView = view.findViewById(R.id.stage_description_text_view);
        acceptAndContinueTextView = view.findViewById(R.id.accept_and_continue_text_view);
        acceptAndContinueTextView.setTextColor(GlobalVariables.textColor);
        certificateTitleTextView = view.findViewById(R.id.certificate_title_text_view);
        certificateTitle = view.findViewById(R.id.certificate_title);
        issuerTitleTextView = view.findViewById(R.id.issuer_title_text_view);
        issureTitle = view.findViewById(R.id.issuer_title);
        consentImageView = view.findViewById(R.id.consent_image_view);
        consentImageView.setVisibility(View.GONE);
        acceptAndContinueLinearLayout = view.findViewById(R.id.accept_and_continue_linear_layout);
        digilockerCertificateLinearLayout = view.findViewById(R.id.digilocker_certificate_linear_layout);

        digilockerCertificateLinearLayout.setVisibility(View.GONE);


        CommonUtils.setButtonAlphaHigh(acceptAndContinueLinearLayout);
        acceptProgressBar = view.findViewById(R.id.accept_progress_bar);
        acceptProgressBar.setVisibility(View.GONE);

        acceptAndContinueTextView.setText(handShakeReadyResponse.getLocale().getCommon().getAcceptButtonLabel());

        switch (STAGE_TYPE) {
            case GlobalVariables.STAGE_TYPE_PAN:
                currentVerificationStage = handShakeReadyResponse.getLocale().getStagePan().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getStagePan().getDesc());

                // TODO add set consent image
//                consentImageView.setBackgroundResource(0);
//                consentImageView.setBackgroundResource(R.drawable.pan_consent);
//                consentImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));

                acceptAndContinueLinearLayout.setOnClickListener(v -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    PanVerificationFragment panVerificationFragment = PanVerificationFragment.getInstance(true);
                    panVerificationFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(panVerificationFragment);

                });
                break;
            case GlobalVariables.STAGE_TYPE_VPA:
                currentVerificationStage = handShakeReadyResponse.getLocale().getStageVpa().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getStageVpa().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(v -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new UpiVerificationFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_BANK_ACCOUNT:
                currentVerificationStage = handShakeReadyResponse.getLocale().getStageAcc().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getStageAcc().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new BankAccountVerificationFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_GSTIN:
                currentVerificationStage = handShakeReadyResponse.getLocale().getStageGstin().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getStageGstin().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new GstinCheckFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_MASTER_BUSINESS:
                currentVerificationStage = handShakeReadyResponse.getLocale().getMasterBusinessLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getMasterBusinessLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new MasterBusinessFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR:
                currentVerificationStage = handShakeReadyResponse.getLocale().getMasterDirectorLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getMasterDirectorLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new MasterDirectorFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_AML_BUSINESS:
                currentVerificationStage = handShakeReadyResponse.getLocale().getAmlBusinessLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getAmlBusinessLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new BusinessAMLFragment());
                });
                break;
            case GlobalVariables.STAGE_TYPE_AML_PERSON:
                currentVerificationStage = handShakeReadyResponse.getLocale().getAmlPersonLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getAmlPersonLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new PersonAMLFragment(mActivity));
                });
                break;
            case GlobalVariables.STAGE_TYPE_UIDAI:
                currentVerificationStage = handShakeReadyResponse.getLocale().getStageUidai().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getStageUidai().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    AadharOfflineXMLVerificationFragment aadharOfflineXMLVerificationFragment =
                            AadharOfflineXMLVerificationFragment.getInstance(true);
                    aadharOfflineXMLVerificationFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(aadharOfflineXMLVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                currentVerificationStage = handShakeReadyResponse.getLocale().getFaceMatchLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getFaceMatchLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    FaceMatchFragment faceMatchFragment = FaceMatchFragment.getInstance(true);
                    faceMatchFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(faceMatchFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_IPV:
                try {
                    currentVerificationStage = handShakeReadyResponse.getLocale().getIpvLocale().getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                    stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getIpvLocale().getDesc());

                    acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                        CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                        acceptProgressBar.setVisibility(View.VISIBLE);
                        InPersonVerificationFragment inPersonVerificationFragment = InPersonVerificationFragment.getInstance(true);
                        inPersonVerificationFragment.setData(mActivity);
                        ((LaunchActivity) mActivity).addFragment(inPersonVerificationFragment);
                    });
                } catch (Exception e) {
                    HandleException.handleInternalException("Stage Info IPV: "+ e);
                }
                break;
            case GlobalVariables.STAGE_TYPE_EAADHAAR:
                currentVerificationStage = handShakeReadyResponse.getLocale().getEaadhaarLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getEaadhaarLocale().getDesc());
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("startWebView", "false");
                dataMap.put("digest", null);
                dataMap.put("selectedOption", null);

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    ((LaunchActivity) mActivity).addFragment(new WaitFragment(mActivity, dataMap));
                });
                break;
            case GlobalVariables.STAGE_TYPE_DIGILOCKER_DOC:
                digilockerCertificateLinearLayout.setVisibility(View.INVISIBLE);
                GlobalVariables.docType = Objects.requireNonNull(GlobalVariables.stageReadyResponse.getData().getMetadata().get("document")).toString();
                Map<String, String> params = new HashMap<>();
                DigiLockerDocumentResponse digiLockerDocumentResponse = CommonUtils.getGson().fromJson(mDigiDocResponse, DigiLockerDocumentResponse.class);
                currentVerificationStage = handShakeReadyResponse.getLocale().getDigilockerDocumentLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getDigilockerDocumentLocale().getDesc());
                certificateTitleTextView.setText(handShakeReadyResponse.getLocale().getDigilockerDocumentLocale().getCertificateTitle());
                issuerTitleTextView.setText(handShakeReadyResponse.getLocale().getDigilockerDocumentLocale().getIssuerTitle());
                certificateTitle.setText(digiLockerDocumentResponse.getDescription());
                issureTitle.setText(digiLockerDocumentResponse.getName());
                params.put("issuer", digiLockerDocumentResponse.getName());
                for (int i = 0; i < digiLockerDocumentResponse.getDocuments().size(); i++) {
                    if (GlobalVariables.docType.equals(
                            digiLockerDocumentResponse.getDocuments().get(i).getDocument().getDoctype()
                    )) {
                        paramsArray.addAll(digiLockerDocumentResponse.getDocuments().get(i).getParams());
                        String certificate = digiLockerDocumentResponse.getDocuments().get(i).getDocument().getDescription();
                        params.put("certificate", certificate);
                        certificateTitle.setText(certificate);
                        break;
                    }
                }
                certificateTitleTextView.setVisibility(View.VISIBLE);
                certificateTitle.setVisibility(View.VISIBLE);
                issuerTitleTextView.setVisibility(View.VISIBLE);
                issureTitle.setVisibility(View.VISIBLE);

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    try {
                        acceptProgressBar.setVisibility(View.VISIBLE);
                        DigiLockerDocumentFragment digiLockerDocumentFragment = DigiLockerDocumentFragment.getInstance(true);
                        digiLockerDocumentFragment.setData(mActivity, paramsArray, params, CommonUtils.isTablet(mActivity));
                        ((LaunchActivity) mActivity).addFragment(digiLockerDocumentFragment);
                    } catch (Exception e) {
                        HandleException.handleInternalException("DigiLocker document accept and continue click exception: " + e);
                    }
                });
                break;
            case GlobalVariables.STAGE_TYPE_ECOURT_PERSON:
                currentVerificationStage = handShakeReadyResponse.getLocale().geteCourtPerson().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().geteCourtPerson().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    PersonCourtCheckFragment personCourtCheckFragment = PersonCourtCheckFragment.getInstance(true);
                    personCourtCheckFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(personCourtCheckFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_ECOURT_BUSINESS:
                currentVerificationStage = handShakeReadyResponse.getLocale().geteCourtBusiness().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().geteCourtBusiness().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    BusinessCourtCheckFragment businessCourtCheckFragment = BusinessCourtCheckFragment.getInstance(true);
                    businessCourtCheckFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(businessCourtCheckFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                currentVerificationStage = handShakeReadyResponse.getLocale().getDigitalAddressLocale().getTitle();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getDigitalAddressLocale().getDesc());

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    DigitalAddressCheckFragmentOne digitalAddressCheckFragmentOne = DigitalAddressCheckFragmentOne.getInstance(true);
                    digitalAddressCheckFragmentOne.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(digitalAddressCheckFragmentOne);
                });
                break;
            case GlobalVariables.STAGE_TYPE_MOBILE_OTP:
                MobileOtpLocale mobileOtpLocale = handShakeReadyResponse.getLocale().getMobileOtpLocale();
                if (mobileOtpLocale.getTitle() != null) {
                    currentVerificationStage = mobileOtpLocale.getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                }

                if (mobileOtpLocale.getDesc() != null) {
                    stageDescriptionTextView.setText(mobileOtpLocale.getDesc());
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }


                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    MobileOtpFragmentOne mobileOtpFragmentOne = MobileOtpFragmentOne.getInstance(true);
                    mobileOtpFragmentOne.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(mobileOtpFragmentOne);
                });
                break;
            case GlobalVariables.STAGE_TYPE_EMAIL_OTP:
                EmailOTPLocale emailOTPLocale = handShakeReadyResponse.getLocale().getEmailOTPLocale();
                if (emailOTPLocale.getTitle() != null) {
                    currentVerificationStage = emailOTPLocale.getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                }

                if (emailOTPLocale.getDesc() != null) {
                    stageDescriptionTextView.setText(emailOTPLocale.getDesc());
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    EmailOTPFragmentOne emailOtpFragmentOne = EmailOTPFragmentOne.getInstance(true);
                    emailOtpFragmentOne.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(emailOtpFragmentOne);
                });
                break;
            case GlobalVariables.STAGE_TYPE_QUERY:
                try {
                    currentVerificationStage = handShakeReadyResponse.getLocale().getQueryLocale().getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                    stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getQueryLocale().getDesc());
                    acceptAndContinueLinearLayout.setOnClickListener(v -> {
                        CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                        acceptProgressBar.setVisibility(View.VISIBLE);
                        QueryFragment queryFragment = QueryFragment.getInstance(true);
                        queryFragment.setData(mActivity, CommonUtils.isTablet(mActivity));
                        ((LaunchActivity) mActivity).addFragment(queryFragment);
                    });
                } catch (Exception e) {
                    HandleException.handleInternalException("Query fragment exception: "+e.toString());
                }
                break;
            case GlobalVariables.STAGE_TYPE_EDUCATION:
                EducationVerificationLocale educationVerificationLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEducationVerificationLocale();
                String courseLevel = String.valueOf(GlobalVariables.stageReadyResponse.getData().getMetadata().get("level"));
                String[] strings = educationVerificationLocale.getDesc().split("\\{0\\}");
                Map<String, Object> educationLocaleKeyMap = null;
                try {
                    educationLocaleKeyMap = CommonUtils.jsonToMap(CommonUtils.getGson().toJson(educationVerificationLocale));
                } catch (JSONException e) {
                    HandleException.handleInternalException("Education locale json to map: " + e.toString());
                }

                if (educationLocaleKeyMap != null) {
                    try {
                        String course = String.valueOf(GlobalVariables.stageReadyResponse.getData().getMetadata().get("course"));
                        String courseLevelKey = courseLevel + "_DOC";
                        String course_value = Objects.requireNonNull(educationLocaleKeyMap.get(courseLevelKey)).toString();
                        if ("OTHER".equals(courseLevel)) {
                            course_value = course_value.replace("{0}", course);
                            String description = strings[0] + course_value + strings[1];
                            stageDescriptionTextView.setText(description);
                        } else {
                            String description = strings[0] + course_value + strings[1];
                            stageDescriptionTextView.setText(description);
                        }
                    } catch (Exception e) {
                        HandleException.handleInternalException(e.toString());
                    }
                }
                currentVerificationStage = educationVerificationLocale.getTitle();
                stageTitleTextView.setText(currentVerificationStage);

                acceptAndContinueLinearLayout.setOnClickListener(v -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    EducationVerificationFragment educationVerificationFragment = EducationVerificationFragment.getInstance(true);
                    educationVerificationFragment.setData(mActivity, CommonUtils.isTablet(mActivity));
                    ((LaunchActivity) mActivity).addFragment(educationVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_EMPLOYMENT:
                EmploymentVerificationLocale employmentVerificationLocale = GlobalVariables.handshakeReadyResponse.getLocale().getEmploymentVerificationLocale();
                String title = employmentVerificationLocale.getTitle();
                String info = employmentVerificationLocale.getInfo();
                String desc = employmentVerificationLocale.getDesc();

                if (desc != null) {
                    stageDescriptionTextView.setText(desc);
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }

                if (title != null) {
                    currentVerificationStage = title;
                    stageTitleTextView.setText(currentVerificationStage);
                }

                acceptAndContinueLinearLayout.setOnClickListener(v -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    WorkVerificationFragment workVerificationFragment = WorkVerificationFragment.getInstance(true);
                    workVerificationFragment.setData(mActivity, CommonUtils.isTablet(mActivity));
                    ((LaunchActivity) mActivity).addFragment(workVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_FSSAI:
                FssaiLocale fssaiLocale = handShakeReadyResponse.getLocale().getFssaiLocale();
                if (fssaiLocale.getTitle() != null) {
                    currentVerificationStage = fssaiLocale.getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                }

                if (fssaiLocale.getDesc() != null) {
                    stageDescriptionTextView.setText(fssaiLocale.getDesc());
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    FssaiVerificationFragment fssaiVerificationFragment = FssaiVerificationFragment.getInstance(true);
                    fssaiVerificationFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(fssaiVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_VOTER_ID:
                VoterIDLocale voterIDLocale = handShakeReadyResponse.getLocale().getVoterIDLocale();
                if (voterIDLocale.getTitle() != null) {
                    currentVerificationStage = voterIDLocale.getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                }

                if (voterIDLocale.getDesc() != null) {
                    stageDescriptionTextView.setText(voterIDLocale.getDesc());
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    VoterIDVerificationFragment voterIDVerificationFragment = VoterIDVerificationFragment.getInstance(true);
                    voterIDVerificationFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(voterIDVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_DRIVING_LICENSE:
                DrivingLicenseLocale drivingLicenseLocale = handShakeReadyResponse.getLocale().getDrivingLicenseLocale();
                if (drivingLicenseLocale.getTitle() != null) {
                    currentVerificationStage = drivingLicenseLocale.getTitle();
                    stageTitleTextView.setText(currentVerificationStage);
                }

                if (drivingLicenseLocale.getDesc() != null) {
                    stageDescriptionTextView.setText(drivingLicenseLocale.getDesc());
                } else {
                    stageDescriptionTextView.setVisibility(View.GONE);
                }

                acceptAndContinueLinearLayout.setOnClickListener(view1 -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    DrivingLicenseVerificationFragment drivingLicenseVerificationFragment = DrivingLicenseVerificationFragment.getInstance(true);
                    drivingLicenseVerificationFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(drivingLicenseVerificationFragment);
                });
                break;
            case GlobalVariables.STAGE_TYPE_EPanDigilocker:
                currentVerificationStage = handShakeReadyResponse.getLocale().getePanLocale().getTITLE();
                stageTitleTextView.setText(currentVerificationStage);
                stageDescriptionTextView.setText(handShakeReadyResponse.getLocale().getePanLocale().getDESC());

                acceptAndContinueLinearLayout.setOnClickListener(v -> {
                    CommonUtils.setButtonAlphaLow(acceptAndContinueLinearLayout);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    EPanDigilockerFragment ePanDigilockerFragment = EPanDigilockerFragment.getInstance(true);
                    ePanDigilockerFragment.setData(mActivity);
                    ((LaunchActivity) mActivity).addFragment(ePanDigilockerFragment);
                });
                break;
        }
        GlobalVariables.currentVerificationStage = currentVerificationStage;

    }

}


