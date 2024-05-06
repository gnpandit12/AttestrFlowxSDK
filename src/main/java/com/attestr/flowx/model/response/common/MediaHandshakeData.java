package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 08/10/21
 **/
public class MediaHandshakeData extends BaseData {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media")
    @Expose
    private String media;
    @SerializedName("meta")
    @Expose
    private String meta;

    public String getUrl() {
        return url;
    }

    public String getMedia() {
        return media;
    }

    public String getMeta() {
        return meta;
    }

}
