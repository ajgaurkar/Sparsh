package com.relecotech.androidsparsh.azurecontroller;

/**
 * Created by ajinkya on 1/14/2016.
 */
public class Assignment_Attachment {
    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("filename")
    private String filename;

    @com.google.gson.annotations.SerializedName("attachementIdentifier")
    private String attachementIdentifier;
    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     *  imageUri - points to location in storage where photo will go
     */
    @com.google.gson.annotations.SerializedName("imageUri")
    private String mImageUri;
    /**
     * Returns the item ImageUri
     */
    public String getImageUri() {
        return mImageUri;
    }

    /**
     * Sets the item ImageUri
     *
     * @param ImageUri
     *            Uri to set
     */
    public final void setImageUri(String ImageUri) {
        mImageUri = ImageUri;
    }
    /**
     * ContainerName - like a directory, holds blobs
     */
    @com.google.gson.annotations.SerializedName("containerName")
    private String mContainerName;

    /**
     * Returns the item ContainerName
     */
    public String getContainerName() {
        return mContainerName;
    }

    /**
     * Sets the item ContainerName
     *
     * @param ContainerName
     *            Uri to set
     */
    public final void setContainerName(String ContainerName) {
        mContainerName = ContainerName;
    }

    /**
     *  ResourceName
     */
    @com.google.gson.annotations.SerializedName("resourceName")
    private String mResourceName;

    /**
     * Returns the item ResourceName
     */
    public String getResourceName() {
        return mResourceName;
    }

    /**
     * Sets the item ResourceName
     *
     * @param ResourceName
     *            Uri to set
     */
    public final void setResourceName(String ResourceName) {
        mResourceName = ResourceName;
    }

    /**
     *  SasQueryString - permission to write to storage
     */
    @com.google.gson.annotations.SerializedName("sasQueryString")
    private String mSasQueryString;

    /**
     * Returns the item SasQueryString
     */
    public String getSasQueryString() {
        return mSasQueryString;
    }

    /**
     * Sets the item SasQueryString
     *
     * @param SasQueryString
     *            Uri to set
     */
    public final void setSasQueryString(String SasQueryString) {
        mSasQueryString = SasQueryString;
    }

    public Assignment_Attachment() {
        mContainerName = "";
        mResourceName = "";
        mImageUri = "";
        mSasQueryString = "";
    }

    public Assignment_Attachment(String filename, String attachementIdentifier, String mId, String mImageUri, String mContainerName, String mResourceName, String mSasQueryString) {
        this.filename = filename;
        this.attachementIdentifier = attachementIdentifier;
        this.mId = mId;
        this.mImageUri = mImageUri;
        this.mContainerName = mContainerName;
        this.mResourceName = mResourceName;
        this.mSasQueryString = mSasQueryString;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAttachementIdentifier() {
        return attachementIdentifier;
    }

    public void setAttachementIdentifier(String attachementIdentifier) {
        this.attachementIdentifier = attachementIdentifier;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Assignment_Attachment && ((Assignment_Attachment) o).mId == mId;
    }
}