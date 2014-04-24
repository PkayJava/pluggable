package com.angkorteam.pluggable.framework.json;

import java.io.Serializable;

/**
 * @author Socheat KHAUV
 */
public class LoginForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -124512273375208426L;

    private String login;

    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
