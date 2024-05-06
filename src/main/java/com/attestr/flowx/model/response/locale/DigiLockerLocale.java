
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class DigiLockerLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("DL_SIGNUP_OPTION")
    @Expose
    private String dlSignupOption;
    @SerializedName("DL_SIGNIN_OPTION")
    @Expose
    private String dlSigninOption;
    @SerializedName("DL_PROCEED_SIGNUP_ALERT")
    @Expose
    private String dlProceedSignupAlert;
    @SerializedName("DL_PROCEED_LABEL")
    @Expose
    private String dlProceedLabel;
    @SerializedName("DL_CANCEL_LABEL")
    @Expose
    private String dlCancelLabel;
    @SerializedName("DL_AUTHORIZATION_FAILED")
    @Expose
    private String dlAuthorizationFailed;
    @SerializedName("DL_ACCESS_REVOKED")
    @Expose
    private String dlAccessRevoked;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDlSignupOption() {
        return dlSignupOption;
    }

    public void setDlSignupOption(String dlSignupOption) {
        this.dlSignupOption = dlSignupOption;
    }

    public String getDlSigninOption() {
        return dlSigninOption;
    }

    public void setDlSigninOption(String dlSigninOption) {
        this.dlSigninOption = dlSigninOption;
    }

    public String getDlProceedSignupAlert() {
        return dlProceedSignupAlert;
    }

    public void setDlProceedSignupAlert(String dlProceedSignupAlert) {
        this.dlProceedSignupAlert = dlProceedSignupAlert;
    }

    public String getDlProceedLabel() {
        return dlProceedLabel;
    }

    public void setDlProceedLabel(String dlProceedLabel) {
        this.dlProceedLabel = dlProceedLabel;
    }

    public String getDlCancelLabel() {
        return dlCancelLabel;
    }

    public void setDlCancelLabel(String dlCancelLabel) {
        this.dlCancelLabel = dlCancelLabel;
    }

    public String getDlAuthorizationFailed() {
        return dlAuthorizationFailed;
    }

    public void setDlAuthorizationFailed(String dlAuthorizationFailed) {
        this.dlAuthorizationFailed = dlAuthorizationFailed;
    }

    public String getDlAccessRevoked() {
        return dlAccessRevoked;
    }

    public void setDlAccessRevoked(String dlAccessRevoked) {
        this.dlAccessRevoked = dlAccessRevoked;
    }

}
