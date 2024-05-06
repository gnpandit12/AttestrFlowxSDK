package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class AmlPersonLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_FNAME_LABEL")
    @Expose
    private String fieldFnameLabel;
    @SerializedName("FIELD_FNAME_PLACEHOLDER")
    @Expose
    private String fieldFnamePlaceholder;
    @SerializedName("FIELD_FNAME_DATAERROR")
    @Expose
    private String fieldFnameDataerror;
    @SerializedName("FIELD_LNAME_LABEL")
    @Expose
    private String fieldLnameLabel;
    @SerializedName("FIELD_LNAME_PLACEHOLDER")
    @Expose
    private String fieldLnamePlaceholder;
    @SerializedName("FIELD_LNAME_DATAERROR")
    @Expose
    private String fieldLnameDataerror;
    @SerializedName("FIELD_MNAME_LABEL")
    @Expose
    private String fieldMnameLabel;
    @SerializedName("FIELD_MNAME_PLACEHOLDER")
    @Expose
    private String fieldMnamePlaceholder;
    @SerializedName("FIELD_MNAME_DATAERROR")
    @Expose
    private String fieldMnameDataerror;
    @SerializedName("FIELD_DOB_LABEL")
    @Expose
    private String fieldDobLabel;
    @SerializedName("FIELD_DOB_PLACEHOLDER")
    @Expose
    private String fieldDobPlaceholder;
    @SerializedName("FIELD_DOB_DATAERROR")
    @Expose
    private String fieldDobDataerror;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;

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

    public String getFieldFnameLabel() {
        return fieldFnameLabel;
    }

    public void setFieldFnameLabel(String fieldFnameLabel) {
        this.fieldFnameLabel = fieldFnameLabel;
    }

    public String getFieldFnamePlaceholder() {
        return fieldFnamePlaceholder;
    }

    public void setFieldFnamePlaceholder(String fieldFnamePlaceholder) {
        this.fieldFnamePlaceholder = fieldFnamePlaceholder;
    }

    public String getFieldFnameDataerror() {
        return fieldFnameDataerror;
    }

    public void setFieldFnameDataerror(String fieldFnameDataerror) {
        this.fieldFnameDataerror = fieldFnameDataerror;
    }

    public String getFieldLnameLabel() {
        return fieldLnameLabel;
    }

    public void setFieldLnameLabel(String fieldLnameLabel) {
        this.fieldLnameLabel = fieldLnameLabel;
    }

    public String getFieldLnamePlaceholder() {
        return fieldLnamePlaceholder;
    }

    public void setFieldLnamePlaceholder(String fieldLnamePlaceholder) {
        this.fieldLnamePlaceholder = fieldLnamePlaceholder;
    }

    public String getFieldLnameDataerror() {
        return fieldLnameDataerror;
    }

    public void setFieldLnameDataerror(String fieldLnameDataerror) {
        this.fieldLnameDataerror = fieldLnameDataerror;
    }

    public String getFieldMnameLabel() {
        return fieldMnameLabel;
    }

    public void setFieldMnameLabel(String fieldMnameLabel) {
        this.fieldMnameLabel = fieldMnameLabel;
    }

    public String getFieldMnamePlaceholder() {
        return fieldMnamePlaceholder;
    }

    public void setFieldMnamePlaceholder(String fieldMnamePlaceholder) {
        this.fieldMnamePlaceholder = fieldMnamePlaceholder;
    }

    public String getFieldMnameDataerror() {
        return fieldMnameDataerror;
    }

    public void setFieldMnameDataerror(String fieldMnameDataerror) {
        this.fieldMnameDataerror = fieldMnameDataerror;
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

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

}
