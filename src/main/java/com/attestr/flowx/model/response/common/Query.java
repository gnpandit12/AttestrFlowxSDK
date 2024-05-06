
package com.attestr.flowx.model.response.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Query {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("response")
    @Expose
    private Object response;
    @SerializedName("documents")
    @Expose
    private Object documents;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("updated")
    @Expose
    private Object updated;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Object getResponse() {
        return response;
    }

    public Object getDocuments() {
        return documents;
    }

    public String getAuthor() {
        return author;
    }

    public Long getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Object getUpdated() {
        return updated;
    }


}
