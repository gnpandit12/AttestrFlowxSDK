package com.attestr.flowx.model.response.common;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class BaseData {

    private int code;
    private int httpStatusCode;
    private String message;
    private boolean retry;
    private String stage;
    private ResponseError error;
    private Original original;

    public int getCode() {
        return code;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRetry() {
        return retry;
    }

    public String getStage() {
        return stage;
    }

    public ResponseError getError() {
        return error;
    }

    public Original getOriginal() {
        return original;
    }
}
