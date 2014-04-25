package com.angkorteam.pluggable.framework.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.database.EntityRowMapper;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Role;
import com.angkorteam.pluggable.framework.entity.RoleGroup;
import com.angkorteam.pluggable.framework.entity.RoleUser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * @author Socheat KHAUV
 */
public class RoleUtilities {

    private RoleUtilities() {
    }

    public static final List<String> lookupMongoRoles(DB db, String login) {

//        List<String> roles = new ArrayList<String>();
//
//        String tbl_user = TableUtilities.getTableName(AbstractUser.class);
//        DBCollection user_collection = db.getCollection(tbl_user);
//
//        String tbl_user_group = TableUtilities.getTableName(UserGroup.class);
//        DBCollection user_group_collection = db.getCollection(tbl_user_group);
//
//        String tbl_group = TableUtilities.getTableName(Group.class);
//        DBCollection group_collection = db.getCollection(tbl_group);
//
//        String tbl_role = TableUtilities.getTableName(Role.class);
//        DBCollection role_collection = db.getCollection(tbl_role);
//
//        String tbl_role_group = TableUtilities.getTableName(RoleGroup.class);
//        DBCollection role_group_collection = db.getCollection(tbl_role_group);
//
//        String tbl_role_user = TableUtilities.getTableName(RoleUser.class);
//        DBCollection role_user_collection = db.getCollection(tbl_role_user);
//
//        DBObject user = null;
//        {
//            BasicDBObject query = new BasicDBObject();
//            query.put(AbstractUser.LOGIN, login);
//            user = user_collection.findOne(query);
//        }
//
//        List<DBObject> groups = new ArrayList<DBObject>();
//        {
//            BasicDBObject query = new BasicDBObject();
//            DBRef ref = new DBRef(db, tbl_user, user.get("_id"));
//            query.put("ref_" + UserGroup.USER_ID, ref);
//            DBCursor cursor = user_group_collection.find(query);
//            while (cursor.hasNext()) {
//                DBObject user_group = cursor.next();
//                DBObject group = ((DBRef) user_group.get("ref_"
//                        + UserGroup.GROUP_ID)).fetch();
//                Boolean disable = (Boolean) group.get(Group.DISABLE);
//                if (!disable) {
//                    groups.add(group);
//                }
//            }
//        }
//
//        // {
//        // "key": {
//        // "_id": true
//        // },
//        // "initial": {},
//        // "reduce": function(obj, prev) {},
//        // "cond": {
//        // "group_id": {
//        // "$in": [1, 2]
//        // }
//        // }
//        // }
//
//        // group_collection.group(new BasicDBObject("ref_", value), cond,
//        // initial, reduce);
//
//        // group_collection.mapReduce(map, reduce, outputTarget, query)
//
//        if (groups != null && !groups.isEmpty()) {
//
//            role_group_collection.aggregate();
//
//            BasicDBObject query = new BasicDBObject();
//            query.append("key",
//                    new BasicDBObject("ref_" + RoleGroup.ROLE_ID, 1));
//            query.append("reduce", new BasicDBObject());
//
//            role_group_collection.group(new BasicDBObject("ref_"
//                    + RoleGroup.ROLE_ID, true), new BasicDBObject(
//                    "ref_group_id", new BasicDBObject("$in", groups)), null,
//                    null);
//            select = new StringBuffer();
//            select.append("select role." + Role.NAME + " from "
//                    + TableUtilities.getTableName(Role.class) + " role ");
//            select.append("inner join "
//                    + TableUtilities.getTableName(RoleGroup.class)
//                    + " role_group on role." + Role.ID + " = role_group."
//                    + RoleGroup.ROLE_ID + " ");
//            select.append("where role_group." + RoleGroup.GROUP_ID + " in ("
//                    + StringUtils.join(groups, ",") + ") and role."
//                    + Role.DISABLE + " = ? group by role." + Role.ID);
//            roles.addAll(jdbcTemplate.queryForList(select.toString(),
//                    String.class, false));
//        }
//        select = new StringBuffer();
//        select.append("select role." + Role.NAME + " from "
//                + TableUtilities.getTableName(Role.class) + " role ");
//        select.append("inner join "
//                + TableUtilities.getTableName(RoleUser.class)
//                + " role_user on role." + Role.ID + " = role_user."
//                + RoleUser.ROLE_ID + " ");
//        select.append("where role_user." + RoleUser.USER_ID + " = ? and role."
//                + Role.DISABLE + " = ?");
//        roles.addAll(jdbcTemplate.queryForList(select.toString(), String.class,
//                user.get(AbstractUser.ID), false));
//
//        return Collections.<String> unmodifiableList(roles);
        return null;
    }

