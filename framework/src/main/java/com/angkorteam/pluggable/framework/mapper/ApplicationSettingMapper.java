package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.ApplicationSetting;
import com.angkorteam.pluggable.framework.entity.Job;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class ApplicationSettingMapper extends EntityMapper<ApplicationSetting> {

    public ApplicationSettingMapper() {
        super(ApplicationSetting.class);
    }

    @Override
    public ApplicationSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApplicationSetting entity = new ApplicationSetting();
        entity.setId(rs.getLong(ApplicationSetting.ID));
        entity.setName(rs.getString(ApplicationSetting.NAME));
        entity.setValue(rs.getString(ApplicationSetting.VALUE));
        return entity;
    }
}
