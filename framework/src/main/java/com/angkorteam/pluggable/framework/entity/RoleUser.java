package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_role_user")
public class RoleUser implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4239609556224904138L;

    @Id
    @Column(name = ROLE_ID, columnDefinition = "INT")
    private Long roleId;
    public static final String ROLE_ID = Role.ID;

    @Id
    @Column(name = USER_ID, columnDefinition = "INT")
    private Long userId;
    public static final String USER_ID = AbstractUser.ID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleUser)) return false;

        RoleUser roleUser = (RoleUser) o;

        if (!roleId.equals(roleUser.roleId)) return false;
        if (!userId.equals(roleUser.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
