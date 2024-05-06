
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class BankAccountLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_ACC_LABEL")
    @Expose
    private String fieldAccLabel;
    @SerializedName("FIELD_ACC_PLACEHOLDER")
    @Expose
    private String fieldAccPlaceholder;
    @SerializedName("FIELD_ACC_DATAERROR")
    @Expose
    private String fieldAccDataerror;
    @SerializedName("FIELD_IFSC_LABEL")
    @Expose
    private String fieldIfscLabel;
    @SerializedName("FIELD_IFSC_PLACEHOLDER")
    @Expose
    private String fieldIfscPlaceholder;
    @SerializedName("FIELD_IFSC_DATAERROR")
    @Expose
    private String fieldIfscDataerror;
    @SerializedName("BUTTON_ACC_PROCEED")
    @Expose
    private String buttonAccProceed;
    @SerializedName("INVALID_IFSC")
    @Expose
    private String invalidIfsc;
    @SerializedName("INVALID_ACC")
    @Expose
    private String invalidAcc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldAccLabel() {
        return fieldAccLabel;
    }

    public void setFieldAccLabel(String fieldAccLabel) {
        this.fieldAccLabel = fieldAccLabel;
    }

    public String getFieldAccPlaceholder() {
        return fieldAccPlaceholder;
    }

    public void setFieldAccPlaceholder(String fieldAccPlaceholder) {
        this.fieldAccPlaceholder = fieldAccPlaceholder;
    }

    public String getFieldAccDataerror() {
        return fieldAccDataerror;
    }

    public void setFieldAccDataerror(String fieldAccDataerror) {
        this.fieldAccDataerror = fieldAccDataerror;
    }

    public String getFieldIfscLabel() {
        return fieldIfscLabel;
    }

    public void setFieldIfscLabel(String fieldIfscLabel) {
        this.fieldIfscLabel = fieldIfscLabel;
    }

    public String getFieldIfscPlaceholder() {
        return fieldIfscPlaceholder;
    }

    public void setFieldIfscPlaceholder(String fieldIfscPlaceholder) {
        this.fieldIfscPlaceholder = fieldIfscPlaceholder;
    }

    public String getFieldIfscDataerror() {
        return fieldIfscDataerror;
    }

    public void setFieldIfscDataerror(String fieldIfscDataerror) {
        this.fieldIfscDataerror = fieldIfscDataerror;
    }

    public String getButtonAccProceed() {
        return buttonAccProceed;
    }

    public void setButtonAccProceed(String buttonAccProceed) {
        this.buttonAccProceed = buttonAccProceed;
    }

    public String getInvalidIfsc() {
        return invalidIfsc;
    }

    public void setInvalidIfsc(String invalidIfsc) {
        this.invalidIfsc = invalidIfsc;
    }

    public String getInvalidAcc() {
        return invalidAcc;
    }

    public void setInvalidAcc(String invalidAcc) {
        this.invalidAcc = invalidAcc;
    }

    public String getDesc() {
        return desc;
    }

    public String getInfo() {
        return info;
    }
}
