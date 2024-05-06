
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FssaiLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_FSSAI_LABEL")
    @Expose
    private String fieldFssaiLabel;
    @SerializedName("FIELD_FSSAI_PLACEHOLDER")
    @Expose
    private String fieldFssaiPlaceholder;
    @SerializedName("FIELD_FSSAI_DATAERROR")
    @Expose
    private String fieldFssaiDataerror;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;
    @SerializedName("INVALID_FSSAI")
    @Expose
    private String invalidFssai;

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

    public String getFieldFssaiLabel() {
        return fieldFssaiLabel;
    }

    public void setFieldFssaiLabel(String fieldFssaiLabel) {
        this.fieldFssaiLabel = fieldFssaiLabel;
    }

    public String getFieldFssaiPlaceholder() {
        return fieldFssaiPlaceholder;
    }

    public void setFieldFssaiPlaceholder(String fieldFssaiPlaceholder) {
        this.fieldFssaiPlaceholder = fieldFssaiPlaceholder;
    }

    public String getFieldFssaiDataerror() {
        return fieldFssaiDataerror;
    }

    public void setFieldFssaiDataerror(String fieldFssaiDataerror) {
        this.fieldFssaiDataerror = fieldFssaiDataerror;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

    public String getInvalidFssai() {
        return invalidFssai;
    }

    public void setInvalidFssai(String invalidFssai) {
        this.invalidFssai = invalidFssai;
    }

}
