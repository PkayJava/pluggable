package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.ApplicationSetting;
import com.angkorteam.pluggable.framework.entity.PluginSetting;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class PluginSettingMapper extends EntityMapper<PluginSetting> {

    public PluginSettingMapper() {
        super(PluginSetting.class);
    }

    @Override
    public PluginSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
        PluginSetting entity = new PluginSetting();
        entity.setId(rs.getLong(PluginSetting.ID));
        entity.setIdentity(rs.getString(PluginSetting.IDENTITY));
        entity.setName(rs.getString(PluginSetting.NAME));
        entity.setValue(rs.getString(PluginSetting.VALUE));
        return entity;
    }
}
