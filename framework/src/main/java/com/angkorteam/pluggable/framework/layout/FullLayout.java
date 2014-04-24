package com.angkorteam.pluggable.framework.layout;

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

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.WebSession;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.UserGroup;
import com.angkorteam.pluggable.framework.page.LogoutPage;
import com.angkorteam.pluggable.framework.page.PluginManagementPage;
import com.angkorteam.pluggable.framework.page.WebPage;
import com.angkorteam.pluggable.framework.panel.EmptyPanel;
import com.angkorteam.pluggable.framework.panel.PluginInfoPanel;
import com.angkorteam.pluggable.framework.panel.menu.ItemLabelPanel;
import com.angkorteam.pluggable.framework.panel.menu.ItemLinkPanel;
import com.angkorteam.pluggable.framework.panel.menu.ItemParentPanel;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;

/**
 * @author Socheat KHAUV
 */
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
