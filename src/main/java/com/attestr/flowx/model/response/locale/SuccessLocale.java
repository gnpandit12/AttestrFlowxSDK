
package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class SuccessLocale {

    @SerializedName("LABEL")
    @Expose
    private String label;
    @SerializedName("BUTTON_PROCEED")
    @Expose
    private String buttonProceed;
    @SerializedName("DESC")
    @Expose
    private String description;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getButtonProceed() {
        return buttonProceed;
    }

    public void setButtonProceed(String buttonProceed) {
        this.buttonProceed = buttonProceed;
    }

    public String getDescription() {
        return description;
    }
}
