package com.itrustcambodia.pluggable.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

public class NewScheduleJob implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        AbstractWebApplication application = (AbstractWebApplication) jobDataMap.get(AbstractWebApplication.class.getName());

        String clazz = (String) jobDataMap.get("clazz");

        SchedulerFactory schedulerFactory = application.getBean(SchedulerFactory.class);
        try {
            schedulerFactory.getScheduler().deleteJob(JobKey.jobKey(clazz));
        } catch (SchedulerException e1) {
        }

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        com.itrustcambodia.pluggable.entity.Job job = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", new EntityRowMapper<com.itrustcambodia.pluggable.entity.Job>(com.itrustcambodia.pluggable.entity.Job.class), clazz);
        if (job.isDisable() || job.isPause()) {
            return;
        }

        try {
            JobDetail jobDetail = null;
            try {
                jobDetail = newJob((Class<Job>) Class.forName(clazz)).withIdentity(clazz).build();
            } catch (ClassNotFoundException e) {
            }
            if (jobDetail != null) {
                jobDetail.getJobDataMap().put(AbstractWebApplication.class.getName(), application);
                CronTrigger trigger = newTrigger().withIdentity(clazz).withSchedule(cronSchedule(job.getCron())).build();
                schedulerFactory.getScheduler().scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.DISABLE + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", true, clazz);
        }

    }

}
