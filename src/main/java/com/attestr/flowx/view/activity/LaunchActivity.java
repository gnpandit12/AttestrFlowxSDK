package com.attestr.flowx.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.attestr.flowx.databinding.ActivityLaunchBinding;
import com.attestr.flowx.listener.AddFragmentListener;
import com.attestr.flowx.listener.DigilockerListener;
import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.ViewPagerAdapter;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.locale.ModalLocale;
import com.attestr.flowx.utils.AttestrApplication;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.OkHttpWebSocket;
import com.attestr.flowx.view.fragments.DigiLockerAuthorizationFragment;
import com.attestr.flowx.view.fragments.EducationVerificationFragment;
import com.attestr.flowx.view.fragments.QRFragment;
import com.attestr.flowx.view.fragments.QueryFragment;
import com.attestr.flowx.view.fragments.StageInfoFragment;
import com.attestr.flowx.view.fragments.VerificationSuccessFragment;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.attestr.flowx.view.fragments.WorkVerificationFragment;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckFragmentTwo;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 **/
public class LaunchActivity extends AppCompatActivity implements EventListener, View.OnClickListener,
        AddFragmentListener, DigilockerListener {

    private LinearLayout footerLinearLayout;
    private TextView footerLabelTextView;
    private ImageView headerLogoImageView, headerSkipImageView;
    private HandshakeReadyData handshakeReadyData;
    private RelativeLayout headerRelativeLayout;
    private ViewPager2 bodyViewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragmentList;
    private Fragment currentFragment;
    private String SKIP_TITLE;
    private String SKIP_DESC;
    private String SKIP_PROCEED;
    private String SKIP_CANCEL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AttestrApplication attestrApplication = AttestrApplication.getInstance(false);
        getLifecycle().addObserver(attestrApplication);
        HandleException.init(this);
        HandleException.setCurrentActivity(LaunchActivity.this);
        try {
            if (this.getSupportActionBar() != null) {
                this.getSupportActionBar().hide();
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        CommonUtils.setEventListener(this);
        OkHttpWebSocket okHttpWebSocket = GlobalVariables.webSocket;
        if (okHttpWebSocket != null) {
            okHttpWebSocket.setData(LaunchActivity.this);
        } else {
            HandleException.handleInternalException("Launch activity okHttpWebSocket null");
        }

        ActivityLaunchBinding launchBinding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(launchBinding.getRoot());

        footerLinearLayout = launchBinding.footerLinearLayoutInclude.footerLinearLayout;
        footerLinearLayout.setVisibility(View.VISIBLE);
        footerLabelTextView = launchBinding.footerLinearLayoutInclude.waitFooterLable;
        headerRelativeLayout = launchBinding.headerRelativeLayout.headerRelativeLayout;
        headerLogoImageView = launchBinding.headerRelativeLayout.logoImageView;
        headerSkipImageView = launchBinding.headerRelativeLayout.skipImageView;
        headerSkipImageView.setOnClickListener(this);
        bodyViewPager = launchBinding.bodyViewPager;
        bodyViewPager.setUserInputEnabled(false);
        fragmentList = new ArrayList<>();
        try {
            Intent intent = this.getIntent();
            if (intent != null) {
                String handShakeResponseString = GlobalVariables.currentResponse;
                String status =  GlobalVariables.currentStatus;
                GlobalVariables.handshakeReadyResponseString = handShakeResponseString;
                handshakeReadyData = CommonUtils.gsonBuilder.create().fromJson(handShakeResponseString, HandshakeReadyData.class);
                ModalLocale modalLocale = handshakeReadyData.getLocale().getModal();
                SKIP_TITLE = modalLocale.getSkipTile();
                SKIP_DESC = modalLocale.getSkipDescription();
                SKIP_PROCEED = modalLocale.getSkipProcceed();
                SKIP_CANCEL = modalLocale.getSkipCancel();
                GlobalVariables.handshakeReadyResponse = handshakeReadyData;
                if (GlobalVariables.HANDSHAKE_READY.equals(status)) {
                    fragmentList.add(new WaitFragment(this, null));
                    handShakeReady(status);
                } else if (GlobalVariables.STATUS_DLAUTH_READY.equals(status)) {
                    DigiLockerAuthorizationFragment digiLockerAuthorizationFragment =
                            DigiLockerAuthorizationFragment.getInstance(true);
                    digiLockerAuthorizationFragment.setData(LaunchActivity.this);
                    fragmentList.add(digiLockerAuthorizationFragment);
                    handShakeReady(status);
                }
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    @Override
    public void updateUI(String STATUS, String stageType, String dataErrorResponse, String response) {
        try {
            switch (STATUS) {
                case GlobalVariables.STAGE_READY:
                    switch (stageType) {
                        case GlobalVariables.STAGE_TYPE_PAN:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getStagePan().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_VPA:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getStageVpa().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_BANK_ACCOUNT:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getStageAcc().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_GSTIN:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getStageGstin().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_MASTER_BUSINESS:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getMasterBusinessLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_MASTER_DIRECTOR:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getMasterDirectorLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_AML_BUSINESS:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getAmlBusinessLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_AML_PERSON:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getAmlPersonLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_UIDAI:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getStageUidai().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_FACE_MATCH:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getFaceMatchLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_EAADHAAR:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getEaadhaarLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_ECOURT_PERSON:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().geteCourtPerson().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_ECOURT_BUSINESS:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().geteCourtBusiness().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_DIGITAL_ADDRESS:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getDigitalAddressLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_MOBILE_OTP:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getMobileOtpLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_EMAIL_OTP:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getEmailOTPLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_QUERY:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getQueryLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_EDUCATION:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getEducationVerificationLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_EMPLOYMENT:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getEmploymentVerificationLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_FSSAI:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getFssaiLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_IPV:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getIpvLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_VOTER_ID:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getVoterIDLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_DRIVING_LICENSE:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getDrivingLicenseLocale().getTitle();
                            break;
                        case GlobalVariables.STAGE_TYPE_EPAN:
                            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getePanLocale().getTITLE();
                            break;
                    }
                    addFragment(new StageInfoFragment(this, stageType, null));
                    break;
                case GlobalVariables.DATA_READY:
                    addFragment(new VerificationSuccessFragment());
                    break;
                case GlobalVariables.QR_GEN_READY:
                    QRFragment qrFragment = QRFragment.getInstance(true);
                    qrFragment.setData(LaunchActivity.this, response);
                    addFragment(qrFragment);
                    break;
            }
        } catch (Exception e) {
            HandleException.handleInternalException("Launch activity Update UI: "+ e);
        }
    }

    private void handShakeReady(String status) {
        Bitmap logoBitmap = null;
        String hexString;
        int primaryTextColor;
        String themeColor;
        try {
            footerLabelTextView.setText(handshakeReadyData.getLocale().getModal().getFooterLabel());
            hexString = handshakeReadyData.getConfig().getText();
            primaryTextColor = Color.parseColor(hexString);
            themeColor = handshakeReadyData.getConfig().getTheme();
            headerSkipImageView.setColorFilter(Color.parseColor(themeColor));
            GlobalVariables.textColor = primaryTextColor;
            GlobalVariables.themeColor = themeColor;
            try {
                byte[] decodedString = Base64.decode(
                        handshakeReadyData.getConfig().getLogo(),
                        Base64.DEFAULT
                );
                logoBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                HandleException.handleInternalException(e.toString());
            }

            if (logoBitmap != null) {
                headerLogoImageView.setImageBitmap(logoBitmap);
            }

            GlobalVariables.reqNotProcessed = handshakeReadyData.getLocale().getCommon().getReqNotprocessed();
            if (GlobalVariables.HANDSHAKE_READY.equals(status)) {
                CommonUtils.sendRequest(AttestrRequest.stageNext());
            }
            viewPagerAdapter = new ViewPagerAdapter(this, fragmentList);
            bodyViewPager.setAdapter(viewPagerAdapter);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == headerSkipImageView) {
            skipFlow();
        }
    }

    @Override
    public void addFragment(Fragment fragment) {
        this.currentFragment = fragment;
        try {
            if (viewPagerAdapter != null) {
                viewPagerAdapter.addFragment(fragment);
                bodyViewPager.setCurrentItem(bodyViewPager.getCurrentItem() + 1);
                viewPagerAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }


    @Override
    public void skipFlow() {
        LaunchActivity.this.runOnUiThread(() -> CommonUtils.showTerminateAlertMessage(
                LaunchActivity.this,
                SKIP_TITLE,
                SKIP_DESC,
                SKIP_PROCEED,
                SKIP_CANCEL));
    }

    @Override
    public void changeFooterLayoutVisibility(boolean isVisible) {
        if (isVisible) {
            footerLinearLayout.setVisibility(View.VISIBLE);
        } else {
            footerLinearLayout.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!CommonUtils.isCleanedUp) {
            CommonUtils.cleanUp();
        }
    }

    @Override
    public void onBackPressed() {
        LaunchActivity.this.runOnUiThread(() -> CommonUtils.showTerminateAlertMessage(
                LaunchActivity.this,
                SKIP_TITLE,
                SKIP_DESC,
                SKIP_PROCEED,
                SKIP_CANCEL));
    }

    @Override
    public void digiDocumentResponse(String response) {
        this.runOnUiThread(() -> {
            GlobalVariables.currentVerificationStage = GlobalVariables.handshakeReadyResponse.getLocale().getDigilockerDocumentLocale().getTitle();
            addFragment(new StageInfoFragment(LaunchActivity.this, GlobalVariables.STAGE_TYPE_DIGILOCKER_DOC, response));
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                if (currentFragment != null) {
                    if (currentFragment instanceof DigitalAddressCheckFragmentTwo) {
                        DigitalAddressCheckFragmentTwo digitalAddressCheckFragmentTwo = DigitalAddressCheckFragmentTwo.getInstance(false);
                        if (digitalAddressCheckFragmentTwo != null) {
                            digitalAddressCheckFragmentTwo.hideBottomSheet(event);
                        }
                    } else if (currentFragment instanceof EducationVerificationFragment) {
                        EducationVerificationFragment educationVerificationFragment = EducationVerificationFragment.getInstance(false);
                        if (educationVerificationFragment != null) {
                            educationVerificationFragment.hideBottomSheet(event);
                        }
                    } else if (currentFragment instanceof QueryFragment) {
                        QueryFragment queryFragment = QueryFragment.getInstance(false);
                        if (queryFragment != null) {
                            queryFragment.hideBottomSheet(event);
                        }
                    }
                }
            } catch (Exception e) {
                HandleException.handleInternalException("Bottom sheet touch event exception: " + e);
            }
        }
        return super.dispatchTouchEvent(event);
    }

}