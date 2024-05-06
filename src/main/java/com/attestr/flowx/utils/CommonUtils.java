package com.attestr.flowx.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.attestr.flowx.listener.EventListener;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.ModalLocale;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.attestr.flowx.view.fragments.WaitFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 **/

public class CommonUtils {

    private static final Gson gson = new Gson();
    private static final ValueAnimator animation = ValueAnimator.ofFloat(1f, 1.3f);
    private static Map<String, Object> classToMap = new HashMap<>();
    private static GradientDrawable shape = new GradientDrawable();
    public static Drawable aadhaarDrawable, aadhaarDrawableRed, emailDrawable,
            emailDrawableRed, phoneDrawable, phoneDrawableRed, captchaDrawable, captchaDrawableRed,
            otpDrawable, otpDrawableRed, identityDrawable, identityDrawableRed, upiDrawable, upiDrawableRed,
            bankAccountNumber, bankAccountNumberRed, bankIfsc, bankIfscRed, businessDrawable, businessDrawableRed,
            personDrawable, personDrawableRed, calenderDrawable, calenderDrawableRed, shareCodeDrawable, shareCodeDrawableRed,
            keyboardDrawable, keyboardDrawableRed, fssaiDrawable, fssaiDrawableRed;
    public static GsonBuilder gsonBuilder = new GsonBuilder();
    private static OkHttpWebSocket okHttpWebSocket;
    private static EventListener mEventListener;
    public static boolean isCleanedUp = false;
    private AlertDialog alertDialog;

    public static void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public static Gson getGson() {
        if (gson == null) {
            return new Gson();
        }
        return gson;
    }


    public static void startAnimation(final ImageView verificationImageView) {
        animation.setDuration(1000);
        animation.addUpdateListener(animation -> {
            verificationImageView.setScaleX((Float) animation.getAnimatedValue());
            verificationImageView.setScaleY((Float) animation.getAnimatedValue());
        });
        animation.setRepeatCount(20);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.start();
    }

    public static boolean isSmallDevice(Activity activity) {
        int screenSize = activity.getResources().getDisplayMetrics().densityDpi;
        return DisplayMetrics.DENSITY_HIGH == screenSize;
    }

    public static Map<String, Object> jsonToMap(String jsonResponse) throws JSONException {
        final HashMap<String, Object> map = new HashMap<>();
        JSONObject jObject = new JSONObject(jsonResponse);
        Iterator<?> keys = jObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, value);
        }
        return map;
    }


    public static void setButtonAlphaLow(LinearLayout buttonLinearLayout) {
        buttonLinearLayout.setAlpha(GlobalVariables.ALPHA_LOW);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            buttonLinearLayout.getBackground().setColorFilter(Color.parseColor(GlobalVariables.themeColor), PorterDuff.Mode.SRC_ATOP);
        } else {
            buttonLinearLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        }
    }

    public static void setButtonAlphaHigh(LinearLayout buttonLinearLayout) {
        buttonLinearLayout.setAlpha(GlobalVariables.ALPHA_HIGH);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            buttonLinearLayout.getBackground().setColorFilter(Color.parseColor(GlobalVariables.themeColor), PorterDuff.Mode.SRC_ATOP);
        } else {
            buttonLinearLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalVariables.themeColor)));
        }
    }

