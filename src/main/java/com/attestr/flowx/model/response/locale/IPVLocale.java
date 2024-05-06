
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IPVLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;

    @SerializedName("DESC")
    @Expose
    private String desc;

    @SerializedName("INFO")
    @Expose
    private String info;

    @SerializedName("FIELD_VIDEO_LABEL")
    @Expose
    private String fieldVideoLabel;

    @SerializedName("FIELD_AUDIO_LABEL")
    @Expose
    private String fieldAudioLabel;

    @SerializedName("FIELD_AUDIO_VIDEO_LABEL")
    @Expose
    private String fieldAudioVideoLabel;

    @SerializedName("FIELD_VIDEO_PLACEHOLDER")
    @Expose
    private String fieldVideoPlaceholder;

    @SerializedName("FIELD_VIDEO_DATAERROR")
    @Expose
    private String fieldVideoDataerror;

    @SerializedName("FIELD_VIDEO_REPLACE")
    @Expose
    private String fieldVideoReplace;

    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;

    @SerializedName("INVALID_IPV")
    @Expose
    private String invalidIpv;

    @SerializedName("INSTRUCTION_VIDEO")
    @Expose
    private String instructionVideo;

    @SerializedName("INSTRUCTION_AUDIO")
    @Expose
    private String instructionAudio;

    @SerializedName("INSTRUCTION_AUDIO_VIDEO")
    @Expose
    private String instructionAudioVideo;

    @SerializedName("FIELD_CAPTURE_VIDEO_LABEL")
    @Expose
    private String fieldCaptureVideoLabel;

    public String getInstructionVideo() {
        return instructionVideo;
    }

    public String getInstructionAudio() {
        return instructionAudio;
    }

    public String getInstructionAudioVideo() {
        return instructionAudioVideo;
    }

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

    public String getFieldVideoPlaceholder() {return fieldVideoPlaceholder;}

    public String getFieldVideoDataerror() {
        return fieldVideoDataerror;
    }

    public String getFieldVideoReplace() {
        return fieldVideoReplace;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public String getInvalidIpv() {
        return invalidIpv;
    }

    public String getFieldVideoLabel() {
        return fieldVideoLabel;
    }

    public String getFieldAudioLabel() {
        return fieldAudioLabel;
    }

    public String getFieldAudioVideoLabel() {
        return fieldAudioVideoLabel;
    }

    public String getFieldCaptureVideoLabel() {
        return fieldCaptureVideoLabel;
    }

}
