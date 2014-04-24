package com.angkorteam.pluggable.framework.page;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import com.angkorteam.pluggable.framework.core.ILoginPage;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.json.LoginForm;
import com.angkorteam.pluggable.framework.layout.AbstractLayout;
import com.angkorteam.pluggable.framework.layout.BlankLayout;
import com.angkorteam.pluggable.framework.wicket.authroles.authentication.AuthenticatedWebSession;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/i")
public final class LoginPage extends WebPage implements ILoginPage {

    /**
     * 
     */
    private static final long serialVersionUID = 6977027942872089343L;

    private LoginForm loginForm = new LoginForm();

    @Override
    public AbstractLayout requestLayout(String id) {
        return new BlankLayout(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        AbstractLayout layout = requestLayout("layout");
        add(layout);

        Form<LoginForm> form = new Form<LoginForm>("loginForm",
                new CompoundPropertyModel<LoginForm>(this.loginForm));
        layout.add(form);

        TextField<String> login = new TextField<String>("login");
        login.setRequired(true);
        form.add(login);

        PasswordTextField password = new PasswordTextField("password");
        form.add(password);

        Button okay = new Button("okay") {

            private static final long serialVersionUID = -4311972536352178115L;

            @Override
            public void onSubmit() {
                okayClick();
            }

        };
        form.add(okay);
    }

    public void okayClick() {
        AuthenticatedWebSession session = (AuthenticatedWebSession) getSession();
        boolean valid = session.signIn(this.loginForm.getLogin(),
                this.loginForm.getPassword());
        if (valid) {
            continueToOriginalDestination();
            setResponsePage(getApplication().getHomePage());
        }
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
