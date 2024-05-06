
package com.attestr.flowx.model.response.digilockerDoument;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DigiLockerDocumentResponse {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("categories")
    @Expose
    private String categories;
    @SerializedName("issuerid")
    @Expose
    private String issuerid;
    @SerializedName("orgid")
    @Expose
    private String orgid;
    @SerializedName("documents")
    @Expose
    private List<Document> documents = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategories() {
        return categories;
    }

    public String getIssuerid() {
        return issuerid;
    }

    public String getOrgid() {
        return orgid;
    }

    public List<Document> getDocuments() {
        return documents;
    }

}
