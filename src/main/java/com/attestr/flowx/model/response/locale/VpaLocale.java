
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class VpaLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_VPA_LABEL")
    @Expose
    private String fieldVpaLabel;
    @SerializedName("FIELD_VPA_PLACEHOLDER")
    @Expose
    private String fieldVpaPlaceholder;
    @SerializedName("FIELD_VPA_DATAERROR")
    @Expose
    private String fieldVpaDataerror;
    @SerializedName("BUTTON_VPA_PROCEED")
    @Expose
    private String buttonVpaProceed;
    @SerializedName("INVALID_VPA")
    @Expose
    private String invalidVpa;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldVpaLabel() {
        return fieldVpaLabel;
    }

    public void setFieldVpaLabel(String fieldVpaLabel) {
        this.fieldVpaLabel = fieldVpaLabel;
    }

    public String getFieldVpaPlaceholder() {
        return fieldVpaPlaceholder;
    }

    public void setFieldVpaPlaceholder(String fieldVpaPlaceholder) {
        this.fieldVpaPlaceholder = fieldVpaPlaceholder;
    }

    public String getFieldVpaDataerror() {
        return fieldVpaDataerror;
    }

    public void setFieldVpaDataerror(String fieldVpaDataerror) {
        this.fieldVpaDataerror = fieldVpaDataerror;
    }

    public String getButtonVpaProceed() {
        return buttonVpaProceed;
    }

    public void setButtonVpaProceed(String buttonVpaProceed) {
        this.buttonVpaProceed = buttonVpaProceed;
    }

    public String getInvalidVpa() {
        return invalidVpa;
    }

    public void setInvalidVpa(String invalidVpa) {
        this.invalidVpa = invalidVpa;
    }

    public String getDesc() {
        return desc;
    }

    public String getInfo() {
        return info;
    }

}
