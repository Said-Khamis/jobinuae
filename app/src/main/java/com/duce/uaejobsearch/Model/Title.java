package com.duce.uaejobsearch.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Title {

    @SerializedName("rendered")
    @Expose
    private String rendered;

    public String getRendered() {
        return rendered != null ? rendered : null;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

}
