package com.angkorteam.pluggable.framework.entity;

import java.io.Serializable;


import javax.persistence.*;

/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_role_group")
public class RoleGroup implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4239609556224904138L;

    @Id
    @Column(name = ROLE_ID, columnDefinition = "INT")
    private Long roleId;
    public static final String ROLE_ID = Role.ID;

    @Id
    @Column(name = GROUP_ID, columnDefinition = "INT")
    private Long groupId;
    public static final String GROUP_ID = Group.ID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleGroup)) return false;

        RoleGroup roleGroup = (RoleGroup) o;

        if (!groupId.equals(roleGroup.groupId)) return false;
        if (!roleId.equals(roleGroup.roleId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId.hashCode();
        result = 31 * result + groupId.hashCode();
        return result;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
