package com.angkorteam.pluggable.framework.page;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.reflections.ReflectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.FrameworkConstants;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.layout.AbstractLayout;
import com.angkorteam.pluggable.framework.layout.InstallationLayout;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.GroupUtilities;
import com.angkorteam.pluggable.framework.utilities.SecurityUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.angkorteam.pluggable.framework.validation.constraints.NotNull;
import com.angkorteam.pluggable.framework.validation.type.Setting;
import com.angkorteam.pluggable.framework.validation.type.TextFieldType;
import com.angkorteam.pluggable.framework.validator.LocalRepositoryValidator;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.widget.Button;

/**
 * @author Socheat KHAUV
 */
@Mount("/s")
public final class InstallationPage extends KnownPage {

    private static final long serialVersionUID = -8037168822749291356L;

    @com.angkorteam.pluggable.framework.widget.TextField(label = "Login", placeholder = "Login", order = -1)
    @NotNull
    private String login;

    @com.angkorteam.pluggable.framework.widget.TextField(label = "Password", placeholder = "Password", order = 0, type = TextFieldType.PASSWORD)
    @NotNull
    private String password;

    @com.angkorteam.pluggable.framework.widget.TextField(label = "Server Address", placeholder = "Server Address", order = 1, type = TextFieldType.URL)
    @NotNull
    private String serverAddress;

    @com.angkorteam.pluggable.framework.widget.TextField(label = "Local", placeholder = "Local", order = 3)
    private String local;

    public InstallationPage() {
        initializeInterceptor();
        this.local = FileUtils.getTempDirectoryPath();
        this.serverAddress = FrameworkUtilities
                .getServerAddress((HttpServletRequest) getRequest()
                        .getContainerRequest());
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                if (field.getType() != FileUpload.class
                        && field.getType() != FileUpload[].class) {
                    try {
                        FieldUtils.writeField(
                                field,
                                this,
                                application.select(setting.name(),
                                        field.getType()), true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        org.apache.wicket.markup.html.form.TextField<String> local = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("local");
        getForm().add(new LocalRepositoryValidator(local));

    }

    @Override
    public String getPageTitle() {
        return "Installation";
    }

    @Override
    public AbstractLayout requestLayout(String id) {
        return new InstallationLayout(id);
    }

    @Button(label = "Okay", validate = true)
    public void okayClick() {

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName(TableUtilities.getTableName(application
                .getUserEntity()));
        insert.usingGeneratedKeyColumns(AbstractUser.ID);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(AbstractUser.LOGIN, this.login);
        fields.put(AbstractUser.PASSWORD, this.password);
        fields.put(AbstractUser.DISABLE, false);

        Long userId = insert.executeAndReturnKey(fields).longValue();

        Group group = GroupUtilities.createGroup(jdbcTemplate,
                AbstractWebApplication.SUPER_ADMIN_GROUP,
                AbstractWebApplication.SUPER_ADMIN_GROUP, false);
        SecurityUtilities.grantAccess(jdbcTemplate, group, userId);

        application.update(FrameworkConstants.LOCAL, this.local);

        application.update(FrameworkConstants.SERVER_ADDRESS,
                this.serverAddress);
        application.update(FrameworkConstants.DEBUG, false);
        application.update(FrameworkConstants.DEPRECATED, false);

        setResponsePage(MigrationPage.class);
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
