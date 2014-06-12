package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class RoleMapper extends EntityMapper<Role> {

    public RoleMapper() {
        super(Role.class);
    }

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role entity = new Role();
        entity.setId(rs.getLong(Role.ID));
        entity.setName(rs.getString(Role.NAME));
        entity.setDescription(rs.getString(Role.DESCRIPTION));
        entity.setDisable(rs.getBoolean(Role.DISABLE));
        return entity;
    }
}
