package com.itrustcambodia.pluggable.page;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/u")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_USER_MANAGEMENT", description = "Access User Management Page") })
public class UserManagementPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -7073012893184863565L;

    @Override
    public String getPageTitle() {
        return "User Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        layout.add(new BookmarkablePageLink<Void>("newUserPageLink", NewUserPage.class));

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        List<Map<String, Object>> users = application.getJdbcTemplate().queryForList("select * from " + TableUtilities.getTableName(application.getUserEntity()));

        ListView<Map<String, Object>> table = new ListView<Map<String, Object>>("table", users) {

            private static final long serialVersionUID = -3535017717835810733L;

            @Override
            protected void populateItem(ListItem<Map<String, Object>> item) {
                PageParameters parameters = new PageParameters();
                parameters.add("userId", item.getModelObject().get(AbstractUser.ID));
                BookmarkablePageLink<Void> loginLink = new BookmarkablePageLink<Void>("loginLink", EditUserPage.class, parameters);
                item.add(loginLink);

                Label login = new Label("loginLabel", (String) item.getModelObject().get(AbstractUser.LOGIN));
                loginLink.add(login);

                if (item.getModelObject().get(AbstractUser.DISABLE) == null) {
                    Label disable = new Label("disable", "false");
                    item.add(disable);
                } else {
                    Label disable = new Label("disable", (Boolean) item.getModelObject().get(AbstractUser.DISABLE));
                    item.add(disable);
                }
            }
        };
        layout.add(table);
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
