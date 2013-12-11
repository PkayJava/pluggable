package com.itrustcambodia.pluggable.page;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.border.Border;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.json.ObjectAPIForm;
import com.itrustcambodia.pluggable.json.RestAPIForm;
import com.itrustcambodia.pluggable.panel.FormAPIPanel;
import com.itrustcambodia.pluggable.panel.RestAPIPanel;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.RestDocUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/j")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_JSON_DOC", description = "Access Json Doc Page") })
public class JsonDocPage extends WebPage {

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

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        RestDocUtilities.fillRestAPI(request, application, restAPIForms, objectAPIForms);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Border layout = requestLayout("layout");
        add(layout);

        RestAPIPanel restAPIPanel = new RestAPIPanel("restAPIPanel", restAPIForms);
        layout.add(restAPIPanel);

        FormAPIPanel formAPIPanel = new FormAPIPanel("formAPIPanel", objectAPIForms);
        layout.add(formAPIPanel);
    }

    @Override
    public String getPageTitle() {
        return "Rest API";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
