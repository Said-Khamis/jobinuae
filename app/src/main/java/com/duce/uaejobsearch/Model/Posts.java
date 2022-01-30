package com.duce.uaejobsearch.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Posts {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("date_gmt")
    @Expose
    private String dateGmt;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("modified_gmt")
    @Expose
    private String modifiedGmt;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("excerpt")
    @Expose
    private Excerpt excerpt;
    @SerializedName("featured_media")
    @Expose
    private Integer featuredMedia;
    @SerializedName("comment_status")
    @Expose
    private String commentStatus;
    @SerializedName("categories")
    @Expose
    private List<Integer> categories = null;
    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;
    public Integer getId() {
        return id != null ? id : null;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDate() {
        return date != null ? date : " ";
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDateGmt() {
        return dateGmt != null ? dateGmt : " ";
    }
    public void setDateGmt(String dateGmt) {
        this.dateGmt = dateGmt;
    }
    public String getModified() {
        return  modified != null ? modified : " ";
    }
    public void setModified(String modified) {
        this.modified = modified;
    }
    public String getModifiedGmt() {
        return   modifiedGmt != null ? modifiedGmt : " ";
    }
    public void setModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
    public String getStatus() {
        return   status != null ? status : " ";
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Title getTitle() {
        return  title != null ? title : null;
    }
    public void setTitle(Title title) {
        this.title = title;
    }
    public Content getContent() {
        return  content != null ? content : null;
    }
    public void setContent(Content content) {
        this.content = content;
    }
    public Excerpt getExcerpt() {
        return  excerpt != null ? excerpt : null;
    }
    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }
    public Integer getFeaturedMedia() {
        return  featuredMedia != null ? featuredMedia : null;
    }
    public void setFeaturedMedia(Integer featuredMedia) {
        this.featuredMedia = featuredMedia;
    }

    public String getCommentStatus() {
        return  commentStatus != null ? commentStatus : null;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public List<Integer> getCategories() {
        return  categories != null ? categories : Collections.emptyList();
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public Embedded getEmbedded() {
        return   embedded != null ? embedded : null;
    }

    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }

}







