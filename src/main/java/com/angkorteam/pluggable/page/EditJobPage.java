package com.angkorteam.pluggable.page;

import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.core.Menu;
import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.database.EntityRowMapper;
import com.angkorteam.pluggable.entity.Job;
import com.angkorteam.pluggable.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.utilities.TableUtilities;
import com.angkorteam.pluggable.validation.constraints.NotNull;
import com.angkorteam.pluggable.validation.controller.Navigation;
import com.angkorteam.pluggable.wicket.authroles.Role;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.angkorteam.pluggable.widget.Button;
import com.angkorteam.pluggable.widget.CheckBox;
import com.angkorteam.pluggable.widget.LabelField;
import com.angkorteam.pluggable.widget.TextArea;

/**
 * @author Socheat KHAUV
 */
@Mount("/z")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_JOB", description = "Access Edit Job Page") })
public final class EditJobPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 3570912758453166177L;

    @LabelField(label = "Job Name", order = 1)
    private String jobId;

    @NotNull
    @TextArea(label = "Description", order = 2)
    private String description;

    @NotNull
    @LabelField(label = "Cron Expression", order = 3)
    private String cron;

    @CheckBox(label = "Disable", placeholder = "Is disable ?")
    private boolean disable;

    @CheckBox(label = "Pause", placeholder = "Is Pause ?")
    private boolean pause;

    public EditJobPage(PageParameters parameters) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        this.jobId = parameters.get("jobId").toString();
        Job job = jdbcTemplate.queryForObject(
                "select * from " + TableUtilities.getTableName(Job.class)
                        + " where " + Job.ID + " = ?",
                new EntityRowMapper<Job>(Job.class), this.jobId);
        this.description = job.getDescription();
        this.cron = (job.getNewCron() == null || "".equals(job.getNewCron())) ? job
                .getCron() : job.getNewCron();
        this.disable = job.isDisable();
        this.pause = job.isPause();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
    }

    @Button(label = "Okay", order = 3, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("UPDATE " + TableUtilities.getTableName(Job.class)
                + " SET " + Job.DESCRIPTION + " = ?, " + Job.DISABLE + " = ?, "
                + Job.PAUSE + " = ? where " + Job.ID + " = ?",
                this.description, this.disable, this.pause, this.jobId);
        return new Navigation(JobManagementPage.class);
    }

    @Button(label = "Cancel", order = 1, validate = false)
    public Navigation Cancel() {
        return new Navigation(JobManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Job";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
