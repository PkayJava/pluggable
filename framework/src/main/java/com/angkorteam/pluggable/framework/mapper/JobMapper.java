package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Job;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class JobMapper extends EntityMapper<Job> {

    public JobMapper() {
        super(Job.class);
    }

    @Override
    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
        Job entity = new Job();
        entity.setCron(rs.getString(Job.CRON));
        entity.setId(rs.getString(Job.ID));
        entity.setLastError(rs.getString(Job.LAST_ERROR));
        entity.setLastProcess(rs.getDate(Job.LAST_PROCESS));
        entity.setNewCron(rs.getString(Job.NEW_CRON));
        entity.setPause(rs.getBoolean(Job.PAUSE));
        entity.setStatus(rs.getString(Job.STATUS));
        entity.setDescription(rs.getString(Job.DESCRIPTION));
        entity.setDisable(rs.getBoolean(Job.DISABLE));
        return entity;
    }
}
