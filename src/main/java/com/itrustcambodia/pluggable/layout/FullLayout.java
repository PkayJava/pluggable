package com.itrustcambodia.pluggable.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.UserGroup;
import com.itrustcambodia.pluggable.page.LogoutPage;
import com.itrustcambodia.pluggable.page.PluginManagementPage;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.panel.EmptyPanel;
import com.itrustcambodia.pluggable.panel.PluginInfoPanel;
import com.itrustcambodia.pluggable.panel.menu.ItemLabelPanel;
import com.itrustcambodia.pluggable.panel.menu.ItemLinkPanel;
import com.itrustcambodia.pluggable.panel.menu.ItemParentPanel;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

public class FullLayout extends Bootstrap3Layout {

    /**
     * 
     */
    private static final long serialVersionUID = 933002174394197672L;

    public FullLayout(String id) {
        super(id);
    }

    public FullLayout(String id, IModel<?> model) {
        super(id, model);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        BookmarkablePageLink<Void> logout = new BookmarkablePageLink<Void>("logout", LogoutPage.class);

        WebSession session = (WebSession) getSession();
        logout.setVisible(session.isSignedIn());

        WebMarkupContainer rightBar = new WebMarkupContainer("rightBar");
        addToBorder(rightBar);
        rightBar.add(logout);

        String identity = application.getPluginMapping(getPage().getClass().getName());
        Panel pluginInfo = null;
        if (identity != null && !"".equals(identity)) {
            pluginInfo = new PluginInfoPanel("pluginInfo", application.getPlugin(identity).getName(), identity);
        } else {
            WebPage page = (WebPage) getPage();
            pluginInfo = new PluginInfoPanel("pluginInfo", "Welcome to " + application.getBrandLabel(), page.getPageTitle());
        }
        addToBorder(pluginInfo);

        BookmarkablePageLink<Void> brandPage = new BookmarkablePageLink<Void>("brandPage", application.getDashboardPage());
        Label brandLabel = new Label("brandLabel", "Dashboard");
        brandPage.add(brandLabel);
        addToBorder(brandPage);

        List<Menu> applicationMenus = null;

        applicationMenus = ((AbstractWebApplication) application).getApplicationMenus(session.getRoles());
        if (applicationMenus == null) {
            applicationMenus = new ArrayList<Menu>();
        }
        Menu security = FrameworkUtilities.getSecurityMenu((AbstractWebApplication) application, session.getRoles());
        if (security != null && security.getChildren() != null && !security.getChildren().isEmpty()) {
            applicationMenus.add(security);
        }

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (session.getUsername() != null && !"".equals(session.getUsername())) {
            long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(UserGroup.class) + " user_group inner join " + TableUtilities.getTableName(AbstractUser.class) + " user on user_group." + UserGroup.USER_ID + " = user." + AbstractUser.ID + " inner join " + TableUtilities.getTableName(Group.class) + " `group` on user_group." + UserGroup.GROUP_ID + " = `group`." + Group.ID + " where user." + AbstractUser.LOGIN + " = ? and `group`." + Group.NAME + " = ?", Long.class, session.getUsername(), AbstractWebApplication.SUPER_ADMIN_GROUP);
            if (count > 0) {
                List<Menu> pluginMenus = application.getPluginMenus();
                if (pluginMenus != null && !pluginMenus.isEmpty()) {
                    Menu plugins = Menu.parentMenu(application.getPluginLabel());

                    for (Menu menu : pluginMenus) {
                        plugins.getChildren().add(menu);
                    }

                    Menu divider = Menu.dividerMenu();
                    plugins.getChildren().add(divider);

                    plugins.getChildren().add(Menu.linkMenu("Plugin Management", PluginManagementPage.class));

                    applicationMenus.add(plugins);

                }
            }
        }

        ListView<Menu> leftNavItems = new ListView<Menu>("leftNavItems", applicationMenus) {

            private static final long serialVersionUID = 7171982333612770707L;

            @Override
            protected void populateItem(ListItem<Menu> item) {
                if (item.getModelObject().getType() == Menu.Type.LINK) {
                    ItemLinkPanel itemLinkPanel = new ItemLinkPanel("navItem", item.getModelObject());
                    item.add(itemLinkPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.PARENT) {
                    ItemParentPanel itemParentPanel = new ItemParentPanel("navItem", item.getModelObject());
                    item.add(itemParentPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.LABEL) {
                    ItemLabelPanel itemLabelPanel = new ItemLabelPanel("navItem", item.getModelObject());
                    item.add(itemLabelPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.DIVIDER || item.getModelObject().getType() == Menu.Type.HEAD) {
                    EmptyPanel itemEmptyPanel = new EmptyPanel("navItem");
                    item.add(itemEmptyPanel);
                }

            }
        };
        addToBorder(leftNavItems);
    }
}
