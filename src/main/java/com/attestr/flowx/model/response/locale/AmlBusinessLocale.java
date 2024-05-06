package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class AmlBusinessLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_ENTITY_LABEL")
    @Expose
    private String fieldEntityLabel;
    @SerializedName("FIELD_ENTITY_PLACEHOLDER")
    @Expose
    private String fieldEntityPlaceholder;
    @SerializedName("FIELD_ENTITY_DATAERROR")
    @Expose
    private String fieldEntityDataerror;
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

    public String getFieldEntityLabel() {
        return fieldEntityLabel;
    }

    public void setFieldEntityLabel(String fieldEntityLabel) {
        this.fieldEntityLabel = fieldEntityLabel;
    }

    public String getFieldEntityPlaceholder() {
        return fieldEntityPlaceholder;
    }

    public void setFieldEntityPlaceholder(String fieldEntityPlaceholder) {
        this.fieldEntityPlaceholder = fieldEntityPlaceholder;
    }

    public String getFieldEntityDataerror() {
        return fieldEntityDataerror;
    }

    public void setFieldEntityDataerror(String fieldEntityDataerror) {
        this.fieldEntityDataerror = fieldEntityDataerror;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

}
