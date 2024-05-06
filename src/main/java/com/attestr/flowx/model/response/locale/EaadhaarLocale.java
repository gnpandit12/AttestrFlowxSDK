
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EaadhaarLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("WAIT_TEXT")
    @Expose
    private String waitText;
    @SerializedName("DOWNLOAD_TITLE")
    @Expose
    private String downloadTitle;
    @SerializedName("DOWNLOAD_TEXT")
    @Expose
    private String downloadText;
    @SerializedName("NOT_AVAILABLE")
    @Expose
    private String notAvailable;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;
    @SerializedName("BUTTON_DOWNLOAD")
    @Expose
    private String buttonDownload;
    @SerializedName("FIELD_DATAERROR")
    @Expose
    private String fieldDataerror;
    @SerializedName("STEP_ONE")
    @Expose
    private String stepOne;
    @SerializedName("STEP_ONE_LINK_LABEL")
    @Expose
    private String stepOneLinkLabel;
    @SerializedName("STEP_ONE_LINK")
    @Expose
    private String stepOneLink;
    @SerializedName("STEP_TWO")
    @Expose
    private String stepTwo;
    @SerializedName("STEP_TWO_LINK_LABEL")
    @Expose
    private String stepTwoLinkLabel;
    @SerializedName("STEP_TWO_LINK")
    @Expose
    private String stepTwoLink;
    @SerializedName("STEP_THREE")
    @Expose
    private String stepThree;

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

    public String getWaitText() {
        return waitText;
    }

    public void setWaitText(String waitText) {
        this.waitText = waitText;
    }

    public String getDownloadTitle() {
        return downloadTitle;
    }

    public void setDownloadTitle(String downloadTitle) {
        this.downloadTitle = downloadTitle;
    }

    public String getDownloadText() {
        return downloadText;
    }

    public void setDownloadText(String downloadText) {
        this.downloadText = downloadText;
    }

    public String getNotAvailable() {
        return notAvailable;
    }

    public void setNotAvailable(String notAvailable) {
        this.notAvailable = notAvailable;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

    public String getButtonDownload() {
        return buttonDownload;
    }

    public void setButtonDownload(String buttonDownload) {
        this.buttonDownload = buttonDownload;
    }

    public String getFieldDataerror() {
        return fieldDataerror;
    }

    public void setFieldDataerror(String fieldDataerror) {
        this.fieldDataerror = fieldDataerror;
    }

    public String getStepOne() {
        return stepOne;
    }

    public void setStepOne(String stepOne) {
        this.stepOne = stepOne;
    }

    public String getStepOneLinkLabel() {
        return stepOneLinkLabel;
    }

    public void setStepOneLinkLabel(String stepOneLinkLabel) {
        this.stepOneLinkLabel = stepOneLinkLabel;
    }

    public String getStepOneLink() {
        return stepOneLink;
    }

    public void setStepOneLink(String stepOneLink) {
        this.stepOneLink = stepOneLink;
    }

    public String getStepTwo() {
        return stepTwo;
    }

    public void setStepTwo(String stepTwo) {
        this.stepTwo = stepTwo;
    }

    public String getStepTwoLinkLabel() {
        return stepTwoLinkLabel;
    }

    public void setStepTwoLinkLabel(String stepTwoLinkLabel) {
        this.stepTwoLinkLabel = stepTwoLinkLabel;
    }

    public String getStepTwoLink() {
        return stepTwoLink;
    }

    public void setStepTwoLink(String stepTwoLink) {
        this.stepTwoLink = stepTwoLink;
    }

    public String getStepThree() {
        return stepThree;
    }

    public void setStepThree(String stepThree) {
        this.stepThree = stepThree;
    }

}
