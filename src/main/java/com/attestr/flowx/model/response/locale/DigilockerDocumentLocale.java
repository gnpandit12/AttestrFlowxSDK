
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DigilockerDocumentLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("CERTIFICATE_TITLE")
    @Expose
    private String certificateTitle;
    @SerializedName("ISSUER_TITLE")
    @Expose
    private String issuerTitle;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;
    @SerializedName("ISSUER_UNAVAILABLE")
    @Expose
    private String issuerUnavailable;
    @SerializedName("ISSUER_MISMATCH")
    @Expose
    private String issuerMismatch;
    @SerializedName("AADHAAR_NOT_LINKED")
    @Expose
    private String aadhaarNotLinked;
    @SerializedName("INVALID_VPA")
    @Expose
    private String invalidVpa;

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

    public String getCertificateTitle() {
        return certificateTitle;
    }

    public void setCertificateTitle(String certificateTitle) {
        this.certificateTitle = certificateTitle;
    }

    public String getIssuerTitle() {
        return issuerTitle;
    }

    public void setIssuerTitle(String issuerTitle) {
        this.issuerTitle = issuerTitle;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

    public String getIssuerUnavailable() {
        return issuerUnavailable;
    }

    public void setIssuerUnavailable(String issuerUnavailable) {
        this.issuerUnavailable = issuerUnavailable;
    }

    public String getIssuerMismatch() {
        return issuerMismatch;
    }

    public void setIssuerMismatch(String issuerMismatch) {
        this.issuerMismatch = issuerMismatch;
    }

    public String getAadhaarNotLinked() {
        return aadhaarNotLinked;
    }

    public void setAadhaarNotLinked(String aadhaarNotLinked) {
        this.aadhaarNotLinked = aadhaarNotLinked;
    }

    public String getInvalidVpa() {
        return invalidVpa;
    }

    public void setInvalidVpa(String invalidVpa) {
        this.invalidVpa = invalidVpa;
    }

}
