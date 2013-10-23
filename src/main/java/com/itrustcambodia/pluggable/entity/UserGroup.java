package com.itrustcambodia.pluggable.entity;

import java.io.Serializable;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

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
