package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.ApplicationRegistry;
import com.angkorteam.pluggable.framework.entity.PluginRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class ApplicationRegistryMapper extends EntityMapper<ApplicationRegistry> {

    public ApplicationRegistryMapper() {
        super(ApplicationRegistry.class);
    }

    @Override
    public ApplicationRegistry mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApplicationRegistry entity = new ApplicationRegistry();
        entity.setVersion(rs.getDouble(ApplicationRegistry.VERSION));
        entity.setUpgradeDate(rs.getDate(ApplicationRegistry.UPGRADE_DATE));
        entity.setId(rs.getLong(ApplicationRegistry.ID));
        return entity;
    }
}
