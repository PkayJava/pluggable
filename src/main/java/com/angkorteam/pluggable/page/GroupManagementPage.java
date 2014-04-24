package com.angkorteam.pluggable.page;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.core.Menu;
import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.database.EntityRowMapper;
import com.angkorteam.pluggable.entity.Group;
import com.angkorteam.pluggable.layout.AbstractLayout;
import com.angkorteam.pluggable.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.utilities.TableUtilities;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/g")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_GROUP_MANAGEMENT", description = "Access Group Management Page") })
public final class GroupManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = 5045329317380838559L;

    @Override
    public String getPageTitle() {
        return "Group Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        final AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        layout.add(new BookmarkablePageLink<Void>("newGroupPageLink",
                application.getNewGroupPage()));

        List<Group> groups = jdbcTemplate.query("select * from "
                + TableUtilities.getTableName(Group.class),
                new EntityRowMapper<Group>(Group.class));
        ListView<Group> table = new ListView<Group>("table", groups) {

            private static final long serialVersionUID = -8045778852435218474L;

            @Override
            protected void populateItem(ListItem<Group> item) {

                PageParameters parameters = new PageParameters();
                parameters.add("groupId", item.getModelObject().getId());

                BookmarkablePageLink<Void> nameLink = new BookmarkablePageLink<Void>(
                        "nameLink", application.getEditGroupPage(), parameters);
                item.add(nameLink);

                Label nameLabel = new Label("nameLabel", item.getModelObject()
                        .getName());
                nameLink.add(nameLabel);

                Label description = new Label("description", item
                        .getModelObject().getDescription());
                item.add(description);

                Label disable = new Label("disable", item.getModelObject()
                        .isDisable());
                item.add(disable);
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
