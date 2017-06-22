package com.relecotech.androidsparsh.controllers;

import java.io.Serializable;

/**
 * Created by amey on 10/16/2015.
 */
public class FeesListdata implements Serializable {

    int nullId = 0;
    private int installment;
    private String months;
    private String duedate;
    private String feesamount;
    private String stauts;
    private String amount_Split1;
    private String amount_Split2;
    private String amount_Split3;
    private String amount_Split4;
    private String amount_Split5;
    private String amount_Split6;
    private String fee_Type;
    private String fee_Comment;
    private String paid_Date;
    private String amount_split_comment_1;
    private String amount_split_comment_2;
    private String amount_split_comment_3;
    private String amount_split_comment_4;
    private String amount_split_comment_5;
    private String amount_split_comment_6;

    public FeesListdata(int installment, String months, String duedate, String feesamount, String stauts, String amount_Split1, String amount_Split2, String amount_Split3, String amount_Split4, String amount_Split5, String amount_Split6, String fee_Type, String fee_Comment, String paid_Date, String amount_split_comment_1, String amount_split_comment_2, String amount_split_comment_3, String amount_split_comment_4, String amount_split_comment_5, String amount_split_comment_6) {
        this.installment = installment;
        this.months = months;
        this.duedate = duedate;
        this.feesamount = feesamount;
        this.stauts = stauts;
        this.amount_Split1 = amount_Split1;
        this.amount_Split2 = amount_Split2;
        this.amount_Split3 = amount_Split3;
        this.amount_Split4 = amount_Split4;
        this.amount_Split5 = amount_Split5;
        this.amount_Split6 = amount_Split6;
        this.fee_Type = fee_Type;
        this.fee_Comment = fee_Comment;
        this.paid_Date = paid_Date;
        this.amount_split_comment_1 = amount_split_comment_1;
        this.amount_split_comment_2 = amount_split_comment_2;
        this.amount_split_comment_3 = amount_split_comment_3;
        this.amount_split_comment_4 = amount_split_comment_4;
        this.amount_split_comment_5 = amount_split_comment_5;
        this.amount_split_comment_6 = amount_split_comment_6;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

    public int getInstallment() {
        return installment;
    }

    public void setInstallment(int installment) {
        this.installment = installment;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getFeesamount() {
        return feesamount;
    }

    public void setFeesamount(String feesamount) {
        this.feesamount = feesamount;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public String getAmount_Split1() {
        return amount_Split1;
    }

    public void setAmount_Split1(String amount_Split1) {
        this.amount_Split1 = amount_Split1;
    }

    public String getAmount_Split2() {
        return amount_Split2;
    }

    public void setAmount_Split2(String amount_Split2) {
        this.amount_Split2 = amount_Split2;
    }

    public String getAmount_Split3() {
        return amount_Split3;
    }

    public void setAmount_Split3(String amount_Split3) {
        this.amount_Split3 = amount_Split3;
    }

    public String getAmount_Split4() {
        return amount_Split4;
    }

    public void setAmount_Split4(String amount_Split4) {
        this.amount_Split4 = amount_Split4;
    }

    public String getAmount_Split5() {
        return amount_Split5;
    }

    public void setAmount_Split5(String amount_Split5) {
        this.amount_Split5 = amount_Split5;
    }

    public String getAmount_Split6() {
        return amount_Split6;
    }

    public void setAmount_Split6(String amount_Split6) {
        this.amount_Split6 = amount_Split6;
    }

    public String getFee_Type() {
        return fee_Type;
    }

    public void setFee_Type(String fee_Type) {
        this.fee_Type = fee_Type;
    }

    public String getFee_Comment() {
        return fee_Comment;
    }

    public void setFee_Comment(String fee_Comment) {
        this.fee_Comment = fee_Comment;
    }

    public String getPaid_Date() {
        return paid_Date;
    }

    public void setPaid_Date(String paid_Date) {
        this.paid_Date = paid_Date;
    }

    public String getAmount_split_comment_1() {
        return amount_split_comment_1;
    }

    public void setAmount_split_comment_1(String amount_split_comment_1) {
        this.amount_split_comment_1 = amount_split_comment_1;
    }

    public String getAmount_split_comment_2() {
        return amount_split_comment_2;
    }

    public void setAmount_split_comment_2(String amount_split_comment_2) {
        this.amount_split_comment_2 = amount_split_comment_2;
    }

    public String getAmount_split_comment_3() {
        return amount_split_comment_3;
    }

    public void setAmount_split_comment_3(String amount_split_comment_3) {
        this.amount_split_comment_3 = amount_split_comment_3;
    }

    public String getAmount_split_comment_4() {
        return amount_split_comment_4;
    }

    public void setAmount_split_comment_4(String amount_split_comment_4) {
        this.amount_split_comment_4 = amount_split_comment_4;
    }

    public String getAmount_split_comment_5() {
        return amount_split_comment_5;
    }

    public void setAmount_split_comment_5(String amount_split_comment_5) {
        this.amount_split_comment_5 = amount_split_comment_5;
    }

    public String getAmount_split_comment_6() {
        return amount_split_comment_6;
    }

    public void setAmount_split_comment_6(String amount_split_comment_6) {
        this.amount_split_comment_6 = amount_split_comment_6;
    }
}

