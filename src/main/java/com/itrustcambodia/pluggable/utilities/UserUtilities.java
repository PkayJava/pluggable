package com.itrustcambodia.pluggable.utilities;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.entity.AbstractUser;

public class UserUtilities {
    private UserUtilities() {
    }

    public static final Long findUserId(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("select " + AbstractUser.ID + " from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.LOGIN + " = ?", Long.class, username);
    }
}
