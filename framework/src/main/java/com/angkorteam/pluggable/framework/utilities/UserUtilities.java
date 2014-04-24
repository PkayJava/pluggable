package com.angkorteam.pluggable.framework.utilities;

import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.entity.AbstractUser;

/**
 * @author Socheat KHAUV
 */
public class UserUtilities {
    private UserUtilities() {
    }

    public static final Long findUserId(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("select " + AbstractUser.ID + " from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.LOGIN + " = ?", Long.class, username);
    }
}
