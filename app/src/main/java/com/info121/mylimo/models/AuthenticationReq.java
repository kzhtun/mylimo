package com.info121.mylimo.models;

/**
 * Created by KZHTUN on 7/6/2017.
 */

public class AuthenticationReq  {
    private String AgentID;
    private String UserName;
    private String Password;

    public AuthenticationReq(String agentID, String userName, String password) {
        AgentID = agentID;
        UserName = userName;
        Password = password;
    }

    public String getAgentID() {
        return AgentID;
    }

    public void setAgentID(String agentID) {
        AgentID = agentID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
