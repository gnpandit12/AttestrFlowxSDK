
package com.attestr.flowx.model.response.locale;
import com.attestr.flowx.model.response.common.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPGenReadyLocale extends BaseData {

    @SerializedName("valid")
    @Expose
    private Boolean valid;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;

    public Boolean getValid() {
        return valid;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

}
