package com.info121.mylimo.models;

/**
 * Created by KZHTUN on 7/6/2017.
 */

public class AuthenticationRes {
    public String SecurityToken;
    private Status Status;

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }

    public Status getStatus() {
        return Status;
    }

    public void setStatus(Status status) {
        Status = status;
    }
}
