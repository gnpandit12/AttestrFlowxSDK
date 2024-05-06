package com.attestr.flowx.listener;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 20/12/21
 **/
public interface MediaHandshakeListener {
    void onDocumentUploaded(String mediaID, String documentName, String mimeType);
    void onImageUploaded(String metadata, String mediaID);
    void onVideoUploaded(String metadata, String mediaID);
}
