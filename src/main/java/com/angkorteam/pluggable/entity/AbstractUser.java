package com.angkorteam.pluggable.entity;

import java.io.Serializable;

import com.angkorteam.pluggable.database.annotation.Column;
import com.angkorteam.pluggable.database.annotation.Entity;
import com.angkorteam.pluggable.database.annotation.GeneratedValue;
import com.angkorteam.pluggable.database.annotation.GenerationType;
import com.angkorteam.pluggable.database.annotation.Id;
import com.angkorteam.pluggable.database.annotation.Table;

/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_user")
public class AbstractUser implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3968402256384106790L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    protected Long id;
    public static final String ID = "user_id";

    @Column(name = LOGIN, columnDefinition = "VARCHAR(255)")
    protected String login;
    public static final String LOGIN = "login";

    @Column(name = PASSWORD, columnDefinition = "VARCHAR(255)")
    protected String password;
    public static final String PASSWORD = "password";

    @Column(name = DISABLE, columnDefinition = "BOOLEAN")
    protected boolean disable;
    public static final String DISABLE = "disable";

    public final String getLogin() {
        return login;
    }

    public final void setLogin(String login) {
        this.login = login;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final boolean isDisable() {
        return disable;
    }

    public final void setDisable(boolean disable) {
        this.disable = disable;
    }

    public final Long getId() {
        return id;
    }
}
