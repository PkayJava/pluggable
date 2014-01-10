package com.itrustcambodia.pluggable.page;

import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.page.ApplicationSettingPage;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/c")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_SETTING", description = "Access Application Setting Page") })
public final class SettingPage extends ApplicationSettingPage {

    /**
     * 
     */
    private static final long serialVersionUID = -5480911426200218227L;

}
