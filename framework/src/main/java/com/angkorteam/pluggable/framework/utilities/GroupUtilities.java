package com.angkorteam.pluggable.framework.utilities;

import java.util.HashMap;
import java.util.Map;

import com.angkorteam.pluggable.framework.mapper.GroupMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Group;

/**
 * @author Socheat KHAUV
 */
public class GroupUtilities {

    private GroupUtilities() {
    }

    public static final Group createJdbcGroup(JdbcTemplate jdbcTemplate,
            String name, String description, boolean disable) {
        SimpleJdbcInsert insertGroup = new SimpleJdbcInsert(jdbcTemplate);
        insertGroup.withTableName(TableUtilities.getTableName(Group.class));
        insertGroup.usingGeneratedKeyColumns(Group.ID);
        Map<String, Object> fields = new HashMap<String, Object>();
        Group group = null;
        Long groupId = null;
        try {
            group = jdbcTemplate.queryForObject("select * from "
                    + TableUtilities.getTableName(Group.class) + " where "
                    + Group.NAME + " = ?", new GroupMapper(), name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (group == null) {
            fields.put(Group.NAME, name);
            fields.put(Group.DESCRIPTION, description);
            fields.put(Group.DISABLE, disable);
            groupId = insertGroup.executeAndReturnKey(fields).longValue();
            group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setId(groupId);
            group.setDisable(disable);
        }
        return group;
    }
}
