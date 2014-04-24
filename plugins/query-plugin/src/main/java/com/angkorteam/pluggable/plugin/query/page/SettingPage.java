package com.angkorteam.pluggable.plugin.query.page;
 
import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.page.PluginSettingPage;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation(roles = { @Role(name = "ROLE_QUERY_PLUGIN_PAGE_SETTING", description = "Role Access Query Plugin Setting Page") })
@Mount("/query/setting")
public class SettingPage extends PluginSettingPage {

    /**
     * 
     */
    private static final long serialVersionUID = 5704264020647908454L;

    public SettingPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public String getPageTitle() {
        return "Query Setting";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