//    public static void setResources(Activity activity){
//        aadhaarDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.fingerprint_drawable, null);
//        aadhaarDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.fingerprint_drawable_red, null);
//        emailDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.email_drawable, null);
//        emailDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.email_drawable_red, null);
//        phoneDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.phone_drawable, null);
//        phoneDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.phone_drawable_red, null);
//        captchaDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.captcha_drawable, null);
//        captchaDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.captcha_drawable_red, null);
//        otpDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.otp_drawable, null);
//        otpDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.otp_drawable_red, null);
//        identityDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.identity_drawable, null);
//        identityDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.identity_drawable_red, null);
//        upiDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.upi_drawable, null);
//        upiDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.upi_drawable_red, null);
//        bankAccountNumber = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.bank_acc_no_drawable, null);
//        bankAccountNumberRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.bank_acc_no_drawable_red, null);
//        bankIfsc = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.bank_drawable, null);
//        bankIfscRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.bank_drawable_red, null);
//        businessDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.business_drawable, null);
//        businessDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.business_drawable_red, null);
//        personDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.person_drawable, null);
//        personDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.person_drawable_red, null);
//        calenderDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.calender_drawable, null);
//        calenderDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.calender_drawable_red, null);
//        shareCodeDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.share_code_drawable, null);
//        shareCodeDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.share_code_drawable_red, null);
//        keyboardDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.keyboard_drawable, null);
//        keyboardDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.keyboard_drawable_red, null);
//        fssaiDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.fssai_drawable, null);
//        fssaiDrawableRed = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.fssai_drawable_red, null);
//    }

    public static void cleanUp() {
        GlobalVariables.currentConnectID = null;
        GlobalVariables.handshakeReadyResponseString = null;
        GlobalVariables.handshakeId = null;
        GlobalVariables.clientKey = null;
        GlobalVariables.locale = null;
        GlobalVariables.webSocket = null;
        GlobalVariables.currentStageId = null;
        GlobalVariables.themeColor = null;
        GlobalVariables.textColor = 0;
        GlobalVariables.handshakeReadyResponse = null;
        GlobalVariables.stageReadyResponse = null;
        GlobalVariables.stageInitResponse = null;
        GlobalVariables.userAadhaarNumber = null;
        GlobalVariables.userAadhaarLinkedEmail = null;
        GlobalVariables.userAadhaarLinkedMobile = null;
        GlobalVariables.xmlShareCode = null;
        GlobalVariables.xmlMedia = null;
        GlobalVariables.mediaSubmitUrl = null;
        GlobalVariables.reqNotProcessed = null;
        GlobalVariables.isValidateEmail = false;
        GlobalVariables.isValidateMobile = false;
        GlobalVariables.currentVerificationStage = null;
        GlobalVariables.currentEvent = null;
        GlobalVariables.currentDataMap = null;
        GlobalVariables.handshakeEndDataMap = null;
        GlobalVariables.sessionErrorDataMap = null;
        GlobalVariables.selectedFileName = null;
        GlobalVariables.selectedXMLFileByteArray = null;
        GlobalVariables.capturedImageByteArray = null;
        GlobalVariables.selfieVideoByteArray = null;
        GlobalVariables.recordedVideoBytes = null;
        GlobalVariables.selfieSource = null;
        GlobalVariables.targetFront = null;
        GlobalVariables.targetBack = null;
        GlobalVariables.ipvImageMediaID = null;
        GlobalVariables.ipvVideoMediaID = null;
        GlobalVariables.faceMatchDocumentType = null;
        GlobalVariables.faceMatchCurrentMetadata = null;
        GlobalVariables.isRetry = false;
        GlobalVariables.queryMap = null;
        GlobalVariables.currentVerificationStageType = null;
        GlobalVariables.skipDataMap = null;
        GlobalVariables.docType = null;
        GlobalVariables.issuer = null;
        GlobalVariables.digiLockerDigest = null;
        GlobalVariables.digitalAddressDataMap = null;
        GlobalVariables.detectedLatitude = 0.0;
        GlobalVariables.detectedLongitude = 0.0;
        GlobalVariables.markerDragLatitude = 0.0;
        GlobalVariables.markerDragLongitude = 0.0;
        GlobalVariables.digitalAddressFrontDocSource = null;
        GlobalVariables.digitalAddressBackDocSource = null;
        GlobalVariables.digitalAddressExtOneSource = null;
        GlobalVariables.digitalAddressExtTwoSource = null;
        GlobalVariables.mobileOTPVerificationNumber = null;
        GlobalVariables.emailOTPVerificationAddress = null;
        GlobalVariables.isOTPExpired = false;
        GlobalVariables.queryID = null;
        GlobalVariables.queryText = null;
        GlobalVariables.selectedMediaMimeType = null;
        GlobalVariables.ipvImageMimetype = null;
        GlobalVariables.ipvVideoMimetype = null;
        GlobalVariables.querySelectedPDFByteArray = null;
        GlobalVariables.ipvOTP = null;
        GlobalVariables.isCameraSelected = false;
        GlobalVariables.isStorageSelected = false;
        isCleanedUp = true;
    }

    public static void sendRequest(String jsonString) {
        try {
            okHttpWebSocket = GlobalVariables.webSocket;
            okHttpWebSocket.sendRequest(jsonString);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public static void showTerminateAlertMessage(
            @NonNull final Activity launchActivity,
            @NonNull String skipTitle,
            @NonNull String skipDescription,
            @NonNull String skipProceed,
            @NonNull String skipCancel
    ) {
        final AlertDialog alertDialog;
        try {
            alertDialog = new AlertDialog.Builder(launchActivity).create();
            alertDialog.setTitle(skipTitle);
            alertDialog.setMessage(skipDescription);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, skipProceed, (dialogInterface, i) -> {
                mEventListener.addFragment(new WaitFragment(launchActivity, null));
                CommonUtils.sendRequest(AttestrRequest.actionSkipFlow());
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, skipCancel,
                    (dialog, which) -> {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public static byte[] getByteArray(
            Activity activity,
            Uri fileUri
    ) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(fileUri);
            byteArray = getBytes(inputStream);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return byteArray;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getFileName(Activity activity, Uri uri) {
        String fileName = null;
        try (Cursor returnCursor = activity.getContentResolver().query(uri, null, null, null, null)) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        return fileName;
    }

    public static String getMimeType(Activity activity, Uri uri) {
        ContentResolver cR = activity.getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static void showLocationPermissionAlertDialog(Activity activity) {
        CommonLocale commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            handler.post(() -> {
                alertDialog.setTitle(commonLocale.getGpsError102Title());
                alertDialog.setMessage(commonLocale.getGpsError102Desc());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, commonLocale.getGpsError102ButtonContinue(), (dialogInterface, i) -> {
                    alertDialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, commonLocale.getActionCancel(), (dialogInterface, i) -> {
                    if ((boolean) GlobalVariables.stageReadyResponse.getData().getMetadata().get("ipGeolocationEnabled")) {
                        CommonUtils.sendRequest(AttestrRequest.actionIPGeolocation());
                    } else {
                        HandleException.handleLocationException(commonLocale.getGpsError500());
                    }
                    alertDialog.cancel();
                });
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public static void mediaUploadAlertDialog(Activity activity) {
        CommonLocale commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            handler.post(() -> {
                alertDialog.setTitle(commonLocale.getMediaUploadErrorPromptTitle());
                alertDialog.setMessage(commonLocale.getMediaUploadErrorPromptDesc());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, commonLocale.getMediaUploadErrorButtonContinue(), (dialogInterface, i) -> {
                    alertDialog.cancel();
                });
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public static void showQRRedirectAlertDialog(Activity activity) {
        ModalLocale modalLocale = GlobalVariables.handshakeReadyResponse.getLocale().getModal();
        ;
        CommonLocale commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            handler.post(() -> {
                alertDialog.setTitle(modalLocale.getSkipTile());
                alertDialog.setMessage(commonLocale.getLabelIncompleteRedirectionQR());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, modalLocale.getSkipProcceed(), (dialogInterface, i) -> {
                    alertDialog.cancel();
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, commonLocale.getSkipButtonLabel(), (dialogInterface, i) -> {
                    ((LaunchActivity) activity).skipFlow();
                });
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

    public static Bitmap convertByteArrayToBitmap(byte[] selectedImageByteArray, int maxSize) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImageByteArray, 0, selectedImageByteArray.length);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static Bitmap getPdfBitmap(File selectedFile) {
        Bitmap selectedFileBitmap = null;
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(selectedFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page pageNumber = pdfRenderer.openPage(1);
            selectedFileBitmap = Bitmap.createBitmap(pageNumber.getWidth(), pageNumber.getHeight(), Bitmap.Config.ARGB_8888);
            pageNumber.render(selectedFileBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pageNumber.close();
            pdfRenderer.close();
        } catch (IOException e) {
            HandleException.handleInternalException("Pdf to Bitmap"+ e);
        }
        return selectedFileBitmap;
    }


}

