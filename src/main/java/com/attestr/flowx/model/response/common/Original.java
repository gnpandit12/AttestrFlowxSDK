package com.attestr.flowx.model.response.common;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 05/01/23
 **/
public class Original {

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAppError() {
        return appError;
    }

    private int httpStatusCode;
    private int code;
    private String message;
    private boolean appError;



}
