package com.attestr.flowx.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.attestr.flowx.R;
import com.attestr.flowx.model.AttestrRequest;
import com.attestr.flowx.model.response.common.QRData;
import com.attestr.flowx.model.response.common.HandshakeReadyData;
import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.locale.CommonLocale;
import com.attestr.flowx.model.response.locale.ModalLocale;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;
import com.attestr.flowx.utils.HandleException;
import com.attestr.flowx.utils.ProgressIndicator;
import com.attestr.flowx.view.activity.CameraActivity;
import com.attestr.flowx.view.activity.LaunchActivity;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 30/09/21
 **/
public class QRFragment extends Fragment implements View.OnClickListener {

    private ImageView faceMatchQRImageView;
    private String mResponse;
    private String qrCodeBase64;
    private TextView cameraQRText, cameraQRWarning, faceMatchDataError;
    private HandshakeReadyData handShakeReadyResponse;
    private ResponseData<QRData> QRGenReady;
    private TextView shareQRLinkButtonTextView;
    private LinearLayout shareQRLinkButton;
    private TextView qrRedirectTextView;
    private Activity mActivity;
    private static QRFragment qrFragment;

    public static QRFragment getInstance(boolean newInstance) {
        if (newInstance){
            qrFragment = new QRFragment();
            return qrFragment;
        } else {
            if (qrFragment == null){
                qrFragment = new QRFragment();
            }
        }
        return qrFragment;
    }

    public void setData(Activity activity, String response) {
        try {
            this.mActivity = activity;
            this.mResponse = response;
            ProgressIndicator.setActivity(mActivity);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handShakeReadyResponse = GlobalVariables.handshakeReadyResponse;
        Bitmap qrCodeBitmap = null;
        CommonLocale commonLocale = handShakeReadyResponse.getLocale().getCommon();
        QRGenReady = CommonUtils.gsonBuilder.create().fromJson(mResponse, new TypeToken<ResponseData<QRData>>(){}.getType());
        faceMatchDataError = view.findViewById(R.id.face_match_data_error);
        faceMatchQRImageView = view.findViewById(R.id.face_match_qr_image_view);
        cameraQRText = view.findViewById(R.id.error_qr_text);
        cameraQRWarning = view.findViewById(R.id.error_qr_warning);
        shareQRLinkButtonTextView = view.findViewById(R.id.share_qr_link_button_text_view);
        shareQRLinkButton = view.findViewById(R.id.share_qr_link_button);
        qrRedirectTextView = view.findViewById(R.id.redirect_text_view);
        qrRedirectTextView.setText(commonLocale.getLabelLinkRedirectionQR());
        qrRedirectTextView.setTextColor(Color.parseColor(GlobalVariables.themeColor));
        shareQRLinkButton.setOnClickListener(this);
        qrRedirectTextView.setOnClickListener(this);
        shareQRLinkButtonTextView.setText(commonLocale.getButtonCopyQRURL());

        cameraQRText.setText(handShakeReadyResponse.getLocale().getCommon().getCamErrorQrText());
        cameraQRWarning.setText(handShakeReadyResponse.getLocale().getCommon().getCamErrorWarning());
        faceMatchDataError.setText(handShakeReadyResponse.getLocale().getCommon().getCamError103());
        qrCodeBase64 = QRGenReady.getData().getQr();
        qrCodeBase64 = qrCodeBase64.replace("data:image/png;base64,", "");
        try {
            byte[] decodedString = Base64.decode(
                    qrCodeBase64,
                    Base64.DEFAULT
            );
            qrCodeBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
        if (qrCodeBitmap != null){
            faceMatchQRImageView.setImageBitmap(qrCodeBitmap);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == shareQRLinkButton) {
            shareQRLink();
        } else if (view == qrRedirectTextView) {
            try {
                CommonUtils.sendRequest(AttestrRequest.actionQRRedirect());
                qrRedirectAlertDialog();
            } catch (Exception e) {
                HandleException.handleInternalException("QR Redirect Alert Dialog Exception: "+ e);
            }
        }
    }

    private void shareQRLink() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        String qrURL = QRGenReady.getData().getUrl();
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share QR Link");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, qrURL);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    public void qrRedirectAlertDialog() {
        ModalLocale modalLocale = GlobalVariables.handshakeReadyResponse.getLocale().getModal();
        CommonLocale commonLocale = GlobalVariables.handshakeReadyResponse.getLocale().getCommon();
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            handler.post(() -> {
                alertDialog.setTitle(modalLocale.getSkipTile());
                alertDialog.setMessage(commonLocale.getLabelIncompleteRedirectionQR());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, modalLocale.getSkipProcceed(), (dialogInterface, i) -> {
                    CommonUtils.sendRequest(AttestrRequest.actionSkipFlow());
                    alertDialog.cancel();
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, commonLocale.getSkipButtonLabel(), (dialogInterface, i) -> {
                    alertDialog.cancel();
                });
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            });
        } catch (Exception e) {
            HandleException.handleInternalException(e.toString());
        }
    }

}
