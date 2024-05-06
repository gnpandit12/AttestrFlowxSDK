
package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class HandshakeReadyData {

    @SerializedName("digest")
    @Expose
    private String digest;
    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("locale")
    @Expose
    private Locale locale;

    public Config getConfig() {
        return config;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getDigest() {
        return digest;
    }

}
