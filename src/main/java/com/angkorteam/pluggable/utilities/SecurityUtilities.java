package com.angkorteam.pluggable.utilities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.entity.AbstractUser;
import com.angkorteam.pluggable.entity.Group;
import com.angkorteam.pluggable.entity.Role;
import com.angkorteam.pluggable.entity.RoleGroup;
import com.angkorteam.pluggable.entity.RoleUser;
import com.angkorteam.pluggable.entity.UserGroup;

/**
 * @author Socheat KHAUV
 */
public class SecurityUtilities {

    private SecurityUtilities() {
    }

    public static final boolean authenticate(JdbcTemplate jdbcTemplate, String login, String password) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap("select * from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.LOGIN + " = ? and " + AbstractUser.PASSWORD + " = ?", login, password);
            if (user.get(AbstractUser.DISABLE) == null) {
                return true;
            } else {
                return !((Boolean) user.get(AbstractUser.DISABLE));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public static final boolean authenticate(JdbcTemplate jdbcTemplate, String login) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap("select * from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.LOGIN + " = ?", login);
            if (user.get(AbstractUser.DISABLE) == null) {
                return true;
            } else {
                return !((Boolean) user.get(AbstractUser.DISABLE));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Role role, Group group) {
        SecurityUtilities.grantAccess(jdbcTemplate, group, role);
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Group group, Role role) {
        long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(RoleGroup.class) + " where " + RoleGroup.ROLE_ID + " = ? and " + RoleGroup.GROUP_ID + " = ?", Long.class, role.getId(), group.getId());
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(RoleGroup.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(RoleGroup.ROLE_ID, role.getId());
            fields.put(RoleGroup.GROUP_ID, group.getId());
            insert.execute(fields);
        }
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Role role, Long userId) {
        SecurityUtilities.grantAccess(jdbcTemplate, userId, role);
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Long userId, Role role) {
        long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(RoleUser.class) + " where " + RoleUser.ROLE_ID + " = ? and " + RoleUser.USER_ID + " = ?", Long.class, role.getId(), userId);
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(RoleUser.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(RoleUser.ROLE_ID, role.getId());
            fields.put(RoleUser.USER_ID, userId);
            insert.execute(fields);
        }
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Group group, Long userId) {
        SecurityUtilities.grantAccess(jdbcTemplate, userId, group);
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Long userId, Group group) {
        long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(UserGroup.class) + " where " + UserGroup.USER_ID + " = ? and " + UserGroup.GROUP_ID + " = ?", Long.class, userId, group.getId());
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(UserGroup.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(UserGroup.GROUP_ID, group.getId());
            fields.put(UserGroup.USER_ID, userId);
            insert.execute(fields);
        }
    }

}
