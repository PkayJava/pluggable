package com.itrustcambodia.pluggable.layout;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.resource.JQueryResourceReference;

import com.itrustcambodia.pluggable.resources.ResourceReference;

public abstract class Bootstrap3Layout extends AbstractLayout {

    /**
     * 
     */
    private static final long serialVersionUID = -3293930125616091104L;

    public Bootstrap3Layout(String id) {
        super(id);
    }

    public Bootstrap3Layout(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(ResourceReference.BOOTSTRAP_CSS));
        // response.render(CssHeaderItem.forReference(ResourceReference.BOOTSTRAP_TIMEPIKER_CSS));
        // response.render(CssHeaderItem.forReference(ResourceReference.BOOTSTRAP_THEME_CSS));
        response.render(JavaScriptHeaderItem.forReference(JQueryResourceReference.get()));
        response.render(JavaScriptHeaderItem.forReference(ResourceReference.BOOTSTRAP_JS));
        // response.render(JavaScriptHeaderItem.forReference(ResourceReference.BOOTSTRAP_TIMEPIKER_JS));

        response.render(JavaScriptHeaderItem.forReference(ResourceReference.MOMENT_JS));
        response.render(JavaScriptHeaderItem.forReference(ResourceReference.PIKADAY_JS));
        response.render(CssHeaderItem.forReference(ResourceReference.PIKADAY_CSS));

        WebSession session = (WebSession) getSession();
        WebClientInfo webClientInfo = session.getClientInfo();
        if (webClientInfo.getProperties().isBrowserInternetExplorer()) {
            if (webClientInfo.getProperties().getBrowserVersionMajor() < 9) {
                response.render(JavaScriptHeaderItem.forReference(ResourceReference.HTML5SHIV_JS));
                response.render(JavaScriptHeaderItem.forReference(ResourceReference.RESPOND_JS));
            }
        }
    }
}
