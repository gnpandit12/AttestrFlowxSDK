
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class PanLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_PAN_LABEL")
    @Expose
    private String fieldPanLabel;
    @SerializedName("FIELD_PAN_PLACEHOLDER")
    @Expose
    private String fieldPanPlaceholder;
    @SerializedName("FIELD_PAN_DATAERROR")
    @Expose
    private String fieldPanDataerror;
    @SerializedName("BUTTON_PAN_PROCEED")
    @Expose
    private String buttonPanProceed;
    @SerializedName("INVALID_PAN")
    @Expose
    private String invalidPan;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldPanLabel() {
        return fieldPanLabel;
    }

    public void setFieldPanLabel(String fieldPanLabel) {
        this.fieldPanLabel = fieldPanLabel;
    }

    public String getFieldPanPlaceholder() {
        return fieldPanPlaceholder;
    }

    public void setFieldPanPlaceholder(String fieldPanPlaceholder) {
        this.fieldPanPlaceholder = fieldPanPlaceholder;
    }

    public String getFieldPanDataerror() {
        return fieldPanDataerror;
    }

    public void setFieldPanDataerror(String fieldPanDataerror) {
        this.fieldPanDataerror = fieldPanDataerror;
    }

    public String getButtonPanProceed() {
        return buttonPanProceed;
    }

    public void setButtonPanProceed(String buttonPanProceed) {
        this.buttonPanProceed = buttonPanProceed;
    }

    public String getInvalidPan() {
        return invalidPan;
    }

    public void setInvalidPan(String invalidPan) {
        this.invalidPan = invalidPan;
    }

    public String getDesc() {
        return desc;
    }

    public String getInfo() {
        return info;
    }

}
