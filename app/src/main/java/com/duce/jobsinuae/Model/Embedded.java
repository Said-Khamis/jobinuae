package com.duce.jobsinuae.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Embedded {
    @SerializedName("wp:featuredmedia")
    @Expose
    private List<WpFeaturedmedium> wpFeaturedmedia = null;


    public List<WpFeaturedmedium> getWpFeaturedmedia() {
        return wpFeaturedmedia != null ? wpFeaturedmedia : new ArrayList<>();
    }

    public void setWpFeaturedmedia(List<WpFeaturedmedium> wpFeaturedmedia) {
        this.wpFeaturedmedia = wpFeaturedmedia;
    }

}
