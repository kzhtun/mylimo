package com.info121.mylimo.models;

/**
 * Created by KZHTUN on 7/17/2017.
 */

public class Status {
    private String errorMessage;
    private String resultCode;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
