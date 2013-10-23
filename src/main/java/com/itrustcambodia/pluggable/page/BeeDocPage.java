//package com.itrustcambodia.pluggable.page;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.wicket.authroles.authorization.strategies.role.Roles;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.html.border.Border;
//
//import com.google.gson.Gson;
//import com.itrustcambodia.pluggable.PluggableConstants;
//import com.itrustcambodia.pluggable.core.IAbstractApplication;
//import com.itrustcambodia.pluggable.core.IApplication;
//import com.itrustcambodia.pluggable.core.Menu;
//import com.itrustcambodia.pluggable.core.PluggableUtils;
//import com.itrustcambodia.pluggable.form.ObjectAPIForm;
//import com.itrustcambodia.pluggable.form.RestAPIForm;
//
//@AuthorizeInstantiation({ PluggableConstants.ROLE_REST,
//	PluggableConstants.ROLE_PAGE_BEE_DOC,
//	PluggableConstants.ROLE_ADMINISTRATOR })
//public class BeeDocPage extends WebPage {
//
//    /**
//     * 
//     */
//    private static final long serialVersionUID = -9168502412606501545L;
//
//    private List<RestAPIForm> restAPIForms;
//
//    private List<ObjectAPIForm> objectAPIForms;
//
//    public BeeDocPage() {
//	IApplication application = (IApplication) getApplication();
//
//	restAPIForms = new ArrayList<RestAPIForm>();
//	objectAPIForms = new ArrayList<ObjectAPIForm>();
//
//	HttpServletRequest request = (HttpServletRequest) getRequest()
//		.getContainerRequest();
//	PluggableUtils.fillRestAPI(request,
//		application.getApplicationContext(), restAPIForms,
//		objectAPIForms);
//    }
//
//    @Override
//    protected void onInitialize() {
//	super.onInitialize();
//	Border layout = requestLayout("layout");
//	add(layout);
//
//	IApplication application = (IApplication) getApplication();
//
//	Gson gson = application.getGson();
//
//	HttpServletRequest request = (HttpServletRequest) getRequest()
//		.getContainerRequest();
//	String address = PluggableUtils.getServerAddress(request);
//
//	Map<String, Object> bee = new HashMap<String, Object>();
//	bee.put("title", application.getBrandLabel());
//	bee.put("author", address + "/rest");
//
//	Map<String, String> server = new HashMap<String, String>();
//	server.put("development", request.getServerName());
//	server.put("test", request.getServerName());
//	server.put("production", request.getServerName());
//	bee.put("server", server);
//
//	Map<String, Map<String, String>> model = new HashMap<String, Map<String, String>>();
//	bee.put("model", model);
//
//	for (ObjectAPIForm objectAPIForm : objectAPIForms) {
//	    String name = objectAPIForm.getName();
//	    Map<String, String> fields = new HashMap<String, String>();
//	    for (List<Map<String, String>> field : objectAPIForm.getFields()) {
//		for (Map<String, String> description : field) {
//		    fields.put("name", description.get("name"));
//		}
//	    }
//	    model.put(name, fields);
//	}
//
//	Label json = new Label("json", gson.toJson(bee));
//	layout.add(json);
//    }
//
//    @Override
//    public String getTitle() {
//	return "Rest API";
//    }
//
//    @Override
//    public List<Menu> getPageMenus(Roles roles) {
//	IAbstractApplication application = (IAbstractApplication) getApplication();
//	return PluggableUtils.getSecurityMenu(application, roles).getChildren();
//    }
//
// }
