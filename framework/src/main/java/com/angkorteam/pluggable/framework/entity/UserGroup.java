package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_user_group")
public class UserGroup implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4239609556224904138L;

    @Id
    @Column(name = GROUP_ID, columnDefinition = "INT")
    private Long groupId;
    public static final String GROUP_ID = Group.ID;

    @Id
    @Column(name = USER_ID, columnDefinition = "INT")
    private Long userId;
    public static final String USER_ID = AbstractUser.ID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;

        UserGroup userGroup = (UserGroup) o;

        if (!groupId.equals(userGroup.groupId)) return false;
        if (!userId.equals(userGroup.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
