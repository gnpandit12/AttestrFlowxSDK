
package com.attestr.flowx.model.response.common;

import com.attestr.flowx.model.response.locale.AmlBusinessLocale;
import com.attestr.flowx.model.response.locale.AmlPersonLocale;
import com.attestr.flowx.model.response.locale.BankAccountLocale;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.DigiLockerLocale;
import com.attestr.flowx.model.response.locale.DigilockerDocumentLocale;
import com.attestr.flowx.model.response.locale.DigitalAddressLocale;
import com.attestr.flowx.model.response.locale.DrivingLicenseLocale;
import com.attestr.flowx.model.response.locale.EaadhaarLocale;
import com.attestr.flowx.model.response.locale.EcourtBusinessLocale;
import com.attestr.flowx.model.response.locale.EcourtPersonLocale;
import com.attestr.flowx.model.response.locale.EducationVerificationLocale;
import com.attestr.flowx.model.response.locale.EmailOTPLocale;
import com.attestr.flowx.model.response.locale.EmploymentVerificationLocale;
import com.attestr.flowx.model.response.locale.FaceMatchLocale;
import com.attestr.flowx.model.response.locale.FssaiLocale;
import com.attestr.flowx.model.response.locale.GstinLocale;
import com.attestr.flowx.model.response.locale.IPVLocale;
import com.attestr.flowx.model.response.locale.MasterBusinessLocale;
import com.attestr.flowx.model.response.locale.MasterDirectorLocale;
import com.attestr.flowx.model.response.locale.MobileOtpLocale;
import com.attestr.flowx.model.response.locale.ModalLocale;
import com.attestr.flowx.model.response.locale.PanLocale;
import com.attestr.flowx.model.response.locale.QueryLocale;
import com.attestr.flowx.model.response.locale.SuccessLocale;
import com.attestr.flowx.model.response.locale.UidaiLocale;
import com.attestr.flowx.model.response.locale.VoterIDLocale;
import com.attestr.flowx.model.response.locale.VpaLocale;
import com.attestr.flowx.model.response.locale.WaitLocale;
import com.attestr.flowx.model.response.locale.ePanLocale;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class Locale {

    @SerializedName("COMMON")
    @Expose
    private CommonLocale commonLocale;
    @SerializedName("MODAL")
    @Expose
    private ModalLocale modalLocale;
    @SerializedName("SUCCESS")
    @Expose
    private SuccessLocale successLocale;
    @SerializedName("WAIT")
    @Expose
    private WaitLocale waitLocale;
    @SerializedName("STAGE_UIDAI")
    @Expose
    private UidaiLocale uidaiLocale;
    @SerializedName("STAGE_PAN")
    @Expose
    private PanLocale panLocale;
    @SerializedName("STAGE_BANK_ACC")
    @Expose
    private BankAccountLocale bankAccountLocale;
    @SerializedName("STAGE_VPA")
    @Expose
    private VpaLocale vpaLocale;
    @SerializedName("STAGE_GSTIN")
    @Expose
    private GstinLocale gstinLocale;
    @SerializedName("STAGE_MASTER_BUSINESS")
    @Expose
    private MasterBusinessLocale masterBusinessLocale;
    @SerializedName("STAGE_FACE_MATCH")
    @Expose
    private FaceMatchLocale faceMatchLocale;
    @SerializedName("STAGE_MASTER_DIRECTOR")
    @Expose
    private MasterDirectorLocale masterDirectorLocale;

    @SerializedName("STAGE_AML_BUSINESS")
    @Expose
    private AmlBusinessLocale amlBusinessLocale;
    @SerializedName("STAGE_AML_PERSON")
    @Expose
    private AmlPersonLocale amlPersonLocale;
    @SerializedName("STAGE_EAADHAAR")
    @Expose
    private EaadhaarLocale eaadhaarLocale;
    @SerializedName("DL")
    @Expose
    private DigiLockerLocale digiLockerLocale;
    @SerializedName("STAGE_DIGILOCKER_DOC")
    @Expose
    private DigilockerDocumentLocale digilockerDocumentLocale;
    @SerializedName("STAGE_ECOURT_PERSON")
    @Expose
    private EcourtPersonLocale eCourtPerson;
    @SerializedName("STAGE_ECOURT_BUSINESS")
    @Expose
    private EcourtBusinessLocale eCourtBusiness;
    @SerializedName("STAGE_DIGITAL_ADDRESS")
    @Expose
    private DigitalAddressLocale digitalAddressLocale;
    @SerializedName("STAGE_MOBILE_OTP")
    @Expose
    private MobileOtpLocale mobileOtpLocale;
    @SerializedName("QUERY")
    @Expose
    private QueryLocale queryLocale;
    @SerializedName("STAGE_EDUCATION")
    @Expose
    private EducationVerificationLocale educationVerificationLocale;
    @SerializedName("STAGE_EMPLOYMENT")
    @Expose
    private EmploymentVerificationLocale employmentVerificationLocale;
    @SerializedName("STAGE_EMAIL_OTP")
    @Expose
    private EmailOTPLocale emailOTPLocale;
    @SerializedName("STAGE_FSSAI")
    @Expose
    private FssaiLocale fssaiLocale;
    @SerializedName("STAGE_IPV")
    @Expose
    private IPVLocale ipvLocale;
    @SerializedName("STAGE_EPIC")
    @Expose
    private VoterIDLocale voterIDLocale;
    @SerializedName("STAGE_DL")
    @Expose
    private DrivingLicenseLocale drivingLicenseLocale;
    @SerializedName("STAGE_EPAN")
    @Expose
    private ePanLocale ePanLocale;

    public CommonLocale getCommon() {
        return commonLocale;
    }

    public void setCommon(CommonLocale commonLocale) {
        this.commonLocale = commonLocale;
    }

    public ModalLocale getModal() {
        return modalLocale;
    }

    public void setModal(ModalLocale modalLocale) {
        this.modalLocale = modalLocale;
    }

    public SuccessLocale getSuccess() {
        return successLocale;
    }

    public void setSuccess(SuccessLocale successLocale) {
        this.successLocale = successLocale;
    }

    public WaitLocale getWait() {
        return waitLocale;
    }

    public void setWait(WaitLocale waitLocale) {
        this.waitLocale = waitLocale;
    }

    public UidaiLocale getStageUidai() {
        return uidaiLocale;
    }

    public void setStageUidai(UidaiLocale uidaiLocale) {
        this.uidaiLocale = uidaiLocale;
    }

    public PanLocale getStagePan() {
        return panLocale;
    }

    public void setStagePan(PanLocale panLocale) {
        this.panLocale = panLocale;
    }

    public BankAccountLocale getStageAcc() {
        return bankAccountLocale;
    }

    public void setStageAcc(BankAccountLocale bankAccountLocale) {
        this.bankAccountLocale = bankAccountLocale;
    }

    public VpaLocale getStageVpa() {
        return vpaLocale;
    }

    public void setStageVpa(VpaLocale vpaLocale) {
        this.vpaLocale = vpaLocale;
    }

    public GstinLocale getStageGstin() {
        return gstinLocale;
    }

    public void setStageGstin(GstinLocale gstinLocale) {
        this.gstinLocale = gstinLocale;
    }

    public MasterBusinessLocale getMasterBusinessLocale() {
        return masterBusinessLocale;
    }

    public FaceMatchLocale getFaceMatchLocale() {
        return faceMatchLocale;
    }

    public MasterDirectorLocale getMasterDirectorLocale() {
        return masterDirectorLocale;
    }

    public AmlBusinessLocale getAmlBusinessLocale() {
        return amlBusinessLocale;
    }

    public AmlPersonLocale getAmlPersonLocale() {
        return amlPersonLocale;
    }

    public EaadhaarLocale getEaadhaarLocale() {
        return eaadhaarLocale;
    }

    public DigiLockerLocale getDigiLockerLocale() {
        return digiLockerLocale;
    }

    public DigilockerDocumentLocale getDigilockerDocumentLocale() {
        return digilockerDocumentLocale;
    }

    public EcourtPersonLocale geteCourtPerson() {
        return eCourtPerson;
    }

    public EcourtBusinessLocale geteCourtBusiness() {
        return eCourtBusiness;
    }

    public DigitalAddressLocale getDigitalAddressLocale() {
        return digitalAddressLocale;
    }

    public MobileOtpLocale getMobileOtpLocale() {
        return mobileOtpLocale;
    }

    public QueryLocale getQueryLocale() {
        return queryLocale;
    }

    public EducationVerificationLocale getEducationVerificationLocale() {
        return educationVerificationLocale;
    }

    public EmploymentVerificationLocale getEmploymentVerificationLocale() {
        return employmentVerificationLocale;
    }

    public EmailOTPLocale getEmailOTPLocale() {
        return emailOTPLocale;
    }

    public FssaiLocale getFssaiLocale() {
        return fssaiLocale;
    }

    public IPVLocale getIpvLocale() {
        return ipvLocale;
    }


    public VoterIDLocale getVoterIDLocale() {
        return voterIDLocale;
    }

    public DrivingLicenseLocale getDrivingLicenseLocale() {
        return drivingLicenseLocale;
    }

    public ePanLocale getePanLocale() {
        return ePanLocale;
    }
}
