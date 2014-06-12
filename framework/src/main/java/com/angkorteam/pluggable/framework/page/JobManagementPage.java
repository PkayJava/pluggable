package com.angkorteam.pluggable.framework.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.angkorteam.pluggable.framework.mapper.JobMapper;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Job;
import com.angkorteam.pluggable.framework.layout.AbstractLayout;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/q")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_JOB_MANAGEMENT", description = "Access Job Management Page") })
public final class JobManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -8827747090739298001L;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss ZZ");

    @Override
    public String getPageTitle() {
        return "Job Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        List<Job> jobs = jdbcTemplate.query(
                "select * from " + TableUtilities.getTableName(Job.class),
               new JobMapper());

        ListView<Job> table = new ListView<Job>("table", jobs) {

            private static final long serialVersionUID = -8045778852435218474L;

            @Override
            protected void populateItem(ListItem<Job> item) {

                Job job = item.getModelObject();

                PageParameters parameters = new PageParameters();
                parameters.add("jobId", job.getId());

                BookmarkablePageLink<Void> nameLink = new BookmarkablePageLink<Void>(
                        "nameLink", EditJobPage.class, parameters);
                item.add(nameLink);

                Label nameLabel = new Label("nameLabel", job.getId());
                nameLink.add(nameLabel);

                Label description = new Label("description",
                        job.getDescription());
                item.add(description);

                Label disable = new Label("disable", job.isDisable());
                item.add(disable);

                Label status = new Label("status", job.getStatus());
                item.add(status);

                Label pause = new Label("pause", job.isPause());
                item.add(pause);

                Label cron = new Label("cron", job.getNewCron() == null
                        || "".equals(job.getNewCron()) ? job.getCron()
                        : job.getNewCron());
                item.add(cron);

                Label lastError = new Label("lastError",
                        job.getLastError() == null
                                || "".equals(job.getLastError()) ? "N/A"
                                : job.getLastError());
                item.add(lastError);

                Label lastProcess = new Label("lastProcess",
                        job.getLastProcess() == null ? "N/A"
                                : DATE_FORMAT.format(job.getLastProcess()));
                item.add(lastProcess);
            }
        };
        layout.add(table);
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
