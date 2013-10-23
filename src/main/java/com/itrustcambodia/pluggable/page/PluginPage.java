//package com.itrustcambodia.pluggable.page;
//
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.request.mapper.parameter.PageParameters;
//
//import com.itrustcambodia.pluggable.core.AbstractPlugin;
//import com.itrustcambodia.pluggable.core.IApplication;
//
//public abstract class PluginPage extends AbstractPage {
//
//    /**
//     * 
//     */
//    private static final long serialVersionUID = 3196271133101419487L;
//
//    public final String getIdentity() {
//        IApplication application = (IApplication) getApplication();
//        return application.getPluginMapping(this.getClass().getName());
//    }
//
//    public PluginPage() {
//    }
//
//    public PluginPage(IModel<?> model) {
//        super(model);
//    }
//
//    public PluginPage(PageParameters parameters) {
//        super(parameters);
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//        IApplication application = (IApplication) getApplication();
//        if (application.isMigrated()) {
//            AbstractPlugin plugin = application.getPlugin(getIdentity());
//            if (!plugin.isActivated()) {
//                PageParameters parameters = new PageParameters();
//                parameters.add("identity", plugin.getIdentity());
//                setResponsePage(plugin.getInstallationPage(), parameters);
//            }
//        } else {
//            setResponsePage(MigrationPage.class);
//        }
//
//    }
//
//}
