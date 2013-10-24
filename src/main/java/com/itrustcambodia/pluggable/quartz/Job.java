package com.itrustcambodia.pluggable.quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.joda.time.DateTime;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

/**
 * @author Socheat KHAUV
 */
public abstract class Job implements org.quartz.Job {

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        AbstractWebApplication application = (AbstractWebApplication) jobDataMap.get(AbstractWebApplication.class.getName());
        if (!application.isMigrated()) {
            return;
        }
        AbstractPlugin plugin = application.getPlugin(application.getPluginMapping(this.getClass().getName()));
        if (plugin != null) {
            if (!plugin.isMigrated() || !plugin.isActivated()) {
                return;
            }
        }
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        com.itrustcambodia.pluggable.entity.Job job = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", new EntityRowMapper<com.itrustcambodia.pluggable.entity.Job>(com.itrustcambodia.pluggable.entity.Job.class), this.getClass().getName());
        if (job.isDisable() || job.isPause()) {
            return;
        }
        jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.STATUS + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", com.itrustcambodia.pluggable.entity.Job.Status.BUSY, this.getClass().getName());
        try {
            process(application, context);
            jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.STATUS + " = ?, " + com.itrustcambodia.pluggable.entity.Job.LAST_PROCESS + " = ?, " + com.itrustcambodia.pluggable.entity.Job.LAST_ERROR + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", com.itrustcambodia.pluggable.entity.Job.Status.IDLE, new Date(), "", this.getClass().getName());
            job = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", new EntityRowMapper<com.itrustcambodia.pluggable.entity.Job>(com.itrustcambodia.pluggable.entity.Job.class), this.getClass().getName());
            if (job.getNewCron() != null && !"".equals(job.getNewCron())) {
                if (CronExpression.isValidExpression(job.getNewCron())) {
                    jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.CRON + " = ?, " + com.itrustcambodia.pluggable.entity.Job.NEW_CRON + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", job.getNewCron(), "", this.getClass().getName());
                    SchedulerFactory schedulerFactory = application.getBean(SchedulerFactory.class);
                    JobDetail jobDetail = newJob(NewScheduleJob.class).withIdentity(NewScheduleJob.class.getName()).build();
                    jobDetail.getJobDataMap().put(AbstractWebApplication.class.getName(), application);
                    jobDetail.getJobDataMap().put("cron", job.getCron());
                    jobDetail.getJobDataMap().put("clazz", job.getId());
                    DateTime now = new DateTime();
                    Trigger trigger = newTrigger().withIdentity(String.valueOf(System.currentTimeMillis())).startAt(now.toDate()).build();
                    schedulerFactory.getScheduler().scheduleJob(jobDetail, trigger);
                } else {
                    jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.PAUSE + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", true, this.getClass().getName());
                }
            }
        } catch (Throwable e) {
            jdbcTemplate.update("update " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Job.class) + " set " + com.itrustcambodia.pluggable.entity.Job.STATUS + " = ?, " + com.itrustcambodia.pluggable.entity.Job.LAST_PROCESS + " = ?, " + com.itrustcambodia.pluggable.entity.Job.LAST_ERROR + " = ? ," + com.itrustcambodia.pluggable.entity.Job.PAUSE + " = ? where " + com.itrustcambodia.pluggable.entity.Job.ID + " = ?", com.itrustcambodia.pluggable.entity.Job.Status.IDLE, new Date(), e.getMessage(), true, this.getClass().getName());
        }
    }

    public abstract void process(AbstractWebApplication application, JobExecutionContext context);

}
