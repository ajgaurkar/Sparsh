package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 7/5/2016.
 */
public class AttachmentsListData {

    private String itemId;
    private String attachmentFileName;
    private String attachmentFileUri;

    public AttachmentsListData(String itemId, String attachmentFileName, String attachmentFileUri) {
        this.itemId = itemId;
        this.attachmentFileName = attachmentFileName;
        this.attachmentFileUri = attachmentFileUri;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileUri() {
        return attachmentFileUri;
    }

    public void setAttachmentFileUri(String attachmentFileUri) {
        this.attachmentFileUri = attachmentFileUri;
    }
}
