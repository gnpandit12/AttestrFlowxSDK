
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class ModalLocale {

    @SerializedName("FOOTER_LABEL")
    @Expose
    private String footerLabel;
    @SerializedName("SKIP_TITLE")
    @Expose
    private String skipTile;
    @SerializedName("SKIP_DESC")
    @Expose
    private String skipDescription;
    @SerializedName("SKIP_PROCEED")
    @Expose
    private String skipProcceed;
    @SerializedName("SKIP_CANCEL")
    @Expose
    private String skipCancel;



    public String getFooterLabel() {
        return footerLabel;
    }

    public String getSkipTile() {
        return skipTile;
    }

    public String getSkipDescription() {
        return skipDescription;
    }

    public String getSkipProcceed() {
        return skipProcceed;
    }

    public String getSkipCancel() {
        return skipCancel;
    }

}
