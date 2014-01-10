package com.itrustcambodia.pluggable.page;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/d")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_DASHBOARD", description = "Access Dashboard Page") })
public final class DashboardPage extends WebPage {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8711509323120746132L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Border layout = requestLayout("layout");
        add(layout);

        HttpServletRequest request = (HttpServletRequest) getRequest()
                .getContainerRequest();
        List<Map<String, String>> headers = new ArrayList<Map<String, String>>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Map<String, String> header = new HashMap<String, String>();
            header.put("name", name);
            header.put("value", request.getHeader(name));
            headers.add(header);
        }

        ListView<Map<String, String>> table = new ListView<Map<String, String>>(
                "table", headers) {

            private static final long serialVersionUID = -6077251552007209672L;

            @Override
            protected void populateItem(ListItem<Map<String, String>> item) {
                Label name = new Label("name", item.getModelObject()
                        .get("name"));
                item.add(name);
                Label value = new Label("value", item.getModelObject().get(
                        "value"));
                item.add(value);
            }
        };
        layout.add(table);
    }

    @Override
    public String getPageTitle() {
        return "Dashboard";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
