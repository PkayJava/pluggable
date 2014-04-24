package com.angkorteam.pluggable.framework.page;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.database.EntityRowMapper;
import com.angkorteam.pluggable.framework.entity.Role;
import com.angkorteam.pluggable.framework.layout.AbstractLayout;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/r")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.framework.wicket.authroles.Role(name = "ROLE_PAGE_ROLE_MANAGEMENT", description = "Access Role Management Page") })
public final class RoleManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -8351965843828778810L;

    @Override
    public String getPageTitle() {
        return "Role Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        List<Role> roles = application.getJdbcTemplate().query(
                "select * from " + TableUtilities.getTableName(Role.class),
                new EntityRowMapper<Role>(Role.class));

        ListView<Role> table = new ListView<Role>("table", roles) {

            private static final long serialVersionUID = -3535017717835810733L;

            @Override
            protected void populateItem(ListItem<Role> item) {
                PageParameters parameters = new PageParameters();
                parameters.add("roleId", item.getModelObject().getId());
                BookmarkablePageLink<Void> nameLink = new BookmarkablePageLink<Void>(
                        "nameLink", EditRolePage.class, parameters);
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
