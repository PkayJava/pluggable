package com.itrustcambodia.pluggable.resources;

import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Socheat KHAUV
 */
public class ResourceReference {

	/**
	 * Bootstrap 3.0.0.RC1
	 */
    public static final CssResourceReference BOOTSTRAP_RC1_CSS = new CssResourceReference(ResourceReference.class, "bootstrap/3.0.0.RC1/css/bootstrap.min.css");
    public static final JavaScriptResourceReference BOOTSTRAP_RC1_JS = new JavaScriptResourceReference(ResourceReference.class, "bootstrap/3.0.0.RC1/js/bootstrap.min.js");

    /**
     * Bootstrap 3.1.1
     */
    public static final CssResourceReference BOOTSTRAP_CSS = new CssResourceReference(ResourceReference.class, "bootstrap/3.1.1/css/bootstrap.min.css");
    public static final CssResourceReference BOOTSTRAP_THEME_CSS = new CssResourceReference(ResourceReference.class, "bootstrap/3.1.1/css/bootstrap-theme.min.css");
    public static final JavaScriptResourceReference BOOTSTRAP_JS = new JavaScriptResourceReference(ResourceReference.class, "bootstrap/3.1.1/js/bootstrap.min.js");

    /**
     * Bootstrap 3.0.0.M1
     */
    public static final CssResourceReference BOOTSTRAP_M1_CSS = new CssResourceReference(ResourceReference.class, "bootstrap/3.0.0.M1/css/bootstrap.min.css");
    public static final JavaScriptResourceReference BOOTSTRAP_M1_JS = new JavaScriptResourceReference(ResourceReference.class, "bootstrap/3.0.0.M1/js/bootstrap.min.js");
    
    public static final JavaScriptResourceReference HTML5SHIV_JS = new JavaScriptResourceReference(ResourceReference.class, "html5shiv/3.7.0/html5shiv.min.js");

    public static final JavaScriptResourceReference JQUERY_JS = new JavaScriptResourceReference(ResourceReference.class, "jquery/1.11.0/jquery-1.11.0.js");

    public static final JavaScriptResourceReference RESPOND_JS = new JavaScriptResourceReference(ResourceReference.class, "respond/1.4.2/respond.min.js");

    public static final CssResourceReference BOOTSTRAP_GLYPHICONS_CSS = new CssResourceReference(ResourceReference.class, "bootstrap-glyphicons/css/bootstrap-glyphicons.css");

    public static final CssResourceReference PIKADAY_CSS = new CssResourceReference(ResourceReference.class, "pikaday/pikaday.css");

    public static final JavaScriptResourceReference PIKADAY_JS = new JavaScriptResourceReference(ResourceReference.class, "pikaday/pikaday.js");

    public static final CssResourceReference BOOTSTRAP_TIMEPIKER_CSS = new CssResourceReference(ResourceReference.class, "bootstrap-timepicker/css/bootstrap-timepicker.min.css");

    public static final JavaScriptResourceReference BOOTSTRAP_TIMEPIKER_JS = new JavaScriptResourceReference(ResourceReference.class, "bootstrap-timepicker/js/bootstrap-timepicker.min.js");

    public static final JavaScriptResourceReference MOMENT_JS = new JavaScriptResourceReference(ResourceReference.class, "moment/moment+langs.min.js");

}
