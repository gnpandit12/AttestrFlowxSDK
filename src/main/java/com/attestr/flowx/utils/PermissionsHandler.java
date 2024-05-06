package com.attestr.flowx.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.UserLocation;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.view.activity.JobDetailsActivity;
import com.attestr.flowx.view.fragments.AadharOfflineXMLVerificationFragment;
import com.attestr.flowx.view.fragments.EducationVerificationFragment;
import com.attestr.flowx.view.fragments.FaceMatchFragment;
import com.attestr.flowx.view.fragments.InPersonVerificationFragment;
import com.attestr.flowx.view.fragments.QueryFragment;
import com.attestr.flowx.view.fragments.digitalAddressCheck.DigitalAddressCheckFragmentTwo;

import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 15/12/21
 **/
public class PermissionsHandler {

    private Activity mActivity;
    private Fragment mCurrentFragment;
    private JobDetailsActivity jobDetailsActivity;
    private Handler handler;
    private String[] PERMISSIONS;
    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;
    private CommonLocale commonLocale;

    public void setData(Activity activity,
                         Fragment currentFragment) {
        this.mActivity = activity;
        this.mCurrentFragment = currentFragment;
        commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        if (activity instanceof JobDetailsActivity) {
            this.jobDetailsActivity = (JobDetailsActivity) activity;
        }
        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();

        if (mCurrentFragment instanceof InPersonVerificationFragment) {
            if ((Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled") &&
                    (Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("geolocationEnabled")) {
                PERMISSIONS = new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
            } else if ((Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("geolocationEnabled")
                    && !(Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled")) {
                PERMISSIONS = new String[]{
//                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
            } else if ((Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled")
                    && !(Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("geolocationEnabled")) {
                PERMISSIONS = new String[] {
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                };
            } else if ( !(Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("voiceOTPEnabled")
                    && !(Boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("geolocationEnabled")) {
                PERMISSIONS = new String[] {
                        Manifest.permission.CAMERA
                };
            }
        } else if (mCurrentFragment instanceof AadharOfflineXMLVerificationFragment) {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else if (mCurrentFragment instanceof EducationVerificationFragment) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else if (mCurrentFragment instanceof FaceMatchFragment) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else if (mCurrentFragment instanceof QueryFragment) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else if (mCurrentFragment instanceof DigitalAddressCheckFragmentTwo) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else if (mCurrentFragment == null && mActivity instanceof JobDetailsActivity) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        if (mCurrentFragment != null) {
            ProgressIndicator.setActivity(mCurrentFragment.getActivity());
        } else if (mActivity != null) {
            ProgressIndicator.setActivity(mActivity);
        }

         setPermissionsLauncher();
    }

    private void setPermissionsLauncher() {
        if (mCurrentFragment != null) {
            multiplePermissionLauncher = mCurrentFragment.registerForActivityResult(multiplePermissionsContract, isGranted -> {
                for (Map.Entry<String, Boolean> permission : isGranted.entrySet()) {
                    String permissionKey = permission.getKey();
                    Boolean permissionValue = permission.getValue();
                    switch (permissionKey) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if (permissionValue) {
                                try {
                                    UserLocation.getInstance(true).setData(mActivity);
                                } catch (Exception e) {
                                    if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled")) {
                                        CommonUtils.sendRequest(AttestrRequest.actionIPGeolocation());
                                    } else {
                                        HandleException.handleLocationException(commonLocale.getGpsError500());
                                    }
                                }
                            } else {
                                CommonUtils.showLocationPermissionAlertDialog(mActivity);
                            }
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            if (!permissionValue) {
                                HandleException.handleCameraException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getCamError102());
                            }
                            break;
                        case Manifest.permission.CAMERA:
                            if (!permissionValue) {
                                HandleException.handleCameraException(commonLocale.getCamError102());
                            }
                            break;
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            if (!permissionValue) {
                                HandleException.handleStoragePermissionException(commonLocale.getStorageAccessError());
                            }
                            break;
                    }
                }
            });
        } else if (jobDetailsActivity != null) {
            multiplePermissionLauncher = jobDetailsActivity.registerForActivityResult(multiplePermissionsContract, isGranted -> {
                for (Map.Entry<String, Boolean> permission : isGranted.entrySet()) {
                    String permissionKey = permission.getKey();
                    Boolean permissionValue = permission.getValue();
                    switch (permissionKey) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if (permissionValue) {
                                try {
                                    UserLocation.getInstance(true).setData(mActivity);
                                } catch (Exception e) {
                                    if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled")) {
                                        CommonUtils.sendRequest(AttestrRequest.actionIPGeolocation());
                                    } else {
                                        HandleException.handleLocationException(commonLocale.getGpsError500());
                                        break;
                                    }
                                    break;
                                }
                            } else {
                                CommonUtils.showLocationPermissionAlertDialog(mActivity);
                            }
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            if (!permissionValue) {
                                HandleException.handleCameraException(GlobalVariables.handshakeReadyResponse.getLocale().getCommon().getCamError102());
                                break;
                            }
                            break;
                        case Manifest.permission.CAMERA:
                            if (!permissionValue) {
                                HandleException.handleCameraException(commonLocale.getCamError102());
                            }
                            break;
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            if (!permissionValue) {
                                HandleException.handleStoragePermissionException(commonLocale.getStorageAccessError());
                                break;
                            }
                            break;
                    }

                }
            });
        }
    }

    public void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(PERMISSIONS)) {
                    multiplePermissionLauncher.launch(PERMISSIONS);
                } else {
                    for (String permission : PERMISSIONS) {
                        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                            try {
                                UserLocation.getInstance(true).setData(mActivity);
                            } catch (Exception e) {
                                if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled")) {
                                    CommonUtils.sendRequest(AttestrRequest.actionIPGeolocation());
                                } else {
                                    HandleException.handleLocationException(commonLocale.getGpsError500());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            HandleException.handleInternalException("Request Permissions Exception: "+ e);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
