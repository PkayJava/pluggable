package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.PluginRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class GroupMapper extends EntityMapper<Group> {

    public GroupMapper() {
        super(Group.class);
    }

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        Group entity = new Group();
        entity.setId(rs.getLong(Group.ID));
        entity.setName(rs.getString(Group.NAME));
        entity.setDescription(rs.getString(Group.DESCRIPTION));
        entity.setDisable(rs.getBoolean(Group.DISABLE));
        return entity;
    }
}
