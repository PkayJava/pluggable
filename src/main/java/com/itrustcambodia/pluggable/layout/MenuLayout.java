package com.itrustcambodia.pluggable.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.behaviour.ActiveMenuBehaviour;
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
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

public class MenuLayout extends Bootstrap3Layout {

    /**
     * 
     */
    private static final long serialVersionUID = 933002174394197672L;

    public MenuLayout(String id) {
        super(id);
    }

    public MenuLayout(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        BookmarkablePageLink<Void> brandPage = new BookmarkablePageLink<Void>("brandPage", application.getDashboardPage());
        Label brandLabel = new Label("brandLabel", "Dashboard");
        brandPage.add(brandLabel);
        addToBorder(brandPage);

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

        List<Menu> applicationMenus = null;

        applicationMenus = ((AbstractWebApplication) application).getApplicationMenus(session.getRoles());
        if (applicationMenus == null) {
            applicationMenus = new ArrayList<Menu>();
        }
        Menu security = FrameworkUtilities.getSecurityMenu((AbstractWebApplication) application, session.getRoles());
        if (security != null && security.getChildren() != null && !security.getChildren().isEmpty()) {
            applicationMenus.add(security);
        }

        Roles roles = session.getRoles();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (session.getUsername() != null && !"".equals(session.getUsername())) {
            long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(UserGroup.class) + " user_group inner join " + TableUtilities.getTableName(AbstractUser.class) + " user on user_group." + UserGroup.USER_ID + " = user." + AbstractUser.ID + " inner join " + TableUtilities.getTableName(Group.class) + " `group` on user_group." + UserGroup.GROUP_ID + " = `group`." + Group.ID + " where user." + AbstractUser.LOGIN + " = ? and `group`." + Group.NAME + " = ?", Long.class, session.getUsername(), AbstractWebApplication.SUPER_ADMIN_GROUP);
            if (count > 0) {
                List<Menu> pluginMenus = application.getPluginMenus();
                if (pluginMenus != null && !pluginMenus.isEmpty()) {
                    Menu plugins = Menu.parentMenu(application.getPluginLabel());

                    applicationMenus.add(plugins);

                    for (Menu menu : pluginMenus) {
                        plugins.getChildren().add(menu);
                    }
                    Menu divider = Menu.dividerMenu();
                    plugins.getChildren().add(divider);

                    plugins.getChildren().add(Menu.linkMenu("Plugin Management", PluginManagementPage.class));
                }
            }
        }

        ListView<Menu> leftNavItems = new ListView<Menu>("leftNavItems", applicationMenus) {

            private static final long serialVersionUID = 7171982333612770707L;

            @Override
            protected void populateItem(ListItem<Menu> item) {
                Panel panel = null;
                if (item.getModelObject().getType() == Menu.Type.LINK) {
                    panel = new ItemLinkPanel("navItem", item.getModelObject());
                }
                if (item.getModelObject().getType() == Menu.Type.PARENT) {
                    panel = new ItemParentPanel("navItem", item.getModelObject());
                }
                if (item.getModelObject().getType() == Menu.Type.LABEL) {
                    panel = new ItemLabelPanel("navItem", item.getModelObject());
                }
                if (item.getModelObject().getType() == Menu.Type.DIVIDER || item.getModelObject().getType() == Menu.Type.HEAD) {
                    panel = new EmptyPanel("navItem");
                }
                item.add(panel);
            }
        };
        addToBorder(leftNavItems);

        WebPage page = (WebPage) getPage();
        List<Menu> pageMenus = page.getPageMenus(roles);
        ListView<Menu> leftMenu = new ListView<Menu>("leftMenu", pageMenus) {

            private static final long serialVersionUID = -6439382547158547449L;

            @Override
            protected void populateItem(ListItem<Menu> item) {
                if (item.getModelObject().getType() == Menu.Type.DIVIDER) {
                    EmptyPanel itemEmptyPanel = new EmptyPanel("navItem");
                    item.add(itemEmptyPanel);
                } else if (item.getModelObject().getType() == Menu.Type.LABEL || item.getModelObject().getType() == Menu.Type.HEAD) {
                    Fragment navItemHeadFragment = new Fragment("navItem", "navItemHeadFragment", MenuLayout.this);
                    Label label = new Label("label", item.getModelObject().getLabel());
                    navItemHeadFragment.add(label);
                    item.add(navItemHeadFragment);
                } else {
                    Fragment navItemLinkFragment = new Fragment("navItem", "navItemLinkFragment", MenuLayout.this);
                    BookmarkablePageLink<Void> page = new BookmarkablePageLink<Void>("page", item.getModelObject().getPage());
                    page.add(new ActiveMenuBehaviour(item.getModelObject()));
                    Label label = new Label("label", item.getModelObject().getLabel());
                    page.add(label);

                    Label badge = new Label("badge", item.getModelObject().getBadge());
                    page.add(badge);
                    badge.setVisible(item.getModelObject().getBadge() > 0);

                    WebMarkupContainer arrow = new WebMarkupContainer("arrow");
                    arrow.setVisible(item.getModelObject().getBadge() <= 0);
                    page.add(arrow);

                    navItemLinkFragment.add(page);
                    item.add(navItemLinkFragment);
                }
            }
        };
        addToBorder(leftMenu);

    }
}
