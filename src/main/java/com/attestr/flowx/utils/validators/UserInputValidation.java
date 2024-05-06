package com.attestr.flowx.utils.validators;

import android.text.TextUtils;
import android.util.Patterns;

import com.attestr.flowx.utils.GlobalVariables;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class UserInputValidation {

    public static boolean isBankAccountNumberValid(String bankAccountNumber){
        if (!TextUtils.isEmpty(bankAccountNumber)) {
            return GlobalVariables.BANK_ACCOUNT_VALIDATION_REGEX_PATTERN.matcher(bankAccountNumber).matches();
        }
        return false;
    }

    public static boolean isIfscCodeValid(String ifscCode){
        if (!TextUtils.isEmpty(ifscCode)) {
            return GlobalVariables.IFSC_CODE_VALIDATION_REGEX_PATTERN.matcher(ifscCode).matches();
        }
        return false;
    }

    public static boolean isPanValid(String panNumber){
        if (!TextUtils.isEmpty(panNumber)) {
            return GlobalVariables.PAN_VALIDATION_REGEX_PATTERN.matcher(panNumber).matches();
        }
        return false;
    }

    public static boolean isUpiValid(String upiID){
        if (!TextUtils.isEmpty(upiID)) {
            return GlobalVariables.UPI_VALIDATION_REGEX_PATTERN.matcher(upiID).matches();
        }
        return false;
    }

    public static boolean validateEmailID(String emailAddress) {
        if (emailAddress != null) {
            return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
        }
        return false;
    }

    public static boolean validateMobileNumber(String aadhaarLinkedMobile) {
        if (!TextUtils.isEmpty(aadhaarLinkedMobile)) {
            return GlobalVariables.MOBILE_NUMBER_VALIDATION_REGEX_PATTERN.matcher(aadhaarLinkedMobile).matches();
        }
        return false;
    }

    public static boolean validateFssaiNumber(String fssaiNumber) {
        if (!TextUtils.isEmpty(fssaiNumber)) {
            return GlobalVariables.FSSAI_NUMBER_VALIDATION_REGEX_PATTERN.matcher(fssaiNumber).matches();
        }
        return false;
    }

    public static boolean validateOtp(String otp) {
        if (!TextUtils.isEmpty(otp)) {
            return otp.length() == 6;
        }
        return false;
    }

    public static boolean isHandshakeValid(String handshakeID){
        if (!TextUtils.isEmpty(handshakeID)) {
            return GlobalVariables.HANDSHAKE_REGEX_PATTERN.matcher(handshakeID).matches();
        }
        return false;
    }

    public static boolean isClientKeyValid(String clientKey) {
        if (!TextUtils.isEmpty(clientKey)){
            return GlobalVariables.HEXADECIMAL_REGEX_PATTERN.matcher(clientKey).matches();
        }
        return false;
    }


    public static boolean isGstinNumberValid(String gstinNumber){
        if (!TextUtils.isEmpty(gstinNumber)){
            return GlobalVariables.GSTIN_VALIDATION_REGEX_PATTERN.matcher(gstinNumber).matches();
        }
        return false;
    }

    public static boolean isVoterIDNumberValid(String voterIDValid) {
        if (!TextUtils.isEmpty(voterIDValid)){
            return GlobalVariables.VOTER_ID_VALIDATION_REGEX_PATTERN.matcher(voterIDValid).matches();
        }
        return false;
    }

    public static boolean isDrivingLicenseValid(String drivingLicense) {
        if (!TextUtils.isEmpty(drivingLicense)){
            return GlobalVariables.DRIVING_LICENSE_VALIDATION_REGEX_PATTERN.matcher(drivingLicense).matches();
        }
        return false;
    }

    public static boolean isMcaCompanyDataNumberValid(String mcaCompanyDataNumber){
        if (!TextUtils.isEmpty(mcaCompanyDataNumber)){
            return GlobalVariables.MCA_COMPANY_VALIDATION_REGEX_PATTERN.matcher(mcaCompanyDataNumber).matches();
        }
        return false;
    }

    public static boolean isMcaDirectorDataNumberValid(String mcaDirectorDataNumber){
        if (!TextUtils.isEmpty(mcaDirectorDataNumber)){
            return GlobalVariables.MCA_DIRECTOR_VALIDATION_REGEX_PATTERN.matcher(mcaDirectorDataNumber).matches();
        }
        return false;
    }

    public static boolean isCompanyNameValid(String companyName){
        return !TextUtils.isEmpty(companyName);
    }

    public static boolean isAadhaarLastFourDigitsEntered(String aadhaarLastFourDigits){
        if (!TextUtils.isEmpty(aadhaarLastFourDigits)){
            return aadhaarLastFourDigits.length() == 4;
        }
        return false;
    }

    public static boolean isShareCodeEntered(String enteredShareCode){
        if (!TextUtils.isEmpty(enteredShareCode)){
            return enteredShareCode.length() == 4;
        }
        return false;
    }

    public static boolean isEnteredDateFormatValid(String date){
        if (!TextUtils.isEmpty(date)){
            return GlobalVariables.DATE_VALIDATION_REGEX_PATTERN.matcher(date).matches();
        }
        return false;
    }

    public static boolean isSelfieSourceValid(){
        if (!TextUtils.isEmpty(GlobalVariables.selfieSource)){
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.selfieSource).matches();
        }
        return false;
    }

    public static boolean isTargetFrontValid(){
        if (!TextUtils.isEmpty(GlobalVariables.targetFront)){
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.targetFront).matches();
        }
        return false;
    }

    public static boolean isTargetBackValid() {
        if (!TextUtils.isEmpty(GlobalVariables.targetBack)){
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.targetBack).matches();
        }
        return false;
    }

    public static boolean isDigitalAddressFrontDocValid() {
        if (!TextUtils.isEmpty(GlobalVariables.digitalAddressFrontDocSource)){
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.digitalAddressFrontDocSource).matches();
        }
        return false;
    }

    public static boolean isDigitalAddressBackDocValid(){
        if (!TextUtils.isEmpty(GlobalVariables.digitalAddressBackDocSource)){
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.digitalAddressBackDocSource).matches();
        }
        return false;
    }

    public static boolean isDigitalAddressMediaIDValid(String mediaID) {
        if (!TextUtils.isEmpty(mediaID)) {
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(mediaID).matches();
        }
        return false;
    }

    public static boolean isDigitalAddressExteriorTwoValid(){
        if (!TextUtils.isEmpty(GlobalVariables.digitalAddressExtTwoSource)) {
            return GlobalVariables.MEDIA_ID_REGEX_PATTERN.matcher(GlobalVariables.digitalAddressExtTwoSource).matches();
        }
        return false;
    }

    public static boolean isNameValid(String name){
        return !TextUtils.isEmpty(name);
    }

    public static boolean isAddressValid(String residentialAddress){
        return !TextUtils.isEmpty(residentialAddress);
    }

    public static boolean isStringValid(String input){
        return !TextUtils.isEmpty(input);
    }

    public static boolean isYearValid(String year){
        if (!TextUtils.isEmpty(year)){
            return year.length() == 4;
        }
        return false;
    }

    public static boolean isStartYearValid(String year){
        if (!TextUtils.isEmpty(year)){
            return year.length() == 4 && Integer.parseInt(year) >= 1900;
        }
        return false;
    }

}
