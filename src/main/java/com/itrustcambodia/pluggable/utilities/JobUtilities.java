package com.itrustcambodia.pluggable.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.Job;
import com.itrustcambodia.pluggable.quartz.Scheduled;

public class JobUtilities {
    private JobUtilities() {
    }

    public static final void register(ScheduledTaskRegistrar registrar,
            Scheduled scheduled, com.itrustcambodia.pluggable.quartz.Job bean) {

        try {
            Runnable runnable = new ScheduledMethodRunnable(bean, "execute");

            boolean processedSchedule = false;
            String errorMessage = "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";

            // Determine initial delay
            long initialDelay = scheduled.initialDelay();
            String initialDelayString = scheduled.initialDelayString();
            if (!"".equals(initialDelayString)) {
                Assert.isTrue(initialDelay < 0,
                        "Specify 'initialDelay' or 'initialDelayString', not both");

                try {
                    initialDelay = Integer.parseInt(initialDelayString);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "Invalid initialDelayString value \""
                                    + initialDelayString
                                    + "\" - cannot parse into integer");
                }
            }

            // Check cron expression
            String cron = scheduled.cron();
            if (!"".equals(cron)) {
                Assert.isTrue(initialDelay == -1,
                        "'initialDelay' not supported for cron triggers");
                processedSchedule = true;
                String zone = scheduled.zone();
                TimeZone timeZone;
                if (!"".equals(zone)) {
                    timeZone = StringUtils.parseTimeZoneString(zone);
                } else {
                    timeZone = TimeZone.getDefault();
                }
                registrar.addCronTask(new CronTask(runnable, new CronTrigger(
                        cron, timeZone)));
            }

            // At this point we don't need to differentiate between initial
            // delay set or not anymore
            if (initialDelay < 0) {
                initialDelay = 0;
            }

            // Check fixed delay
            long fixedDelay = scheduled.fixedDelay();
            if (fixedDelay >= 0) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                registrar.addFixedDelayTask(new IntervalTask(runnable,
                        fixedDelay, initialDelay));
            }
            String fixedDelayString = scheduled.fixedDelayString();
            if (!"".equals(fixedDelayString)) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                try {
                    fixedDelay = Integer.parseInt(fixedDelayString);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "Invalid fixedDelayString value \""
                                    + fixedDelayString
                                    + "\" - cannot parse into integer");
                }
                registrar.addFixedDelayTask(new IntervalTask(runnable,
                        fixedDelay, initialDelay));
            }

            // Check fixed rate
            long fixedRate = scheduled.fixedRate();
            if (fixedRate >= 0) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                registrar.addFixedRateTask(new IntervalTask(runnable,
                        fixedRate, initialDelay));
            }
            String fixedRateString = scheduled.fixedRateString();
            if (!"".equals(fixedRateString)) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                try {
                    fixedRate = Integer.parseInt(fixedRateString);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "Invalid fixedRateString value \""
                                    + fixedRateString
                                    + "\" - cannot parse into integer");
                }
                registrar.addFixedRateTask(new IntervalTask(runnable,
                        fixedRate, initialDelay));
            }

        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Encountered invalid @Scheduled method '"
                            + bean.getClass().getName() + "': "
                            + ex.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Encountered no process method '"
                    + bean.getClass().getName() + "': " + e.getMessage());
        }
    }

    public static final Job createJob(JdbcTemplate jdbcTemplate,
            Class<? extends com.itrustcambodia.pluggable.quartz.Job> clazz) {
        Scheduled scheduled = clazz.getAnnotation(Scheduled.class);
        String tableName = TableUtilities.getTableName(Job.class);
        String id = clazz.getName();
        Job job = null;
        if (jdbcTemplate.queryForObject("select count(*) from " + tableName
                + " where " + Job.ID + " = ?", Long.class, id) > 0) {
            job = jdbcTemplate.queryForObject("select * from " + tableName
                    + " where " + Job.ID + " = ?", new EntityRowMapper<Job>(
                    Job.class), id);
            job.setPause(false);
            job.setStatus(Job.Status.IDLE);
            jdbcTemplate.update("UPDATE " + tableName + " set " + Job.CRON
                    + " = ?, " + Job.PAUSE + " = ?, " + Job.STATUS
                    + " = ? where " + Job.ID + " = ?", scheduled.cron(), false,
                    Job.Status.IDLE, id);
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
