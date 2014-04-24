package com.angkorteam.pluggable.page;

import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.page.ApplicationSettingPage;
import com.angkorteam.pluggable.wicket.authroles.Role;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/c")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_SETTING", description = "Access Application Setting Page") })
public final class SettingPage extends ApplicationSettingPage {

    /**
     * 
     */
    private static final long serialVersionUID = -5480911426200218227L;

}
