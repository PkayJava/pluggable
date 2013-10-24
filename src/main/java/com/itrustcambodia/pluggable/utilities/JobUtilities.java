package com.itrustcambodia.pluggable.utilities;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.WicketRuntimeException;
import org.quartz.CronExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.Job;
import com.itrustcambodia.pluggable.quartz.Scheduled;

public class JobUtilities {
    private JobUtilities() {
    }

    public static final Job createJob(JdbcTemplate jdbcTemplate, Class<? extends com.itrustcambodia.pluggable.quartz.Job> clazz) {
        Scheduled scheduled = clazz.getAnnotation(Scheduled.class);
        if (scheduled == null) {
            throw new WicketRuntimeException("job class " + clazz.getName() + " schedule information is null");
        } else {
            if (scheduled.cron() == null || "".equals(scheduled.cron()) || !CronExpression.isValidExpression(scheduled.cron())) {
                throw new WicketRuntimeException("job class " + clazz.getName() + " cron expression is null or invalid");
            }
        }
        String tableName = TableUtilities.getTableName(Job.class);
        String id = clazz.getName();
        Job job = null;
        if (jdbcTemplate.queryForObject("select count(*) from " + tableName + " where " + Job.ID + " = ?", Long.class, id) > 0) {
            job = jdbcTemplate.queryForObject("select * from " + tableName + " where " + Job.ID + " = ?", new EntityRowMapper<Job>(Job.class), id);
            job.setPause(false);
            job.setStatus(Job.Status.IDLE);
            jdbcTemplate.update("UPDATE " + tableName + " set " + Job.PAUSE + " = ?, " + Job.STATUS + " = ? where " + Job.ID + " = ?", false, Job.Status.IDLE, id);
        } else {
            job = new Job();
            job.setId(id);
            job.setCron(scheduled.cron());
            job.setDescription(scheduled.description());
            job.setDisable(scheduled.disable());
            job.setPause(false);
            job.setStatus(Job.Status.IDLE);
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(Job.ID, id);
            fields.put(Job.CRON, scheduled.cron());
            fields.put(Job.DESCRIPTION, scheduled.description());
            fields.put(Job.DISABLE, scheduled.disable());
            fields.put(Job.PAUSE, false);
            fields.put(Job.STATUS, Job.Status.IDLE);
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(tableName);
            insert.execute(fields);
        }
        return job;
    }
}
