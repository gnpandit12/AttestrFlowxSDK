
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileOtpLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_MOBILE_LABEL")
    @Expose
    private String fieldMobileLabel;
    @SerializedName("FIELD_MOBILE_PLACEHOLDER")
    @Expose
    private String fieldMobilePlaceholder;
    @SerializedName("FIELD_MOBILE_DATAERROR")
    @Expose
    private String fieldMobileDataerror;
    @SerializedName("FIELD_OTP_LABEL")
    @Expose
    private String fieldOtpLabel;
    @SerializedName("FIELD_OTP_PLACEHOLDER")
    @Expose
    private String fieldOtpPlaceholder;
    @SerializedName("FIELD_OTP_DATAERROR")
    @Expose
    private String fieldOtpDataerror;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;
    @SerializedName("BUTTON_SUBMIT")
    @Expose
    private String buttonSubmit;
    @SerializedName("INVALID_OTP")
    @Expose
    private String invalidOtp;
    @SerializedName("EXPIRED_OTP")
    @Expose
    private String expiredOtp;
    @SerializedName("RESEND_COUNT_EXCEEDED")
    @Expose
    private String resendCountExceeded;
    @SerializedName("BUTTON_GEN")
    @Expose
    private String buttonGen;
    @SerializedName("BUTTON_RESEND")
    @Expose
    private String buttonResend;
    @SerializedName("BUTTON_REGEN")
    @Expose
    private String buttonRegen;

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

    public String getFieldMobileLabel() {
        return fieldMobileLabel;
    }

    public void setFieldMobileLabel(String fieldMobileLabel) {
        this.fieldMobileLabel = fieldMobileLabel;
    }

    public String getFieldMobilePlaceholder() {
        return fieldMobilePlaceholder;
    }

    public void setFieldMobilePlaceholder(String fieldMobilePlaceholder) {
        this.fieldMobilePlaceholder = fieldMobilePlaceholder;
    }

    public String getFieldMobileDataerror() {
        return fieldMobileDataerror;
    }

    public void setFieldMobileDataerror(String fieldMobileDataerror) {
        this.fieldMobileDataerror = fieldMobileDataerror;
    }

    public String getFieldOtpLabel() {
        return fieldOtpLabel;
    }

    public void setFieldOtpLabel(String fieldOtpLabel) {
        this.fieldOtpLabel = fieldOtpLabel;
    }

    public String getFieldOtpPlaceholder() {
        return fieldOtpPlaceholder;
    }

    public void setFieldOtpPlaceholder(String fieldOtpPlaceholder) {
        this.fieldOtpPlaceholder = fieldOtpPlaceholder;
    }

    public String getFieldOtpDataerror() {
        return fieldOtpDataerror;
    }

    public void setFieldOtpDataerror(String fieldOtpDataerror) {
        this.fieldOtpDataerror = fieldOtpDataerror;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

    public String getButtonSubmit() {
        return buttonSubmit;
    }

    public void setButtonSubmit(String buttonSubmit) {
        this.buttonSubmit = buttonSubmit;
    }

    public String getInvalidOtp() {
        return invalidOtp;
    }

    public void setInvalidOtp(String invalidOtp) {
        this.invalidOtp = invalidOtp;
    }

    public String getExpiredOtp() {
        return expiredOtp;
    }

    public void setExpiredOtp(String expiredOtp) {
        this.expiredOtp = expiredOtp;
    }

    public String getResendCountExceeded() {
        return resendCountExceeded;
    }

    public void setResendCountExceeded(String resendCountExceeded) {
        this.resendCountExceeded = resendCountExceeded;
    }

    public String getButtonGen() {
        return buttonGen;
    }

    public void setButtonGen(String buttonGen) {
        this.buttonGen = buttonGen;
    }

    public String getButtonResend() {
        return buttonResend;
    }

    public void setButtonResend(String buttonResend) {
        this.buttonResend = buttonResend;
    }

    public String getButtonRegen() {
        return buttonRegen;
    }

    public void setButtonRegen(String buttonRegen) {
        this.buttonRegen = buttonRegen;
    }

}
