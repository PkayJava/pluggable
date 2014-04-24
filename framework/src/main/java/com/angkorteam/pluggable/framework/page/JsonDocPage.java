package com.angkorteam.pluggable.framework.page;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.border.Border;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.json.ObjectAPIForm;
import com.angkorteam.pluggable.framework.json.RestAPIForm;
import com.angkorteam.pluggable.framework.panel.FormAPIPanel;
import com.angkorteam.pluggable.framework.panel.RestAPIPanel;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.RestDocUtilities;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/j")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.framework.wicket.authroles.Role(name = "ROLE_PAGE_JSON_DOC", description = "Access Json Doc Page") })
public final class JsonDocPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -9168502412606501545L;

    private List<RestAPIForm> restAPIForms;

    private List<ObjectAPIForm> objectAPIForms;

    public JsonDocPage() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        restAPIForms = new ArrayList<RestAPIForm>();
        objectAPIForms = new ArrayList<ObjectAPIForm>();

        HttpServletRequest request = (HttpServletRequest) getRequest()
                .getContainerRequest();
        RestDocUtilities.fillRestAPI(request, application, restAPIForms,
                objectAPIForms);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Border layout = requestLayout("layout");
        add(layout);

        RestAPIPanel restAPIPanel = new RestAPIPanel("restAPIPanel",
                restAPIForms);
        layout.add(restAPIPanel);

        FormAPIPanel formAPIPanel = new FormAPIPanel("formAPIPanel",
                objectAPIForms);
        layout.add(formAPIPanel);
    }

    @Override
    public String getPageTitle() {
        return "Rest API";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
