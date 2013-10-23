package com.itrustcambodia.pluggable.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;

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
        process(application, context);
    }

    public abstract void process(AbstractWebApplication application, JobExecutionContext context);

}
