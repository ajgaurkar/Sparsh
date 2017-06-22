package com.relecotech.androidsparsh.controllers;

/**
 * Created by yogesh on 27-03-2017.
 */

public class GalleryRecyclerListData {

    String postDate;

    public GalleryRecyclerListData(String postDate, String description, String url, String fileName, String mainFolderName, String thumbnailFolderName) {
        this.postDate = postDate;
        this.description = description;
        this.url = url;
        this.fileName = fileName;
        this.mainFolderName = mainFolderName;
        this.thumbnailFolderName = thumbnailFolderName;
    }

    String description;
    String url;
    String fileName;
    String mainFolderName;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMainFolderName() {
        return mainFolderName;
    }

    public void setMainFolderName(String mainFolderName) {
        this.mainFolderName = mainFolderName;
    }

    public String getThumbnailFolderName() {
        return thumbnailFolderName;
    }

    public void setThumbnailFolderName(String thumbnailFolderName) {
        this.thumbnailFolderName = thumbnailFolderName;
    }

    String thumbnailFolderName;
}
