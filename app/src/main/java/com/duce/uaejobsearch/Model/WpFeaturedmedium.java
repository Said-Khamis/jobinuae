package com.duce.uaejobsearch.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WpFeaturedmedium {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("source_url")
    @Expose
    private String sourceUrl;


    public Integer getId() {
        return id != null ? id : null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date != null ? date : null;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSourceUrl() {
        return sourceUrl != null ? sourceUrl : null;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

}
