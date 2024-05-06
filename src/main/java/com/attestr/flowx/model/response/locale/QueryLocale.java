
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QueryLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("LABEL_TEXT")
    @Expose
    private String labelText;
    @SerializedName("FIELD_RESPONSE_LABEL")
    @Expose
    private String fieldResponseLabel;
    @SerializedName("FIELD_RESPONSE_PLACEHOLDER")
    @Expose
    private String fieldResponsePlaceholder;
    @SerializedName("FIELD_RESPONSE_DATAERROR")
    @Expose
    private String fieldResponseDataerror;
    @SerializedName("FIELD_DOCUMENTS_LABEL")
    @Expose
    private String fieldDocumentsLabel;
    @SerializedName("FIELD_DOCUMENTS_DATAERROR")
    @Expose
    private String fieldDocumentsDataerror;
    @SerializedName("BUTTON_SUBMIT")
    @Expose
    private String buttonSubmit;

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

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getFieldResponseLabel() {
        return fieldResponseLabel;
    }

    public void setFieldResponseLabel(String fieldResponseLabel) {
        this.fieldResponseLabel = fieldResponseLabel;
    }

    public String getFieldResponsePlaceholder() {
        return fieldResponsePlaceholder;
    }

    public void setFieldResponsePlaceholder(String fieldResponsePlaceholder) {
        this.fieldResponsePlaceholder = fieldResponsePlaceholder;
    }

    public String getFieldResponseDataerror() {
        return fieldResponseDataerror;
    }

    public void setFieldResponseDataerror(String fieldResponseDataerror) {
        this.fieldResponseDataerror = fieldResponseDataerror;
    }

    public String getFieldDocumentsLabel() {
        return fieldDocumentsLabel;
    }

    public void setFieldDocumentsLabel(String fieldDocumentsLabel) {
        this.fieldDocumentsLabel = fieldDocumentsLabel;
    }

    public String getFieldDocumentsDataerror() {
        return fieldDocumentsDataerror;
    }

    public void setFieldDocumentsDataerror(String fieldDocumentsDataerror) {
        this.fieldDocumentsDataerror = fieldDocumentsDataerror;
    }

    public String getButtonSubmit() {
        return buttonSubmit;
    }

    public void setButtonSubmit(String buttonSubmit) {
        this.buttonSubmit = buttonSubmit;
    }

}
