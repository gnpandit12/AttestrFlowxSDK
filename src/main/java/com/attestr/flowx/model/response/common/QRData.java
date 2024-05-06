package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 08/10/21
 **/
public class QRData extends BaseData {

    @SerializedName("qr")
    @Expose
    private String qr;
    @SerializedName("url")
    @Expose
    private String url;

    public String getQr() {
        return qr;
    }

    public String getUrl() {
        return url;
    }
}
