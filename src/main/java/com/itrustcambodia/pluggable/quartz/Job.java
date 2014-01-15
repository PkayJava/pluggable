package com.itrustcambodia.pluggable.quartz;

import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

/**
 * @author Socheat KHAUV
 */
public abstract class Job {

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
        com.itrustcambodia.pluggable.entity.Job job = jdbcTemplate
                .queryForObject(
                        "select * from "
                                + TableUtilities
                                        .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                                + " where "
                                + com.itrustcambodia.pluggable.entity.Job.ID
                                + " = ?",
                        new EntityRowMapper<com.itrustcambodia.pluggable.entity.Job>(
                                com.itrustcambodia.pluggable.entity.Job.class),
                        this.getClass().getName());
        if (job.isDisable()
                || job.isPause()
                || com.itrustcambodia.pluggable.entity.Job.Status.BUSY
                        .equals(job.getStatus())) {
            return;
        }
        // LOGGER.info("job {}", this.getClass().getName());
        jdbcTemplate
                .update("update "
                        + TableUtilities
                                .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                        + " set "
                        + com.itrustcambodia.pluggable.entity.Job.STATUS
                        + " = ? where "
                        + com.itrustcambodia.pluggable.entity.Job.ID + " = ?",
                        com.itrustcambodia.pluggable.entity.Job.Status.BUSY,
                        this.getClass().getName());
        try {
            process(application);
            jdbcTemplate
                    .update("update "
                            + TableUtilities
                                    .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                            + " set "
                            + com.itrustcambodia.pluggable.entity.Job.STATUS
                            + " = ?, "
                            + com.itrustcambodia.pluggable.entity.Job.LAST_PROCESS
                            + " = ?, "
                            + com.itrustcambodia.pluggable.entity.Job.LAST_ERROR
                            + " = ? where "
                            + com.itrustcambodia.pluggable.entity.Job.ID
                            + " = ?",
                            com.itrustcambodia.pluggable.entity.Job.Status.IDLE,
                            new Date(), "", this.getClass().getName());
            job = jdbcTemplate
                    .queryForObject(
                            "select * from "
                                    + TableUtilities
                                            .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                                    + " where "
                                    + com.itrustcambodia.pluggable.entity.Job.ID
                                    + " = ?",
                            new EntityRowMapper<com.itrustcambodia.pluggable.entity.Job>(
                                    com.itrustcambodia.pluggable.entity.Job.class),
                            this.getClass().getName());
        } catch (Throwable e) {
            jdbcTemplate
                    .update("update "
                            + TableUtilities
                                    .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                            + " set "
                            + com.itrustcambodia.pluggable.entity.Job.STATUS
                            + " = ?, "
                            + com.itrustcambodia.pluggable.entity.Job.LAST_PROCESS
                            + " = ?, "
                            + com.itrustcambodia.pluggable.entity.Job.LAST_ERROR
                            + " = ? ,"
                            + com.itrustcambodia.pluggable.entity.Job.PAUSE
                            + " = ? where "
                            + com.itrustcambodia.pluggable.entity.Job.ID
                            + " = ?",
                            com.itrustcambodia.pluggable.entity.Job.Status.IDLE,
                            new Date(), e.getMessage(), true, this.getClass()
                                    .getName());
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
