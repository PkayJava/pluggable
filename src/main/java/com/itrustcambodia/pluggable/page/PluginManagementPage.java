package com.itrustcambodia.pluggable.page;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.PluginRegistry;
import com.itrustcambodia.pluggable.migration.AbstractPluginMigrator;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/p")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_PLUGIN_MANAGEMENT", description = "Access Plugin Management Page") })
public final class PluginManagementPage extends WebPage {

    private static final long serialVersionUID = -384903579385084809L;

    @Override
    public String getPageTitle() {
        return "Plugin Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Border layout = requestLayout("layout");
        add(layout);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        List<PluginRegistry> plugins = jdbcTemplate.query("select * from "
                + TableUtilities.getTableName(PluginRegistry.class),
                new EntityRowMapper<PluginRegistry>(PluginRegistry.class));

        ListView<PluginRegistry> table = new ListView<PluginRegistry>("table",
                plugins) {

            private static final long serialVersionUID = -4046518104364999697L;

            @Override
            protected void populateItem(ListItem<PluginRegistry> item) {
                PluginRegistry pluginRegistry = item.getModelObject();

                Fragment identity = null;

                AbstractWebApplication application = (AbstractWebApplication) getApplication();

                AbstractPlugin plugin = application.getPlugin(pluginRegistry
                        .getIdentity());

                if (pluginRegistry.isPresented()) {
                    identity = new Fragment("identity", "linkFragment",
                            PluginManagementPage.this);
                    BookmarkablePageLink<Void> link = null;

                    link = new BookmarkablePageLink<Void>("link",
                            plugin.getSettingPage());

                    Label label = new Label("label",
                            pluginRegistry.getIdentity());
                    link.add(label);
                    identity.add(link);
                } else {
                    identity = new Fragment("identity", "labelFragment",
                            PluginManagementPage.this);
                    Label label = new Label("label",
                            pluginRegistry.getIdentity());
                    identity.add(label);
                }

                item.add(identity);

                Label name = new Label("name", pluginRegistry.getName());
                item.add(name);
                Label codeVersion = new Label(
                        "codeVersion",
                        !pluginRegistry.isPresented() ? "N/A"
                                : ((AbstractPluginMigrator) application
                                        .getBean(plugin.getMigrator().getName()))
                                        .getVersion());
                item.add(codeVersion);

                Label databaseVersion = new Label("databaseVersion",
                        pluginRegistry.getVersion());
                item.add(databaseVersion);

                String statusString = "N/A";
                if (pluginRegistry.isPresented()) {
                    if (!plugin.isMigrated()) {
                        statusString = "To Be Upgrade";
                    } else {
                        statusString = pluginRegistry.isActivated() ? "Enabled"
                                : "Disabled";
                    }
                } else {
                    statusString = "N/A";
                }

                Label status = new Label("status", statusString);
                item.add(status);
            }
        };
        layout.add(table);
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }
}
