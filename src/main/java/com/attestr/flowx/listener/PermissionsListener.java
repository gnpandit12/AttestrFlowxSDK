package com.attestr.flowx.listener;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 15/12/21
 **/
public interface PermissionsListener {
    void onImageCaptured(byte[] capturedImageByteArray);
    void onVideoRecorded(byte[] numberOfBytesRecorded, long startTimeStamp, long endTimeStamp);
    void onDocumentSelected(byte[] selectedFileByteArray);
}