    public static final List<String> lookupJdbcRoles(JdbcTemplate jdbcTemplate,
            String login) {

        List<String> roles = new ArrayList<String>();

        Map<String, Object> user = jdbcTemplate.queryForMap("select * from "
                + TableUtilities.getTableName(AbstractUser.class) + " where "
                + AbstractUser.LOGIN + " = ?", login);

        StringBuffer select = null;

        select = new StringBuffer();
        select.append("select `group`." + Group.ID + " from tbl_group `group` ");
        select.append("inner join tbl_user_group user_group on `group`.group_id = user_group.group_id ");
        select.append("where user_group.user_id = ? and `group`.disable = ?");
        List<Long> groups = jdbcTemplate.queryForList(select.toString(),
                Long.class, user.get(AbstractUser.ID), false);

        if (groups != null && !groups.isEmpty()) {
            select = new StringBuffer();
            select.append("select role." + Role.NAME + " from "
                    + TableUtilities.getTableName(Role.class) + " role ");
            select.append("inner join "
                    + TableUtilities.getTableName(RoleGroup.class)
                    + " role_group on role." + Role.ID + " = role_group."
                    + RoleGroup.ROLE_ID + " ");
            select.append("where role_group." + RoleGroup.GROUP_ID + " in ("
                    + StringUtils.join(groups, ",") + ") and role."
                    + Role.DISABLE + " = ? group by role." + Role.ID);
            roles.addAll(jdbcTemplate.queryForList(select.toString(),
                    String.class, false));
        }
        select = new StringBuffer();
        select.append("select role." + Role.NAME + " from "
                + TableUtilities.getTableName(Role.class) + " role ");
        select.append("inner join "
                + TableUtilities.getTableName(RoleUser.class)
                + " role_user on role." + Role.ID + " = role_user."
                + RoleUser.ROLE_ID + " ");
        select.append("where role_user." + RoleUser.USER_ID + " = ? and role."
                + Role.DISABLE + " = ?");
        roles.addAll(jdbcTemplate.queryForList(select.toString(), String.class,
                user.get(AbstractUser.ID), false));

        return Collections.<String> unmodifiableList(roles);
    }

    public static final void removeJdbcRole(JdbcTemplate jdbcTemplate,
            String name) {
        Role role = jdbcTemplate.queryForObject("select * from "
                + TableUtilities.getTableName(Role.class) + " where "
                + Role.NAME + " = ?", new EntityRowMapper<Role>(Role.class),
                name);
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleUser.class)
                        + " where " + RoleUser.ROLE_ID + " = ?", role.getId());
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.ROLE_ID + " = ?", role.getId());
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(Role.class)
                        + " where " + Role.ID + " = ?", role.getId());
    }

    public static final void removeMongoRole(DB db, String name) {
        DBObject role = null;
        {
            DBCollection roles = db.getCollection(TableUtilities
                    .getTableName(Role.class));
            BasicDBObject query = new BasicDBObject();
            query.put(Role.NAME, name);
            role = roles.findOne(query);
            roles.remove(role);
        }
        {
            DBCollection role_user = db.getCollection(TableUtilities
                    .getTableName(RoleUser.class));
            BasicDBObject query = new BasicDBObject();
            query.put(
                    "ref_" + RoleUser.ROLE_ID,
                    new DBRef(db, TableUtilities.getTableName(Role.class), role
                            .get("_id")));
            role_user.remove(query);
        }
        {
            DBCollection role_group = db.getCollection(TableUtilities
                    .getTableName(RoleGroup.class));
            BasicDBObject query = new BasicDBObject();
            query.put(
                    "ref_" + RoleGroup.ROLE_ID,
                    new DBRef(db, TableUtilities.getTableName(Role.class), role
                            .get("_id")));
            role_group.remove(query);
        }
    }

    public static final Role createJdbcRole(DataSource dataSource, String name,
            String description, boolean disable) {
        SimpleJdbcInsert insertRole = new SimpleJdbcInsert(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        insertRole.withTableName(TableUtilities.getTableName(Role.class));
        insertRole.usingGeneratedKeyColumns(Role.ID);
        Map<String, Object> fields = new HashMap<String, Object>();
        Role role = null;
        Long roleId = null;
        try {
            role = jdbcTemplate.queryForObject("select * from "
                    + TableUtilities.getTableName(Role.class) + " where "
                    + Role.NAME + " = ?",
                    new EntityRowMapper<Role>(Role.class), name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (role == null) {
            fields.put(Role.NAME, name);
            fields.put(Role.DESCRIPTION, description);
            fields.put(Role.DISABLE, disable);
            roleId = insertRole.executeAndReturnKey(fields).longValue();
            role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setId(roleId);
        }
        return role;
    }

    public static final DBObject createMongoRole(DB db, String name,
            String description, boolean disable) {
        DBCollection roles = db.getCollection(TableUtilities
                .getTableName(Role.class));
        BasicDBObject query = new BasicDBObject();
        query.put(Role.NAME, name);

        DBObject role = roles.findOne(query);

        if (role == null) {
            role = new BasicDBObject();
            role.put(Role.NAME, name);
            role.put(Role.DESCRIPTION, description);
            role.put(Role.DISABLE, disable);
            roles.insert(role);
        }
        return role;
    }
}
