package com.attestr.flowx.model.response.locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 08/01/23
 **/
public class VoterIDLocale {

    @SerializedName("TITLE")
    @Expose
    private String title;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INFO")
    @Expose
    private String info;
    @SerializedName("FIELD_EPIC_LABEL")
    @Expose
    private String fieldEpicLabel;
    @SerializedName("FIELD_EPIC_PLACEHOLDER")
    @Expose
    private String fieldEpicPlaceholder;
    @SerializedName("FIELD_EPIC_DATAERROR")
    @Expose
    private String fieldEpicDataerror;
    @SerializedName("BUTTON_EPIC_PROCEED")
    @Expose
    private String buttonEpicProceed;
    @SerializedName("INVALID_EPIC")
    @Expose
    private String invalidEpic;

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getInfo() {
        return info;
    }

    public String getFieldEpicLabel() {
        return fieldEpicLabel;
    }

    public String getFieldEpicPlaceholder() {
        return fieldEpicPlaceholder;
    }

    public String getFieldEpicDataerror() {
        return fieldEpicDataerror;
    }


    public String getButtonEpicProceed() {
        return buttonEpicProceed;
    }

    public String getInvalidEpic() {
        return invalidEpic;
    }

}
