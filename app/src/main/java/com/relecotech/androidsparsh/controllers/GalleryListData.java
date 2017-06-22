package com.relecotech.androidsparsh.controllers;

/**
 * Created by ajinkya on 10/21/2015.
 */
public class GalleryListData {

    String id;

    public GalleryListData(String id, String active, String postDate, String title, String imgDesc, String tag, int imageCount, String url) {
        this.id = id;
        this.active = active;
        this.postDate = postDate;
        this.title = title;
        this.imgDesc = imgDesc;
        this.tag = tag;
        this.imageCount = imageCount;
        this.url = url;
    }

    String active;
    String postDate;
    String title;
    String imgDesc;
    String tag;
    int imageCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgDesc() {
        return imgDesc;
    }

    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

    String url;
    int nullId = 0;


}
