package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class ResponseError {

    @SerializedName("httpStatusCode")
    @Expose
    private Integer httpStatusCode;
    @SerializedName("retry")
    @Expose
    private Boolean retry;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("stage")
    @Expose
    private String stage;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("original")
    @Expose
    private ResponseError original;

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Boolean isRetry() {
        return retry;
    }

    public void setRetry(Boolean retry) {
        this.retry = retry;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseError getOriginal() {
        return original;
    }

    public void setOriginal(ResponseError original) {
        this.original = original;
    }
}
