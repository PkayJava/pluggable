package com.angkorteam.pluggable.quartz;

import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.core.AbstractPlugin;
import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.database.EntityRowMapper;
import com.angkorteam.pluggable.utilities.TableUtilities;

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
        com.angkorteam.pluggable.entity.Job job = jdbcTemplate
                .queryForObject(
                        "select * from "
                                + TableUtilities
                                        .getTableName(com.angkorteam.pluggable.entity.Job.class)
                                + " where "
                                + com.angkorteam.pluggable.entity.Job.ID
                                + " = ?",
                        new EntityRowMapper<com.angkorteam.pluggable.entity.Job>(
                                com.angkorteam.pluggable.entity.Job.class),
                        this.getClass().getName());
        if (job.isDisable()
                || job.isPause()
                || com.angkorteam.pluggable.entity.Job.Status.BUSY
                        .equals(job.getStatus())) {
            return;
        }
        // LOGGER.info("job {}", this.getClass().getName());
        jdbcTemplate
                .update("update "
                        + TableUtilities
                                .getTableName(com.angkorteam.pluggable.entity.Job.class)
                        + " set "
                        + com.angkorteam.pluggable.entity.Job.STATUS
                        + " = ? where "
                        + com.angkorteam.pluggable.entity.Job.ID + " = ?",
                        com.angkorteam.pluggable.entity.Job.Status.BUSY,
                        this.getClass().getName());
        try {
            process(application);
            jdbcTemplate
                    .update("update "
                            + TableUtilities
                                    .getTableName(com.angkorteam.pluggable.entity.Job.class)
                            + " set "
                            + com.angkorteam.pluggable.entity.Job.STATUS
                            + " = ?, "
                            + com.angkorteam.pluggable.entity.Job.LAST_PROCESS
                            + " = ?, "
                            + com.angkorteam.pluggable.entity.Job.LAST_ERROR
                            + " = ? where "
                            + com.angkorteam.pluggable.entity.Job.ID
                            + " = ?",
                            com.angkorteam.pluggable.entity.Job.Status.IDLE,
                            new Date(), "", this.getClass().getName());
            job = jdbcTemplate
                    .queryForObject(
                            "select * from "
                                    + TableUtilities
                                            .getTableName(com.angkorteam.pluggable.entity.Job.class)
                                    + " where "
                                    + com.angkorteam.pluggable.entity.Job.ID
                                    + " = ?",
                            new EntityRowMapper<com.angkorteam.pluggable.entity.Job>(
                                    com.angkorteam.pluggable.entity.Job.class),
                            this.getClass().getName());
        } catch (Throwable e) {
            try {
                jdbcTemplate
                        .update("update "
                                + TableUtilities
                                        .getTableName(com.angkorteam.pluggable.entity.Job.class)
                                + " set "
                                + com.angkorteam.pluggable.entity.Job.STATUS
                                + " = ?, "
                                + com.angkorteam.pluggable.entity.Job.LAST_PROCESS
                                + " = ?, "
                                + com.angkorteam.pluggable.entity.Job.LAST_ERROR
                                + " = ? ,"
                                + com.angkorteam.pluggable.entity.Job.PAUSE
                                + " = ? where "
                                + com.angkorteam.pluggable.entity.Job.ID
                                + " = ?",
                                com.angkorteam.pluggable.entity.Job.Status.IDLE,
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