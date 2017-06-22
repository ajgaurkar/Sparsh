package com.relecotech.androidsparsh.controllers;

/**
 * Created by amey on 5/23/2016.
 */
public class FeesDialogListData {
    String amount_comment;
    String amount_split;

    public FeesDialogListData(String amount_comment, String amount_split) {
        this.amount_comment = amount_comment;
        this.amount_split = amount_split;
    }

    public String getAmount_comment() {
        return amount_comment;
    }

    public void setAmount_comment(String amount_comment) {
        this.amount_comment = amount_comment;
    }

    public String getAmount_split() {
        return amount_split;
    }

    public void setAmount_split(String amount_split) {
        this.amount_split = amount_split;
    }
}
