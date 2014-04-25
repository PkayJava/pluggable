package com.angkorteam.pluggable.framework.utilities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Role;
import com.angkorteam.pluggable.framework.entity.RoleGroup;
import com.angkorteam.pluggable.framework.entity.RoleUser;
import com.angkorteam.pluggable.framework.entity.UserGroup;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * @author Socheat KHAUV
 */
public class SecurityUtilities {

    private SecurityUtilities() {
    }

    public static final boolean authenticateJdbc(JdbcTemplate jdbcTemplate,
            String login, String password) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                    "select * from "
                            + TableUtilities.getTableName(AbstractUser.class)
                            + " where " + AbstractUser.LOGIN + " = ? and "
                            + AbstractUser.PASSWORD + " = ?", login, password);
            if (user.get(AbstractUser.DISABLE) == null) {
                return true;
            } else {
                return !((Boolean) user.get(AbstractUser.DISABLE));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public static final boolean authenticateMongo(DB db, String login,
            String password) {

        BasicDBObject query = new BasicDBObject();
        query.put(AbstractUser.LOGIN, login);
        query.put(AbstractUser.PASSWORD, password);

        DBCollection users = db.getCollection(TableUtilities
                .getTableName(AbstractUser.class));

        DBObject user = users.findOne(query);

        if (user == null) {
            return false;
        } else {
            if (!user.containsField(AbstractUser.DISABLE)) {
                return true;
            } else {
                return !((Boolean) user.get(AbstractUser.DISABLE));
            }
        }
    }

    public static final boolean authenticate(JdbcTemplate jdbcTemplate,
            String login) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                    "select * from "
                            + TableUtilities.getTableName(AbstractUser.class)
                            + " where " + AbstractUser.LOGIN + " = ?", login);
            if (user.get(AbstractUser.DISABLE) == null) {
                return true;
            } else {
                return !((Boolean) user.get(AbstractUser.DISABLE));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public static final void grantJdbcAccess(JdbcTemplate jdbcTemplate,
            Role role, Group group) {
        SecurityUtilities.grantJdbcAccess(jdbcTemplate, group, role);
    }

    public static final void grantMongoAccessRole(DB db, DBObject role,
            DBObject group) {
        SecurityUtilities.grantMongoAccessGroup(db, group, role);
    }

    public static final void grantJdbcAccess(JdbcTemplate jdbcTemplate,
            Group group, Role role) {
        long count = jdbcTemplate.queryForObject(
                "select count(*) from "
                        + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.ROLE_ID + " = ? and "
                        + RoleGroup.GROUP_ID + " = ?", Long.class,
                role.getId(), group.getId());
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(RoleGroup.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(RoleGroup.ROLE_ID, role.getId());
            fields.put(RoleGroup.GROUP_ID, group.getId());
            insert.execute(fields);
        }
    }

    public static final void grantMongoAccessGroup(DB db, DBObject group,
            DBObject role) {
        DBCollection role_group = db.getCollection(TableUtilities
                .getTableName(RoleGroup.class));
        BasicDBObject query = new BasicDBObject();
        query.put(RoleGroup.ROLE_ID, role.get("_id"));
        query.put(RoleGroup.GROUP_ID, group.get("_id"));
        long count = role_group.count(query);
        if (count <= 0) {
            BasicDBObject object = new BasicDBObject();
            object.put(
                    "ref_" + RoleGroup.ROLE_ID,
                    new DBRef(db, TableUtilities.getTableName(Role.class), role
                            .get("_id")));
            object.put("ref_" + RoleGroup.GROUP_ID, new DBRef(db,
                    TableUtilities.getTableName(Group.class), group.get("_id")));
            role_group.insert(object);
        }
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate, Role role,
            Long userId) {
        SecurityUtilities.grantAccess(jdbcTemplate, userId, role);
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate,
            Long userId, Role role) {
        long count = jdbcTemplate.queryForObject("select count(*) from "
                + TableUtilities.getTableName(RoleUser.class) + " where "
                + RoleUser.ROLE_ID + " = ? and " + RoleUser.USER_ID + " = ?",
                Long.class, role.getId(), userId);
        if (count <= 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(RoleUser.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(RoleUser.ROLE_ID, role.getId());
            fields.put(RoleUser.USER_ID, userId);
            insert.execute(fields);
        }
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate,
            Group group, Long userId) {
        SecurityUtilities.grantAccess(jdbcTemplate, userId, group);
    }

    public static final void grantAccess(JdbcTemplate jdbcTemplate,
            Long userId, Group group) {
        long count = jdbcTemplate.queryForObject(
                "select count(*) from "
                        + TableUtilities.getTableName(UserGroup.class)
                        + " where " + UserGroup.USER_ID + " = ? and "
                        + UserGroup.GROUP_ID + " = ?", Long.class, userId,
                group.getId());
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
