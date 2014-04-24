package com.angkorteam.pluggable.framework.page;

import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.page.ApplicationSettingPage;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/c")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_SETTING", description = "Access Application Setting Page") })
public final class SettingPage extends ApplicationSettingPage {

    /**
     * 
     */
    private static final long serialVersionUID = -5480911426200218227L;

}
