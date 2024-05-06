package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 09/01/23
 **/
public class DrivingLicenseLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_DL_LABEL")
    @Expose
    private String fieldDlLabel;
    @SerializedName("FIELD_DL_PLACEHOLDER")
    @Expose
    private String fieldDlPlaceholder;
    @SerializedName("FIELD_DL_DATAERROR")
    @Expose
    private String fieldDlDataerror;
    @SerializedName("FIELD_DOB_LABEL")
    @Expose
    private String fieldDobLabel;
    @SerializedName("FIELD_DOB_PLACEHOLDER")
    @Expose
    private String fieldDobPlaceholder;
    @SerializedName("FIELD_DOB_DATAERROR")
    @Expose
    private String fieldDobDataerror;
    @SerializedName("BUTTON_DL_PROCEED")
    @Expose
    private String buttonDlProceed;
    @SerializedName("INVALID_DL")
    @Expose
    private String invalidDl;

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

    public String getFieldDlLabel() {
        return fieldDlLabel;
    }

    public void setFieldDlLabel(String fieldDlLabel) {
        this.fieldDlLabel = fieldDlLabel;
    }

    public String getFieldDlPlaceholder() {
        return fieldDlPlaceholder;
    }

    public void setFieldDlPlaceholder(String fieldDlPlaceholder) {
        this.fieldDlPlaceholder = fieldDlPlaceholder;
    }

    public String getFieldDlDataerror() {
        return fieldDlDataerror;
    }

    public void setFieldDlDataerror(String fieldDlDataerror) {
        this.fieldDlDataerror = fieldDlDataerror;
    }

    public String getFieldDobLabel() {
        return fieldDobLabel;
    }

    public void setFieldDobLabel(String fieldDobLabel) {
        this.fieldDobLabel = fieldDobLabel;
    }

    public String getFieldDobPlaceholder() {
        return fieldDobPlaceholder;
    }

    public void setFieldDobPlaceholder(String fieldDobPlaceholder) {
        this.fieldDobPlaceholder = fieldDobPlaceholder;
    }

    public String getFieldDobDataerror() {
        return fieldDobDataerror;
    }

    public void setFieldDobDataerror(String fieldDobDataerror) {
        this.fieldDobDataerror = fieldDobDataerror;
    }

    public String getButtonDlProceed() {
        return buttonDlProceed;
    }

    public void setButtonDlProceed(String buttonDlProceed) {
        this.buttonDlProceed = buttonDlProceed;
    }

    public String getInvalidDl() {
        return invalidDl;
    }

    public void setInvalidDl(String invalidDl) {
        this.invalidDl = invalidDl;
    }


}
