package com.angkorteam.pluggable.framework.quartz;

import java.util.Date;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.mapper.JobMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.core.AbstractPlugin;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractJob {

    // private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

    private AbstractWebApplication application;

    public final void execute() {
        if (!application.isMigrated()) {
            return;
        }
        AbstractPlugin plugin = application.getPlugin(application
                .getPluginMapping(this.getClass().getName()));
        if (plugin != null) {
            if (!plugin.isMigrated() || !plugin.isActivated()) {
                return;
            }
        }
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        com.angkorteam.pluggable.framework.entity.Job job = jdbcTemplate
                .queryForObject(
                        "select * from "
                                + TableUtilities
                                        .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                                + " where "
                                + com.angkorteam.pluggable.framework.entity.Job.ID
                                + " = ?",
                        new JobMapper(),
                        this.getClass().getName());
        if (job.isDisable()
                || job.isPause()
                || com.angkorteam.pluggable.framework.entity.Job.Status.BUSY
                        .equals(job.getStatus())) {
            return;
        }
        // LOGGER.info("job {}", this.getClass().getName());
        jdbcTemplate
                .update("update "
                        + TableUtilities
                                .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                        + " set "
                        + com.angkorteam.pluggable.framework.entity.Job.STATUS
                        + " = ? where "
                        + com.angkorteam.pluggable.framework.entity.Job.ID + " = ?",
                        com.angkorteam.pluggable.framework.entity.Job.Status.BUSY,
                        this.getClass().getName());
        try {
            process(application);
            jdbcTemplate
                    .update("update "
                            + TableUtilities
                                    .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                            + " set "
                            + com.angkorteam.pluggable.framework.entity.Job.STATUS
                            + " = ?, "
                            + com.angkorteam.pluggable.framework.entity.Job.LAST_PROCESS
                            + " = ?, "
                            + com.angkorteam.pluggable.framework.entity.Job.LAST_ERROR
                            + " = ? where "
                            + com.angkorteam.pluggable.framework.entity.Job.ID
                            + " = ?",
                            com.angkorteam.pluggable.framework.entity.Job.Status.IDLE,
                            new Date(), "", this.getClass().getName());
            job = jdbcTemplate
                    .queryForObject(
                            "select * from "
                                    + TableUtilities
                                            .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                                    + " where "
                                    + com.angkorteam.pluggable.framework.entity.Job.ID
                                    + " = ?",
                           new JobMapper(),
                            this.getClass().getName());
        } catch (Throwable e) {
            try {
                jdbcTemplate
                        .update("update "
                                + TableUtilities
                                        .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                                + " set "
                                + com.angkorteam.pluggable.framework.entity.Job.STATUS
                                + " = ?, "
                                + com.angkorteam.pluggable.framework.entity.Job.LAST_PROCESS
                                + " = ?, "
                                + com.angkorteam.pluggable.framework.entity.Job.LAST_ERROR
                                + " = ? ,"
                                + com.angkorteam.pluggable.framework.entity.Job.PAUSE
                                + " = ? where "
                                + com.angkorteam.pluggable.framework.entity.Job.ID
                                + " = ?",
                                com.angkorteam.pluggable.framework.entity.Job.Status.IDLE,
                                new Date(), e.getMessage(), true, this
                                        .getClass().getName());
            } catch (Throwable e1) {
            }
        }
    }

    public abstract void process(AbstractWebApplication application);

    public AbstractWebApplication getApplication() {
        return application;
    }

    public void setApplication(AbstractWebApplication application) {
        this.application = application;
    }

}
