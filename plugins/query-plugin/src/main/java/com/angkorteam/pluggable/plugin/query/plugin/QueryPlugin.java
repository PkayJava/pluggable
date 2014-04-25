package com.angkorteam.pluggable.plugin.query.plugin;

import com.angkorteam.pluggable.framework.core.AbstractPlugin;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.migration.AbstractPluginMigrator;
import com.angkorteam.pluggable.framework.page.PluginSettingPage;
import com.angkorteam.pluggable.framework.page.WebPage;
import com.angkorteam.pluggable.plugin.query.QueryConstants;
import com.angkorteam.pluggable.plugin.query.page.SQLQueryPage;
import com.angkorteam.pluggable.plugin.query.page.SettingPage;

public class QueryPlugin extends AbstractPlugin {

    public static final String ID;
    static {
        ID = QueryConstants.class.getPackage().getName();
    }

    @Override
    public String getName() {
        return "Query Plugin";
    }

    @Override
    public boolean activate(AbstractWebApplication application) {
        return true;
    }

    @Override
    public void initialize(AbstractWebApplication application) {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public String getIdentity() {
        return ID;
    }

    @Override
    public Class<? extends PluginSettingPage> getSettingPage() {
        return SettingPage.class;
    }

    @Override
    public Class<? extends WebPage> getDashboardPage() {
        return SQLQueryPage.class;
    }

    @Override
    public Class<? extends AbstractPluginMigrator> getMigrator() {
        return QueryMigrator.class;
    }

    @Override
    public String[] getPackages() {
        return new String[] { ID };
    }

}
