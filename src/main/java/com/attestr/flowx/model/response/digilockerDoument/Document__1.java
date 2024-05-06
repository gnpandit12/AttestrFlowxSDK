
package com.attestr.flowx.model.response.digilockerDoument;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document__1 {

    @SerializedName("doctype")
    @Expose
    private String doctype;
    @SerializedName("description")
    @Expose
    private String description;

    public String getDoctype() {
        return doctype;
    }

    public String getDescription() {
        return description;
    }

}
