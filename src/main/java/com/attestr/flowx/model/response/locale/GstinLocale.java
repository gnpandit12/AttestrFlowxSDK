
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class GstinLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_GSTIN_LABEL")
    @Expose
    private String fieldGstinLabel;
    @SerializedName("FIELD_GSTIN_PLACEHOLDER")
    @Expose
    private String fieldGstinPlaceholder;
    @SerializedName("FIELD_GSTIN_DATAERROR")
    @Expose
    private String fieldGstinDataerror;
    @SerializedName("BUTTON_GSTIN_PROCEED")
    @Expose
    private String buttonGstinProceed;
    @SerializedName("INVALID_GSTIN")
    @Expose
    private String invalidGstin;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldGstinLabel() {
        return fieldGstinLabel;
    }

    public void setFieldGstinLabel(String fieldGstinLabel) {
        this.fieldGstinLabel = fieldGstinLabel;
    }

    public String getFieldGstinPlaceholder() {
        return fieldGstinPlaceholder;
    }

    public void setFieldGstinPlaceholder(String fieldGstinPlaceholder) {
        this.fieldGstinPlaceholder = fieldGstinPlaceholder;
    }

    public String getFieldGstinDataerror() {
        return fieldGstinDataerror;
    }

    public void setFieldGstinDataerror(String fieldGstinDataerror) {
        this.fieldGstinDataerror = fieldGstinDataerror;
    }

    public String getButtonGstinProceed() {
        return buttonGstinProceed;
    }

    public void setButtonGstinProceed(String buttonGstinProceed) {
        this.buttonGstinProceed = buttonGstinProceed;
    }

    public String getInvalidGstin() {
        return invalidGstin;
    }

    public void setInvalidGstin(String invalidGstin) {
        this.invalidGstin = invalidGstin;
    }

    public String getDesc() {
        return desc;
    }

    public String getInfo() {
        return info;
    }

}
