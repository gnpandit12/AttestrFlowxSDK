package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 17/10/21
 **/
public class HandshakeData extends BaseData{

    @SerializedName("meta")
    @Expose
    private String meta;
    @SerializedName("cid")
    @Expose
    private String cid;
    @SerializedName("digest")
    @Expose
    private String digest;
    @SerializedName("signature")
    @Expose
    private String signature;
    @SerializedName("rurl")
    @Expose
    private Object rurl;

    public String getSignature() {
        return signature;
    }

    public Object getRurl() {
        return rurl;
    }

    public String getDigest() {
        return digest;
    }

    public String getMeta() {
        return meta;
    }

    public String getCid() {
        return cid;
    }

}
