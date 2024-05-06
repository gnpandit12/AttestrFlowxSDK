
package com.attestr.flowx.model.response.digilockerDoument;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document {

    @SerializedName("document")
    @Expose
    private Document__1 document;
    @SerializedName("params")
    @Expose
    private JsonArray params = null;

    public Document__1 getDocument() {
        return document;
    }

    public JsonArray getParams() {
        return params;
    }

}
