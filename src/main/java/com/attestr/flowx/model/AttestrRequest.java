package com.attestr.flowx.model;

import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.GlobalVariables;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class AttestrRequest {

    public static final String TAG = "Request_class";
    private static String actionInitRequest;
    private static String actionStageNextRequest;
    private static String actionStageInitRequest;
    private static String actionSkipFlow;
    private static String actionDestroy;
    private static String actionSubmitPan;
    private static String actionSubmitVpa;
    private static String actionSubmitBankAccount;
    private static String actionNotifyEvent;
    private static String actionSubmitVoterID;
    private static String actionSubmitMasterBusiness;
    private static String actionSubmitMasterDirector;
    private static String actionSubmitBusinessAML;
    private static String actionSubmitPersonAML;
    private static String actionSubmitMediaHandshake;
    private static String actionSubmitUidai;
    private static String actionMediaHandshake;
    private static String actionFaceMatchSubmit;
    private static String actionEaadhaarSubmit;
    private static String actionQRGenerate;
    private static String actionDigiDocumentSubmit;
    private static String actionEcourtPersonSubmit;
    private static String actionEcourtBusinessSubmit;
    private static String actionDigitalAddressSubmit;
    private static String actionGenerateMobileOtp;
    private static String actionResendMobileOtp;
    private static String actionResendEmailOtp;
    private static String actionVerifyMobileOtp;
    private static String actionKeepAlive;
    private static String actionQuerySubmit;
    private static String actionDocumentMediaHandshake;
    private static String actionEducationSubmit;
    private static String actionEmploymentSubmit;
    private static String actionGenerateEmailOtp;
    private static String actionFssaiSubmit;
    private static String actionIpvSubmit;
    private static String actionSubmitDrivingLicense;
    private static String actionIPGeolocation;
    private static String actionReconnect;
    private static String actionePanSubmit;
    private static String actionQRRedirect;

    public static String actionInit() {
        try {
            JSONObject data = new JSONObject();
            JSONObject queryJsonObj = null;
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("retry", GlobalVariables.isRetry);
            if (GlobalVariables.queryMap != null) {
                queryJsonObj = new JSONObject(GlobalVariables.queryMap);
            }
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_INIT);
            data.put("qr", queryJsonObj);
            action.put("data", data);
            actionInitRequest = action.toString();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return  actionInitRequest;
    }

    public static String stageNext() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("cur", GlobalVariables.currentStageId == null ? JSONObject.NULL : GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_STAGE_NEXT);
            action.put("data", data);
            actionStageNextRequest = action.toString();
    } catch (Exception e){
            HandleException.handleInternalException(e.toString());
    }
    return actionStageNextRequest;
    }

    public static String stageInit(String stageID) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", stageID);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_STAGE_INIT);
            action.put("data", data);
            actionStageInitRequest = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionStageInitRequest;
    }

    public static String actionSkipFlow() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SKIP_FLOW);
            action.put("data", data);
            actionSkipFlow = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSkipFlow;
    }

    public static String actionDestroy() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_DESTROY);
            action.put("data", data);
            actionDestroy = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionDestroy;
    }

    public static String actionSubmitPan (
            String userPanNumber,
            boolean extended
    ) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("pan", userPanNumber);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("extended", extended);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_PAN);
            action.put("data", data);
            actionSubmitPan = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitPan;
    }

    public static String actionSubmitVpa(String userVpa) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("vpa", userVpa);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_VPA);
            action.put("data", data);
            actionSubmitVpa = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitVpa;
    }

    public static String actionNotifyEvent(
            JSONObject responseJSONObject,
            String event
    ) {
        try {
            JSONObject data = new JSONObject();
            JSONObject payload = new JSONObject();
            data.put("hs", GlobalVariables.handshakeId);
            data.put("cl", GlobalVariables.clientKey);
            if (GlobalVariables.NOTIFY_EVENT_COMPLETED.equals(event)) {
                data.put("event", GlobalVariables.NOTIFY_EVENT_COMPLETED);
            } else if (GlobalVariables.NOTIFY_EVENT_SKIPPED.equals(event)) {
                data.put("event", GlobalVariables.NOTIFY_EVENT_SKIPPED);
            } else if (GlobalVariables.NOTIFY_EVENT_ERRORED.equals(event)) {
                data.put("event", GlobalVariables.NOTIFY_EVENT_ERRORED);
            }
            if (GlobalVariables.NOTIFY_EVENT_SKIPPED.equals(event)) {
                if (responseJSONObject == null) {
                    payload.put("code", 1001);
                } else {
                    payload.put("data", responseJSONObject);
                }
            }
            JSONObject action = new JSONObject();
            if (GlobalVariables.NOTIFY_EVENT_ERRORED.equals(event)) {
                if (responseJSONObject != null) {
                    data.put("payload", responseJSONObject);
                } else {
                    data.put("payload", payload);
                }
            }
            action.put("action", GlobalVariables.ACTION_NOTIFY);
            action.put("data", data);
            actionNotifyEvent = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionNotifyEvent;
    }


    public static String actionSubmitBankAccount(String bankAccountNumber, String bankAccountIfsc) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("acc", bankAccountNumber);
            data.put("ifsc", bankAccountIfsc);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_BANK_ACC_SUBMIT);
            action.put("data", data);
            actionSubmitBankAccount = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitBankAccount;
    }

    public static String actionSubmitMasterBusiness(String cinNumber) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("reg", cinNumber);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_MASTER_BUSINESS);
            action.put("data", data);
            actionSubmitMasterBusiness = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitMasterBusiness;
    }

    public static String actionSubmitMasterDirector(String dinNumber) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("reg", dinNumber);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_MASTER_DIRECTOR);
            action.put("data", data);
            actionSubmitMasterDirector = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitMasterDirector;
    }

    public static String actionSubmitBusinessAML(String companyName) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("businessName", companyName);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_AML_BUSINESS);
            action.put("data", data);
            actionSubmitBusinessAML = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitBusinessAML;
    }

    public static String actionSubmitPersonAML(
            String firstName,
            String middleName,
            String lastName,
            String dateOfBirth) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("firstName", firstName);
            data.put("middleName", middleName);
            data.put("lastName", lastName);
            data.put("dob", dateOfBirth);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_AML_PERSON);
            action.put("data", data);
            actionSubmitPersonAML = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitPersonAML;
    }


    public static String actionSubmitMediaHandshake() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_MEDIA_HANDSHAKE);
            action.put("data", data);
            actionSubmitMediaHandshake = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitMediaHandshake;
    }

    public static String actionSubmitUidai(String mediaID) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("media", mediaID);
            data.put("uuid", GlobalVariables.userAadhaarNumber);
            data.put("code", GlobalVariables.xmlShareCode);
            data.put("email", GlobalVariables.userAadhaarLinkedEmail);
            data.put("mobile", GlobalVariables.userAadhaarLinkedMobile);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_UIDAI);
            action.put("data", data);
            actionSubmitUidai = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitUidai;
    }


    public static String actionMediaHandshake(String metadata) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("meta", metadata);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_MEDIA_HANDSHAKE);
            action.put("data", data);
            actionMediaHandshake = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionMediaHandshake;
    }

    public static String actionFaceMatchSubmit(String selectedDocument,
                                               Double threshold,
                                               String targetFront,
                                               String targetBack) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("src", GlobalVariables.selfieSource);
            data.put("targetFront", targetFront);
            data.put("targetBack", targetBack);
            data.put("type", selectedDocument);
            data.put("threshold", threshold);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_FACE_MATCH);
            action.put("data", data);
            actionFaceMatchSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionFaceMatchSubmit;
    }


    public static String actionEaadhaarSubmit() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_EAADHAAR);
            action.put("data", data);
            actionEaadhaarSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionEaadhaarSubmit;
    }


    public static String actionQRGenerate(boolean dev) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("dev", String.valueOf(dev));
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_QR_GEN);
            action.put("data", data);
            actionQRGenerate = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionQRGenerate;
    }

    public static String actionDigiDocumentSubmit(HashMap<String, String> paramsMap) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("issuer", GlobalVariables.issuer);
            data.put("document",  GlobalVariables.docType);
            data.put("params", new JSONObject(paramsMap));
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_DIGILOCKER_DOC);
            action.put("data", data);
            actionDigiDocumentSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionDigiDocumentSubmit;
    }

    public static String actionEcourtPersonSubmit(HashMap<String, String> paramsMap) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("name", paramsMap.get("name"));
            data.put("fatherName", paramsMap.get("fatherName"));
            data.put("birthDate", paramsMap.get("birthDate"));
            data.put("address", paramsMap.get("address"));
            data.put("additionalAddress",
                    paramsMap.get("additionalAddress") == null ?
                    JSONObject.NULL : paramsMap.get("additionalAddress"));
            data.put("extended", Boolean.parseBoolean(paramsMap.get("extended")));
            data.put("priority", paramsMap.get("priority") == null ? false: paramsMap.get("priority"));
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_ECOURT_PERSON);
            action.put("data", data);
            actionEcourtPersonSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionEcourtPersonSubmit;
    }

    public static String actionEcourtBusinessSubmit(HashMap<String, String> paramsMap) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("extended", Boolean.parseBoolean(paramsMap.get("extended")));
            data.put("businessName", paramsMap.get("businessName"));
            data.put("reg", paramsMap.get("reg") == null ? JSONObject.NULL : paramsMap.get("reg"));
            data.put("address", paramsMap.get("address"));
            data.put("priority", paramsMap.get("priority") == null ? false: paramsMap.get("priority"));
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_ECOURT_BUSINESS);
            action.put("data", data);
            actionEcourtBusinessSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionEcourtBusinessSubmit;
    }

    public static String actionDigitalAddressSubmit(Map<String, String> requestMap,
                                                    JSONArray entranceImagesMediIDList,
                                                    JSONArray buildingImagesMediaList) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("address", requestMap.get("address"));
            data.put("stayingSinceMonth", requestMap.get("stayingSinceMonth"));
            data.put("stayingSinceYear", requestMap.get("stayingSinceYear"));
            data.put("docType", requestMap.get("docType"));
            data.put("frontDoc", GlobalVariables.digitalAddressFrontDocSource);
            data.put("backDoc", GlobalVariables.digitalAddressBackDocSource);
            data.put("extOne", entranceImagesMediIDList);
            data.put("extTwo", buildingImagesMediaList);
            data.put("origLatitude", Double.parseDouble(Objects.requireNonNull(requestMap.get("origLatitude"))));
            data.put("origLongitude", Double.parseDouble(Objects.requireNonNull(requestMap.get("origLongitude"))));
            data.put("latitude", Double.parseDouble(Objects.requireNonNull(requestMap.get("latitude"))));
            data.put("longitude", Double.parseDouble(Objects.requireNonNull(requestMap.get("longitude"))));
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_DIGITAL_ADDRESS);
            action.put("data", data);
            actionDigitalAddressSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionDigitalAddressSubmit;
    }


    public static String actionGenerateMobileOtp() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("mobile", GlobalVariables.mobileOTPVerificationNumber);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_GENERATE_MOBILE_OTP);
            action.put("data", data);
            actionGenerateMobileOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionGenerateMobileOtp;
    }

    public static String actionGenerateEmailOtp() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("email", GlobalVariables.emailOTPVerificationAddress);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_GENERATE_EMAIL_OTP);
            action.put("data", data);
            actionGenerateEmailOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionGenerateEmailOtp;
    }

    public static String actionResendMobileOtp(String id) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("req", id);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_MOBILE_OTP_RESEND);
            action.put("data", data);
            actionResendMobileOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionResendMobileOtp;
    }

    public static String actionResendEmailOtp(String id) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("req", id);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_EMAIL_OTP_RESEND);
            action.put("data", data);
            actionResendEmailOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionResendEmailOtp;
    }

    public static String actionVerifyMobileOtp(
            String otp,
            String id) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("req", id);
            data.put("otp", otp);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_MOBILE_OTP_VERIFY);
            action.put("data", data);
            actionVerifyMobileOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionVerifyMobileOtp;
    }

    public static String actionVerifyEmailOtp(
            String otp,
            String id) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("req", id);
            data.put("otp", otp);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_EMAIL_OTP_VERIFY);
            action.put("data", data);
            actionVerifyMobileOtp = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionVerifyMobileOtp;
    }

    public static String actionKeepAlive(){
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_KEEP_ALIVE);
            action.put("data", data);
            actionKeepAlive = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionKeepAlive;
    }

    public static String actionDocumentMediaHandshake(boolean isQueryMediaHandshake) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("query", isQueryMediaHandshake ? GlobalVariables.queryID: JSONObject.NULL);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_MEDIA_HANDSHAKE);
            action.put("data", data);
            actionDocumentMediaHandshake = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionDocumentMediaHandshake;
    }

    public static String actionQuerySubmit(String response,
                                           JSONArray documentMediaIDArray){
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("response", response);
            if (documentMediaIDArray != null){
                data.put("documents", documentMediaIDArray);
            }
            data.put("query", GlobalVariables.queryID);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_QUERY_SUBMIT);
            action.put("data", data);
            actionQuerySubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionQuerySubmit;
    }

    public static String actionEducationSubmit(
            Map<String, String> requestParamsMap,
            JSONArray documentMediaIDArray) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("level", requestParamsMap.get("level"));
            data.put("course", requestParamsMap.get("course") == null ? JSONObject.NULL : requestParamsMap.get("course"));
            data.put("courseName", requestParamsMap.get("courseName"));
            data.put("reg", requestParamsMap.get("reg"));
            data.put("startMonth", requestParamsMap.get("startMonth"));
            data.put("startYear", requestParamsMap.get("startYear"));
            data.put("ended", Boolean.parseBoolean(requestParamsMap.get("ended")));
            data.put("endedMonth", requestParamsMap.get("endedMonth") == null ? JSONObject.NULL : requestParamsMap.get("endedMonth"));
            data.put("endedYear", requestParamsMap.get("endedYear") == null ? JSONObject.NULL : requestParamsMap.get("endedYear"));
            data.put("documents", documentMediaIDArray);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_EDUCATION_SUBMIT);
            action.put("data", data);
            actionEducationSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionEducationSubmit;
    }


    public static String actionEmploymentSubmit(JSONArray criteriaValueArray) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("items", criteriaValueArray);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_EMPLOYMENT_SUBMIT);
            action.put("data", data);
            actionEmploymentSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionEmploymentSubmit;
    }

    public static String actionFssaiSubmit(
            String fssaiNumber,
            boolean fetchProducts) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("reg", fssaiNumber);
            data.put("fetchProducts", fetchProducts);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_FSSAI_SUBMIT);
            action.put("data", data);
            actionFssaiSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionFssaiSubmit;
    }

    public static String actionIpvSubmit(
            String videoMediaID,
            String imageMediaID,
            String otp,
            String startTimeStamp,
            String endTimeStamp,
            String latitude,
            String longitude
    ) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            data.put("videoMedia", videoMediaID);
            data.put("photoMedia", imageMediaID);
            data.put("otp", otp);
            data.put("startTs", startTimeStamp);
            data.put("endTs", endTimeStamp);
            if (latitude != null && longitude != null) {
                data.put("lat", latitude);
                data.put("lng", longitude);
            }
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_IPV_SUBMIT);
            action.put("data", data);
            actionIpvSubmit = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionIpvSubmit;
    }

    public static String actionSubmitVoterID(String voterIDNumber) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("epic", voterIDNumber);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_VOTER_ID);
            action.put("data", data);
            actionSubmitVoterID = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitVoterID;
    }


    public static String actionSubmitDrivingLicense(String drivingLicenseNumber,
                                                    String dateOfBirth) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("dl", drivingLicenseNumber);
            data.put("dob", dateOfBirth);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_SUBMIT_DRIVING_LICENSE);
            action.put("data", data);
            actionSubmitDrivingLicense = action.toString();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return actionSubmitDrivingLicense;
    }

    public static String actionIPGeolocation() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_IP_GEOLOCATION);
            action.put("data", data);
            actionIPGeolocation = action.toString();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return  actionIPGeolocation;
    }

    public static String actionReconnect() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("cid", GlobalVariables.currentConnectID);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_RECONNECT);
            action.put("data", data);
            actionReconnect = action.toString();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return  actionReconnect;
    }

    public static String actionePanSubmit(
            String panNumber,
            boolean downloadPdf) {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("pan", panNumber);
            data.put("downloadPdf", downloadPdf);
            data.put("stage", GlobalVariables.currentStageId);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_EPAN_SUBMIT);
            action.put("data", data);
            actionePanSubmit = action.toString();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return actionePanSubmit;
    }

    public static String actionQRRedirect() {
        try {
            JSONObject data = new JSONObject();
            data.put("cl", GlobalVariables.clientKey);
            data.put("hs", GlobalVariables.handshakeId);
            data.put("lc", GlobalVariables.locale);
            data.put("stage", JSONObject.NULL);
            JSONObject action = new JSONObject();
            action.put("action", GlobalVariables.ACTION_QR_REDIRECT);
            action.put("data", data);
            actionQRRedirect = action.toString();
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
        return actionQRRedirect;
    }

}
