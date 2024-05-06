package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class MasterDirectorLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_REG_LABEL")
    @Expose
    private String fieldRegLabel;
    @SerializedName("FIELD_REG_PLACEHOLDER")
    @Expose
    private String fieldRegPlaceholder;
    @SerializedName("FIELD_REG_DATAERROR")
    @Expose
    private String fieldRegDataerror;
    @SerializedName("BUTTON_REG_PROCEED")
    @Expose
    private String buttonRegProceed;
    @SerializedName("INVALID_REG")
    @Expose
    private String invalidReg;

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

    public String getFieldRegLabel() {
        return fieldRegLabel;
    }

    public void setFieldRegLabel(String fieldRegLabel) {
        this.fieldRegLabel = fieldRegLabel;
    }

    public String getFieldRegPlaceholder() {
        return fieldRegPlaceholder;
    }

    public void setFieldRegPlaceholder(String fieldRegPlaceholder) {
        this.fieldRegPlaceholder = fieldRegPlaceholder;
    }

    public String getFieldRegDataerror() {
        return fieldRegDataerror;
    }

    public void setFieldRegDataerror(String fieldRegDataerror) {
        this.fieldRegDataerror = fieldRegDataerror;
    }

    public String getButtonRegProceed() {
        return buttonRegProceed;
    }

    public void setButtonRegProceed(String buttonRegProceed) {
        this.buttonRegProceed = buttonRegProceed;
    }

    public String getInvalidReg() {
        return invalidReg;
    }

    public void setInvalidReg(String invalidReg) {
        this.invalidReg = invalidReg;
    }

}
