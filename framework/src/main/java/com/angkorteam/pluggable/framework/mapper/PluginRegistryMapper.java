package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.PluginRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class PluginRegistryMapper extends EntityMapper<PluginRegistry> {

    public PluginRegistryMapper() {
        super(PluginRegistry.class);
    }

    @Override
    public PluginRegistry mapRow(ResultSet rs, int rowNum) throws SQLException {
        PluginRegistry entity = new PluginRegistry();
        entity.setActivated(rs.getBoolean(PluginRegistry.ACTIVATED));
        entity.setIdentity(rs.getString(PluginRegistry.IDENTITY));
        entity.setName(rs.getString(PluginRegistry.NAME));
        entity.setPresented(rs.getBoolean(PluginRegistry.PRESENTED));
        entity.setUpgradeDate(rs.getDate(PluginRegistry.UPGRADE_DATE));
        entity.setVersion(rs.getDouble(PluginRegistry.VERSION));
        entity.setId(rs.getLong(PluginRegistry.ID));
        return entity;
    }
}
