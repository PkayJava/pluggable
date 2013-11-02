package com.itrustcambodia.pluggable.page;

import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.Job;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.validator.CronExpressionValidator;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;
import com.itrustcambodia.pluggable.widget.CheckBox;
import com.itrustcambodia.pluggable.widget.LabelField;
import com.itrustcambodia.pluggable.widget.TextArea;
import com.itrustcambodia.pluggable.widget.TextField;

/**
 * @author Socheat KHAUV
 */
@Mount("/z")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_JOB", description = "Access Edit Job Page") })
public class EditJobPage extends KnownPage {

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
    @TextField(label = "Cron Expression", placeholder = "Cron Expression", order = 3)
    private String cron;

    @CheckBox(label = "Disable", placeholder = "Is disable ?")
    private boolean disable;

    @CheckBox(label = "Pause", placeholder = "Is Pause ?")
    private boolean pause;

    public EditJobPage(PageParameters parameters) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        this.jobId = parameters.get("jobId").toString();
        Job job = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Job.class) + " where " + Job.ID + " = ?", new EntityRowMapper<Job>(Job.class), this.jobId);
        this.description = job.getDescription();
        this.cron = (job.getNewCron() == null || "".equals(job.getNewCron())) ? job.getCron() : job.getNewCron();
        this.disable = job.isDisable();
        this.pause = job.isPause();
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        org.apache.wicket.markup.html.form.TextField<String> cron = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("cron");
        cron.add(new CronExpressionValidator());
        org.apache.wicket.markup.html.form.Button once = (org.apache.wicket.markup.html.form.Button) getFormButton("once");
        once.setVisible(!this.disable);
    }

    @Button(label = "Okay", order = 3, validate = true)
    public Navigation okay() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("UPDATE " + TableUtilities.getTableName(Job.class) + " SET " + Job.DESCRIPTION + " = ?, " + Job.DISABLE + " = ?, " + Job.PAUSE + " = ? , " + Job.NEW_CRON + " = ? where " + Job.ID + " = ?", this.description, this.disable, this.pause, this.cron, this.jobId);
        return new Navigation(JobManagementPage.class);
    }

    @Button(label = "Run Once", order = 2, validate = false)
    public Navigation once() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        Job job = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Job.class) + " where " + Job.ID + " = ?", new EntityRowMapper<Job>(Job.class), this.jobId);
        if (Job.Status.IDLE.equals(job.getStatus())) {
            try {
                application.getSchedulerFactory().getScheduler().triggerJob(JobKey.jobKey(job.getId()));
            } catch (SchedulerException e) {
            }
        }
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
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
